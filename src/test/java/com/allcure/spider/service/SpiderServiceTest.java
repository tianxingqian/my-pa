package com.allcure.spider.service;

import org.junit.Assert;
import org.junit.Test;



import static org.junit.Assert.*;

public class SpiderServiceTest {

    @Test
    public void spide() {


        SpiderService spiderService = new SpiderService();
        spiderService.spide();
    }

    @Test
    public void testHttpRequest() {
        String url = "https://haoping.haodf.com/keshi/1010000/daifu_all_374.htm";
        String text = SpiderService.httpRequest(url);
        System.out.println(text);
        Assert.assertNotNull(text);
    }
}