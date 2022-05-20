package com.fjm.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Author: fongjinming
 * @CreateTime: 2019-09-17 16:57
 * @Description:异常工具
 */
public class ThrowableUtil {
    /**
     * 获取堆栈信息
     *
     * @param throwable
     * @return
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    /**
     * 获取堆栈信息
     *
     * @param throwable
     * @param isDebug
     * @return
     */
    public static String getStackTrace(Throwable throwable, boolean isDebug) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            if (isDebug) {
                throwable.printStackTrace(pw);
            }
            return sw.toString();
        } finally {
            pw.close();
        }
    }
}
