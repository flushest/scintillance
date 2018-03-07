package com.scintillance.common.web.util;

import groovy.util.logging.Slf4j;
import lombok.extern.log4j.Log4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@Slf4j
//@Log4j
public class FileUploadUtil {
    private static final String PATH_UPLOAD = "/ssebk/img-server/webapps/ROOT";
    private static final String PATH_TEMP = "C:\\Users\\work_pc\\Desktop\\images\\temp";
    private static final Integer BYTE = 1024;
    private static final Integer THRESHOLD = 4 * BYTE;
    private static final Integer FILE_SIZE_MAX = 40 * BYTE * BYTE;
    private static final String FILE_SEPARATOR = File.separator;
    private static final Integer THUMBNAIL_WIDTH = 150;
    private static final Integer THUMBNAIL_HEIGHT = 150;

    /**
     * Uploading file implemented via Apache Commons FileUpload project.
     * @param req HttpServletRequest
     * @return filename 文件名称.
     * @version 2016年09月18日 13:38
     */
    public static String upload(HttpServletRequest req) {
        // 多文件上传
        // List<MultipartFile> mf = request.getFiles(name);
        File fileUpload = new File(PATH_UPLOAD);
        if (!fileUpload.exists()) {
            fileUpload.mkdirs();
        }
        File fileTempPath = new File(PATH_TEMP);
        if (!fileTempPath.exists()) {
            fileTempPath.mkdirs();
        }

        try {
            new String("fa".getBytes(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(THRESHOLD);
        factory.setRepository(fileTempPath);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(FILE_SIZE_MAX);
        String filename = null;
        try {
            for (FileItem fileItem : upload.parseRequest(req)) {
                String itemName = fileItem.getName();
                if (!StringUtils.isBlank(itemName)) {
                    fileItem.write(new File(PATH_UPLOAD, itemName));
                    filename = itemName;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return filename;
    }

    /**
     * SSEBK业务上传.
     * @param mfImg MultipartFile.
     * @return      文件名称.
     * @author      Thomas
     * @since       2016年11月20日 下午7:15:02
     */
    public static UploadImage uploadImage(MultipartFile mfImg, String type, Integer userId, String imageServerDir, String domain) throws IOException {
        validate(mfImg, type, userId);
        final String pathROOT = imageServerDir;
        final String strImgFolder = getImgFolder();
        final String imgDir = pathROOT + FILE_SEPARATOR + strImgFolder;

        File fImgDir = new File(imgDir);
        if (!fImgDir.exists()) {
            fImgDir.mkdirs();
        }

        String strImgName = mfImg.getOriginalFilename();
        String imgFormat = strImgName.substring(strImgName.lastIndexOf(".") + 1, strImgName.length());
        String bizImgName = getBizImgName(type, userId, imgFormat);
        String imgPath = getImgPath(imgDir, bizImgName);
        String imgUrl = getImgUrl(domain, strImgFolder, bizImgName);

        // 缩略图.
        String bizThumbImgName = "thumbnail" + bizImgName;
        String imgThumbnailPath = getImgThumbnailPath(imgDir, bizThumbImgName);
        String imgThumbUrl = getImgUrl(domain, strImgFolder, bizThumbImgName);
        ImageCompressUtils.resize(mfImg.getBytes(), imgThumbnailPath, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);

        UploadImage uploadImage = new UploadImage();
        uploadImage.setFilename(strImgName);
        uploadImage.setUrl(imgUrl);
        uploadImage.setThumbnailUrl(imgThumbUrl);

        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + imgPath);

        return FileUtils.writeViaNIO(imgPath, mfImg.getBytes()) ? uploadImage : null;
    }

    /**
     * 
     * @param imgDir
     * @param bizImgName
     * @return
     * @author HT-LiChuanbin 
     * @version 2017年7月31日 下午6:48:21
     */
    private static String getImgThumbnailPath(String imgDir, String bizImgName) {
        return imgDir + FILE_SEPARATOR + bizImgName;
    }

    /**
     * 
     * @param imgDir
     * @param bizImgName
     * @return
     * @author HT-LiChuanbin 
     * @version 2017年7月31日 下午6:48:25
     */
    private static String getImgPath(String imgDir, String bizImgName) {
        return imgDir + FILE_SEPARATOR + bizImgName;
    }

    /**
     * 
     * @param mfImg
     * @param type
     * @param userId
     * @author HT-LiChuanbin 
     * @version 2017年7月31日 下午6:48:28
     */
    private static void validate(MultipartFile mfImg, String type, Integer userId) {
    }

    /**
     * <p>注意：
          1. pictureId格式 type_userId_timestamp；
          2. 按每月生成文件夹 ，服务器同时生成大图与缩略图，获取图片时以small/big区分。
     * @return
     * @author  HT-LiChuanbin 
     * @version 2017年7月26日 下午2:43:02
     */
    private static String getBizImgName(String type, Integer userId, String imgFormat) {
        return type + userId + String.valueOf(new Date().getTime()) + CaptchaUtil.get4DigitsAndLetters() + "." + imgFormat;
    }

    /**
     * 获取图片URL.
     * @param path
     * @param imgName
     * @return
     * @author HT-LiChuanbin 
     * @version 2017年7月26日 下午1:43:04
     */
    private static String getImgUrl(String domain, String imgFolder, String bizImgName) {
        return domain + imgFolder + FILE_SEPARATOR + bizImgName;
    }

    /**
     * 获取文件URL.
     * <p> eg. http://183.129.255.165:8300/20170726/tomato.jpg
     * @param host
     * @param port
     * @param filepath
     * @param filename
     * @return
     * @author HT-LiChuanbin 
     * @version 2017年7月26日 上午11:33:37
     */
    public static String getFileUrl(String host, String port, String filepath) {
        final String scheme = "http";
        return scheme + "://" + host + ":" + port + filepath + "/";
    }

    /**
     * 获取文件保存路径.
     * @return
     * @author HT-LiChuanbin 
     * @version 2017年7月26日 下午1:30:01
     */
    public static String getImgFolder() {
        Date now = new Date();
        return FILE_SEPARATOR + DateUtils.getyyyyMM(now);
    }

    public static void main(String[] args) {
    }
}