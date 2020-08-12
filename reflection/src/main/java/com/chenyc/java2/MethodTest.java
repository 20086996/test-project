package com.chenyc.java2;

import com.chenyc.java1.Person;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 获取运行时类的方法结构
 *
 * @author shkstart
 * @create 2019 下午 3:37
 */
public class MethodTest {

    @Test
    public void test1(){

        Class clazz = Person.class;

        //getMethods():获取当前运行时类及其所有父类中声明为public权限的方法
        Method[] methods = clazz.getMethods();
        for(Method m : methods){
            System.out.println(m);
        }
        System.out.println("*************************************************");
        //getDeclaredMethods():获取当前运行时类中声明的所有方法。（不包含父类中声明的方法）
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for(Method m : declaredMethods){
            System.out.println(m);
        }

        /**
         * public java.lang.String com.chenyc.java1.Person.toString()
         * public int com.chenyc.java1.Person.compareTo(java.lang.String)
         * public int com.chenyc.java1.Person.compareTo(java.lang.Object)
         * public void com.chenyc.java1.Person.info()
         * public java.lang.String com.chenyc.java1.Person.display(java.lang.String,int) throws java.lang.NullPointerException,java.lang.ClassCastException
         * public void com.chenyc.java1.Creature.eat()
         * public final void java.lang.Object.wait() throws java.lang.InterruptedException
         * public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
         * public final native void java.lang.Object.wait(long) throws java.lang.InterruptedException
         * public boolean java.lang.Object.equals(java.lang.Object)
         * public native int java.lang.Object.hashCode()
         * public final native java.lang.Class java.lang.Object.getClass()
         * public final native void java.lang.Object.notify()
         * public final native void java.lang.Object.notifyAll()
         * *************************************************
         * public java.lang.String com.chenyc.java1.Person.toString()
         * public int com.chenyc.java1.Person.compareTo(java.lang.String)
         * public int com.chenyc.java1.Person.compareTo(java.lang.Object)
         * public void com.chenyc.java1.Person.info()
         * private static void com.chenyc.java1.Person.showDesc()
         * private java.lang.String com.chenyc.java1.Person.show(java.lang.String)
         * public java.lang.String com.chenyc.java1.Person.display(java.lang.String,int) throws java.lang.NullPointerException,java.lang.ClassCastException
         * Disconnected from the target VM, address: '127.0.0.1:57644', transport: 'socket'
         * */
    }

    /*
    @Xxxx
    权限修饰符  返回值类型  方法名(参数类型1 形参名1,...) throws XxxException{}
     */
    @Test
    public void test2(){
        Class clazz = Person.class;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for(Method m : declaredMethods){
            //1.获取方法声明的注解
            Annotation[] annos = m.getAnnotations();
            for(Annotation a : annos){
                System.out.println(a);
            }

            //2.权限修饰符
            System.out.print(Modifier.toString(m.getModifiers()) + "\t");

            //3.返回值类型
            System.out.print(m.getReturnType().getName() + "\t");

            //4.方法名
            System.out.print(m.getName());
            System.out.print("(");
            //5.形参列表
            Class[] parameterTypes = m.getParameterTypes();
            if(!(parameterTypes == null && parameterTypes.length == 0)){
                for(int i = 0;i < parameterTypes.length;i++){

                    if(i == parameterTypes.length - 1){
                        System.out.print(parameterTypes[i].getName() + " args_" + i);
                        break;
                    }

                    System.out.print(parameterTypes[i].getName() + " args_" + i + ",");
                }
            }

            System.out.print(")");

            //6.抛出的异常
            Class[] exceptionTypes = m.getExceptionTypes();
            if(exceptionTypes.length > 0){
                System.out.print("throws ");
                for(int i = 0;i < exceptionTypes.length;i++){
                    if(i == exceptionTypes.length - 1){
                        System.out.print(exceptionTypes[i].getName());
                        break;
                    }

                    System.out.print(exceptionTypes[i].getName() + ",");
                }
            }


            System.out.println();
        }

        /**
         * public	java.lang.String	toString()
         * public	int	compareTo(java.lang.String args_0)
         * public volatile	int	compareTo(java.lang.Object args_0)
         * public	void	info()
         * @com.chenyc.java1.MyAnnotation(value=hello)
         * private    java.lang.String	show(java.lang.String args_0)
         * public	java.lang.String	display(java.lang.String args_0,int args_1)throws java.lang.NullPointerException,java.lang.ClassCastException
         * private static	void	showDesc()
         *
         * */

    }
}
