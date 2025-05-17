package test.com.luotao.并发编程.app.highcache.concurrenthashmap.byfuture.byputIfAbsent.handlexception.setcacheexpire;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;
/**
 * 抽象组件（Component）：先将最基础的业务逻辑抽象成一个抽象组件，比如说接口，它定义了具体组件和装饰者的行为抽象
 * 具体组件（Concrete Component）：去实现抽象组件，编写抽象组件具体的实现逻辑
 * 具体装饰者（Concrete Decorator）: 和具体组件一样实现同一个抽象组件，此外，还必须要持有一个具体组件的引用，最后在调用具体组件的方法时，编写额外的功能逻辑以扩展具体组件的功能。
 * 如果我们想扩展多个功能，就可以继续编写具体装饰者，然后在具体装饰者中继续包装具体组件，以此类推，形成一个装饰链。
 *
 * @author: LuoTao
 * 2025-05-16 16:32:44
 **/


/**
 * 抽象组件（Component）：定义一个抽象接口，规定了被装饰对象和装饰者对象的共同行为
 *
 * @author: LuoTao
 * 2025-05-16 15:18:18
 **/
interface Computable<K,V>{
    V doCompute(K key) throws Exception;
}
/**
 * 具体组件（ConcreteComponent）：实现抽象组件接口，是被装饰的原始对象。Computer 类实现了 Computable 接口，提供了具体的计算逻辑
 *
 * @author: LuoTao
 * 2025-05-16 15:00:49
 **/
class Computer implements Computable<String,Integer>{
    @Override
    public Integer doCompute(String key) throws Exception {
        TimeUnit.SECONDS.sleep(1); //  模拟计算
        return Integer.valueOf(key);
    }
}
/**
 ## 用 map 实现缓存，运用了装饰者模式
 具体装饰者（ConcreteDecorator）：实现了抽象组件Computable接口，负责给具体组件Computer添加额外功缓存功能
 * @version 1.0.0
 *  2025/5/15 17:07
 * @author LuoTao
 */
public class CacheByConcurrentHashMapByFutureByPutIfAbsentByHandExceptionBySetCacheExpire<K,V> implements Computable<K,V>{
    /**
     *  加final关键字增加安全：该变量只能被赋值一次，且一旦被赋值，final的变量指向的引用就不会变化了。
     *  即缓存一旦建立之后，指向的引用就不会变化
     *
     *  使用 Future<V> 来缓存每个 key 对应的异步计算任务的结果
     * @author: LuoTao
     * 2025-05-15 23:02:52
     **/
    private final Map<K, Future<V>> cache = new ConcurrentHashMap<>(); //Future包装V

    public Map<K, Future<V>> getCache() {
        return cache;
    }

    private final Computable<K,V> myComputer; //  被装饰的对象:持有一个 Component抽象组件 的引用。

    public CacheByConcurrentHashMapByFutureByPutIfAbsentByHandExceptionBySetCacheExpire(Computable<K,V> myComputer) {
        this.myComputer = myComputer;
    }

