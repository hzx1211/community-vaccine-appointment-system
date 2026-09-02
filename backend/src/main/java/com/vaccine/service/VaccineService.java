package com.vaccine.service;

import com.vaccine.common.PageResult;
import com.vaccine.entity.Vaccine;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.VaccineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 疫苗服务
 */
@Service
public class VaccineService {

    @Autowired
    private VaccineMapper vaccineMapper;

    public Vaccine findById(Long id) {
        return vaccineMapper.findById(id);
    }

    public void save(Vaccine vaccine) {
        if (vaccine.getId() == null) {
            vaccine.setAppointmentCount(0);
            vaccine.setStatus(1);
            vaccineMapper.insert(vaccine);
        } else {
            vaccineMapper.update(vaccine);
        }
    }

    public void deleteById(Long id) {
        vaccineMapper.deleteById(id);
    }

    public PageResult<Vaccine> findPage(Vaccine vaccine, int pageNum, int pageSize) {
        Long total = vaccineMapper.count(vaccine);
        List<Vaccine> list = vaccineMapper.findList(vaccine);
        // 简单分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        return PageResult.of(total, list);
    }

    public List<Vaccine> findAll(Vaccine vaccine) {
        return vaccineMapper.findList(vaccine);
    }

    public List<Vaccine> findHotVaccines(int limit) {
        return vaccineMapper.findHotVaccines(limit);
    }

    /**
     * 减少库存（预约时调用）
     */
    public void decreaseStock(Long id) {
        int rows = vaccineMapper.decreaseStock(id);
        if (rows == 0) {
            throw BusinessException.of("疫苗库存不足");
        }
    }

    /**
     * 增加库存（取消预约时调用）
     */
    public void increaseStock(Long id) {
        vaccineMapper.increaseStock(id);
    }

    public void updateStatus(Long id, Integer status) {
        Vaccine vaccine = new Vaccine();
        vaccine.setId(id);
        vaccine.setStatus(status);
        vaccineMapper.update(vaccine);
    }
}
