package jie.example.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import jie.example.boutique.R;
import jie.example.manager.BoutiqueApp;
import jie.example.utils.LogUtil;

/**
 * 单线程下载器
 */
public class SingleThreadDownload implements Runnable {
	public static final String TAG = "Download";
	public static final int TIME_REFRESH_VIEW = 1500;
	private Context mContext;
	private NotificationManager mNotiManager;
	private NotificationCompat.Builder mNotiBuilder;
	private Notification mNotification;
	private RemoteViews mNotiViews;
	private Timer mTimer;
	private String mDownloadPath;
	private String mFileName;
	private int mNotiId = 0;// 通知栏Id，具有相同Id的通知是同一个通知
	private int mDownloadSize;// 需要下载的文件的大小
	private int mDownloaded = 0;// 已经下载的文件的大小
	private int mDownloadedTime = 0;// 已经下载的了多少时间
	private int mDownloadedBefore = 0;// 用于计算每一秒钟的下载速率

	public SingleThreadDownload(Context context, String downloadPath) {
		mContext = context;
		mNotiManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		this.mDownloadPath = downloadPath.trim();
		mFileName = getFileNamefromPath(downloadPath);
		initDownload(context);
	}

	private void initDownload(Context context) {
		mNotiBuilder = new NotificationCompat.Builder(context);
		mNotiBuilder.setSmallIcon(R.drawable.download_anim)
				.setContentTitle(context.getString(R.string.download_note))
				.setTicker(context.getString(R.string.downloading) + mFileName);

		mNotiViews = new RemoteViews(context.getPackageName(),
				R.layout.download_notification);
		mNotiViews.setTextViewText(R.id.noti_tv_name,
				context.getString(R.string.downloading) + mFileName);
		mNotiViews.setTextViewText(R.id.noti_tv_rate, "0%");

		mNotiBuilder.setContent(mNotiViews);
		mNotiBuilder.setAutoCancel(false);
	}

	private String getFileNamefromPath(String downloadPath) {
		return downloadPath.substring(downloadPath.lastIndexOf('/') + 1);
	}

	@Override
	public void run() {
		try {
			URL url = new URL(mDownloadPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(1000 * 5);

			mNotification = mNotiBuilder.build();
			mNotiId = (int) new Date().getTime();
			mNotiManager.notify(mNotiId, mNotification);

			File dir = new File(BoutiqueApp.getAppFolder(), "Download/");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, mFileName);// 生成本地文件，用于保存需要下载的文件
			if (file.exists()) {// 如果文件重名，就修改文件名
				file = new File(dir, mNotiId + "_" + mFileName);
			}

			OutputStream outStream = new FileOutputStream(file);
			InputStream inputStream = conn.getInputStream();

			int len = 0;
			mDownloadSize = conn.getContentLength();
			byte[] buffer = new byte[1024 * 4];
			mTimer = new Timer();
			mTimer.schedule(new DownloadTask(), TIME_REFRESH_VIEW,
					TIME_REFRESH_VIEW);
			while ((len = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
				mDownloaded += len;
				if (mDownloaded >= mDownloadSize) {// 下载完成
					mTimer.cancel();
					String ticker = mFileName
							+ mContext.getString(R.string.download_finish);

					mNotification.icon = R.drawable.download_anim_i_1;
					mNotification.tickerText = ticker;
					mNotiViews.setTextViewText(R.id.noti_tv_name, ticker);
					mNotiViews.setTextViewText(R.id.noti_tv_rate,
							mContext.getString(R.string.downloaded_install));

					Intent intent = new Intent();// 自动安装
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file),
							"application/vnd.android.package-archive");
					mContext.startActivity(intent);

					PendingIntent pendingIntent = PendingIntent.getActivity(
							mContext, 0, intent,
							PendingIntent.FLAG_CANCEL_CURRENT);// 点击通知栏安装
					mNotification.contentIntent = pendingIntent;
					mNotiManager.notify(mNotiId, mNotification);
				}
			}

			outStream.close();
			inputStream.close();
			LogUtil.i(TAG, mNotiId + "::download finished!!");
		} catch (Exception e) {
			LogUtil.e(TAG, "download exception::" + e.toString());
		}
	}

	public void stopDownloadTask() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		mNotiManager.cancelAll();
	}

	private final class DownloadTask extends TimerTask {
		@Override
		public void run() {
			try {
				mDownloadedTime++;
				LogUtil.i(TAG, "DownloadTask::run()::" + mDownloadedTime);
				// int downloadRate = (int) 1.0f * mDownloaded / mDownloadedTime
				// / TIME_REFRESH_VIEW;// 计算平均下载速率，单位：KB/s

				// 计算每一秒钟的下载速率，单位：KB/s，(mDownloaded - mDownloadedBefore)为增量
				int downloadRate = (mDownloaded - mDownloadedBefore)
						/ TIME_REFRESH_VIEW;
				mNotiViews.setTextViewText(R.id.noti_tv_rate,
						(int) (100.0f * mDownloaded / mDownloadSize) + "% ,"
								+ downloadRate + "KB/s");
				mNotiManager.notify(mNotiId, mNotification);
				mDownloadedBefore = mDownloaded;
			} catch (Exception e) {
				LogUtil.e(TAG, "DownloadTask::run()::" + e.toString());
			}
		}
	}

}
