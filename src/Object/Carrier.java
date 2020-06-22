package Object;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Carrier类<br>
 * 该类是对抽象载具的属性及行为的描述<br>
 * @author 何浩文
 */
public class Carrier {
    //属性
    /**全局载具映射,uid到载具对象*/
    public static Map<Integer,Carrier> carrierMap=new HashMap<Integer, Carrier>();
    /**车辆识别码*/
    public int uid;
    /**全局载具数量，用于生成uid*/
    protected static int carOnRoad=1;
    /**载具种类*/
    public String carrierType;
    /**距离前站距离*/
    public double DistanceToFormerStation;
    /**最大运载量*/
    protected int maximumPassenger;
    /**若是在路上(即未到站)则是下一站，若是到站则是本站*/
    public Station nextStation;
    /**载具内乘客集合[线程安全]*/
    public Set<Passenger> passengerCollection=Collections.newSetFromMap(new ConcurrentHashMap<Passenger, Boolean>());
    /**载具速度*/
    public double speed;
    /**状态：是否满载——拒绝加入新乘客与否*/
    private boolean queueIsFull=false;
    /**目标方向：1：西安；2：宝鸡*/
    public int target;
    /**从上一次离站到现在，单位为分钟*/
    public int timerFromLeave=0;
    /**现有乘客数*/
    public int presentPassenger=0;
    /**<code>carOnRoad的值加一</code>*/
    public static void carOnRoadplus() {
        Carrier.carOnRoad++;
    }

    /**更新<code>queueIsFull</code>的值并判断载具是否未满载，
     *
     * @return 是的话返回True
     */
    public boolean isEmpty() {   //更新queueIsFull并返回其最新值
        if(presentPassenger==maximumPassenger)queueIsFull=true;
        return !queueIsFull;
    }
    /**载具离站，更新下一站及到上一站的距离，并且重置离站时间*/
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

    /**载具到站，检测是否有乘客下车并返回下车人数
     *
     * @return 下车人数
     */
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

    /**
     * 乘客上车
     * @param a-Passenger
     * @return 满员false,成功true
     */
    public boolean passengerEmbark(Passenger a) {   //上载具
        if(queueIsFull)return false;
        else {
            passengerCollection.add(a);
            presentPassenger++;
        }
        return true;
    }
    /**
     *载具每分钟所发生的移动
     */
    public void carrierMovePerMinutes(){
            this.DistanceToFormerStation+=speed;
    }

}
class Car extends Carrier{
}

