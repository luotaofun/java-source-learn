package test.com.luotao.myhashmap;

/**
 * @Classname 链表
 * @Description TODO
 * @Version 1.0.0
 * @Date 2025/5/8 20:06
 * @Author LuoTao
 */
public class Node {
    public Node next; // 指针
    private Object data;
    public Node(Object data){
        this.data = data;
    }
    public static void main(String[] args) {
        Node head = new Node("neko1");
        head.next = new Node("neko2");
        head.next.next = new Node("neko2");
        System.out.println(head.data);
        System.out.println(head.next.data);
        System.out.println(head.next.next.data);
    }
}
