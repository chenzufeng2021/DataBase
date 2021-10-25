package com.example.controller;

import com.example.entity.Student;
import com.example.service.StudentService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenzufeng
 * @date 2021/10/17
 * @usage StudentController
 */
@Api(value = "学生信息", tags = "学生信息")
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
    @ApiOperation(value = "根据学生主键ID查询学生信息")
    @GetMapping("/studentInfo")
    public Student getStudentInfo(
            // name为参数名称：在传参路径中显示；value对参数的说明
            @ApiParam(name = "id", value = "主键ID", required = true)
            @RequestParam("id") Integer id) {
        Student student = studentService.queryStudentById(id);
        return student;
    }

    /**
     * 查询所有学生信息（不分页）
     * @return 学生信息列表
     */
    @ApiOperation(value = "查询所有学生信息（不分页）")
    @GetMapping("/allStudentInfo")
    public List<Student> getAllStudentInfo() {
        return studentService.queryAllStudent();
    }

    /**
     * 查询所有学生信息（分页）
     * @return 学生信息列表
     */
    @ApiOperation(value = "查询所有学生信息（分页）")
    @GetMapping("/allStudentInfoPage")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "第几页", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "展示多少条数据", required = true, paramType = "path")
    })
    public List<Student> getAllStudentInfoPage(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "2") Integer pageSize
    ) {
        PageHelper.startPage(pageNo, pageSize);
        return studentService.queryAllStudent();
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
