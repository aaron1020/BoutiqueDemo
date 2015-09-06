package jie.example.boutique;

import java.io.File;

import jie.example.utils.LogUtil;
import jie.example.widget.RecordVideoView;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RecordVideoActivity extends Activity {
	private static final String TAG = RecordVideoActivity.class.getSimpleName();
	private static final int REQUEST_PLAY_RECORDED_VIDEO = 101;
	private RecordVideoView mRecordView;
	private TextView mBtnStartRecord, mBtnStopRecord;
	private boolean mStopRecorded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_video_aty);
		initData();
		loadingData();
	}

	public void initData() {
		mRecordView = (RecordVideoView) findViewById(R.id.moive_rv);
		mBtnStartRecord = (TextView) findViewById(R.id.start_record);
		mBtnStopRecord = (TextView) findViewById(R.id.stop_record);
	}

	public void loadingData() {
		mBtnStopRecord.setEnabled(false);
	}

	public void setOnClick(View view) {
		switch (view.getId()) {
		case R.id.start_record:
			try {
				mRecordView.startRecord();
				mBtnStopRecord.setEnabled(true);
				mStopRecorded = false;
			} catch (Exception e) {
				LogUtil.e(TAG, "mRecordView.record()::" + e.toString());
			}
			break;
		case R.id.stop_record:
			try {
				if (!mStopRecorded) {
					mStopRecorded = true;
					mRecordView.stop();
					mBtnStartRecord.setText(R.string.upload);
					mBtnStopRecord.setText(R.string.play);
				} else { // 播放刚刚录制的视频
					File videoFile = mRecordView.getSaveRecordFile();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(videoFile), "video/*");
					startActivityForResult(intent, REQUEST_PLAY_RECORDED_VIDEO);
				}
			} catch (Exception e) {
				LogUtil.e(TAG, "mRecordView.stop()::" + e.toString());
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_PLAY_RECORDED_VIDEO:
			mBtnStopRecord.setText(R.string.replay);
			break;

		default:
			break;
		}
	}
}
