package com.vaccine.mapper;

import com.vaccine.entity.Vaccine;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 疫苗Mapper
 */
@Mapper
public interface VaccineMapper {

    @Select("SELECT v.*, c.name as category_name FROM vaccine v " +
            "LEFT JOIN vaccine_category c ON v.category_id = c.id WHERE v.id = #{id}")
    Vaccine findById(Long id);

    @Insert("INSERT INTO vaccine(name, category_id, manufacturer, price, stock, doses, interval_days, " +
            "target_group, contraindication, description, image, appointment_count, status) " +
            "VALUES(#{name}, #{categoryId}, #{manufacturer}, #{price}, #{stock}, #{doses}, #{intervalDays}, " +
            "#{targetGroup}, #{contraindication}, #{description}, #{image}, #{appointmentCount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Vaccine vaccine);

    int update(Vaccine vaccine);

    @Delete("DELETE FROM vaccine WHERE id = #{id}")
    int deleteById(Long id);

    List<Vaccine> findList(Vaccine vaccine);

    Long count(Vaccine vaccine);

    /**
     * 减少库存（乐观锁，保证原子性）
     */
    @Update("UPDATE vaccine SET stock = stock - 1 WHERE id = #{id} AND stock > 0")
    int decreaseStock(Long id);

    /**
     * 增加库存
     */
    @Update("UPDATE vaccine SET stock = stock + 1 WHERE id = #{id}")
    int increaseStock(Long id);

    /**
     * 增加预约计数
     */
    @Update("UPDATE vaccine SET appointment_count = appointment_count + 1 WHERE id = #{id}")
    int increaseAppointmentCount(Long id);

    /**
     * 减少预约计数
     */
    @Update("UPDATE vaccine SET appointment_count = GREATEST(0, appointment_count - 1) WHERE id = #{id}")
    int decreaseAppointmentCount(Long id);

    /**
     * 统计疫苗种类数量
     */
    @Select("SELECT COUNT(*) FROM vaccine WHERE status = 1")
    Long countActive();

    @Select("SELECT v.*, c.name as category_name FROM vaccine v " +
            "LEFT JOIN vaccine_category c ON v.category_id = c.id " +
            "WHERE v.status = 1 ORDER BY v.appointment_count DESC LIMIT #{limit}")
    List<Vaccine> findHotVaccines(int limit);
}
