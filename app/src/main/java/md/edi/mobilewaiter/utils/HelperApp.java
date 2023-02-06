package md.edi.mobilewaiter.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class HelperApp {

    public static boolean isAppRunning(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (context.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName())){
                //it return true if app is open on display or is running in background , because
                //check if app is in background or is open on display (return true if is open on display, return false if is fore-background)
                return !isInBackground();
            }

        }
        return false;
    }

    private static boolean isInBackground(){
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        return myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
    }
}