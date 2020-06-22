/**
 * 宝鸡-西安高速公路模拟主程序
 *   @author 何浩文、马元浩、赵靖宇、李科燃
 *   @version 1.0
 */

import java.io.*;
import java.lang.*;
import Object.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.text.SimpleDateFormat;

/**
 * Simulator类<br>
 * 该类的主要作用是执行模拟过程<br>
 */
public class Simulator{
    /**以秒表示的模拟时间*/
    int currentTime;                      //时间放大比 1:60,单位为秒
    /**当前分钟数*/
    int mins;
    /**当前小时数*/
    int hours;
    /**timer类的线程(已初始化)
     * @see timer
     */
    Thread timerThread = new timer(this);
    /**以passengerGenerate类线程(已初始化)
     * @see passengerGenerate
     */
    Thread passengerGenerateThread = new passengerGenerate(this);
    /**JudgeAction类线程(已初始化)
     * @see JudgeAction
     */
    Thread judgeThread = new JudgeAction(this);
    /**在运行中的车辆集合[线程安全]*/
    Set<Carrier> carrierOnRoad=Collections.newSetFromMap(new ConcurrentHashMap<Carrier, Boolean>());
    /**已生成的所有站点的映射，Integer为对应uid*/
    Map<Integer,Station> totalStationMap=new HashMap<Integer,Station>();
    /**输出的文件url*/
    File log=new File("Xi-baoExpresswaySimulator\\src\\log-"+getSystemDate()+".txt");

    /**
     * 该方法是该类的构造函数
     * 具体完成的工作是将Simulator类的初始时间重置
     */
    Simulator() {
        currentTime =0;
        mins = 30;
        hours = 7;
    }
    //模拟初始化函数

    /**
     * 该方法是在模拟过程中对公路状况的初始化<br>
     * 具体而言，他从/src/input.txt中读取头四个int分别作为西安和宝鸡Volve（V）和Iveco(I）的数量<br>
     * 然后在以”站名、简称以及到前站的距离“的顺序从接下来的每一行中读取数据并生成车站<br>
     * 最后在调用两个始发站的<code>generateCarrier</code>生成车辆并将指定该车辆<code>Carrier.nextStation</code>为本站<br>
     * @see Station
     * @see Carrier
     * @throws FileNotFoundException 路径未找到时
     *
     */
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

    /**
     * 生成当前系统时间并以年月日_小时_分钟_秒的形式返回
     * @return String-当前系统时间
     */
    String getSystemDate(){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyyMMdd_HH_mm_ss");
        Date date=new Date();
        return sdf.format(date);
    }

    /**
     * 返回当前时间，格式为HH时mm分
     * @return String-当前时间
     */
    String returnCurrentTime () {
        return hours +"时 "+ mins +"分 ";
    }

    /**
     * 将一个字符串输出到标准输出流并将该字符串以追加的模式写入<code>Simulator.log</code>
     * @param message-String 待处理字符串
     */
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

    /**
     * 主模拟过程，具体发生的事件为：
     *      新建一个<code>Simulator</code>类对象Simulation
     *      执行<code>Simulation.init</code>
     *      在18点前并且路上还有车时：
     *          新建一个<code>timer</code>的线程并启动
     *          新建一个<code>passengerGenerate</code>的线程并启动
     *          执行judgeThread
     * @param args 命令行参数
     * @see timer
     * @see passengerGenerate
     * @see JudgeAction
     */
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

/**
 * JudgeAction类<br>
 * 通过继承Thread类实现多线程<br>
 * 该类的作用主要是判断两个首发站是否该发车以及路上每辆车的行为<br>
 * @see java.lang.Thread
 * @see Carrier
 * @see Station
 */
class JudgeAction extends Thread {
    /**初始化用*/
    private Thread thread;
    /**指定作用的模拟过程*/
    Simulator Simulation;

    /**
     * JudgeAction构造函数<br>
     * 接受一个参数<code>Simulator</code>a来指明作用的模拟过程<br>
     * @param a <code>Simulator</code>-指明作用过程，为<code>JudgeAction.Simulation</code>赋值
     */
    JudgeAction(Simulator a) {
            Simulation = a;
    }

    /**
     * 线程的运行流程<br>
     *      <p>设立一个标志<code>boolean:flag</code>来判断一个车站是否已经发出了车次(即实现一次一站只发一辆车)<br>
     *      然后判断路上每辆车的运行状态<br></p>
     */
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

