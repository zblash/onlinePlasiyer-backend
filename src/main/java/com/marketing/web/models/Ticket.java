package com.marketing.web.models;

import lombok.*;

import javax.persistence.*;


public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String message;


}
