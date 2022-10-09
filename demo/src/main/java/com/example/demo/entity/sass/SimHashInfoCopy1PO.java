package com.example.demo.entity.sass;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "z_simhash_info_copy1")
@Entity
@Data
public class SimHashInfoCopy1PO implements Serializable {

    private static final long serialVersionUID = 7506645871089160291L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String mobile;

    private String hash_code;

    private Integer bad_sign;

    private String county;

    private Date create_time;

}