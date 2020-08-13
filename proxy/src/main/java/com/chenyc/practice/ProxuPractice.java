package com.chenyc.practice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author chenyc
 * @create 2020-08-13 11:41
 */
interface Animal{
    void eat(String food);
}

class  dog implements Animal{
    @Override
    public void eat(String food) {
        System.out.println("狗狗吃"+food);
    }
}

class ProxyFactory{
    public  static  Object getProxyInstance(Object object){
        ProxyInvocationHandler handler =new ProxyInvocationHandler(object);
        return Proxy.newProxyInstance(object.getClass().getClassLoader(),object.getClass().getInterfaces(),handler);
    }
}

class ProxyInvocationHandler implements InvocationHandler {
    private Object object;

    public ProxyInvocationHandler(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        AnimalUtil util = new AnimalUtil();
        util.method1();
        method.invoke(object,args);
        util.method2();
        return null;
    }
}

class AnimalUtil{
    public void method1(){
        System.out.println("====================通用方法一====================");
    }
    public void method2(){
        System.out.println("====================通用方法二====================");
    }
}
public class ProxuPractice {
    public static void main(String[] args) {
        Animal animal =new dog();
        Animal animalInstance = (Animal) ProxyFactory.getProxyInstance(animal);
        animalInstance.eat("苹果");

        ShoesFactory shoes= new NikeShoesFactory();
        ShoesFactory ShoesFactory = (com.chenyc.practice.ShoesFactory) ProxyFactory.getProxyInstance(shoes);
        ShoesFactory.productShoes();
    }
}
