package com.vaccine.mapper;

import com.vaccine.entity.TimeSlotCapacity;
import org.apache.ibatis.annotations.*;

/**
 * 预约时段容量Mapper
 */
@Mapper
public interface TimeSlotCapacityMapper {

    @Select("SELECT * FROM time_slot_capacity WHERE community_id = #{communityId} AND slot_date = #{slotDate} AND time_slot = #{timeSlot}")
    TimeSlotCapacity findBySlot(@Param("communityId") Long communityId, @Param("slotDate") String slotDate, @Param("timeSlot") String timeSlot);

    @Insert("INSERT INTO time_slot_capacity(community_id, slot_date, time_slot, capacity, booked) VALUES(#{communityId}, #{slotDate}, #{timeSlot}, #{capacity}, #{booked})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TimeSlotCapacity capacity);

    @Update("UPDATE time_slot_capacity SET booked = booked + 1 WHERE id = #{id} AND booked < capacity")
    int increaseBooked(Long id);

    @Update("UPDATE time_slot_capacity SET booked = booked - 1 WHERE id = #{id} AND booked > 0")
    int decreaseBooked(Long id);

    @Update("UPDATE time_slot_capacity SET capacity = #{capacity} WHERE id = #{id}")
    int updateCapacity(@Param("id") Long id, @Param("capacity") Integer capacity);
}
