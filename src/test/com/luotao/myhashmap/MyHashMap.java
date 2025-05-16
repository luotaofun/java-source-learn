package test.com.luotao.myhashmap;

interface Map<K,V> {
    V put(K k, V v);
    V get(K k);
    int size();//返回当前map中元素的个数

    // Entry接口
    interface Entry<K,V>{
        K getKey();
        V getValue();
    }
}
/**
 * @Classname HashMap
 * @Description 手写HashMap
 * @Version 1.0.0
 * @Date 2025/5/8 20:51
 * @Author LuoTao
 */
public class MyHashMap<K, V> implements Map<K, V> {
    public static void main(String[] args) {
        MyHashMap myHashMap = new MyHashMap();
        myHashMap.put("张三", 18);
        myHashMap.put("张三", 20);
        System.out.println(myHashMap.get("张三"));
    }
    Entry<K, V>[] table = null;    // 存放数据的数组
    int size=0;

    public MyHashMap(){
        table=new Entry[16];
    }
    /**
     * @Description
     * 1. 将key进行hash取模算出index下标
     * 2. 判断下标对应的元素对象entry是否为空，如果为空则赋值存储，否则使用链表存储
     * 3. 返回值
     * @Author LuoTao
     * @Date 2025/5/8 22:16
     * @return V
     * @param k
     * @param v
     **/
    @Override
    public V put(K k, V v) {
        int index=hash(k);//计算数组下标
        Entry<K, V> entry = table[index];
        if (null==entry){
            table[index] = new Entry<>(k, v, index, null); //指针为null
            size++; // 更新元素个数
        }else{
            table[index]=new Entry<>(k,v,index,entry); //如果table数组的元素已经存在了，则在这个index上维护一个指针指向这个已经存在的元素
        }
        return table[index].getValue();
    }

    /**
     * @Description
     * 1. 将key进行hash取模算出index下标
     * 2. 判断下标对应的元素对象entry是否为空，如果为空则返回null
     * 3. 下标对应的元素对象entry不为空，则对比key是否相同，如果相同则返回value，如果不相同则去判断next指针是否为空
     * 4. 如果指针为空则返回null，如果指针不为空则判断指针的key是否相同，如果相同则返回value，如果不相同则去判断next的指针是否为空，直到key相同为止。
     * @Author LuoTao
     * @Date 2025/5/8 22:44
     * @return V
     * @param k
     **/
    @Override
    public V get(K k) {
        int index=hash(k);//计算数组下标
        Entry<K, V> entry = findValue(table[index],k);
        return entry==null?null:entry.v; //判断下标对应的元素对象entry是否为空，如果为空则返回null
    }
    private Entry<K, V> findValue(Entry<K, V> entry, K k) {
        if(k.equals(entry.getKey()) || k==entry.getKey()){
            //对比key是否相同，如果相同则返回entry
            return entry;
        }else{
            // key不相同则去判断next指针是否为空
            if (entry.next != null){
                //如果指针不为空则递归判断指针的key是否相同，如果相同则返回value，如果不相同则去判断next的指针是否为空，直到key相同为止。
                return findValue(entry.next,k);
            }

        //如果指针为空则返回null
        }
        return null;
    }
    private int hash(K k){
        int index=k.hashCode() % 16;
        return index>=0?index:-index;
    }

    @Override
    public int size() {
        return this.size;
    }

    // 数组table的元素
    class Entry<K,V> implements Map.Entry<K,V>{
        K k;
        V v;
        int index;// 数组table的元素下标
        Entry<K,V> next; // 链表的指针

        public Entry(K k, V v, int index, Entry<K, V> next) {
            this.k = k;
            this.v = v;
            this.index = index;
            this.next = next;
        }

        @Override
        public K getKey() {
            return this.k;
        }

        @Override
        public V getValue() {
            return this.v;
        }
    }
}
