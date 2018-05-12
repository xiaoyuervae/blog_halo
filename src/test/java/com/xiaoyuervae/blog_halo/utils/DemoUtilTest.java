package com.xiaoyuervae.blog_halo.utils;

import org.junit.Test;

/**
 * @author : xiaoyuervae
 * @date : 2017/12/26
 * @version : 1.0
 * description:
 */
public class DemoUtilTest {

    @Test
    public void testZip(){
        ZipUtils.unZip("/Users/xiaoyuervae/Desktop/adminlog.html.zip","/Users/xiaoyuervae/Desktop/");
    }
}