    /**
     * 判断每辆车的运行状态<br>
     * <p>具体而言：<br>
     *      如果没有到站则继续运行<br>
     *      如果到站且为终点站则所有成员下车、从<code>Simulation.carrierOnRoad</code>中删除、转向并加入该站的发车序列<br>
     *      如果到非起始站就判断是否有人员下车，有则休眠2min,无则不休眠，并调用<code>Simulation.writeLog</code>进行记录</p>
     * @param e <code>Carrier</code>-哪辆车在接受判别
     * @see Simulator
     * @see Carrier
     */
    void judge(Carrier e) {

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

    /**
     * 启动线程
     */
    @Override
    public synchronized void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * 判断在始发站的车辆是否该发车
     * @param pos <code>int</code>-是哪个车站
     * @return <code>boolean</code> 是否成功发车——是则为<code>false</code>，没有则为<code>true</code>
     */
    boolean startCar(int pos){
        Carrier e=Simulation.totalStationMap.get(pos).carrierQueue.peek();
        if(e==null)return false;
        boolean volveJudge=e.carrierType.equals("Volve")&&Simulation.hours>=8&&Simulation.hours<18&&Simulation.mins==30;
        boolean ivecoJudge=e.carrierType.equals("Iveco")&&Simulation.hours>=8&&Simulation.hours<=18&&Simulation.mins%20==0;
        //如何实现判断时间到了以后再发车呢
        if(!volveJudge&&!ivecoJudge)return false;
        //发车时间Satrt
        if(pos==1){         //宝鸡站
            while(e.isEmpty()&&Simulation.totalStationMap.get(1).passengerInStation.peek()!=null){//只要e还没有满、站里还有人等上车，并且在指定时间内
                //所谓isEmpty实际上是指e不是被塞满了，望周知
                e.passengerEmbark(Simulation.totalStationMap.get(1).passengerInStation.poll());
                Simulation.totalStationMap.get(1).passengerNumberinStation--;
            }
                System.out.println(Simulation.returnCurrentTime()+e.uid+e.carrierType+"从"+e.nextStation.returnFullName()+"开车了");
                Simulation.carrierOnRoad.add(e);
                Simulation.totalStationMap.get(pos).carrierQueue.poll();        //将乘客以FIFO原则顶出
                e.leaveStation();
                return false;
        }
        if(pos==7)          //西安站
        {
            while(e.isEmpty() && !Simulation.totalStationMap.get(7).passengerInStation.isEmpty()) {

                //只要e还没有满、站里还有人等上车，并且在指定时间内
                // 所谓isEmpty实际上是指e不是被塞满了，望周知
                e.passengerEmbark(Simulation.totalStationMap.get(7).passengerInStation.poll());
                Simulation.totalStationMap.get(7).passengerNumberinStation--;
            }
                System.out.println(Simulation.returnCurrentTime() + e.uid + e.carrierType + "从" + e.nextStation.returnFullName() + "开车了");
                Simulation.carrierOnRoad.add(e);
                Simulation.totalStationMap.get(pos).carrierQueue.poll();        //将乘客以FIFO原则顶出
                e.leaveStation();
                return false;
        }
        return true;
    }
}

/**
 * timer类<br>
 * 通过继承Thread类实现多线程<br>
 * 该类的作用主要是改变时间<br>
 */
class timer extends Thread{
    /**指明作用的模拟过程*/
    Simulator simulation;
    /**
     * timer构造函数<br>
     * <p>接受一个参数<code>Simulator</code>simulations来指明作用的模拟过程</p>
     * @param simulations <code>Simulator</code>-指明作用过程，为<code>JudgeAction.Simulation</code>赋值
     */
    timer(Simulator simulations) {
        simulation=simulations;
    }

    /**
     * 线程的运行流程<br>
     *      <p>通过执行<code>timechange</code>改变时间后睡眠<br>
     *      然后判断路上每辆车的运行状态</p>
     */
    @Override
    public void run() {
        timeChange();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.out.println("错误：线程错误(类JudgeAction)，运行时间为" + returnCurrentTime());
            e.printStackTrace();
        }

    }

    /**
     * 改变Simulation时间
     */
    void timeChange(){
        simulation.currentTime += 60;
        if(simulation.mins<59)simulation.mins +=1;
        else{
            simulation.mins=0;
            simulation.hours++;
        }
    }

    /**
     * 返回现在字段<code>simulation</code>的时间
     * @return  <code>String</code>当前时间，格式为"HH时mm分"
     */
    String returnCurrentTime () {
        return simulation.hours +"时 "+ simulation.mins +"分 ";
    }
}

/**
 * passengerGenerate类<br>
 * 通过继承Thread类实现多线程<br>
 *     <p>该类的作用主要是在始发站生成乘客并加入到<code>Station.passengerInStation</code>中</p>
 */
class passengerGenerate extends Thread{
    /**初始化用*/
    private Thread thread;
    /**指定作用的模拟过程*/
    Simulator simulator;

    /**
     * passengerGenerate的构造函数<br>
     * 接受一个参数<code>Simulator</code>a来指明作用的模拟过程
     * @param a <code>Simulator</code>-指明作用过程，为<code>passengerGenerate.simulator</code>赋值
     */
    passengerGenerate(Simulator a) {
        simulator=a;
    }

    /**
     * 线程的运行流程<br>
     *      <p>执行<code>Station.generatePassenger</code>方法</p>
     */
    @Override
    public void run() {
            simulator.totalStationMap.get(1).generatePassenger();
            simulator.totalStationMap.get(7).generatePassenger();
        }
    /**
     * 启动线程
     */
    @Override
    public synchronized void start() {
        if (thread == null) {
            thread = new Thread (this);
            thread.start ();
        }
    }

}
