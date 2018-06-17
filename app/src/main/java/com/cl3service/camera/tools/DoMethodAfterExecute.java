package com.cl3service.camera.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DoMethodAfterExecute {
    private Object object;
    private Method method;
    private Object[] args;

    public DoMethodAfterExecute(Object object, Method method, Object... args){
        this.object = object;
        this.method = method;
        this.args = args;
    }

    public void run(){
        if(method != null){
            try {
                method.invoke(object, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
