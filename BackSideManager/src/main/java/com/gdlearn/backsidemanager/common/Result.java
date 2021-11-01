package com.gdlearn.backsidemanager.common;

import lombok.Data;


@Data
public class Result {

    private int code;
    private String msg;
    private Object data;


    public static Result succ(Object data){
        Result r = new Result();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    public static Result succ(String msg,Object data){
        return succ(200,msg,data);
    }

    public static Result succ(int code, String msg, Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static Result fail(String msg){
        return fail(500,msg,null);
    }

    public static Result fail(int code, String msg, Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
}
