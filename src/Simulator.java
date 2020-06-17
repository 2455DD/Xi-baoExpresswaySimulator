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
    File log=new File("D:\\Develop\\ExpressWaySimulation\\Xi-baoExpresswaySimulator\\Xi-baoExpresswaySimulator\\src\\log.txt");
//    timer timeCounter=new timer();
    //构造函数——重置时间
    Simulator() {
        currentTime =0;
        mins = 30;
        hours = 6;
    }
    //模拟初始化函数
    void init() throws FileNotFoundException {
        //使用Scanner类读取文件来实现对车站和初始车辆的生成，位置为/src/input.txt，要注意的是编码一定要写
        Scanner scanner=new Scanner(new File("D:\\Develop\\ExpressWaySimulation\\Xi-baoExpresswaySimulator\\Xi-baoExpresswaySimulator\\src\\input.txt"),"Unicode");
        int xnv,xni,bjv,bji;//分别为西安和宝鸡的Volve（V）和Iveco(I）
        xnv=scanner.nextInt();
        xni=scanner.nextInt();
        bjv=scanner.nextInt();
        bji=scanner.nextInt();
        //车站初始化
        for(int i=0;i<7;i++){
            //System.out.println("请依次输入站名、简称（前两个请输入完后换行）以及到前站的距离：");
            String fnn= scanner.next();
            String abn= scanner.next();
            int fmd=scanner.nextInt();
            System.out.println();
            this.totalStationMap.put(i+1,new Station(fnn,abn,fmd));
            System.out.println("第"+(i+1)+"车站"+fnn+"已添加.");
        }
        for(int i=0;i<xnv;i++){
            int uid=this.totalStationMap.get(1).generateCarrier("Volve",2,this.totalStationMap.get(1));
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(2);
            this.carrierOnRoad.add(Carrier.carrierMap.get(uid));
        }
        for(int i=0;i<xni;i++){
            int uid=this.totalStationMap.get(1).generateCarrier("Iveco",2,this.totalStationMap.get(1));
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(2);
            this.carrierOnRoad.add(Carrier.carrierMap.get(uid));
        }
        for(int i=0;i<bji;i++){
            int uid=this.totalStationMap.get(7).generateCarrier("Iveco",1,this.totalStationMap.get(7));
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(2);
            this.carrierOnRoad.add(Carrier.carrierMap.get(uid));
        }
        for(int i=0;i<bjv;i++){
            int uid=this.totalStationMap.get(7).generateCarrier("Volve",1,this.totalStationMap.get(7));
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(2);
            this.carrierOnRoad.add(Carrier.carrierMap.get(uid));
        }
    }
    public void timeChange(){
        this.currentTime += 60;
        if(this.mins<50)this.mins +=1;
        else{
            this.mins=0;
            this.hours++;
        }
    }
    String returnCurrentTime () {
        return hours +"时 "+ mins +"分 ";
    }
    public void writeLog(String message){
        System.out.println(message);
        try (Writer writer = new FileWriter(log)) {
            // 把内容转换成字节数组
            char[] data = message.toCharArray();
            // 向文件写入内容
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args)  {
        Simulator Simulation = new Simulator();
        Thread mainThread=new Thread();
        //初始化模拟过程——构建车站与初始车辆
        try {
            Simulation.init();
        }
        catch (FileNotFoundException e){
            System.out.println("文件Input.txt不存在");
            e.printStackTrace();
        }
        Thread timerThread=new timer(Simulation);
        Thread passengerGenerateThread=new passengerGenerate(Simulation);
        Thread judgeThread=new JudgeAction(Simulation);
        //模拟过程
        while (true) {
            try{timerThread.run();
                timerThread.join();
                passengerGenerateThread.run();
                passengerGenerateThread.join();
            }
            catch(Exception e){e.printStackTrace();}
            if (Simulation.hours>= 18||Simulation.carrierOnRoad.isEmpty()) break;   //(18-7.5)*60*60+((24+21+62+21+24+22)/1.4+7*2)
            else {
                    judgeThread.run();
                }
            }
        }
    }



class JudgeAction extends Thread {

