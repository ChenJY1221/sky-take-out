package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 定义切入点
     * @return
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPoint() {

    }

    /**
     * 前置通知，再通知中进行公共字段的赋值
     */
    @Before("autoFillPoint()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段自动填充");
        //获取当前被拦截的方法上的数据库操作类型，获取autofill那个注解里面的value值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = annotation.value();

        //获取到当前被拦截方法的参数--实体参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        Object entity = args[0];

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的属性同故宫反射来赋值
        if (operationType == OperationType.INSERT) {
            //为四个字段赋值
            try {
                //获取对应的四个方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method getCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity, now);
                getCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (operationType == OperationType.UPDATE) {
            //为两个字段赋值
            try {
                //获取对应的四个方法
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}