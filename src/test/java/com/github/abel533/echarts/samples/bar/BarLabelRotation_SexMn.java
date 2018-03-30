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

import com.github.abel533.echarts.axis.AxisLabel;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.util.EnhancedOption;

import java.sql.*;
import java.util.*;

/**
 * Created by banny on 2018/3/29.
 */
public class BarLabelRotation_SexMn {


    public static class OrderAccount{
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
    public static void main(String[] args) throws SQLException,ClassNotFoundException {
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        //Class.forName("org.apache.hive.jdbc.HiveDriver");

        Connection connection = DriverManager.getConnection("jdbc:presto://localhost:8080/hive/default","root",null);
                //getConnection("jdbc:hive2://localhost:10000/default", "banny", "banny");

        /** 输出数据格式:
         sex |         mn         | date
        -----+--------------------+-------
         -------+--------------------+---------
         女    |  200900.5474240672 | 2016-08
         男    |  33456.33864530749 | 2016-08
         女    |  228407.7329228234 | 2016-09
         男    | 38937.811564978896 | 2016-09
         女    | 221956.42468260508 | 2016-10
         男    |  36617.73850638348 | 2016-10
         女    | 202745.64704825153 | 2016-11
         男    | 33918.600673691486 | 2016-11
        */
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select case when sex=1 then '女' else '男' end, sum(loan_amount) as mn,date_format(loan_time, '%Y-%m') from t_user join t_loan on t_user.uid=t_loan.uid group by sex, date_format(loan_time,'%Y-%m') order by date_format(loan_time,'%Y-%m'),sex");

        List<String> yNamelist = new ArrayList<String>();

       //x轴: 时间
        Set<String> xdateList= new TreeSet<String>(new MyComparator());
        //y轴: 性别
        Set<String> ySexList= new TreeSet<String>(new MyComparator());
        List<OrderAccount> orderAccountList = new ArrayList<OrderAccount>();

        while (rs.next()) {
            OrderAccount orderAccount = new OrderAccount();
            orderAccount.setSex(rs.getString(1));
            orderAccount.setMn(rs.getString(2));
            orderAccount.setDate(rs.getString(3));
            orderAccountList.add(orderAccount);
            xdateList.add(rs.getString(3));
            ySexList.add(rs.getString(1));
        }
        System.out.println(xdateList.toString().substring(1, xdateList.toString().length()-1));
        String[] dateArray = xdateList.toString().substring(1, xdateList.toString().length()-1).split(",");
        System.out.println(ySexList.toString().substring(1, ySexList.toString().length()-1));
        String[] ageArray = ySexList.toString().substring(1,ySexList.toString().length()-1).replace(" ","").split(",");
        for (int i = 0; i < ageArray.length; i++) {
            System.out.println("======="+ageArray[i]);
        }


        Map<String,List> mnMap = new HashMap<String, List>();

        rs.close();
        connection.close();


        for (String sex: ySexList) {
            //y轴: 取不同的性别:男 女
            yNamelist.add(sex);
            List<String> yMnlist = new ArrayList<String>();
            for(int i = 0; i < orderAccountList.size(); i++){
                if(sex.equals(orderAccountList.get(i).getSex())){
                    //y轴: 取不同性别的金钱
                    yMnlist.add(orderAccountList.get(i).getMn());
                }
            }
            mnMap.put(sex,yMnlist);

        }


        Bar[] bar = new Bar[yNamelist.size()];
        for (int i = 0; i < yNamelist.size(); i++) {
            Bar b1 = new Bar(yNamelist.get(i).toString());
            b1.itemStyle().normal().label().show(true);

            List<String> b2 = mnMap.get(yNamelist.get(i).toString());
            String[] data = new String[b2.size()];
            for (int j = 0; j < b2.size(); j++) {
                data[j] = b2.get(j);
            }

            b1.data(data);
            //a（系列名称），b（类目值），c（数值）, d（无）
            b1.label().normal().formatter("{a} {c}").position(Position.insideBottom).verticalAlign(VerticalAlign.middle).align(Align.left).setRotate(90);
            bar[i] = b1;
        }

        //地址：http://echarts.baidu.com/examples/editor.html?c=bar-label-rotation
        EnhancedOption option = new EnhancedOption();
        option.title("京东金融信贷数据分析系统", "数据经过脱敏处理");
        option.tooltip(Trigger.axis);
        option.legend(ageArray);
        option.toolbox().show(true)
                .feature(
                        Tool.mark, Tool.dataView,
                        new MagicType(Magic.line, Magic.bar),
                        Tool.restore, Tool.saveAsImage);
        option.calculable(true);
        option.grid().y(70).y2(30).x2(20);

        option.xAxis(new CategoryAxis().data(dateArray));
        option.yAxis(new ValueAxis().axisLabel(new AxisLabel().formatter("{value}")));

        option.series(bar);
        option.exportToHtml("1.html");


    }

}
