package com.vaccine.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约时段容量实体
 */
@Data
public class TimeSlotCapacity {
    private Long id;
    private Long communityId;
    private LocalDate slotDate;
    private String timeSlot;
    private Integer capacity;
    private Integer booked;
    private LocalDateTime createTime;
    
    // 非数据库字段
    private String communityName;
}
