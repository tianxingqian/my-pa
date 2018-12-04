package com.allcure.spider.service;

import org.junit.Test;

import static org.junit.Assert.*;

public class SpiderDataUpdateServiceTest {

    @Test
    public void updateDoctor() {
        SpiderDataUpdateService service = new SpiderDataUpdateService();
        service.updateDoctor();
    }
}