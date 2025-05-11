package test.数据结构与算法.mylinkedlist;

/**
 * @Classname MyDoubleLinkList
 * @Description 手写双向链表
 *
## 双向链表？mysql的B+Tree?
> - 支持两个方向，每个结点不止有一个后继指针指向后面的结点，还有一个前驱指针直系那个上一个结点
> - 所以存储同样多的数据，双向链表比单链表更占内存空间

## 如何设计一个LRU缓存淘汰算法？
> - 遍历单链表，如果找到了就删掉然后插入到头部，那头部就是最新的
> - 如果不在则先判断LRU是否还有空间，如果有则插入到头部，没有空间则删除最后一个结点。

 * @Version 1.0.0
 * @Date 2025/5/11 14:58
 * @Author LuoTao
 */
public class MyDoubleLinkList {
    private DoubleNode head; // 头结点
    private DoubleNode tail; // 尾结点
    private int size; //  链表长度
    public MyDoubleLinkList(){
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    /**
     * @Description O(1)
    > - 头插法：
    >   - 创建新结点
    >   - 是空链表：头结点和尾结点都赋值为新结点
    >   - 是空链表:
    >       - 新节点的 next前驱指针 指向当前头节点
    >       - 当前头节点的 prev后继指针 指向新节点
    >       - 新结点赋值为头结点
     **/
    public void insertHead(int data){
        DoubleNode newNode = new DoubleNode(data);  // 创建新结点

        if(head == null){ // null说明插入的是头结点，头结点的前后指针都null表示是它是最后一个结点
//            newNode.next = null; // 可省略，构造函数已初始化为 null
//            newNode.prev = null; // 可省略，构造函数已初始化为 null
            tail = newNode; // 尾结点即新结点
            head = newNode;// 新结点赋值为头结点
        }else{
            newNode.next = head; // 新节点的 next前驱指针 指向当前头节点
//            newNode.prev = null; //  新结点作为头结点，它的前驱指针指向null表示它是头结点,可省略，构造函数已初始化为 null
//            tail.next = null; // 尾结点的前后指针指向null表示它是尾结点,可省略，构造函数已初始化为 null
//            tail.prev = null;// 尾结点的前后指针指向null表示它是尾结点,可省略，构造函数已初始化为 null
            head.prev = newNode;  // 当前头节点的 prev后继指针 指向新节点
            head = newNode;     // 新结点赋值为头结点
            // tail 不变
        }

    }

    /**
     * @Description 删除链表的头部
     **/
    public void deleteHead(){
        this.head.next.prev = null; // 将当前头结点的下一个结点的前驱指针指向null，即删除当前头结点
        this.head = this.head.next;// 将当前头结点的下一个结点赋值为头结点
    }

}
/**
 * @Classname DoubleNode
 * @Description 结点
 **/
class DoubleNode{
    int value;      // 结点的值
    DoubleNode next;  // 结点的前驱指针
    DoubleNode prev;  // 结点的后继指针
    DoubleNode(int value){
        this.value = value;
        this.next = null; // 初始化指针为null
        this.prev = null;
    }
}
