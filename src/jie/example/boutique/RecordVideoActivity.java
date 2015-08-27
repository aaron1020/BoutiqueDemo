package jie.example.boutique;

import jie.example.utils.LogUtil;
import jie.example.widget.RecordVideoView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RecordVideoActivity extends Activity {
	private static final String TAG = RecordVideoActivity.class.getSimpleName();
	private RecordVideoView mRecordView;
	private TextView startBtn, stopBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_video_aty);
		initData();
		loadingData();

	}

	public void initData() {
		mRecordView = (RecordVideoView) findViewById(R.id.moive_rv);
		startBtn = (TextView) findViewById(R.id.start_btn);
		stopBtn = (TextView) findViewById(R.id.stop_btn);
	}

	public void loadingData() {
		startBtn.setOnClickListener(new View.OnClickListener() { // 开始录制
			@Override
			public void onClick(View v) {
				try {
					mRecordView.record();
				} catch (Exception e) {
					LogUtil.e(TAG, "mRecordView.record()::" + e.toString());
				}
			}
		});
		stopBtn.setOnClickListener(new View.OnClickListener() {// 停止录制
			@Override
			public void onClick(View v) {
				try {
					mRecordView.stop();
				} catch (Exception e) {
					LogUtil.e(TAG, "mRecordView.stop()::" + e.toString());
				}
			}
		});
	}

}
