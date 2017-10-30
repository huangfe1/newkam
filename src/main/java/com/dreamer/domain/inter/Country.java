package com.dreamer.domain.inter;

import com.dreamer.domain.user.Agent;

public class Country {

    private Integer id;

    private String name;

    private Agent agent;//接收返利的人

    private Boolean open;//是否打开


    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
