package test.com.luotao.并发编程.lock.lock;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
// 锁是用于控制对共享资源的访问的机制。

/**
 * 乐观锁(非互斥同步锁)：适合并发写少，大部分是读取的场景
 *      出错是小概率，先肆无忌惮去做一些操作，遇到问题再去解决。
 *      比如两个线程并行获取资源并各种计算，线程A先计算完不会立刻写到资源，而是判断资源在此期间是否被修改过，没有才写资源，那线程B发现在自己计算期间资源被抢先修改了，那线程B报错或重试或丢弃
 *       git就是乐观锁的行为，当push到远程仓库时，git会检查远程仓库的版本是不是领先于现在的版本，如果版本号和本地不一样就表示有其他人修改了远端代码，这次就提交失败，要先把对方代码fetch下来解决冲突，否则就可以顺利提交。
 *       git不适合用悲观锁，我在写代码的期间，对方是不能push的，因为我把整个远程仓库锁了，这段时间内只能我本人去提交，很可能造成今天我一整天都在写代码，那其他人根本就不能去协作了，效率低下
 *      一般是用CAS算法实现的，核心思想是可以在一个原子操作内把数据对比并且交换。
 *      他认为自己在处理对象的时候,不会有其他线程来干扰，所以不会锁住被操作的对象，也就带来一种可能性是多个人同时操作，所以在更新时如果发现数据已经被抢先于我更新了就不能继续刚才那个过程了，而是可以选择多种策略，比如放弃、报错也可以重试，不同的逻辑有不同的实现。
 *      原子类、并发容器的底层很多都用到乐观锁的思想
 *      select for update就是数据库的悲观锁体现，执行期间会把库锁住，其他人不能修改
 *      用version控制数据库就是乐观锁：
 *          添加一个字段lock_version来记录版本号，在查询时先把版本号查出来并且在下一次更新时，将版本号+1更新上去，更新时会检查版本号,如果版本号不一致就是报错（update set num=2,version=version+1 where version=1 and id =5）
 *
* @author: LuoTao
* 2025-05-22 09:56:20
**/
public class MyLock {
}

/**
 * 悲观锁(互斥同步锁)：适合并发写入多，比如IO操作
 *      比如两个线程去抢锁，线程A拿到锁，线程B只能等待线程A释放锁，线程B才能拿到锁，执行完自己的逻辑，最后释放锁
 *      synchronized和Lock接口。出错是大概率，为了保证结果会在每次获取并修改数据时把数据锁住，让别人无法访问
 *

 * synchronized遇到异常时JVM会自动释放锁，这是隐藏在背后的逻辑
 * 而Lock需要在Finally中显式的保证锁最终被释放
 *
 * ## ReentrantLock可重入锁
 * 
 * 
 * @author LuoTao
 * @version 1.0.0
 * 2025/5/19 18:18
 */
class ByTryLock implements  Runnable{
    /**
    * 两个线程同时去抢lock1 和 lock2 这两把锁
    * 
    * @author: LuoTao
    * 2025-05-19 20:12:22
    **/
    public static void main(String[] args) {
        ByTryLock byTryLock1 = new ByTryLock();
        byTryLock1.flag = 1;
        ByTryLock byTryLock2 = new ByTryLock();
        byTryLock2.flag = 2;
        new Thread(byTryLock1,"线程1").start();
        new Thread(byTryLock2,"线程2").start();
    }
    int flag =0;
    private static Lock lock1 = new ReentrantLock();
    private static Lock lock2 = new ReentrantLock();
    @Override
    public void run() {
        for (int i = 0; i <2 ; i++) {

            if(flag == 1){
                try {
                    if(lock1.tryLock(2, TimeUnit.SECONDS)){
                        try {
                            System.out.println(Thread.currentThread().getName() + "获得了lock1");
                            Thread.sleep(2000);
                            // lock2.lockInterruptibly(); lockInterruptibly相当于tryLock把超时时间设置为无限
                            if(lock2.tryLock(2, TimeUnit.SECONDS)){
                                try {
                                    System.out.println(Thread.currentThread().getName() + "获得了lock1 和 lock2 这两把锁");
                                    Thread.sleep(2000);
                                    break;
                                } finally {
                                    lock2.unlock(); // 释放锁
                                }
                            }else{
                                System.out.println(Thread.currentThread().getName() + "获取lock2锁失败，已重试");

                            }
                        } finally {
                            lock1.unlock(); // 释放锁
                        }
                    }else{
                        System.out.println(Thread.currentThread().getName() + "获取lock1失败，已重试");

                    }
                } catch (InterruptedException e) {
                    System.out.println("lock1 被中断了");
                }

            }
            if(flag == 2){
                try {
                    if(lock2.tryLock(2, TimeUnit.SECONDS)){
                        try {
                            System.out.println(Thread.currentThread().getName() + "获得了lock2");
                            Thread.sleep(2000);
                            if(lock1.tryLock(2, TimeUnit.SECONDS)){
                                try {
                                    System.out.println(Thread.currentThread().getName() + "获得了lock1 和 lock2 这两把锁");
                                    Thread.sleep(2000);
                                    break;
                                } finally {
                                    lock1.unlock(); // 释放锁
                                }
                            }else{
                                System.out.println(Thread.currentThread().getName() + "获取lock1锁失败，已重试");

                            }
                        } finally {
                            lock2.unlock(); // 释放锁
                        }
                    }else{
                        System.out.println(Thread.currentThread().getName() + "获取lock2失败，已重试");

                    }
                } catch (InterruptedException e) {
                    System.out.println("lock2 被中断了");
                }

            }

        }
    }
}

