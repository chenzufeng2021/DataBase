package com.example.controller;

import com.example.entity.Student;
import com.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author chenzufeng
 * @date 2021/10/17
 * @usage StudentController
 */
@RestController
@RequestMapping("/student")
public class StudentController {
    /**
     * 注入业务层
     * 如果没有在容器中注入StudentServiceImpl会报错：
     * No beans of 'StudentService' type found.
     */
    @Autowired
    private StudentService studentService;

    /**
     * 查
     * 根据id获取学生信息
     * @param id 主键id
     * @return Student
     */
    @GetMapping("/studentInfo")
    public Student getStudentInfo(@RequestParam("id") Integer id) {
        Student student = studentService.queryStudentById(id);
        return student;
    }

    /**
     * 改
     * 根据主键修改Student信息
     * @param student Body中的信息
     */
    @PostMapping("/updateStudentAllInfoById")
    public Student updateStudentAllInfoById(@RequestBody Student student) {
        studentService.updateStudentAllInfoById(student);
        return student;
    }

    /**
     * 增
     * 增加一条学生记录
     * @param student Student
     * @return Integer id
     */
    @PostMapping("/createStudent")
    public Integer createStudent(@RequestBody Student student) {
        // TODO 学习一下返回值的包装
        Integer id = studentService.createStudent(student);
        return id;
    }
}
