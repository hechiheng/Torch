package com.hch.torch;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 手电筒
 * 
 * @author hch
 * 
 */
@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	private LinearLayout torchLayout;
	private boolean isLighton = true;// 是否点亮
	private Camera camera;

	private Notification mNotification;
	private NotificationManager mNotificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("aaa", "onCreate-----------");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		torchLayout = (LinearLayout) findViewById(R.id.torchLayout);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.notification).setTicker("手电筒已打开")
				.setContentTitle("手电筒已打开").setContentText("点击此处以关闭手电筒")
				.setAutoCancel(true).build();
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mNotification.contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		torchLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isLighton = isLighton == true ? false : true;
				if (isLighton) {
					openTorch();
				} else {
					closeTorch();
				}
			}
		});
		// 打开手电筒
		openTorch();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		closeTorch();
		isLighton = false;
	}

	/**
	 * 打开手电筒
	 */
	private void openTorch() {
		if (camera == null) {
			camera = Camera.open();
		}
		Parameters params = camera.getParameters();
		params.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(params);
		camera.startPreview();

		showNotifiction();
		torchLayout.setBackgroundResource(R.drawable.torch_pressed);
		Toast.makeText(MainActivity.this, "手电筒已打开", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 关闭手电筒
	 */
	private void closeTorch() {
		camera.stopPreview();
		camera.release();
		camera = null;

		cancelNotifiction();
		torchLayout.setBackgroundResource(R.drawable.torch_normal);
		Toast.makeText(MainActivity.this, "手电筒已关闭", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示通知
	 */
	private void showNotifiction() {
		mNotificationManager.notify(0, mNotification);
	}

	/**
	 * 关闭通知
	 */
	private void cancelNotifiction() {
		mNotificationManager.cancel(0);
	}

	@Override
	protected void onDestroy() {
		if (isLighton) {
			closeTorch();
		}
		super.onDestroy();
	}
}
