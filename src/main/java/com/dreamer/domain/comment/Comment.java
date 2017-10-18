package com.dreamer.domain.comment;

import com.dreamer.domain.user.Agent;

import java.util.Date;

public class Comment {

    private Integer id;

    private Agent agent;//评论人

    private Integer gid;//产品id

    private String goodsName;

    private Integer type;//0为mall 1为pmall

    private Boolean canPublish;//是否可以展示

    private String body;//内容

    private Date updateTime;//评论时间

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getCanPublish() {
        return canPublish;
    }

    public void setCanPublish(Boolean canPublish) {
        this.canPublish = canPublish;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
