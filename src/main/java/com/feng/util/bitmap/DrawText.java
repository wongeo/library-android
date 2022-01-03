package com.feng.util.bitmap;

/**
 * 文字排版描述关系实体类
 * 
 * @author WangJing
 * 
 */
public class DrawText {
	/** X轴坐标 */
	public float x;
	/** 字符 */
	public char ch;

	/**
	 * 构造一个绘制信息的实例
	 * 
	 * @param x
	 * @param ch
	 */
	public DrawText(float x, char ch) {
		this.x = x;
		this.ch = ch;
	}
}
