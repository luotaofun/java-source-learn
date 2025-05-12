package test.数据结构与算法.myqueue;

/**
 * @Classname MyArrayLoopQueue
 * @Description 基于数组实现循环队列

## 循环（双向）队列？如何判断循环队列是否已满？
 * > 循环队列是一种线性表，允许从两端进行插入和删除操作
 * > 使用取模运算实现队头和队尾的“循环”,无需像顺序队列那样处理假溢出(不再需要手动移动元素来腾出空间)。
 * > 取模运算的本质：模拟“环形结构”,当 rear == 4（最后一个索引）时，(rear + 1) % 5 == 0，即回到索引 0,实现了“首尾相连”的效果
 * > 我们要保证 永远留出一个空位，用来区分“队列满”和“队列空”。
        如果不这样做：
        front == rear 可能表示队列空或队列满
            array: [A, B, C, D, E]
            index:  0   1   2   3   4
            front = 0
            rear  = 0
            → front == rear → 但是队列满了！？
        array: [A, B, C, D, _]
        index:  0   1   2   3   4
        front = 0
        rear  = 4
        → 当插入第5个元素E时(rear + 1) % arraySize == front==(4+1)%5==0，不插入，即留出一个空位避免歧义
        所以我们规定留出空位后：
        (rear + 1) % arraySize == front → 表示队列满；
        front == rear → 表示队列空；

## 优先队列
> 排队的场景：普通用户、VIP用户和SVIP用户

## 阻塞队列

## 延时队列


 * @Version 1.0.0
 * @Date 2025/5/12 17:10
 * @Author LuoTao
 */
public class MyArrayLoopQueue {
    private int[] array;
    private int front; // 队头下标
    private int rear;  // 队尾的位置，第一次入队时，rear = 0 + 1 = 1
    private int arraySize; // 数组容量（固定）

    public MyArrayLoopQueue(int size) {
        this.array = new int[size];
        this.front = 0;
        this.rear = 0;
        this.arraySize = size;
    }

    /**
     * @Description
     * enqueue入队O(1)：
     * > rear 指向下一个要插入的位置
     * > 判断是否队列已满：
     *      - 如果 (rear + 1) % arraySize == front → 队列满
     * > 否则插入到 rear 位置，并更新 rear
     *
     * 示例图解：
     * array: [_, _, _, _, _]
     * index:  0   1   2   3   4
     * front = 0
     * rear  = 0
        → front == rear → 队列空
     * -------------------------------------
     * array: [A, B, C, D, _]
     * index:  0   1   2   3   4
     * front = 0
     * rear  = 4
     * → (rear + 1) % 5 == 0 == front == (4+1)%5==0 → 队列满
     *
    第一次入队：new MyArrayLoopQueue(5).enqueue(10):
        > 示意图：
                array: [10, _, _, _, _]
                index:  0    1   2   3   4
                front = 0 -> 插入数据到 rear 位置（索引 0）
                rear  = 1 -> 更新 rear = (rear + 1) % arraySize → rear = (0+1)%5=1

     *
     * @param data 入队数据
     * @Date 2025/5/12 17:10
     * @Author LuoTao
     **/
    public boolean enqueue(int data) {
        if (isFull()) {
            System.out.println("队列已满，无法插入");
            return false;
        }
        array[rear] = data; //  入队。第一次入队时：插入数据到 rear 位置（索引 0）
        rear = (rear + 1) % arraySize; // 第一次入队：更新 rear = (rear + 1) % arraySize → rear = (0+1)%5=1
        return true;
    }

    /**
     * @Description
    > dequeue出队O(1):
    > 队列非空则队头下标后移,即从队头出队
    > rear == front → 队列空
    > 否则取出 front 位置元素，并更新 front
     *
     * 示例图解：
     * array: [A, B, C, D, E]
     * index:  0   1   2   3   4
     * front = 0
     * rear  = 0
     * → (rear + 1) % 5 == 1 != front → 队列满
     *
     * 出队 A:
     * front = (front + 1) % 5 → 1
     *
     * @Author LuoTao
     * @Date 2025/5/12 18:07
     * @return int 出队的数据
     **/
    public int dequeue() {
        if (isEmpty()) {
            System.out.println("队列为空");
            throw new IllegalStateException("队列为空");
        }
        int data = array[front];
        front = (front + 1) % arraySize;
        return data;
    }

    /**
     * @Description 判断队列是否为空
     * > front == rear → 队列空
     * 示例图解：
     * array: [_, _, _, _, _]
     * index:  0   1   2   3   4
     * front = 0
     * rear  = 0
     * → front == rear → 队列空
     * @Author LuoTao
     * @Date 2025/5/12 18:10
     **/
    public boolean isEmpty() {
        return front == rear;
    }

    /**
     * @Description 判断队列是否已满
     * > (rear + 1) % arraySize == front → 队列满
     * 示例图解：
     * array: [A, B, C, D, _]
     * index:  0   1   2   3   4
     * front = 0
     * rear  = 4
     * → (rear + 1) % 5 == 0 == front → 队列满
     * @Author LuoTao
     * @Date 2025/5/12 22:37
     **/
    public boolean isFull() {
        return (rear + 1) % arraySize == front; // 队列满,此时不会插入新元素,即最后一个位置始终没有用到
    }

    /**
     * @Description 获取当前队列中有效元素个数
     * @return int
     * @Author LuoTao
     * @Date 2025/5/12 22:39
     **/
    public int size() {
        return (rear - front + arraySize) % arraySize;
    }

    /**
     * @Description 查看队头元素（不出队）
     * @return int
     * @Author LuoTao
     * @Date 2025/5/12 22:40
     **/
    public int peek() {
        if (isEmpty()) {
            throw new IllegalStateException("队列为空");
        }
        return array[front];
    }
}
 class Test {
    public static void main(String[] args) {
        MyArrayLoopQueue queue = new MyArrayLoopQueue(5);

        for (int i = 0; i < 4; i++) {
            queue.enqueue(i);
        }

        System.out.println("队列是否满？" + queue.isFull()); // false

        queue.enqueue(4); // 此时队列满
        queue.dequeue(); // front 移动到 1
        queue.enqueue(5); // 应该插入到索引 0

        System.out.println("队列是否满？" + queue.isFull()); // false
        System.out.println("队列大小：" + queue.size()); // 4
    }
}


