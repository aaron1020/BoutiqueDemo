package jie.example.boutique;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LogingActivity extends BasicActivity {

	private EditText mEditUserName, mEditPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.user_login);
		setContentView(R.layout.login_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		TextView textName = (TextView) findViewById(R.id.login_text_password);
		// 在代码设置任意多的空格让“密码”变成“密(空格)(空格)码”，以保证和“用户名”前后对齐
		textName.setText(getString(R.string.user_password, "    "));
		mEditUserName = (EditText) findViewById(R.id.login_edit_name);
		mEditPassword = (EditText) findViewById(R.id.login_edit_password);
	}

	@Override
	public void loadingData() {
		mEditUserName.setText("Admin");
		mEditPassword.setText("123456");
	}

	public void setOnClick(View view) {
		switch (view.getId()) {
		case R.id.login_btn:
			if ("Admin".equals(mEditUserName.getText().toString())
					&& "123456".equals(mEditPassword.getText().toString())) {
				startActivity(new Intent(this, OfflineActivity.class));
			}
			break;
		case R.id.login_iv_password_show:
			break;
		default:
			break;
		}
	}

}
