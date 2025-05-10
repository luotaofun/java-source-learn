package test.数据结构与算法;

/**
 * @Classname HeapAndStack
 * @Description 堆栈
## 堆内存（Heap）?

> - 所有线程共享的一块内存区域。
> - 垃圾回收器（GC）自动管理堆内存。
> - 存储对象实例（包括数组）。
> - 生命周期由对象决定，当对象不再被引用时，可能会被回收。

## 栈内存（Stack）?

> - 每个线程拥有自己的私有栈。
> - 存储方法调用时的局部变量、基本类型、参数传递、方法调用过程中的`上下文`等信息,因为栈的速度比堆`快`，而且栈的数据可`共享`。
> - 局部变量如果是基本类型，直接保存值；如果是引用类型，则保存指向堆中对象的`引用`。
> - 方法执行完毕后，栈帧自动弹出，局部变量随之销毁。

 * @Version 1.0.0
 * @Date 2025/5/10 16:21
 * @Author LuoTao
 */
public class HeapAndStack {
    public static void main(String[] args) {
        String str1 = "abc"; // 在栈中创建变量 str1，先不会创建 abc 而是先在栈中找有没有 abc，如果有直接指向，否则就加一个 abc 进来。
        String str2 = "abc"; // 创建一个变量 str12,直接指向已有的 abc
        System.out.println(str1 == str2); // true

        str1 = "bcd"; // str1 指向一个新的 bcd,而 str2 仍然指向 abc
        System.out.println(str1 == str2); // false,虽然栈的数据可`共享`，但是每个线程拥有自己的私有栈，不会影响 str2

        String str4 = "bcd";
        System.out.println(str1 == str4); // true,str1 和 str4 指向同一个bcd,并没有新建对象

        str1 = new String("abc");
        System.out.println(str1 == str2); // false,new 在存储在堆内存中，新开了一个对象，而 str2 仍然在栈内存中

        String s1 = "ja";
        String s2 = "va";
        String s3 = "java";
        String s4 = s1 + s2;// 加号在 JDK 是做了重载的，调用了线程不安全的 StringBuilder（性能更高，在绝大多数字符串拼接场景中，都是单线程操作） 的 append 方法，会 new 对象，所以 s4 是在堆内存中创建的，s3 是在栈内存中创建的
        System.out.println(s3 == s4 ); // false
        System.out.println(s3.equals(s4) ); // true,只比较值
    }
}
