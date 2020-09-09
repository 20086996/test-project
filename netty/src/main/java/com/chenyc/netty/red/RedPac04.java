package com.chenyc.netty.red;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author chenyc
 * @create 2020-09-07 11:02
 */
public class RedPac04 {
    /**
     * 返回一次抽奖在指定中奖概率下是否中奖
     * @param rate 中奖概率
     * @return
     */
    public static boolean canReward(double rate) {
        double random = Math.random();
        System.out.print("double==="+random);
        System.out.println();
        return random <= rate;
    }

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
        Integer avg = totalBonus / totalNum;
        Integer leftLen = avg - rdMin;
        Integer rightLen = rdMax - avg;
        Integer boundMin = 0, boundMax = 0;
        if (leftLen.equals(rightLen)) {
            boundMax = Math.min((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMin), rdMax);
            boundMin = Math.max((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMax), rdMin);
        } else if (rightLen.compareTo(leftLen) > 0) {
            // 上限偏离
            double bigRate = leftLen / (double)(leftLen + rightLen);
            Integer standardRdMax = avg + leftLen;  // 右侧对称上限点
            Integer _rdMax = canReward(bigRate) ? rdMax : standardRdMax;
            boundMax = Math.min((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMin), _rdMax);
            boundMin = Math.max((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * standardRdMax), rdMin);
         } else {
            // 下限偏离
            double smallRate = rightLen / (double)(leftLen + rightLen);
            Integer standardRdMin = avg - rightLen;  // 左侧对称下限点
            Integer _rdMin = canReward(smallRate) ? rdMin : standardRdMin;
            boundMax = Math.min((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * standardRdMin), rdMax);
            boundMin = Math.max((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMax), _rdMin);
        }
        return getRandomVal(boundMin, boundMax);
    }

    /**
     * 生成红包一次分配结果
     * @param totalBonus 总红包量
     * @param totalNum 总份数
     * @return
     */
    public static List<Integer> createBonusList(Integer totalBonus, Integer totalNum, Integer rdMin, Integer rdMax) {
        Integer sendedBonus = 0;
        Integer sendedNum = 0;
        List<Integer> bonusList = new ArrayList<>();
        while (sendedNum < totalNum) {
            Integer bonus = randomBonusWithSpecifyBound(totalBonus, totalNum, sendedBonus, sendedNum, rdMin, rdMax);
            bonusList.add(bonus);
            sendedNum++;
            sendedBonus += bonus;
        }
        return bonusList;
    }


    @Test
    public void test01(){
        List<Integer> bonusList = createBonusList(5000, 50, 100, 400);
        System.out.println("bonusList======"+bonusList);
        Optional<Integer> reduce = bonusList.stream().reduce(Integer::sum);
        System.out.println("reduce======"+reduce.get());
    }
}
