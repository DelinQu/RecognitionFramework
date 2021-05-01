package com.lin.controller;

import com.lin.Repository.PictureRepository;
import com.lin.common.api.ApiResult;
import com.lin.entity.Picture;
import com.lin.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


@RestController
@RequestMapping("/image")
public class PictureController {

    @Autowired
    UploadService uploadService;

    @Value("${img.imgURL}")
    private String imgURL;

    @Value("${img.newImgURL}")
    private String newImgURL;

    @Value("${upload.dir}")
    private String uploadDir;


    @Value("${upload.newDir}")
    private String newDir;

    @Autowired
    private PictureRepository pictureRepository;



    @PostMapping(value="/upload")
    public ApiResult<Picture> update(@RequestParam("file") MultipartFile file) {
        if(file==null)
            ApiResult.failed("empty file!");
        // 图片名
        List<Picture> all = pictureRepository.findAll();
        int id = all.size()+1;
        String indexPath=Long.toString(id);

        // 处理文件名
        int lastIndexOf = file.getOriginalFilename().lastIndexOf(".");
        // 获取文件的后缀名
        String suffix = file.getOriginalFilename().substring(lastIndexOf);
        String fileName=indexPath + suffix;

        String localPath = uploadDir +fileName;
        String path = imgURL + fileName;
        String newPath = newImgURL + fileName;
        Picture picture = new Picture();
        picture.setPath(path);
        picture.setLocalPath(localPath);
        picture.setNewPath(newPath);
        // 插入对象
        pictureRepository.save(picture);
        // 上传
        uploadService.upload(file,fileName);

        /********** 调用算法 **********/

        Process p;
        //test.bat中的命令是ipconfig/all
        String cmd="python /home/qdl/Desktop/work2.py -i "+ uploadDir+fileName +" -o "+ newDir+fileName;
        try
        {
            //执行命令
            p = Runtime.getRuntime().exec(cmd);
            //取得命令结果的输出流
            InputStream fis=p.getInputStream();
            //用一个读输出流类去读
            InputStreamReader isr=new InputStreamReader(fis);
            //用缓冲器读行
            BufferedReader br=new BufferedReader(isr);
            String line=null;
            //直到读完为止
            while((line=br.readLine())!=null)
            {
                System.out.println(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        /////////////////////////////////////


        // 完成
        return ApiResult.success(picture);
    }
}
