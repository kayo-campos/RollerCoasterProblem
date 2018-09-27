import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Passenger implements Runnable {

	public String passengerName;
	public int id;
	public long timeToSit;
	public long timeToGetOut;
	
	public Point myPosition;
	public ImageIcon currentImage;
	public int width;
	public int height;
	
	private boolean alive = true;
	private boolean traveling = false;
	
	private ImageIcon sleepingImage;
	private ArrayList<ImageIcon> walkRightImages;
	private ArrayList<ImageIcon> walkLeftImages;
	private ArrayList<ImageIcon> jumpingImages;
	private ImageIcon waitingImage;
	private ArrayList<ImageIcon> watchAroundImages;
	private ArrayList<ImageIcon> jumpingOutImages;
	private ArrayList<ImageIcon> dyingImages;
	
	
	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public boolean isTraveling() {
		return traveling;
	}

	public void setTraveling(boolean traveling) {
		this.traveling = traveling;
	}

	public Passenger(int passengerID,String passengerName,long timeToSit,long timeToGetOut){
		this.id = passengerID;
		this.passengerName = passengerName;
		this.timeToSit = timeToSit;
		this.timeToGetOut = timeToGetOut;
		
		this.width = 50;
		this.height = 46;
		
		sleepingImage = new ImageIcon("src/images/passenger/right.png");
		
		walkRightImages = new ArrayList<ImageIcon>();
		walkRightImages.add(new ImageIcon("src/images/passenger/right_walk_1.png"));
		walkRightImages.add(new ImageIcon("src/images/passenger/right_walk_2.png"));
		walkRightImages.add(new ImageIcon("src/images/passenger/right_walk_3.png"));
		walkRightImages.add(new ImageIcon("src/images/passenger/right_walk_4.png"));
		walkRightImages.add(new ImageIcon("src/images/passenger/right_walk_5.png"));
		walkRightImages.add(new ImageIcon("src/images/passenger/right_walk_6.png"));
		
		walkLeftImages = new ArrayList<ImageIcon>();
		walkLeftImages.add(new ImageIcon("src/images/passenger/left_walk_1.png"));
		walkLeftImages.add(new ImageIcon("src/images/passenger/left_walk_2.png"));
		walkLeftImages.add(new ImageIcon("src/images/passenger/left_walk_3.png"));
		walkLeftImages.add(new ImageIcon("src/images/passenger/left_walk_4.png"));
		walkLeftImages.add(new ImageIcon("src/images/passenger/left_walk_5.png"));
		walkLeftImages.add(new ImageIcon("src/images/passenger/left_walk_6.png"));
		
		jumpingImages = new ArrayList<ImageIcon>();
		jumpingImages.add(new ImageIcon("src/images/passenger/right_walk_2.png"));
		jumpingImages.add(new ImageIcon("src/images/passenger/right_walk_3.png"));
		jumpingImages.add(new ImageIcon("src/images/passenger/right_walk_4.png"));
		
		waitingImage = new ImageIcon("src/images/passenger/right.png");
		
		watchAroundImages = new ArrayList<ImageIcon>();
		watchAroundImages.add(new ImageIcon("src/images/passenger/right.png"));
		watchAroundImages.add(new ImageIcon("src/images/passenger/left.png"));
		
		jumpingOutImages = new ArrayList<ImageIcon>();
		jumpingOutImages.add(new ImageIcon("src/images/passenger/left_walk_2.png"));
		jumpingOutImages.add(new ImageIcon("src/images/passenger/left_walk_3.png"));
		jumpingOutImages.add(new ImageIcon("src/images/passenger/left_walk_4.png"));
		
		dyingImages = new ArrayList<ImageIcon>();
		dyingImages.add(new ImageIcon("src/images/passenger/right.png"));
		dyingImages.add(new ImageIcon("src/images/passenger/left.png"));

		currentImage = sleepingImage;
	}
	
	@Override
	public void run() {
		while(this.isAlive()){
			setSleepingImage();
			
			tryToGetInWagon();

			if (!this.isAlive()) {
				removeFromArrays();
				RollerCoaster.passengerController.release();
				RollerCoaster.wagonFreeChairs.release();
				return;
			}
			getIn();
			waitForOthers();
			watchAround();
			tryToGetOutWagon();
			getOut();	
		}
		removeFromArrays();
	}
	
	
	
	/**
	 * Work with the semaphores of passengers to discover 
	 * if he can get in and if its his time to get in
	 * get in only one by time
	 * */
	private void tryToGetInWagon(){	
		// Check if there is any available seat
		
		try {
			RollerCoaster.wagonFreeChairs.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Check if passenger can get in
		
		try {
			RollerCoaster.passengerController.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		normalizeImageSize();
	}
	
	
	/**
	 * This is a critic zone where the passengers threads can not 
	 * do it in parallel
	 * Here is executed all the code of UI to make the passenger get in
	 * and update the semaphores of passengers and the of the wagon
	 */
	private void getIn(){	
		setTraveling(true);
		
		walkGetInAnimation();
		
		jumpingInAnimation(285, timeToSit*0.25f,null);		
		Main.roller.queuePassengers.remove(this);
		Main.roller.passengersOnWagon.add(this);
		
		moveQueue();
		
		Main.roller.freeChairs --;
		if(Main.roller.freeChairs == 0){
			// TIME TO HAVE FUN!! WAKE UP ROLLER COASTER
			RollerCoaster.wagonTravel.release();
		}
		
		RollerCoaster.passengerController.release();
	}
	
	
	private void walkGetInAnimation(){
		walkToAnimation(calculeDistance(), timeToSit*0.75f, new Runnable(){

			@Override
			public void run() {
				currentImage = walkRightImages.get(0);
			}
			
		});
	}
	
	
	private int calculeDistance() {
		Wagon wagon = Main.roller.wagon;
		int distance = wagon.freeChairPosition(Main.roller.freeChairs) + 5;
		return distance;
	}
	
	
	private void moveQueue() {
		//move queue foward
		for(int i = 0 ; i < Main.roller.queuePassengers.size();i++){
			Passenger passenger = Main.roller.queuePassengers.get(i);
					
			passenger.moveOnQueue();
		}
	}
	
	
	public int calcMyPositionOnQueue(){
		int rightPositionForMe = Main.roller.positonOfPassengerOnQueue(this);
		
		return  Main.roller.startQueuePosition.x - (rightPositionForMe*(31));
	}
	
	
	public void moveOnQueue(){
		walkToAnimation(calcMyPositionOnQueue(),0.3f,null);
	}
	
	
	private void waitForOthers() {
		System.out.println(passengerName + " dormindo na montanha russa");
		try {
			RollerCoaster.passengersHaveFun.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void watchAround(){
		animateWatchAround();			
	}
	
	private void animateWatchAround() {		
		long initialTime = System.currentTimeMillis();
		float timeDiference = 0;
				
		long lastUpdateTime = System.currentTimeMillis();
		float updateInterval = 0;
		int watchAroundImagePosition = 0;
		
		while(timeDiference <= Main.roller.wagon.travelTime || Main.roller.wagon.isTraveling()){
			timeDiference = (float) ((System.currentTimeMillis() - initialTime)/1000.0);			
			updateInterval = (System.currentTimeMillis() - lastUpdateTime)/1000f;

			if(updateInterval >= 0.2){
				setWatchingImage(watchAroundImagePosition);
				watchAroundImagePosition++;
				watchAroundImagePosition = watchAroundImagePosition >= watchAroundImages.size() ? 0 : watchAroundImagePosition;
				lastUpdateTime = System.currentTimeMillis();
			}
		}
	}
	
	
	private void tryToGetOutWagon(){
		
		System.out.println(passengerName+" pedindo permissão para sair da montanha russa");
		try {
			RollerCoaster.passengerController.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private void getOut(){
		normalizeImageSize();
		System.out.println(passengerName + " saindo da montanha russa");
		
		Main.roller.queuePassengers.add(this);//add on queue
		Main.roller.passengersOnWagon.remove(this);//remove from passengers
		
		jumpingOutAnimation(350, timeToGetOut*0.25f,null);		
		walkToQueueAnimation();
		
		setTraveling(false);
		
		if(Main.roller.passengersOnWagon.size() == 0){
			Main.roller.lastPassengerGotOutWagon();
			RollerCoaster.wagonFreeChairs.release(Main.roller.freeChairs);
		}
		
		RollerCoaster.passengerController.release();
	}
	
	
	public void walkToQueueAnimation(){
		walkToAnimation(calcMyPositionOnQueue(), timeToGetOut*0.75f,null);
	}
	
	
	private void walkToAnimation(int finalXPosition,float timeInterval,Runnable finishingBlock){
		long initialTime = System.currentTimeMillis();
		int initialPosition = myPosition.x ;
		double velocity = (finalXPosition - initialPosition)/timeInterval;
		float timePassed = 0f;
		
		long lastUpdateTime = System.currentTimeMillis();
		float updateInterval = 0;//update frequence
		int walkToQueueImagePosition = 0;
	
		ArrayList<ImageIcon> currentWalkImages = finalXPosition - initialPosition >= 0 ? walkRightImages : walkLeftImages;
		
		while(myPosition.x != finalXPosition && timePassed <= timeInterval){
			timePassed = (System.currentTimeMillis() - initialTime)/1000.0f;
			this.myPosition.x = initialPosition + (int) ((velocity)*timePassed);

			
			updateInterval = (System.currentTimeMillis() - lastUpdateTime)/1000f;
			if(updateInterval >= 0.2){
				this.currentImage = currentWalkImages.get(walkToQueueImagePosition);
				walkToQueueImagePosition++;
				walkToQueueImagePosition = walkToQueueImagePosition >= currentWalkImages.size() ? 0 : walkToQueueImagePosition;
				lastUpdateTime = System.currentTimeMillis();
			}
		}
		
		myPosition.x = finalXPosition;
		this.currentImage = sleepingImage;
		if(finishingBlock != null){
			finishingBlock.run();
		}
	}

	
	private void jumpingOutAnimation(int finalYPosition,float timeInterval,Runnable finishingBlock) {
		long initialTime = System.currentTimeMillis();
		int initialPosition = myPosition.y;
		double velocity = (finalYPosition - initialPosition)/timeInterval;
		float timePassed = 0f;

		long lastUpdateTime = System.currentTimeMillis();
		float updateInterval = 0;//update frequence
		int jumpImagePosition = 0;
		
		while(finalYPosition != this.myPosition.y && timePassed <= timeInterval){
			timePassed = (float) ((System.currentTimeMillis() - initialTime)/1000.0);			
			this.myPosition.y = initialPosition + (int) ((velocity)*timePassed);
			
			updateInterval = (System.currentTimeMillis() - lastUpdateTime)/1000f;
			if(updateInterval >= 0.2){
				this.currentImage = jumpingOutImages.get(jumpImagePosition);
				if (jumpingOutImages.size()-1 != jumpImagePosition) jumpImagePosition++;
				lastUpdateTime = System.currentTimeMillis();
			}
		}
		myPosition.y = finalYPosition;
		
		this.currentImage = waitingImage;
		if(finishingBlock != null){
			finishingBlock.run();
		}	
		
	}
	
	private void jumpingInAnimation(int finalYPosition,float timeInterval,Runnable finishingBlock) {
		long initialTime = System.currentTimeMillis();
		int initialPosition = myPosition.y;
		double velocity = (finalYPosition - initialPosition)/timeInterval;
		float timePassed = 0f;

		long lastUpdateTime = System.currentTimeMillis();
		float updateInterval = 0;
		int jumpImagePosition = 0;
		
		while(finalYPosition != this.myPosition.y && timePassed <= timeInterval){
			timePassed = (float) ((System.currentTimeMillis() - initialTime)/1000.0);			
			this.myPosition.y = initialPosition + (int) ((velocity)*timePassed);

		
			
			updateInterval = (System.currentTimeMillis() - lastUpdateTime)/1000f;
			if(updateInterval >= 0.2){
				this.currentImage = jumpingImages.get(jumpImagePosition);
				if (jumpingOutImages.size()-1 != jumpImagePosition) jumpImagePosition++;
				lastUpdateTime = System.currentTimeMillis();
			}
		}
		myPosition.y = finalYPosition;

		currentImage = waitingImage;
		if(finishingBlock != null){
			finishingBlock.run();
		}	
		
	}
	
	private void removeFromArrays() {
		if (Main.roller.passengers.contains(this)) {
			this.animateDying();
			Main.roller.queuePassengers.remove(this);
			Main.roller.passengers.remove(this);
		}
	}
	
	public void animateDying(){
		long initialTime = System.currentTimeMillis();
		float timeDiference = 0;
		
		long lastUpdateTime = System.currentTimeMillis();
		float updateInterval = 0;
		
		int dyingImagePosition = 0;
		
		while(timeDiference <= 1){
			timeDiference = (float) ((System.currentTimeMillis() - initialTime)/1000.0);		
									
			updateInterval = (System.currentTimeMillis() - lastUpdateTime)/1000f;

			if(updateInterval >= 0.2){
				this.currentImage = dyingImages.get(dyingImagePosition);
				dyingImagePosition++;
				dyingImagePosition = dyingImagePosition >= dyingImages.size() ? 0 : dyingImagePosition;
				lastUpdateTime = System.currentTimeMillis();
			}

		}
	}
	
	private void setSleepingImage() {
		this.currentImage = sleepingImage;
	}
	
	private void setWatchingImage(int position) {
		this.width = 50 + (position*7);
		this.height = 46;
		this.currentImage = watchAroundImages.get(position);
	}
	
	private void normalizeImageSize() {
		this.width = 50;
		this.height = 46;
	}

}
