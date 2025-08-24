package com.simon.community.dao;

import com.simon.community.pojo.LoginTicket;

@Deprecated
public interface LoginTicketMapper {
    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);

    int updateStatus(String ticket, int status);


}