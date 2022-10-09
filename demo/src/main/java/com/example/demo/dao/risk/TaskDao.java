package com.example.demo.dao.risk;

import com.example.demo.entity.risk.RiskTaskPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskDao extends JpaRepository<RiskTaskPo, String> {

    @Query(nativeQuery = true, value = "select * from t_risk_task where out_order_id =?1 ")
    RiskTaskPo findId(String orderId);
}
