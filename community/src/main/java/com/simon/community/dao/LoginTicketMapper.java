package com.simon.community.dao;

import com.simon.community.pojo.LoginTicket;

public interface LoginTicketMapper {
    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);

    int updateStatus(String ticket, int status);


}