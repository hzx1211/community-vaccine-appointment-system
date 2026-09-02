package com.vaccine.service;

import com.vaccine.common.PageResult;
import com.vaccine.entity.VaccineCategory;
import com.vaccine.mapper.VaccineCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 疫苗分类服务
 */
@Service
public class VaccineCategoryService {

    @Autowired
    private VaccineCategoryMapper vaccineCategoryMapper;

    public VaccineCategory findById(Long id) {
        return vaccineCategoryMapper.findById(id);
    }

    public void save(VaccineCategory category) {
        if (category.getId() == null) {
            category.setStatus(1);
            vaccineCategoryMapper.insert(category);
        } else {
            vaccineCategoryMapper.update(category);
        }
    }

    public void deleteById(Long id) {
        vaccineCategoryMapper.deleteById(id);
    }

    public PageResult<VaccineCategory> findPage(VaccineCategory category, int pageNum, int pageSize) {
        Long total = vaccineCategoryMapper.count(category);
        List<VaccineCategory> list = vaccineCategoryMapper.findList(category);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        return PageResult.of(total, list);
    }

    public List<VaccineCategory> findAllEnabled() {
        return vaccineCategoryMapper.findAllEnabled();
    }
}
