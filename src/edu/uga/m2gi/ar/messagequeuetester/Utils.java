package edu.uga.m2gi.ar.messagequeuetester;

public class Utils {
    private static Integer CURRENT_CLIENT_ID = Constants.STARTING_CLIENT_ID;

    public static String idToName(Integer id) {
        return String.valueOf(id);
    }

    public static synchronized Integer nextClientId() {
        return CURRENT_CLIENT_ID++;
    }
}
