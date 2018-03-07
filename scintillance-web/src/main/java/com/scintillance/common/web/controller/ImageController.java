package com.scintillance.common.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sse.ssbk.common.UserSession;
import com.sse.ssbk.exception.AppReturnCode;
import com.sse.ssbk.exception.SSEBKRuntimeException;
import com.sse.ssbk.utils.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 图片上传控制器.
 * <p> 从缩略图获取大图，只需要在缩略图名称前面加上“thumbnail”关键字。
 * @author  HT-LiChuanbin 
 * @version 2017年7月26日 上午11:10:53
 */
@RestController
@RequestMapping("/ssebk/common")
public class ImageController {
    @Value("${img.server.dir}")
    private String imageServerDir;
    @Value("${img.server.domain}")
    private String domain;

    /**
     * 上传图片.
     * @param req HttpServletRequest.
     * @return
     * @author    HT-LiChuanbin 
     * @version   2017年7月25日 下午3:53:51
     * @throws    IOException 
     */
    @PostMapping("/images")
    public Result<?> uploadImage(HttpServletRequest req) throws IOException {
        String type = req.getParameter("type");
        UserSession session = BizUtils.getCurrentUser();
        Integer userId = session.getUserId();

        String indexs = req.getParameter("indexs");
        List<UploadImage> argUploadImages = this.assemble(indexs);

        // 上传图片.
        List<UploadImage> uploadImages = new ArrayList<UploadImage>();
        if (!(req instanceof MultipartHttpServletRequest)) {
            return ResultUtils.failure("请选择文件.");
        }
        MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) req;
        MultiValueMap<String, MultipartFile> mMultiValueReq = multiReq.getMultiFileMap();
        Collection<List<MultipartFile>> cMultiFiles = mMultiValueReq.values();
        for (List<MultipartFile> multiFiles : cMultiFiles) {
            for (MultipartFile multiFile : multiFiles) {
                UploadImage uploadImage = FileUploadUtil.uploadImage(multiFile, type, userId, imageServerDir, domain);
                uploadImages.add(uploadImage);
            }
        }

        // 其他业务处理：关联图片顺序和URL.
        List<UploadImageVo> voUploadImages = new ArrayList<UploadImageVo>();
        Iterator<UploadImage> iArgUploadImages = argUploadImages.iterator();
        while (iArgUploadImages.hasNext()) {
            UploadImage argUploadImage = iArgUploadImages.next();
            String argFilename = argUploadImage.getFilename();
            for (UploadImage uploadImg : uploadImages) {
                if (StringUtils.equals(argFilename, uploadImg.getFilename())) {
                    UploadImageVo voUploadImage = new ImageController().new UploadImageVo();
                    voUploadImage.setIndex(argUploadImage.getIndex());
                    voUploadImage.setUrl(uploadImg.getUrl());
                    voUploadImage.setThumbnailUrl(uploadImg.getThumbnailUrl());
                    voUploadImages.add(voUploadImage);
                }
            }
        }
        return ResultUtils.success(voUploadImages);
    }

    /**
     * 组装数据.
     * @param indexs
     * @author  HT-LiChuanbin 
     * @version 2017年7月26日 下午2:08:57
     */
    private List<UploadImage> assemble(String indexs) {
        if (StringUtils.isBlank(indexs)) {
            throw new SSEBKRuntimeException(AppReturnCode.ParaError);
        }
        List<UploadImage> uploadImages = new ArrayList<UploadImage>();
        JSONArray jaIndexs = (JSONArray) JSON.parse(indexs);
        for (int i = 0; i < jaIndexs.size(); i++) {
            JSONObject jIndex = (JSONObject) jaIndexs.get(i);
            String filename = jIndex.getString("filename");
            String index = jIndex.getString("index");
            UploadImage uploadImage = new UploadImage();
            uploadImage.setFilename(filename);
            uploadImage.setIndex(index);
            uploadImages.add(uploadImage);
        }
        return uploadImages;
    }

    @Data
    private class UploadImageDto {
        private List<Index> indexs;
        private String type;
    }

    @Data
    private class UploadImageVo {
        private String index;
        private String url;
        private String thumbnailUrl;
    }

    @Data
    private class Index {
        private String filename;
        private String index;
    }
}