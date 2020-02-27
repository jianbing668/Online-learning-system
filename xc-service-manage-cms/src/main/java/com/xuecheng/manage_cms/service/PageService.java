package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName : PageService
 * @Description : 页面的service
 * @Author : JB
 * @Date: 2019-12-09 14:19
 */

@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    CmsSiteRepository cmsSiteRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;


    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;


//    /***
//     * 页面列表分页查询
//     * @param page 当前页码
//     * @param size 页面显示个数
//     * @param queryPageRequest 查询条件
//     * @return 页面列表
//     * */
//    public QueryResponseResult findList(int page,int size,QueryPageRequest queryPageRequest){
//        if (queryPageRequest == null) {
//            queryPageRequest = new QueryPageRequest();
//        }
//        if (page <= 0) {
//            page = 1;
//        }
//        page = page - 1;
//        //为了适应mongodb的接口将页码减1
//        if (size <= 0) {
//            size = 20;
//        }
//        //分页对象
//        Pageable pageable = new PageRequest(page, size);
//        //分页查询
//        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
//        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
//        cmsPageQueryResult.setList(all.getContent()); cmsPageQueryResult.setTotal(all.getTotalElements());
//        //返回结果
//    return new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
//        }

    /** * 页面列表分页查询
     * @param page 当前页码
     * @param size 页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页面列表 */
    public  QueryResponseResult findList(int page,int size,QueryPageRequest queryPageRequest){
         //条件匹配器
        //页面名称模糊查询，需要自定义字符串的匹配器实现模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值  将所需的条件值封装到对象中
        CmsPage cmsPage = new CmsPage();
        //站点ID
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //页面别名
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //页码
        page = page - 1;
        //分页对象
        Pageable pageable = new PageRequest(page, size);
        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
    }

    //新增页面
    public CmsPageResult add(CmsPage cmsPage){
        //检验页面名称、站点ID、页面webPath的唯一性
    CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),cmsPage.getSiteId(),cmsPage.getPageWebPath());
     if(cmsPage1 == null){
         //调用dao增加页面
         cmsPage.setPageId(null);//添加页面主键由Spring Data 自动生成
         CmsPage  save = cmsPageRepository.save(cmsPage);
         return new CmsPageResult(CommonCode.SUCCESS,save);
     }
     //添加失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }

     //根据页面id查询页面
    public CmsPage getById(String id){
        Optional<CmsPage> optional =  cmsPageRepository.findById(id);
        if(optional != null){
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }

    //修改页面
    public CmsPageResult update(String id,CmsPage cmsPage){
        //根据id从数据库查询页面信息
        CmsPage cmsPage1 = this.getById(id);
        if(cmsPage1 != null){
            //准备更新数据，设置要修改的数据
            //更新模板id
            cmsPage1.setTemplateId(cmsPage.getTemplateId());
             //更新所属站点
            cmsPage1.setSiteId(cmsPage.getSiteId());
            // 更新页面别名
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
            // 更新页面名称
            cmsPage1.setPageName(cmsPage.getPageName());
            // 更新访问路径
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
            // 更新物理路径
            cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新dataUrl
            cmsPage1.setDataUrl(cmsPage.getDataUrl());
            //执行更新
            CmsPage save = cmsPageRepository.save(cmsPage1);
            if (save != null) {
                //返回成功
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        //修改失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }


    //删除页面
    public ResponseResult delete(String id){
        CmsPage one = this.getById(id);
        if(one != null){
            //删除页面
            cmsPageRepository.deleteById(id);
            return  ResponseResult.SUCCESS();
           // 等价于 return new ResponseResult(CommonCode.SUCCESS);
        }
        return ResponseResult.FAIL();
    }

     //页面静态化
    public String getPageHtml(String pageId) {
        //获取页面模型数据
        Map model = this.getModelByPageId(pageId);
        if (model == null) {
            //获取页面模型数据为空
             ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面模板
        String templateContent = getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(templateContent)) {
            //页面模板为空
             ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL); }
            // 执行静态化
            String html = generateHtml(templateContent, model);
            if (StringUtils.isEmpty(html)) {
                ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
            }
            return html;
        }


    //页面静态化
    public String generateHtml(String template,Map model){
            try {
                //生成配置类
                Configuration configuration = new Configuration(Configuration.getVersion());
                //模板加载器
                StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
                stringTemplateLoader.putTemplate("template",template);
                //配置模板加载器
                configuration.setTemplateLoader(stringTemplateLoader);
                //获取模板
                Template template1 = configuration.getTemplate("template");
                String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
                return html;
                 } catch (Exception e) {
                e.printStackTrace();
                }
                return null;
            }

    //获取页面模板
    public String getTemplateByPageId(String pageId) {
        //查询页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
            }
            //页面模板
            String templateId = cmsPage.getTemplateId();
            if (StringUtils.isEmpty(templateId)) {
                //页面模板为空
                ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
            }
           //获取页面的模板信息
            Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
            if (optional.isPresent()) {
                CmsTemplate cmsTemplate = optional.get();
                //模板文件id
                String templateFileId = cmsTemplate.getTemplateFileId();
                //取出模板文件内容
                GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
                //打开下载流对象
                GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
                //创建GridFsResource
                GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
                try {
                    String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                    return content;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
    }


    //获取页面模型数据
    public Map getModelByPageId(String pageId){
        //查询页面信息
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl)){
             //页面dataUrl为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return  body;
    }


    //页面发布
    public ResponseResult postPage(String pageId){
        //执行静态化
        String pageHtml = this.getPageHtml(pageId);
        if(StringUtils.isEmpty(pageHtml)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //保存静态化文件
         CmsPage cmsPage = saveHtml(pageId, pageHtml);
        // 发送消息
         sendPostPage(pageId);
         return new ResponseResult(CommonCode.SUCCESS);
    }
    //发送页面发布消息
    private void sendPostPage(String pageId){
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        Map<String,String> msgMap = new HashMap<>();
        msgMap.put("pageId",pageId);
        //消息内容
         String msg = JSON.toJSONString(msgMap);
        // 获取站点id作为routingKey
         String siteId = cmsPage.getSiteId();
        // 发布消息
        this.rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,msg);
    }
    //保存静态页面内容
    private CmsPage saveHtml(String pageId,String content){
        //查询页面
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        CmsPage cmsPage = optional.get();
        //存储之前先删除
        String htmlFileId = cmsPage.getHtmlFileId();
        if(StringUtils.isNotEmpty(htmlFileId)){
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        //保存html文件到GridFS
        InputStream inputStream = IOUtils.toInputStream(content);
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        //文件id
        String fileId = objectId.toString();
        //将文件id存储到cmspage中
        cmsPage.setHtmlFileId(fileId);
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }

    //保存页面，有就更新，没有就新增
    public CmsPageResult save(CmsPage cmsPage) {
        //判断页面是否存在
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(cmsPage1 != null){
            //进行更新
            return  this.update(cmsPage1.getPageId(),cmsPage);
        }
        //进行增加
        return  this.add(cmsPage);
    }
    //一键发布
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //将页面信息存储到cmspage集合
        CmsPageResult save = this.save(cmsPage);
        if(!save.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CmsPage cmsPageSave = save.getCmsPage();
        //得到页面的id
        String pageId = cmsPageSave.getPageId();
        //执行页面发布（先静态化、保存GridFS，向MQ发送消息）
        ResponseResult postResult = this.postPage(pageId);
        if(!postResult.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //拼接页面Url= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
        //取出站点
        String siteId = cmsPageSave.getSiteId();
        //查询站点信息
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        String pageUrl = cmsSite.getSiteDomain()+cmsSite.getSiteWebPath()+cmsPageSave.getPageWebPath()+cmsPageSave.getPageName();
        return  new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }

    //根据站点id查询站点信息
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return  optional.get();
        }
        return  null;
    }
}
