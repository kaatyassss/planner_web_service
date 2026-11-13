package com.kaatyassss.planner.web.util;

// Экранирование текста для существующих HTML-страниц
public final class HttpUtil {

    private HttpUtil() {}

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
