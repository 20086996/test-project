package com.chenyc.java3;

import com.chenyc.java2.Employee;
import com.chenyc.java2.EmployeeData;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 测试Stream的终止操作
 *
 * @author shkstart
 * @create 2019 下午 6:37
 */
public class StreamAPITest2 {

    //1-匹配与查找
    @Test
    public void test1(){
        List<Employee> employees = EmployeeData.getEmployees();

        /**allMatch(Predicate p)——检查是否匹配所有元素。*/
//          练习：是否所有的员工的年龄都大于18
        boolean allMatch = employees.stream().allMatch(e -> e.getAge() > 18);
        System.out.println(allMatch);

        /**anyMatch(Predicate p)——检查是否至少匹配一个元素。*/
//         练习：是否存在员工的工资大于 10000
        boolean anyMatch = employees.stream().anyMatch(e -> e.getSalary() > 10000);
        System.out.println(anyMatch);

        /**noneMatch(Predicate p)——检查是否没有匹配的元素。*/
//          练习：是否存在员工姓“雷”
        boolean noneMatch = employees.stream().noneMatch(e -> e.getName().startsWith("雷"));
        System.out.println(noneMatch);

        /**findFirst——返回第一个元素*/
        Optional<Employee> employee = employees.stream().findFirst();
        System.out.println(employee);

        /**findAny——返回当前流中的任意元素*/
        Optional<Employee> employee1 = employees.parallelStream().findAny();
        System.out.println(employee1);

    }

    @Test
    public void test2(){
        List<Employee> employees = EmployeeData.getEmployees();
        /**count——返回流中元素的总个数*/
        long count = employees.stream().filter(e -> e.getSalary() > 5000).count();
        System.out.println(count);

       /**max(Comparator c)——返回流中最大值*/
//        练习：返回最高的工资：
        Stream<Double> salaryStream = employees.stream().map(e -> e.getSalary());
        Optional<Double> maxSalary = salaryStream.max(Double::compare);
        System.out.println(maxSalary);

        /**min(Comparator c)——返回流中最小值*/
//        练习：返回最低工资的员工
        Optional<Employee> employee = employees.stream().min((e1, e2) -> Double.compare(e1.getSalary(), e2.getSalary()));
        System.out.println(employee);
        System.out.println();

        /**forEach(Consumer c)——内部迭代*/
        employees.stream().forEach(System.out::println);
        System.out.println();

        /**使用集合的遍历操作*/
        employees.forEach(System.out::println);
    }

    //2-归约
    @Test
    public void test3(){
        /**reduce(T identity, BinaryOperator)——可以将流中元素反复结合起来，得到一个值。返回 T*/
//        练习1：计算1-10的自然数的和
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        Integer sum = list.stream().reduce(0, Integer::sum);
        System.out.println(sum);


        /**reduce(BinaryOperator) ——可以将流中元素反复结合起来，得到一个值。返回 Optional<T>*/
//        练习2：计算公司所有员工工资的总和
        List<Employee> employees = EmployeeData.getEmployees();
        Stream<Double> salaryStream = employees.stream().map(Employee::getSalary);
//        Optional<Double> sumMoney = salaryStream.reduce(Double::sum);
        Optional<Double> sumMoney = salaryStream.reduce((d1,d2) -> d1 + d2);
        System.out.println(sumMoney.get());

    }

    //3-收集
    @Test
    public void test4(){
//        collect(Collector c)——将流转换为其他形式。接收一个 Collector接口的实现，用于给Stream中元素做汇总的方法
//        练习1：查找工资大于6000的员工，结果返回为一个List或Set

        List<Employee> employees = EmployeeData.getEmployees();
        List<Employee> employeeList = employees.stream().filter(e -> e.getSalary() > 6000).collect(Collectors.toList());

        employeeList.forEach(System.out::println);
        System.out.println();
        Set<Employee> employeeSet = employees.stream().filter(e -> e.getSalary() > 6000).collect(Collectors.toSet());
        employeeSet.forEach(System.out::println);

        System.out.println("--------------------------------------");
        List<String> collect = employees.stream().map(Employee::getName).collect(Collectors.toList());
        collect.forEach(System.out::print);

        System.out.println("--------------------------------------");
        Set<String> collect1 = employees.stream().map(Employee::getName).collect(Collectors.toSet());
        collect1.forEach(System.out::println);

        System.out.println("--------------------------------------");
        HashSet<String> collect2 = employees.stream().map(Employee::getName).collect(Collectors.toCollection(HashSet::new));
        collect2.forEach(System.out::println);

        System.out.println("--------------------------------------");
        Long collect3 = employees.stream().map(Employee::getName).collect(Collectors.counting());
        System.out.println(collect3);

        System.out.println("--------------------------------------");
        Double collect4 = employees.stream().collect(Collectors.averagingDouble(e -> e.getSalary()));
        System.out.println(collect4);

        System.out.println("--------------------------------------");
        Double collect5 = employees.stream().collect(Collectors.summingDouble(Employee::getSalary));
        System.out.println(collect5);

        System.out.println("--------------------------------------");
        Optional<Double> collect6 = employees.stream().map(Employee::getSalary).collect(Collectors.maxBy(Double::compareTo));
        System.out.println(collect5.getClass());

        System.out.println("--------------------------------------");
        Optional<Double> collect7 = employees.stream().map(Employee::getSalary).collect(Collectors.minBy(Double::compareTo));
        System.out.println(collect7.getClass());

        System.out.println("--------------------------------------");
        Map<Integer, List<Employee>> collect8 = employees.stream().collect(Collectors.groupingBy(Employee::getAge));
        System.out.println(collect8);

        System.out.println("--------------------------------------");
        Map<String, Map<String, List<Employee>>> collect9 = employees.stream().collect(Collectors.groupingBy(Employee::getName, Collectors.groupingBy(e -> {
            if (e.getAge() <= 35) {
                return "青年";
            } else {
                return "老年";
            }
        })));
        System.out.println(collect9);

        System.out.println("--------------------------------------");
        Map<Boolean, List<Employee>> collect10 = employees.stream().collect(Collectors.partitioningBy(e -> e.getSalary() > 8000));
        System.out.println(collect10);

        System.out.println("--------------------------------------");
        DoubleSummaryStatistics collect11 = employees.stream().collect(Collectors.summarizingDouble(Employee::getSalary));
        System.out.println(collect11.getAverage());
        System.out.println(collect11.getMax());
        System.out.println(collect11.getMin());
        System.out.println(collect11.getSum());

        System.out.println("--------------------------------------");
        String collect12 = employees.stream().map(Employee::getName).collect(Collectors.joining(","));
        System.out.println(collect12);
    }
}
