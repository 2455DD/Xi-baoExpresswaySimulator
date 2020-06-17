package Object;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Passenger {
    Station target;
    static Integer globalPassengersStatistic= 1;
    Integer uid;
    static Map<Integer,Passenger> passengerMap=new HashMap<Integer, Passenger>();
    Passenger(){
        this.uid=globalPassengersStatistic++;
        this.target=Station.stationMap.get(this.targetGenerate(7));
        Passenger.passengerMap.put(uid,this);
    }
    private int targetGenerate(int hiLimit){
        Random random=new Random();
        return random.nextInt(hiLimit);
    }
    public Integer returnUID(){
        return this.uid;
    }
}
