package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName : SysDictionaryService
 * @Description :
 * @Author : JB
 * @Date: 2020-01-15 14:37
 */

@Service
public class SysDictionaryService {
    @Autowired
    SysDictionaryRepository sysDictionaryRepository;

    //根据类型查询数据字典
    public SysDictionary getByType(String type){

        return sysDictionaryRepository.findByDType(type);
    }

//    //根据id查数据字典
//    public SysDictionary getById(String id){
//        Optional<SysDictionary> optionalSysDictionary = sysDictionaryRepository.findById(id);
//        SysDictionary sysDictionary = optionalSysDictionary.get();
//        return sysDictionary;
//    }
}
