package com.login.hofo;

public class Solicitud {

    private String hour, address, additional_comments, nameMenu;

    private int id, amount_people, menu_id, diner_id, total_charge, type_order, type_pay;

    //para solicitudes agendadas
    private boolean isSchedule;

    private int modality, status;

    private String chance, food_type, fotoMenu;


    public Solicitud(String nameMenu, String fotoMenu, String hour, String address, String additional_comments,
                     int amount_people, int total_charge, int type_order,int type_pay, int menu_id,
                     int diner_id, boolean isSchedule, int modality, int status, String chance,
                     String food_type, int id) {

        this.nameMenu = nameMenu;
        this.fotoMenu = fotoMenu;
        this.hour = hour;
        this.address = address;
        this.amount_people = amount_people;
        this.total_charge = total_charge;
        this.type_order = type_order;
        this.type_pay = type_pay;
        this.additional_comments = additional_comments;
        this.menu_id = menu_id;
        this.diner_id = diner_id;
        this.isSchedule = isSchedule;
        this.modality = modality;
        this.status = status;
        this.chance = chance;
        this.food_type = food_type;
        this.id = id;
    }

    public String getFotoMenu() {
        return fotoMenu;
    }

    public void setFotoMenu(String fotoMenu) {
        this.fotoMenu = fotoMenu;
    }

    public String getNameMenu() {
        return nameMenu;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAdditional_comments() {
        return additional_comments;
    }

    public void setAdditional_comments(String additional_comments) {
        this.additional_comments = additional_comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount_people() {
        return amount_people;
    }

    public void setAmount_people(int amount_people) {
        this.amount_people = amount_people;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public int getDiner_id() {
        return diner_id;
    }

    public void setDiner_id(int diner_id) {
        this.diner_id = diner_id;
    }

    public int getTotal_charge() {
        return total_charge;
    }

    public void setTotal_charge(int total_charge) {
        this.total_charge = total_charge;
    }

    public int getType_order() {
        return type_order;
    }

    public void setType_order(int type_order) {
        this.type_order = type_order;
    }

    public int getType_pay() {
        return type_pay;
    }

    public void setType_pay(int type_pay) {
        this.type_pay = type_pay;
    }

    public void setNameMenu(String nameMenu) {
        this.nameMenu = nameMenu;
    }

    public boolean isSchedule() {
        return isSchedule;
    }

    public void setSchedule(boolean schedule) {
        isSchedule = schedule;
    }

    public int getModality() {
        return modality;
    }

    public void setModality(int modality) {
        this.modality = modality;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getChance() {
        return chance;
    }

    public void setChance(String chance) {
        this.chance = chance;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }
}
