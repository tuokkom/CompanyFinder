package com.tuokko.companyfinder;

public class Log {
    private final static String PACKAGE_NAME = "CompanyFinder";

    public static void d(String className, String methodName, String msg) {
        android.util.Log.d(PACKAGE_NAME, className + ": " + methodName + ": " + msg);
    }
}
