package com.vaccine.mapper;

import com.vaccine.entity.Banner;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 轮播图Mapper
 */
@Mapper
public interface BannerMapper {

    @Select("SELECT * FROM banner WHERE id = #{id}")
    Banner findById(Long id);

    @Insert("INSERT INTO banner(title, image, link, sort, status) " +
            "VALUES(#{title}, #{image}, #{link}, #{sort}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Banner banner);

    int update(Banner banner);

    @Delete("DELETE FROM banner WHERE id = #{id}")
    int deleteById(Long id);

    List<Banner> findList(Banner banner);

    Long count(Banner banner);

    @Select("SELECT * FROM banner WHERE status = 1 ORDER BY sort ASC")
    List<Banner> findAllEnabled();
}
