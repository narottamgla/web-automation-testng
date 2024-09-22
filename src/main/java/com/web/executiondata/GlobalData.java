package com.web.executiondata;

import com.web.enums.Browser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobalData {

    public static String BS_USERNAME = System.getProperty("bs_user","");

    public static String BS_KEY = System.getProperty("bs_key","");

    public static boolean IS_REMOTE= Boolean.parseBoolean(System.getProperty("is_remote","false"));

    public static String EXECUTION_BROWSER = System.getProperty("browser", Browser.CHROME.name());

    private static final Map<String, Object> GLOBALDATA = new HashMap<>();

    public static Object getData(final String key) {
        AtomicReference<String> expression = new AtomicReference<>();
        String finalKey = key;
        if (key == null) {
            finalKey = "";
        } else if (key.contains("EXPR:")) {
            expression.set(key.split("EXPR:")[1]);
            finalKey = "EXPR:" + resolveData(expression).get();
        } else if (key.contains("LIST:")) {
            expression.set(key.split("LIST:")[1]);
            finalKey = "LIST:" + resolveData(expression).get();
        }
        String search =
                finalKey.contains("B:") ? finalKey : finalKey + "" + Thread.currentThread().getId();
        if (contains(search)) return GLOBALDATA.get(search);
        else return finalKey;
    }

    private static AtomicReference<String> resolveData(final AtomicReference<String> expression) {
        Map<Object, Object> stream =
                Stream.of(expression.get().split("[^A-Z_]"))
                        .collect(Collectors.toMap(k -> k, GlobalData::getData, (x, y) -> x));
        stream.forEach(
                (k, v) ->
                        expression.set(expression.get().replaceAll(String.valueOf(k), String.valueOf(v))));
        return expression;
    }

    public static void setData(final String key, final Object obj) {
        if (key.contains("B:")) GLOBALDATA.put(key, obj);
        else GLOBALDATA.put(key + "" + Thread.currentThread().getId(), obj);
    }

    public static boolean contains(final String key) {
        return GLOBALDATA.containsKey(key);
    }

    public static void clearGlobalData() {
        GLOBALDATA.clear();
    }


}
