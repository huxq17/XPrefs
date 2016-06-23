package com.huxq17.xprefs.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by huxq17 on 2016/6/21.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface XIgnore {
}
