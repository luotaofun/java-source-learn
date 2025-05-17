package test.com.luotao.并发编程.mythreadpool;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Classname MyThreadpool
 * @Description
## 线程池
> Tomcat也利用了线程池，服务器接受到大量请求时，使用线程池减少线程创建和销毁的开销，提高服务器效率
> 线程池可以复用每个线程，从而减少创建和销毁线程的开销
> 内存不是无限的，我们需要合理的统筹CPU和内存，灵活的调整线程的数量，以便于线程不是太多导致内存溢出，我们可以通过线程池去掌控线程的数量达到平衡

## 线程池构造函数的参数？饭店的桌子不够的时候，把临时的桌子搬到外面去
| 参数名           | 类型                | 含义                                                         |
|------------------|---------------------|--------------------------------------------------------------|
| corePoolSize     | int                 | 1.线程数小于核心线程数时：创建新线程来执行任务                                       |
| maxPoolSize      | int                 | 3.[workQueue满队,maxPoolSize)时：创建新线程来执行任务                                     |
| keepAliveTime    | long                | 6.>=corePoolSize的线程的空闲时间超过存活时间，则该线程被回收，减少资源消耗                                              |
| workQueue        | BlockingQueue       | 2.任务存储的阻塞队列：[corePoolSize,maxPoolSize]时放入该队列                                                 |
| threadFactory    | ThreadFactory       | 5.新线程由threadFactory来生成新的线程，默认使用Executors.defaultThreadFactory() |
| Handler          | RejectedExecutionHandler | 4.拒绝策略：workQueue满队，并且>=maxPoolSize时：拒绝这个任务                   |

当corePoolSize=maxPoolSize时，线程池会创建固定数量的线程，如果任务超过这个数量并且有界队列满队时，触发拒绝
当maxPoolSize=Integer.MAX_VALUE，并且workQueue为有界队列，比如ArrayBlockingQueue时,即2^31-1时，线程池几乎可以容纳所有并发任务，可能导致OOM。
当使用的是无界队列，比如基于链表的LinkedBlockingQueue时，队列容量无限，线程是不会超过corePoolSize（即任务一直在队列中），线程池也能容纳所有并发任务,但是如果处理速度跟不上任务提交就会导致OOM。
execute的逻辑：
+------------------+     +-------------------+
| 提交任务         | --> | [corePoolSize,maxPoolSize]?|
+------------------+     +-------------------+
                             |
                    +--------v-------+      +------------------+
                    | 添加到 workQueue | <---- 创建新线程执行 |
                    +------------------+      +---------------+
                             |
                   +---------v----------+    是
                   | 队列是否已满？     | ---+
                   +--------------------+    |
                                 否        |
                                 +---------v----------+
                                 | 是否小于 maxPool?  | -- 是 --> 创建新线程
                                 +--------------------+
                                                     |
                                                    否
                                                 +------------+
                                                 | 触发拒绝策略 |
                                                 +------------+

## 线程池应该手动创建？
> 用Executors去自动创建线程池，比如FixedThreadPool、SingleThreadExecutor、CacheThreadPool、ScheduledThreadPool这些自动创建线程池的方式都是提前被设计好的，那在设计的时候肯定不完全和我们的业务完全契合，我们最好根据不同的业务场景自己设置线程池的参数，比如我们想给线程取什么名字，那就需要传入自己的线程工厂，还比如说任务被拒绝时该如何记录日志，我们的并发量有多大，可能会决定我们的线程数量需要多少，这些最好经过调研之后结合业务去确定自己的线程池。

## 线程池的线程数量设定为多少比较合适?
> 线程数 ≈ CPU 核心数 × (1 + 平均等待时间 / 平均计算时间)
> 做压测得到一个相对比较合适的线程数量

我们在一个电子商务系统中使用线程池异步写入日志，任务主要是将日志发送到远程日志服务器。
日志写入速度受磁盘 IO 影响较大，每个任务大部分时间在等待 IO 完成
corePoolSize = 16; // 4核CPU × 4倍IO等待
maxPoolSize = 32;
keepAliveTime = 60s;
workQueue = new LinkedBlockingQueue<>(1000);

线程池有哪些组件？
+-------------------+
|     Executor顶级接口      |
+-------------------+
         |
+-------------------+
|  ExecutorService继承了上层接口，比如submit、shutdown  |
+-------------------+
         |
+-----------------------+
| AbstractExecutorService提供了ExecutorService的部分默认实现，比如submit的具体逻辑 |
+-----------------------+
         |
+---------------------------+
|   ThreadPoolExecutor继承了AbstractExecutorService，是更具体实现      |
+---------------------------+

Executors是工具类，可以快速创建线程池，本质也是调用ThreadPoolExecutor的构造

## 线程池如何实现线程复用？
在runworker()中把一个一个Runnable类型的getTask()拿到任务，并且去调他的run方法，这样就可以用相同的线程去把run方法反复执行。

