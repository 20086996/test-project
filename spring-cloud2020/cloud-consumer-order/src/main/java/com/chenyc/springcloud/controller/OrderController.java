package com.chenyc.springcloud.controller;

import com.chenyc.springcloud.entities.CommonResult;
import com.chenyc.springcloud.entities.Payment;
import com.chenyc.springcloud.lb.LoadBalancer;
import com.chenyc.springcloud.openFeign.PaymentFeignService;
import com.chenyc.springcloud.po.Greetings;
import com.chenyc.springcloud.service.GreetingsService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
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
@DefaultProperties(defaultFallback = "payment_Global_FallbackMethod")
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

    @Autowired
    GreetingsService greetingsService;

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

    /**********************************************自定义轮询*************************************************************************/
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

/**********************************************openFeign*************************************************************************/
    @GetMapping(value = "/payment/openFeign/get/{id}")
    public CommonResult<Payment> openFeign(@PathVariable("id") Long id)
    {
        return paymentFeignService.getPaymentById(id);
    }


/**********************************************hystrix*************************************************************************/
    @GetMapping("/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id)
    {
        String result = paymentFeignService.paymentInfo_OK(id);
        return result;
    }

    @GetMapping("/payment/hystrix/timeout/{id}")
    @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1500")
    })
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id) {
//        int age = 10/0;
        String result = paymentFeignService.paymentInfo_TimeOut(id);
        return result;
    }

    @GetMapping("/payment/hystrix/global/timeout/{id}")
    @HystrixCommand//用全局的fallback方法
    public String paymentInfoGlobalTimeOut(@PathVariable("id") Integer id) {
        int age = 10/0;
        String result = paymentFeignService.paymentInfo_TimeOut(id);
        return result;
    }

    @GetMapping("/payment/hystrix/currency/timeout/{id}")
    public String paymentInfoCurrencyTimeOut(@PathVariable("id") Integer id) {
        String result = paymentFeignService.paymentInfo_TimeOut(id);
        return result;
    }

    //善后方法
    public String paymentTimeOutFallbackMethod(@PathVariable("id") Integer id){
        return "我是消费者80,对方支付系统繁忙请10秒钟后再试或者自己运行出错请检查自己,o(╥﹏╥)o";
    }

    // 下面是全局fallback方法
    public String payment_Global_FallbackMethod()
    {
        return "Global异常处理信息，请稍后再试，/(ㄒoㄒ)/~~";
    }

/*********************************************zipkin测试*************************************************************************/
    @GetMapping("/payment/zipkin")
    public String paymentZipkin()
    {
        String result = restTemplate.getForObject(PAYMENY_URL+"/payment/zipkin/", String.class);
        return result;
    }

    @GetMapping("/greetings")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void greetings(@RequestParam("message") String message) {
        Greetings greetings = Greetings.builder()
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
        greetingsService.sendGreeting(greetings);
    }

}
