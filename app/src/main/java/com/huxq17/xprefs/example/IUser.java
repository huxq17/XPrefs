package com.huxq17.xprefs.example;

import android.content.Context;

import com.huxq17.xprefs.annotations.XGet;
import com.huxq17.xprefs.annotations.XSPFile;
import com.huxq17.xprefs.annotations.XSet;

/**
 * Created by huxq17 on 2016/6/23.
 */
@XSPFile(fileName = "IUser", fileMode = Context.MODE_PRIVATE)
public interface IUser {
    @XSet(key = "name", fileName = "IUser", fileMode = Context.MODE_PRIVATE)
    void setName(String name);

    @XGet(key = "name")
    String getName();

    @XSet(key = "age")
    void setAge(int age);

    @XGet(key = "age")
    int getAge();

    @XSet(key = "funs")
    void setFuns(int funs);

    @XGet(key = "funs")
    int getFuns();

    @XSet(key = "vip")
    void setVip(boolean vip);

    @XGet(key = "vip")
    boolean getVip();

    @XSet(key = "money")
    void setMoney(float money);

    @XGet(key = "money")
    float getMoney();
}
