package com.vaccine.mapper;

import com.vaccine.entity.News;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 资讯Mapper
 */
@Mapper
public interface NewsMapper {

    @Select("SELECT n.*, c.name as community_name, u.username as author_name " +
            "FROM news n " +
            "LEFT JOIN community c ON n.community_id = c.id " +
            "LEFT JOIN user u ON n.author_id = u.id " +
            "WHERE n.id = #{id}")
    News findById(Long id);

    @Insert("INSERT INTO news(title, summary, content, cover_image, community_id, author_id, view_count, status) " +
            "VALUES(#{title}, #{summary}, #{content}, #{coverImage}, #{communityId}, #{authorId}, #{viewCount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(News news);

    int update(News news);

    @Delete("DELETE FROM news WHERE id = #{id}")
    int deleteById(Long id);

    List<News> findList(News news);

    Long count(News news);

    @Update("UPDATE news SET view_count = view_count + 1 WHERE id = #{id}")
    int increaseViewCount(Long id);

    @Select("SELECT n.*, c.name as community_name FROM news n " +
            "LEFT JOIN community c ON n.community_id = c.id " +
            "WHERE n.status = 1 ORDER BY n.create_time DESC LIMIT #{limit}")
    List<News> findLatest(int limit);

    @Select("SELECT n.*, c.name as community_name FROM news n " +
            "LEFT JOIN community c ON n.community_id = c.id " +
            "WHERE n.status = 1 ORDER BY n.view_count DESC LIMIT 1")
    News findTopByViewCount();
}
