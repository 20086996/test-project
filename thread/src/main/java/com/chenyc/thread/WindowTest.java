package com.chenyc.thread;

/**
 * @author chenyc
 * @create 2020-08-18 13:10
 */
public class WindowTest{
        public static void main(String[] args) {
            Window t1 = new Window();
            Window t2 = new Window();
            Window t3 = new Window();


            t1.setName("窗口1");
            t2.setName("窗口2");
            t3.setName("窗口3");

            t1.start();
            t2.start();
            t3.start();

        }

}

class Window extends Thread{
    //一共三百张票
    //private  int ticket = 100;

    //加static一共100张表
    private static int ticket = 100;
    @Override
    public void run() {

        while(true){

            if(ticket > 0){
                System.out.println(getName() + "：卖票，票号为：" + ticket);
                ticket--;
            }else{
                break;
            }

        }

    }
}