package org.example.twainprint.entity;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;


public class TwainMachineName {

    //twain设备索引
    private Integer nameIndex;
    //twain设备名
    private String name;
    //是否调用系统UI
    private Boolean systemUI;
    //是否高扫自动进纸
    private Boolean autoDocumentFeeder;
    //扫描颜色
    private String color;
    //扫描dpi
    private Integer dpi;
    //是否双面扫描
    private Boolean doubleSide;
    //是否去除白页
    private Boolean removeBlankSide;
    //自动歪斜校正
    private Boolean maticdskem;
    //自动边缘检测
    private Boolean maticborderdetection;
    //格式
    private static int xhr;

    public static int getXhr() {
        return xhr;
    }

    public void setXhr(int xhr) {
        this.xhr = xhr;
    }

    public Integer getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(Integer nameIndex) {
        this.nameIndex = nameIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSystemUI() {
        return systemUI;
    }

    public void setSystemUI(Boolean systemUI) {
        this.systemUI = systemUI;
    }

    public Boolean getAutoDocumentFeeder() {
        return autoDocumentFeeder;
    }

    public void setAutoDocumentFeeder(Boolean autoDocumentFeeder) {
        this.autoDocumentFeeder = autoDocumentFeeder;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getDpi() {
        return dpi;
    }

    public void setDpi(Integer dpi) {
        this.dpi = dpi;
    }

    public Boolean getDoubleSide() {
        return doubleSide;
    }

    public void setDoubleSide(Boolean doubleSide) {
        this.doubleSide = doubleSide;
    }

    public Boolean getRemoveBlankSide() {
        return removeBlankSide;
    }

    public void setRemoveBlankSide(Boolean removeBlankSide) {
        this.removeBlankSide = removeBlankSide;
    }

    public Boolean getMaticdskem() {
        return maticdskem;
    }

    public void setMaticdskem(Boolean maticdskem) {
        this.maticdskem = maticdskem;
    }

    public Boolean getMaticborderdetection() {
        return maticborderdetection;
    }

    public void setMaticborderdetection(Boolean maticborderdetection) {
        this.maticborderdetection = maticborderdetection;
    }
}
