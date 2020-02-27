package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName : CourseView
 * @Description : 课程详情页 的响应结果类型
 * @Author : JB
 * @Date: 2020-01-30 22:21
 */

@Data
@ToString
@NoArgsConstructor
public class CourseView  implements Serializable {
    private CourseBase courseBase;    //基础信息
    private CourseMarket courseMarket; //课程营销
    private  CoursePic coursePic;      //课程图片
    private TeachplanNode teachplanNode;        //教学计划
}
