package test.com.luotao.并发编程.app.highcache.concurrenthashmap.byfuture.byputIfAbsent.handlexception;


import java.io.IOException;
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
public class CacheByConcurrentHashMapByFutureByPutIfAbsentByHandException<K,V> implements Computable<K,V>{
    /**
     *  加final关键字增加安全：该变量只能被赋值一次，且一旦被赋值，final的变量指向的引用就不会变化了。
     *  即缓存一旦建立之后，指向的引用就不会变化
     *
     *  使用 Future<V> 来缓存每个 key 对应的异步计算任务的结果
     * @author: LuoTao
     * 2025-05-15 23:02:52
     **/
    private final Map<K, Future<V>> cache = new ConcurrentHashMap<>(); //Future包装V
    private final Computable<K,V> myComputer; //  被装饰的对象:持有一个 Component抽象组件 的引用。

    public CacheByConcurrentHashMapByFutureByPutIfAbsentByHandException(Computable<K,V> myComputer) {
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
        CacheByConcurrentHashMapByFutureByPutIfAbsentByHandException<String,Integer> cache = new CacheByConcurrentHashMapByFutureByPutIfAbsentByHandException(new Computer());
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
        CacheByConcurrentHashMapByFutureByPutIfAbsentByHandException<String,Integer> cache = new CacheByConcurrentHashMapByFutureByPutIfAbsentByHandException(new Computer());
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

