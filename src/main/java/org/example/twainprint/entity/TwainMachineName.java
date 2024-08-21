package org.example.twainprint.entity;

import lombok.Data;

@Data
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
}
