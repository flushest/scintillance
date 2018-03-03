package com.scintillance.common.resource;

import com.scintillance.common.util.Assert;

/**
 * Created by Administrator on 2017/11/12 0012.
 */
public class URIParser {

    private String uri;

    public String getProtocol() {
        Assert.notHasText(uri,"uri must be not null");
        int protocolEndIndex = uri.indexOf(":");
        Assert.judge(protocolEndIndex == -1,"can not find protocol from uri:"+uri);
        return uri.substring(0,protocolEndIndex).toLowerCase();
    }

    public URIParser(String uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return uri;
    }
}
