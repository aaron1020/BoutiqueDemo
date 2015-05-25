package jie.example.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BasicAdapter<T, ViewHolder> extends BaseAdapter {

	private List<T> mDataList;
	private LayoutInflater mInflater;

	public BasicAdapter(List<T> dataList, Context context) {
		this.mDataList = dataList;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (mDataList != null) {
			return mDataList.size();
		}
		return 0;
	}

	@Override
	public T getItem(int position) {
		if (mDataList != null) {
			return mDataList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder vHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(getItemViewLayoutId(), null);
			vHolder = loadViewHolder(convertView);
			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		T object = getItem(position);
		if (object != null) {
			bindView(position, object, vHolder);
		}

		return convertView;
	}

	protected abstract int getItemViewLayoutId();

	protected abstract void bindView(int position, T object, ViewHolder vHolder);

	protected abstract ViewHolder loadViewHolder(View view);

}
