package com.scintillance.common.web.controller;

import com.scintillance.common.util.DateUtil;
import com.scintillance.common.web.file.FileItem;
import com.scintillance.common.web.file.FileServerProperties;
import com.scintillance.common.web.file.FileStore;
import com.scintillance.common.web.model.OpenResult;
import com.scintillance.common.web.model.UploadFileResult;
import com.scintillance.common.web.util.RequestContext;
import com.scintillance.common.web.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传控制器.
 * <p> 从缩略图获取大图，只需要在缩略图名称前面加上“thumbnail”关键字。
 * @author  HT-LiChuanbin 
 * @version 2017年7月26日 上午11:10:53
 */
@RestController
@EnableConfigurationProperties(FileServerProperties.class)
@RequestMapping("/sci/common/file")
public class FileController {

    @Autowired
    private FileServerProperties fileServerProperties;

    @Autowired
    private FileStore fileStore;

    @RequestMapping("/upload")
    @ResponseBody
    public UploadFileResult upload(MultipartFile multipartFile) {
        FileItem fileItem = FileItem.builder()
                .file(multipartFile)
                .storeType(fileServerProperties.getProtocol())
                .tempPath(fileServerProperties.getUploadUrl())
                .fileName(generateFileName(multipartFile))
                .build();

        fileStore.store(fileItem);

        return UploadFileResult.builder()
                .fileName(fileItem.getFileName())
                .url(fileServerProperties.getDownloadUrl() + fileItem.getFileName())
                .build();
    }

    private String generateFileName(MultipartFile multipartFile) {
        return String.join("丨", multipartFile.getContentType().replaceAll("/", "_"), RequestContext.currentUserId(), String.valueOf(System.currentTimeMillis()), multipartFile.getOriginalFilename().replaceAll("\\.","~"));
    }

    @RequestMapping("/download/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName) {
        FileItem fileItem = FileItem.builder()
                .storeType(fileServerProperties.getProtocol())
                .tempPath(fileServerProperties.getDownloadUrl())
                .fileName(fileName)
                .build();
        byte[] bytes = fileStore.read(fileItem);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileName.substring(fileName.lastIndexOf("丨" ) + 1).replaceAll("~","."));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(bytes, headers, HttpStatus.);
    }
}