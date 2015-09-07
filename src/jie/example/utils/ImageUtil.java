package jie.example.utils;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtil {

	private static byte[] bitmapToByteArray(Bitmap bitmap) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] data = baos.toByteArray();
		baos.close();
		return data;
	}

	/**
	 * 把图片保存在默认的的文件夹里。默认文件夹路径：/SDCard/应用的应用名
	 * 
	 * @param fileName
	 * @param info
	 */
	public static void saveBitmapToImageFile(String fileName, Bitmap bitmap)
			throws Exception {
		FileUtil.saveInfoToSDCard(fileName, bitmapToByteArray(bitmap));
	}

	/**
	 * 把图片保存在指定的文件夹里。指定文件夹路径：/SDCard/应用的应用名/指定文件夹的名称
	 * 
	 * @param folder
	 *            保存在哪个文件夹
	 * @param fileName
	 *            要保存的文件名
	 * @param bitmap
	 *            要保存的图片
	 */
	public static void saveBitmapToFile(String folder, String fileName,
			Bitmap bitmap) throws Exception {
		FileUtil.saveInfoToSDCard(folder, fileName, bitmapToByteArray(bitmap));
	}

	/**
	 * Bitmap--> Drawable
	 */
	public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
		BitmapDrawable drawable = null;
		if (bitmap != null) {
			drawable = new BitmapDrawable(context.getResources(), bitmap);
		}
		return drawable;
	}

}
