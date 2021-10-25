package com.example.service.impl;

import com.example.dao.StudentDao;
import com.example.entity.Student;
import com.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chenzufeng
 * @date 2021/10/17
 * @usage StudentServiceImpl
 * @Service 将StudentServiceImpl注册到容器中供使用
 */
@Service
public class StudentServiceImpl implements StudentService {
    /**
     * 注入dao层，如果没有在容器中注入会报错：
     * No beans of 'StudentDao' type found.
     */
    @Autowired
    private StudentDao studentDao;

    @Override
    public Student queryStudentById(Integer id) {
        Student student = studentDao.selectByPrimaryKey(id);
        return student;
    }

    @Override
    public Student updateStudentAllInfoById(Student student) {
        studentDao.updateByPrimaryKey(student);
        return student;
    }

    @Override
    public Integer createStudent(Student student) {
        Integer id = studentDao.insertSelective(student);
        return id;
    }
}
