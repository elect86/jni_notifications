package com.zoffcc.applications.jninotifications;

public class NTFYActivity {
    static final String TAG = "NTFYActivity";
    static final String Version = "0.99.2";

    public static native String jninotifications_version();

    public static native int jninotifications_notify(String application, String title,
                                                     String message, String icon_filename_fullpath);

    /**
     * Utility class to allow OS determination
     * <p>
     * Created on Mar 11, 2010
     *
     * @author Eugene Ryzhikov
     */
    public enum OperatingSystem {

        WINDOWS("windows"), MACOS("mac"), MACARM("silicone"), RASPI("aarm64"), LINUX("linux"), UNIX("nix"), SOLARIS("solaris"),

        UNKNOWN("unknown") {
            @Override
            protected boolean isReal() {
                return false;
            }
        };

        private final String tag;

        OperatingSystem(String tag) {
            this.tag = tag;
        }

        public boolean isCurrent() {
            return isReal() && getName().toLowerCase().contains(tag);
        }

        public static String getName() {
            return System.getProperty("os.name");
        }

        public static String getVersion() {
            return System.getProperty("os.version");
        }

        public static String getArchitecture() {
            return System.getProperty("os.arch");
        }

        @Override
        public final String toString() {
            return String.format("%s v%s (%s)", getName(), getVersion(), getArchitecture());
        }

        protected boolean isReal() {
            return true;
        }

        /**
         * Returns current operating system
         *
         * @return current operating system or UNKNOWN if not found
         */
        public static OperatingSystem getCurrent() {
            for (OperatingSystem os : OperatingSystem.values()) {
                if (os.isCurrent()) {
                    if (os == OperatingSystem.MACOS) {
                        if (getArchitecture().equalsIgnoreCase("aarch64")) {
                            return OperatingSystem.MACARM;
                        }
                    } else if (os == OperatingSystem.LINUX) {
                        if (getArchitecture().equalsIgnoreCase("aarch64")) {
                            return OperatingSystem.RASPI;
                        }
                    }
                    return os;
                }
            }
            return UNKNOWN;
        }
    }

    public static int jninotifications_loadjni(String jnilib_path) {
        String linux_lib_filename;
        if (OperatingSystem.getCurrent() == OperatingSystem.LINUX) {
            linux_lib_filename = jnilib_path + "/libjni_notifications.so";
        } else if (OperatingSystem.getCurrent() == OperatingSystem.RASPI) {
            linux_lib_filename = jnilib_path + "/libjni_notifications_raspi.so";
        } else if (OperatingSystem.getCurrent() == OperatingSystem.WINDOWS) {
            linux_lib_filename = jnilib_path + "/jni_notificationsi.dll";
        } else if (OperatingSystem.getCurrent() == OperatingSystem.MACOS) {
            linux_lib_filename = jnilib_path + "/libjni_notifications.jnilib";
        } else if (OperatingSystem.getCurrent() == OperatingSystem.MACARM) {
            linux_lib_filename = jnilib_path + "/libjni_notifications_arm64.jnilib";
        } else {
            Log.i(TAG, "OS:Unknown operating system: " + OperatingSystem.getCurrent());
            return -1;
        }

        try {
            System.load(linux_lib_filename);
            Log.i(TAG, "successfully loaded native library path: " + linux_lib_filename);
            return 0;
        } catch (java.lang.UnsatisfiedLinkError e) {
            Log.i(TAG, "loadLibrary libjni_notifications failed! path: " + linux_lib_filename);
            e.printStackTrace();
            return -1;
        }
    }
}