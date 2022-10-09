package com.example.demo.dao.sass;

import com.example.demo.entity.sass.LoanOrderPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoanOrderDao extends JpaRepository<LoanOrderPO, String> {

    @Query(nativeQuery = true, value = "select * from loan_order where id =?1 ")
    LoanOrderPO findOrderId(String orderId);

}
