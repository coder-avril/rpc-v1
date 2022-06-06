package com.lding.config;

import com.lding.common.RpcServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {
    @Bean
    public RpcServer rpcServer(ApplicationContext applicationContext){
        RpcServer rpcServer = new RpcServer(9000, applicationContext);
        return rpcServer;
    }
}
