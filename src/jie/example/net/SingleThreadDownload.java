package jie.example.net;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import jie.example.utils.FileUtil;
import jie.example.utils.LogUtil;

/**
 * 单线程下载器
 */
public class SingleThreadDownload implements Runnable {
	public static final String TAG = "Download";
	private String downloadPath;

	public SingleThreadDownload(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	private String getFileNamefromPath(String downloadPath) {
		return downloadPath.substring(downloadPath.lastIndexOf('/') + 1);
	}

	@Override
	public void run() {
		try {
			URL url = new URL(downloadPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(1000 * 5);

			InputStream inputStream = conn.getInputStream();
			FileUtil.inputStreamToFile(TAG, getFileNamefromPath(downloadPath),
					inputStream);

			LogUtil.i(TAG, "download finished!!");
		} catch (Exception e) {
			LogUtil.e(TAG, "download exception::" + e.toString());
		}
	}

}
