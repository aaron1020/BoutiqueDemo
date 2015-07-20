package jie.example.boutique.receiver;

import jie.example.boutique.LogingActivity;
import jie.example.boutique.R;
import jie.example.manager.ActivityCollector;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

/**
 * 强制下线功能
 */
public class ForceOfflineReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.force_offline_warn).setMessage(
				R.string.force_offline_warn_content);
		dialogBuilder.setCancelable(false);// 设置AlertDialog为不可取消
		dialogBuilder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActivityCollector.finishAllActivity();// 启动登录界面前，先销毁所有的Activity

						Intent intent = new Intent(context,
								LogingActivity.class);
						// 因为是在广播接收者启动Activity，所以要加入FLAG_ACTIVITY_NEW_TASK，不然会报错
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}
				});
		AlertDialog alertDialog = dialogBuilder.create();
		/*
		 * 除了在功能清单文件中声明可以弹出Dialog的权限外，还需要在代码中设置AlertDialog的类型，
		 * 以保证在广播接收者可以正常弹出AlertDialog
		 */
		alertDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alertDialog.show();
	}

}
