package com.login.hofo;

class Order {

    private static boolean order = false;

    private static int domiciliary = 3500;

    public static boolean orderInProcess() {
        return order;
    }

    public static void setOrder(boolean order) {
        Order.order = order;
    }

    public static int getDomiciliary() {
        return domiciliary;
    }

    public static void setDomiciliary(int domiciliary) {
        Order.domiciliary = domiciliary;
    }
}
