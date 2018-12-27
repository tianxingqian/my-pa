package com.allcure.spider.service;

import com.allcure.spider.model.DoctorInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.logging.Logger;

/**
 * 抓取的日志修复
 */
public class SpiderDataUpdateService {
    SpiderDataService dataService = new SpiderDataService();
    SpiderService spiderService = new SpiderService();
    int dataVersion = 0;
    Logger logger = Logger.getLogger(SpiderDataUpdateService.class.getName());

    public void updateDoctor() {

        //读取医生信息
        dataService = new SpiderDataService();

        List<DoctorInfo> doctorInfoList = dataService.getDoctorInfos(dataService.getJsonFile());
        //循环， 找出没有头像的数据，进行修复
        for (int i=0; i<doctorInfoList.size(); i++) {
            DoctorInfo doctorInfo = doctorInfoList.get(i);
            //数据版本, null 表示未处理
            if (doctorInfo.getDataVersion() == null || doctorInfo.getDataVersion() == 0) {
                if (doctorInfo.getProfilePicUrl() == null || doctorInfo.getProfilePicUrl().length() <=0) {
                    // 个人网站为空，不需要获取
                    if (doctorInfo.getPersonalWebUrl() == null) {
                        //清空 错误的title
                        doctorInfo.setTitle(null);
                        spiderService.persistence(doctorInfo);
                        continue;
                    }
                    logger.info("开始处理：" + doctorInfo.getDeptType() + " " + doctorInfo.getDeptName() + " " + doctorInfo.getDoctorName());
                    //获取title信息
                    if(getTitle(doctorInfo) == 0) {
                        doctorInfo.setDataVersion(1);
                        //写入文件
                        spiderService.persistence(doctorInfo);
                        try {
                            Thread.sleep(1000 * 1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    doctorInfo.setDataVersion(1);
                    spiderService.persistence(doctorInfo);
                }
            }
        }




    }

    /**
     *  0 正常、1 错误
     * @param doctorInfo
     * @return
     */
    private int getTitle(DoctorInfo doctorInfo) {
        String pUrl = doctorInfo.getPersonalWebUrl();

        if (pUrl == null || pUrl.length() <=0) {
            return 1;
        }
        String htmlStr = SpiderService.httpRequest(pUrl);
        Document document = Jsoup.parse(htmlStr);
        String nameAndTitle = document.select(".doc_title .doc_name").text().trim();
        String title = nameAndTitle.replace(doctorInfo.getDoctorName(), "").replaceAll("\\u00A0", "").trim();
        //替换值
        doctorInfo.setTitle(title);

        return 0;
    }

}
