package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @ClassName : CustomException
 * @Description :自定义异常类
 * @Author : JB
 * @Date: 2020-01-09 11:40
 */


public class CustomException extends RuntimeException {
    private ResultCode resultCode;
    public CustomException(ResultCode resultCode) {
        //异常信息为错误代码+异常信息
         super("错误代码："+resultCode.code()+"错误信息："+resultCode.message());
         this.resultCode = resultCode;
         }
         public ResultCode getResultCode(){
         return this.resultCode;
        }
}
