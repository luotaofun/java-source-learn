package test.com.luotao.并发编程.mythreadlocal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
* ThreadLocal的底层原理？如何避免内存泄漏？
* 每个Thread对象都持有一个ThreadLocalMap对象（是Thread的成员变量），最开始是空的，如果有ThreadLocal来赋值（map.set），那就会形成一个一个键值对，key是ThreadLocal对象，value是要保存的对象，比如用户权限信息或者user对象。
 那ThreadLocalMap的一个内部类Entry（k-v）， 如果ThreadLocalMap产生了Hash冲突，就继续寻找下一个空位置，而不是像hashmap那样去链表或者树化成红黑树
 ThreadLocalMap的每个Entry都是对key的弱引用，这样可以保证在线程停止后ThreadLocal可以被垃圾回收，但是value是强引用，如果线程需要保存非常久，那key对应的value就会一直存在，可能会导致内存泄漏，那JDK也考虑到了所以会提供一些接口（比如主动调用remove就会删除对应的entry对象，删除value的强引用），去扫描key为null的Entry，并把对应的value设置为null，这样就可以HelpGC回收。

 *
 * initialValue()方法：
      延迟加载：当调用ThreadLocal的get()方法时，如果ThreadLocalMap中没有对应的value值，则会调用initialValue()方法来初始化value值。
      这个方法默认返回null，如果需要自定义初始化value值，可以重写initialValue()方法。
 除非线程先前调用了set方法或者remove之后再调get，否则不会对该线程调用initialValue方法。
get方法：
    先取出当前线程的ThreadLocalMap对象，然后调用map.getEntry把本ThreadLocal的引用作为参数传入，取出map中对应的value值。
set方法：
    先getMap(Thread.currentThread)取出当前线程的ThreadLocalMap对象，然后调用map.set方法，把ThreadLocal对象作为key，把要设置的值作为value，存入map中。


* @author: LuoTao
* 2025-05-19 15:51:50
**/
/**
 *     "每个线程内它需要保存一些全局变量，可以让不同方法（线程）直接共享使用，避免参数传递的麻烦，强调同一个请求(线程)的不同方法间共享变量",
 *     用ThreadLocal保存一些业务内容（用户权限信息、从用户系统获取到的用户名、userID等），这些信息在同一个线程内相同，在不同线程使用的业务内容不相同，在线程的生命周期内，都通过静态ThreadLocal实例的get方法拿自己set过的那个对象，避免将比如User对象作为参数传递的麻烦
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
    public static void main(String[] args) {
        new Service1().process("neko");
    }

    /**
    * 持有用户信息，防止参数传递的麻烦，都通过静态ThreadLocal实例的get方法拿自己set过的那个对象，避免将比如User对象作为参数传递的麻烦
    *
    * @author: LuoTao
    * 2025-05-19 15:23:04
    **/
    public static ThreadLocal<User> holder = new ThreadLocal<>();

    static class Service1{
        public void process(String name){
            System.out.println("Service1 将用户信息保存到 ThreadLocal ");
            holder.set(new User(name)); // 保存全局变量
            new Service2().process();
        }
    }
    static class Service2{

        public void process(){
            // 直接从ThreadLocal中获取全局变量进行业务处理
            System.out.println("Service2 处理完 " + holder.get().name + " 的请求");
            // 避免内层泄露：线程退出之前主动调用remove就会删除对应的entry对象，删除value的强引用
            holder.remove();
        }
    }

    static class User{
        String name;public User(String name) {this.name = name;}
    }
}
/**
 *  不推荐使用 synchronized 关键字来保证每次只允许一个线程访问 SimpleDateFormat 的 format 方法，其他线程等待。
 *  但这样会降低并发性能，因为每个线程都是串行排队使用 SimpleDateFormat
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

 * @author: LuoTao
 * 2025-05-18 20:51:45
 **/
