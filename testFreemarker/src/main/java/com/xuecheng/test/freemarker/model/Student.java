package com.xuecheng.test.freemarker.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @ClassName : Student
 * @Description : 模型类型用于测试
 * @Author : JB
 * @Date: 2020-01-08 09:28
 */

@Data
@ToString
public class Student {
    private String name;
    private int age;
    private Date birthday;
    private Float money;
    private List<Student> friends;  //朋友列表
    private  Student bestFriend;    //最好的朋友

}
