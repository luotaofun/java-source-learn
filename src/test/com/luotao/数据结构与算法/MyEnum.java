package test.com.luotao.数据结构与算法;

import java.util.HashMap;

/**
 * 错误码枚举类
 * Java 枚举要求枚举实例必须最先声明。
 * @author LuoTao
 * @version 1.0.0
 * 2025/5/15 22:21
 */
enum ErrorCode {
    // -------枚举实例----------
    // 成功
    SUCCESS(200, "成功"),
    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    // 服务器错误
    INTERNAL_SERVER_ERROR(500, "内部服务器错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用") {
        /**
        * 这个匿名内部类是枚举类的子类实例。
         * 枚举实例的匿名内部类方法：这种方式允许某个枚举值拥有与其他枚举值不同的实现逻辑，适用于需要个性化扩展的场景。
        *
        * @author: LuoTao
        * 2025-05-15 23:40:28
        **/
        @Override
        public String getMessage() {
            return "服务不可用，请稍后再试";
        }
    };

    // ------- 枚举成员 ----------
    private final String message;
    private final int code;

    /**
     * 使用 Map 保存 ErrorCode 实例，提升查找效率
    * @author: LuoTao
    * 2025-05-15 23:05:23
    **/
    private static final HashMap<Integer, ErrorCode> cache = new HashMap<>();
    static {
        /**
         *  values() 是由编译器自动生成的静态方法，用于返回该枚举类所有枚举实例的数组。
         *
         * @author: LuoTao
         * 2025-05-15 23:34:03
         **/
        for (ErrorCode errorCode : values()) {
            cache.put(errorCode.getCode(), errorCode);
        }
        /**
        * 根据名字获取枚举实例
        *
        * @author: LuoTao
        * 2025-05-15 23:34:03
        **/
        System.out.println(ErrorCode.valueOf("SUCCESS") == ErrorCode.SUCCESS); // true
    }

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据 code 获取对应的枚举
     */
    public static ErrorCode fromCode(int code) {
        return cache.get(code);
    }
}

/**
 * API 响应封装类
 *
 * @param <T> 响应数据的类型
 * @author LuoTao
 * 2025/5/15 22:33
 */
class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    /**
     * Java 的泛型是 伪泛型（擦除式泛型），也就是说在运行时，所有泛型信息都会被擦除，只保留原始类型（raw type），这是为了兼容旧版本 JVM。
     * 泛型检查是在 编译阶段完成的，确保类型安全，避免运行时 ClassCastException，
     * 调用时编译器根据上下文自动推断出泛型参数的具体类型。
     *
     * T（Type）或? ，表示任意类型
    * <T> 声明泛型的位置，可以在类、接口或方法上
     * ?（通配符）表示未知类型，常用于限制边界
     * 所有泛型参数会被替换为 Object（如果是限定类型的则替换为其上界）
     * ? extends T 上界通配符：只能读取，除 null外不能写入(防止插入错误类型),数据生产者（只读）
     * List<? extends Number> numbers; // 表示listJ<Number>、listJ<Number的子类>的父类
     * ? super T 下界通配符：只能读为 Object 类型,数据消费者
     * List<? super Number> numbers; // 表示listJ<Number>、listJ<Number的父类>的父类
    * @author: LuoTao
    * 2025-05-16 04:28:21
    **/
    public <T> void print(T item) {
        System.out.println(item);
    }

    /**
     * 创建一个成功的API响应对象
     *
     * @param data 响应的数据
     * @return 成功的API响应对象
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    /**
     * 创建一个错误的API响应对象
     *
     * @param code    响应码
     * @param message 错误消息
     * @return 错误的API响应对象
     */
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    /**
     * 根据枚举成员创建一个错误的API响应对象
     *
     * @param errorCode 错误的枚举成员
     * @return 错误的API响应对象
     */
    public static <T> ApiResult<T> error(ErrorCode errorCode) {
        return new ApiResult<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    // 构造函数，初始化ApiResult对象
    private ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
}