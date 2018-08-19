package com.kwon.znshare.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Entity
public class MeiNv {
//    @Id // 主键
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
//    private Long id; //id
//    @NotEmpty(message = "姓名不能为空")
//    @Size(min=2, max=20)
//    @Column(nullable = false, length = 20)
//    private String name;//姓名
//    @OneToMany(mappedBy = "author",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
//    //级联保存、更新、删除、刷新;延迟加载。当删除用户，会级联删除该用户的所有文章
//    //拥有mappedBy注解的实体类为关系被维护端
//    //mappedBy="author"中的author是Article中的author属性
//    private List<Article> articleList;//文章列表

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; //id

    @NotEmpty(message = "类型不能为空")
    @Column(nullable = false)
    private String type;//类型

    @NotEmpty(message = "标题不能为空")
    @Column(nullable = false)
    private String title;//标题

    @NotEmpty(message = "封面不能为空")
    @Column(nullable = false)
    private String cover;//封面图片

    @NotEmpty(message = "片段不能为空")
    @Column(nullable = false)
    private String fragment;//拼接图片链接的片段
    @NotEmpty(message = "图片数量不能为空")
    @Column(nullable = false)
    private String total;//图片数量

    @Column(nullable = false)
    private Date creatTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }
}
