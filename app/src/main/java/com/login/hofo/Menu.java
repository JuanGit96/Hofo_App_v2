package com.login.hofo;


public class Menu {

    private int id;
    private String name;
    private String description;
    private String price;
    private String sale_price;
    private String photo;
    private String type_menu;
    private int chef_id;


    public Menu() {
    }

    public Menu(int id, String name, String description, String price, String sale_price, String photo, String type_menu, int chef_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sale_price = sale_price;
        this.photo = photo;
        this.type_menu = type_menu;
        this.chef_id = chef_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType_menu() {
        return type_menu;
    }

    public void setType_menu(String type_menu) {
        this.type_menu = type_menu;
    }

    public int getChef_id() {
        return chef_id;
    }

    public void setChef_id(int chef_id) {
        this.chef_id = chef_id;
    }


}
