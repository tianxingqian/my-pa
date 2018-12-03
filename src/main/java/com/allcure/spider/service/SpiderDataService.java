package com.allcure.spider.service;

import com.allcure.spider.model.DoctorInfo;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SpiderDataService {

    /**
     * json 转Excel
     */
    public void jsonToExcel() {
        List<DoctorInfo> doctors = getDoctorInfos();
        for (DoctorInfo item: doctors) {
            System.out.println(item);

        }
    }

    /**
     * 入库
     */
    public void jsonToDb() {
        List<DoctorInfo> doctors = getDoctorInfos();
        System.out.println();
    }

    /**
     * 将json数据转成对象 {@link DoctorInfo}
     * @return
     */
    private List<DoctorInfo> getDoctorInfos() {
        Gson gson = new Gson();
        //TODO 将json数据转成对象
        //读取所有的json文件
        List<File> files = getJsonFile();
        //转成DoctorInfo对象
        List<DoctorInfo> rsList = new ArrayList<>();
        for (File file : files) {
            //读取json文件的内容
            String jsonStr = readFileContent(file);
            //存入List中
            DoctorInfo info = gson.fromJson(jsonStr, DoctorInfo.class);
            rsList.add(info);
        }
        return rsList;
    }

    private String readFileContent(File file) {
        return null;
    }

    private List<File> getJsonFile() {
        String savePath = SpiderService.savePath;

        List<File> files = new ArrayList<>();
        getJsonFile(savePath, files);

        for (File item : files) {
            System.out.println(item.getPath());
        }

        return files;
    }

    private void getJsonFile(String savePath, List<File> files) {

        File file = new File(savePath);
        if (file.isDirectory()) {
            File[] filesList = file.listFiles();
            for (File item : filesList) {
                getJsonFile(savePath, files);
            }
        } else {
            files.add(file);
        }

    }


}
