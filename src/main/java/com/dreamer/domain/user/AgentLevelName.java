package com.dreamer.domain.user;

public enum AgentLevelName {

    独立核算,联盟单位,分公司,金董,董事,大区;

    public static boolean contains(String name){
        for(AgentLevelName s : values()){
            if(s.toString().equals(name)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(AgentLevelName.contains("分2公司"));
    }
}
