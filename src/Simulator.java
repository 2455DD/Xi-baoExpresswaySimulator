import java.io.*;
import java.lang.*;
import Object.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.text.SimpleDateFormat;

public class Simulator extends Thread{
    int currentTime;                      //时间放大比 1:60,单位为秒
    int mins;
    int hours;
    Thread timerThread = new timer(this);
    Thread passengerGenerateThread = new passengerGenerate(this);
    Thread judgeThread = new JudgeAction(this);

    Set<Carrier> carrierOnRoad=Collections.newSetFromMap(new ConcurrentHashMap<Carrier, Boolean>());
    Map<Integer,Station> totalStationMap=new HashMap<Integer,Station>();

    File log=new File("Xi-baoExpresswaySimulator\\src\\log-"+getSystemDate()+".txt");
//    timer timeCounter=new timer();
    //构造函数——重置时间
    Simulator() {
        currentTime =0;
        mins = 30;
        hours = 7;
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
            int uid=this.totalStationMap.get(1).generateCarrier("Volve",1,this.totalStationMap.get(1));
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(1);
        }
        for(int i=0;i<xni;i++){
            int uid=this.totalStationMap.get(1).generateCarrier("Iveco",1,this.totalStationMap.get(1));
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(1);
        }
        for(int i=0;i<bji;i++){
            int uid=this.totalStationMap.get(7).generateCarrier("Iveco",2,this.totalStationMap.get(7));
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(7);
        }
        for(int i=0;i<bjv;i++){
            int uid=this.totalStationMap.get(7).generateCarrier("Volve",2,this.totalStationMap.get(7));
            Carrier.carrierMap.get(uid).nextStation=this.totalStationMap.get(7);
        }
    }
    String getSystemDate(){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyyMMdd_HH_mm_ss");
        Date date=new Date();
        return sdf.format(date);
    }
    String returnCurrentTime () {
        return hours +"时 "+ mins +"分 ";
    }
    public void writeLog(String message){
        System.out.println(message);
        try (Writer writer = new FileWriter(log,true)) {
            // 把内容转换成字节数组
            char[] data = message.toCharArray();
            // 向文件写入内容
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        Simulator Simulation = new Simulator();
        //初始化模拟过程——构建车站与初始车辆
        try {
            Simulation.init();
        }
        catch (FileNotFoundException e){
            System.out.println("文件Input.txt不存在");
            e.printStackTrace();
        }

        //模拟过程
        while (true) {
            try{
                Thread timeThread=new timer(Simulation);
                timeThread.start();
                timeThread.join();
                if((Simulation.hours==7&&Simulation.mins>=30)||(Simulation.hours>7&&Simulation.hours<18)){        //只在规定时间内发车
                    Simulation.passengerGenerateThread=new passengerGenerate(Simulation);
                    Simulation.passengerGenerateThread.start();
                    Simulation.passengerGenerateThread.join();
                }

            }
            catch(Exception e){e.printStackTrace();}
            if (Simulation.hours>= 18&&Simulation.carrierOnRoad.isEmpty()) break;   //(18-7.5)*60*60+((24+21+62+21+24+22)/1.4+7*2)
            else {
                    Simulation.judgeThread.run();
                }
            }
        }
    }

class JudgeAction extends Thread {
    private Thread thread;
    Simulator Simulation;
    JudgeAction(Simulator a) {
            Simulation = a;
    }
    @Override
    public void run() {
            boolean flag=true;
            while(flag){
                flag=startCar(1);
            }
            flag=true;
            while(flag){
                flag=startCar(7);
            }
            for(Carrier e:Simulation.carrierOnRoad)
            {judge(e);}
        }


