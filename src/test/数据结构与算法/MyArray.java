package test.数据结构与算法;

import java.io.*;
/**
 * @Classname MyArray
 * @Description
## ArrayList数组？

> - JDK封装了数组，并且封装了数组的扩容机制。
> - 往中间增删O(N)元素效率很低，因为数组是连续的内存块，存储相同类型的数据，为了保证连续性会涉及元素的移动过程（大量的数据搬移）。
> - 查询效率高O(1)，因为支持随机访问，即可以通过下标定位到素质中的某一个元素。
> - 如果知道数据的大小又很关注性能，就选用数组。数组需要关注的就是越界。

## 为什么数组的下标要从0开始？
> - 理论上的合理选择,是基于计算机底层原理和性能优化的结果，CPU 寻址方式天然支持偏移量从0开始。
> - 数组是连续的内存块,访问任意元素的地址可以通过公式计算  address = baseAddress + index * elementSize
> - 如果从0开始，那第一个元素的地址就刚好是 baseAddress + 0 * elementSize = baseAddress，也就是数组的起始地址。这种计算非常直接，不需要额外处理。
> - 如果从1开始，那每次访问都要做一次减法：index - 1，才能得到正确的偏移量，这就增加了不必要的开销。

 *
 * @Version 1.0.0
 * @Date 2025/5/9 11:13
 * @Author LuoTao
 */
public class MyArray {
    /**
     * @Description 用数组实现统计文件中的年龄段出现的次数,年龄和数组下标对应
     **/
    public static void countAge() throws IOException {
        String str = null;
        String fileName = "D:\\workspace\\java-projects\\java-source-learn\\src\\test\\数据结构与算法\\data.txt";
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");

        long startTime = System.currentTimeMillis();
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        int total = 0; // 数组中元素的实际大小
        int[] data = new int[20]; // 存储[1,20]每个值出现的次数
        while ( (str = bufferedReader.readLine()) != null ){ // O(n)
            int age = Integer.valueOf(str);
            data[age] ++; //  每个年龄段出现的次数
            total ++;
        }
        for (int i = 0; i < 20; i++) {
            System.out.println(i + "出现次数:" + data[i]);
        }
        System.out.println("花费时间：" + (System.currentTimeMillis() - startTime) + "ms");
    }


    class ArrayTest{
        private int size;   // 数组长度
        private int data[]; // 保存数据的数组
        private int index;  // data数组实际数据大小

        // 数组初始化
        public ArrayTest(int size) {
            this.size = size;
            this.data = new int[size];
            this.index = 0;
        }

        public void print(){
            System.out.println("index:" + index);
            for (int i = 0; i < index; i++) {
                System.out.println(data[i] + " ");
            }
            System.out.println();
        }

        /**
         * @Description
         * 1 判断数组是否还有空间：
         *      1.1 如果没有则扩容
         *      1.2 如果有空间存储，将插入位置loc之后的数据后移
         * 2 插入成功则元素个数+1
         * @Author LuoTao
         * @Date 2025/5/10 15:13
         * @return void
         * @param loc 插入元素的位置
         * @param n
         **/
        public void insert(int loc,int n){ // O(n)
            if (index ++ < size){ // 判断是否有空间存储否则扩容
                for (int i = size -1; i > loc; i--) { // 将插入位置loc之后的数据后移;size -1表示最后一个元素；
                    data[i] = data[i - 1]; // 数据后移
                }
                data[loc] = n; // 当size -1==loc时，直接赋值插入数据不用数据后移
                index ++; // 元素个数加1
            }
            // TODO: 扩容
        }

        /**
         * @Description
         * 1 判断删除元素位置是否为最后一个元素
         *      1.1 当删除元素位置不为最后一个元素时，数据前移；
         *      1.2 当删除元素位置为最后一个元素时，删除最后一个元素。
         * 2 删除成功则元素个数-1
         * @Author LuoTao
         * @Date 2025/5/10 15:11
         * @return void
         * @param loc 删除元素的位置
         **/
        public void delete(int loc){// O(n)
            for (int i = loc; i < size; i++) {
                if(i != size -1){ // 当删除最后一个元素时，不能数据前移
                    data[i] = data[i + 1]; // 数据前移
                }else{
                    data[i]=0; // 删除最后一个元素
                }
            }
            index --; // 删除元素，元素个数减1
        }

        public void update(int loc,int n){// O(1)
            data[loc] = n;
        }

        public int get(int loc){// O(1)
            return data[loc];
        }

    }
}
