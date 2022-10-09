package com.example.demo.dao.sass;

import com.example.demo.entity.sass.SimHashInfoPO;
import com.example.demo.entity.sass.ZPhonesPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SimHashInfoDao extends JpaRepository<SimHashInfoPO, Integer> {


    @Query(nativeQuery = true, value = "select * from z_simhash_info where mobile =?1 ")
    List<SimHashInfoPO> findSimHashInfo(String mobile);


    @Query(nativeQuery = true, value = "select * from z_simhash_info where hash_code =?1 ")
    List<SimHashInfoPO> findSimHashInfoPOByHash_code(String hash_code);


    @Query(nativeQuery = true, value = "select hash_code from z_simhash_info where hash_code != ?1 ")
    List<String> findHashCode(String hashCode);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update z_simhash_info set hanming_distance = ?1, scale=?2, number=?3 where mobile = ?4 ")
    int updateDistance(Integer hanmingDistance,String scale,Integer number,String mobile);


    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update z_simhash_info set bili=?1, mobile_length = ?2 where mobile = ?3 ")
    int updateBiLi(String scale,String length,String mobile);


    @Query(nativeQuery = true, value = "select * from z_simhash_info where bili is null ")
    List<SimHashInfoPO> findBillNull();

}
