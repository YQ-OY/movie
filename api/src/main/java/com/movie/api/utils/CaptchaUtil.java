package com.movie.api.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class CaptchaUtil {

    private CaptchaUtil() {
    }

    public static String generateCode() {
        return String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000));
    }

    public static String renderSvg(String code) {
        Random random = new Random(code.hashCode());
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int x1 = random.nextInt(120);
            int y1 = random.nextInt(40);
            int x2 = random.nextInt(120);
            int y2 = random.nextInt(40);
            lines.append(String.format(
                    "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"#c0c4cc\" stroke-width=\"1\" opacity=\"0.6\"/>",
                    x1, y1, x2, y2));
        }
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            int x = 18 + i * 24 + random.nextInt(4);
            int y = 28 + random.nextInt(6);
            int rotate = random.nextInt(21) - 10;
            digits.append(String.format(
                    "<text x=\"%d\" y=\"%d\" fill=\"#303133\" font-size=\"22\" font-family=\"Arial,sans-serif\" font-weight=\"700\" transform=\"rotate(%d %d %d)\">%c</text>",
                    x, y, rotate, x, y, code.charAt(i)));
        }
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"120\" height=\"40\" viewBox=\"0 0 120 40\">"
                + "<rect width=\"120\" height=\"40\" rx=\"6\" fill=\"#f5f7fa\"/>"
                + lines
                + digits
                + "</svg>";
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        String p = phone.trim();
        return p.substring(0, 3) + "****" + p.substring(p.length() - 4);
    }
}
