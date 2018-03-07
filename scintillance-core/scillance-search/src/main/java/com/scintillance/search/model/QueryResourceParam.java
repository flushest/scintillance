package com.scintillance.search.model;

/**
 * Created by Administrator on 2018/3/6 0006.
 */
public class QueryResourceParam {
    //key 关键字(书名、作者、出版社、译者、标签、格式|歌名、演唱者、作词、作曲、专辑)模糊查询--关键字需要进行语意分割，分割后进行查询
    //type 资源类型(0音乐/1书籍)
    //高级查询(利用定义好的标签进行精确到某字段的模糊查询)，如author：赵磊，singer:XXX
    //排序字段用于查询结果的排序
    //分页数据(page(页数)、count(一页几条))
}
