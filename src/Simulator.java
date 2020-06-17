import java.io.*;
import java.lang.*;
import Object.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Simulator extends Thread {
    int currentTime;                      //时间放大比 1:60,单位为秒
    int mins;
    int hours;
    Set<Carrier> carrierOnRoad=new HashSet<Carrier>();
    Map<Integer,Station> totalStationMap=new HashMap<Integer,Station>();
    File log=new File("./log.txt");
    timer timeCounter=new timer();
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
        mins = 0;
        hours = 0;
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
            int uid=this.totalStationMap.get(1).generateCarrier("Volve",2);
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(2);
        }
        for(int i=0;i<xni;i++){
            int uid=this.totalStationMap.get(1).generateCarrier("Iveco",2);
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(2);
        }
        for(int i=0;i<bji;i++){
            int uid=this.totalStationMap.get(7).generateCarrier("Iveco",1);
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(2);
        }
        for(int i=0;i<bjv;i++){
            int uid=this.totalStationMap.get(7).generateCarrier("Volve",1);
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(2);
        }
    }
    String returnCurrentTime () {
        return this.hours +"时 "+ this.mins +"分 ";
    }
    public void timeChange(){
        this.currentTime += 60;
        if(this.mins<50)this.mins +=1;
        else{
            this.mins=0;
            this.hours++;
        }
    }
    public void writeLog(String message){
        try (Writer writer = new FileWriter(log)) {
            // 把内容转换成字节数组
            char[] data = message.toCharArray();
            // 向文件写入内容
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Simulator Simulation = new Simulator();
        Simulation.init();
        Simulation.timeCounter.start();
        while (true) {
            Simulation.timeCounter.run();
            if (Simulation.hours>= 18&&Simulation.carrierOnRoad.isEmpty()) break;  //(18-7.5)*60*60+((24+21+62+21+24+22)/1.4+7*2)
            else {
                for(Carrier e:Simulation.carrierOnRoad){
                    JudgeAction judgeAction=new JudgeAction(Simulation,e);
                    judgeAction.start();
                }
            }
        }
    }
    class passengerGenerate extends Thread{
        private Thread thread;

        @Override
        public void run() {
            while(hours<18){
                totalStationMap.get(1).generatePassenger();
                totalStationMap.get(7).generatePassenger();
            }
        }

        @Override
        public synchronized void start() {
            if (thread == null) {
                thread = new Thread (this);
                thread.start ();
            }
        }
    }
    class timer extends Thread{
        Simulator Simulation;
        private Thread thread;

        @Override
        public synchronized void start() {
            if (thread == null) {
                thread = new Thread (this);
                thread.start ();
        }
    }
        @Override
        public void run() {
            Simulation.timeChange();
            try {
                Simulator.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("错误：线程错误(类JudgeAction)，运行时间为" + Simulation.returnCurrentTime());
                e.printStackTrace();
            }
        }
    }
}

class JudgeAction extends Thread {

    private Thread thread;
    Simulator Simulation;
    Carrier e;

    JudgeAction(Simulator a, Carrier b) {
        Simulation = a;
        e = b;
    }

    @Override
    public void run() {
        try {
            judge();
        } catch (InterruptedException interruptedException) {
            System.out.println("错误：线程错误(类JudgeAction)，运行时间为" + Simulation.returnCurrentTime());
            interruptedException.printStackTrace();
        }

    }

    void judge() throws InterruptedException {
        if (++e.timerFromLeave * e.speed < e.nextStation.distancetoFormerStation()) e.timerFromLeave++;
        else {
            int downloadNum = e.arriveStation();
            Simulation.writeLog(Simulation.returnCurrentTime() + "编号为" + e.uid + "的" + e.carrierType + "到达站点并有" + downloadNum + "人下车\n");
            if (downloadNum != 0) {
                Simulation.timeChange();
                Simulation.timeChange();
                Thread.sleep(2000);
                e.leaveStation();
            }
        }
    }

    @Override
    public synchronized void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }
}
