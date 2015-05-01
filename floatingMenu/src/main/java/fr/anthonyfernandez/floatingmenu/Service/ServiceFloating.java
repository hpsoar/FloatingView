package fr.anthonyfernandez.floatingmenu.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListPopupWindow;

import com.beacon.android.QuickLauncherView;

import java.util.ArrayList;
import java.util.List;

import fr.anthonyfernandez.floatingmenu.Activities.QuickLauncherActivity;
import fr.anthonyfernandez.floatingmenu.Manager.PInfo;
import fr.anthonyfernandez.floatingmenu.R;

public class ServiceFloating extends Service {

	public static  int ID_NOTIFICATION = 2018;

	private WindowManager windowManager;
	private ImageView chatHead;
    private WindowManager.LayoutParams chatHeadParams;

	long lastPressTime;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override 
	public void onCreate() {
		super.onCreate();
		
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        createChatHead();

		try {
			chatHead.setOnTouchListener(new View.OnTouchListener() {
				private WindowManager.LayoutParams paramsF = chatHeadParams;
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						// Get current time in nano seconds.
						long pressTime = System.currentTimeMillis();


						// If double click...
						if (pressTime - lastPressTime <= 300) {
                            createNotification();
                            ServiceFloating.this.stopSelf();
						}

						lastPressTime = pressTime;
						initialX = paramsF.x;
						initialY = paramsF.y;
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
						break;
					case MotionEvent.ACTION_UP:
						break;
					case MotionEvent.ACTION_MOVE:
						paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
						paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
						windowManager.updateViewLayout(chatHead, paramsF);
                        return true;
					}
					return false;
				}
			});

            chatHead.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                // TODO: show an input interface, with action buttons
                // 				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //				getApplicationContext().startActivity(intent);
                return true;
                }
            });
		} catch (Exception e) {
			// TODO: handle exception
		}

		chatHead.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
                int cx = (chatHeadParams.x + chatHead.getWidth() / 2);
                int cy = (chatHeadParams.y + chatHead.getHeight() / 2);

                Intent intent = new Intent(ServiceFloating.this, QuickLauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
			}
		});

	}

    private void createChatHead() {
        chatHead = new ImageView(this);

        chatHead.setImageResource(R.drawable.floating2);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(prefs.getString("ICON", "floating2").equals("floating3")){
            chatHead.setImageResource(R.drawable.floating3);
        } else if(prefs.getString("ICON", "floating2").equals("floating4")){
            chatHead.setImageResource(R.drawable.floating4);
        } else if(prefs.getString("ICON", "floating2").equals("floating5")){
            chatHead.setImageResource(R.drawable.floating5);
        } else if(prefs.getString("ICON", "floating2").equals("floating5")){
            chatHead.setImageResource(R.drawable.floating2);
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        chatHeadParams = params;

        windowManager.addView(chatHead, params);
    }

	public void createNotification(){
		Intent notificationIntent = new Intent(getApplicationContext(), ServiceFloating.class);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, notificationIntent, 0);

		Notification notification = new Notification(R.drawable.floating2, "Click to start launcher",System.currentTimeMillis());
		notification.setLatestEventInfo(getApplicationContext(), "Start launcher" ,  "Click to start launcher", pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;

		NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(ID_NOTIFICATION,notification);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null) windowManager.removeView(chatHead);
	}
}