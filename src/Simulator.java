import java.lang.*;
import Object.*;
public class Simulator
{
    Timer global=new Timer();
    //Under Construction

    /*          To-Do List
        1、实现时间的迭代变化——暂定使用线程睡眠+死循环（满足x条件跳出）
        2、随时间生成Carrier
        3、实现数据统计
        4、Java-IO到文件以及可能的GUI设计
                                                    ——何浩文
     */
}
class Timer extends Thread{
    int currentTime;                      //时间放大比 1:630,单位为秒，判断依据为21/2
    int secs;
    int mins;
    int hours;

    Timer(){
        currentTime=0;
        secs=0;
        mins=0;
        hours=0;
    }

    public void run(){
        try {
            while(true) {
                secs=currentTime%60;
                mins=currentTime/60;
                hours=mins/60;
                if(currentTime!=0)mins%=mins;
                if(hours*60*60+mins*60+secs>=42497)break;              //(18-8.5)*60*60+((24+21+62+21+24+22)/1.4+7*2)
                else {
                    Timer.sleep(1000);
                    currentTime+=630;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    int[] returnCurrentTime(){
        int secs=currentTime%60;
        int mins=currentTime/60;
        int hours=mins/60;
        mins%=mins;
        return new int[]{hours, mins, secs};
    }
}


