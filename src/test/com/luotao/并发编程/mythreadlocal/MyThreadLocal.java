package test.com.luotao.并发编程.mythreadlocal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *     ThreadLocal的使用场景一：
 *     "每个线程内它需要保存一些全局的信息",
 *     "比如说在拦截器中获取到的用户的信息",
 *     "在每个线程中都把这个信息保存下来之后",
 *     "可以在不同的方法内直接去调用",
 *     "不需要每一次都去传递这个参数",
 *     "比如说我们每一个请求进来",
 *     "都会走一个共同的拦截器",
 *     "会把请求中的 token 转成用户信息",
 *     "比如说我们就知道这个用户",
 *     "他的名字叫做王小美",
 *     "那么这个线程进来之后啊",
 *     "他很有可能会继续的调用很多个不同的方法",
 *     "比如说我去买一个商品啊",
 *     "在买商品的过程中",
 *     "可能我还要更新一下库存啊",
 *     "可能还需要做一下日志记录啊",
 *     "那么这一系列的内容呢",
 *     "都可能是来自同一个请求",
 *     "因为这一个请求进来可能要做很多的事情",
 *     "而在刚才所说的那些步骤中",
 *     "比如说抽奖",
 *     "再比如说扣库存",
 *     "这些方法的实现",
 *     "很有可能都是需要把用户信息给带进去的",
 *     "比如说抽奖",
 *     "那么我们一定要知道是谁抽奖",
 *     "所以这个时候就要把用户的 id 给带进去",
 *     "在这个时候",
 *     "如果我们每一次的方法传递中",
 *     "把这个王小美的信息或者是 id 作为参数",
 *     "层层传递的话",
 *     "是非常繁琐的",
 *     "所以说在这种场景下呢",
 *     "也可以利用 thread local 来解决参数传递的麻烦",
 *
 * @author LuoTao
 * @version 1.0.0
 * 2025/5/18 16:19
 */
public class MyThreadLocal {
}
/**
 *     ThreadLocal的使用场景二：
 *     "每个 thread 类中有自己的实例副本",
 *     "而各个线程之间的实例呢",
 *     "它们是不共享的",
 *     "我们一个班的同学只有一本教材",
 *     "那这本教材呢，可能是老师发的",
 *     "如果只有这一本教材",
 *     "大家呢，又都想使用这本教材的话",
 *     "很有可能就是说会抢着看",
 *     "而且抢着看的同时呢",
 *     "还会抢着做笔记",
 *     "自然就会发生线程安全问题",
 *     "就是并发的读写会带来数据不一致",
 *     "而用了 thread local 的时候",
 *     "就相当于是把这同一本教材",
 *     "这本书去直接复印了30份",
 *     "一个班有30个同学",
 *     "那我就复印30份的教材",
 *     "这样每个人都使用自己的书",
 *     "都使用自己的教材",
 *     "这样就不会出问题了",
 *     "而这里的每一本书",
 *     "每一个实例都只能由当前的那个 thread",
 *     "当前的这个线程访问到并且使用",
 *     "其他的线程是访问不到的",
 *     "这也就是threadlocal这个类的名字的由来 ",
 *     "thread local ， local 代表是本地的意思",
 *     "它所想表达的内容呢",
 *     "就是这一个副本",
 *     "只能被我这个线程本人本地所使用",
 *     "其他的线程是没有办法访问的",
 *     "不是公用的",
 *     "这样也就不存在多线程之间的共享问题",

* 
* @author: LuoTao
* 2025-05-18 19:14:26 
**/
class MyThreadLocalBysimpledateformat{
    /**
    * 多个线程共享同一个 SimpleDateFormat也可能导致线程安全
     * SimpleDateFormat 会使用内部的 Calendar 来解析或格式化时间。
     * 这个 Calendar 是一个可变对象，状态会被反复修改。
Thread A 调用 dateFormat.format(dateA)
   ↓ 使用 Calendar 设置时间为 dateA
   ↓ 正在格式化...

Thread B 调用 dateFormat.format(dateB)
   ↓ 修改同一个 Calendar 的时间到 dateB
   ↓ 完成输出 "2025-05-18"

Thread A 继续执行
   ↓ 发现 Calendar 已经变成 dateB
   ↓ 输出结果为 "2025-05-18"（原本应是 dateA）

   解决方案：
     使用 ThreadLocal 隔离每个线程的 SimpleDateFormat
     每个线程中有自己的实例副本,而各个线程之间的实例呢,它们是不共享的
     相当于，每个线程把SimpleDateFormat都建立了副本
     * @author: LuoTao
    * 2025-05-18 20:51:45
    **/
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            /**
            * 由于 finalI 是循环体内的变量，在每次循环中都会重新创建，且没有被修改，所以相当于它是不可变的,这样可以保证线程安全。如果直接使用 比如i=10，在主线程执行完后又改成了 20，那么新开的线程读取可能是20，引发线程数据不一致。
            *
            * @author: LuoTao
            * 2025-05-18 20:37:12
            **/
            int finalI = i;
            executorService.submit(() -> {
                System.out.println(Thread.currentThread().getName() + ": " + date(finalI));
            });
        }
        executorService.shutdown();

    }
    /**
     * 时间戳转化为日期格式
     *
     * @author LuoTao
     * 2025/5/18 20:04
     * @return java.lang.String
     * @param seconds 毫秒，从1970.1.1 00:00:00 GMT开始计时
     **/
    public static String date(int seconds){
        Date date = new Date(seconds*1000); // 毫秒
        return dateFormat.format(date);
    }

}
