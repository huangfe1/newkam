package com.dreamer.domain.user;

public enum AgentLevelName {
    联合股东,官方,眼博士,眼卫士,眼护士,VIP顾客;

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
