package com.scintillance.auth.model.account;

import com.scintillance.auth.model.auth.QueryAuthResult;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/3/6 0006.
 */
public class QueryUserResult {
    private String userId;//用户id
    private String userName;
    private String email;
    private String score;
    private Date createTime;
    private String roleId;
    private String roleName;
}
