package com.example.demo.service.impl;

import com.example.demo.dao.risk.TestTask2Dao;
import com.example.demo.entity.risk.TestTask2Po;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService2 {


    @Autowired
    private TestTask2Dao testTaskDao2;


    //@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void addTask2() {
        TestTask2Po taskPo2 = TestTask2Po.builder().age(12).name("lisi").build();
        testTaskDao2.save(taskPo2);
        int i = 1/0;
    }
}
