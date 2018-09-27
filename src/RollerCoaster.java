import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class RollerCoaster {
	
		//control when the wagon should start to travel
		public static Semaphore wagonTravel = new Semaphore(0);
		//controls how many passengers are on wagon
		public static Semaphore wagonFreeChairs;
		// make the passengers get in or get out one by time
		public static Semaphore passengerController = new Semaphore(1, true);
		//control if the passengers can start to appreciate the travel or not
		public static Semaphore passengersHaveFun = new Semaphore(0);

		public Point startQueuePosition = new Point(350,350);

		
		public ArrayList<Passenger> queuePassengers = new ArrayList<Passenger>();
		public ArrayList<Passenger> passengersOnWagon = new ArrayList<Passenger>();
		public ArrayList<Passenger> passengers = new ArrayList<Passenger>();

		
		public Wagon wagon;
		
		public int freeChairs;
		public int travelTime;
		
		public static void delay(long seconds){
			long initialTime = System.currentTimeMillis();
			
			long timeDiference = 0;
			while(timeDiference <= seconds){
				timeDiference = (System.currentTimeMillis() - initialTime)/1000;
			}
		}
		
		
		public RollerCoaster(int numbOfChairs,int travelTime,long passengerTimeToSit,long passengerTimeToGetOut){
			wagon = new Wagon(numbOfChairs,travelTime);
			wagonFreeChairs = new Semaphore(numbOfChairs, true);
						
			freeChairs = wagon.numbOfChairs;
			this.travelTime = travelTime;
		}

		
		public int positonOfPassengerOnQueue(Passenger passenger){
			return queuePassengers.contains(passenger) ? queuePassengers.indexOf(passenger) : queuePassengers.size();
		}
		
		
		public void createPassengers(int quant,long passengerTimeToSit,long passengerTimeToGetOut){
						
			for(int i=0; i<quant; i++){
				Passenger newPassenger = new Passenger(i+1,"thread-"+(i+1),passengerTimeToSit,passengerTimeToGetOut);
				
				int newPosition = startQueuePosition.x - (i*(newPassenger.width + 3));
		
				newPassenger.myPosition = new Point(newPosition, startQueuePosition.y);
				passengers.add(newPassenger);
				queuePassengers.add(newPassenger);
			}
		}
		
		public void removePassenger(){
			
		}
		
		
		/**
		 *Update the values of some attributes of passengers and wagon
		 */
		public void lastPassengerGotOutWagon(){
			
			
			int numbOfChairs = Main.control.getNumbOfChairs();
			int travelTime = Main.control.getTravelTime();
			long timeToSit = Main.control.getTimeToSit();
			long timeToGetOut = Main.control.getTimeToGetOut();
			applyChanges(numbOfChairs,travelTime,timeToSit,timeToGetOut);
			
			//put all chairs to free
			Main.roller.freeChairs =  Main.roller.wagon.numbOfChairs;

		}
		
		public void applyChanges(int numbOfChairs,int travelTime,long timeToSit,long timeToGetOut){
			this.travelTime = travelTime;
			applyToWagon(numbOfChairs, travelTime);
			applyToPassengers(numbOfChairs, timeToSit, timeToGetOut);
		}
		
		private void applyToWagon(int numbOfChairs,int travelTime){
		
			if(wagon == null){
				freeChairs = numbOfChairs;
				wagonFreeChairs = new Semaphore(numbOfChairs, true);
				wagon = new Wagon(numbOfChairs,travelTime);
			}
			else{
				wagon.updateWagonValues(numbOfChairs, travelTime);
			}
		}
		
		private void applyToPassengers(int quant,long timeToSit,long timeToGetOut){
			if(passengers.size() == 0){
				createPassengers(quant, timeToSit, timeToGetOut);
			}
			else{
				System.out.println("agora dá bom");
			}
		}
		
		public void startRunning() {
			Thread vagao = new Thread(wagon,"WagonThread");
			vagao.start();
			startPassengers();
		}
		
		public void removePassengerWithID(int id){
			for(int i = 0; i < this.passengers.size(); i++){
				Passenger passenger = this.passengers.get(i);
				if(passenger.id == id){
					passenger.setAlive(false);
					queuePassengers.remove(passenger);
					
					if (!passenger.isTraveling()) {
						passenger.animateDying();
						passengers.remove(passenger);
					}
				}
			}
		}
		
		public void startPassengers(){
			for(int i = 0 ; i < passengers.size() ; i++){
				Passenger someOne = passengers.get(i);
				Thread passenger = new Thread(someOne,someOne.passengerName); 
				passenger.start();
			}
		}
		
		public void addNewPassenger(){
			int id = passengers.size()+1;	
			
			Passenger newPassenger = new Passenger(id, "Passageiro " + id, Integer.parseInt(UIController.timeToSitTF.getText()), Integer.parseInt(UIController.timeToGetOutTF.getText()));
			
			int newPosition = -31;
			newPassenger.myPosition = new Point(newPosition, 350);
			passengers.add(newPassenger);
			
			newPassenger.walkToQueueAnimation();
			
			queuePassengers.add(newPassenger);
			
			Thread newThread = new Thread(newPassenger, newPassenger.passengerName); 
			newThread.start();
		}
}
