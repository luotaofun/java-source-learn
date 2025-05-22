package test.com.luotao.并发编程.lock.lock;

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