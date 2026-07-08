package com.smartpark.models;

public abstract class Person {
    protected int userId;
    protected String name;
    protected String contactNumber;

    public Person(int userId, String name, String contactNumber) {
        this.userId = userId;
        this.name = name;
        this.contactNumber = contactNumber;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}