    void judge(Carrier e) {
        //boolean volveJudge=e.carrierType.equals("Volve")&&Simulation.hours>=8&&Simulation.hours<18&&Simulation.mins==30;
        //boolean ivecoJudge=e.carrierType.equals("Iveco")&&Simulation.hours>=8&&Simulation.hours<=18&&Simulation.mins%20==0;

        try {
            if ((e.target == 1&&e.DistanceToFormerStation < e.nextStation.distancetoFormerStation()) || ( e.target == 2 &&e.DistanceToFormerStation < e.nextStation.distancetoLatterStation()))
                e.carrierMovePerMinutes();
            else {
                if(e.nextStation.equals(Simulation.totalStationMap.get(7))&&e.target==1){
                    e.target=2;
                    Simulation.writeLog(Simulation.returnCurrentTime() + "编号为" + e.uid + "的" + e.carrierType +"到达终点站" + e.nextStation.returnFullName() +"\n");
                    Simulation.carrierOnRoad.remove(e);
                    for(Passenger a:e.passengerCollection){
                        e.passengerCollection.remove(a);
                    }
                    Simulation.totalStationMap.get(7).carrierQueue.add(e);
                    return;
                }
                if(e.nextStation.equals(Simulation.totalStationMap.get(1))&&e.target==2){
                    e.target=1;
                    Simulation.writeLog(Simulation.returnCurrentTime() + "编号为" + e.uid + "的" + e.carrierType +"到达终点站" + e.nextStation.returnFullName() +"\n");
                    Simulation.carrierOnRoad.remove(e);
                    Simulation.totalStationMap.get(1).carrierQueue.add(e);
                    for(Passenger a:e.passengerCollection){
                        e.passengerCollection.remove(a);
                    }
                    return;
                }
                int downloadNum = e. arriveStation();
                if (downloadNum != 0) {
                    Simulation.writeLog(Simulation.returnCurrentTime() + e.uid + e.carrierType + "于" + e.nextStation.returnFullName() + "靠站2min让乘客下车" + "并有" + downloadNum + "人下车\n");
                    Simulation.timerThread.run();Simulation.timerThread.run();
                }
                else   Simulation.writeLog(Simulation.returnCurrentTime() + e.uid + e.carrierType + "于" + e.nextStation.returnFullName() + "靠站，无人下车\n");
                Simulation.writeLog(Simulation.returnCurrentTime() + e.uid + e.carrierType + "从" + e.nextStation.returnFullName() + "再出发了\n");
                e.leaveStation();
                }
        }
        catch(NullPointerException ex){
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

    boolean startCar(int pos){
        Carrier e=Simulation.totalStationMap.get(pos).carrierQueue.peek();
        if(e==null)return false;
        boolean volveJudge=e.carrierType.equals("Volve")&&Simulation.hours>=8&&Simulation.hours<18&&Simulation.mins==30;
        boolean ivecoJudge=e.carrierType.equals("Iveco")&&Simulation.hours>=8&&Simulation.hours<=18&&Simulation.mins%20==0;
        //如何实现判断时间到了以后再发车呢
        //System.out.println(e.uid+"正在运行"+e.returnLocation());
        if(!volveJudge&&!ivecoJudge)return false;
        //发车时间Satrt
        if(pos==1){         //如果车子在首站宝鸡且方向是从左向右
            while(e.isEmpty()&&Simulation.totalStationMap.get(1).passengerInStation.peek()!=null){//只要e还没有满、站里还有人等上车，并且在指定时间内
                //所谓isEmpty实际上是指e不是被塞满了，望周知
                e.passengerEmbark(Simulation.totalStationMap.get(1).passengerInStation.poll());
                Simulation.totalStationMap.get(1).passengerNumberinStation--;
            }
                System.out.println(Simulation.returnCurrentTime()+e.uid+e.carrierType+"从"+e.nextStation.returnFullName()+"开车了");
                Simulation.carrierOnRoad.add(e);
                Simulation.totalStationMap.get(pos).carrierQueue.poll();
                e.leaveStation();
                return false;
        }
        if(pos==7) {
            while(e.isEmpty() && !Simulation.totalStationMap.get(7).passengerInStation.isEmpty()) {

                //只要e还没有满、站里还有人等上车，并且在指定时间内
                // 所谓isEmpty实际上是指e不是被塞满了，望周知
                e.passengerEmbark(Simulation.totalStationMap.get(7).passengerInStation.poll());
                Simulation.totalStationMap.get(7).passengerNumberinStation--;
            }
                System.out.println(Simulation.returnCurrentTime() + e.uid + e.carrierType + "从" + e.nextStation.returnFullName() + "开车了");
                Simulation.carrierOnRoad.add(e);
                Simulation.totalStationMap.get(pos).carrierQueue.poll();
                e.leaveStation();
                return false;
        }
        return true;
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
        if(simulation.mins<59)simulation.mins +=1;
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
