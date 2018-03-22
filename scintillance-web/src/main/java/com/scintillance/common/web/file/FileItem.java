package com.scintillance.common.web.file;

import com.scintillance.common.annotation.RouteProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2018/3/8 0008.
 */
@Builder
@Data
public class FileItem {
    @RouteProperty
    private String storeType;

    private String tempPath;

    private String fileName;

    private MultipartFile file;
}
