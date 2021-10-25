package com.example.service;

import com.example.entity.Student;

import java.util.List;

/**
 * @author chenzufeng
 * @date 2021/10/17
 * @usage StudentService
 */
public interface StudentService {
    /**
     * 根据id查询学生信息
     * @param id 主键id
     * @return Student
     */
    Student queryStudentById(Integer id);

    /**
     * 根据主键修改Student信息
     * @param student Body中的信息
     * @return Student
     */
    Student updateStudentAllInfoById(Student student);

    /**
     * 增
     * 增加一条学生记录
     * @param student Student
     * @return Integer id
     */
    Integer createStudent(Student student);

    /**
     * 查询所有学生信息
     * @return 学生信息列表
     */
    List<Student> queryAllStudent();
}
