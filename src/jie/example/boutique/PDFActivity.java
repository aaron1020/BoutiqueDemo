package jie.example.boutique;

import jie.example.utils.ToastUtil;
import jie.example.widget.SteeringWheelView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import android.app.Activity;
import android.os.Bundle;

public class PDFActivity extends Activity implements OnPageChangeListener {

	private PDFView pdfView;
	private int pageNumber = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pdf_activity);
		pdfView = (PDFView) findViewById(R.id.pdfView);
		new SteeringWheelView(this, 240, 200);
		//new SteeringWheelView(this);
	}

	@Override
	public void onPageChanged(int page, int pageCount) {
		pageNumber = page;
		ToastUtil.showToast("页数:" + pageNumber);
	}

	private void display(String assetFileName, boolean jumpToFirstPage) {
		if (jumpToFirstPage) {
			pageNumber = 1;
		}

		pdfView.fromAsset(assetFileName).defaultPage(pageNumber)
				.swipeVertical(true).onPageChange(this).load();
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread(new Runnable() {
			@Override
			public void run() {
				display("sample.pdf", true);
			}
		}).start();
	}

}
