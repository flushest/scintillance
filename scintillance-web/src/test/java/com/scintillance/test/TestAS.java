package com.scintillance.test;

import com.scintillance.common.util.SpringUtil;
import com.scintillance.common.web.ApplicationStarter;
import org.junit.Before;

import javax.swing.*;

/**
 * Created by Administrator on 2018/3/2 0002.
 */
public class TestAS {
    @Before
    public void beforeStart() {
        ApplicationStarter.main(new String[0]);
    }

    @org.junit.Test
    public void test() {
        SpringUtil.getBean(Test.class).test();
    }
}
