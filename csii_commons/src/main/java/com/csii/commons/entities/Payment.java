package com.csii.commons.entities;


import lombok.Data;

@Data
public class Payment {
    private Long id;
    private String serial;

    public Payment() {

    }

    public Payment(Long id, String serial) {
        this.id = id;
        this.serial = serial;
    }

}
