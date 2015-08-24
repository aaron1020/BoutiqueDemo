package jie.example.widget;

import jie.example.boutique.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class CascadeLayout extends ViewGroup {
	private static final String TAG = CascadeLayout.class.getSimpleName();
	private int mHorizontalSpacing;
	private int mVerticalSpacing;

	// 当通过XML文件创建视图时会调用该构造函数
	public CascadeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		Log.i(TAG, "CascadeLayout(Context, AttributeSet)");
	}

	public CascadeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.i(TAG, "CascadeLayout(Context, AttributeSet, int)");
		try {
			TypedArray typedArray = context.obtainStyledAttributes(attrs,
					R.styleable.CascadeLayout);

			// mHorizontalSpacing和mVerticalSpacing由自定义属性中获取，如果其值未指定，就使用默认值
			mHorizontalSpacing = typedArray.getDimensionPixelSize(
					R.styleable.CascadeLayout_horizontal_spacing,
					getResources().getDimensionPixelSize(
							R.dimen.cascade_bv_horizontal_space));
			mVerticalSpacing = typedArray.getDimensionPixelSize(
					R.styleable.CascadeLayout_vertical_spacing,
					getResources().getDimensionPixelSize(
							R.dimen.cascade_bv_vertical_space));
			typedArray.recycle();

			Log.i(TAG, "mHorizontalSpacing = " + mHorizontalSpacing);
			Log.i(TAG, "mVerticalSpacing = " + mVerticalSpacing);
		} catch (Exception e) {
			Log.e(TAG, "Init CascadeLayout Exception-->" + e.toString());
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 使用宽和高计算布局的最终大小以及子视图的x与y轴位置
		int width = 0;
		int height = getPaddingTop();
		Log.i(TAG, "getPaddingTop() = " + height);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			int verticalSpacing = mVerticalSpacing;
			View child = getChildAt(i);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);// 令每个子视图测量自身

			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			// 每个子view距离最左边的x坐标 = 左内边距 + 每个子view设置的左间距(倍数递增)
			width = getPaddingLeft() + mHorizontalSpacing * i;
			Log.i(TAG, "getPaddingLeft() = " + getPaddingLeft());
			lp.x = width;
			lp.y = height;
			if (lp.verticalSpacing >= 0) {// 如果有子view定义了垂直间距，再把这高度加上layout的高度里
				verticalSpacing = lp.verticalSpacing;
				Log.i(TAG, "verticalSpacing = " + verticalSpacing);
			}

			// 总宽度 = 每个子view距离最左边的x坐标 + 子view宽度
			width += child.getMeasuredWidth();
			Log.i(TAG, "child.getMeasuredWidth() = " + child.getMeasuredWidth());
			height += verticalSpacing;
			Log.i(TAG, "width = " + width);
			Log.i(TAG, "height = " + height);
		}
		// 宽度最后加上右边距
		width += getPaddingRight();
		Log.i(TAG, "getPaddingRight() = " + getPaddingRight());
		// 这样layout的高度只是最底部的上边距加上最后一个子view的高度而已
		int getPaddingBottom = getChildAt(getChildCount() - 1)
				.getMeasuredHeight() + getPaddingBottom();
		Log.i(TAG, "getPaddingBottom() = " + getPaddingBottom);
		height += getPaddingBottom;

		// 有个不是最后一个的子view特别宽怎么办?-->宽度测量不对
		// 高度测量也不对
		// 使用计算所得的宽和高设置整个布局的测量尺寸
		setMeasuredDimension(resolveSize(width, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {// 定位子view
			View child = getChildAt(i);
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y
					+ child.getMeasuredHeight());
			Log.i(TAG,
					"lp.x = " + lp.x + " ,lp.y = " + lp.y
							+ " ,lp.x + child.getMeasuredWidth() = "
							+ (lp.x + child.getMeasuredWidth())
							+ ", lp.y + child.getMeasuredHeight() = "
							+ (lp.y + child.getMeasuredHeight()));
		}
	}

	// 要使用自定义的LayoutParams，还需要重写以下方法start
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof CascadeLayout.LayoutParams;
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new CascadeLayout.LayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new LayoutParams(p);
	}

	// 要使用自定义的LayoutParams，还需要重写以下方法end

	/**
	 * 定义LayoutParams，用于保存子视图的x、y轴位置
	 */
	public static class LayoutParams extends ViewGroup.LayoutParams {

		int x;// 记录子view的x坐标
		int y;// 记录子view的y坐标
		int verticalSpacing;// 子view的垂直间距

		public LayoutParams(Context context, AttributeSet attrs) {
			super(context, attrs);

			// 拿到子视图自定义属性，这里是自定义垂直间距
			TypedArray typedArray = context.obtainStyledAttributes(attrs,
					R.styleable.CascadeLayout_LayoutParams);

			try {
				verticalSpacing = typedArray
						.getDimensionPixelSize(
								R.styleable.CascadeLayout_LayoutParams_layout_vertical_spacing,
								-1);
			} catch (Exception e) {
				Log.e(TAG,
						"Init CascadeLayout.LayoutParams Exception-->"
								+ e.toString());
			} finally {
				if (typedArray != null) {
					typedArray.recycle();
				}
			}
		}

		public LayoutParams(int w, int h) {
			super(w, h);
		}

		public LayoutParams(ViewGroup.LayoutParams layoutParams) {
			super(layoutParams);
		}
	}

}
