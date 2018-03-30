/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.abel533.echarts.samples.bar;

import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.AxisLabel;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.json.OptionUtil;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.util.EnhancedOption;

import java.sql.*;
import java.util.*;

/**
 * Created by banny on 2018/3/29.
 *
 *  生成多个echars
 */
public class BarCategoryStack_jd {

    public static void main(String[] args) throws SQLException, ClassNotFoundException{

        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        //Class.forName("org.apache.hive.jdbc.HiveDriver");

        Connection connection = DriverManager.getConnection("jdbc:presto://localhost:8080/hive/default","root",null);
        //getConnection("jdbc:hive2://localhost:10000/default", "banny", "banny");

        /** 输出数据格式:
         age |         mn         | _col2
         -----+--------------------+-------
         20 |    6418.1848975178 | 16-08
         25 |  756839.0602344226 | 16-08
         30 |  3032081.362369452 | 16-08
         35 | 3637875.1676671705 | 16-08
         40 | 1196439.2327383216 | 16-08
         45 | 316887.46297681896 | 16-08
         50 | 135231.25678165894 | 16-08
         20 |  9519.064578255096 | 16-09
         25 |  997918.8561539274 | 16-09
         30 |  3656256.929678789 | 16-09
         35 | 1571166.7435728677 | 16-09
         40 | 1002559.1365480089 | 16-09
         45 | 327588.64633890707 | 16-09
         50 |   83648.8255157377 | 16-09
         20 |    4632.3064286934 | 16-10
         25 |  834861.0009397868 | 16-10
         30 |  4869088.174626559 | 16-10
         35 |   4266860.19204269 | 16-10
         40 | 1351780.5864337445 | 16-10
         45 |   279754.386631412 | 16-10
         50 |  87172.34989640569 | 16-10
         20 | 4296.0098319971985 | 16-11
         25 |  639969.1476134546 | 16-11
         30 |  3844251.039861639 | 16-11
         35 |  6575119.519861714 | 16-11
         40 | 1039813.7893111408 | 16-11
         45 |  178834.3709865522 | 16-11
         50 |   61127.8571872786 | 16-11
         */
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select age,sum(price*qty-discount)as mn, date_format(buy_time,'%Y-%m') from t_user  join t_order on t_user.uid=t_order.uid group by age, date_format(buy_time,'%Y-%m') order by date_format(buy_time,'%Y-%m'),age");

        List<String> yAgeList = new ArrayList<String>();

        //x轴: 时间
        Set<String> xDateSet= new TreeSet<String>();
        //y轴: 年龄
        Set<String> yAgeSet= new TreeSet<String>(new MyComparator());
        List<OrderAccount> orderAccountList = new ArrayList<OrderAccount>();

        while (rs.next()) {
            //System.out.println(rs.getString(3));
            OrderAccount orderAccount = new OrderAccount();
            orderAccount.setAge(rs.getString(1)+"岁");
            orderAccount.setMn(rs.getString(2));
            orderAccount.setDate(rs.getString(3));
            orderAccountList.add(orderAccount);
            xDateSet.add(rs.getString(3));
            yAgeSet.add(rs.getString(1)+"岁");
//            System.out.println(rs.getString(3));
        }
        System.out.println(xDateSet.toString().substring(1, xDateSet.toString().length()-1));
        String[] dateArray = xDateSet.toString().substring(1, xDateSet.toString().length()-1).split(",");
        System.out.println(yAgeSet.toString().substring(1, yAgeSet.toString().length()-1));
        String[] ageArray = yAgeSet.toString().substring(1,yAgeSet.toString().length()-1).replace(" ","").split(",");
        for (int i = 0; i < ageArray.length; i++) {
            System.out.println("======="+ageArray[i]);
        }


        Map<String,List> mnMap = new HashMap<String, List>();

        for (String age: yAgeSet) {
            //y轴: 取不同的年龄:20 25 30 35 40 45 50
            yAgeList.add(age);
            List<String> yMnlist = new ArrayList<String>();
            for(int i = 0; i < orderAccountList.size(); i++){
                if(age.equals(orderAccountList.get(i).getAge())){
                    //y轴: 取不同的年龄的金钱
                    yMnlist.add(orderAccountList.get(i).getMn());
                }
            }
            mnMap.put(age,yMnlist);

        }

        //地址：http://echarts.baidu.com/examples/editor.html?c=bar-y-category-stack
        EnhancedOption option = new EnhancedOption();
        option.title("各年龄段每月消费情况", "数据经过脱敏处理");
        option.tooltip().trigger(Trigger.axis).axisPointer().type(PointerType.shadow);
        option.legend(ageArray);
        option.toolbox().show(true).feature(Tool.mark, Tool.dataView, new MagicType(Magic.line, Magic.bar).show(true), Tool.restore, Tool.saveAsImage);
        option.calculable(true);
        option.yAxis(new CategoryAxis().data(dateArray));
        option.xAxis(new ValueAxis());

        Bar[] bar = new Bar[yAgeList.size()];
        for (int i = 0; i < yAgeList.size(); i++) {
            Bar b1 = new Bar(yAgeList.get(i).toString());
            b1.stack("总量");
            //b1.itemStyle().normal().label().show(true);//.position("insideRight");

            List<String> b2 = mnMap.get(yAgeList.get(i).toString());
            String[] data = new String[b2.size()];
            for (int j = 0; j < b2.size(); j++) {
                data[j] = b2.get(j);
            }
            b1.data(data);

            bar[i] = b1;
        }

        option.series(bar);


        /*
            不同性别每月消费情况
         */

        ResultSet rs1 = stmt.executeQuery("select case when sex=1 then '女' else '男' end, sum(loan_amount) as mn,date_format(loan_time, '%Y-%m') from t_user join t_loan on t_user.uid=t_loan.uid group by sex, date_format(loan_time,'%Y-%m') order by date_format(loan_time,'%Y-%m'),sex");

        List<String> yNamelist = new ArrayList<String>();

        //x轴: 时间
        Set<String> xdateList= new TreeSet<String>(new MyComparator());
        //y轴: 性别
        Set<String> ySexList= new TreeSet<String>(new MyComparator());
        List<OrderAccount1> orderAccountList1 = new ArrayList<OrderAccount1>();

        while (rs1.next()) {
            OrderAccount1 orderAccount1 = new OrderAccount1();
            orderAccount1.setSex(rs1.getString(1));
            orderAccount1.setMn(rs1.getString(2));
            orderAccount1.setDate(rs1.getString(3));
            orderAccountList1.add(orderAccount1);
            xdateList.add(rs1.getString(3));
            ySexList.add(rs1.getString(1));
        }
        System.out.println(xdateList.toString().substring(1, xdateList.toString().length()-1));
        String[] dateArray1 = xdateList.toString().substring(1, xdateList.toString().length()-1).split(",");
        System.out.println(ySexList.toString().substring(1, ySexList.toString().length()-1));
        String[] ageArray1 = ySexList.toString().substring(1,ySexList.toString().length()-1).replace(" ","").split(",");
        for (int i = 0; i < ageArray.length; i++) {
            System.out.println("======="+ageArray[i]);
        }


        Map<String,List> mnMap1 = new HashMap<String, List>();

        rs.close();
        connection.close();


        for (String sex: ySexList) {
            //y轴: 取不同的性别:男 女
            yNamelist.add(sex);
            List<String> yMnlist = new ArrayList<String>();
            for(int i = 0; i < orderAccountList1.size(); i++){
                if(sex.equals(orderAccountList1.get(i).getSex())){
                    //y轴: 取不同性别的金钱
                    yMnlist.add(orderAccountList1.get(i).getMn());
                }
            }
            mnMap1.put(sex,yMnlist);

        }


        Bar[] bar1 = new Bar[yNamelist.size()];
        for (int i = 0; i < yNamelist.size(); i++) {
            Bar b1 = new Bar(yNamelist.get(i).toString());
            b1.itemStyle().normal().label().show(true);

            List<String> b2 = mnMap1.get(yNamelist.get(i).toString());
            String[] data = new String[b2.size()];
            for (int j = 0; j < b2.size(); j++) {
                data[j] = b2.get(j);
            }

            b1.data(data);
            //a（系列名称），b（类目值），c（数值）, d（无）
            b1.label().normal().formatter("{a} {c}").position(Position.insideBottom).verticalAlign(VerticalAlign.middle).align(Align.left).setRotate(90);
            bar1[i] = b1;
        }

        //地址：http://echarts.baidu.com/examples/editor.html?c=bar-label-rotation
        EnhancedOption option1 = new EnhancedOption();
        option1.title("不同性别每月消费情况", "数据经过脱敏处理");
        option1.tooltip(Trigger.axis);
        option1.legend(ageArray1);
        option1.toolbox().show(true)
                .feature(
                        Tool.mark, Tool.dataView,
                        new MagicType(Magic.line, Magic.bar),
                        Tool.restore, Tool.saveAsImage);
        option1.calculable(true);
        option1.grid().y(70).y2(30).x2(20);

        option1.xAxis(new CategoryAxis().data(dateArray1));
        option1.yAxis(new ValueAxis().axisLabel(new AxisLabel().formatter("{value}")));

        option1.series(bar1);

        //将多个potion写入,生产多个图形的html
        List<Option> optionList = new ArrayList<Option>();
        optionList.add(option);
        optionList.add(option1);
        OptionUtil.exportToHtml(optionList,"/tmp/echarts","jd.html");

        System.out.println("====文件生成成功=====");
        //option.view();
    }

    public static class OrderAccount{
        private String age;
        private String mn;
        private String date;

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getMn() {
            return mn;
        }

        public void setMn(String mn) {
            this.mn = mn;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public static class OrderAccount1{
        private String sex;
        private String mn;
        private String date;

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) { this.sex = sex; }

        public String getMn() {
            return mn;
        }

        public void setMn(String mn) {
            this.mn = mn;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
    static class MyComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);//升序排列
        }
    }
}
