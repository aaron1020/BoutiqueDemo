package jie.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;

import jie.example.manager.BoutiqueApp;

public class FileUtil {

	public static void saveInfo(File dir, String fileName, byte[] data)
			throws Exception {
		OutputStream out = new FileOutputStream(new File(dir, fileName.trim()));
		out.write(data);
		out.close();
	}

	public static void saveInfo(File dir, String fileName, String info)
			throws Exception {
		saveInfo(dir, fileName, info.getBytes());
	}

	public static void saveInfoToSDCard(String fileName, byte[] data)
			throws Exception {
		saveInfo(BoutiqueApp.getAppFolder(), fileName, data);
	}

	/**
	 * 把信息保存在指定的文件夹里。指定文件夹路径：/SDCard/应用的英文名/指定文件夹的名称
	 * 
	 * @param folder
	 *            保存在哪个文件夹
	 * @param fileName
	 *            要保存的文件名
	 * @param info
	 *            要保存的信息
	 */
	public static void saveInfoToSDCard(String folder, String fileName,
			byte[] data) throws Exception {
		File dir = new File(BoutiqueApp.getAppFolder(), folder.trim());
		if (!dir.exists()) {
			dir.mkdir();
		}
		saveInfo(dir, fileName, data);
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

	/**
	 * 以比特流的形式读取输入流数据
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048 * 2];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

	public static void inputStreamToFile(File file, InputStream inputStream)
			throws Exception {
		OutputStream outStream = new FileOutputStream(file);
		int len = 0;
		byte[] buffer = new byte[1024 * 4];
		while ((len = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inputStream.close();
	}

	/**
	 * 把输入流数据以文件的形式存放
	 */
	public static void inputStreamToFile(String fileName,
			InputStream inputStream) throws Exception {
		File file = new File(BoutiqueApp.getAppFolder(), fileName.trim());
		inputStreamToFile(file, inputStream);
	}

	/**
	 * 把输入流的数据以文件的形式存放
	 */
	public static void inputStreamToFile(String folder, String fileName,
			InputStream inputStream) throws Exception {
		File dir = new File(BoutiqueApp.getAppFolder(), folder.trim());
		if (!dir.exists()) {
			dir.mkdir();
		}
		inputStreamToFile(new File(dir, fileName.trim()), inputStream);
	}

	/**
	 * 从输入流中读取第一行数据(以"\r\n"为分隔符)
	 */
	public static String readLine(PushbackInputStream in) throws IOException {
		char buf[] = new char[128];
		int room = buf.length;
		int offset = 0;
		int c;
		loop: while (true) {
			switch (c = in.read()) {
			case -1:
			case '\n':
				break loop;
			case '\r':
				int c2 = in.read();
				if ((c2 != '\n') && (c2 != -1))
					in.unread(c2);
				break loop;
			default:
				if (--room < 0) {
					char[] lineBuffer = buf;
					buf = new char[offset + 128];
					room = buf.length - offset - 1;
					System.arraycopy(lineBuffer, 0, buf, 0, offset);

				}
				buf[offset++] = (char) c;
				break;
			}
		}
		if ((c == -1) && (offset == 0))
			return null;
		return String.copyValueOf(buf, 0, offset);
	}

}
