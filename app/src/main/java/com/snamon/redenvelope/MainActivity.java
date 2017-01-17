package com.snamon.redenvelope;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    private SweetAlertDialog mSweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String serviceName = getPackageName() + "/" + EnvelopeAccessibilityService.class.getCanonicalName();

        if (!SystemUtil.isAccessibilitySettingsOn(this, serviceName)) {
            LoggWrap.i("显示设置");
            mSweetAlertDialog = new SweetAlertDialog(this)
                    .setContentText("进入")
                    .setConfirmText("去设置")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        //跳转到辅助界面
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                        if (mSweetAlertDialog.isShowing()) {
                            mSweetAlertDialog.dismiss();
                        }
                    });
            mSweetAlertDialog.show();
        }
        findViewById(R.id.btn_mock_click)
                .setOnClickListener(v -> delaySendNotification());
    }

    private void delaySendNotification() {
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    Intent intent = new Intent(MainActivity.this, WxActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent,
                            PendingIntent.FLAG_NO_CREATE);

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
                    Notification notification = builder
                            .setContentTitle("模拟")
                            .setContentText("这是通知内容")
                            .setContentInfo("test")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(
                                    getResources(), R.mipmap.ic_launcher))
                            .setContentIntent(pendingIntent)
                            .build();
                    manager.notify(1, notification);
                });
    }
}