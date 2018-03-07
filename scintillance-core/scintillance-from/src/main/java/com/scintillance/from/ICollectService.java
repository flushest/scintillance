package com.scintillance.from;

import com.scintillance.from.model.UploadFileParam;
import com.scintillance.from.model.UploadFileResult;

/**
 * Created by Administrator on 2018/3/6 0006.
 * 文件上传的接口
 */
public interface ICollectService {
    //F001 文件上传的接口
    UploadFileResult UploadFile(UploadFileParam param);
    //F002
}
