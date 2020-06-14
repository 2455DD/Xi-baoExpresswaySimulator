package Object;

public class Volve extends Car{
    public Volve(int target,Station whereami){
        speed=2;
        maximumPassenger=40;
        this.uid=carOnRoad;
        this.carrierType="Volve";
        this.target=target;
        this.nextStation=whereami.next;
        this.DistanceToFormerStation=0;
        Carrier.carOnRoad++;
        Integer tempUID = uid;
        Carrier.carrierMap.put(tempUID,this);
    }
}
