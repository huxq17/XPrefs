package com.huxq17.xprefs.annotations;

import android.content.Context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huxq17 on 2016/6/22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XGet {
    String key() default "";

    String fileName() default "";

    int fileMode() default Context.MODE_PRIVATE;
}
