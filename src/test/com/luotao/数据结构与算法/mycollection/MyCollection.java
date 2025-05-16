package test.com.luotao.数据结构与算法.mycollection;
import java.util.*;

/**
 * @Classname MyCollection
 * @Description collection接口实现的常用方法
 * @Version 1.0.0
 * @Date 2025/5/15 17:52
 * @Author LuoTao
 */
public class MyCollection {
    public static void main(String[] args) {
        // 接口不能创建对象，需要利用实现类创建对象
        // 集合只能存放引用数据类型的数据，不能是基本数据类型
        Collection col = new ArrayList();
        col.add("age");
        col.add(18); // int自动装箱->integer
        boolean isContainsAge = col.contains("age");
        System.out.println(col);
        List<Integer> list = Arrays.asList(new Integer[]{1, 2, 3, 4, 5});
        col.addAll(list);// 添加集合
        System.out.println(col);

        System.out.println("集合中元素数量：" + col.size());
//        col.clear(); // 清空集合
//        boolean isRemove = col.remove(18);
        System.out.println("集合中的元素是否为空：" + col.isEmpty());

        System.out.println(Arrays.asList(new Integer[]{1, 2, 3, 4, 5}).equals(Arrays.asList(new Integer[]{5,4,3,2,1}))); // false
        System.out.println(Arrays.asList(new Integer[]{1, 2, 3, 4, 5}).equals(Arrays.asList(new Integer[]{1,2,3,4,5}))); // true

        System.out.println("============");
        for (Object item : col) {
            System.out.println(item);
        }
        System.out.println("============");
        for (int i = 0; i < col.size(); i++) {
            System.out.println(col.toArray()[i]);
        }
        System.out.println("============");
        for (Iterator it = col.iterator(); it.hasNext();){
            System.out.println(it.next());
        }
        while (col.iterator().hasNext()){
            System.out.println(col.iterator().next());
        }
    }
}
/**
* @Description:
> List是collection接口的子接口
> List接口实现的常用方法
* @Author: LuoTao
* @Date: 2025-05-15 18:21:29
**/
class MyList{
    public static void main(String[] args) {
        List list = new ArrayList();
        list.add("age");
        list.add(18);
        System.out.println(list);
        list.add(0, "name"); // 将元素添加到指定下标位置
        list.set(2, 19);// 修改指定下标位置的元素
        System.out.println(list);
        list.add(2);
        list.add("abc");
//        list.remove(2);//  删除指定下标元素
        list.remove("abc"); // 删除指定元素
        Object o = list.get(0);

        List myList = new ArrayList();
        myList.addAll(Arrays.asList(new Integer[]{1, 2, 3, 4, 5}));
        for (int i = 0; i < myList.size(); i++){
            System.out.println(myList.get(i));
        }
        for (Object item : myList){
            System.out.println(item);
        }
        for (Iterator it = myList.iterator(); it.hasNext();){
            System.out.println(it.next());
        }
        while (myList.iterator().hasNext()){
            System.out.println(myList.iterator().next());
        }
    }
}

