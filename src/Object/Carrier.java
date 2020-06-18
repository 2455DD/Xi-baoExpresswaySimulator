package Object;

import java.util.*;

public class Carrier {
    //属性
    public static Map<Integer,Carrier> carrierMap=new HashMap<Integer, Carrier>();
    public int uid;                             //识别码
    public static int carOnRoad=1;            //载具数
    public String carrierType;
    public double DistanceToFormerStation;     //距离前站距离
    int maximumPassenger;               //最大运载量
    public Station nextStation;                //下一站
    public Set<Passenger> passengerCollection=new HashSet<Passenger>();    //运载乘客
    public double speed;                       //速度
    public boolean queueIsFull=false;          //状态：是否满载——拒绝加入新乘客与否
    public int target;                         //目标方向：1：西安；2：宝鸡
    public int timerFromLeave=0;               //从上一次离站，单位为分钟
    public int presentPassenger=0;             //现有乘客
    //方法
    public String returnLocation() {        //返回当前位置
        return this.carrierType+this.uid+"离上一站 "+this.nextStation.returnFullName()+" 已有"+(this.DistanceToFormerStation)+"米。下一站是 "+this.nextStation.returnFullName();
    }
    public boolean isEmpty() {   //更新queueIsFull并返回其最新值
        if(presentPassenger==maximumPassenger)queueIsFull=true;
        return !queueIsFull;
    }
    public void leaveStation() {    //离站
        if(this.target==1&&this.nextStation.no!=6){
            this.DistanceToFormerStation=this.nextStation.DistanceToLatter;
            this.nextStation=this.nextStation.next;
        }
        else{
            if (this.nextStation.no==6){
                this.nextStation=this.nextStation.before;
                this.DistanceToFormerStation=this.nextStation.DistanceToFormer;
            }

            if(this.target==2&&this.nextStation.no!=0){
                this.DistanceToFormerStation=this.nextStation.DistanceToFormer;
                this.nextStation=this.nextStation.before;
            }
            else {
                this.DistanceToFormerStation=this.nextStation.DistanceToLatter;
                this.nextStation=this.nextStation.next;
            }

        }

        this.timerFromLeave=0;
    }
    public int arriveStation(){       //到站行为
        int ans=0;
        Iterator<Passenger> iterator= this.passengerCollection.iterator();

        while(iterator.hasNext()){
            Passenger e = iterator.next();
            if(e.target.equals(this.nextStation)) {
                ans++;
                iterator.remove();//注意，details at https://www.cnblogs.com/dolphin0520/p/3933551.html
                this.nextStation.passengerDownload++;
            }
        }
        return ans;
    }
    public boolean passengerEmbark(Passenger a) {   //上载具
        if(queueIsFull)return false;
        else {
            passengerCollection.add(a);
            presentPassenger++;
        }
        return true;
    }
    public void carrierMovePerMinutes(){
            this.DistanceToFormerStation+=speed;
    }

}
class Car extends Carrier{
}

