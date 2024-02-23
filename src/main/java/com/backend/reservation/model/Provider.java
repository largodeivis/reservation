package com.backend.reservation.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    private String name;

    public Provider(){

    }

    public Provider(String name){
        this.name = name;
    }
}
