package jie.example.boutique;

import java.io.File;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import jie.example.adapter.TreeListViewAdapter;
import jie.example.constant.Constant;
import jie.example.manager.ActivityCollector;
import jie.example.manager.BoutiqueApp;
import jie.example.net.SingleThreadDownload;
import jie.example.utils.FileUtil;
import jie.example.utils.LogUtil;
import jie.example.utils.TimeUtil;
import jie.example.utils.ToastUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 树形ListView
 */
public class TreeListViewActivity extends BasicActivity {
	private static final String TAG = TreeListViewActivity.class
			.getSimpleName();
	private static final int UPDATE_SURFACE_TIME = 1000;
	private static final int MSG_SHOW_VIEWSTUB = 1001;
	private static final int MSG_UPDATE_UPLOADBAR = 1002;
	private static final int REQUEST_RECORD_VIDEO = 1003;
	private static final int TEXT_SHOW_TIME = 3;
	private ListView mListView;
	private TreeListViewAdapter mTreeAdapter;
	private List<String> mStringList;
	private TextView mTextView;
	private ViewStub mViewStub;
	private View mInflateView;
	private Button mInflateBtn;
	private ProgressBar mUploadProgress;
	private TextView mTextProgress;
	private Timer mUploadTimer;
	private SingleThreadDownload mThreadDownload;
	private int mTimeCounter = TEXT_SHOW_TIME;
	private long mFileLength = 0;// 要上传的文件的大小
	private int mUploadedTime = 0;// 已经上传的时间
	private int mUploadedTotal = 0;// 已经上传的文件大小
	private boolean isInflate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.tree_listview_aty);
		setContentView(R.layout.tree_listview_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		mStringList = new ArrayList<String>();
		mListView = (ListView) findViewById(R.id.tree_listview);
		mTreeAdapter = new TreeListViewAdapter(mStringList, this);
		mTextView = (TextView) findViewById(R.id.tl_text_vs);
		mViewStub = (ViewStub) findViewById(R.id.tl_vs);
		mUploadProgress = (ProgressBar) findViewById(R.id.uploadbar);
		mTextProgress = (TextView) findViewById(R.id.upload_text_indicator);
	}

	@Override
	public void loadingData() {
		mHandler.sendEmptyMessage(MSG_SHOW_VIEWSTUB);
		mListView.setAdapter(mTreeAdapter);
		mStringList.add("1111111111111");
		mStringList.add("2222222222222");
		mStringList.add("3333333333333");
		mStringList.add("4444444444444");
		mStringList.add("5555555555555");
		mStringList.add("6666666666666");
		mStringList.add("7777777777777");
		mStringList.add("8888888888888");
		mStringList.add("9999999999999");
		mStringList.add("10000000000000");
		mStringList.add("11000000000000");
		mTreeAdapter.notifyDataSetChanged();
	}

	public void setOnClick(View view) {
		switch (view.getId()) {
		case R.id.tl_btn_exit_app:
			ActivityCollector.finishAllActivity();
			break;
		case R.id.login_exit_btn:
			mTimeCounter = TEXT_SHOW_TIME;
			mTextView.setVisibility(View.VISIBLE);
			mViewStub.setVisibility(View.GONE);
			mHandler.sendEmptyMessage(MSG_SHOW_VIEWSTUB);
			break;
		case R.id.tl_btn_download:
			try {
				mThreadDownload = new SingleThreadDownload(this,
						"http://192.168.63.90:8080/NetForAndroid/WPSOffice.apk");
				new Thread(mThreadDownload).start();
			} catch (Exception e) {
				ToastUtil.showToast(R.string.download_fail);
			}
			break;
		case R.id.tl_btn_upload:
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				final File file = new File(BoutiqueApp.getAppFolder(),
						"Download/" + "AdobeReader.exe");
				if (file.exists()) {
					mFileLength = file.length();
					mUploadProgress.setMax((int) mFileLength);
					mTextProgress.setText(R.string.start_upload);

					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								uploadFile(file);
							} catch (Exception e) {
								LogUtil.e(TAG,
										"upload Exception::" + e.toString());
							}
						}
					}).start();
				} else {
					ToastUtil.showToast(R.string.upload_file_unexist);
				}
			}
			break;
		case R.id.tl_btn_record_video:
			startActivityForResult(new Intent(this, RecordVideoActivity.class),
					REQUEST_RECORD_VIDEO);
		case R.id.tl_btn_temp:
			startActivity(new Intent(this, ScreenColorActivity.class));
			break;
		default:
			break;
		}
	}

	private void uploadFile(File file) throws Exception {
		long startTime = System.currentTimeMillis();

		Socket socket = new Socket(Constant.SERVER_IP, 7880);
		OutputStream outStream = socket.getOutputStream();

		String fileName = file.getName();
		long filelength = file.length();
		LogUtil.i(TAG, "filelength = " + filelength);
		String head = "Content-Length=" + filelength + ";filename=" + fileName
				+ ";sourceid=\r\n";// head为自定义协议，回车换行是为方便我们提取第一行数据而自行设定。
		outStream.write(head.getBytes()); // 向服务器发送协议消息

		PushbackInputStream inStream = new PushbackInputStream(
				socket.getInputStream());// 发送协议后，获取从服务器返回的信息
		String response = FileUtil.readLine(inStream);
		LogUtil.i(TAG, "reponse:" + response);

		String[] items = response.split(";");
		long position = Integer.valueOf(items[1].substring(items[1]
				.indexOf("=") + 1));
		RandomAccessFile raf = new RandomAccessFile(file, "r");// 用随机文件访问类操作文件
		raf.seek(position);// position表示从文件的什么位置开始上传，也表示已经上传了多少个字节

		// 开始向服务器写出数据
		byte[] buffer = new byte[1024 * 4];
		int len = -1;
		mUploadedTotal = (int) position;
		mUploadTimer = new Timer();
		mUploadTimer.schedule(new UploadTask(), UPDATE_SURFACE_TIME,
				UPDATE_SURFACE_TIME);// 为了防止频繁地发消息更新主界面，我们设定每一秒钟更新一次界面
		while ((len = raf.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
			mUploadedTotal += len;
			if (mUploadedTotal >= mFileLength) {// "total >= fileLength"表示已经完全上传，直接在循环中发消息更新界面
				stopUploadTask();
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_UPDATE_UPLOADBAR;
				msg.arg1 = mUploadedTotal;
				mHandler.sendMessage(msg);
			}
		}

		if (mUploadedTotal == filelength) {
			long endTime = System.currentTimeMillis();
			LogUtil.i(TAG, "upload successfully, take time: "
					+ (endTime - startTime) / 1000 + "s");
		} else {
			LogUtil.e(TAG, "upload error!!");
		}

		raf.close();
		inStream.close();
		outStream.close();
		socket.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler = null;
		stopUploadTask();
		if (mThreadDownload != null) {
			mThreadDownload.stopDownloadTask();
		}
	}

	/**
	 * 停止时间计时器，即停止发消息更新界面
	 */
	private void stopUploadTask() {
		if (mUploadTimer != null) {
			mUploadTimer.cancel();
			mUploadTimer = null;
		}
	}

	private final class UploadTask extends TimerTask {
		@Override
		public void run() {
			try {
				mUploadedTime++;
				long remainLength = mFileLength - mUploadedTotal;// 剩余的文件大小
				int remainTime = (int) (remainLength * mUploadedTime / mUploadedTotal);// 计算大概还需要多少时间上传完成
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_UPDATE_UPLOADBAR;
				msg.arg1 = mUploadedTotal;
				msg.arg2 = remainTime;
				mHandler.sendMessage(msg);
			} catch (Exception e) {
				LogUtil.e(TAG, "UploadTask::run()::" + e.toString());
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SHOW_VIEWSTUB:
				mTextView.setText(getString(R.string.text_show_vs_counter,
						mTimeCounter));
				if (mHandler != null && mTimeCounter > 0) {
					mTimeCounter--;
					mHandler.sendEmptyMessageDelayed(MSG_SHOW_VIEWSTUB, 1000);
				} else {
					mTextView.setText("");
					mTextView.setVisibility(View.GONE);
					/*
					 * mViewStub.setVisibility(View.VISIBLE)与mViewStub.inflate()
					 * 的相同与不同：相同-->两者都可以使ViewStub包含的布局显示，从而填充ViewStub；
					 * 不同-->1.前者能被调用多次，后者只能被调用一次； 2.如果前者先调用，则后者不能再调用；
					 * 如果后者先调用，则前者还可以再调用。
					 */
					if (!isInflate) {
						isInflate = true;
						mInflateView = mViewStub.inflate();
						if (mInflateView.getId() == R.id.tl_vs_inflate_id) {
							ToastUtil.showToast("inflatedId");
						}
						mInflateBtn = (Button) mInflateView
								.findViewById(R.id.login_exit_btn);
						mInflateBtn.setText(R.string.inflate_only_invoke);
					} else {
						mViewStub.setVisibility(View.VISIBLE);
						mInflateBtn.setText(R.string.press_one_more);
					}
				}
				break;
			case MSG_UPDATE_UPLOADBAR:
				int progress = msg.arg1;
				mUploadProgress.setProgress(progress);

				String remainTime = TimeUtil.calculateTemainTime(msg.arg2);// 把还需要上传的时间完成转化为时分秒格式
				float percent = (float) progress
						/ (float) mUploadProgress.getMax();
				int result = (int) (percent * 100);
				mTextProgress.setText(result + "% ,"
						+ getString(R.string.need_time) + remainTime);

				if (progress == mUploadProgress.getMax()) {
					stopUploadTask();
					ToastUtil.showToast(R.string.upload_file_finished);
					mTextProgress.setText(R.string.upload_file_finished);
				}
				break;
			default:
				break;
			}
		}
	};

}
