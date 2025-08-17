package com.simon.community.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录凭证
 */
@Data
public class LoginTicket implements Serializable {
    private Integer id;

    private Integer userId;

    private String ticket;

    /**
     * 0-有效; 1-无效;
     */
    private Integer status;

    private Date expired;

}