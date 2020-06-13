package Object;

import java.util.*;

public class Station {
    static Map<Integer,Station> stationMap=new HashMap<Integer, Station>();
    static int totalStation=1;
    int no;                     //编码
	String Fullname;            //全称：用于输出
    String Name;                //简写
    Queue<Carrier> carrierQueue;    //当前站内载具
    Queue<Passenger> passengerInStation;    //站内乘客：仅在首站和终点站有实际意义
    int DistanceToFormer;       //到前一站距离
    int DistanceToLatter;       //到后一站距离
    int passengerDownload=0;    //到站人数
    Station next;               //下一站
    Station before;             //上一站
    //构造函数
    public Station(String fn,String n,int dtf){
        this.no=totalStation-1;
        if(totalStation!=1)
        {
            this.before=Station.stationMap.get(no-1);
            before.next=this;
            before.DistanceToLatter=dtf;
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

    public String returnName() {return this.Name;}
    int returnNo() {
    	return this.no;
    }
}
