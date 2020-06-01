package Object;
public class Station {
	int no;
	String Fullname;
    String Name;
    Queue<Carrier> carrierQueue;
    Queue<Passenger> passengerInStation;
    int DistanceToFormer;
    int DistanceToLatter;
    int passengerDownload=0;
    Station next;
    Station before;
    //Constructor
    Station(String fn,String n,int dtf,int dtl,Station latter,Station former,int no){
    	this.Fullname=fn;
    	this.Name=n;
    	this.DistanceToFormer=dtf;
    	this.DistanceToLatter=dtl;
    	this.before=former;
    	this.next=latter;
    	this.no=no;
    }
    //Method
    public String returnNextStation() {
    	return this.next.Name;
    }
    public String returnFullName() {
    	return this.Fullname;
    }
    public String returnBeforeStation() {
    	return this.before.Name;
    }
    public int waytoFormerStation()
    {
    	return this.DistanceToFormer;
    }
    public void carrierArrive(Carrier obj1) {
    	;
    }
    public void carrierStart(Carrier obj1) {
    	
    }
    public String returnName() {return this.Name;}
    int returnNo() {
    	return this.no;
    }
}
