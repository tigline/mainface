/**
 * 
 */
package com.tcl.roselauncher.ui.mainface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @Project MainFace	
 * @author houxb
 * @Date 2015-10-23
 */
public class FontUtil {
	
	static int cIndex = 0;
	static final float textSize = 40;
	static int R = 255;
	static int G = 255;
	static int B = 255;
	public static Bitmap generateWTF(String[] str, int width, int height) {
		
		Paint paint = new Paint();
		paint.setARGB(255, R, G, B);
		paint.setTextSize(textSize);
		paint.setTypeface(null);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		Bitmap bmTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bmTemp);
		for (int i = 0; i < str.length; i++) {
			canvasTemp.drawText(str[i], 0, textSize*i+(i-1)*5, paint);
		}
		return bmTemp;
	}
	static String[] content = {
		"讯飞语音"
	};
	
	public static String[] getContent(int length, String[] content) {
		String[] result = new String[length + 1];
		for (int i = 0; i < length; i++) {
			result[i] = content[i];
		}
		return result;
	}
	
	public static void updateRGB() {
		R = (int) (255*Math.random());
		G = (int) (255*Math.random());
		B = (int) (255*Math.random());
	}
}
