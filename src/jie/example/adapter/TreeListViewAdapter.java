package jie.example.adapter;

import java.util.List;

import jie.example.boutique.R;
import jie.example.boutique.TreeListViewActivity;
import jie.example.utils.StringUtil;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class TreeListViewAdapter extends
		BasicAdapter<String, TreeListViewAdapter.ViewHolder> {

	private Animation mTextRightAnimation;
	private Animation mTextLeftAnimation;
	private TreeListViewActivity mActivity;

	public TreeListViewAdapter(List<String> dataList, Context context) {
		super(dataList, context);
		mActivity = (TreeListViewActivity) context;
		mTextRightAnimation = AnimationUtils.loadAnimation(mActivity,
				R.anim.tree_listview_right_text_rotate);
		mTextLeftAnimation = AnimationUtils.loadAnimation(mActivity,
				R.anim.tree_listview_left_text_rotate);
	}

	@Override
	protected int getItemViewLayoutId() {
		return R.layout.tree_listview_adt;
	}

	@Override
	protected ViewHolder loadViewHolder(View view) {
		return new ViewHolder(view);
	}

	@Override
	protected void bindView(int position, String object, ViewHolder vHolder) {

		if (StringUtil.isNotEmpty(object)) {
			// if (position % 2 == 0) {
			vHolder.textLeft.setText(object);
			vHolder.textLeft.startAnimation(mTextLeftAnimation);
			// vHolder.textRight.setVisibility(View.GONE);
			// vHolder.textLeft.setVisibility(View.VISIBLE);
			// } else {
			vHolder.textRight.setText(object);
			vHolder.textRight.startAnimation(mTextRightAnimation);
			// vHolder.textLeft.setVisibility(View.GONE);
			// vHolder.textRight.setVisibility(View.VISIBLE);
			// }
		}

		// 隐藏最后一项的竖线
		if (position == super.getCount() - 1) {
			vHolder.centerImageLine.setVisibility(View.GONE);
		} else {
			vHolder.centerImageLine.setVisibility(View.VISIBLE);
		}
	}

	final class ViewHolder {

		TextView textLeft, textRight;
		ImageView centerImageLine;

		public ViewHolder(View view) {
			textLeft = (TextView) view.findViewById(R.id.tree_left_text);
			textRight = (TextView) view.findViewById(R.id.tree_right_text);
			centerImageLine = (ImageView) view
					.findViewById(R.id.tree_centre_vertical_line);
		}
	}

}
