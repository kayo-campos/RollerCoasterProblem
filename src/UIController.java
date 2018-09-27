
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.text.AbstractDocument;


public class UIController implements ActionListener {
	
	 private JFrame mainFrame;
	 private JLabel numbOfChairsLabel;
	 private JTextField numbOfChairsTF;
	 private JLabel travelTimeLabel;
	 private JTextField travelTimeTF;
	 private JLabel timeToSitLabel;
	 public static JTextField timeToSitTF;
	 private JLabel timeToGetOutLabel;
	 public static JTextField timeToGetOutTF;
	 private JButton startStopButton;
	 private JLabel removePassengerLabel;
	 private JTextField removePassengeTextField;
	 private JButton removePassengerButton;
	 private JButton addNewPassenger;

	 
	 private JPanel topPanel;
	 private RollerCoasterCanvas rCCanvas;

	
	 
	 
	public int getNumbOfChairs() {
		try{
			return Integer.parseInt(numbOfChairsTF.getText(), 10);
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}	
		return 2;
	}

	public int getTravelTime() {
		try{
			return Integer.parseInt(travelTimeTF.getText(), 10);
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}	
		return 10;
		
	}

	public int getTimeToSit() {
		try{
			return Integer.parseInt(timeToSitTF.getText(), 10);
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}		
		return 1;
	}

	public int getTimeToGetOut() {
		try{
			return Integer.parseInt(timeToGetOutTF.getText(), 10);
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}		
		return 1;	
		
	}


