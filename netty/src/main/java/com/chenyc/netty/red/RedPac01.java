package com.chenyc.netty.red;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author chenyc
 * @create 2020-09-07 11:02
 */
public class RedPac01 {



    @Test
    public void test01(){
        LeftMoneyPackage moneyPackage = new LeftMoneyPackage();
        moneyPackage.remainMoney = 40;
        moneyPackage.remainSize = 10;
        BigDecimal total = BigDecimal.ZERO;

        while (moneyPackage.remainSize != 0) {
            Double randomMoney = RedPac01.getRandomMoney(moneyPackage);
            System.out.print(randomMoney + "   ");
            total=total.add(new BigDecimal(randomMoney.toString()));
        }
        System.out.println("total====="+total);
    }

    private class LeftMoneyPackage {
        public double remainMoney;
        public int remainSize;
    }


    public static double getRandomMoney(LeftMoneyPackage _leftMoneyPackage) {
        // remainSize 剩余的红包数量
        // remainMoney 剩余的钱
        if (_leftMoneyPackage.remainSize == 1) {
            _leftMoneyPackage.remainSize--;
            return (double) Math.round(_leftMoneyPackage.remainMoney * 100) / 100;
        }
        Random r = new Random();
        double min = 0.01; //
        double max = _leftMoneyPackage.remainMoney / _leftMoneyPackage.remainSize * 2;
        if (max > 6) {
            max = 6;
        }
        double money = r.nextDouble() * max;
        money = money <= min ? 0.01 : money;
        money = Math.floor(money * 100) / 100;
        _leftMoneyPackage.remainSize--;
        _leftMoneyPackage.remainMoney -= money;
        return money;
    }


    @Test
    public void test02(){
        List<Double> doubles = Arrays.asList(new Double[]{4.16  , 2.45  , 4.82  , 1.71  , 3.75 ,  2.75  , 4.63  , 3.79 ,  4.63,   7.31});
        Double aDouble = doubles.stream().reduce(Double::sum).get();
        System.out.println(aDouble);


    }
}