    /**
     * 对原始计算逻辑（被装饰的原始对象）扩展缓存功能
     *
     * @author: LuoTao
     * 2025-05-16 15:50:02
     **/
    @Override
    public  V doCompute(K key) throws InterruptedException {
        /**
         * while(true)来保证计算出错不会影响其他线程的计算
         * 如果计算错误就进入下一个循环重新计算，直到计算成功
         * 如果是人为取消就抛出异常然后结束运行
         *
         * @author: LuoTao
         * 2025-05-17 14:40:35
         **/
        while(true){
            /**
             *  如果未命中缓存，则创建一个 Callable 封装计算逻辑并包装成 FutureTask ，然后将 FutureTask putIfAbsent原子性操作存入，如果返回值为null，则表示还没有添加缓存，执行任务FutureTask.run()，如果返回值非null，则表示已存在FutureTask，直接复用任务get() 阻塞等待结果
             线程 A 调用 cache.get(key)，发现缓存未命中（返回 null）；
             线程 B 同样调用 cache.get(key)，也发现缓存未命中；
             线程 A 创建 FutureTask、写入缓存并执行任务
             线程 B 因为 A 已经插入了 FutureTask，B 会直接获取到这个任务，而不会重复创建，从而避免重复计算。
             * @author: LuoTao
             * 2025-05-16 17:41:34
             **/

            Future<V> result = cache.get(key);// 先检查cache里面有没有缓存
            if(result == null ){ // 发生重复计算或者缓存未命中时进入分支并执行一个 FutureTask 来计算结果
                // 封装一个Callable对象，封装异步执行计算任务的逻辑
                Callable<V> callable = new Callable<V>() {
                    // 匿名内部类：Callable接口的实现类
                    @Override
                    public V call() throws Exception {
                        return myComputer.doCompute(key); // 原始计算逻辑（被装饰的原始对象）
                    }
                };
                FutureTask<V> ft = new FutureTask<>(callable); //创建FutureTask任务，将任务逻辑封装到FutureTask中

                /**
                 * put(key, ft)
                 *    无论指定的键是否已经存在，都会将新的值放入Map中
                 *    如果键已存在，则旧值会被覆盖并返回对应的旧值。
                 *    如果键不存在，则添加新的键值对并则返回 null。
                 *
                 * putIfAbsent(key, ft)
                 *    只有当指定的键不存在时，才会将键值对插入到Map中
                 *    如果键已存在，旧值不会被覆盖，而是直接返回对应的当前值。
                 *    如果键不存在，则添加新的键值对并则返回 null。
                 * @author: LuoTao
                 * 2025-05-17 02:19:34
                 **/
                // 先将FutureTask放入缓存，这样其他线程也可以获取到FutureTask
                result = cache.putIfAbsent(key, ft);// 原子性操作。如果这里ft任务执行错误没有被释放，则后续从这个任务拿结果也会出错（缓存污染），所以在任务出错时需要从cache中移除这个任务
                if(result == null){ //线程 A 创建 FutureTask、写入缓存并执行任务,同时来的线程B执行到这里发现键已存在，则跳过这个分支的逻辑，直接复用任务通过 get() 阻塞等待结果
                    result = ft; // 将result的null赋值,否则get()时会空指针
                    System.out.println("从ft调用了doCompute计算");
                    /**
                     * FutureTask.run() 被设计为只能执行一次:如果多个线程都调用同一个 FutureTask 的 run()，只有第一个调用会真正执行任务，其他调用会被忽略
                     *
                     * 但是如果两个线程同时进来走if(result == null )分支并创建 了FutureTask时，也会导致重复计算
                     * 解决方案：
                     *      使用 putIfAbsent原子性操作，而不是put；
                     *      然后判断putIfAbsent的返回值是否为空，
                     *      为空则表示还没有添加缓存，执行任务FutureTask.run()
                     *      不为空则表示已存在FutureTask，直接从已存在的FutureTask拿结果
                     *
                     * @author: LuoTao
                     * 2025-05-17 01:38:56
                     **/
                    ft.run(); // 执行任务
                }
            }
            try {
                return result.get(); // 阻塞直到任务完成拿到结果  任务出错会抛出java.util.concurrent.ExecutionException
            } catch (CancellationException e) { // 任务可能人为取消
                System.out.println(Thread.currentThread().getName()+"任务可能被人为取消");
//                e.printStackTrace();
                cache.remove(key); // 从缓存中移除执行出错的任务，避免缓存污染
                throw e;
            } catch (InterruptedException e) { // 任务可能人为中断
//                e.printStackTrace();
                cache.remove(key);
                throw e;
            } catch (ExecutionException e) {
//                e.printStackTrace();
                System.out.println(Thread.currentThread().getName() + "计算错误，需要重试"); //计算出错则进入while的下一次循环，直到计算成功
                cache.remove(key);
            }

        }

    }



