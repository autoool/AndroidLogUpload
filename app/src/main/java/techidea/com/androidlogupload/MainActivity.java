package techidea.com.androidlogupload;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.techidea.logupload.LogcatHelper;

import java.io.FileNotFoundException;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    boolean running = true;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_log)
    void buttonLog() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    if (!running) {
                        break;
                    }
                    try {
                        throw new FileNotFoundException("file not find test");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
        LogcatHelper.getINSTANCE(getApplicationContext()).stop();
    }
}
