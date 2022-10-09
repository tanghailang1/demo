package com.example.demo.entity.sass;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Table(name = "z_statistical_data")
@Entity
@Data
public class StatisticalData {

    private static final long serialVersionUID = 7506645871089160291L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String mobile_phone;

    private String name;

    private Date last_apply_time;

    private Date last_actual_time;
}