    private Thread thread;
    Simulator Simulation;
    Carrier e;
    int temp=0;
    JudgeAction(Simulator a) {
        Simulation = a;
    }
    @Override
    public void run() {
        try {
            for(Carrier e:Simulation.carrierOnRoad)
            {judge(e);}
        } catch (InterruptedException interruptedException) {
            System.out.println("错误：线程错误(类JudgeAction)，运行时间为" + Simulation.returnCurrentTime());
            interruptedException.printStackTrace();
        }

    }

    void judge(Carrier e) throws InterruptedException {
        boolean flag1=true;
        boolean flag2=true;
        System.out.println(temp++);
        for(Carrier a:Simulation.totalStationMap.get(1).carrierQueue){
            if(e.equals(a)&&flag1){
                for(int i=0;!e.queueIsFull&&Simulation.totalStationMap.get(1).passengerNumberinStation>0;i++){
                    if(!Simulation.totalStationMap.get(1).passengerInStation.isEmpty()){
                        e.passengerEmbark(Simulation.totalStationMap.get(1).passengerInStation.get(Simulation.totalStationMap.get(1).passengerNumberinStation-1));
                        Simulation.totalStationMap.get(1).passengerInStation.remove(Simulation.totalStationMap.get(1).passengerNumberinStation-1);
                        Simulation.totalStationMap.get(1).passengerNumberinStation--;
                    }
                    else{flag1=false;break;}
                }
            }
        }
        for(Carrier a:Simulation.totalStationMap.get(7).carrierQueue){
            if(e.equals(a)&&flag2){
                for(int i=0;!e.queueIsFull&&Simulation.totalStationMap.get(7).passengerNumberinStation>0;i++){
                    if(!Simulation.totalStationMap.get(7).passengerInStation.isEmpty()){
                        e.passengerEmbark(Simulation.totalStationMap.get(7).passengerInStation.get(Simulation.totalStationMap.get(7).passengerNumberinStation-1));
                        Simulation.totalStationMap.get(7).passengerInStation.remove(Simulation.totalStationMap.get(7).passengerNumberinStation-1);
                        Simulation.totalStationMap.get(7).passengerNumberinStation--;
                    }
                    else{flag2=false;break;}
                }
            }
        }
        try {
            if ((e.DistanceToFormerStation < e.nextStation.distancetoFormerStation() && e.target == 1) || (e.DistanceToFormerStation < e.nextStation.distancetoLatterStation() && e.target == 2))
                e.carrierMovePerMinutes();
            else {
                int downloadNum = e.arriveStation();
                Simulation.writeLog(Simulation.returnCurrentTime() + "编号为" + e.uid + "的" + e.carrierType + "到达站点" + e.nextStation.returnFullName() + "并有" + downloadNum + "人下车\n");
                if (downloadNum != 0) {
                    Thread.sleep(20);
                    e.leaveStation();
                }
            }
        }
        catch(Exception ex){
            System.out.println(e.uid+"号车在"+e.nextStation+"处翻车，时间是"+Simulation.returnCurrentTime());
            ex.printStackTrace();
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
class timer extends Thread{

    Simulator simulation;

    timer(Simulator simulations) {
        simulation=simulations;
    }

    @Override
    public void run() {
        timeChange();
        try {
            Simulator.sleep(10);
        } catch (InterruptedException e) {
            System.out.println("错误：线程错误(类JudgeAction)，运行时间为" + returnCurrentTime());
            e.printStackTrace();
        }

    }
    void timeChange(){
        simulation.currentTime += 60;
        if(simulation.mins<50)simulation.mins +=1;
        else{
            simulation.mins=0;
            simulation.hours++;
        }
    }
    String returnCurrentTime () {
        return simulation.hours +"时 "+ simulation.mins +"分 ";
    }
}

class passengerGenerate extends Thread{
    private Thread thread;
    Simulator simulator;
    passengerGenerate(Simulator a) {
        simulator=a;
    }

    @Override
    public void run() {
            simulator.totalStationMap.get(1).generatePassenger();
            simulator.totalStationMap.get(7).generatePassenger();
        }
    @Override
    public synchronized void start() {
        if (thread == null) {
            thread = new Thread (this);
            thread.start ();
        }
    }

}
