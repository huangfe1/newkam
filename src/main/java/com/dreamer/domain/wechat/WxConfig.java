package com.dreamer.domain.wechat;

public class WxConfig {

    public final static String  RECHARGE_NOTICE_URL = "http://ht.kam365.com/kam/dmz/mobile/accounts/recharge.json";

    public final static String PMALL_NOTICE_URL = "http://ht.kam365.com/kam/dmz/pmall/shopcart/wxPay.json";

    private Integer id;

    private String name;

    private Boolean baseConfig;

    private Boolean payConfig;


    private Integer number;//最大消费值



    private String wxid;

    private String appid;

    private String secret;

    private String mchID;

    private String key;

    private String noticeUrl;

    private String spbill_create_ip;

    private Boolean open;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Boolean getBaseConfig() {
        return baseConfig;
    }

    public void setBaseConfig(Boolean baseConfig) {
        this.baseConfig = baseConfig;
    }

    public Boolean getPayConfig() {
        return payConfig;
    }

    public void setPayConfig(Boolean payConfig) {
        this.payConfig = payConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getMchID() {
        return mchID;
    }

    public void setMchID(String mchID) {
        this.mchID = mchID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid;
    }

    public void setNoticeUrl(String noticeUrl) {
        this.noticeUrl = noticeUrl;
    }






}
