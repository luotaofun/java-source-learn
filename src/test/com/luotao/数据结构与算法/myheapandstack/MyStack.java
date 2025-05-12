package test.com.luotao.数据结构与算法.myheapandstack;

import java.util.Scanner;

/**
 * @Classname MyStack
 * @Description
## 栈（Stack）?
> - 数组和链表暴露太多接口，实现上太灵活，理解不到位可能出错，在某些特定场景，比如只能操作端和尾数据可以使用栈，比如浏览器的前进和后退功能。
> - Last in first out 后进先出，即先进后出，只能从栈顶（top）插入（push入栈）和删除元素（pop出栈）
：比如在放盘子的时候都是从下往上一个一个放，拿的时候从上往下一个一个的拿，不能从中间抽，即后进先出
> - 一个限定仅在表尾增删的线性表，这一端被称为栈项，把另一端称为栈底
> - 把新元素放到栈顶叫进栈，把栈顶元素删除叫出栈O(1)
> - 每个线程拥有自己的私有栈。
> - 存储方法调用时的局部变量、基本类型、参数传递、方法调用过程中的`上下文`等信息,因为栈的速度比堆`快`，而且栈的数据可`共享`。
> - 局部变量如果是基本类型，直接保存值；如果是引用类型，则保存指向堆中对象的`引用`。
> - 方法执行完毕后，栈帧自动弹出，局部变量随之销毁。

## 用栈来实现计算器，比如 `2*(3+(4*5))`
> 遍历表达式字符：逐个读取输入字符串中的字符
> 操作数栈:
>   - 如果是数字，则持续拼接成完整的多位数，然后压入操作数栈
> 运算符栈:
>   - 运算符栈为空或栈顶是左括号 '('，则直接将当前运算符压入栈
>   - 否则，比较当前运算符与栈顶运算符的优先级:
>       - 遇到右括号 ')'，则不断弹出运算符并计算，直到遇到左括号为止（左括号不参与计算，仅作为边界标识）
>       - 若当前运算符优先级更高，则压入栈
>       - 否则，弹出栈顶运算符，并从操作数栈中弹出两个操作数进行计算，将结果重新压入操作数栈，继续比较下一个栈顶运算符。
> 遍历结束后，将运算符栈中所有运算符依次弹出并计算

## 如何设计一个浏览器的前进和后退功能？
> 访问新页面:将当前页面压入后退栈，清空前进栈（因为前进历史被破坏）
> 后退栈:点击后退，如果后退栈非空则将当前页面压入前进栈，弹出后退栈的栈顶作为当前页面
> 前进栈:点击前进，如果前进栈非空则将当前页面压入后退栈，弹出前进栈的栈顶作为当前页面


 * @Version 1.0.0
 * @Date 2025/5/11 18:06
 * @Author LuoTao
 */
public interface MyStack<Item> {
    Item push(Item item); // 入栈
    Item pop(); // 出栈
    int size(); // 栈大小
    boolean isEmpty();
}

/**
* @Description: 基于数组的栈，已数组头为栈底
* @Author: LuoTao
* @Date: 2025-05-11 20:23:48
**/
class MyArrayStack<Item> implements MyStack<Item>{
    private Item[] array;    //  数组
    private int n;     //  表示当前栈中元素的实际个数,即已使用的数组长度
    public MyArrayStack(int cap){
        array = (Item[]) new Object[cap];
        n = 0;
    }

    @Override
    public Item push(Item item) { // O(1)
        judgeSize(); //判断是否需要扩容
        array[n++] = item;// 栈顶元素入栈
        return item;
    }

    /**
    * @Description: 判断数组大小扩容
    **/
    private void judgeSize(){
        if (n >= array.length ){  // 当 n（实际元素个数）等于 array.length（数组容量）时，表示数组已经满载，需要先扩容
            resize(array.length * 2); // 扩容为原数组的2倍
        }else if(n >0  && n == array.length / 2){
            // 当 n（实际元素个数）等于 array.length（数组容量）的一半时，表示数组已经半满，需要先缩容，因为数组new出来的内存并不会自动释放，所以需要手动释放。
            resize(array.length / 2);
        }
    }
    /**
    * @Description: 将旧数组 array 中的元素复制到新数组 temp
     * @param size 扩容后的数组长度
    **/
    private void resize(int size){ // 扩容O(n)
        Item[] temp = (Item[]) new Object[size]; // 临时数组
        for (int i = 0; i < n; i++) {
            // 将旧数组 array 中的元素复制到新数组 temp 的对应位置。
            temp[i] = array[i];
        }
        array = temp; // 将新数组赋值给 array
    }

    @Override
    public Item pop() { // O(1)
        if (isEmpty()) {
            return null; // 已经全部出栈了
        }
        Item item = array[--n]; // 把栈顶元素删除出栈，--n这个表达式的值为n-1，即递减后的值，也就是当前栈顶元素第二个元素，也是数组的倒数第二个元素。
        array[n] = null; // 释放栈顶元素引用：将删除的元素置为 null，以方便GC回收。n是数组长度，执行 --n 后，n 的值变为 n-1，这里是原始栈顶元素，即数组的倒数第一个元素。
        return item; // 返回
    }

    @Override
    public int size() {
        return n; // 返回栈中元素的个数，即数组a的大小
    }

    @Override
    public boolean isEmpty() {
        return n == 0; // 栈中元素的个数，即数组a的大小是否为0
    }

    public void print(){
        for (int i = 0; i < n; i++) {
            System.out.print(array[i] + " ");
        }
    }
}

/**
 * @Classname BracketMatchStack
 * @Description
## 设计一个括号匹配的功能？
> 基于数组栈实现括号匹配，比如给一串括号判断是否符合括号原则。
> 利用栈后进先出的特性，用栈保存尚未匹配的左括号，转成字符数组遍历逐个读取每个字符
> 遇到左括号（{, (, [）就压入栈，遇到右括号（}, ), ]）时，判断栈顶元素是否为对应的左括号，若匹配则出栈。
 */
 class BracketMatchStack {
    /**
     * @Description
     * @return boolean
     * @param str 待匹配的字符串
     **/
    public static boolean isOk(String str)
    {
        MyArrayStack<Character> brackets = new MyArrayStack<>(20);
        char c[] = str.toCharArray();// 字符串转数组
        Character top;// 栈顶元素
        for(char x : c){ //O(n)
            switch (x){
                case '{':
                case '(':
                case '[':
                    brackets.push(x);//  入栈O(1)
                    break;
                case '}':
                    top =  brackets.pop();
                    if (top == null) {
                        return false;
                    }
                    if (top == '{') {
                        System.out.println(x + "匹配成功,已出栈");
                        break; // 匹配成功
                    }else{
                        return false;
                    }
                case ')':
                    top =  brackets.pop(); // O(1)
                    if (top == '(') {
                        break;
                    }else{
                        return false;
                    }
                case ']':
                    top =  brackets.pop();
                    if (top == '[') {
                        break;
                    }else{
                        return false;
                    }
                default:
                    break;
            }
        }
        return brackets.isEmpty();
    }
}

class Test{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String next = scanner.next();
            System.out.println(BracketMatchStack.isOk(next));
        }
    }
}

