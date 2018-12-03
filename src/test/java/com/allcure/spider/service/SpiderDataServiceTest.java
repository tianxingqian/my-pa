package com.allcure.spider.service;

import org.junit.Test;

import static org.junit.Assert.*;

public class SpiderDataServiceTest {

    @Test
    public void jsonToExcel() {
        SpiderDataService spiderDataService = new SpiderDataService();
        spiderDataService.jsonToExcel();
    }
}