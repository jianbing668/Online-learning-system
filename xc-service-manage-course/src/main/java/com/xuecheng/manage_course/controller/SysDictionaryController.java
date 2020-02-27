package com.xuecheng.manage_course.controller;

import com.xuecheng.api.sysDictionary.SysDictionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName : SysDictionaryController
 * @Description :
 * @Author : JB
 * @Date: 2020-01-15 14:33
 */

@RestController
@RequestMapping("/sys/dic")
public class SysDictionaryController implements SysDictionaryControllerApi {
    @Autowired
    SysDictionaryService sysDictionaryService;

    @Override
    @GetMapping("/get/{type}")
    public SysDictionary getByType(@PathVariable("type") String type) {
        return sysDictionaryService.getByType(type);
    }
    ///@Override
    //@GetMapping("/dictionary/{id}")
    //public SysDictionary getById(@PathVariable("id") String id) {
     //   return sysDictionaryService.getById(id);
    //}
}
