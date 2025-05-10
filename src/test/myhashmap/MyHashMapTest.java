package test.myhashmap;

/**
 * @Classname MyHashMapTest
 * @Description TODO
 * @Version 1.0.0
 * @Date 2025/5/9 0:01
 * @Author LuoTao
 */
public class MyHashMapTest {
    public static void main(String[] args) {
        MyHashMap myHashMap = new MyHashMap();
        myHashMap.put("张三", 18);
        myHashMap.put("张三", 20);
        System.out.println(myHashMap.get("张三"));
    }
}

