package com.example.demo.entity.sass;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "z_phones_copy")
@Entity
@Data
public class ZPhonesCopyPO implements Serializable {

    private static final long serialVersionUID = 7506645871089160291L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String user_phone;

    private String phone_numbers;



}