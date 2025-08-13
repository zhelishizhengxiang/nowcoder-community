package com.simon.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhengx
 * @version 1.0
 * 封装分页相关的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    //当前的页码
    private Integer currentPage = 1;
    //每页显示条数
    private Integer pageSize = 10;
    //数据总数：用于计算总页数
    private Integer rows;
    //查询路径：复用分页链接
    private String path;

    public void setCurrentPage(Integer currentPage) {
        if (currentPage == null || currentPage < 1)
            return;
        this.currentPage = currentPage;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1 || pageSize > 100)
            return;
        this.pageSize = pageSize;
    }

    public void setRows(Integer rows) {
        if (rows == null || rows < 0)
            return;
        this.rows = rows;
    }

    /**
     * 计算开始索引offset
     *
     */
    public int getOffset() {
        return (this.currentPage - 1) * this.pageSize;
    }

    /**
     * 获取所有数据能显示的总页数
     *
     */
    public int getTotal() {
        if (rows % pageSize == 0)
            return rows / pageSize;
        else
            return rows / pageSize + 1;
    }

    /**
     * 获取起始页码
     *
     */
    public int getFrom() {
        int from = currentPage - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结束页码
     *
     */
    public int getTo() {
        int to = currentPage + 2;
        int total = getTotal();
        return to > total ? total : to;
    }

}
