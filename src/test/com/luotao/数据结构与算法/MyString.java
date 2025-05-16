package test.com.luotao.数据结构与算法;

import java.util.Arrays;

/**
# String的底层原理？
*  内部使用 private final char[] value 存储字符数据，final 关键字保证了引用不可变
    每次对 String 进行拼接、替换等操作时，都会创建新的对象。
    原始字符串内容不会被修改，但会保留在内存中（直到被 GC 回收）
 * 不可变的优点：
 *      String的hash经常被使用，如作为HashMap的key；
 *      如果一个String对象已被创建过则JVM 会从StringPool取得引用；
 *      String作为网络连接参数时若可变则可能出现安全性问题；
 *		可以安全地在多个线程间共享；
 适用于当字符串内容无需修改时
 * java8版本中使用char数组存储数据
 * java9版本中改用byte数组存储字符串
 *
* String 实现了Comparable接口，里面有一个抽象方法compareTo,所以String一定对这个方法重写。
性能比较:
 *       单线程场景：StringBuilder - StringBuffer - String
 *       多线程场景：StringBuffer - String
 *
 * @version 1.0.0
 * 2025/5/15 18:53
 * @author LuoTao
 */
public class MyString {
    public static void main(String[] args) {
        System.out.println("张三".equals("张四")); //遍历字符数组逐个比较字符内容: 三 != 四
        System.out.println("abc".compareTo("abcde")); // 字符数组长度的差值：char[abc].len - char[abcde].len=3-5=-2
        System.out.println("abc".compareTo("abbde")); // 逐一比对ASCII码，不一样时返回差值：abc abbde   return c.asc-b.asc =99-98=1
        // 对字符串的操作都会返回一个新的 String 对象:新操作基于原始内容创建新字符数组
        System.out.println("abcdefg".substring(2,3)); // [2,3) -> c
        System.out.println("abcdefg".concat("hijk")); //abcdefghijk
        System.out.println("abcdefg".replace("a","x")); //xbcdefg
        System.out.println("abcdefg".replace("a","x")); //xbcdefg
        System.out.println(Arrays.toString("a,b,c".split(",")));

    }
}

/**
 * 内部使用 synchronized 进行同步以保证线程安全，使用与字符串内容需要频繁修改且在多线程环境的场景
 **/
class StringBufferExample{
    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer("neko");
        sb.append("fun");// 修改现有的StringBuffer对象
        System.out.println(sb.toString());
    }
}

/**
* StringBuilder 是可变的，非线程安全，没有同步开销，适用于字符串内容频繁修改且单线程的场景
修改操作（如 append()、delete()）在原对象上进行，不创建新对象,而是复用内部的字符数组；
 * @author: LuoTao
 * 2025-05-15 20:47:59
 **/
class StringBuilderExample{
    public static void main(String[] args) {
        StringBuilder sb1 = new StringBuilder(); // 创建空字符串对象，默认容量16个字符
        StringBuilder sb = new StringBuilder("没头脑和不高兴");
        sb.append("。");// 直接修改内部 char[]
        // 都是左闭右开原则
        sb.replace(3,4,"LOVE"); // 没头脑LOVE不高兴。
        sb.insert(0, "888"); //888没头脑LOVE不高兴。
        sb.delete(0, 3);//没头脑LOVE不高兴。
        sb.reverse();
        System.out.println(sb.toString());
    }

}

