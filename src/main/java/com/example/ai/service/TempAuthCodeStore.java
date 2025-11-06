package com.example.ai.service;

import com.example.ai.dto.response.AuthData;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TempAuthCodeStore {

    private final Map<String, StoredAuth> store = new ConcurrentHashMap<>();

    public void save(String code, AuthData data) {
        store.put(code, new StoredAuth(data, Instant.now().plusSeconds(120))); // 2 min expiry
    }

    public AuthData get(String code) {
        StoredAuth s = store.get(code);
        if (s == null || Instant.now().isAfter(s.expiry())) {
            store.remove(code);
            return null;
        }
        return s.data();
    }

    public void delete(String code) {
        store.remove(code);
    }

    private record StoredAuth(AuthData data, Instant expiry) {}
}
