package com.happyiterating.util;

import android.util.Log;

/**
 * Created by guowei on 10/01/2017.
 */

public class GWLog {

    public static final int LEVEL_VERBOSE = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARNING = 3;
    public static final int LEVEL_ERROR = 4;

    public static int sLogLevel = 2;

    public static void setLogLevel(int var0) {
        sLogLevel = var0;
    }

    public static void v(String var0, String var1, Object... var2) {
        if(0 >= sLogLevel) {
            v(var0, String.format(var1, var2));
        }
    }

    public static void d(String var0, String var1, Object... var2) {
        if(1 >= sLogLevel) {
            d(var0, String.format(var1, var2));
        }
    }

    public static void i(String var0, String var1, Object... var2) {
        if(2 >= sLogLevel) {
            i(var0, String.format(var1, var2));
        }
    }

    public static void w(String var0, String var1, Object... var2) {
        if(3 >= sLogLevel) {
            w(var0, String.format(var1, var2));
        }
    }

    public static void e(String var0, String var1, Object... var2) {
        if(4 >= sLogLevel) {
            e(var0, String.format(var1, var2));
        }
    }

    public static void v(String var0, String var1) {
        if(0 >= sLogLevel) {
           Log.v(var0, var1);
        }
    }

    public static void d(String var0, String var1) {
        if(1 >= sLogLevel) {
            Log.d(var0, var1);
        }
    }

    public static void i(String var0, String var1) {
        if(2 >= sLogLevel) {
            Log.i(var0, var1);
        }
    }

    public static void w(String var0, String var1) {
        if(3 >= sLogLevel) {
            Log.w(var0, var1);
        }
    }

    public static void e(String var0, String var1) {
        if(4 >= sLogLevel) {
            Log.e(var0, var1);
        }
    }
}