    public static void main(String[] args) throws Exception {
        CacheByConcurrentHashMapByFutureByPutIfAbsentByHandExceptionBySetCacheExpire<String,Integer> cache = new CacheByConcurrentHashMapByFutureByPutIfAbsentByHandExceptionBySetCacheExpire(new Computer());
        LoggingDecorator<String,Integer> loggedAndCached = new LoggingDecorator(cache); //将具体组件传递给具体装饰者的构造函数，形成组合关系
        // 第一次调用 doCompute("1") 时，缓存中没有值，会委托给内部的 myComputer 进行计算并缓存结果。
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        result = loggedAndCached.doCompute("1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("第一次计算的结果：" + result);
                }
        ).start();
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        System.out.println("第二次计算的结果：" + (result=loggedAndCached.doCompute("2")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        System.out.println("第三次计算的结果：" + (result=loggedAndCached.doCompute("1"))); // 缓存命中
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

}

/**
 * 在不修改CacheByHashMap的前提下，创建一个新的装饰器类 LoggingDecorator，它也实现 Computable<K, V> 接口,并在调用前后扩展打印功能
 *每个装饰者在其方法中调用被装饰对象的方法，并在调用前后添加自己的逻辑
 * @author: LuoTao
 * 2025-05-16 15:42:34
 **/
class LoggingDecorator<K, V> implements Computable<K, V> {
    private final Computable<K, V> decorated; // 被装饰的对象:持有一个 Component抽象组件 的引用。

    public LoggingDecorator(Computable<K, V> decorated) {
        this.decorated = decorated;
    }

    /**
     * 对原始计算逻辑（被装饰的原始对象）扩展功能
     *
     * @author: LuoTao
     * 2025-05-16 15:50:02
     **/
    @Override
    public V doCompute(K key) throws Exception {

        V result = decorated.doCompute(key); // 原始计算逻辑（被装饰的原始对象） 可以在调用前后添加自己的逻辑

        return result;
    }
}

/**
 * 模拟任务执行异常
 *
 * @author: LuoTao
 * 2025-05-17 13:44:11
 **/
class MayThrowExceptionDecorator<K, V> implements Computable<K, V>{
    private final Computable<K, V> decorated; // 被装饰的对象:持有一个 Component抽象组件 的引用。

    public MayThrowExceptionDecorator(Computable<K, V> decorated) {
        this.decorated = decorated;
    }

    /**
     * 对原始计算逻辑（被装饰的原始对象）扩展功能
     *
     * @author: LuoTao
     * 2025-05-16 15:50:02
     **/
    @Override
    public V doCompute(K key) throws Exception {
        /**
         * 模拟任务执行异常
         *
         * @author: LuoTao
         * 2025-05-17 13:51:43
         **/
        V result = decorated.doCompute((K) new IOException("触发文件读取执行异常")); //java.util.concurrent.ExecutionException

        return result;
    }

    public static void main(String[] args) throws Exception {
        CacheByConcurrentHashMapByFutureByPutIfAbsentByHandExceptionBySetCacheExpire<String,Integer> cache = new CacheByConcurrentHashMapByFutureByPutIfAbsentByHandExceptionBySetCacheExpire(new Computer());
        LoggingDecorator<String,Integer> loggedAndCached = new LoggingDecorator(cache); //将具体组件传递给具体装饰者的构造函数，形成组合关系
        MayThrowExceptionDecorator<String,Integer> mayThrowException = new MayThrowExceptionDecorator(loggedAndCached); //将具体组件传递给具体装饰者的构造函数，形成组合关系
        // 第一次调用 doCompute("1") 时，缓存中没有值，会委托给内部的 myComputer 进行计算并缓存结果。
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        result = mayThrowException.doCompute("1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("第一次计算的结果：" + result);
                }
        ).start();
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        System.out.println("第二次计算的结果：" + (result=mayThrowException.doCompute("2")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        System.out.println("第三次计算的结果：" + (result=mayThrowException.doCompute("1"))); // 缓存命中
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();

    }
}

/**
 * 添加设置缓存过期功能
 *
 * @author: LuoTao
 * 2025-05-17 13:44:11
 **/
class CacheExpireDecorator<K, V> implements Computable<K, V>{
    private final Computable<K, V> decorated; // 被装饰的对象:持有一个 Component抽象组件 的引用。
    private final Map<K, Future<V>> cache;
    public CacheExpireDecorator(Computable<K, V> decorated, Map<K, Future<V>> cache) {
        this.decorated = decorated;
        this.cache = cache; // 通过构造函数传入 cache
    }
    /**
     * @Description:
    ScheduledThreadPool是定时任务或重复执行任务的线程池，无容量阻塞队列，自动回收多余线程
    调用了ScheduledThreadPoolExecutor创建线程池，它extends ThreadPoolExecutor implements ScheduledExecutorService
    super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
    new DelayedWorkQueue());
    DelayedWorkQueue的数据结构是使用数组实现的最小堆（heap），内部使用 ReentrantLock 保证并发安全
    想象你在医院挂号看病：
    每个病人（任务）都有一个预计就诊时间（delay）
    医生（线程）总是先看最早到点的病人
    如果你挂的是专家号（优先级高），你也不能插队，除非你的预约时间更早
     * @Author: LuoTao
     * @Date: 2025-05-15 16:16:05
     **/
    public final static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5); // 定时执行缓存过期任务
    @Override
    public V doCompute(K key) throws Exception {
        V result = decorated.doCompute(key); // 原始计算逻辑（被装饰的原始对象）
        return result;
    }

    /**
    * 缓存过期时间为随机值
    *
    * @author: LuoTao
    * 2025-05-17 16:53:33
    **/
    public V computeRandomExpire(K key) throws Exception {
        // 生成随机的过期时间: 1 到 10秒之间的随机数
        long randomExpireTime = (long) (Math.random() * 10000) + 1;
        // 调用doCompute方法，设置缓存过期时间
        return doCompute(key, randomExpireTime);
    }

    /**
     * 对原始计算逻辑（被装饰的原始对象）扩展功能: 重载doCompute，添加设置缓存过期功能
     *
     * 高并发时的问题：缓存雪崩
     * 如果多个线程同时调用 doCompute(key)，并且这些线程都在同一时间内过期，那么这些线程都会执行计算，导致打爆CPU和数据库
     * 解决方案：
     *      设置缓存过期时间为随机值，避免同时过期
     *
     *
     * @author LuoTao
     * 2025/5/17 15:17
     * @return V
     * @param key
     * @param expireTime 缓存过期时间(毫秒)
     **/
    public V doCompute(K key,long expireTime ) throws Exception {
        if (expireTime > 0) {
            executorService.schedule(new Runnable(){
                @Override
                public void run() {
                    expire(key); // 过期后从缓存中移除
                }
            },expireTime,TimeUnit.MILLISECONDS);
        }
        // 执行计算
        return doCompute(key);
    }
    /**
     * 从缓存中移除
     * 先检查缓存中是否存在对应的 Future 对象
     * 如果存在，判断Future是否执行完毕，如果没有则取消任务
     * @author: LuoTao
     * 2025-05-17 15:25:59
     **/
    public synchronized void expire(K key){
        Future<V> future = cache.get(key);
        if (future != null) {
            if(!future.isDone()){
                System.out.println("Future任务到期时间到了，但是还没有执行完，任务被取消");
                future.cancel(true);
            }
            System.out.println("设置的过期时间到了，从缓存中移除");
            cache.remove(key);
        }
    }

    public static void main(String[] args) throws Exception {
        CacheByConcurrentHashMapByFutureByPutIfAbsentByHandExceptionBySetCacheExpire<String,Integer> cache = new CacheByConcurrentHashMapByFutureByPutIfAbsentByHandExceptionBySetCacheExpire(new Computer());
        LoggingDecorator<String,Integer> loggedAndCached = new LoggingDecorator(cache); //将具体组件传递给具体装饰者的构造函数，形成组合关系
        CacheExpireDecorator<String,Integer> loggedAndCachedAndCacheExpire = new CacheExpireDecorator(loggedAndCached,cache.getCache()); //将具体组件传递给具体装饰者的构造函数，形成组合关系
        // 第一次调用 doCompute("1") 时，缓存中没有值，会委托给内部的 myComputer 进行计算并缓存结果。
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        result = loggedAndCachedAndCacheExpire.doCompute("1",5000L); //设置过期时间为5秒
//                        result = loggedAndCachedAndCacheExpire.computeRandomExpire("1"); //随机过期时间避免缓存雪崩
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("第一次计算的结果：" + result);
                }
        ).start();
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        System.out.println("第二次计算的结果：" + (result=loggedAndCachedAndCacheExpire.doCompute("2")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();
        Thread.sleep(6000L); // 主线程等待6秒，模拟过期时间5秒到了
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        System.out.println("第三次计算的结果：" + (result=loggedAndCachedAndCacheExpire.doCompute("1"))); // 等待5秒后缓存过期，应该会重复计算
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();
        executorService.shutdown(); // 关闭线程池，不再接受新任务
    }
    
}
/**
* 用CountDownLatch实现压测:同一时刻请求同时到达
* 
* @author: LuoTao
* 2025-05-17 17:06:17 
**/
class CacheExpireDecoratorTest{
    public static CountDownLatch countDownLatch = new CountDownLatch(1);
    public static void main(String[] args) throws Exception {
        CacheByConcurrentHashMapByFutureByPutIfAbsentByHandExceptionBySetCacheExpire<String,Integer> cache = new CacheByConcurrentHashMapByFutureByPutIfAbsentByHandExceptionBySetCacheExpire(new Computer());
        LoggingDecorator<String,Integer> loggedAndCached = new LoggingDecorator(cache); //将具体组件传递给具体装饰者的构造函数，形成组合关系
        CacheExpireDecorator<String,Integer> loggedAndCachedAndCacheExpire = new CacheExpireDecorator(loggedAndCached,cache.getCache()); //将具体组件传递给具体装饰者的构造函数，形成组合关系
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                Integer resut = null;
                try {
                    System.out.println(Thread.currentThread().getName() + "开始等待");
                    countDownLatch.await();
                    SimpleDateFormat simpleDateFormat = ThreadSafeFormatter.dateFormatter.get();
                    String time = simpleDateFormat.format(new Date());
                    System.out.println(Thread.currentThread().getName() + " " + time + "被放行");
                    resut = loggedAndCachedAndCacheExpire.doCompute("1");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println(resut);
            });
        }
/*        executorService.shutdown(); // 关闭线程池
        // 等待所有任务完成
        while (!executorService.isTerminated()){

        }
        System.out.println("总耗时(毫秒)：" + (System.currentTimeMillis() - startTime));
    }*/
        Thread.sleep(1000L);
        System.out.println("=====================================");
        countDownLatch.countDown(); // 统一放行
        executorService.shutdown();
    }
}
class ThreadSafeFormatter{
    public static ThreadLocal<SimpleDateFormat> dateFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("mm:ss");
        }

        @Override
        public SimpleDateFormat get() {
            return super.get();
        }
    };
}

