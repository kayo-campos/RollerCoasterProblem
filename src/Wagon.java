import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Wagon implements Runnable {

	public int numbOfChairs;
	public long travelTime;
	private boolean isTraveling;
	
	// Current position
	public Point position;
	
	// The position where it should start traveling
	private Point startPosition;
	
	public int width;
	public int height;
	
	public ImageIcon currentImage;
	public ImageIcon stoppedImage;
	private ArrayList<ImageIcon> travelImages;
		
	private boolean alive = true;

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public boolean isTraveling() {
		return isTraveling;
	}

	public Wagon(int numbOfChairs,long travelTime){
		this.numbOfChairs = numbOfChairs;
		this.travelTime = travelTime;
		this.startPosition = new Point(500, 305);
		this.position = new Point(500, 305);
		this.width = 160;
		this.height = 86;
		
		stoppedImage = new ImageIcon("src/images/wagon/vagaoNormal.png");
		currentImage = stoppedImage;
		
		travelImages = new ArrayList<ImageIcon>();
		travelImages.add(stoppedImage);
		travelImages.add(new ImageIcon("src/images/wagon/vagaoNormal.png"));
		travelImages.add(new ImageIcon("src/images/wagon/vagaoElevado.png"));
	}
	
	public void updateWagonValues(int numbOfChairs,long travelTime){
		if(numbOfChairs != this.numbOfChairs){
			updateNumbOfChairsAnimation(numbOfChairs);
		}
		this.travelTime = travelTime;

	}
	
	public int numbOfWagons(){
		return numbOfChairs % 2 == 0 ? numbOfChairs / 2: numbOfChairs / 2 + 1;
	}
	
	public int freeChairPosition(int chairNumber){
		return this.position.x + ((this.width/2))*((chairNumber - this.numbOfChairs) + 1); //- this.width*(numbOfChairs - Main.roller.freeChairs);
	}
	
	@Override
	public void run() {
		int i = 0;
		while(this.isAlive()){
			tryToStartTravel();
			this.travel();
			this.stopTravel();
			i++;
			System.out.println("Viagem " + i + " chegou ao fim");
		}
	}
	
	/**
	 * This method will do something like decrement the semaphore
	 */
	private void tryToStartTravel(){
		try {
			RollerCoaster.wagonTravel.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private void travel(){
		System.out.println("Montanha russa viajando");
		isTraveling = true;
		RollerCoaster.passengersHaveFun.release(this.numbOfChairs);
		travelAnimation();
	}
	
	private void travelAnimation() {
		long initialTime = System.currentTimeMillis();
		float timeDiference = 0;
		
		int wagonFixPosition = startPosition.x;
		int initialPosition = position.x;
		
		float updateInterval = 0;
		int finalPosition = 1000 + this.width*(numbOfChairs/2) + this.width;

		long lastUpdateTime = System.currentTimeMillis();
		int travelImagePosition = 0;
		float timeSliceAppearAgain = 0;

		while(timeDiference <= travelTime){
			timeDiference = (float) ((System.currentTimeMillis() - initialTime)/1000.0);
			
			int incrementXValue = (int) ((finalPosition - initialPosition)*(timeDiference-timeSliceAppearAgain)/(travelTime-timeSliceAppearAgain));
			position.x = initialPosition + incrementXValue; 
					
			if(timeDiference > travelTime*0.5) {
				initialPosition = 0;
				finalPosition = wagonFixPosition;
				timeSliceAppearAgain =  (float) (travelTime*0.5);
			}
			
			movePassengers();
			updateInterval = (System.currentTimeMillis() - lastUpdateTime)/1000f;
			if(updateInterval >= 0.075){
				this.currentImage = travelImages.get(travelImagePosition);
				travelImagePosition++;
				travelImagePosition = travelImagePosition >= travelImages.size() ? 0 : travelImagePosition;
				lastUpdateTime = System.currentTimeMillis();
			}
		}
		
		
		
		this.position.x = startPosition.x;
		movePassengers();
		
		
		this.currentImage = stoppedImage;
	}	
	
	private void  updateNumbOfChairsAnimation(int newNumbOfChairs){
		
		moveToAnimation( 1000 + this.width*(numbOfChairs/2) + this.width,3f,null);
		
		this.numbOfChairs = newNumbOfChairs;
		this.position.x = 0;
		moveToAnimation(startPosition.x,3f,null);
		
		this.currentImage = stoppedImage;
	}
	
	private void moveToAnimation(int finalXPosition,float timeInterval,Runnable finishingBlock){
		long initialTime = System.currentTimeMillis();		
		int initialPosition = position.x;
		float timePassed = 0f;
		double velocity = (finalXPosition - initialPosition)/timeInterval;
		
		float updateInterval = 0;
		long lastUpdateTime = System.currentTimeMillis();
		int travelImagePosition = 0;
		
		while(position.x != finalXPosition && timePassed <= timeInterval){			
			timePassed = (float) ((System.currentTimeMillis() - initialTime)/1000.0);			
			int incrementXValue = (int) ((velocity)*((System.currentTimeMillis() - initialTime)/1000.0));
			position.x = initialPosition + incrementXValue; 
					
			//images animations
			updateInterval = (System.currentTimeMillis() - lastUpdateTime)/1000f;
			if(updateInterval >= 0.075){
				this.currentImage = travelImages.get(travelImagePosition);
				travelImagePosition++;
				travelImagePosition = travelImagePosition >= travelImages.size() ? 0 : travelImagePosition;
				lastUpdateTime = System.currentTimeMillis();
			}
		}
		currentImage = stoppedImage;
		if(finishingBlock != null){
			finishingBlock.run();
		}
	}

	
	private void stopTravel(){
		isTraveling = false;
		System.out.println("Montanha russa temrinou de viajar");
	}
	
	private void movePassengers() {
		ArrayList<Passenger> passengersOnWagon = Main.roller.passengersOnWagon;
		for (int i=0; i<passengersOnWagon.size(); i++) {
			Passenger passenger = passengersOnWagon.get(i);
			
			passenger.myPosition.x = freeChairPosition(numbOfChairs - i) + 5;
		}
	}
	
	
	

}
