package com.simon.community.actuator;

import com.simon.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author zhengx
 * @version 1.0
 */
@Component
//给自定义端点取id
@Endpoint(id="database")
@Slf4j
public class DatabaseEndpoint {
    @Autowired
    private DataSource dataSource;

    @ReadOperation//通过get请求访问该端点
    public String checkConnection() {
        try (Connection conn = dataSource.getConnection()){
            return CommunityUtil.getJSONString(200,"连接成功");
        } catch (SQLException e) {
            log.error("获取连接失败"+e.getMessage());
            return CommunityUtil.getJSONString(500,"连接失败");
        }

    }




}