	public void addUIElements(){
		mainFrame = new JFrame("Problema da Montanha Russa");
		mainFrame.setResizable(false);
		mainFrame.setSize(875,550);
		mainFrame.setLayout(null);
	    mainFrame.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
	    	}        
	    }); 
	    
	    addTopPanel();
	    addCanvas();
	    addStartStopButton();
	    addRemovePassengerViews();
	    addAddNewPassengerViews();
	    
	    mainFrame.setVisible(true);
		rCCanvas.runGame();
	}
	
	private void addTopPanel(){
		topPanel = new JPanel();
		topPanel.setBackground(new Color(1f,1f,1f));
		topPanel.setLayout(new GridLayout(2,6)/*null*/);
		topPanel.setSize(600, 50);
	    mainFrame.add(topPanel);
	    
	    addNumbOfChairsLabel();
	    addTravelTimeLabel();
	    addTimeToSitLabel();
	    addTimeToGetOutLabel();

	    addNumbOfChairsTextField();
	    addTravelTimeTextField();
	    addTimeToSitTF();
	    addTimeToGetOutTF();
	}
	
	private void addRemovePassengerViews() {
		removePassengerLabel = new JLabel("ID do passageiro", SwingConstants.CENTER);
		removePassengerLabel.setBounds(700, 50, 175, 50);
		
		removePassengeTextField = new JTextField();
		removePassengeTextField.setBounds(700, 85, 175, 25);
		removePassengeTextField.setHorizontalAlignment(SwingConstants.CENTER);
		((AbstractDocument)removePassengeTextField.getDocument()).setDocumentFilter(new OnlyNumericDocumentFilter(99));
		
		removePassengerButton = new JButton("Remover");
		removePassengerButton.setBounds(700, 110, 175, 25);
		removePassengerButton.addActionListener(this);
		
		mainFrame.add(removePassengerLabel);
		mainFrame.add(removePassengeTextField);
		mainFrame.add(removePassengerButton);
		
		removePassengerButton.setEnabled(false);

	}
	
	private void addAddNewPassengerViews() {
		addNewPassenger = new JButton("Adicionar passageiro");
		addNewPassenger.setBounds(700, 140, 175, 25);
		
		addNewPassenger.addActionListener(this);

		mainFrame.add(addNewPassenger);
		
		addNewPassenger.setEnabled(false);
	}
	
	private void addNumbOfChairsLabel(){
		
		numbOfChairsLabel = new JLabel("Cadeiras",SwingConstants.CENTER);
		
	    topPanel.add(numbOfChairsLabel);
	}
	
	private void addNumbOfChairsTextField(){
		numbOfChairsTF = new JTextField();
		numbOfChairsTF.setHorizontalAlignment(SwingConstants.CENTER);
		((AbstractDocument)numbOfChairsTF.getDocument()).setDocumentFilter(new OnlyNumericDocumentFilter(6));
	    topPanel.add(numbOfChairsTF);
	}
	
	private void addTravelTimeLabel(){
		
		travelTimeLabel = new JLabel("Tempo da viagem:",SwingConstants.CENTER);
	    topPanel.add(travelTimeLabel);
	}
	
	private void addTravelTimeTextField(){
		
		travelTimeTF = new JTextField();
		travelTimeTF.setHorizontalAlignment(SwingConstants.CENTER);
		((AbstractDocument)travelTimeTF.getDocument()).setDocumentFilter(new OnlyNumericDocumentFilter(99));
	    topPanel.add(travelTimeTF);
	}
	
	
	private void addTimeToSitLabel(){
		
		timeToSitLabel = new JLabel("Tempo de embarque:",SwingConstants.CENTER);
	    topPanel.add(timeToSitLabel);
	}
	
	private void addTimeToSitTF(){
		
		timeToSitTF = new JTextField();
		timeToSitTF.setHorizontalAlignment(SwingConstants.CENTER);
		((AbstractDocument)timeToSitTF.getDocument()).setDocumentFilter(new OnlyNumericDocumentFilter(99));		//numbOfChairsTF.setBounds((int) (numbOfChairsLabel.getBounds().getMaxX() + 5),numbOfChairsLabel.getBounds().y,40, 20);

	    topPanel.add(timeToSitTF);
	}
	
	
	private void addTimeToGetOutLabel(){
		
		timeToGetOutLabel = new JLabel("Tempo desembarque:",SwingConstants.CENTER);
	    topPanel.add(timeToGetOutLabel);
	}
	
	private void addTimeToGetOutTF(){
		
		timeToGetOutTF = new JTextField();
		timeToGetOutTF.setHorizontalAlignment(SwingConstants.CENTER);
		
		((AbstractDocument)timeToGetOutTF.getDocument()).setDocumentFilter(new OnlyNumericDocumentFilter(99));
		
	    topPanel.add(timeToGetOutTF);
	}
	
	
	private void addStartStopButton(){
		startStopButton = new JButton("Start");
		startStopButton.setBounds(topPanel.getWidth(),topPanel.getY(), mainFrame.getWidth() - topPanel.getWidth(), topPanel.getHeight());
		startStopButton.setBackground(Color.GREEN);
		startStopButton.addActionListener(this);

		mainFrame.add(startStopButton);
	}
	
	
	private void startStopButtonAction(){
		if(!Main.has_started){
			try{
				
				int numbOfChairs = Integer.parseInt(numbOfChairsTF.getText(), 10);
				int travelTime = Integer.parseInt(travelTimeTF.getText(), 10);
				Main.startSimulationWithValues(numbOfChairs, travelTime, 5, 5);
				
				
				addNewPassenger.setEnabled(true);
				removePassengerButton.setEnabled(true);


			}
			catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		else{
		}
		
	}
	
	// Canvas
	private void addCanvas(){
		rCCanvas = new RollerCoasterCanvas();
		rCCanvas.setBackground(Color.CYAN);
		rCCanvas.setBounds(0,topPanel.getHeight() + topPanel.getY(),700, 700 - topPanel.getHeight());

		mainFrame.add(rCCanvas);

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		switch(arg0.getActionCommand().toLowerCase()){
		case "start":
		case "stop":
			startStopButtonAction();
			break;
		case "remover":
			removePassengerButtonAction();
			break;
		case "adicionar passageiro":
			addNewUserAction();
			break;
		default:
			break;	
		}
	}
	

	private void removePassengerButtonAction(){
		try{
			Main.roller.removePassengerWithID(Integer.parseInt(removePassengeTextField.getText(),10));
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}
	}
	
	private void addNewUserAction() {
		Main.roller.addNewPassenger();
	}
}
