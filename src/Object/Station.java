package Object;

import java.util.*;

public class Station {
    public static Map<Integer,Station> stationMap=new HashMap<Integer, Station>();
    static int totalStation=1;
    int no;                     //编码
	String Fullname;            //全称：用于输出
    String Name;                //简写
    public Queue<Carrier> carrierQueue=new LinkedList<Carrier>();    //当前站内载具
    public Queue<Passenger> passengerInStation=new LinkedList<Passenger>();    //站内乘客：仅在首站和终点站有实际意义
    int DistanceToFormer;       //到前一站距离
    int DistanceToLatter;       //到后一站距离
    int passengerDownload=0;    //到站人数
    public Station next;               //下一站
    public Station before;             //上一站
    public boolean firstStation=false;
    public int passengerNumberinStation=0;
    //构造函数
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
        this.Fullname=fn;
    	this.Name=n;
    	this.DistanceToFormer=dtf;
    	totalStation++;
    	Integer tempNO=no;
    	Station.stationMap.put(tempNO,this);
    }
    //Method
    public String returnNextStation() {
    	return this.next.Name;
    }
    public String returnFullName() {
    	return this.Fullname;
    }
    public String returnBeforeStation() {
    	return this.before.Name;
    }
    public int distancetoFormerStation()
    {
    	return this.DistanceToFormer;
    }
    public int distancetoLatterStation()
    {
        return this.DistanceToLatter;
    }
    public void generatePassenger(){
        Passenger newPassenger=new Passenger();
        this.passengerInStation.add(newPassenger);
        this.passengerNumberinStation++;
    }
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

    public String returnName() {return this.Name;}
    int returnNo() {
    	return this.no;
    }
    public static Station seekStationByUID(Integer no){
        return Station.stationMap.get(no);
    }
}

