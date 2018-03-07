package com.scintillance.auth;

import com.scintillance.auth.model.account.QueryUserParam;
import com.scintillance.auth.model.account.QueryUserResult;
import com.scintillance.auth.model.account.SaveUserParam;
import com.scintillance.auth.model.account.SaveUserResult;

/**
 * Created by Administrator on 2018/3/6 0006.
 * 用户服务
 */
public interface IAccountService {
    //A001 获取用户信息
    QueryUserResult queryUser(QueryUserParam param);

    //A002 保存用户信息
    SaveUserResult saveUser(SaveUserParam param);
}
