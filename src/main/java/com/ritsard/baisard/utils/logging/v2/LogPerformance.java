/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.logging.v2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface LogPerformance {
    public long threshold() default 300L;

    public String description() default "";

    public String category() default "";

    public boolean logParams() default false;

    public boolean logResult() default false;
}