/**
 # 公平锁与非公平锁

 > 在实际开发中，非公平锁是更优的选择。例如，ReentrantLock` 默认就是非公平的，`synchronized` 关键字实现的也是非公平锁。这是因为在大多数高并发场景下，**吞吐量**往往比严格的**公平性**更为重要。虽然非公平锁存在线程饥饿的风险，但在实际应用中，这种风险通常可以通过其他机制（如线程调度器、任务队列等）来缓解。

 公平锁和非公平锁是并发编程中，对多个线程请求同一把锁时，获取锁的策略的两种不同实现。

 *   **等待队列（Waiting Queue）**：当多个线程竞争同一把锁而未能立即获取时，它们会被放入一个等待队列中。
 *   **锁的获取机制**：
 *   **公平锁**：多个线程在请求同一把锁时，会按照它们请求的顺序来获取锁。在锁被释放时，会检查等待队列。如果队列中有线程在等待，那么锁会严格地交给队列头部的线程。每个线程都有机会执行。性能开销相对较大。因为每次释放锁后，都需要进行上下文切换，唤醒等待队列中的线程，这会带来额外的 CPU 消耗。吞吐量相对较低。
 *   **非公平锁**：允许后来的线程“插队”获取锁，即使有其他线程已经在等待。在锁被释放时，它会尝试让当前正在请求锁的线程（可能是刚释放锁的线程，也可能是新来的线程）立即获取锁，而不会优先检查等待队列。只有当这种“插队”尝试失败（比如没有线程立即请求，或者请求的线程需要被唤醒且成本较高）时，才会去检查等待队列。性能开销相对较小，吞吐量更高。因为它允许“插队”，减少了不必要的上下文切换和线程唤醒开销。

 公平锁：你排在队伍的最后面，每个人依次买票，只有前面的人都买完了，才轮到你。没有人可以插队。
 非公平锁：你刚排到队尾，突然有人跑过来直接插队买票了。虽然前面还有人在等，但他刚好在售票窗口空闲的一瞬间插进来，效率更高，但你可能要等更久甚至一直等不到。

 ReentrantLock(true)：构造一个公平锁，线程必须按照请求顺序获取锁。
 ReentrantLock(false) 或默认构造：构造一个非公平锁，允许线程尝试抢占锁。

 公平锁流程：
 线程尝试获取锁。
 如果锁空闲且等待队列为空，则线程获得锁并执行。
 如果锁被占用或等待队列不为空，当前线程进入等待队列排队。
 当前持有锁的线程释放锁后，AQS 唤醒队列头部的线程，它再次尝试获取锁。
 成功获取后继续执行。
 非公平锁流程：
 线程尝试获取锁。
 如果锁空闲，不管是否有等待线程，当前线程立即获取锁并执行。
 如果锁被占用，线程进入等待队列。
 锁释放后，AQS 唤醒队列头部线程，该线程尝试获取锁；但此时如果有新线程也尝试获取，可能会“插队”成功。
 被唤醒线程如果失败，继续等待或重新入队。

*
* @author: LuoTao
* 2025-05-27 14:17:42
**/
class FaireLock{
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                print(new Object());
            }).start();
            Thread.sleep(1000);
        }
    }
    private static Lock queueLock = new ReentrantLock(true);
//    private static Lock queueLock = new ReentrantLock(false);
    /**
     * 每个线程打印两份
     *
     * @author LuoTao
     * 2025/5/27 14:28
     **/
    static void print(Object documentObj){
        System.out.println(Thread.currentThread().getName() + " 开始打印 ");
        queueLock.lock();
        try {
            int time = (new Random().nextInt(10) + 1) ;
            System.out.println(Thread.currentThread().getName() + "正在打印，需要时间" + time + "秒");
            Thread.sleep(time* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
        queueLock.lock();
        try {
            int time = (new Random().nextInt(10) + 1) ;
            System.out.println(Thread.currentThread().getName() + "正在打印，需要时间" + time + "秒" );
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
        System.out.println(Thread.currentThread().getName() + " 结束打印 ");
    }
}
