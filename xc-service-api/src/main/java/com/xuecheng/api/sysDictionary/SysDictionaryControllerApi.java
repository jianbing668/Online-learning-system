package com.xuecheng.api.sysDictionary;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "数据字典管理接口",description = "提供数据字典的类型的查询")
public interface SysDictionaryControllerApi {
    @ApiOperation("根据类型查询数据字典")
    public SysDictionary getByType(String type);

    //@ApiOperation("根据id查询数据字典")
    //public SysDictionary getById(String id);
}
