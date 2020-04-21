package com.titanium.app_manager.utils;

import android.content.Intent;
import android.net.Uri;

import java.io.DataOutputStream;
import java.io.File;

public class SystemRemoveUtils {

    public static boolean removeSystemApp(String appDir){
        try {
            String parent = new File(appDir).getParent();
            Process exec = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(exec.getOutputStream());
            dataOutputStream.writeBytes("mount -o rw,remount /system; \n");
            dataOutputStream.writeBytes("chmod 777 "+ parent + "; \n");
            dataOutputStream.writeBytes("chmod 777 "+ appDir + "; \n");
            dataOutputStream.writeBytes("rm -r"+ appDir + "; \n");
            dataOutputStream.writeBytes("mount -o ro,remount /system; \n");
            dataOutputStream.flush();
            dataOutputStream.close();
            exec.waitFor();
        } catch (Throwable th){
            th.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean isRootGranted(){
        try {
            Process process = Runtime.getRuntime().exec("su",null, new File("/"));
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes("pwd\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            process.waitFor();
            process.destroy();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Intent getRootHelpIntent(){
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wikihow.tech/Root-Android-Phones"));
    }
}
