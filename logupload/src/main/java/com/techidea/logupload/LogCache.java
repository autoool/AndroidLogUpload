package com.techidea.logupload;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Created by zchao on 2017/1/10.
 * 只管把字符串写到文件里面，如果单独文件超出大小，新建另一个文件
 */

public class LogCache {

    private final LogFile logFile;
    private final Context context;
    private final File logDir;
    private Executor executor;
    private static final long maxFileSize = 1024 * 1024 * 2;//2M  1024 = 1k
    private String baseFileName = "logcat";
    private String filename;
    private int fileCount = 0;
    private String logDirName = "Fantasee";
    private String deviceSerial = "";
    private String PATH_LOGCAT;

    public LogCache(Context context) {
        this.context = context;
        this.logFile = new LogFile();
        this.deviceSerial = Build.SERIAL;
        this.logDir = getLogDir();
        this.executor = new JobExecutor();
    }

    private File getLogDir() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            PATH_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + logDirName + File.separator + deviceSerial;
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                    + File.separator + logDirName + File.separator + deviceSerial;
        }
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private File buildFile(String filename) {
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(this.logDir.getPath());
        fileNameBuilder.append(File.separator);
        fileNameBuilder.append(filename);
        return new File(fileNameBuilder.toString());
    }

    public void putLog(String content) {

        filename = baseFileName + "_" + String.valueOf(fileCount) + ".txt";
        File logFile = this.buildFile(filename);
        try {
            if (!logFile.exists())
                logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.executeAsynchronously(new CacheWriter(
                this.logFile, logFile, content
        ));
        long fileSize = getFileSize(logFile);
        if (fileSize >= maxFileSize)
            fileCount++;

    }

    public void evictAll() {
        this.executeAsynchronously(new CacheEvictor(this.logFile, this.logDir));
    }

    public boolean isExist(String filename) {
        File file = this.buildFile(filename);
        return this.logFile.exists(file);
    }

    private void executeAsynchronously(Runnable runnable) {
        this.executor.execute(runnable);
    }


    private static class CacheWriter implements Runnable {

        private final LogFile logFile;
        private final File fileToWrite;
        private final String fileContent;

        public CacheWriter(LogFile logFile, File fileToWrite, String fileContent) {
            this.logFile = logFile;
            this.fileToWrite = fileToWrite;
            this.fileContent = fileContent;
        }

        @Override
        public void run() {
            this.logFile.writeToFile(fileToWrite, fileContent);
        }
    }

    private static class CacheEvictor implements Runnable {
        private final LogFile logFile;
        private final File cacheDir;

        public CacheEvictor(LogFile fileManager, File cacheDir) {
            this.logFile = fileManager;
            this.cacheDir = cacheDir;
        }

        @Override
        public void run() {
            this.logFile.clearDirectory(this.cacheDir);
        }
    }

    private long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

}
