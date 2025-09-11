package org.example.gestion.Login;

public class Session {
    private static User current;
    public static void set(User u) { current = u; }
    public static User get() { return current; }
    public static boolean isAuthenticated() { return current != null; }
    public static void clear() { current = null; }
}