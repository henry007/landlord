package com.hurray.landlord.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.InputStream;

public class ImageUtils {

	/**
	 * @param bmp
	 * @param scale 缩放比例
	 * @return 缩放后新的图片
	 */
	public static Bitmap zoom(Bitmap bmp, float scale) {
		int bmpWidth=bmp.getWidth();  
		int bmpHeight=bmp.getHeight();   
		    
		Matrix matrix=new Matrix();  
		matrix.postScale(scale, scale);  
 
		return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
	}
	
	public static Bitmap zoom(Bitmap bmp, int newWidth, int newHeight) {
		int bmpWidth=bmp.getWidth();  
		int bmpHeight=bmp.getHeight();  
		  
		float scaleWidth = ((float) newWidth) / bmpWidth; 
		float scaleHeight = ((float) newHeight) / bmpHeight; 
		    
		Matrix matrix=new Matrix();  
		matrix.postScale(scaleWidth, scaleHeight);  
 
		return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
	}
	
	/**
	 * @param is 图片流
	 * @param size 优化图片所占内存，数值越大占用内存越小
	 * @return
	 */
	public static Bitmap curtail(InputStream is, int size) {
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = size;
		return BitmapFactory.decodeStream(is, null, options);
	}
	
	public static Bitmap curtail2(byte[] data, int offset, int length, int size) {
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = size;
		return BitmapFactory.decodeByteArray(data, offset, length, options);
	}
	
	public static Bitmap curtail3(String path, int size) {
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = size;
		return BitmapFactory.decodeFile(path, options);
	}
	
	public static Bitmap curtail4(Resources res, int resId, int size) {
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = size;
		return BitmapFactory.decodeResource(res, resId, options);
	}
}
