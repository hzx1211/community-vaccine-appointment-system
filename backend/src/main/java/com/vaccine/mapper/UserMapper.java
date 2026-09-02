package com.vaccine.mapper;

import com.vaccine.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 普通用户Mapper
 */
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO user(username, password, real_name, phone, email, id_card, avatar, balance, status) " +
            "VALUES(#{username}, #{password}, #{realName}, #{phone}, #{email}, #{idCard}, #{avatar}, #{balance}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    int update(User user);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);

    List<User> findList(User user);

    Long count(User user);

    @Update("UPDATE user SET balance = #{balance} WHERE id = #{id}")
    int updateBalance(@Param("id") Long id, @Param("balance") java.math.BigDecimal balance);

    /**
     * 带乐观锁的余额更新（防止并发问题）
     */
    @Update("UPDATE user SET balance = #{newBalance} WHERE id = #{id} AND balance = #{oldBalance}")
    int updateBalanceWithCheck(@Param("id") Long id, 
                               @Param("oldBalance") java.math.BigDecimal oldBalance, 
                               @Param("newBalance") java.math.BigDecimal newBalance);

    @Update("UPDATE user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 统计用户数量
     */
    @Select("SELECT COUNT(*) FROM user")
    Long countUsers();

    /**
     * 按日期统计新注册用户数量（用户增长趋势）
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as count FROM user " +
            "WHERE create_time >= #{startDate} GROUP BY DATE(create_time) ORDER BY date")
    java.util.List<java.util.Map<String, Object>> countUsersByDate(String startDate);
}
