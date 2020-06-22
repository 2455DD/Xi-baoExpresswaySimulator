package Object;

import java.util.*;

/**
 * Station类<br>
 * 该类是对车站及其行为的模拟
 */
public class Station {
    /**全局车站映射，Integer为no*/
    public static Map<Integer,Station> stationMap=new HashMap<Integer, Station>();
    /**全局车站数量*/
    static int totalStation=1;
    /**车站识别码*/
    int no;
    /**车站全称，中文，用于输出*/
    String fullName;
    /**车站简称*/
    String Name;
    /**当前车站的载具队列，实行FIFO模型*/
    public Queue<Carrier> carrierQueue=new LinkedList<Carrier>();
    /**当前车站的乘员队列，实行FIFO模型，应当仅在首发站使用*/
    public Queue<Passenger> passengerInStation=new LinkedList<Passenger>();
    /**距宝鸡方向下一站的距离*/
    int DistanceToFormer;
    /**距西安方向下一站的距离*/
    int DistanceToLatter;
    /**到站人数*/
    int passengerDownload=0;
    /**距宝鸡方向下一站*/
    public Station next;
    /**距西安方向下一站*/
    public Station before;
    /**是否为首站，默认为否*/
    public boolean firstStation=false;
    /**站内等待乘客人数*/
    public int passengerNumberinStation=0;

    /**
     * 默认构造函数<br>
     *     通过查询映射中该站的上一站(向宝鸡方向的下一站)来定义<code>before</code>以及上一站(向宝鸡方向的下一站)的<code>next</code>，以此来循环定义<br>
     *     并为现在站点命名<br>
     * @param fn-全称
     * @param n-简称
     * @param dtf-距上一站(向宝鸡方向的下一站)
     */
    public Station(String fn,String n,int dtf){
        this.no=totalStation-1;
        if(totalStation!=1)
        {
            this.before=Station.stationMap.get(no-1);
            before.next=this;
            before.DistanceToLatter=dtf;
        }
        else{
            this.DistanceToFormer=0;
            firstStation=true;
        }
        if(totalStation==7){
            firstStation=true;
            this.DistanceToLatter=0;
        }
        this.fullName=fn;
    	this.Name=n;
    	this.DistanceToFormer=dtf;
    	totalStation++;
    	Integer tempNO=no;
    	Station.stationMap.put(tempNO,this);
    }

    /**
     *返回全程
     * @return 车站全称(中文)
     */
    public String returnFullName() {
    	return this.fullName;
    }

    /**
     *返回到上一站的距离
     * @return 上一站的距离
     */
    public int distancetoFormerStation()
    {
    	return this.DistanceToFormer;
    }
    /**
     *返回到下一站的距离
     * @return 下一站的距离
     */
    public int distancetoLatterStation()
    {
        return this.DistanceToLatter;
    }

    /**
     * 生成乘客并将乘客加入到<code>passengerInStation</code>中
     */
    public void generatePassenger(){
        Passenger newPassenger=new Passenger();
        this.passengerInStation.add(newPassenger);
        this.passengerNumberinStation++;
    }

    /**
     * 依照参数生成载具并指定其方向
     * @param type-载具种类
     * @param target-方向
     * @param e-当前车站
     * @return 生成车辆的uid
     */
    public int generateCarrier(String type,int target,Station e){
        if(type.equals("Iveco")){
            Carrier newCarrier = new Iveco(target, e);
            this.carrierQueue.add(newCarrier);
            return newCarrier.uid;
        }
        else {
            Carrier newCarrier = new Volve(target, e);
            this.carrierQueue.add(newCarrier);
            return newCarrier.uid;
        }
    }

}

