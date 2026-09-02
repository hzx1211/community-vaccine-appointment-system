package com.vaccine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要指定角色才能访问的接口。
 *
 * <p>权限判断必须在服务端完成；前端菜单和路由仅用于改善体验，不能作为安全边界。</p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    String[] value();
}
