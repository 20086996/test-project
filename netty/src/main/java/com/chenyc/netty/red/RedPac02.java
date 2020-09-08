package com.chenyc.netty.red;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author chenyc
 * @create 2020-09-07 11:02
 */
public class RedPac02 {

    /**
     * 返回min~max区间内随机数，含min和max
     * @param min
     * @param max
     * @return
     */
    private static int getRandomVal(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 随机分配第n个红包
     * @param totalBonus 总红包量
     * @param totalNum 总份数
     * @param sendedBonus 已发送红包量
     * @param sendedNum 已发送份数
     * @param rdMin 随机下限
     * @param rdMax 随机上限
     * @return
     */
    private static Integer randomBonusWithSpecifyBound(Integer totalBonus, Integer totalNum, Integer sendedBonus,
                                                       Integer sendedNum, Integer rdMin, Integer rdMax) {
        Integer boundMin = Math.max((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMax), rdMin);
        Integer boundMax = Math.min((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMin), rdMax);
        return getRandomVal(boundMin, boundMax);
    }

    /**
     * 生成红包一次分配结果
     * @param totalBonus 总红包量
     * @param totalNum 总份数
     * @return
     */
    public static List<Integer> createBonusList(Integer totalBonus, Integer totalNum) {
        Integer sendedBonus = 0;
        Integer sendedNum = 0;
        Integer rdMin = (int) (totalBonus / totalNum * 0.1);
        Integer rdMax = (int) (totalBonus / totalNum * 1.9);
        List<Integer> bonusList = new ArrayList<>();
        while (sendedNum < totalNum) {
            if(sendedNum==19){
                System.out.println(sendedNum);
            }
            Integer bonus = randomBonusWithSpecifyBound(totalBonus, totalNum, sendedBonus, sendedNum, rdMin, rdMax);
            bonusList.add(bonus);
            sendedNum++;
            sendedBonus += bonus;
        }
        return bonusList;
    }

    @Test
    public void test01(){
        List<Integer> bonusList = createBonusList(100, 20);
        System.out.println("bonusList======"+bonusList);
        Optional<Integer> reduce = bonusList.stream().reduce(Integer::sum);
        System.out.println("reduce======"+reduce.get());
    }
}
