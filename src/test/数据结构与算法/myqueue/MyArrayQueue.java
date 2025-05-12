package test.数据结构与算法.myqueue;

/**
 * @Classname MyArrayQueue
 * @Description 基于数组实现顺序队列

## 队列(Queue)？
> 排队买票，先来的先买，后来的站队尾，不允许插队
> first in first out 先进先出的线性表，顺序（单向）队列只允许在表的队头（front）出队（dequeue），在队尾（rear）入队（enqueue）
> 与栈的先进后出（栈只能从栈顶插入和删除元素）相反。

## 线程池里面的任务满时，又来一个新任务，线程池是如何处理的？具体有哪些策略？这些策略又是如何实现的？

 * @Version 1.0.0
 * @Date 2025/5/12 17:10
 * @Author LuoTao
 */
public class MyArrayQueue {
    private int[] array; // 保存数据的数组
    private int front; // 队头的数组下标
    private int rear; // 队尾的位置，第一次入队时，rear = 0 + 1 = 1
    private int arraySize; // 保存数据的数组的大小
    public MyArrayQueue(int size) {
        this.array = new int[size];
        this.front = 0;
        this.rear = 0;
        this.arraySize = size;
    }
    /**
     * @Description
    >   enqueue入队O(1)：
    >       - 不满队则数据从队尾入队
            >   示例图解：
                        array: [10, _, _, _, _]
                        index:  0   1   2   3   4
                        front = 0
                        rear  = 1
                        → 第一次入队

    >   顺序队列的空间浪费问题（假溢出）:
    >       - dequeue出队后 front 移动，前面的空间无法再被利用
            array: [A, B, C, D, E]
            index:  0  1  2  3  4
            front = 2 ->出队 A、B
            rear  = 5
            → rear == arraySize（5），队满但前两个位置空着无法使用 → 空间浪费！
    >       - 解决方式：
    >           - 在入队的时候如果队满了，就集中移动一次。
    >           当 rear == arraySize（即队列已满），但 front > 0（说明前面有空位），
    >           将队列中的有效元素整体前移，使 front 回到素质索引 0 的位置，从而腾出空间继续入队。
    >           时间复杂度O(n)，但只在必要时才执行，均摊时间仍为 O(1)
            >   示例图解：
                        array: [C, D, E, _, _]
                        index:  0   1   2   3   4
                        front = 0
                        rear  = 3
                        → 可继续入队 F、G、H...

     * @param data 入队数据
     * @Date 2025/5/12 17:10
     * @Author LuoTao
     **/
    public void enqueue(int data){
        if (isFull()){
            System.out.println("队列假溢出，正在解决空间浪费问题...");
            // 解决空间浪费问题O(n)
            // 如果 front == 0 表示真的满了，否则进行整理
            if (front == 0) {
                System.out.println("数组已满，无法插入");
            } else {
                // 整理：把现有数据前移到数组开头
                for (int i = front; i < rear; i++) {
                    array[i - front] = array[i];
                }
                rear -= front;     // 新的队尾位置
                front = 0;         // front 归零
            }
        }else {
            array[rear] = data; // 数据从队尾入队
            rear++; // 第一次入队时，rear=0+1 =1
        }
    }

    /**
     * @Description
     > dequeue出队O(1):
     >  - 队列非空则队头下标后移,即从队头出队
    >   示例图解：
                array: [A, B, C, D, E]
                index:  0   1   2   3   4
                front = 1
                rear  = 5
                → A已出队，front后移，即 front = A的下标+1 = 1

     * @Author LuoTao
     * @Date 2025/5/12 18:07
     * @return int 出队的数据
     **/
    public int dequeue(){
        if (isEmpty()){
            System.out.println("队列为空");
            return -1;
        }else {
            int data = array[front]; // 原队头数据
            front++; // 队头下标后移
            return data;
        }
    }
    /**
     * @Description 判断队列是否为空
    > 顺序队列空的情况:
    >     - front == rear 即队头位置等于队尾位置
        >   示例图解：
                    array: [_, _, _, _, _]
                    index:  0   1   2   3   4
                    front = 0
                    rear  = 0
                    → front == rear → 队列空
     * @Author LuoTao
     * @Date 2025/5/12 18:10
     **/
    public boolean isEmpty(){
        return front == rear;
    }
    /**
     * @Description
    > 顺序队列满的情况:
    >   - rear == arraySize 即队尾的下标等于数组容量
        >   示例图解：
                    array: [A, B, C, D, E]
                    index:  0  1  2  3  4
                    front = 0
                    rear  = 5
                → rear == arraySize（5） → 队列满
     * @Author LuoTao
     * @Date 2025/5/12 22:37
     **/
    public boolean  isFull(){
        return rear == arraySize;
    }
}
