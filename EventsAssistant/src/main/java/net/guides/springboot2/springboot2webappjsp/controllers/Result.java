package net.guides.springboot2.springboot2webappjsp.controllers;

import lombok.Data;

import java.io.Serializable;
@Data
public class Result implements Serializable {
        private String code;
        private String msg;
        private Object data;

    public static Result succ(Object data) {
        Result m = new Result();
        m.setCode("0");
        m.setData(data);
        m.setMsg("Update success!");
        return m;
    }

    public static Result succ(String mess) {
        Result m = new Result();
        m.setCode("0");
        m.setData(null);
        m.setMsg(mess);
        return m;
    }

    public static Result succ(String mess, Object data) {
        Result m = new Result();
        m.setCode("0");
        m.setData(data);
        m.setMsg(mess);
        return m;
    }
    public static Result fail(String mess) {
        Result m = new Result();
        m.setCode("-1");
        m.setData(null);
        m.setMsg(mess);
        return m;
    }

    public static Result fail(String mess, Object data) {
        Result m = new Result();
        m.setCode("-1");
        m.setData(data);
        m.setMsg(mess);
        return m;
    }
}
