package kangxiaofei.miniweather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
/**
 * Created by kang on 2016/12/23.
 */
public class UpdateService extends Service{
    private final static String TAG = "UpdateService";

    @Override
    public void onCreate() {
        Log.i(TAG, "Service--------onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service--------onStartCommand");
        //这里执行更新的操作

        // return START_STICKY;
        //这个返回的是？？？
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service--------onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
}
