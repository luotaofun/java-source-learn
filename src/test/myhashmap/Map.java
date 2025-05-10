package test.myhashmap;

public interface Map<K,V> {
    V put(K k, V v);
    V get(K k);
    int size();//返回当前map中元素的个数

    // Entry接口
    interface Entry<K,V>{
        K getKey();
        V getValue();
    }
}
