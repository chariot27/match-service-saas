package br.ars.match_service.util;

import java.util.UUID;

public final class PairKeyUtil {
    private PairKeyUtil() {}

    public static UUID low(UUID a, UUID b) { return a.compareTo(b) <= 0 ? a : b; }
    public static UUID high(UUID a, UUID b) { return a.compareTo(b) > 0 ? a : b; }
}
