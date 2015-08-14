package jie.example.boutique;

import jie.example.widget.WriterView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class WriterActivity extends BasicActivity {
	private WriterView mWriterView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarTitle(R.string.writer_aty);
		setContentView(R.layout.writer_aty);
		
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		mWriterView = (WriterView) findViewById(R.id.writerView);
	}

	@Override
	public void loadingData() {
	}
	
	public void setOnClick(View view){
		switch(view.getId()){
		case R.id.btn_confirm:
			mWriterView.savePanelText();
			//mWriterView.changeBitmapBackBg();
			break;
		case R.id.btn_clear:
			mWriterView.clearPanel();
			break;
		default:
			break;
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			default:
				break;
			}
		}
	};
}
