package com.vaccine.mapper;

import com.vaccine.entity.Appointment;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 预约Mapper
 */
@Mapper
public interface AppointmentMapper {

    @Select("SELECT a.*, u.username, u.real_name, v.name as vaccine_name, c.name as community_name " +
            "FROM appointment a " +
            "LEFT JOIN user u ON a.user_id = u.id " +
            "LEFT JOIN vaccine v ON a.vaccine_id = v.id " +
            "LEFT JOIN community c ON a.community_id = c.id " +
            "WHERE a.id = #{id}")
    Appointment findById(Long id);

    @Insert("INSERT INTO appointment(order_no, user_id, vaccine_id, community_id, appointment_date, " +
            "time_slot, dose_number, amount, status, remark) " +
            "VALUES(#{orderNo}, #{userId}, #{vaccineId}, #{communityId}, #{appointmentDate}, " +
            "#{timeSlot}, #{doseNumber}, #{amount}, #{status}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Appointment appointment);

    int update(Appointment appointment);

    @Delete("DELETE FROM appointment WHERE id = #{id}")
    int deleteById(Long id);

    List<Appointment> findList(Appointment appointment); // 分页查询 列表

    Long count(Appointment appointment); // 统计

    @Select("SELECT a.*, u.username, u.real_name, v.name as vaccine_name, c.name as community_name " +
            "FROM appointment a " +
            "LEFT JOIN user u ON a.user_id = u.id " +
            "LEFT JOIN vaccine v ON a.vaccine_id = v.id " +
            "LEFT JOIN community c ON a.community_id = c.id " +
            "WHERE a.user_id = #{userId} ORDER BY a.create_time DESC")
    List<Appointment> findByUserId(Long userId);

    @Select("SELECT a.*, u.username, u.real_name, v.name as vaccine_name, c.name as community_name " +
            "FROM appointment a " +
            "LEFT JOIN user u ON a.user_id = u.id " +
            "LEFT JOIN vaccine v ON a.vaccine_id = v.id " +
            "LEFT JOIN community c ON a.community_id = c.id " +
            "WHERE a.community_id = #{communityId} ORDER BY a.create_time DESC")
    List<Appointment> findByCommunityId(Long communityId);

    // 统计相关
    @Select("SELECT COUNT(*) FROM appointment WHERE status = #{status}")
    Long countByStatus(Integer status);

    @Select("SELECT DATE(create_time) as date, COUNT(*) as count FROM appointment " +
            "WHERE create_time >= #{startDate} GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> countByDate(String startDate);

    @Select("SELECT c.name as community_name, COUNT(*) as count FROM appointment a " +
            "LEFT JOIN community c ON a.community_id = c.id " +
            "WHERE a.status = 4 GROUP BY a.community_id")
    List<Map<String, Object>> countByCommunity();

    /**
     * 统计某社区某日某时段的预约数量（用于容量控制）
     */
    @Select("SELECT COUNT(*) FROM appointment " +
            "WHERE community_id = #{communityId} " +
            "AND appointment_date = #{appointmentDate} " +
            "AND time_slot = #{timeSlot} " +
            "AND status NOT IN (2, 5)")
    int countByTimeSlot(@Param("communityId") Long communityId, 
                        @Param("appointmentDate") String appointmentDate, 
                        @Param("timeSlot") String timeSlot);

    /**
     * 统计某社区的总预约数
     */
    @Select("SELECT COUNT(*) FROM appointment WHERE community_id = #{communityId}")
    Long countByCommunityId(Long communityId);

    /**
     * 统计某社区某状态的预约数
     */
    @Select("SELECT COUNT(*) FROM appointment WHERE community_id = #{communityId} AND status = #{status}")
    Long countByCommunityIdAndStatus(@Param("communityId") Long communityId, @Param("status") Integer status);

    /**
     * 按疫苗统计接种数量（疫苗接种排行）
     */
    @Select("SELECT v.name as vaccine_name, COUNT(*) as count FROM appointment a " +
            "LEFT JOIN vaccine v ON a.vaccine_id = v.id " +
            "WHERE a.status = 4 GROUP BY a.vaccine_id ORDER BY count DESC LIMIT 10")
    List<Map<String, Object>> countByVaccine();

    /**
     * 统计收入（已支付和已接种的预约金额）
     */
    @Select("SELECT COALESCE(SUM(amount), 0) as total FROM appointment WHERE status IN (3, 4)")
    java.math.BigDecimal sumIncome();

    /**
     * 按日期统计收入
     */
    @Select("SELECT DATE(pay_time) as date, COALESCE(SUM(amount), 0) as amount FROM appointment " +
            "WHERE pay_time >= #{startDate} AND status IN (3, 4) GROUP BY DATE(pay_time) ORDER BY date")
    List<Map<String, Object>> sumIncomeByDate(String startDate);
}
