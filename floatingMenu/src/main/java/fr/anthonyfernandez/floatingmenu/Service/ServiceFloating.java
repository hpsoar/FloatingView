package fr.anthonyfernandez.floatingmenu.Service;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListPopupWindow;

import com.beacon.android.QuickLauncherView;

import java.util.ArrayList;
import java.util.List;

import fr.anthonyfernandez.floatingmenu.Adapter.CustomAdapter;
import fr.anthonyfernandez.floatingmenu.Card;
import fr.anthonyfernandez.floatingmenu.Manager.PInfo;
import fr.anthonyfernandez.floatingmenu.Manager.RetrievePackages;
import fr.anthonyfernandez.floatingmenu.R;

public class ServiceFloating extends Service {

	public static  int ID_NOTIFICATION = 2018;

	private WindowManager windowManager;
	private ImageView chatHead;
    private WindowManager.LayoutParams chatHeadParams;
    private ListPopupWindow popupWindow;
    private View testView;

	long lastPressTime;

	ArrayList<PInfo> apps;
	List listCity;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override 
	public void onCreate() {
		super.onCreate();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		RetrievePackages getInstalledPackages = new RetrievePackages(getApplicationContext());
		apps = getInstalledPackages.getInstalledApps(false);

		listCity = new ArrayList();
		for(int i=0 ; i<apps.size() ; ++i) {
			listCity.add(apps.get(i));
		}

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		chatHead = new ImageView(this);
		
		chatHead.setImageResource(R.drawable.floating2);
		
		if(prefs.getString("ICON", "floating2").equals("floating3")){
			chatHead.setImageResource(R.drawable.floating3);
		} else if(prefs.getString("ICON", "floating2").equals("floating4")){
			chatHead.setImageResource(R.drawable.floating4);
		} else if(prefs.getString("ICON", "floating2").equals("floating5")){
			chatHead.setImageResource(R.drawable.floating5);
		} else if(prefs.getString("ICON", "floating2").equals("floating5")){
			chatHead.setImageResource(R.drawable.floating2);
		}

        createTestView();

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



		try {
			chatHead.setOnTouchListener(new View.OnTouchListener() {
				private WindowManager.LayoutParams paramsF = params;
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
						break;
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
                    return false;
                }
            });
		} catch (Exception e) {
			// TODO: handle exception
		}

		chatHead.setOnClickListener(new View.OnClickListener() {
            private boolean isShowing = false;
			@Override
			public void onClick(View arg0) {
                if (isShowing) {
                    //popupWindow.dismiss();
                }
                else {
                    //initiatePopupWindow(chatHead);
                }
                isShowing = !isShowing;
                showTestView(isShowing);
			}
		});

	}

    private void createTestView() {
        if (testView == null) {
            testView = new QuickLauncherView(this);
            //testView = new Card(this);
            testView.setVisibility(View.INVISIBLE);

            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            //params.gravity = Gravity.CENTER;
            windowManager.addView(testView, params);
        }
    }

    private void showTestView(boolean show) {
        View view = chatHead;
        int cx = (chatHeadParams.x + view.getWidth() / 2);
        int cy = (chatHeadParams.y + view.getHeight() / 2);
        float radius = Math.max(testView.getWidth(), testView.getHeight());// * 2.0f;

        Animator reveal;
        if (show) {
            testView.setVisibility(View.VISIBLE);
            reveal = ViewAnimationUtils.createCircularReveal(testView, cx, cy, 0, radius);
            reveal.setInterpolator(new AccelerateInterpolator(2.0f));
        }
        else {
            reveal = ViewAnimationUtils.createCircularReveal(
                    testView, cx, cy, radius, 0);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    testView.setVisibility(View.INVISIBLE);
                }
            });
        }
        reveal.setDuration(200);
        reveal.start();
    }


	private void initiatePopupWindow(View anchor) {
		try {
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            if (popupWindow == null) {
                popupWindow = new ListPopupWindow(this);
            }

			popupWindow.setAnchorView(anchor);
			popupWindow.setWidth((int) (display.getWidth()/(1.5)));
			popupWindow.setAdapter(new CustomAdapter(getApplicationContext(), R.layout.row, listCity));
			popupWindow.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id3) {
					//Log.w("tag", "package : "+apps.get(position).pname.toString());
					Intent i;
					PackageManager manager = getPackageManager();
					try {
						i = manager.getLaunchIntentForPackage(apps.get(position).pname.toString());
						if (i == null)
							throw new PackageManager.NameNotFoundException();
						i.addCategory(Intent.CATEGORY_LAUNCHER);
						startActivity(i);
                        popupWindow.dismiss();
					} catch (PackageManager.NameNotFoundException e) {

					}
				}
			});
			popupWindow.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
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