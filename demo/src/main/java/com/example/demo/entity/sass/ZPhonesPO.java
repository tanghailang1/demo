package com.example.demo.entity.sass;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "z_phones1")
@Entity
@Data
public class ZPhonesPO implements Serializable {

    private static final long serialVersionUID = 7506645871089160291L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String user_phone;

    private String phone_numbers;

}