package Object;

import java.util.*;

public class Carrier {
    //属性
    static Map<Integer,Carrier> carrierMap=new HashMap<Integer, Carrier>();
    int uid;                             //识别码
    static int carOnRoad=1;            //载具数
    String carriertype;
    double DistanceToFormerStation;     //距离前站距离
    int maximumPassenger;               //最大运载量
    Station nextStation;                //下一站
    Set<Passenger> passengerCollection=new HashSet<Passenger>();    //运载乘客
    double speed;                       //速度
    boolean queueIsFull=false;          //状态：是否满载——拒绝加入新乘客与否
    int target;                         //目标方向：1：西安；2：宝鸡
    int timerFromLeave=0;               //从上一次离站
    int presentPassenger=0;             //现有乘客
    //方法
    String returnLocation(int time) {        //返回当前位置
        return this.carriertype+this.uid+"离上一站 "+this.nextStation.returnFullName()+" 已有"+(this.DistanceToFormerStation)+"米。下一站是 "+this.nextStation.returnFullName();
    }
    boolean isFull() {   //更新queueIsFull并返回其最新值
        if(presentPassenger==maximumPassenger)queueIsFull=true;
        return queueIsFull;
    }
    void leaveStation(int new_distance) {    //离站
        this.DistanceToFormerStation=new_distance;
        this.nextStation=this.nextStation.next;
        this.timerFromLeave=0;
    }
    boolean arriveStationCheck(){       //到站检测
        boolean tik=false;
        for(Passenger e:this.passengerCollection)
        {
            if(e.target.equals(this.nextStation)) {
                tik=true;
                passengerCollection.remove(e);
                this.nextStation.passengerDownload++;
            }
        }
        return tik;
    }
    boolean passengerEmbark(Passenger a) {   //上载具
        if(!queueIsFull)return false;
        else {
            passengerCollection.add(a);
            presentPassenger++;
        }
        return true;
    }

}
class Car extends Carrier{
}

class Passenger {
    Station target;
    public Passenger(){
        this.target=Station.stationMap.get(this.targetGenerate(7));
    }
    private int targetGenerate(int hiLimit){
        Random random=new Random();
        return random.nextInt(hiLimit);
    }
}
