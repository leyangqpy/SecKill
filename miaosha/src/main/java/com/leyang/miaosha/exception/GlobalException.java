package com.leyang.miaosha.exception;

import com.leyang.miaosha.result.CodeMsg;

/**
 * Created by qianpyn on 2018/5/31.
 */
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
