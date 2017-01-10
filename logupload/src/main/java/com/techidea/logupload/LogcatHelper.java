package com.techidea.logupload;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zchao on 2017/1/10.
 * 只管输出日志，调用logCache写入文件
 */

public class LogcatHelper {

    private static LogcatHelper INSTANCE = null;

    private LogDumper logDumper = null;

    private int fileCount = 0;
    private final LogCache logCache;
    private int mPID;


    public static LogcatHelper getINSTANCE(Context context) {
        if (INSTANCE == null)
            INSTANCE = new LogcatHelper(context);
        return INSTANCE;
    }


    private LogcatHelper(Context context) {
        this.mPID = android.os.Process.myPid();
        this.logCache = new LogCache(context);
    }


    public void start() {
        if (logDumper == null)
            logDumper = new LogDumper(String.valueOf(mPID));
        logDumper.start();
    }

    public void stop() {
        if (logDumper != null) {
            logDumper.stopLogs();
            logDumper = null;
        }
    }

//    public void deleteLog(){
//        if (logCache!=null)
//            logCache.evictAll();
//    }

    private class LogDumper extends Thread {
        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid) {
            this.mPID = pid;
            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */
            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
//            cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";
        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()
                ), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0)
                        continue;
                    logCache.putLog(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }
    }

}
