package com.example.demo.entity.risk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "z_test_task2")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestTask2Po {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer age;


}
