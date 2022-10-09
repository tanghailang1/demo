package com.example.demo.dao.sass;

import com.example.demo.entity.sass.LoanOrderPO;
import com.example.demo.entity.sass.ZPhonesPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ZPhonesDao extends JpaRepository<ZPhonesPO, Integer> {
    @Query(nativeQuery = true, value = "select phone_numbers from z_phones1 where user_phone =?1 ")
    List<String> findPhones(String userPhone);



    @Query(nativeQuery = true, value = "SELECT  user_phone FROM  z_phones1 GROUP BY user_phone")
    List<String> groupByUserPhone();

}
