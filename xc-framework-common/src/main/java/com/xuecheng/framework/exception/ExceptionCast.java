package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @ClassName : ExceptionCast
 * @Description : 异常抛出类
 * @Author : JB
 * @Date: 2020-01-09 11:42
 */


public class ExceptionCast {
    //使用此静态方法抛出自定义异常
     public static void cast(ResultCode resultCode){
     throw new CustomException(resultCode);
     }
}
