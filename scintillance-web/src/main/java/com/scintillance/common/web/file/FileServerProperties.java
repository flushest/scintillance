package com.scintillance.common.web.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Administrator on 2018/3/8 0008.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "file.server")
public class FileServerProperties {
    private String domain = "sci"; //域
    private String protocol = "local";//保存方式
    private String uploadUrl = "./files/";  //上传URL
    private String downloadUrl = "./files/"; //下载URL
    private long threshold = 100 * 1024;  //限制大小
    private int thumbnailWidth = 150;  //缩略图宽度
    private int thumbnailHeight = 150; //缩略图高度

}
