package com.hjsjsu.annotation;

import com.hjsjsu.enums.Relationship;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface FieldCondition {
    String fieldName();          // 字段名称
    Relationship relationship(); // 值的关系，可以是等于、大于等于、小于等于、不等于
    String value();              // 期望的值
    String[] notEmptyFields();   // 不允许为空的字段名数组
}


