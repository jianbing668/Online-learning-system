import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.security.auth.login.AppConfigurationEntry;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName : testFreemarker
 * @Description : 测试类
 * @Author : JB
 * @Date: 2020-01-08 11:02
 */


public class testFreemarker {


    //基于模板文件静态化
    @Test
    public void testGenerateHtml() throws IOException, TemplateException {
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板路径
         String classpath = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板
        Template template = configuration.getTemplate("test1.ftl");
        //数据模型
        Map<String,Object> map = new HashMap<>();
        map.put("name","哈哈哈");
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //静态化内容
        System.out.println(content);
        InputStream inputStream = IOUtils.toInputStream(content);
        //输出文件
        FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/test1.html"));
        int copy = IOUtils.copy(inputStream, fileOutputStream);
    }


    //基于模板字符串生成静态化文件
    @Test
    public void testGenerateHtmlByString() throws IOException, TemplateException {
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        ///模板内容，这里测试时使用简单的字符串作为模板
        String templateString="" +
                "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                " 名称：${name}\n" +
                " </body>\n" +
                "</html>";
        //模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateString);
        configuration.setTemplateLoader(stringTemplateLoader);
        //得到模板
        Template template = configuration.getTemplate("template","utf-8");

        //数据模型
        Map<String,Object> map = new HashMap<>();
        map.put("name","vaim程序员");
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //静态化内容
        System.out.println(content);

        InputStream inputStream = IOUtils.toInputStream(content);
        //输出文件
        FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/test2.html"));
        IOUtils.copy(inputStream, fileOutputStream);

    }



}
