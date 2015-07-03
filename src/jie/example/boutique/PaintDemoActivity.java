package jie.example.boutique;

import jie.example.utils.LogUtil;
import jie.example.widget.PaintDemoView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
		RelativeLayout mSVContainer = (RelativeLayout) findViewById(R.id.surface_view_container);
		mSVContainer.addView(new MySurfaceView(this));
	}

	@Override
	public void loadingData() {

	}

	public void setOnClick(View view) {
	}

	private class MySurfaceView extends SurfaceView implements
			SurfaceHolder.Callback {

		private SurfaceHolder surfaceHolder;
		private MyRunnable myRunnable;

		public MySurfaceView(Context context) {
			super(context);
			surfaceHolder = this.getHolder();
			surfaceHolder.addCallback(this);
			myRunnable = new MyRunnable(surfaceHolder);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			myRunnable.isRun = true;
			new Thread(myRunnable, "SurfaceView").start();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			myRunnable.isRun = false;
		}

	}

	private class MyRunnable implements Runnable {

		private SurfaceHolder surfaceHolder;
		private boolean isRun;

		public MyRunnable(SurfaceHolder surfaceHolder) {
			this.surfaceHolder = surfaceHolder;
			isRun = true;
		}

		@Override
		public void run() {
			int count = 0;
			while (isRun) {
				Canvas canvas = null;
				try {
					synchronized (surfaceHolder) {
						canvas = surfaceHolder.lockCanvas();
						canvas.drawColor(getResources().getColor(
								R.color.eagle_two));
						Paint paint = new Paint();
						paint.setColor(getResources().getColor(
								R.color.eagle_four));
						Rect rect = new Rect(100, 50, 300, 250);
						canvas.drawRect(rect, paint);
						canvas.drawText("" + (count++), 100, 310, paint);
					}
				} catch (Exception e) {
					LogUtil.e("PaintDemoActivity", "Surface Paint Exctption-->"
							+ e.toString());
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}

	}

}
