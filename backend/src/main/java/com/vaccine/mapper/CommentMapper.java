package com.vaccine.mapper;

import com.vaccine.entity.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 评论Mapper
 */
@Mapper
public interface CommentMapper {

    @Select("SELECT c.*, u.username, u.avatar as user_avatar " +
            "FROM comment c LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.id = #{id}")
    Comment findById(Long id);

    @Insert("INSERT INTO comment(news_id, user_id, content, parent_id, status) " +
            "VALUES(#{newsId}, #{userId}, #{content}, #{parentId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Update("UPDATE comment SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM comment WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT c.*, u.username, u.avatar as user_avatar, " +
            "pu.username as reply_username " +
            "FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN comment pc ON c.parent_id = pc.id " +
            "LEFT JOIN user pu ON pc.user_id = pu.id " +
            "WHERE c.news_id = #{newsId} AND c.status = 1 " +
            "ORDER BY c.create_time ASC")
    List<Comment> findByNewsId(Long newsId);

    @Select("SELECT c.*, u.username, u.avatar as user_avatar, n.title as news_title " +
            "FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN news n ON c.news_id = n.id " +
            "ORDER BY c.create_time DESC")
    List<Comment> findAll();

    @Select("SELECT COUNT(*) FROM comment WHERE news_id = #{newsId} AND status = 1")
    int countByNewsId(Long newsId);
}
