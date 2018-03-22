package com.scintillance.common.web.model;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Administrator on 2018/3/8 0008.
 */
@Builder
@Data
public class UploadFileResult extends OpenResult {
    private String fileName;
    private String url;
}
