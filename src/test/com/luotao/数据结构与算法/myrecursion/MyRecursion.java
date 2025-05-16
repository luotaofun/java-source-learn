package test.com.luotao.数据结构与算法.myrecursion;

/**
 * @Classname MyRecursion
 * @Description
 * ## 用递归计算斐波那契数列
 * > 通过分治的思想把一个数据规模大的问题分解成若干个规模较小的子问题，子问题（往前追问）的求解思路完全一样，最后一定有一个确定的答案（排队场景的第一个人肯定知道自己位置是1），即递归的结束条件。
 *> 推荐使用尾递归算法
 * > 用尾递归实现链表
 * @Version 1.0.0
 * @Date 2025/5/13 17:44
 * @Author LuoTao
 */
 class MyRecursionCalcFibonacci {
    private static int array[];//定义一个数组作为缓存，存储已经计算过的斐波那契值

    /**
     * @Description
     * 斐波那契数列:  1 1 2 3 5 8 13...
        f(1) = 1
        f(2) = 1
        f(3) = f(2) + f(1) = 1 + 1 = 2
        f(4) = f(3) + f(2) = 2 + 1 = 3
        f(5) = f(4) + f(3) = 3 + 2 = 5
        f(n) = f(n-1) + f(n-2)
     * 终止条件：n <= 2 reutrn 1
     * @Author LuoTao
     * @Date 2025/5/13 19:39
     * @return 第 n 项斐波那契数
     * @param n 当前计算到第几项
     **/
    public static int fibonacci(int n) { //O(2^n)
        if (n <= 2) {
            return 1; //终止条件为 n <= 2 返回 1
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    /**
     * @Description
     * 尾递归实现斐波那契数列 1 1 2 3 5
     *  >示例图解：
     *      5,1,1
     *      4,2,1
     *      3,3,2
     *      2,5,3->return 5
     * @Author LuoTao
     * @Date 2025/5/13 23:21
     * @return int 第 n 项斐波那契数
     * @param n 当前计算到第几项
     * @param fi 当前项的值f(i)
     * @param fiPre 前一项的值f(i-1)
     **/
    public static int fibonacciByTailRecursion(int n,int fi,int fiPre) { //O(n)
        System.out.println("fibonacciByTailRecursion(" + n + ", " + fi + ", " + fiPre +")");
        if (n <= 2) {
            return fi; // ✅ 终止条件：返回累计的值
        }
        // 尾递归:该递归调用是函数的最后一行操作，没有后续运算
        return fibonacciByTailRecursion(n-1,fi + fiPre,fi); // 当前项更新为当前项与前一项之和，前项更新为当前项（回溯）
    }


    /**
     * @Description 不用递归实现斐波那契数列
     * @Author LuoTao
     **/
    public static int fibonacciByNone(int n) { //O(n)
        if (n <= 2){
            return 1;
        }
        int f1 = 1;
        int f2 = 1;
        int f3 = 0;
        for (int i = 3; i <= n; i++){
            f3 = f1 + f2; //f(n) = f(n-1) + f(n-2)
            f1 = f2; // 前移
            f2 = f3;
        }
        return f3;
    }
    /**
     * @Description 使用数组实现斐波那契数列
     * @Author LuoTao
     * @return int 第 n 项斐波那契数
     * @param n 当前计算到第几项
     **/
    public static int fibonacciByArray(int n) { //O(n)
        if (n <= 2){
            return 1;
        }
        // 1 1 2 3 5 8 13...
        //0 1 2 3 4 5 6...
        int[] data = new int[n];
        data[0] = 1;
        data[1] = 1;
        for (int i = 3; i <= n; i++){
            data[i-1] = data[i-2] + data[i-3]; // f(n) = f(n-1) + f(n-2)
        }
        return data[n-1]; // 返回数组最后一个元素,即第 n 项斐波那契数
    }


    /**
     * @Description
    记忆化递归算法: 使用数组来做缓存O(n)
        使用一个数组来存储已经计算过的斐波那契值。
        在每次递归调用前检查数组中是否已经存在结果：
        如果有，则直接返回缓存的结果；
        如果没有，则进行递归计算，并将结果保存到数组中。
        递归终止条件为：当 n <= 2 时返回 1。
     * @Author LuoTao
     * @return int 第 n 项斐波那契数
     * @param n 当前计算到第几项
     **/
    public static int fibonacciByArrayToCache(int n) {
        // 初始化缓存数组
        if (array == null || array.length < n + 1) {
            array = new int[n + 1]; // 索引对齐:能够保存 f(1) 到 f(5)，也就是需要索引 1 ~ 5，因此数组长度必须是 5 + 1 = 6
        }
        if (n <= 2) {
            return 1; // 终止条件
        }

        // 命中缓存：如果缓存中有值，直接返回
        if (array[n] != 0) {
            return array[n];
        }

        // 否则递归计算并将结果保存到缓存中
        array[n] = fibonacciByArrayToCache(n - 1) + fibonacciByArrayToCache(n - 2); // f(n) = f(n-1) + f(n-2)
        return array[n];
    }

    /**
     * @param n 当前计算到第几项
     * @return int 第 n 项斐波那契数
     * @Description 尾递归 + 缓存方式计算斐波那契数列
     * @Author LuoTao
     * @Date 2025/5/13 23:13
     **/
    public static int fibonacciByArrayToCacheByTailRecursion(int n, int fi, int fiPrev) {
        System.out.println("fibonacciByArrayToCacheByTailRecursion(" + n + ", " + fi + ", " + fiPrev +")");
        // 初始化缓存数组
        if (array == null || array.length < n + 1) {
            array = new int[n + 1]; // 索引对齐:能够保存 f(1) 到 f(5)，也就是需要索引 1 ~ 5，因此数组长度必须是 5 + 1 = 6
        }
        if (n <= 2) {
            return fi; // 终止条件
        }

        // 命中缓存：如果缓存中有值，直接返回
        if (array[n] != 0) {
            return array[n];
        }
        // 尾递归:该递归调用是函数的最后一行操作，没有后续运算
        int result = fibonacciByArrayToCacheByTailRecursion(n - 1, fiPrev + fi, fi); //  当前项更新为当前项与前一项之和，前项更新为当前项
        array[n] = result; //将结果保存到缓存中
        return result;
    }



}

/**
 * @Description
 * 用递归计算阶乘
 * @Author LuoTao
 * @Date 2025/5/13 21:59
 **/
class MyRecursionCalcFactorial {
    /**
     * @Description
     * 普通递归计算阶乘
     * @Author LuoTao
     * @Date 2025/5/13 21:59
     **/
    public static int factorial(int n) {
        // 5!  = 5 * 4 * 3 * 2 * 1
        if (n == 1) {
            return 1; // 终止条件，此时 result 已累计最终结果
        }
        return n * factorial(n - 1); // f(n) = n * f(n-1)
    }
    /**
     * @Description
    >尾递归计算阶乘 : f(n) = n * f(n-1)
    5!=120
    5,1
    4,5
    3,20
    2,60
    1,120
     * @param fi 当前项的值f(i)
     * @Author LuoTao
     * @Date 2025/5/13 21:59
     **/
    public static int factorialTail(int n, int fi) {
        System.out.println("factorialTail(" + n + ", " + fi + ")");
        if (n == 1) {
            return fi; // 终止条件，此时 result 已累计最终结果,返回当前项的值
        }
        return factorialTail(n - 1, n * fi); // 当前项更新为n*前项
    }
}

/**
 * @Description
> 排队场景的递（问）归（回溯），我不知道我排在第几个，那么我问我前面的人排第几个，前面的人也不知道自己排第几，那他继续往前追问，直到问到第一个人，然后从第一个人一直传回我这里，我就知道了我是排第几了。
f(n) = f(n-1) + 1
1 2 3 4 5...
 * @Author LuoTao
 **/
class MyRecursionCalcQueue{
    public static int queue(int n) { //O(n)
        if (n == 1) {
            return 1;
        }
        return queue(n - 1) + 1;
    }
    /**
     * @Description 尾递归实现  f(n) = f(n-1) + 1
        queueByTail(5, 1)
        → queueByTail(4, 2)
        → queueByTail(3, 3)
        → queueByTail(2, 4)
        → queueByTail(1, 5)
        → return 5 ✅

     * @Author LuoTao
     **/
    public static int queueByTail(int n,int fi) {
        System.out.println("queueByTail(" + n + ", " + fi + ")");
        if (n == 1) {
            return fi;// 返回的是当前累积值 fi
        }
        return queueByTail(n - 1,fi+1) ;
    }
}

class Test {
    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.println("第" + i + "个斐波那契数："+ MyRecursionCalcFibonacci.fibonacciByTailRecursion(i, 1, 1));
        }
        System.out.println("=====================");
        System.out.println(MyRecursionCalcFactorial.factorialTail(5,1)); //阶乘是从 1 开始乘的
        System.out.println(MyRecursionCalcQueue.queueByTail(5, 1));
        System.out.println(MyRecursionCalcFibonacci.fibonacciByArrayToCacheByTailRecursion(5, 1, 1));
        System.out.println("================");
        System.out.println(MyRecursionCalcFactorial.factorial(50));
    }
}
