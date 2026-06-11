package com.movie.api.support;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CaptchaStore {

    private static final long TTL_MS = 2 * 60 * 1000L;

    private static final class Entry {
        final String code;
        final long expireAt;

        Entry(String code, long expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    public String put(String code) {
        String key = UUID.randomUUID().toString();
        store.put(key, new Entry(code, System.currentTimeMillis() + TTL_MS));
        return key;
    }

    public boolean verifyAndRemove(String key, String input) {
        if (key == null || input == null) {
            return false;
        }
        Entry entry = store.get(key);
        if (entry == null || System.currentTimeMillis() > entry.expireAt) {
            if (entry != null) {
                store.remove(key);
            }
            return false;
        }
        boolean ok = entry.code.equals(input.trim());
        if (ok) {
            store.remove(key);
        }
        return ok;
    }
}
