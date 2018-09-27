
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class RollerCoasterCanvas extends Canvas {
	
	
	BufferedImage bufferImage;
	ImageIcon background = new ImageIcon("src/images/background.png");

	public boolean gameRunning;
	
	private void init(){
		bufferImage = new BufferedImage(this.getWidth(),this.getHeight(), BufferedImage.TYPE_INT_RGB);
	}
	
	public void drawWagon() {
		if (Main.roller == null) {
			return;
		}
		
		Wagon wagon = Main.roller.wagon;
		for(int i = 0 ; i < wagon.numbOfWagons(); i++){
			int xIncrement = (wagon.width)*i;
			bufferImage.getGraphics().drawImage(wagon.currentImage.getImage(), wagon.position.x - xIncrement, wagon.position.y, wagon.width, wagon.height, this);
		}
	}
	
	public void drawPassengers(){
		if (Main.roller == null) {
			return;
		}
		
		//pass for all passenger and draw them
		for(int i = 0 ; i < Main.roller.passengers.size(); i++) {
			Passenger currentPassenger = Main.roller.passengers.get(i);
			bufferImage.getGraphics().drawImage(currentPassenger.currentImage.getImage(), currentPassenger.myPosition.x, currentPassenger.myPosition.y, currentPassenger.width, currentPassenger.height, this);
		}
	}

	
	public void runGame(){
		init();
		
		while(true){
			bufferImage.getGraphics().drawImage(background.getImage(), 0, 0, this);
			if(Main.has_started){
				drawWagon();
				drawPassengers();
			}
			this.getGraphics().drawImage(bufferImage, 0, 0, this);
		}
	}
}