class SimpleDateFormatBySynchronized{
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
     *     方案一（不推荐）：
     *          使用 synchronized 关键字来保证线程安全
     *          每次只允许一个线程访问 SimpleDateFormat 的 format 方法，其他线程等待。
     *          但这样会降低并发性能，因为每个线程都是串行排队使用 SimpleDateFormat
     * @author LuoTao
     * 2025/5/18 20:04
     * @return java.lang.String
     * @param seconds 毫秒，从1970.1.1 00:00:00 GMT开始计时
     **/
    public static String date(int seconds){
        Date date = new Date(seconds*1000); // 毫秒
        String formatDate = null;
        synchronized (dateFormat){
            formatDate = dateFormat.format(date);
        }
        return formatDate;
    }
}

/**
 推荐使用 ThreadLocal 隔离每个线程的 SimpleDateFormat
 每个线程中有自己的实例副本,而各个线程之间的实例呢,它们是不共享的
 相当于，每个线程把SimpleDateFormat都建立了副本
 教材只有一本，一起做笔记有线程安全问题，复印后没问题
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
class SimpleDateFormatByThreadLocal{
    /**
     *   重写ThreadLocal的initialValue方法，返回一个新的SimpleDateFormat实例
     *   每个线程都会调用initialValue方法来创建自己的SimpleDateFormat实例
     *  这样每个线程中有自己的SimpleDateFormat实例副本,而各个线程之间的实例呢,它们是不共享的
     *  相当于，每个线程把SimpleDateFormat都建立了副本
     *
     * @author: LuoTao
     * 2025-05-19 14:55:53
     **/
    private static final ThreadLocal<SimpleDateFormat> dateFormat = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     );
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
     *     方案一（不推荐）：
     *          使用 synchronized 关键字来保证线程安全
     *          每次只允许一个线程访问 SimpleDateFormat 的 format 方法，其他线程等待。
     *          但这样会降低并发性能，因为每个线程都是串行排队使用 SimpleDateFormat
     * @author LuoTao
     * 2025/5/18 20:04
     * @return java.lang.String
     * @param seconds 毫秒，从1970.1.1 00:00:00 GMT开始计时
     **/
    public static String date(int seconds){
        Date date = new Date(seconds*1000); // 毫秒
        String formatDate  = dateFormat.get().format(date); // 使用ThreadLocal完成SimpleDateFormat对象的创建保证每个线程都有自己的SimpleDateFormat对象副本
        return formatDate;
    }
}

/**
* 类型转换异常和空指针异常排查。共享对象导致线程不安全
 * 不能将static对象放到ThreadLocal中，因为ThreadLocal是线程局部变量，每个线程都有自己的ThreadLocalMap，
 *  如果在set进去的本来就是多线程共享的同一个对象，比如static对象，那么get取得的还是这个共享对象本身，而不是一个副本，发生线程安全问题。
* @author: LuoTao
* 2025-05-19 17:53:50
**/
class ThreadLocalTypeCastException{
    private static final ThreadLocal<Long> myThreadLocal = new ThreadLocal<>();
    public  void set(){
        myThreadLocal.set(Thread.currentThread().getId());
    }
    public  Long get(){
        // null: 如果没有预先set则默认返回的是null
        System.out.println(myThreadLocal.get());
        return myThreadLocal.get();
    }
    public  long get1(){
        // null: 如果没有预先set则默认返回的是null
        System.out.println(myThreadLocal.get());
        // 但是由于myThreadLocal指定了泛型是Long，而返回值是long类型，null不能自动拆箱（unboxing）操作，所以会报类型转换异常和空指针异常
        return myThreadLocal.get();
    }
    public static void main(String[] args) {
//        new ThreadLocalTypeCastException().get();
        new ThreadLocalTypeCastException().get1();
        new Thread(() -> {
            myThreadLocal.set(Thread.currentThread().getId());
        });
    }
}

/**
* Spring中的ThreadLocal使用？
 * DateTimeContextHolder类使用ThreadLocal将时间上下文存下来，这样每个线程都可以有自己的时间设置
 *
 * RequestContextHolder类使用ThreadLocal保存了很多请求的信息，比如请求头、请求参数等
 *
 * 每次HTTP请求都对应一个线程，线程之间相互隔离
 *
* 
* @author: LuoTao
* 2025-05-19 18:05:19
**/
class DateTimeContextHolder{
    
}