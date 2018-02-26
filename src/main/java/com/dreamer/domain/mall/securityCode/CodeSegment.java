package com.dreamer.domain.mall.securityCode;

import java.util.Date;

public class CodeSegment {

    private Integer id;

    private String prefix;//前缀

    private Integer startCode;//小码开始

    private Integer endCode;//小码结束

    private Integer startBox;//大码开始

    private Integer endBox;//大码结束

    private Integer codeNumber;//小码数量

    private Integer boxNumber;//大码数量

    private String info;//备注

    private Date date;//时间




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(Integer codeNumber) {
        this.codeNumber = codeNumber;
    }

    public Integer getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(Integer boxNumber) {
        this.boxNumber = boxNumber;
    }

    public String getInfo() {
        return info;
    }

    public Integer getStartCode() {
        return startCode;
    }

    public void setStartCode(Integer startCode) {
        this.startCode = startCode;
    }

    public Integer getEndCode() {
        return endCode;
    }

    public void setEndCode(Integer endCode) {
        this.endCode = endCode;
    }

    public Integer getStartBox() {
        return startBox;
    }

    public void setStartBox(Integer startBox) {
        this.startBox = startBox;
    }

    public Integer getEndBox() {
        return endBox;
    }

    public void setEndBox(Integer endBox) {
        this.endBox = endBox;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
