package techidea.com.androidlogupload;

import android.app.Application;

import com.techidea.logupload.LogcatHelper;

/**
 * Created by zchao on 2017/1/10.
 */

public class LogApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogcatHelper.getINSTANCE(getApplicationContext()).start();
    }
}
