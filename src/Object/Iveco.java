package Object;

public class Iveco extends Car{
    public Iveco(int givennum,int target,Station whereami){
        speed=1.4;
        maximumPassenger=21;
        this.uid=givennum;
        this.carriertype="Iveco";
        this.target=target;
        this.nextStation=whereami.next;
        this.DistanceToFormerStation=0;
        Integer tempUID = uid;
        Carrier.carrierMap.put(tempUID,this);
    }


}
