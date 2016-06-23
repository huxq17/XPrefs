package com.huxq17.xprefs.example;

import com.huxq17.xprefs.annotations.XIgnore;

/**
 * Created by 2144 on 2016/6/23.
 */
public class UserBean {
    /**
     * final 修饰的字段不会被存储
     */
    private final String InvalidField="InvalidField";
    /**
     * 添加了XIgnore注解的字段会被忽视，也不会被存储
     */
    @XIgnore
    private String IgnoreField="IgnoreField";
    //目前支持以下几种类型的数据存储
    private String name;
    private float money;
    private int age;
    private boolean isVIP;
    private long funs;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public void setVIP(boolean VIP) {
        isVIP = VIP;
    }

    public long getFuns() {
        return funs;
    }

    public void setFuns(long funs) {
        this.funs = funs;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "IgnoreField='" + IgnoreField + '\'' +
                ", name='" + name + '\'' +
                ", money=" + money +
                ", age=" + age +
                ", isVIP=" + isVIP +
                ", funs=" + funs +
                ", InvalidField='" + InvalidField + '\'' +
                '}';
    }
}
