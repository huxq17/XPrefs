package com.huxq17.xprefs.example.interfaces;

import com.huxq17.xprefs.annotations.XSPFile;

/**
 * Created by huxq17 on 2016/6/24.
 */
@XSPFile(fileName = "IStudent")
public interface IStudent {
    /**
     * 存储字段 name
     *
     * @param name value
     */
    void name(String name);

    /**
     * 读取字段 name
     *
     * @return
     */
    String name();

    void score(int score);

    int score();

    void sex(String sex);

    String sex();
}
