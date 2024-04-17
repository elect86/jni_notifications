package com.zoffcc.applications.jninotifications;

import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.lang.management.ManagementFactory;

import static com.zoffcc.applications.jninotifications.NTFYActivity.*;

public class Test {

    public static void main(String[] args) {
        MemoryUtil.memAlloc(1);
        System.out.println(ManagementFactory.getRuntimeMXBean().getClassPath());
        int loadjni_res = -1;
        try {
//            loadjni_res = jninotifications_loadjni(new java.io.File(".").getAbsolutePath());
        } catch (Exception e) {
        }
        NTFYActivity.initialize();

        File icon_file = new File("./icon-linux.png");
        String icon_path = icon_file.getAbsolutePath();

        Log.i(TAG, "jninotifications version: " + jninotifications_version());

        final int s = 100;

        jninotifications_notify("test application",
                "title", "message",
                icon_path);
        try {
            Thread.sleep(s);
        } catch (Exception e) {
        }

        jninotifications_notify(null,
                null, null,
                null);
        try {
            Thread.sleep(s);
        } catch (Exception e) {
        }

        jninotifications_notify(null,
                "1b", "1c",
                "1d");
        try {
            Thread.sleep(s);
        } catch (Exception e) {
        }

        jninotifications_notify("2a",
                null, "2c",
                "2d");
        try {
            Thread.sleep(s);
        } catch (Exception e) {
        }

        jninotifications_notify("3a",
                "3b", null,
                "3d");
        try {
            Thread.sleep(s);
        } catch (Exception e) {
        }

        jninotifications_notify("4a",
                "4b", "4c",
                null);
        try {
            Thread.sleep(s);
        } catch (Exception e) {
        }

        jninotifications_notify("हिन्दी",
                "हिन्दी", "हिन्दी",
                icon_path);
        try {
            Thread.sleep(s);
        } catch (Exception e) {
        }

        jninotifications_notify("iconpathhindi",
                "हिन्दी", "हिन्दी",
                "हिन्दी");


        // HINT: it should only show 4 notifications (and also not crash)
    }
}
