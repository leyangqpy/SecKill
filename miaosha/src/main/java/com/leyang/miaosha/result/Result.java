package com.leyang.miaosha.result;

/**
 * Created by qianpyn on 2018/5/24.
 */
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result(T data){
        this.code= 0;
        this.msg="success";
        this.data = data;
    }

    private Result(CodeMsg cm){
        if (cm ==null){
            return;
        }
        this.code= cm.getCode();
        this.msg= cm.getMsg();
    }

    public static <T>Result<T> success(T data){
        return new Result<T>(data);
    }

    public static <T>Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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
}
