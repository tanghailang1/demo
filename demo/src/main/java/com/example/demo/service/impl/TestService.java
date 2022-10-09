package com.example.demo.service.impl;

import com.example.demo.dao.risk.TestTask2Dao;
import com.example.demo.dao.risk.TestTaskDao;
import com.example.demo.entity.risk.TestTask2Po;
import com.example.demo.entity.risk.TestTaskPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    @Autowired
    private TestTaskDao testTaskDao;

    @Autowired
    private TestTask2Dao testTaskDao2;

    @Autowired
    private TestService2 testService2;

    @Transactional(propagation = Propagation.MANDATORY,rollbackFor = Exception.class)
    public void addTask() {
        TestTaskPo taskPo = TestTaskPo.builder().age(10).name("zhangsan").build();
        testTaskDao.save(taskPo);

//        try {
//            testService2.addTask2();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        //int i = 1/0;
        testService2.addTask2();
        //addTask2();
        //int i1 = 1/0;
    }





    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void addTask2() {
        TestTask2Po taskPo2 = TestTask2Po.builder().age(12).name("lisi").build();
        testTaskDao2.save(taskPo2);
        int i = 1/0;
    }
}
