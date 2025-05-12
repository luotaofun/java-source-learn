package test.com.luotao.数据结构与算法;
/**
* @Description:
 ## 评价算法的两个指标：
> - 1. 时间复杂度：运行一个程序所花费的时间
> - O(1): 常量阶最优效率，1表示常数，所有能确定的数字都用O(1)
> - O(logn): 对数阶
> - O(n): 线性阶
> - O(nlogn): 线性对数阶
> - O(n^2): 平方阶
> - O(n^n): 指数阶（n的n次方）
> - 2. 空间复杂度：运行程序所需要的内存
* @Author: LuoTao
* @Date: 2025-05-09 07:21:55
**/
class BigO {

/*
    void o1(){
        int a=0;
        int n=3; //这里运行了1次，时间复杂度为O(1)
        for (int i = 0; i < n; i++) { //条件会被检查4次，在第四次的时候跳出循环:i=3 (0 1 2 3)
            a+=1; // 这里运行了3次，时间复杂度为O(1)，因为 n 已经确定了，是常量
        }
    }

    void on(){
        int a=0;
        for (int i = 0; i < n; i++) { // n表示未知,a是一个不确定的变量
            a +=1;// 这里运行n次，时间复杂度为O(n)
        }
    }

    void logn(){
        int i=1;
        while(i <= n){
            i = i * 2; // i的值：2^1 2^2 2^3 2^4 ... 2^x，所以这里运行x次
            */
/**
             对数的性质: log_b(b^a)=a
             2^x=n 求x：
             > 对原方程两边取以2为底的对数得到：log_2(2^x)=log_2(n)
             > 根据对数的性质化简得到：x=log_2(n)
             > 由于计算机忽略掉常数，所以这里时间复杂度为O(logn)
             **//*

        }
    }

    void nlogn(){
        int i=1;
        for (int j = 0; j < n; j++) { // n表示未知
            while(i <= n){
                i = i * 2; // 内层循环的时间复杂度为O(logn)，循环结束后时间复杂度为O(nlogn)
            }
        }
    }

*/


    /***
    * @Description: 判断一个数是否是 2 的 N 次方
    在二进制下，2 的 N 次方的数只有一个 1，其余都是 0。

    - 例如：1（0001）、2（0010）、4（0100）、8（1000）、16（10000）……

    如果 n 是 2 的 N 次方，那么 n-1 的二进制会把这个唯一的 1 变成 0，后面全变成 1。

    - 例如：8（1000）-1 = 7（0111）

    把 n 和 n-1 做按位与（&），结果一定是 0。

    - 8 & 7 = 1000 & 0111 = 0000
    * @Author: LuoTao
    * @Date: 2025-05-13 01:26:03
    **/
    public static boolean isPowerOfTwo(int n) { //O(1)
        // 0 不是 2 的 N 次方，所以要先排除
        if (n <= 0) {
            return false;
        }
        // 利用位运算判断是否为 2 的幂
        return (n & (n - 1)) == 0;
    }





    public static void main(String[] args) {
        int counti=0;
        int countj=0;
//        int n=0; // 内层循环体执行 n 次
//        int n=1; // 内层循环体执行 n-1 次
        int n=2; // 内层循环体执行 n-2 次
        int i=0;
//        int i = n;
        for (; i < n; i++) { // 条件会被检查  n + 1 次（最后一次判断不满足条件退出循环），外层循环体执行了 n 次
            System.out.println("i=" + i);
            // i=0  内层循环体执行了 n 次
            // i=1  内层循环体执行了 n-1 次
            // i=2 内层循环体执行了 n-2 次
            // i=n 内层循环体执行了 n-i 次
            counti += 1;
            int countinner = 0;
            for (int j = i; j < n; j++) { //n n-1 n-2  当i=n时内层循环体执行 n-i 次
                System.out.println("    j=" + j);
                countj += 1;
                countinner += 1;
            }
            System.out.println("    当前内层循环执行次数" + countinner);
        }
        System.out.println("==================================" );
        System.out.println("O(n)外层循环体执行总次数n" + counti);
        System.out.println("O(n²)内层循环体执行总次数" + countj);
        // 总和 = 项数 × (首项 + 末项) ÷ 2
        // n + (n-1) + (n-2) + ... + 2 + 1= n(n+1)/2
    }
}
