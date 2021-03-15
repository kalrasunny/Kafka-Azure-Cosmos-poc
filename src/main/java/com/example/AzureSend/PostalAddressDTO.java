package com.example.AzureSend;

public class PostalAddressDTO {


    public PostalAddressDTO(String name, String email,Integer userid) {
        this.name = name;
        this.email = email;
        this.userid=userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    String email;
    String name;
    Integer userid;
}
