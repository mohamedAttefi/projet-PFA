package com.company.workorders.model;

/**
 * Client domain model
 */
public class Client {
    private long id;
    private String companyName;
    private String phone;
    private String address;
    private String email;

    public Client(long id, String companyName, String phone, String address, String email) {
        this.id = id;
        this.companyName = companyName;
        this.phone = phone;
        this.address = address;
        this.email = email;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return companyName;
    }
}
