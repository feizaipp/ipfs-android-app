package com.ipfstest.z11.ipfs1.service;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import com.ipfstest.z11.ipfs1.common.ExecLog;
import com.ipfstest.z11.ipfs1.utils.Constants;
import com.ipfstest.z11.ipfs1.utils.PropertyUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class IpfsExec {
    private Context context;
    private static IpfsExec ipfsNode;
    private static final String TAG = "IPFSTEST";

    public static IpfsExec getInstance(Context context) {
        if (ipfsNode == null) {
            ipfsNode = new IpfsExec(context);
        }
        return ipfsNode;
    }

    public IpfsExec(Context context) {
        this.context = context;
    }

    public void init() throws IOException {
        save();
        EventBus.getDefault().post(new ExecLog(Constants.Dir.getSDdir(context) + "/.ipfsNode/version"));
        if (!new File(Constants.Dir.getSDdir(context) + "/.ipfsNode/version").exists()) {
            Process init = command("init");
            EventBus.getDefault().post(new ExecLog("init"));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(init.getInputStream()));
            try {
                String log = null;
                while ((log = bufferedReader.readLine()) != null) {
                    EventBus.getDefault().post(new ExecLog(log + ""));
                }
                init.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                bufferedReader.close();
            }


        }
        return;

    }

    public boolean daemon() throws IOException {
        Process exec = command("daemon");
        EventBus.getDefault().post(new ExecLog("daemon"));
        BufferedReader daemon = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String log = null;
        while ((log = daemon.readLine()) != null) {
            EventBus.getDefault().post(new ExecLog(log + ""));
            if (log.equals("Daemon is ready")) {
                daemon.close();
                Log.d(TAG, "Daemon is ready");
                String pid = getProcessId(exec);
                savePID(pid);
                return true;
            }
        }
        EventBus.getDefault().post(new ExecLog("exit"));
        return false;
    }

    public boolean shutDown() throws IOException {
        Process exec = command("shutdown");
        BufferedReader shutdown = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String log = null;
        while ((log = shutdown.readLine()) != null) {
            EventBus.getDefault().post(new ExecLog(log + ""));
            if (log.equals("shutting down...")) {
                shutdown.close();
                return true;
            }
        }
        return false;
    }

    private void save() throws IOException {
        EventBus.getDefault().post(new ExecLog(Constants.Dir.getLocalDir(context) + "/ipfsNode" + ""));
        if (!new File(Constants.Dir.getLocalDir(context) + "/ipfsNode").exists()) {
            InputStream open;
            String arch = PropertyUtils.get("ro.product.cpu.abi", "arm64");
            EventBus.getDefault().post(new ExecLog(arch));
            Log.d(TAG, arch);
            if (arch.contains("x86")) {
                open = context.getAssets().open("x86");
            } else if (arch.contains("arm64")) {
                open = context.getAssets().open("arm64");
            } else {
                return;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(Constants.Dir.getLocalDir(context) + "/ipfsNode");
            byte[] bytes = new byte[1024];
            int size = 0;
            while ((size = open.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, size);
            }
            open.close();
            fileOutputStream.close();

            Process exec = Runtime.getRuntime().exec("chmod 777 " + Constants.Dir.getLocalDir(context) + "/ipfsNode");
            try {
                exec.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public Process command(String cmd) throws IOException {
        String[] envp = new String[1];
        envp[0] = "IPFS_PATH=" + Constants.Dir.getSDdir(context) + "/.ipfsNode";
        String command = Constants.Dir.getLocalDir(context) + "/ipfsNode " + cmd;
        Process exec = Runtime.getRuntime().exec(command, envp);
        return exec;
    }

    private String getProcessId(Process process) {
        long pid = -1;
        Field field = null;

        try {
            Class<?> clazz = Class.forName("java.lang.UNIXProcess");
            field = clazz.getDeclaredField("pid");
            field.setAccessible(true);
            pid = (Integer) field.get(process);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(pid);
    }

    private void savePID(String pid) {
        try {
            File file = new File(Constants.Dir.getLocalDir(context) + "/.pid");
            BufferedWriter write = new BufferedWriter(new FileWriter(file));
            write.write(pid);
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}