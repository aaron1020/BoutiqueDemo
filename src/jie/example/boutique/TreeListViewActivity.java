package jie.example.boutique;

import java.util.ArrayList;
import java.util.List;

import jie.example.adapter.TreeListViewAdapter;
import jie.example.entity.ActivityCollector;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * 树形ListView
 */
public class TreeListViewActivity extends BasicActivity {

	private ListView mListView;
	private TreeListViewAdapter mTreeAdapter;
	private List<String> mStringList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.tree_listview_aty);
		setContentView(R.layout.tree_listview_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		mStringList = new ArrayList<String>();
		mListView = (ListView) findViewById(R.id.tree_listview);
		mTreeAdapter = new TreeListViewAdapter(mStringList, this);
		mListView.setAdapter(mTreeAdapter);
	}

	@Override
	public void loadingData() {
		mStringList.add("1111111111111");
		mStringList.add("2222222222222");
		mStringList.add("3333333333333");
		mStringList.add("4444444444444");
		mStringList.add("5555555555555");
		mStringList.add("6666666666666");
		mStringList.add("7777777777777");
		mStringList.add("8888888888888");
		mStringList.add("9999999999999");
		mStringList.add("10000000000000");
		mStringList.add("11000000000000");
		mTreeAdapter.notifyDataSetChanged();
	}

	public void setOnClick(View view) {
		ActivityCollector.finishAllActivity();
	}

}
