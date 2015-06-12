package jie.example.boutique;

import jie.example.widget.PaintDemoView;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 运用Paint绘制图形
 */
public class PaintDemoActivity extends BasicActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.menu_paint_demo);
		setContentView(R.layout.paint_demo_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.paint_view_container);
		layout.addView(new PaintDemoView(this));
	}

	@Override
	public void loadingData() {

	}

	public void setOnClick(View view) {
	}

}
