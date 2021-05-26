package net.vanderkast.fs4r.service.configuration;

public class Profiles {
    public static final String CONCURRENT_SESSIONS = "concurrent-sessions";
    private static final String NOT = "!";
    public static final String NOT_CONCURRENT_SESSIONS = NOT + CONCURRENT_SESSIONS;
    private Profiles() {
    }
}
