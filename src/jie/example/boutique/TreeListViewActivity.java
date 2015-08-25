package jie.example.boutique;

import java.io.File;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import jie.example.adapter.TreeListViewAdapter;
import jie.example.constant.Constant;
import jie.example.manager.ActivityCollector;
import jie.example.manager.BoutiqueApp;
import jie.example.net.SingleThreadDownload;
import jie.example.utils.FileUtil;
import jie.example.utils.LogUtil;
import jie.example.utils.ToastUtil;
import android.annotation.SuppressLint;
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
			.getSimpleName();;
	private static final int MSG_SHOW_VIEWSTUB = 1001;
	private static final int MSG_UPDATE_UPLOADBAR = 1002;
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
	private int mTimeCounter = TEXT_SHOW_TIME;
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
			new Thread(new SingleThreadDownload(
					"http://192.168.63.66:8080/NetForAndroid/AdobeReader.exe"))
					.start();
			break;
		case R.id.tl_btn_upload:
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				final File file = new File(BoutiqueApp.getAppFolder(),
						"Download/" + "AdobeReader.exe");
				if (file.exists()) {
					mUploadProgress.setMax((int) file.length());
					mTextProgress.setText("0%");

					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								uploadFile(file);
							} catch (Exception e) {
								ToastUtil.showToast(R.string.upload_file_fail);
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
		default:
			break;
		}
	}

	private void uploadFile(File file) throws Exception {
		long startTime = System.currentTimeMillis();

		Socket socket = new Socket(Constant.WEB_IP, 7879);
		OutputStream outStream = socket.getOutputStream();

		String fileName = file.getName();
		long filelength = file.length();
		LogUtil.i(TAG, "filelength = " + filelength);
		String head = "Content-Length=" + filelength + ";filename=" + fileName
				+ ";sourceid=\r\n";// head为自定义协议，回车换行为方便我们提取第一行数据而自行设定的。
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
		int total = 0;
		long intervalTime = System.currentTimeMillis();
		int countTime = 0;
		while ((len = raf.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
			total += len;
			if ((System.currentTimeMillis() - intervalTime) % 1000 == 0) {
				countTime++;
				LogUtil.i(TAG, countTime + "s");
			}
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_UPDATE_UPLOADBAR;
			msg.arg1 = total;
			mHandler.sendMessage(msg);
		}

		if (total == filelength - position) {
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

				float percent = (float) progress
						/ (float) mUploadProgress.getMax();
				int result = (int) (percent * 100);
				mTextProgress.setText(result + "%");
				if (progress == mUploadProgress.getMax()) {
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
