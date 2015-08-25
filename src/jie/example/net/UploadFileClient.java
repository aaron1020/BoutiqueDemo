package jie.example.net;

import java.io.File;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import jie.example.constant.Constant;
import jie.example.utils.FileUtil;

public class UploadFileClient {

	public static void uploadFile(File file) throws Exception {
		long startTime = System.currentTimeMillis();

		Socket socket = new Socket(Constant.WEB_IP, 7879);
		OutputStream outStream = socket.getOutputStream();

		String fileName = file.getName();
		long filelength = file.length();
		System.out.println("filelength = " + filelength);
		String head = "Content-Length=" + filelength + ";filename=" + fileName
				+ ";sourceid=\r\n";// head为自定义协议，回车换行为方便我们提取第一行数据而自行设定的。
		outStream.write(head.getBytes()); // 向服务器发送协议消息

		PushbackInputStream inStream = new PushbackInputStream(
				socket.getInputStream());// 发送协议后，获取从服务器返回的信息
		String response = FileUtil.readLine(inStream);
		System.out.println("reponse:" + response);

		String[] items = response.split(";");
		long position = Integer.valueOf(items[1].substring(items[1]
				.indexOf("=") + 1));
		RandomAccessFile raf = new RandomAccessFile(file, "r");// 用随机文件访问类操作文件
		raf.seek(position);// position表示从文件的什么位置开始上传，也表示已经上传了多少个字节

		// 开始向服务器写出数据
		byte[] buffer = new byte[1024 * 4];
		int len = -1;
		int total = 0;
		while ((len = raf.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
			total += len;
		}

		if (total == filelength - position) {
			long endTime = System.currentTimeMillis();
			System.out.println("upload successfully, take time: "
					+ (endTime - startTime) / 1000 + "s");
		} else {
			System.out.println("upload error!!");
		}

		raf.close();
		inStream.close();
		outStream.close();
		socket.close();
	}

}
