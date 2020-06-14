import java.lang.*;
import Object.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Simulator extends Thread {
    int currentTime;                      //时间放大比 1:630,单位为秒，判断依据为21/2
    int secs;
    int mins;
    int hours;
    Set<Carrier> carrierOnRoad=new HashSet<Carrier>();
    Map<Integer,Station> totalStationMap=new HashMap<Integer,Station>();
    //Under Construction

    /*          To-Do List
        1、实现时间的迭代变化——暂定使用线程睡眠+死循环（满足x条件跳出）
        2、随时间生成Carrier
        3、实现数据统计
        4、Java-IO到文件以及可能的GUI设计
                                                    ——何浩文
     */
    Simulator() {
        currentTime = 0;
        secs = 0;
        mins = 0;
        hours = 0;
    }


    public static void main(String[] args) throws InterruptedException {
        Simulator Simulation = new Simulator();
        Simulation.init();
        while (true) {
            if (Simulation.returnCurrentTime() >= 37940) break;  //(18-7.5)*60*60+((24+21+62+21+24+22)/1.4+7*2)
            else {
                Simulation.timeChange();
                Simulator.sleep(1000);
            }
        }
    }
        void init(){
            Scanner scanner=new Scanner(System.in);
            int xnv,xni,bjv,bji;
            System.out.println("请依次输入XN拥有沃尔沃和依维柯客车(分别为XNW和XNY辆)，BJ拥有沃尔沃和依维柯客车（分别为BJW和BJY辆）:");
            xnv=scanner.nextInt();xni=scanner.nextInt();bjv=scanner.nextInt();bji=scanner.nextInt();
            //车站初始化
            for(int i=0;i<7;i++){
                System.out.println("请依次输入站名、简称（前两个请输入完后换行）以及到前站的距离：");
                String fnn= scanner.nextLine();
                String abn= scanner.nextLine();
                int fmd=scanner.nextInt();
                this.totalStationMap.put(i+1,new Station(fnn,abn,fmd));
                System.out.println("第"+(i+1)+"车站"+fnn+"已添加.");
            }
            //初始车辆生成
            for(int i=0;i<xnv;i++){
                this.totalStationMap.get(1).generateCarrier("Volve",2);
            }
            for(int i=0;i<xni;i++){
                this.totalStationMap.get(1).generateCarrier("Iveco",2);
            }
            for(int i=0;i<bji;i++){
                this.totalStationMap.get(7).generateCarrier("Iveco",1);
            }
            for(int i=0;i<bjv;i++){
                this.totalStationMap.get(7).generateCarrier("Volve",1);
            }
        }
        int returnCurrentTime () {
            return this.hours * 60 * 60 + this.mins * 60 + this.secs;
        }
        void timeChange () {
            this.currentTime += 630;
            this.secs = this.currentTime % 60;
            this.mins = this.currentTime / 60;
            this.hours = this.mins / 60;
            if (this.currentTime != 0) this.mins %= this.mins;
        }


}

