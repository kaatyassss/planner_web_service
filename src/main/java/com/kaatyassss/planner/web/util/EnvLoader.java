package com.kaatyassss.planner.web.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class EnvLoader {

    private static final Map<String, String> ENV = new HashMap<>();

    static {
        Path envPath = Path.of(".env");
        if (envPath.toFile().exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envPath.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int idx = line.indexOf('=');
                    if (idx > 0) {
                        String key = line.substring(0, idx).trim();
                        String value = line.substring(idx + 1).trim();
                        ENV.put(key, value);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Не удалось загрузить .env файл", e);
            }
        }
    }

    private EnvLoader() {}

    public static String get(String key) {
        String val = ENV.get(key);
        if (val == null) {
            val = System.getenv(key);
        }
        return val;
    }

    public static String get(String key, String defaultValue) {
        String val = get(key);
        return val != null ? val : defaultValue;
    }
}
