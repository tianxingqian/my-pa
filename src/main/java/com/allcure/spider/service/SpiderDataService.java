package com.allcure.spider.service;

import com.allcure.spider.model.DoctorInfo;
import com.allcure.spider.model.Treatment;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpiderDataService {

    private Gson gson = new Gson();

    /**
     * json 转Excel
     */
    public void jsonToExcel() {
        System.out.println("开始导出excel");
        exportByDept("f:/doctors");
        System.out.println("导出完成");


    }

    /**
     * 根据科室导出数据
     * @param savePath
     */
    private void exportByDept(String savePath) {
        //获取科室, 根据路径获取文件夹，此为科室名称
        File file = new File(SpiderService.savePath);
        //按科室读取文件
        for(File item : file.listFiles()) {
            if (item.isDirectory()) {
                String excelFileName = savePath + "/" + item.getName() + ".xls";
                if (new File(excelFileName).exists()) {
                    continue;
                }
                List<File> deptFiles = new ArrayList<>();
                getJsonFile(item.getAbsolutePath(), deptFiles);
                List<DoctorInfo> doctors = getDoctorInfos(deptFiles);
                exportExcel(excelFileName, doctors, item.getName());
            }
        }
    }

    /**
     *  将doctor 数据列表 导出excel
     * @param fileName
     * @param doctors
     */
    private void exportExcel(String fileName, List<DoctorInfo> doctors, String deptType) {
        Workbook wb = new HSSFWorkbook();

        //循环添加sheet
        Sheet sheet = wb.createSheet(deptType);
        int rowIndex = 0;

        String[] tiles = {
                "序号", "专科", "科室", "医生姓名",  "职称", "擅长", "头像URL", "执业经历", "个人网站",
                "临床经验"
        };
        //添加标题行
        Row row = sheet.createRow(rowIndex++);
        for (int x=0; x<tiles.length; x++) {
            Cell cell = row.createCell(x);
            cell.setCellValue(tiles[x]);
        }

        for (DoctorInfo item : doctors) {
            Row rowT = sheet.createRow(rowIndex ++);
            rowT.createCell(0).setCellValue(rowIndex - 1);
            rowT.createCell(1).setCellValue(item.getDeptType());
            rowT.createCell(2).setCellValue(item.getDeptName());
            rowT.createCell(3).setCellValue(item.getDoctorName());
            rowT.createCell(4).setCellValue(item.getTitle());
            rowT.createCell(5).setCellValue(item.getGoodAt());
            rowT.createCell(6).setCellValue(item.getProfilePicUrl());
            rowT.createCell(7).setCellValue(item.getResume());
            rowT.createCell(8).setCellValue(item.getPersonalWebUrl());

            StringBuilder sb = new StringBuilder();
            if (item.getTreatments() != null && item.getTreatments().size() > 0) {
                for (Treatment t : item.getTreatments()) {
                    sb.append(t.getDisease() + ":" + t.getCnt()).append("; ");
                }
                sb.substring(0, sb.length() - 2);
            }
            rowT.createCell(9).setCellValue(sb.toString());
        }

        try(OutputStream fileOut = new FileOutputStream(fileName)) {
            ((HSSFWorkbook) wb).write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 将json数据转成对象 {@link DoctorInfo}
     * @return
     */
    public List<DoctorInfo> getDoctorInfos(List<File> files) {

        //TODO 将json数据转成对象
        //读取所有的json文件

        System.out.println("文件数：" + files.size());
        //转成DoctorInfo对象
        List<DoctorInfo> rsList = new ArrayList<>();
        for (File file : files) {
            //读取json文件的内容
            String jsonStr = null;
            try {
                jsonStr = readFileContent(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //存入List中
            try {
                DoctorInfo info = gson.fromJson(jsonStr, DoctorInfo.class);
                rsList.add(info);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(file.getAbsolutePath());
            }
        }
        return rsList;
    }

    /**
     * 读取文本内容
     * @param file
     * @return
     * @throws IOException
     */
    private String readFileContent(File file) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String tem = null;
        while((tem = br.readLine()) != null) {
            sb.append(tem);
        }
        reader.close();
        br.close();
        return sb.toString();
    }

    public List<File> getJsonFile() {
        String savePath = SpiderService.savePath;

        List<File> files = new ArrayList<>();
        getJsonFile(savePath, files);

//        for (File item : files) {
//            System.out.println(item.getPath());
//        }

        return files;
    }

    private void getJsonFile(String savePath, List<File> files) {

        File file = new File(savePath);
        if (file.isDirectory()) {
            File[] filesList = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (new File(dir.getPath() + "/" + name).isDirectory()) {
                        return true;
                    } else if (name.endsWith(".json")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            for (File item : filesList) {
                if (item.isFile()) {
                    files.add(item);
                } else {
                    getJsonFile(item.getPath(), files);
                }
            }
        } else {
            files.add(file);
        }

    }


}
