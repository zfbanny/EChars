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

import com.github.abel533.echarts.axis.*;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.style.ItemStyle;
import com.github.abel533.echarts.util.EnhancedOption;
import javafx.scene.transform.Rotate;
import org.junit.Test;

import java.sql.*;
import java.util.*;

/**
 * Created by banny on 2018/3/29.
 */
public class BarLabelRotation_AgeMn {


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
        Set<String> xDateSet= new TreeSet<String>(new MyComparator());
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

        rs.close();
        connection.close();


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
            //y轴: 不同的年龄的金钱
            mnMap.put(age,yMnlist);

        }

        //生成Y轴各年龄的消费额
        Bar[] bar = new Bar[yAgeList.size()];
        for (int i = 0; i < yAgeList.size(); i++) {
            Bar b1 = new Bar(yAgeList.get(i).toString());
            b1.itemStyle().normal().label().show(true);

            List<String> b2 = mnMap.get(yAgeList.get(i).toString());
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
        option.exportToHtml("BarLabelRotation_AgeMn.html");


    }

}
