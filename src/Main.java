public class Main {
	public static RollerCoaster roller;
	public static UIController control;
	public static boolean has_started = false;
	
	public static void main(String[] args) {
		control = new UIController();	
		control.addUIElements();
	}
	
	
	public static void startSimulationWithValues(int numbOfChairs,int travelTime,long timeToSit,long timeToGetOut){
		
		if(!has_started){
			roller = new RollerCoaster(numbOfChairs, travelTime, timeToSit, timeToGetOut);
			roller.startRunning();
			has_started = true;
		}
	}
	
}
