package Object;

/**
 *  Iveco类<br>
 *  模拟汽车Iveco
 */
public class Iveco extends Car{
    /**
     * 默认构造函数，生成车辆及其驾驶方向并将该车辆加入{@code Carrier.carrierMap}中
     * @param target-驾驶方向，1为西安，2为宝鸡
     * @param whereami-生成车站
     */
    public Iveco(int target,Station whereami){
        speed=1.4;
        maximumPassenger=21;
        this.uid=carOnRoad;
        this.carrierType="Iveco";
        this.target=target;
        this.nextStation=whereami;
        this.DistanceToFormerStation=0;
        Integer tempUID = uid;
        Carrier.carOnRoadplus();
        System.out.println(Carrier.carOnRoad);
        Carrier.carrierMap.put(tempUID,this);
    }


}
