package com.login.hofo;

class User {

    private static boolean chef;

    public static boolean isChef() {
        return chef;
    }

    public static void setChef(boolean chef) {
        User.chef = chef;
    }
}
