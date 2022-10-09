package com.example.demo.entity.sass;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "z_simhash_info_copy")
@Entity
@Data
public class SimHashInfoCopyPO implements Serializable {

    private static final long serialVersionUID = 7506645871089160291L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String mobile;

    private String hash_code;

    private Integer hanming_distance;

    private String scale;

    private String bili;

    private String mobile_length;

    private Integer number;

}