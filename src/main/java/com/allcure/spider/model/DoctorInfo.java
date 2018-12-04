package com.allcure.spider.model;

import java.util.List;

public class DoctorInfo {

    /**
     * 图像 base64
     */
    private String profilePicBase64;

    private String profilePicUrl;

    private String deptId;
    /**
     * 查找时的科室划分
     */
    private String deptType;
    private String doctorName;
    /**
     * 明细中的科室
     */
    private String deptName;

    private String title;

    private String goodAt;

    /**
     * 执业经历
     */
    private String resume;

    private String personalWebUrl;

    private int thankMailCnt;

    private int giftCnt;

    private List<Treatment> treatments;

    private String deptUrl;

    /**
     * 数据的版本，空、0 表示未修改过
     */
    private Integer dataVersion;

    public Integer getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(Integer dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getDeptUrl() {
        return deptUrl;
    }

    public void setDeptUrl(String deptUrl) {
        this.deptUrl = deptUrl;
    }

    public String getProfilePicBase64() {
        return profilePicBase64;
    }

    public void setProfilePicBase64(String profilePicBase64) {
        this.profilePicBase64 = profilePicBase64;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptType() {
        return deptType;
    }

    public void setDeptType(String deptType) {
        this.deptType = deptType;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGoodAt() {
        return goodAt;
    }

    public void setGoodAt(String goodAt) {
        this.goodAt = goodAt;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getPersonalWebUrl() {
        return personalWebUrl;
    }

    public void setPersonalWebUrl(String personalWebUrl) {
        this.personalWebUrl = personalWebUrl;
    }

    public int getThankMailCnt() {
        return thankMailCnt;
    }

    public void setThankMailCnt(int thankMailCnt) {
        this.thankMailCnt = thankMailCnt;
    }

    public int getGiftCnt() {
        return giftCnt;
    }

    public void setGiftCnt(int giftCnt) {
        this.giftCnt = giftCnt;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }

    @Override
    public String toString() {
        return "DoctorInfo{" +
                "profilePicBase64='" + profilePicBase64 + '\'' +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                ", deptId='" + deptId + '\'' +
                ", deptType='" + deptType + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", deptName='" + deptName + '\'' +
                ", title='" + title + '\'' +
                ", goodAt='" + goodAt + '\'' +
                ", resume='" + resume + '\'' +
                ", personalWebUrl='" + personalWebUrl + '\'' +
                ", thankMailCnt=" + thankMailCnt +
                ", giftCnt=" + giftCnt +
                ", treatments=" + treatments +
                ", deptUrl='" + deptUrl + '\'' +
                '}';
    }
}
