package com.vaccine.mapper;

import com.vaccine.entity.VaccineCategory;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 疫苗分类Mapper
 */
@Mapper
public interface VaccineCategoryMapper {

    @Select("SELECT * FROM vaccine_category WHERE id = #{id}")
    VaccineCategory findById(Long id);

    @Insert("INSERT INTO vaccine_category(name, description, sort, status) " +
            "VALUES(#{name}, #{description}, #{sort}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VaccineCategory category);

    int update(VaccineCategory category);

    @Delete("DELETE FROM vaccine_category WHERE id = #{id}")
    int deleteById(Long id);

    List<VaccineCategory> findList(VaccineCategory category);

    Long count(VaccineCategory category);

    @Select("SELECT * FROM vaccine_category WHERE status = 1 ORDER BY sort ASC")
    List<VaccineCategory> findAllEnabled();
}
