package com.chenyc.practice;

/**
 * @author chenyc
 * @create 2020-08-13 11:20
 */
interface  ShoesFactory {
    void productShoes();
}

class ProxyShoesFactory implements  ShoesFactory{

    private ShoesFactory factory;

    public ProxyShoesFactory(ShoesFactory factory) {
        this.factory = factory;
    }

    @Override
    public void productShoes() {
        System.out.println("代理工厂做一些准备工作");
        factory.productShoes();
        System.out.println("代理工厂做一些后续的收尾工作");
    }
}

class NikeShoesFactory implements ShoesFactory{

    @Override
    public void productShoes() {
        System.out.println("Nike工厂生产一批鞋子");
    }
}

/**
 * @author chenyuancheng
 */
public class StaticProxyPractice {
    public static void main(String[] args) {
        ShoesFactory shoesFactory = new NikeShoesFactory();
        ShoesFactory proxyFactory = new ProxyShoesFactory(shoesFactory);
        proxyFactory.productShoes();

        /**
         * 代理工厂做一些准备工作
         * Nike工厂生产一批鞋子
         * 代理工厂做一些后续的收尾工作
         * */
    }
}
