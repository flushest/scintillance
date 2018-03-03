package com.scintillance.common.resource;

import com.scintillance.common.util.Assert;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/12 0012.
 */
public class URI implements Serializable{

    private URIParser urlParser;
    private String protocol;

    public URI(String url) {
        Assert.notHasText(url,"url must be not null");
        this.urlParser = new URIParser(url);
        this.protocol = urlParser.getProtocol();
    }

    public String getUrl() {
        return urlParser.getUrl();
    }

    public URIParser getUrlParser() {
        return urlParser;
    }

    public String getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return getUrl();
    }
}