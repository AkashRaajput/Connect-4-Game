package com.akash.connectfour;

/**
 * Entry point for packaged distributions (jpackage).
 * Do not use {@link Main} directly as the packaged main class when Main extends Application.
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        Main.main(args);
    }
}
