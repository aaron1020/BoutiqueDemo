package jie.example.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import jie.example.manager.BoutiqueApp;

public class FileUtil {

	private static void saveInfo(File dir, String fileName, String info)
			throws Exception {
		final File file = new File(dir, fileName.trim());
		OutputStream out = new FileOutputStream(file);
		out.write(info.getBytes());
		out.close();
	}

	public static void saveInfoToSDCard(String fileName, String info)
			throws Exception {
		saveInfo(BoutiqueApp.getAppFolder(), fileName, info);
	}

	public static void saveInfoToSDCard(String folder, String fileName,
			String info) throws Exception {
		File dir = new File(BoutiqueApp.getAppFolder(), folder.trim());
		if (!dir.exists()) {
			dir.mkdir();
		}
		saveInfo(dir, fileName, info);
	}

}
