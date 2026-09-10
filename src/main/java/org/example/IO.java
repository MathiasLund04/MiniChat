package org.example;

public final class IO {
    private IO() {
    }

    public static void print(String text) {
        if (text == null) {
            return;
        }
        System.out.print(text);
    }

    public static void println(String text) {
        System.out.println(text);
    }
}
