package com.scintillance.auth;

import com.scintillance.auth.model.auth.QueryAuthParam;
import com.scintillance.auth.model.auth.QueryAuthResult;
import com.scintillance.auth.model.auth.SaveAuthParam;
import com.scintillance.auth.model.auth.SaveAuthResult;

import java.util.List;

/**
 * Created by Administrator on 2018/3/6 0006.
 * 权限服务
 */
public interface IAuthService {
    //A011 获取用户权限
    List<QueryAuthResult> queryAuth(QueryAuthParam param);

    //A012 保存用户权限
    SaveAuthResult saveAuth(SaveAuthParam param);
}
