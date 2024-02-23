package com.backend.reservation.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    private String name;

    public Provider(){

    }

    public Provider(String name){
        this.name = name;
    }
}
