package com.huxq17.xprefs.example;

import android.content.Context;

import com.huxq17.xprefs.annotations.XSPFile;

/**
 * Created by huxq17 on 2016/6/23.
 */
@XSPFile(fileName = "IEmployee", fileMode = Context.MODE_PRIVATE)
public interface IEmployee {
    void setName(String name);

    String getName();

    void setAge(int age);

    int getAge();

    void setSalary(float salary);

    float getSalary();
}
