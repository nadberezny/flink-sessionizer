package com.getindata.flink.sessionizer.util;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public final class HashingUtility {

    public static UUID fromStrings(List<String> strings) {
        return fromString(composeForHashing(strings));
    }

    private static UUID fromString(String string) {
        return fromBytes(string.getBytes(StandardCharsets.UTF_8));
    }

    private static UUID fromBytes(byte[] bytes) {
        return UUID.nameUUIDFromBytes(bytes);
    }

    private static String composeForHashing(List<String> strings) {
        String identity = "";
        if (strings == null) {
            return identity;
        }
        return strings
                .stream()
                .map(s -> s == null ? "" : s)
                .reduce((left, right) -> left + "+" + right)
                .orElse(identity);
    }
}
