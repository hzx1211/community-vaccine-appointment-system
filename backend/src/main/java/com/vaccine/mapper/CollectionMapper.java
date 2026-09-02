package com.vaccine.mapper;

import com.vaccine.entity.UserCollection;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 收藏Mapper
 */
@Mapper
public interface CollectionMapper {

    @Select("SELECT c.*, v.name as vaccine_name, v.image as vaccine_image, v.price as vaccine_price " +
            "FROM collection c LEFT JOIN vaccine v ON c.vaccine_id = v.id WHERE c.id = #{id}")
    UserCollection findById(Long id);

    @Insert("INSERT INTO collection(user_id, vaccine_id) VALUES(#{userId}, #{vaccineId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserCollection collection);

    @Delete("DELETE FROM collection WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM collection WHERE user_id = #{userId} AND vaccine_id = #{vaccineId}")
    int deleteByUserAndVaccine(@Param("userId") Long userId, @Param("vaccineId") Long vaccineId);

    @Select("SELECT c.*, v.name as vaccine_name, v.image as vaccine_image, v.price as vaccine_price " +
            "FROM collection c LEFT JOIN vaccine v ON c.vaccine_id = v.id " +
            "WHERE c.user_id = #{userId} ORDER BY c.create_time DESC")
    List<UserCollection> findByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM collection WHERE user_id = #{userId} AND vaccine_id = #{vaccineId}")
    int countByUserAndVaccine(@Param("userId") Long userId, @Param("vaccineId") Long vaccineId);

    List<UserCollection> findList(UserCollection collection);

    Long count(UserCollection collection);
}
