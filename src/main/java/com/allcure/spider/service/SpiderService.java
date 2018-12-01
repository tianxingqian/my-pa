package com.allcure.spider.service;


import com.allcure.spider.model.DoctorInfo;
import com.allcure.spider.model.Treatment;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiderService {

    private String deptListUrl = "https://www.haodf.com/keshi/list.htm";
    Pattern pattern = Pattern.compile("\\d+");
    WebClient client = new WebClient();
    public static final String savePath = "F:/tmp/spide_doctor";
    private Gson gson = new Gson();
    private String cacheFile = savePath + "/cache.dat";
    private String cacheInfo = null;
    private String errInfoFile = savePath + "/error.log";

    public void spide() {

//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.

        try {
            cacheInfo = getCache();
            doSpide(deptListUrl);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String getCache() throws IOException {
        File file = new File(cacheFile);
        if (file.exists()) {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            cacheInfo = br.readLine();
        } else {
            cacheInfo = "";
        }
        return cacheInfo;
    }

    private void putCache(String info) throws IOException {
        File file = new File(cacheFile);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(info);
        bw.flush();
        bw.close();
    }

    private void doErrLog(String info) throws IOException {
        File file = new File(errInfoFile);
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(info);
        bw.close();
    }

    /**
     * 爬取
     * @param url
     * @throws IOException
     * @throws InterruptedException
     */
    private void doSpide(String url) throws IOException, InterruptedException {
        //爬取首页所有科室
        List<String> deptList = getDepts(url);
        //获取每个科室的信息
        Map<String, List<DoctorInfo>> doctors = getDoctors(deptList);

    }

    /**
     *  获取医生
     * @param deptList
     * @return
     * @throws InterruptedException
     */
    private Map<String, List<DoctorInfo>> getDoctors(List<String> deptList) throws InterruptedException, IOException {
        String url = "https://haoping.haodf.com/keshi/";
        String urlSub = ".htm";

        Map<String, List<DoctorInfo>> rs = new HashMap<>();

        boolean findFlag = false;
        for(String dep : deptList) {
            String[] depA = dep.split("_");

            //无缓存数据
            if (cacheInfo == null || cacheInfo.length() == 0) {
                cacheInfo = depA[0] + "_" + 1;
                findFlag = true;
            } else {
                //查找到缓存的位置
                if (!findFlag) {
                    //找缓存到的科室类型
                    if(! depA[0].equals(cacheInfo.split("_")[0])) {
                        continue;
                    } else {
                        findFlag = true;
                    }
                }
            }
            //缓存到的页
            int cachePage = Integer.valueOf(cacheInfo.split("_")[1]);

            List<DoctorInfo> ds = new ArrayList<>();
            rs.put(depA[0], ds);
            String urlStr = url + depA[1] + "/daifu_all" + urlSub;
            String htmlStr = httpRequest(urlStr);

            Document document = Jsoup.parse(htmlStr);
            //处理第1页的内容
            Elements doctEles = document.select(".good_doctor_list_td");

            ds.addAll(getDoctorsOfOnePage(doctEles, depA));

            Elements elements = document.select(".p_text[rel=true]");
            String pageNumStr = elements.get(0).html();

            //提取页码
            Matcher matcher = pattern.matcher(pageNumStr);
            matcher.find();
            String xx = matcher.group();
            int pageNum = Integer.parseInt(xx);

            for(int i = 2; i<=pageNum; i++) {
                i = cachePage > i ? cachePage : i;
                //存入缓存，读一页存一次
                putCache(depA[0] + "_" + i);

                String url2 = url + depA[1] + "/daifu_all_" + i + urlSub;

                String htmlStr2 = httpRequest(url2);
                Document doc = Jsoup.parse(htmlStr2);
                doctEles = doc.select(".good_doctor_list_td");
                System.out.println(depA[0] + " 第" + i + "页");
                ds.addAll(getDoctorsOfOnePage(doctEles, depA));

            }
        }

        return rs;
    }

    private List<DoctorInfo> getDoctorsOfOnePage(Elements doctEles, String[] depA) throws InterruptedException {
        List<DoctorInfo> doctorInfos = new ArrayList<>();
        for (int j=0; j< doctEles.size(); j++) {
            DoctorInfo di = new DoctorInfo();
            doctorInfos.add(di);
            di.setDeptType(depA[0]);
            di.setDeptId(depA[1]);
            Element el = doctEles.get(j)
                    .select("tr").first()
                    .select("td").last()
                    .select("a").first();
            String doUrl = "https:" + el.attr("href");
            di.setDoctorName(el.html());


            //医院
            j++;
            String deptName = doctEles.get(j).text().replace(" ", "").trim();
            di.setDeptName(deptName);
            //热度
            j++;
            //联系大夫
            j++;

            if (! hasSpide(di)) {
                System.out.println("准备获取：" + di.getDeptType() + " " + di.getDeptName() + " " + di.getDoctorName());
                getDoctorDetail(doUrl, di);
                persistence(di);
                Thread.sleep(1000 * 5);
            } else {
                System.out.println("已存在：" + di.getDeptType() + " " + di.getDeptName() + " " + di.getDoctorName());
            }
        }
        return doctorInfos;
    }

    /**
     * 判断文件存在
     * @param di
     * @return
     */
    private boolean hasSpide(DoctorInfo di) {
        String fileDirStr = savePath + "/" + di.getDeptType() + "/" + di.getDeptName();
        String fileStr = fileDirStr + "/" + di.getDoctorName()+ ".json";
        File file = new File(fileStr);
        return file.exists();
    }

    /**
     * 保存对象
     * @param di
     */
    private void persistence(DoctorInfo di) {
        try {
            String fileDirStr = savePath + "/" + di.getDeptType() + "/" + di.getDeptName();
            File fileDir = new File(fileDirStr);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            String file = fileDirStr + "/" + di.getDoctorName()+ ".json";
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(gson.toJson(di));
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getDoctorDetail(String doUrl, DoctorInfo di) throws InterruptedException {

//        String htmlStr = httpRequest(doUrl);
        Document documentAbout = null;
        Document documentTreatment = null;

        try {
            try {
                HtmlPage page = client.getPage(doUrl);
                HtmlDivision division = page.getHtmlElementById("bp_doctor_about");
                documentAbout = Jsoup.parse(division.asXml());
                documentTreatment = Jsoup.parse(page.getHtmlElementById("bp_doctor_servicestar").asXml());
            } catch (Exception e) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                doErrLog(dateFormat.format(new Date()) + di.getDeptType() + " " + di.getDeptName() + " " + di.getDoctorName());
                e.printStackTrace();
                return;
            }
//            System.out.println(division.asXml());


        } catch (IOException e) {
            e.printStackTrace();
        }

        //介绍
        Element aboutElement = documentAbout.select(".middletr .lt").first();


        String picUrl = aboutElement.select(".ys_tx img").attr("src");
        if (picUrl != null && picUrl.length() > 0) {
            di.setProfilePicUrl("https:" + aboutElement.select(".ys_tx img").attr("src"));
        }
        di.setDeptUrl("https:" + aboutElement.select("td[width=231] a").attr("href"));
        di.setDeptName(aboutElement.select("td[width=231] a h2").text().trim());

        if (StringUtils.isNumeric(aboutElement.select("td[width=110] a span").first().text())) {
            di.setThankMailCnt(Integer.valueOf(aboutElement.select("td[width=110] a span").first().text()));
        } else {
            di.setThankMailCnt(0);
        }
        if (StringUtils.isNumeric(aboutElement.select("td[width=110] a span").last().text())) {
            di.setGiftCnt(Integer.valueOf(aboutElement.select("td[width=110] a span").last().text()));
        } else {
            di.setGiftCnt(0);
        }
        di.setTitle(aboutElement.child(0).child(0).child(2).child(2).text());
        di.setGoodAt(aboutElement.select("#truncate_DoctorSpecialize").text().replace("\"","").trim());

        if (aboutElement.select("#full") != null && aboutElement.select("#full").size() > 0) {
            di.setResume(aboutElement.select("#full").text().replace("\"","").trim());
        } else {
            //没有头像
            if (di.getProfilePicUrl() != null && di.getProfilePicUrl().length() > 0) {
                di.setResume(aboutElement.child(0).child(0).child(4).child(2).text().replace("\"","").trim());
            } else {
                di.setResume(aboutElement.child(0).child(0).child(3).child(2).text().replace("\"","").trim());
            }

        }

        if (aboutElement.select(".doctor-home-page a") != null
                && aboutElement.select(".doctor-home-page a").size() > 0) {
            di.setPersonalWebUrl("https:" + aboutElement.select(".doctor-home-page a").first().attr("href"));
        }


        //病例数
//        System.out.println(di);
        if (documentTreatment.select("#doctorgood .ltdiv a") != null
                && documentTreatment.select("#doctorgood .ltdiv a").size() > 0) {
            String treamentUrl = "https:" + documentTreatment.select("#doctorgood .ltdiv a").last().attr("href");
            Thread.sleep(1000 * 2);
            di.setTreatments(getDoctorTreatments(treamentUrl));
        }

    }

    /**
     * 获取治疗明细 （病种、例数、url）
     *  https://www.haodf.com/doctor/5808-all-servicestar.htm
     * @param treatmentUrl
     * @return
     */
    private List<Treatment> getDoctorTreatments(String treatmentUrl) {
        String htmlStr = httpRequest(treatmentUrl);
        Document document = Jsoup.parse(htmlStr);

        List<Treatment> treatments = new ArrayList<>();

        Elements elements = document.select("#tabmainin a");

        for (int i=1; i< elements.size(); i++) {
            Element el = elements.get(i);
            Treatment treatment = new Treatment();
            treatment.setUrl("https:" + el.attr("href"));

            //冠心病 (3458例)
            String txt = el.html();
            treatment.setDisease(txt.substring(0, txt.indexOf(" ")));
            Matcher matcher = pattern.matcher(txt);
            if(matcher.find()) {
                treatment.setCnt(Integer.parseInt(matcher.group()));
            } else {
                treatment.setCnt(0);
            }
            treatments.add(treatment);
        }

        return treatments;
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









