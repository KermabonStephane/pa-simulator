package fr.pa.simulator.entreprise;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Génération et hachage des clés API (utilitaire interne au module).
 */
final class ApiKeys {

    private static final String PREFIX = "pa_";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private ApiKeys() {
    }

    /** Génère une nouvelle clé API en clair (à ne montrer qu'une fois). */
    static String generate() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return PREFIX + ENCODER.encodeToString(bytes);
    }

    /** Hash SHA-256 (hex) d'une clé, utilisé pour le stockage et la comparaison. */
    static String hash(String apiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] out = digest.digest(apiKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(out);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible", e);
        }
    }
}
