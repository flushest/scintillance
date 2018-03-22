package com.scintillance.common.web.model;

import com.scintillance.common.web.constant.ReturnCode;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Administrator on 2018/3/8 0008.
 */
@Data
public class OpenResult {
    private String code = ReturnCode.SUCCESS;
    private String message = "操作成功";
}
