package com.feng.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * 数据库名字
	 * 
	 * @return
	 */
	String name();

	/**
	 * 数据库版本
	 * 
	 * @return
	 */
	int version() default 1;
}
