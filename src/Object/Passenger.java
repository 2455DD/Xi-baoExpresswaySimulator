package Object;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
/**
 * Passenger类<br>
 *     对乘客的行为模拟
 */
public class Passenger {
    /**目标地点*/
    Station target;
    /**全局乘客统计数量*/
    static Integer globalPassengersStatistic= 1;
    /**单个乘客的识别码*/
    private final Integer uid;
    /**全局乘客映射，Integer是uid*/
    static Map<Integer,Passenger> passengerMap=new HashMap<Integer, Passenger>();
    /**默认构造函数，随机生成目标地点*/
    Passenger(){
        this.uid=globalPassengersStatistic++;
        this.target=Station.stationMap.get(this.targetGenerate(7));
        Passenger.passengerMap.put(uid,this);
    }

    /**高斯分布形成目标地点
     *
     * @param hiLimit-极限值，即最大站所对应的UID
     * @return 随机目标站的UID
     */
    private int targetGenerate(int hiLimit){
        Random random=new Random();
        return random.nextInt(hiLimit);
    }
}
