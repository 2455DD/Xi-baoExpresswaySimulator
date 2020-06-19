package Object;

public class Iveco extends Car{
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
