package jie.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import jie.example.manager.BoutiqueApp;

public class FileUtil {

	private static void saveInfo(File dir, String fileName, byte[] data)
			throws Exception {
		OutputStream out = new FileOutputStream(new File(dir, fileName.trim()));
		out.write(data);
		out.close();
	}

	public static void saveInfoToSDCard(String fileName, byte[] data)
			throws Exception {
		saveInfo(BoutiqueApp.getAppFolder(), fileName, data);
	}
	
	public static void saveInfoToSDCard(String folder, String fileName,
			byte[] data) throws Exception {
		File dir = new File(BoutiqueApp.getAppFolder(), folder.trim());
		if (!dir.exists()) {
			dir.mkdir();
		}
		saveInfo(dir, fileName, data);
	}
	
	private static void saveInfo(File dir, String fileName, String info)
			throws Exception {
		saveInfo(dir, fileName, info.getBytes());
	}

	/**
	 * 把信息保存在默认的的文件夹里。默认文件夹：/SDCard/应用的英文名
	 * 
	 * @param fileName
	 * @param info
	 */
	public static void saveInfoToSDCard(String fileName, String info)
			throws Exception {
		saveInfo(BoutiqueApp.getAppFolder(), fileName, info);
	}

	/**
	 * 把信息保存在指定的文件夹里。指定文件夹路径：/SDCard/应用的应用名/指定文件夹的名称
	 * 
	 * @param folder
	 *            保存在哪个文件夹
	 * @param fileName
	 *            要保存的文件名
	 * @param info
	 *            要保存的信息
	 */
	public static void saveInfoToSDCard(String folder, String fileName,
			String info) throws Exception {
		File dir = new File(BoutiqueApp.getAppFolder(), folder.trim());
		if (!dir.exists()) {
			dir.mkdir();
		}
		saveInfo(dir, fileName, info);
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

}
