package com.chenyc.springcloud;

import com.chenyc.myrule.MySelfRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author chenyc
 * @create 2021-03-29 19:55
 */
@SpringBootApplication
@EnableEurekaClient//<--- Enable服务注册添加该标签
@EnableDiscoveryClient//<--- zookeeper服务注册添加该标签
//添加到此处
//@RibbonClient(name = "CLOUD-PROVIDER-PAYMENT", configuration = MySelfRule.class)
@EnableFeignClients
public class OrderMain {
    public static void main(String[] args) {
        SpringApplication.run(OrderMain.class, args);
    }
}
