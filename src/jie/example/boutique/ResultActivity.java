package jie.example.boutique;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends BasicActivity {

	private ImageView barcodeImageView;
	private TextView formatTextView;
	private TextView timeTextView;
	private TextView metaTextView;
	private TextView resultTextView;
	private Bitmap barcodeBitmap;
	private String barcodeFormat;
	private String decodeDate;
	private CharSequence metadataText;
	private String resultString;
	private Bundle bundle;
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.scan_result);
		setContentView(R.layout.scan_result_aty);
		initData();
		setView();
		loadingData();
	}

	private void setView() {
		barcodeImageView.setImageBitmap(barcodeBitmap);
		formatTextView.setText(barcodeFormat);
		timeTextView.setText(decodeDate);
		metaTextView.setText(metadataText);
		resultTextView.setText(resultString);
	}

	@Override
	public void initData() {
		barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
		formatTextView = (TextView) findViewById(R.id.format_text_view);
		timeTextView = (TextView) findViewById(R.id.time_text_view);
		metaTextView = (TextView) findViewById(R.id.meta_text_view);
		resultTextView = (TextView) findViewById(R.id.contents_text_view);
		mWebView = (WebView) findViewById(R.id.web_view);

		bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		barcodeBitmap = bundle.getParcelable("bitmap");
		barcodeFormat = bundle.getString("barcodeFormat");
		decodeDate = bundle.getString("decodeDate");
		metadataText = bundle.getCharSequence("metadataText");
		resultString = bundle.getString("resultString");
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void loadingData() {
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(resultString);
	}
}
