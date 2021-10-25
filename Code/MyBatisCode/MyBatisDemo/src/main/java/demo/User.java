package demo;

/**
 * mybatis数据库中user表的持久类
 * 创建持久化类 User，在类中声明的属性与数据表user的字段一致
 * @author chenzufeng
 */
public class User {
    private Integer id;
    private String name;
    private String password;

    public User() {}

    public User(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
