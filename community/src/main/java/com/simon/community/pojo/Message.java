package com.simon.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * message
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    private Integer id;

    private Integer fromId;

    private Integer toId;

    private String conversationId;

    private String content;

    /**
     * 0-未读;1-已读;2-删除;
     */
    private Integer status;

    private Date createTime;

}