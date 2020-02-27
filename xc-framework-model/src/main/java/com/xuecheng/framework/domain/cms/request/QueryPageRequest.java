package com.xuecheng.framework.domain.cms.request;

import com.xuecheng.framework.model.request.RequestData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : QueryPageRequest
 * @Description : 定义请求模型QueryPageRequest，此模型作为查询条件类型 为后期扩展需求，请求类型统一继承RequestData类型。
 * @Author : JB
 * @Date: 2019-12-03 11:05
 */

@Data
public class QueryPageRequest extends RequestData {
    //站点id
    @ApiModelProperty("站点id")
    private String siteId;
    //页面ID
    @ApiModelProperty("页面ID")
    private String pageId;
    // 页面名称
    @ApiModelProperty("页面名称")
    private String pageName;
    // 别名
    @ApiModelProperty("别名")
    private String pageAliase;
    // 模版id
    @ApiModelProperty("模版id")
    private String templateId;
}
