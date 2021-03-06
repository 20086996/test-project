package com.chenyc.java;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理的举例
 * @author shkstart
 * @create 2019 上午 10:18
 */

interface Human{
    String getBelief();

    void eat(String food);

    void getPeople(People people);
}
//被代理类
class SuperMan implements Human{
    @Override
    public String getBelief() {
        System.out.println("I believe I can fly!");
        return "I believe I can fly!";
    }

    @Override
    public void eat(String food) {
        System.out.println("我喜欢吃" + food);
    }

    @Override
    public void getPeople(People people) {
        System.out.println(people.name+"====="+people.value);
    }
}

class HumanUtil{
    public void method1(){
        System.out.println("====================通用方法一====================");
    }
    public void method2(){
        System.out.println("====================通用方法二====================");
    }
}

/*
要想实现动态代理，需要解决的问题？
问题一：如何根据加载到内存中的被代理类，动态的创建一个代理类及其对象。
问题二：当通过代理类的对象调用方法a时，如何动态的去调用被代理类中的同名方法a。
 */

class ProxyFactory{
    /**调用此方法，返回一个代理类的对象。解决问题一*/
    public static Object getProxyInstance(Object obj){//obj:被代理类的对象
        MyInvocationHandler handler = new MyInvocationHandler();

        handler.bind(obj);

        return Proxy.newProxyInstance(obj.getClass().getClassLoader(),obj.getClass().getInterfaces(),handler);
    }

}

class MyInvocationHandler implements InvocationHandler{

    //需要使用被代理类的对象进行赋值
    private Object obj;

    public void bind(Object obj){
        this.obj = obj;
    }

    /**
     当我们通过代理类的对象，调用方法a时，就会自动的调用如下的方法：invoke()
    将被代理类要执行的方法a的功能就声明在invoke()中
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        HumanUtil util = new HumanUtil();
        util.method1();

        //method:即为代理类对象调用的方法，此方法也就作为了被代理类对象要调用的方法
        //obj:被代理类的对象
        if(args!=null){
            Object arg = args[0];
            System.out.println(arg);
        }

        if (method.getParameterTypes().length > 0){
            Object para = args[0];
            Class<?> parameterType = method.getParameterTypes()[0];
            for (Field field : parameterType.getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                Object o = field.get(para);
                System.out.println(o);
            }
        }


        Object returnValue = method.invoke(obj,args);

        util.method2();

        //上述方法的返回值就作为当前类中的invoke()的返回值。
        return returnValue;

    }
}

class People{
    public String name;
    public String value;
}

public class ProxyTest {

    public static void main(String[] args) {
        SuperMan superMan = new SuperMan();
        //proxyInstance:代理类的对象
        Human proxyInstance = (Human) ProxyFactory.getProxyInstance(superMan);
        //当通过代理类对象调用方法时，会自动的调用被代理类中同名的方法
//        proxyInstance.getBelief();
//        proxyInstance.eat("四川麻辣烫");
        People people = new People();
        people.name="姓名";
        people.value="小名";
        proxyInstance.getPeople(people);

        System.out.println("*****************************");

        NikeClothFactory nikeClothFactory = new NikeClothFactory();

        ClothFactory proxyClothFactory = (ClothFactory) ProxyFactory.getProxyInstance(nikeClothFactory);

        proxyClothFactory.produceCloth();
        /**
         * ====================通用方法一====================
         * I believe I can fly!
         * ====================通用方法二====================
         * ====================通用方法一====================
         * 我喜欢吃四川麻辣烫
         * ====================通用方法二====================
         * *****************************
         * ====================通用方法一====================
         * Nike工厂生产一批运动服
         * ====================通用方法二====================
         * */

    }
}
