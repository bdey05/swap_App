package com.example.swapapp;


import java.util.ArrayList;

public class Chat {
    private String message;
    private String username;
    private String time;

    public Chat(){}

    public Chat(String message, String username, String time){
        this.message = message;
        this.username = username;
        this.time = time;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String messsage) {
        this.message = messsage;
    }

    //private static int lastContactId = 0;
    public static ArrayList<Chat> createContactsList(int numContacts) {
        ArrayList<Chat> contacts = new ArrayList<Chat>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new Chat("Person", "Test", "7:18"));
        }
        return contacts;
    }
}