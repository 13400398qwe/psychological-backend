package com.example.scupsychological.common; // 建议放在一个公共包下

import lombok.Data;

@Data // Lombok 注解，自动生成 getter, setter, toString 等方法
public class Result<T> {

    /** 业务状态码 */
    private Integer code;

    /** 提示信息 */
    private String message;

    /** 数据负载 */
    private T data;

    // 构造函数私有化，不允许外部直接 new
    private Result() {}

    // ======================== 成功的静态方法 ========================
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(T data, String message) {
        Result<T> result = success(data);
        result.setMessage(message);
        return result;
    }


    // ======================== 失败的静态方法 ========================
    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMessage(ResultCode.ERROR.getMessage());
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = error();
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

}