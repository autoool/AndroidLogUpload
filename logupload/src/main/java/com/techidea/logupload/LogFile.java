package com.techidea.logupload;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zchao on 2017/1/10.
 */

public class LogFile {

    public LogFile() {
    }

    //文件存在追加
    public void writeToFile(File file, String fileContent) {
        if (file.exists()) {
            try {
                BufferedWriter bufferedWriter = null;
                FileWriter writer = new FileWriter(file, true);
                bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.append(fileContent);
                bufferedWriter.flush();
                bufferedWriter.close();
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {

            }
        }
    }

    public boolean exists(File file) {
        return file.exists();
    }

    public void clearDirectory(File directory) {
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }
    }

    public String readFileContent(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        if (file.exists()) {
            String stringLine;
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((stringLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(stringLine + "\n");
                }
                bufferedReader.close();
                fileReader.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            } finally {

            }
        }
        return stringBuilder.toString();
    }
}
