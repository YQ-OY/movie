package com.movie.api.support;

import com.movie.api.utils.ValidationUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ForgotCodeStore {

    public static final String DEMO_SMS_CODE = "123456";

    private static final long TTL_MS = 5 * 60 * 1000L;

    private static final class Entry {
        final String code;
        final long expireAt;

        Entry(String code, long expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    public void put(String accountType, String phone) {
        String normalizedPhone = ValidationUtil.normalizePhone(phone);
        if (normalizedPhone == null) {
            return;
        }
        store.put(key(accountType, normalizedPhone), new Entry(DEMO_SMS_CODE, System.currentTimeMillis() + TTL_MS));
    }

    public boolean matches(String accountType, String phone, String input) {
        if (input == null) {
            return false;
        }
        String normalizedPhone = ValidationUtil.normalizePhone(phone);
        if (normalizedPhone == null) {
            return false;
        }
        Entry entry = store.get(key(accountType, normalizedPhone));
        if (entry == null || System.currentTimeMillis() > entry.expireAt) {
            if (entry != null) {
                store.remove(key(accountType, normalizedPhone));
            }
            return false;
        }
        return entry.code.equals(input.trim());
    }

    public void remove(String accountType, String phone) {
        String normalizedPhone = ValidationUtil.normalizePhone(phone);
        if (normalizedPhone != null) {
            store.remove(key(accountType, normalizedPhone));
        }
    }

    private static String key(String accountType, String phone) {
        return accountType + ":" + ValidationUtil.normalizePhone(phone);
    }
}
