package com.ipfstest.z11.ipfs1;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ProcessUtils {
    public static boolean daemonStarted(Context context) {
        File pid = new File(Constants.Dir.getLocalDir(context) + "/.pid");
        if (pid.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(pid));
                String strPid = reader.readLine();
                if (new File("/proc/" + strPid + "/status").exists()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
