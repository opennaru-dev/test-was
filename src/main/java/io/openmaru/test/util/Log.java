package io.openmaru.test.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");

    public static void info(String value) {
        System.out.println("[" + sdf.format(new Date()) + "][" + Thread.currentThread().getName() + "] " + value);
    }
}
