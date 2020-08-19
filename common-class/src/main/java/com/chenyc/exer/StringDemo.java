package com.chenyc.exer;

import org.junit.Test;

/**
 * @author chenyc
 * @create 2020-08-19 17:57
 */
public class StringDemo {


    /**
    将一个字符串进行反转。将字符串中指定部分进行反转。比如“abcdefg”反转为”abfedcg”
    方式一：转换为char[]
     */
    public String reverse(String str,int startIndex,int endIndex){
        char[] chars = str.toCharArray();
        for(int x=startIndex,y=endIndex;x<y;x++,y--){
              char temp = chars[x];
              chars[x] = chars[y];
              chars[y] = temp;
        }
        return new String(chars);
    }

    //方式2
    public  String reverse1(String str,int startIndex,int endIndex){
        //第1部分
        String reverseStr = str.substring(0,startIndex);
        //第2部分
        for (int i=endIndex;i>=startIndex;i--){
            reverseStr+=str.charAt(i);
        }
        //第3部分
        reverseStr += str.substring(endIndex + 1);
        return reverseStr;
    }

    //方式3
    public String reverse2(String str,int startIndex,int endIndex){
        StringBuffer stringBuffer = new StringBuffer();
        //第1部分
        stringBuffer.append(str.substring(0,startIndex));

        //第2部分
        for (int i=endIndex;i>=startIndex;i--){
            stringBuffer.append(str.charAt(i));
        }

        stringBuffer.append(str.substring(endIndex+1));
        return stringBuffer.toString();
    }


    @Test
    public void testReverse(){
        String str = "abcdefg";
        String reverse = reverse2(str, 2, 5);
        System.out.println(reverse);
        //abfedcg
    }
}
