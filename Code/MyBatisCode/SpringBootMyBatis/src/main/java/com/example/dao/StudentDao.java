package com.example.dao;

import com.example.entity.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author chenzufeng
 * @Mapper 扫描StudentDao接口到容器
 */
@Mapper
public interface StudentDao {
    /**
     * 根据主键删除
     * @param id 主键
     */
    void deleteByPrimaryKey(Integer id);

    /**
     * 插入一条记录
     * @param student Student
     * @return
     */
    int insert(Student student);

    /**
     * 插入一条记录（字段可选）
     * @param student Student
     * @return
     */
    Integer insertSelective(Student student);

    /**
     * 根据主键查询
     * @param id 主键
     * @return
     */
    Student selectByPrimaryKey(Integer id);

    /**
     * 更新一条记录（字段可选）
     * @param record Student
     */
    void updateByPrimaryKeySelective(Student record);

    /**
     * 根据主键跟新记录
     * @param student Student
     */
    void updateByPrimaryKey(Student student);

    /**
     * 查询所有学生信息
     * @return 学生信息列表
     */
    List<Student> queryAllStudent();
}