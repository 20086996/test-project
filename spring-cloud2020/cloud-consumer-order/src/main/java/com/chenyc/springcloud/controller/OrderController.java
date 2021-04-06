package com.chenyc.springcloud.controller;

import com.chenyc.springcloud.entities.CommonResult;
import com.chenyc.springcloud.entities.Payment;
import com.chenyc.springcloud.lb.LoadBalancer;
import com.chenyc.springcloud.openFeign.PaymentFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

/**
 * @author chenyc
 * @create 2021-03-30 10:34
 */
@RestController
@RequestMapping("/consumer")
public class OrderController {

    //远程调用的 地址
//    public static final String PAYMENY_URL = "http://localhost:8001";
    //euraka远程调用的地址
    public static final String PAYMENY_URL = "http://CLOUD-PROVIDER-PAYMENT";
    //zookeeper和consul远程调用的地址
//    public static final String PAYMENY_URL = "http://cloud-provider-payment";

    @Resource
    private LoadBalancer loadBalancer;

    @Resource
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    PaymentFeignService paymentFeignService;

    @PostMapping("/payment/create")
    public CommonResult<Payment> create (@RequestBody Payment payment){
        return restTemplate.postForObject(PAYMENY_URL + "/payment/create", payment, CommonResult.class);
    }

    @GetMapping("/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id")Long id){
        return restTemplate.getForObject(PAYMENY_URL + "/payment/get/" + id,//请求地址
                CommonResult.class);//返回类型
    }

    @GetMapping(value = "/payment/zk")
    public String paymentInfo()
    {
        String result = restTemplate.getForObject(PAYMENY_URL+"/payment/zk",String.class);
        return result;
    }


    @GetMapping(value = "/payment/consul")
    public String paymentInfoConsul()
    {
        String result = restTemplate.getForObject(PAYMENY_URL+"/payment/consul",String.class);
        return result;
    }

    @GetMapping("/payment/getForEntity/{id}")
    public CommonResult<Payment> getPayment2(@PathVariable("id") Long id)
    {
        ResponseEntity<CommonResult> entity = restTemplate.getForEntity(PAYMENY_URL+"/payment/get/"+id,CommonResult.class);

        if(entity.getStatusCode().is2xxSuccessful()){
            return entity.getBody();//getForObject()
        }else{
            return new CommonResult<>(444,"操作失败");
        }
    }


    @GetMapping(value = "/payment/lb")
    public String getPaymentLB()
    {
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PROVIDER-PAYMENT");
        if(instances == null || instances.size() <= 0){
            return null;
        }
        ServiceInstance serviceInstance = loadBalancer.instances(instances);
        URI uri = serviceInstance.getUri();
        return restTemplate.getForObject(uri+"/payment/lb",String.class);
    }


    @GetMapping(value = "/payment/openFeign/get/{id}")
    public CommonResult<Payment> openFeign(@PathVariable("id") Long id)
    {
        return paymentFeignService.getPaymentById(id);
    }



}
