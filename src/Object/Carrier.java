package object;

public class Carrier {
    //属性
    int number;
    double DistanceToFormerStation;
    int maximumPassenger;
    Station nextStation;
    Passenger[] passengerCollection;
    double speed;
    boolean queueIsFull=false;
    int target;
    int timerFromLeave=0;
    int presentPassenger=0;
    //方法
    public String returnLocation(int time) {
        return this.number+"离上一站 "+this.nextStation.returnFullName()+" 已有"+(this.DistanceToFormerStation)+"米。下一站是 "+this.nextStation.returnFullName();
    }
    public boolean isFull() {
        if(presentPassenger==maximumPassenger)queueIsFull=true;
        return queueIsFull;
    }
    public void leaveStation(int new_distance) {
        this.DistanceToFormerStation=new_distance;
        this.timerFromLeave=0;
    }
    public void passengerOn(Passenger a) {
        passengerCollection[presentPassenger++]=a;
    }

}
class Car extends Carrier{
}
class Volve extends Car{
    Volve(int givennum,int target,Station whereami){
        speed=2;
        maximumPassenger=40;
        this.number=givennum;
        this.target=target;
        this.nextStation=whereami.next;
        this.DistanceToFormerStation=0;
        this.passengerCollection=new Passenger[this.maximumPassenger];
    }
}
class Iveco extends Car{
    Iveco(int givennum,int target,Station whereami){
        speed=1.4;
        maximumPassenger=21;
        this.number=givennum;
        this.target=target;
        this.nextStation=whereami.next;
        this.DistanceToFormerStation=0;
        this.passengerCollection=new Passenger[this.maximumPassenger];
    }
}