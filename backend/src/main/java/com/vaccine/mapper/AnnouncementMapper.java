package com.vaccine.mapper;

import com.vaccine.entity.Announcement;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 公告Mapper
 */
@Mapper
public interface AnnouncementMapper {

    @Select("SELECT * FROM announcement WHERE id = #{id}")
    Announcement findById(Long id);

    @Insert("INSERT INTO announcement(title, content, sort, status) " +
            "VALUES(#{title}, #{content}, #{sort}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);

    int update(Announcement announcement);

    @Delete("DELETE FROM announcement WHERE id = #{id}")
    int deleteById(Long id);

    List<Announcement> findList(Announcement announcement);

    Long count(Announcement announcement);

    @Select("SELECT * FROM announcement WHERE status = 1 ORDER BY sort ASC, create_time DESC")
    List<Announcement> findAllEnabled();
}
