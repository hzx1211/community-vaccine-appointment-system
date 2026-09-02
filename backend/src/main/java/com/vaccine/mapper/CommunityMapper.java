package com.vaccine.mapper;

import com.vaccine.entity.Community;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 社区Mapper
 */
@Mapper
public interface CommunityMapper {

    @Select("SELECT c.*, a.username as admin_username, a.real_name as admin_real_name " +
            "FROM community c " +
            "LEFT JOIN admin a ON a.community_id = c.id AND a.role = 'community_admin' " +
            "WHERE c.id = #{id}")
    Community findById(Long id);

    @Insert("INSERT INTO community(name, address, contact_phone, description, image, status) " +
            "VALUES(#{name}, #{address}, #{contactPhone}, #{description}, #{image}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Community community);

    int update(Community community);

    @Delete("DELETE FROM community WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 批量删除社区
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    List<Community> findList(Community community);

    Long count(Community community);

    @Select("SELECT * FROM community WHERE status = 1 ORDER BY create_time DESC")
    List<Community> findAllEnabled();
}
