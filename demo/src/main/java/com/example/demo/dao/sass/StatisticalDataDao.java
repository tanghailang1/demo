package com.example.demo.dao.sass;

import com.example.demo.entity.sass.StatisticalData;
import com.example.demo.entity.sass.ZPhonesPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatisticalDataDao extends JpaRepository<StatisticalData, Integer> {
}
