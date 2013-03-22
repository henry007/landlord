package com.hurray.landlord.entity;

public enum ActorInfo {
    
    LEVEL("级别 :"), GENDER("性别 :"),RECORD("战绩 :"),GOLD("金币 :")
    ,INGOT("元宝 :"),INTEGRAL("积分 :"),STATE("状态 :"),TITLE("头衔 :")
    , SHENG("胜"),BAI("负")
    ;
    
    private String name;
    
    private ActorInfo(String name){
        
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public  void setName(String name) {
        this.name = name;
    }

}