## 线程池有哪些状态？
1. runing：接受新任务并处理排队任务
2. shutdown:不接受新任务，但是处理排队任务
3. stop:中断正在进行的任务，不接受、不处理。showdownNow()，把队列中还未执行完毕的任务返回
4. terminated: 存量任务都处理完了，真正关闭了
5. tidying:整理、整洁。workerCount=0，就会进入tidying状态，并调用terminated这个钩子结束
线程池为了优雅起见，会把正在执行的任务以及队列中等待的任务都执行完毕后再关闭
 * 即把存量的任务执行完毕，新的增量就不会有了，也就是再有新的任务提交就会抛出拒绝的异常。
 * @Version 1.0.0
 * @Date 2025/5/14 14:01
 * @Author LuoTao
 */
public class MyThreadPool {
    public static void main(String[] args) {

    }
}


/**
* @Description:
## 为什么要用线程池？
> 不使用线程池，使用for循环每来一个任务都开一个线程处理
>  线程的创建和销毁需要JVM和操作系统提供一些辅助操作，也会给GC压力
> 这样开销太大了，希望有固定数量的线程来执行，避免反复创建并销毁线程所带来的开销
* @Author: LuoTao
* @Date: 2025-05-14 14:10:21
**/
class EverTaskOneThread  {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            // 匿名内部类是没有名字的局部内部类，用于创建某个类（具体类或抽象类）的子类实例，或某个接口的实现类实例。
            createThreadByExtends();// 等价于：new Thread(new Task()).start(); 也等价于createThreadByRunnableImpl()

        }
    }
    /**
     * @Description createThreadByExtends 通过继承 Thread 来创建线程
     * @Author LuoTao
     * @Date 2025/5/14 16:36
     * @return void
     * @param
     **/
    public static void createThreadByExtends() {
        // 创建Thread子类的匿名内部类实例
        new Thread() {
            @Override
            public void run() {
                System.out.println("继承方式的匿名内部类实现的线程执行了任务");
            }
        }.start();
    }
    static class Task implements Runnable{
        @Override
        public void run() {
            System.out.println("执行了任务");
        }
    }
    /**
     * @Description createThreadByRunnableImpl 通过实现 Runnable接口来创建线程
 }
     * @Author LuoTao
     * @Date 2025/5/14 16:38
     * @return void
     * @param
     **/
    public static void createThreadByRunnableImpl() {
        // 创建Runnable接口实现的匿名内部类实例
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("实现方式的匿名内部类执行了任务");
            }
        }).start();
    }
}

/**
 * @Description:
## newFixedThreadPool
> 它也是调用了ThreadPoolExecutor创建线程池，但是corePoolSize=maxPoolSize，线程池会创建固定数量的线程,用了LinkedBlockingQueue这个无界队列，队列容量无限可能导致OOM
 * @Author: LuoTao
 * @Date: 2025-05-14 16:56:38
 **/
class MyFixedThreadPoolOOM{
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            executorService.execute(new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000000); // 累积任务，一直向LinkedBlockingQueue这个无界队列入队任务
                    } catch (InterruptedException e) {
                        // 内存溢出（OutOfMemoryError）
                        e.printStackTrace(); //设置JVM的堆内存初始值（-Xms）和最大值（-Xmx）均为8MB: java -Xmx8m -Xms8m MyFixedThreadPoolOOM
                    }
                }
            });
        }
    }
}


/**
* @Description:
> newSingleThreadExecutor和newFixedThreadPool原理基本一样，只不过设置了corePoolSize=maxPoolSize=1，线程池会创建单个线程,也用了LinkedBlockingQueue这个无界队列，队列容量无限可能导致OOM
* @Author: LuoTao
* @Date: 2025-05-15 15:43:56
**/
class MySingleThreadExecutor{
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 100; i++) {
            executorService.execute(new Thread(){
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "执行了任务");
                }
            });
        }
    }
}

/**
 * @Description:
CacheThreadPoll是可缓存线程池，无容量阻塞队列，自动回收多余线程
也是调用了ThreadPoolExecutor创建线程池
corePoolSize=0，所有线程都是临时线程，需要线程时就临时创建
maxPoolSize = Integer.MAX_VALUE，意味着可以创建无限多线程
> SynchronousQueue: 任务过来就直接交给新线程处理，不需要再到队列中去中转了
> 它不存储元素,put 和 take 都会阻塞直到配对成功
> 生产者必须等到消费者准备好才能交付数据。
    厨师做好一道菜后，站在出餐口等着你来拿；
    你走到出餐口，拿到菜之后马上交给顾客；
    如果你还没来，厨师就得等你；
    如果你来了但菜还没好，你就得等着。
 * @Author: LuoTao
 * @Date: 2025-05-15 15:43:56
 **/
class MyCacheThreadPool{
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 100; i++) {
            executorService.execute(new Thread(){
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "执行了任务");
                }
            });
        }
    }
}

