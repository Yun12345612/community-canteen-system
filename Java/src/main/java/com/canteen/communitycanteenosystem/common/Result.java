package com.canteen.communitycanteenosystem.common;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.common
 * @Author: 夏日花店
 * @CreateTime: 2026/1/27 15:29
 * @Description: 结果类
 * @Version: 1.0
 */
public class Result<T> {
    // 状态码：200-成功，400-失败，500-服务器内部错误（可扩展更多状态码）
    private Integer code;
    // 提示信息：成功/失败的友好提示
    private String msg;
    // 响应数据：泛型封装，支持任意数据类型（实体、集合、null等）
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ---------------------- 静态快捷方法：简化调用，无需手动new ----------------------

    /**
     * 成功：无返回数据（如新增/修改/删除接口，仅提示成功）
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功：有返回数据（如查询/登录接口，返回业务数据）
     *
     * @param data 要返回的业务数据（实体、集合等）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功：自定义提示信息+返回数据（可选，灵活适配）
     *
     * @param msg  自定义成功提示
     * @param data 要返回的业务数据
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    /**
     * 失败：自定义错误提示（无返回数据，如参数错误/业务失败）
     *
     * @param msg 自定义失败提示
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(400, msg, null);
    }

    /**
     * 失败：自定义状态码+错误提示（如500服务器错误）
     *
     * @param code 自定义错误码
     * @param msg  自定义失败提示
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
