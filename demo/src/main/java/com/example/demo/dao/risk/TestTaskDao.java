package com.example.demo.dao.risk;

import com.example.demo.entity.risk.TestTaskPo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestTaskDao extends JpaRepository<TestTaskPo, String> {

}
