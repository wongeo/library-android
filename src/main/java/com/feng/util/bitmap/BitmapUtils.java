package com.feng.util.bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

/**
 * Bitmap工具类
 * 
 * @author WangJing
 * 
 */
public class BitmapUtils {

	/**
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 获取圆形位图
	 * 
	 * @param source
	 * @return
	 */
	public static Bitmap createOvalBitmap(Bitmap source) {
		int size = Math.min(source.getWidth(), source.getHeight());
		float radius = size / 2f;

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(android.graphics.Color.WHITE);
		Bitmap clipped = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(clipped);
		canvas.drawCircle(radius, radius, radius, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		return clipped;
	}

	/**
	 * 获取有圆环边框的圆形位图
	 * 
	 * @param source
	 * @param d
	 * @return
	 */
	public static Bitmap createOvalBitmapWithCircle(Bitmap source, int d) {
		int size = Math.min(source.getWidth(), source.getHeight());
		float radius = (size + d * 2) / 2f;
		Bitmap bitmap = Bitmap.createBitmap(size + d * 2, size + d * 2, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		canvas.drawCircle(radius, radius, radius, paint);
		canvas.drawBitmap(source, d, d, null);
		return bitmap;
	}

	/**
	 * 将位图 转换成字节数组
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] toByteArray(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		bitmap.recycle();
		return stream.toByteArray();
	}

	/**
	 * 获取圆角位图的方法
	 * 
	 * @param bitmap
	 *            需要转化成圆角的位图
	 * @param pixels
	 *            圆角的度数，数值越大，圆角越大
	 * @return 处理后的圆角位图
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(android.graphics.Color.WHITE);

		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		canvas.drawARGB(0, 0, 0, 0);

		canvas.drawRoundRect(rectF, pixels, pixels, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 获取多行
	 * 
	 * @param text
	 * @param width
	 * @param paint
	 * @return
	 */
	public static Bitmap makeMulTextBitmap(String text, int width, TextPaint paint) {
		int lineHeight = (int) (paint.getTextSize() + 20);
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();

		int x = 0;
		List<DrawText> lineDrawText = new ArrayList<DrawText>();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			float cw = paint.measureText(c + "");
			DrawText t = new DrawText(x, c);
			lineDrawText.add(t);
			x += cw;

			float surplus = width - x;
			if (surplus < paint.getTextSize()) {

				Bitmap bm = makeLineTextBitmap(lineDrawText, lineHeight, width, paint, surplus);
				bitmaps.add(bm);
				x = 0;
				lineDrawText.clear();
			}
		}

		if (lineDrawText.size() != 0) {
			Bitmap bm = makeLineTextBitmap(lineDrawText, lineHeight, width, paint, 0);
			bitmaps.add(bm);
			x = 0;
			lineDrawText.clear();
		}

		Bitmap bitmap = Bitmap.createBitmap(width, bitmaps.size() * lineHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		int top = 0;
		for (Bitmap bitmap2 : bitmaps) {
			canvas.drawBitmap(bitmap2, 0, top, null);
			bitmap2.recycle();
			top += lineHeight;
		}
		return bitmap;
	}

	/**
	 * 获取一行
	 * 
	 * @param lineText
	 * @param height
	 * @param width
	 * @param paint
	 * @param surplus
	 * @return
	 */
	private static Bitmap makeLineTextBitmap(List<DrawText> lineText, int height, int width, TextPaint paint, float surplus) {
		Rect rect = new Rect(0, 0, width, height);
		Bitmap bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		int baseline = rect.top + (rect.bottom - rect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

		float f = surplus / (lineText.size() - 1);

		for (int i = 0; i < lineText.size(); i++) {
			DrawText drawText = lineText.get(i);
			drawText.x += f * i;
			canvas.drawText(drawText.ch + "", drawText.x, baseline, paint);
		}
		return bm;
	}

	/**
	 * 底线
	 * 
	 * @param paint
	 * @param rect
	 * @return
	 */
	public static int getBaseLine(TextPaint paint, Rect rect) {
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		int baseline = rect.top + (rect.bottom - rect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		return baseline;
	}
}
