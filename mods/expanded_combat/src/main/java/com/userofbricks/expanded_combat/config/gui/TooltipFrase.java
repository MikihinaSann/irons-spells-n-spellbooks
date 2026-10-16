package com.userofbricks.expanded_combat.config.gui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Repeatable(TooltipFrases.class)
public @interface TooltipFrase {
   int line() default 0;

   String value();
}
