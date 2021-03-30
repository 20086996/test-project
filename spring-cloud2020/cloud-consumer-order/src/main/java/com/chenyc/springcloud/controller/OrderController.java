package com.chenyc.springcloud.controller;

import com.chenyc.springcloud.entities.CommonResult;
import com.chenyc.springcloud.entities.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author chenyc
 * @create 2021-03-30 10:34
 */
@RestController
@RequestMapping("/consumer")
public class OrderController {

    //远程调用的 地址
//    public static final String PAYMENY_URL = "http://localhost:8001";
    public static final String PAYMENY_URL = "http://CLOUD-PROVIDER-PAYMENT";

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/payment/create")
    public CommonResult<Payment> create (@RequestBody Payment payment){
        return restTemplate.postForObject(PAYMENY_URL + "/payment/create", payment, CommonResult.class);
    }

    @GetMapping("/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id")Long id){
        return restTemplate.getForObject(PAYMENY_URL + "/payment/get/" + id,//请求地址
                CommonResult.class);//返回类型
    }

}
