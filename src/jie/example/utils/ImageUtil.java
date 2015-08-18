package jie.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import jie.example.manager.BoutiqueApp;
import android.graphics.Bitmap;

public class ImageUtil {

	private static void saveBitmapToImageFile(File dir, String fileName,
			Bitmap bitmap) throws Exception {
		File file = new File(dir, fileName.trim());
		FileOutputStream fos = new FileOutputStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] data = baos.toByteArray();
		if (data != null) {
			fos.write(data);
		}

		baos.close();
		fos.close();
	}

	public static void saveBitmapToImageFile(String fileName, Bitmap bitmap)
			throws Exception {
		saveBitmapToImageFile(BoutiqueApp.getAppFolder(), fileName, bitmap);
	}

	public static void saveBitmapToFile(String folder, String fileName,
			Bitmap bitmap) throws Exception {
		File dir = new File(BoutiqueApp.getAppFolder(), folder.trim());
		if (!dir.exists()) {
			dir.mkdir();
		}
		saveBitmapToImageFile(dir, fileName, bitmap);
	}

}
