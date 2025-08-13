package com.simon.community.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * discuss_post
 */
@Data
public class DiscussPost implements Serializable {
    private Integer id;

    private Integer userId;

    private String title;

    private String content;

    /**
     * 0-普通; 1-置顶;
     */
    private Integer type;

    /**
     * 0-正常; 1-精华; 2-拉黑;
     */
    private Integer status;

    private Date createTime;

    private Integer commentCount;

    private Double score;


}