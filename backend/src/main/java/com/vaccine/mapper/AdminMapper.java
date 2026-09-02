package com.vaccine.mapper;

import com.vaccine.entity.Admin;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 管理员Mapper
 */
@Mapper
public interface AdminMapper {

    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin findById(Long id);

    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin findByUsername(String username);

    @Insert("INSERT INTO admin(username, password, real_name, phone, email, avatar, role, community_id, status) " +
            "VALUES(#{username}, #{password}, #{realName}, #{phone}, #{email}, #{avatar}, #{role}, #{communityId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);

    int update(Admin admin);

    @Delete("DELETE FROM admin WHERE id = #{id}")
    int deleteById(Long id);

    List<Admin> findList(Admin admin);

    Long count(Admin admin);

    @Update("UPDATE admin SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 统计管理员数量
     */
    @Select("SELECT COUNT(*) FROM admin")
    Long countAdmins();

    /**
     * 统计社区管理员数量
     */
    @Select("SELECT COUNT(*) FROM admin WHERE role = 'community_admin'")
    Long countCommunityAdmins();
}
