package com.huxq17.xprefs.annotations;

import android.content.Context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 2144 on 2016/6/22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XSPFile {
    String fileName() default "";

    int fileMode() default Context.MODE_PRIVATE;
}
