package com.scintillance.common.web.util;

import com.scintillance.common.web.constant.ReturnCode;
import com.scintillance.common.web.model.OpenResult;

/**
 * Created by Administrator on 2018/3/8 0008.
 */
public class ResultUtil {
    public static <T extends OpenResult> T failure(T result) {
        result.setCode(ReturnCode.FAILURE);
        result.setMessage("操作失败");
        return result;
    }

    public static <T extends OpenResult> T success(T result) {
        result.setCode(ReturnCode.SUCCESS);
        result.setMessage("操作成功");
        return result;
    }


}
