package com.hjsjsu.aop;

import com.hjsjsu.annotation.FieldCondition;
import com.hjsjsu.annotation.FieldValueCheck;
import com.hjsjsu.enums.Relationship;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;


@Aspect
@Component
public class FieldValueCheckAspect {
//    @Pointcut("@annotation(com.mashibing.customvalidation.annotation.FieldValueCheck)")
//    public void pointCut() {
//    }

    @Around("@annotation(fieldValueCheck) && args(type)")
    public <T> Object fieldValueCheckAdvice(ProceedingJoinPoint joinPoint, FieldValueCheck fieldValueCheck, T type) throws Throwable {
        Object target = joinPoint.getTarget();

        for (FieldCondition fieldCondition : fieldValueCheck.value()) {
            String fieldName = fieldCondition.fieldName();
            String value = fieldCondition.value();
            Relationship relationship = fieldCondition.relationship();
            String[] notEmptyFields = fieldCondition.notEmptyFields();
            Object[] args = joinPoint.getArgs();

            for (Object arg : args) {
                Class<?> aClass = arg.getClass();
                Field declaredField = aClass.getDeclaredField(fieldName);
                declaredField.setAccessible(true);
                T t = (T) arg;
                Object o = declaredField.get(t);

                if (isSatisfied(relationship, o, value)) {
//                    throw new IllegalArgumentException("Field " + fieldName + " does not satisfy the condition.");

                    for (String notEmptyFieldName : notEmptyFields) {
                        Field notEmptyField = aClass.getDeclaredField(notEmptyFieldName);
                        notEmptyField.setAccessible(true);
                        Object notEmptyFieldValue = notEmptyField.get(t);
                        if (notEmptyFieldValue == null || notEmptyFieldValue.toString().isEmpty()) {
                            throw new IllegalArgumentException("Field " + notEmptyFieldName + " cannot be empty.");
                        }
                    }

                }

            }


        }
        return joinPoint.proceed(); // 继续执行目标方法
    }

    private boolean isSatisfied(Relationship relationship, Object fieldValue, String value) {
        if (fieldValue == null) {
            return false;
        }

        if (relationship == Relationship.EQUAL) {
            return fieldValue.toString().equals(value);
        } else if (relationship == Relationship.NOT_EQUAL) {
            return !fieldValue.toString().equals(value);
        } else if (relationship == Relationship.GREATER_EQUAL) {
            try {
                int fieldValueInt = Integer.parseInt(fieldValue.toString());
                int valueInt = Integer.parseInt(value);
                return fieldValueInt >= valueInt;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (relationship == Relationship.LESS_EQUAL) {
            try {
                int fieldValueInt = Integer.parseInt(fieldValue.toString());
                int valueInt = Integer.parseInt(value);
                return fieldValueInt <= valueInt;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }

}
