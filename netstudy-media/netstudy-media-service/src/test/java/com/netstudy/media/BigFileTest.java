package com.netstudy.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 测试大文件断点续传
 * @date 2024/4/26 15:23
 **/
public class BigFileTest {
    //断点续传的其中一个思路：先对文件分块，断传输后，继续上传剩下的分块，在传完后对所有分快进行合并


    //分块
    @Test
    public void testChunk() throws IOException {
        //源文件
        File source_file = new File("E:\\Video\\2023-08-26 00-14-01.mp4");
        //分块文件路径
        String chunkFilePath = "E:\\JavaCode\\netstudy_tool\\chunk\\";
        //分块文件大小
        int chunkSize = 1024 * 1024 * 5;
        //分块文件数量
        int chunkNum = (int) Math.ceil(source_file.length() * 1.0 / chunkSize);
        //使用流从源文件读数据，再向分块文件中写数据
        RandomAccessFile raf_r = new RandomAccessFile(source_file, "r");
        //缓存流
        byte[] bytes = new byte[1024];
        //开始分块写入数据
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath + i);
            //分块文件写入
            RandomAccessFile raf_rw = new RandomAccessFile(chunkFile, "rw");
            int len;
            //源文件通过r流读到bytes中，然后rw流读取bytes中的数据，写入到chunk文件中
            while ((len = raf_r.read(bytes)) != -1) {
                raf_rw.write(bytes, 0, len);
                if (chunkFile.length() >= chunkSize) {
                    break;
                }
            }
            raf_rw.close();
            System.out.println("分区保存成功：" + i);
        }
        raf_r.close();
    }

    //合并
    @Test
    public void testMerge() throws IOException {
        //合并后的文件
        File mergeFile = new File("E:\\Video\\testMerge.mp4");
        //源文件
        File sourceFile = new File("E:\\Video\\2023-08-26 00-14-01.mp4");
        //分块文件目录
        File chunkFilePath = new File("E:\\JavaCode\\netstudy_tool\\chunk\\");
        //取出所有分块文件
        File[] chunkFiles = chunkFilePath.listFiles();
        //转换list
        if (chunkFiles != null) {
            List<File> files = Arrays.asList(chunkFiles);
            //对分块文件进行排序
            Collections.sort(files, Comparator.comparingInt(o -> Integer.parseInt(o.getName())));
            //向文件合并写入の流
            RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");
            //缓存流
            byte[] bytes = new byte[1024];
            //遍历分块，全部写入
            for (File file : files) {
                //读取分块の流
                RandomAccessFile raf_r = new RandomAccessFile(file, "r");
                int len;
                while ((len = raf_r.read(bytes)) != -1) {
                    raf_rw.write(bytes, 0, len);
                }
                raf_r.close();
            }
            raf_rw.close();
            //判断与源文件md5是否相同
            FileInputStream fileInputStream_source = new FileInputStream(sourceFile);
            FileInputStream fileInputStream_merge = new FileInputStream(mergeFile);
            String md5_source = DigestUtils.md5Hex(fileInputStream_source);
            String md5_merge = DigestUtils.md5Hex(fileInputStream_merge);
            if (md5_merge.equals(md5_source)) {
                System.out.println("成功合并");
            } else {
                System.out.println("合并失败");
            }
        } else {
            System.out.println("分块文件为空");
        }
    }
}
