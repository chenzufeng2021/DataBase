package com.example.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chenzufeng
 * @date 2021/10/27
 * @usage User 实现 Serializable
 * @Accessors(chain = true) 开启链式调用
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {
    private String id;
    private String name;
    private Integer age;
    private Date birth;
}
