package test.数据结构与算法.mylinkedlist;

/**
 * @Classname MyLinkedList
 * @Description 手写链表
## 链表？
> - 将一组零散的内存块(结点)串联在一起，每个结点除了存储数据外，还记录链上的下一个结点的地址。
> - 链表有三种经典的结构：单链表、双链表和循环链表
> - 查询效率低O(n)，因为链表没有数组那样的CPU缓存寻址,没有直接的索引访问方式，只能从头结点遍历到尾结点。
> - 头结点：记录链表的基地址，通过它可以遍历得到整条链表
> - 尾结点：指针指向一个空地址NULL,表示最后一个结点

## LinkedList单链表？
> - 增删效率高，如果要增删某一个元素，不需要像数组那样移动元素，只需要修改结点的指针即可。
> - 如果要插入的元素在链表的尾部，那和数组的效率一样O(n)
> - 头插法：
>   - 创建新结点
>   - 新结点指向头结点
>   - 新结点赋值为头结点
> - 中间插法:
>   - 遍历链表，从头结点开始遍历,找要插入位置的节点的前一个结点
>   - 创建新结点
>   - 注意不能直接指向新结点（会导致后面的数据丢失），需要将后面的结点与新节点建立连接保证不断链，即将新结点的指针指向遍历到的结点的下一个，即要插入位置上的结点。
>   - 将遍历到的结点指向这个新结点

## 用单循环链表实现约瑟夫问题
N 个人围成一圈，从第一个人开始报数。
每数到第 M 个时，该人将被“杀掉”并离开圈子。
下一个人继续从 1 开始报数，直到最后只剩下一个人。

## 循环链表？约瑟夫问题？
> - 循环链表是特殊的单链表，只是最后一个结点的指针指向头结点。

 * @Version 1.0.0
 * @Date 2025/5/11 0:12
 * @Author LuoTao
 */
public class MyLinkedList {
    private ListNode head; // 头结点
    private int size ; //  链表长度
    public MyLinkedList(){
        this.head = null;
        this.size = 0;
    }
    /**
     * @Description O(1)
    > - 头插法：
    >   - 创建新结点
    >   - 新结点指向头结点
    >   - 新结点赋值为头结点
     **/
    public void insertHead(int data){
        ListNode newNode = new ListNode(data);  // 创建新结点

        if(head == null){       // null说明插入的是头结点
//            newNode.next = null;   // 新结点作为头结点指向null表示它是最后一个结点（可省略，构造函数已初始化为 null）
            head = newNode;     // 新结点即是头结点
        }else{
            newNode.next = head; // 新结点指向头结点
            head = newNode;     // 新结点赋值为头结点
        }
    }
    /**
     * @Description O(n)
    > - 中间插法:
    >   - 遍历链表，从头结点开始遍历,找要插入位置的节点的前一个结点
    >   - 创建新结点
    >   - 注意不能直接指向新结点（会导致后面的数据丢失），需要将后面的结点与新节点建立连接保证不断链，即将新结点的指针指向遍历到的结点的下一个，即要插入位置上的结点。
    >   - 将遍历到的结点指向这个新结点

     **/
    public void insertMiddle(int data,int loc){
        if(loc == 0){ // 如果插入位置为0，则头插法
            insertHead(data);
        }else{
            // 遍历链表，【！！！找要插入位置的节点的前一个结点！！！】
            ListNode cur = head;   // 从头结点开始遍历，循环第一次时cur指向第二个结点，所以从i=1开始循环
            for (int i = 1; i < loc; i++) { // 遍历到要插入位置的前一个结点，所以循环条件为i<loc
                cur = cur.next; // i = loc-1 时cur指向要插入位置的前一个结点
            }
            ListNode newNode = new ListNode(data);  // 创建新结点
            newNode.next = cur.next; // 【！！！将新结点的指针指向遍历到的结点的下一个，即要插入位置上的结点！！！ 千万不能用 cur.next = newNode ,因为这样会导致cur后面的数据丢失】
            cur.next = newNode; // 将遍历到的结点指向这个新结点
        }
    }
    /**
     * @Description 删除链表的头部 O(1)
     **/
    public void deleteHead(){
        // 千万不能 this.head = null，因为这会导致断链，需要将头结点的指针指向下一个结点
        this.head = this.head.next;
    }
    /**
     * @Description 删除链表的中间位置结点 O(n)
     **/
    public void deleteMiddle(int loc){
        if(loc == 0){ // 头结点
            deleteHead();
        }else{
            // 遍历链表，【！！！找要删除位置的节点的前一个结点！！！】
            ListNode cur = head;// 从头结点开始遍历，循环第一次时cur指向第二个结点，所以从i=1开始循环
            for (int i = 1; i < loc; i++) {// 遍历到要删除位置的前一个结点，所以循环条件为i<loc
                cur = cur.next;// i = loc-1 时cur指向要删除位置的前一个结点
            }
            cur.next = cur.next.next; // 将遍历到的结点指向下下个结点，即删除它的下一个结点，千万不能 cur.next = null，因为这会导致断链
        }
    }

    /**
     * @Description 根据数据查找结点 O(n)
     **/
    public void find(int data){
        ListNode cur  = head;// 从头结点开始遍历
        int count = 0;
        while (cur != null){
            count++;
            if(cur.value == data) {
                System.out.println("找到结点：" + cur.value + "，位置：" + count);
                break;
            }
            cur = cur.next;
        }
/*        for (; cur != null; cur = cur.next) {
            System.out.println(cur.value + "\n");
        }*/

    }
    /**
     * @Description 打印链表
     **/
    public void print(){
        ListNode cur  = head;// 从头结点开始遍历
        while (cur != null){
            System.out.println(cur.value + "\n");
            cur = cur.next;
        }
/*        for (; cur != null; cur = cur.next) {
            System.out.println(cur.value + "\n");
        }*/

    }


}
/**
* @Classname ListNode
* @Description 结点
**/
class ListNode{
    int value;      // 结点的值
    ListNode next;  // 结点的指针
    ListNode(int value){
        this.value = value;
        this.next = null; // 初始化指针为null
    }
}
