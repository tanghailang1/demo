package com.example.demo.dao.sass;

import com.example.demo.entity.sass.SimHashInfoCopyPO;
import com.example.demo.entity.sass.SimHashInfoPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SimHashInfoCopyDao extends JpaRepository<SimHashInfoCopyPO, Integer> {


    @Query(nativeQuery = true, value = "select * from z_simhash_info_copy where mobile =?1 ")
    List<SimHashInfoCopyPO> findSimHashInfo(String mobile);


    @Query(nativeQuery = true, value = "select * from z_simhash_info_copy where hash_code =?1 ")
    List<SimHashInfoCopyPO> findSimHashInfoPOByHash_code(String hash_code);


    @Query(nativeQuery = true, value = "select hash_code from z_simhash_info_copy where hash_code != ?1 ")
    List<String> findHashCode(String hashCode);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update z_simhash_info_copy set hanming_distance = ?1, scale=?2, number=?3 ,number_list=?4 where mobile = ?5 ")
    int updateDistance(Integer hanmingDistance,String scale,Integer number,String numberList,String mobile);


    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update z_simhash_info_copy set bili=?1, mobile_length = ?2 where mobile = ?3 ")
    int updateBiLi(String scale,String mobileLength,String mobile);


    @Query(nativeQuery = true, value = "select * from z_simhash_info_copy where bili is null ")
    List<SimHashInfoCopyPO> findBillNull();


    @Query(nativeQuery = true, value = "select * from z_simhash_info_copy where hash_code = ?1 ")
    List<SimHashInfoCopyPO> findmobile(String hashCode);

}
