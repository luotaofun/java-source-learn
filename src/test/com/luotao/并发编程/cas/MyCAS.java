package test.com.luotao.并发编程.cas;

/**
 ## CAS (Compare And Swap)

 CAS的全称是 `Compare And Swap`，即“比较并交换”。它是一种用于实现多线程环境下同步的乐观锁机制。

 简单来说，它是一种CPU指令，能够原子性地完成“比较内存中的某个值是否等于预期值，如果相等，则将其更新为新值,如果比较不通过，就说明有其他线程修改了数据，当前操作就失败了。”的操作。

 整个比较和交换的过程由CPU指令保证原子性，避免了传统锁机制带来的上下文切换开销。

 CAS相比于传统的互斥锁（如`synchronized`或`ReentrantLock`）具有以下显著优势：

 *   **无锁化操作**：CAS是一种非阻塞算法，它不涉及线程的阻塞和唤醒，减少了线程上下文切换的开销，从而提高了并发性能。
 *   **高并发性**：在竞争不激烈的情况下，CAS能够提供比锁更高的并发性能，因为它避免了锁的获取和释放。
 *   **细粒度控制**：CAS可以针对单个变量进行原子操作，而互斥锁通常会锁定更大范围的代码块，可能导致不必要的阻塞。

 > 假设有两个人（线程A和线程B）同时想去银行修改自己的账户余额。他们都先查询了当前的余额是100元。线程A想把余额改成200元，线程B想把余额改成300元。
 >
 > - **线程A**：它会带着“预期当前余额是100元”和“想改成200元”这两个信息去银行。银行系统在执行修改前，会先检查当前余额是不是真的是100元。如果是，就成功修改为200元。
 > - **线程B**：几乎同时，线程B也带着“预期当前余额是100元”和“想改成300元”的信息去银行。但是，如果线程A已经成功修改了余额为200元，那么当线程B去检查时，发现当前余额已经是200元，而不是它预期的100元。这时，线程B的修改操作就会失败，因为它发现数据已经被别人修改过了。它不会强行修改，而是选择放弃本次操作（或者重试）。

 *
 * @author LuoTao
 * @version 1.0.0
 * 2025/5/27 15:15
 */
public class MyCAS {
    public static void main(String[] args) throws InterruptedException {
        value=0;
        Thread t1 = new Thread(() -> {
            compareAndSwap(0, 1);
        });
        Thread t2 = new Thread(() -> {
            compareAndSwap(0, 1);
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(value);
    }
    private static volatile int value;
    public static synchronized int compareAndSwap(int expectedValue,int newValue){
        int oldValue = value;
        if(oldValue == expectedValue){
            value = newValue;
        }
        return oldValue;
    }

}