/**
* @Description:
ScheduledThreadPool是定时任务或重复执行任务的线程池，无容量阻塞队列，自动回收多余线程
调用了ScheduledThreadPoolExecutor创建线程池，它extends ThreadPoolExecutor implements ScheduledExecutorService
super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
new DelayedWorkQueue());延迟队列
DelayedWorkQueue的数据结构是使用数组实现的最小堆（heap），内部使用 ReentrantLock 保证并发安全
    想象你在医院挂号看病：
    每个病人（任务）都有一个预计就诊时间（delay）
    医生（线程）总是先看最早到点的病人
    如果你挂的是专家号（优先级高），你也不能插队，除非你的预约时间更早

每隔多少时间去判断缓存是否过期
* @Author: LuoTao
* @Date: 2025-05-15 16:16:05
**/
class MyScheduledThreadPool{
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        scheduledExecutorService.schedule(new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "执行了任务");
            }
        },5, TimeUnit.SECONDS); // 延迟5秒执行
        scheduledExecutorService.scheduleAtFixedRate(new Thread() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "执行了任务");
            }
        }, 1, 3, TimeUnit.SECONDS);// 延迟1秒开始执行，每3秒执行一次
    }
}

/**
*
 * ## shutdown停止线程池
 * 线程池为了优雅起见，会把正在执行的任务以及队列中等待的任务都执行完毕后再关闭
 * 即把存量的任务执行完毕，新的增量就不会有了，也就是再有新的任务提交就会抛出拒绝的异常。
*
 *
* @author: LuoTao
* 2025-05-17 19:29:05
**/
class MyShutdownThreadPool{
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                try {
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + "执行了任务");
                }catch (InterruptedException e){
                    System.out.println(Thread.currentThread().getName() + "任务被中断");
                }
            });
        }
        System.out.println("线程池是否关闭？" + executorService.isShutdown()); // false
//        executorService.shutdown();// 把存量的任务执行完毕，新的增量就不会有了，也就是再有新的任务提交就会抛出拒绝的异常。

        List<Runnable> list = executorService.shutdownNow();
        System.out.println("队列中还没有执行的任务数量：" + list.size());

        System.out.println("线程池存量的任务是否执行完毕？" + executorService.isTerminated());

        System.out.println("10秒后线程池存量的任务是否执行完毕？" + executorService.awaitTermination(10, TimeUnit.SECONDS));


    }
}
/**
* 线程池的4种拒绝策略：
 * 1. AbortPolicy（默认）：直接抛出RejectedExecution异常，阻止系统正常运行
 * 2. DiscardPolicy：不通知直接丢弃任务
 * 3. DiscardOldestPolicy：丢弃队列中最老的任务，尝试提交当前任务
 * // 前面三种要么丢弃要么抛异常，总之不能得到很好的执行，那CallerRunsPolicy就有一个兜底，而且比如说主线程给线程池提交任务，线程池饱和后由主线程自己执行任务，那主线程执行也是要花时间的，线程池也会执行完毕一些任务，相当于是给线程池一个缓存的时间
 * 4. CallerRunsPolicy：由调用该线程的线程处理该任务。
 *
*
* @author: LuoTao
* 2025-05-17 20:33:42
**/
class RejectPolicy{
}

/**
* 线程池的钩子函数：
 * 1. beforeExecute：任务执行前调用
 * 2. afterExecute：任务执行后调用
 * 3. terminated：线程池关闭后调用
*
* @author: LuoTao
* 2025-05-17 20:46:33
**/
class Hook extends ThreadPoolExecutor {
    public static void main(String[] args) {
        Hook hook = new Hook(1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        for (int i = 0; i < 100; i++) {
           hook.execute(()->{
               System.out.println(Thread.currentThread().getName() + "执行了任务");
               try {
                   Thread.sleep(1000);
               }catch (InterruptedException e){
                   e.printStackTrace();
               }
           });
        }
        hook.pause();
        System.out.println("线程池被暂停了");
        hook.resume();
        System.out.println("线程池被恢复了");
    }

    private boolean isPaused; // 线程暂停标志位
    private Lock lock = new ReentrantLock();
    private Condition unPaused = lock.newCondition();

    public Hook(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
    
    /**
    * 在每个任务执行之前都会调用beforeExecute
    * 
    * @author: LuoTao
    * 2025-05-17 20:58:06
    **/

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        lock.lock();
        try {
            while (isPaused) {
                unPaused.await(); // 线程暂停
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    private void pause(){
        lock.lock();
        try {
            isPaused = true;
        }finally {
            lock.unlock();
        }
    }

    /**
    * 恢复执行
    *
    * @author: LuoTao
    * 2025-05-17 21:00:10
    **/
    public void resume(){
        lock.lock();
        try {
            isPaused = false;
            unPaused.signalAll();// 唤醒所有暂停的线程
        }finally {
            lock.unlock();
        }
    }
}







