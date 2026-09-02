package com.vaccine.mapper;

import com.vaccine.entity.RechargeRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 充值记录Mapper
 */
@Mapper
public interface RechargeRecordMapper {

    @Select("SELECT r.*, u.username FROM recharge_record r " +
            "LEFT JOIN user u ON r.user_id = u.id WHERE r.id = #{id}")
    RechargeRecord findById(Long id);

    @Insert("INSERT INTO recharge_record(user_id, amount, before_balance, after_balance, pay_method, status) " +
            "VALUES(#{userId}, #{amount}, #{beforeBalance}, #{afterBalance}, #{payMethod}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RechargeRecord record);

    @Select("SELECT r.*, u.username FROM recharge_record r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "WHERE r.user_id = #{userId} ORDER BY r.create_time DESC")
    List<RechargeRecord> findByUserId(Long userId);

    List<RechargeRecord> findList(RechargeRecord record);

    Long count(RechargeRecord record);
}
