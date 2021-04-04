/*
    Created by ArcyMods on 2021.04.04

    This class is responsible for getting
    the pid of your desired process name.
    The project supports every single android version from
    4.1 to 11.

    (if you have any problems with the project, please create an issue
    on the github page)
*/

package com.arc.pidtesting;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.topjohnwu.superuser.Shell;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            for (int i = 0; i < runningAppProcesses.size(); i++) {
                if (runningAppProcesses.get(i).processName.contains("com.your.processname")) {
                    pid = runningAppProcesses.get(i).pid;
                    Log.e("PID", String.valueOf(pid));
                }
            }
        } else {
            if (Shell.rootAccess()) {
                findPid("com.your.processname");
                Log.e("PID", String.valueOf(pid));
            } else {
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("Oops!")
                        .setMessage("Root was not found on your device. Please root your device or, enable root permissions.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton("Ok", (dialogInterface, i) -> System.exit(0))
                        .show();
            }
        }
    }

    public void findPid(String processName) {
        Shell.Result shellResult = Shell.su("pidof " + processName).exec();
        if (shellResult.isSuccess()) {
            for (int i = 0; i < shellResult.getOut().size(); i++) {
                try {
                    pid = Integer.parseInt(String.valueOf(shellResult.getOut().get(i)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}