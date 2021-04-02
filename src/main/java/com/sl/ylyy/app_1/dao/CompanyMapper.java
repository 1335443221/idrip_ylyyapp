package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.Company;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Company record);

    int insertSelective(Company record);

    Company selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Company record);

    int updateByPrimaryKey(Company record);

    List<Company> selectCompanys();

    List<Company> selectParents();
    List<Company> selectChildrens(Integer parentId);
}