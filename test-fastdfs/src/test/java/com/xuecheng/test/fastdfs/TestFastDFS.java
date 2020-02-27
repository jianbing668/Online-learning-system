package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

    //上传文件
    @Test
    public void testUpload(){
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout="+ClientGlobal.g_network_timeout+"ms");
            System.out.println("charset="+ClientGlobal.g_charset);
            //创建客户端
            TrackerClient tc = new TrackerClient();
            //连接tracker Server
            TrackerServer ts = tc.getConnection();
            if(ts ==null){
                System.out.println("getConnection return null");
                return ;
            }
            //获取一个storage server
            StorageServer ss = tc.getStoreStorage(ts);
            if(ss==null){
                System.out.println("getStoreStorage return null");
            }
            //创建一个storage存储客户端
            StorageClient1 sc1 = new StorageClient1(ts,ss);
            NameValuePair[] meta_list = null;
            String item = "E:\\testfdfs.jpg";
            String fileid;
            fileid = sc1.upload_appender_file1(item,"jpg",meta_list);
            System.out.println("upload local file"+item+"ok,fileid="+fileid);
            //group1/M00/00/00/wKgKgF4u-hGEROa1AAAAACC0WDQ030.jpg
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //下载文件
    @Test
    public void testDowload(){
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout="+ClientGlobal.g_network_timeout+"ms");
            System.out.println("charset="+ClientGlobal.g_charset);
            //创建客户端
            TrackerClient tc = new TrackerClient();
            //连接tracker Server
            TrackerServer ts = tc.getConnection();
            if(ts ==null){
                System.out.println("getConnection return null");
                return ;
            }
            //获取一个storage server
            StorageServer ss = tc.getStoreStorage(ts);
            if(ss==null){
                System.out.println("getStoreStorage return null");
            }
            //创建一个storage存储客户端
            StorageClient1 sc1 = new StorageClient1(ts,ss);
            byte[] result = sc1.download_file1("group1/M00/00/00/wKgKgF4u-hGEROa1AAAAACC0WDQ030.jpg");
            File file =  new File("d:/1.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(result);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
