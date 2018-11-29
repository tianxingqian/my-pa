package com.allcure.spider.service;


import com.allcure.spider.DoctorInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiderService {


    public void spide() {
        String url = "https://www.haodf.com/keshi/list.htm";

//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.

        try {
            doSpide(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void doSpide(String url) throws IOException {
        //爬取首页所有科室
        List<String> deptList = getDepts(url);
        //获取每个科室的信息
        Map<String, List<DoctorInfo>> doctors = getDoctors(deptList);


        //爬取
    }

    private Map<String, List<DoctorInfo>> getDoctors(List<String> deptList) {
        String url = "https://haoping.haodf.com/keshi/";
        String urlSub = ".htm";

        Map<String, List<DoctorInfo>> rs = new HashMap<>();
        Pattern pattern = Pattern.compile("\\d+");
        for(String dep : deptList) {
            String[] depA = dep.split("_");
            List<DoctorInfo> ds = new ArrayList<>();
            rs.put(depA[0], ds);
            String urlStr = url + depA[1] + "/daifu_all" + urlSub;
            String htmlStr = httpRequest(urlStr);

            Document document = Jsoup.parse(htmlStr);
            Elements elements = document.select(".p_text[rel=true]");
            String pageNumStr = elements.get(0).html();

            //提取页码
            Matcher matcher = pattern.matcher(pageNumStr);
            matcher.find();
            String xx = matcher.group();
            int pageNum = Integer.parseInt(xx);


            for(int i = 2; i<=pageNum; i++) {
                String url2 = url + depA[1] + "/daifu_all_" + i + urlSub;

                String htmlStr2 = httpRequest(url2);
                Document doc = Jsoup.parse(htmlStr2);
                Elements doctEles = doc.select(".good_doctor_list_td");


                List<DoctorInfo> doctorInfos = getDoctorsOfOnePage(doctEles);

                ds.addAll(doctorInfos);



            }


        }

        return rs;
    }

    private List<DoctorInfo> getDoctorsOfOnePage(Elements doctEles) {
        List<DoctorInfo> doctorInfos = new ArrayList<>();
        for (int j=0; j< doctEles.size(); j++) {
            DoctorInfo di = new DoctorInfo();
            String name = doctEles.get(j)
                    .select("tr").first()
                    .select("td").last()
                    .select("a").first()
                    .html();
            //医院
            j++;
            //热度
            j++;
            //联系大夫
            j++;
            System.out.println(name);
            doctorInfos.add(di);
        }
        return doctorInfos;
    }

    private List<String> getDepts(String url) {
        String htmlStr = httpRequest(url);
//        System.out.println(htmlStr);

        Document document = Jsoup.parse(htmlStr);

        List<String> depts = new ArrayList<>();
        Elements elements = document.select("#el_result_content .ct li a");

        for(Element element : elements) {
            String href = element.attr("href");
            href = href.substring(href.lastIndexOf("/") + 1, href.lastIndexOf("."));
            String depStr = element.text() + "_" + href;
//            System.out.println(depStr);
            depts.add(depStr);
        }

        return depts;
    }


    private static String httpRequest(String requestUrl) {

        StringBuffer buffer = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        HttpURLConnection httpUrlConn = null;

        try {
            // 建立get请求
            URL url = new URL(requestUrl);
            httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            // 获取输入流
            inputStream = httpUrlConn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "GBK");
            bufferedReader = new BufferedReader(inputStreamReader);

            // 从输入流读取结果
            buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            // 释放资源
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(httpUrlConn != null){
                httpUrlConn.disconnect();
            }
        }
        return buffer.toString();
    }

}









