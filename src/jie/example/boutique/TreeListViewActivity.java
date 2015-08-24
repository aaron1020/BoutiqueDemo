package jie.example.boutique;

import java.util.ArrayList;
import java.util.List;

import jie.example.adapter.TreeListViewAdapter;
import jie.example.manager.ActivityCollector;
import jie.example.net.SingleThreadDownload;
import jie.example.utils.ToastUtil;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 树形ListView
 */
public class TreeListViewActivity extends BasicActivity {
	private static final int MSG_SHOW_VIEWSTUB = 1001;
	private static final int TEXT_SHOW_TIME = 3;
	private ListView mListView;
	private TreeListViewAdapter mTreeAdapter;
	private List<String> mStringList;
	private TextView mTextView;
	private ViewStub mViewStub;
	private View mInflateView;
	private Button mInflateBtn;
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
		default:
			break;
		}
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
			default:
				break;
			}
		}
	};

}
