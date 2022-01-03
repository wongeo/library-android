package com.feng.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库字段注解
 * 
 * @author WangJing
 * 
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	/**
	 * 是否是主键
	 * 
	 * @return
	 */
	boolean key() default false;

	/**
	 * 字段名字
	 * 
	 * @return
	 */
	String name();

	/**
	 * 字段类型
	 * 
	 * @return
	 */
	DataType type();

	/**
	 * 字段默认值
	 * 
	 * @return
	 */
	String dvalue() default "";

}
