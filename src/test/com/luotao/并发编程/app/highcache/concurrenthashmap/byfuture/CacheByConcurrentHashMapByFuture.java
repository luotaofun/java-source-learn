package test.com.luotao.并发编程.app.highcache.concurrenthashmap.byfuture;

import test.com.luotao.并发编程.app.highcache.concurrenthashmap.CacheByConcurrentHashMap;

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
## 用 hashmap 实现缓存，运用了装饰者模式
 具体装饰者（ConcreteDecorator）：实现了抽象组件Computable接口，负责给具体组件Computer添加额外功缓存功能
 * @version 1.0.0
 *  2025/5/15 17:07
 * @author LuoTao
 */
public class CacheByConcurrentHashMapByFuture<K,V> implements Computable<K,V>{
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

    public CacheByConcurrentHashMapByFuture(Computable<K,V> myComputer) {
        this.myComputer = myComputer;
    }

    /**
    * 对原始计算逻辑（被装饰的原始对象）扩展缓存功能
    *
    * @author: LuoTao
    * 2025-05-16 15:50:02
    **/
    @Override
    public  V doCompute(K key) throws Exception {
        /**
        *  如果未命中缓存，则创建一个 Callable 封装计算逻辑并包装成 FutureTask ，然后将 FutureTask 放入缓存。
         *然后调用FutureTask.run异步执行任务，最后从FutureTask.get()阻塞等待任务完成拿结果。
         *
         *
         * 虽然使用了 ConcurrentHashMap和 Future<V> ，但ConcurrentHashMap.get + put这种组合操作也和hashmap一样不是原子操作，仍可能出现重复计算
        * 多个线程同时进入 if(result == null ) 分支: 第一个线程还未put时，第二个线程也进入了if分支，导致重复计算。
             线程 A 调用 cache.get(key)，发现缓存未命中（返回 null）；
             线程 B 同样调用 cache.get(key)，也发现缓存未命中；
            线程 A 创建 FutureTask、写入缓存并执行任务
             线程 B 也执行了同样的计算（已经执行完 doCompute）并再次写入缓存，第二次写入覆盖第一次结果（虽然不影响最终值，但浪费资源）。
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

            // 先将FutureTask放入缓存，这样其他线程也可以获取到FutureTask，避免重复计算
            result = ft; // 将result的null赋值
            cache.put(key, ft);

            System.out.println("从ft调用了doCompute计算");
            ft.run(); // 执行任务 
        }
        return result.get(); // 阻塞直到任务完成拿到结果
    }
    public static void main(String[] args) throws Exception {
        CacheByConcurrentHashMapByFuture<String,Integer> cache = new CacheByConcurrentHashMapByFuture(new Computer());
        LoggingDecorator<String,Integer> loggedAndCached = new LoggingDecorator(cache); //将具体组件传递给具体装饰者的构造函数，形成组合关系
        // 第一次调用 doCompute("1") 时，缓存中没有值，会委托给内部的 myComputer 进行计算并缓存结果。
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        result = loggedAndCached.doCompute("1");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
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
                        throw new RuntimeException(e);
                    }
                }
        ).start();
        new Thread(
                () -> {
                    Integer result = null;
                    try {
                        System.out.println("第三次计算的结果：" + (result=loggedAndCached.doCompute("1"))); // 缓存命中
                    } catch (Exception e) {
                        throw new RuntimeException(e);
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

