package Object;
/**
 * Volve类<br>
 * 模拟汽车沃尔沃
 */
public class Volve extends Car{
    /**
     * 默认构造函数，生成车辆及其驾驶方向并将该车辆加入{@code Carrier.carrierMap}中
     * @param target-驾驶方向，1为西安，2为宝鸡
     * @param whereami-生成车站
     */
    public Volve(int target,Station whereami){
        speed=2;
        maximumPassenger=40;
        this.uid=carOnRoad;
        this.carrierType="Volve";
        this.target=target;
        this.nextStation=whereami;
        this.DistanceToFormerStation=0;
        Carrier.carOnRoadplus();
        System.out.println(Carrier.carOnRoad);
        Integer tempUID = uid;
        Carrier.carrierMap.put(tempUID,this);
    }
}
