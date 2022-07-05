package application;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.control.ProgressIndicator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup; 
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.*;
import javafx.scene.layout.AnchorPane;



import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


import application.FaceDetector;
import application.Database;
import application.OCR;
import application.Database;

public class SampleController  {

	//**********************************************************************************************
	//Mention The file location path where the face will be saved & retrieved
	
	public String filePath="./faces";
	
	
	//**********************************************************************************************
	@FXML
	private Button startCam;
	@FXML
	private Button stopBtn;
	@FXML
	private Button motionBtn;
	@FXML
	private Button eyeBtn;
	@FXML
	private Button shapeBtn;
	@FXML
	private Button upperBodyBtn;
	@FXML
	private Button fullBodyBtn;
	@FXML
	private Button smileBtn;
	@FXML
	private Button gesture;
	@FXML
	private Button gestureStop;
	@FXML
	private Button saveBtn;
	@FXML
	private Button ocrBtn;
	@FXML
	private Button capBtn;
	@FXML
	private Button recogniseBtn;
	@FXML
	private Button stopRecBtn;
	@FXML
	private Button beginList;
	@FXML
	private Button endList;
	@FXML
	private Button dialogfaceOui;
	@FXML
	private Button dialogfaceNon;
	@FXML
	private Button filechoose;
	@FXML
	private ImageView frame;
	@FXML
	private ImageView motionView;
	@FXML
	private AnchorPane pdPane;
	@FXML
	private TitledPane dataPane;
	@FXML
	private TitledPane eventPane;
	@FXML
	private TextField fname;
	@FXML
	private TextField lname;
	@FXML
	private TextField code;

	private String filiere="";
	private String filiereE="";
	@FXML
	private TextField nomprof;
	@FXML 
	private TextField nomcours;
	@FXML
	private TextField nomfich;
	
	@FXML
	private TextField sec;
	@FXML
	private TextField age;
	@FXML
	private TextField matricule;
	@FXML
	public ListView<String> logList;
	@FXML
	public ListView<String> output;
	@FXML
	public ProgressIndicator pb;
	@FXML
	public Label savedLabel;
	@FXML
	public Label warning;
	@FXML
	public Label warningcode;
	@FXML
	public Label warningface;
	@FXML
	public Label warningperiode;
	@FXML
	public AnchorPane dialogface;
	@FXML
	public Label title;
	@FXML
	public TilePane tile;
	@FXML
	public TextFlow ocr;
	
	@FXML
	public DatePicker dateFiche; //LocalDate localDate = dateFiche.getValue(); String s = String.valueOf(localDate);
	@FXML
	public RadioButton periode1;
	@FXML
	public RadioButton periode2; //Methode isSelected pour verifier que c'est sélectionné
	@FXML
	public ToggleGroup tg;
	
    @FXML
    private ComboBox comb;
    @FXML
    private ComboBox combE;
	
	

//**********************************************************************************************
	FaceDetector faceDetect = new FaceDetector();	//Creating Face detector object									
	ColoredObjectTracker cot = new ColoredObjectTracker(); //Creating Color Object Tracker object		
	Database database = new Database();		//Creating Database object

	OCR ocrObj = new OCR();
	ArrayList<String> user = new ArrayList<String>();
	ImageView imageView1;
	
	public static ObservableList<String> event = FXCollections.observableArrayList();
	public static ObservableList<String> outEvent = FXCollections.observableArrayList();

	public boolean enabled = false;
	public boolean isDBready = false;
	

	FileChooser fileChooser = new FileChooser();
	DirectoryChooser directoryChooser = new DirectoryChooser();
	
    @FXML
    public void Selectionner() {
    	try 
    	{
    	filiere = comb.getSelectionModel().getSelectedItem().toString();
    	}catch(Exception e){
    		filiere = "";		
    	}
    	System.out.println("filiere sélectionné: "+filiere);
    	
    }
    
    @FXML
    public void SelectionnerPrim() {
    	try 
    	{
    	filiereE = combE.getSelectionModel().getSelectedItem().toString();
    	}catch(Exception e){
    		filiereE = "";		
    	}
    	System.out.println("filiere sélectionné: "+filiereE);
    	
    }

	
	//**********************************************************************************************
	public void putOnLog(String data) {

		Instant now = Instant.now();

		String logs = now.toString() + ":\n" + data;

		event.add(logs);

		logList.setItems(event);
		
	}
	
	@FXML
	protected void chooseFile() {
		
		directoryChooser.setTitle("Choisir le repertoire");
		File dir = directoryChooser.showDialog(new Stage());

		try {
	
		String name = dir.getPath();
		nomfich.setText(name);
		}catch(Exception e) {
			
			
		}
		
		
		
	}

	@FXML
	protected void startCamera() throws SQLException {
		
		System.out.println("La filiere est: " +filiere);
		
		//*******************************************************************************************
		//initializing objects from start camera button event
		faceDetect.init();

		faceDetect.setFrame(frame);

		faceDetect.start();
		
		
		if (!database.init()) {

			putOnLog("Error: Database Connection Failed ! ");

		} else {
			isDBready = true;
			putOnLog("Success: Database Connection Succesful ! ");
		}

		//*******************************************************************************************
		//Activating other buttons
		startCam.setVisible(false);
		stopBtn.setVisible(true);
		motionBtn.setDisable(false);
		saveBtn.setDisable(false);

		if (isDBready) {
			recogniseBtn.setDisable(false);
		}
		
		ObservableList<String> list = FXCollections.observableArrayList("L1-GENIE_INFO","L2-ISR","L2-GENIE_LOGICIEL","L3-QSIR","L3-CDRI");
		comb.getSelectionModel().clearSelection();
		combE.getSelectionModel().clearSelection();
		comb.setItems(list);
		combE.setItems(list);
	

		
		dataPane.setDisable(false);
		eventPane.setDisable(false);

		if (stopRecBtn.isDisable()) {
			//stopRecBtn.setDisable(false);
		}
		//*******************************************************************************************
		
		
		tile.setPadding(new Insets(15, 15, 55, 15));
		tile.setHgap(30);
		
		//**********************************************************************************************
		//Picture Gallary
		
		String path = filePath;

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		
		//Image reader from the mentioned folder
	tile.getChildren().clear();
	
		for (final File file : listOfFiles) {

			imageView1 = createImageView(file);
			tile.getChildren().addAll(imageView1);
			
		}
	faceDetect.setCount(listOfFiles.length);
	System.out.println(listOfFiles.length);
		putOnLog(" Real Time WebCam Stream Started !");
		
		//**********************************************************************************************
	}
	int count = 0;
	
	
	@FXML
	protected void makeList() {
		
		
		
		// Vérifier que les champs de présences sont tous entrés
		
		if(filiere.equals("") || dateFiche.getValue()==null || nomprof.getText().equals("") || nomcours.getText().equals("") || nomfich.getText().equals("")) 
		{
		
			new Thread(() -> {

				try 
				{
					warning.setVisible(true);

					Thread.sleep(4000);

					warning.setVisible(false);

				} catch (InterruptedException ex) 
				{
					
				}

			}).start();
			
		}
		
		else if(!periode1.isSelected() && !periode2.isSelected() ) 
		{
			new Thread(() -> {

				try 
				{
					warningperiode.setVisible(true);

					Thread.sleep(4000);

					warningperiode.setVisible(false);

				} catch (InterruptedException ex) 
				{
					
				}

			}).start();
			
			
		}
		
		else 
		{
		
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH-mm-ss");
			DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate localDate = dateFiche.getValue(); 
			String date = dtf2.format(localDate);
			String heure = dtf.format(LocalTime.now());
			
			
			String liste = "\\"+date+"#"+heure+".txt";
			System.out.println(liste);
			
			String chemin = nomfich.getText();
			System.out.println(chemin);
			
			String repertoire = chemin+liste;
			System.out.println(repertoire);
			
			File fichier = new File(repertoire);
			
			try 
			{
				fichier.createNewFile();
				
			}catch(IOException e)
			
			{
				
				System.out.println(e.getMessage());
			}
			
			
			/*
				Ecrire l'en tete du fichier (Filiere, date, nom du professeur, nom du cours et periode)
			*/
			String periode=null;
			
			if(periode1.isSelected()) periode = periode1.getText();
			if(periode2.isSelected()) periode = periode2.getText();
			
			String ligne = String.valueOf(date)+"||"+filiere+"||"+nomprof.getText()+"||"+nomcours.getText()+"||"+periode;
			try 
			{
				FileWriter writer = new FileWriter(fichier,true);
				BufferedWriter bw = new BufferedWriter(writer);
				
				
				bw.newLine();
				bw.append("+----------------------------------------------------------------+");
				bw.newLine();
				bw.append("| "+ligne+" |");
				bw.newLine();
				bw.append("+----------------------------------------------------------------+");
				bw.newLine();
				bw.newLine();
				bw.append("*****************************Liste des étudiants présents*****************************");
				bw.newLine();
				
				bw.close();
				writer.close();
				
			}catch(Exception e1) 
			
			{
				
			}
			
			
			int id = database.createCourse(nomcours.getText(), filiere,nomprof.getText());
			
			if(id!=0) {
				putOnLog("le Cours à été créé dans la base de données ID: "+id);
			}
			
			
			faceDetect.setidCours(id);
			faceDetect.setperiodeP(periode);
			faceDetect.setdateP(String.valueOf(date));
			// On envoi une valeur True pour la variable ismakeList
			faceDetect.setRepertoire(repertoire);
			faceDetect.setIsMakeList(true);
			beginList.setVisible(false);
			beginList.setDisable(true);
			
			endList.setVisible(true);
			endList.setDisable(false);
			putOnLog("L'appel à démarré");
		
		}

	}
	
	@FXML
	protected void stopmakeList() {
		
		faceDetect.setIsMakeList(false);
		
		endList.setVisible(false);
		endList.setDisable(true);
		
		beginList.setVisible(true);
		beginList.setDisable(false);
		putOnLog("L'appel est terminé");
	}

	@FXML
	protected void faceRecognise() {

		
		faceDetect.setIsRecFace(true);
		// printOutput(faceDetect.getOutput());

		recogniseBtn.setText("Détails parsonels");
		beginList.setDisable(false);

		//Getting detected faces
		user = faceDetect.getOutput();
		try {
			if (count > 0) 
			{
	
				//Retrieved data will be shown in Fetched Data pane
				String t = "********* Etudiant: " + user.get(1) + " " + user.get(2) + " *********";
	
				outEvent.add(t);
	
				String n1 = "Nom: " + user.get(1);
	
				outEvent.add(n1);
	
				output.setItems(outEvent);
	
				String n2 = "Prenom: " + user.get(2);
	
				outEvent.add(n2);
	
				output.setItems(outEvent);
	
				String fc = "Matricule: " + user.get(5);
	
				outEvent.add(fc);
	
				output.setItems(outEvent);
	
				String r = "Age: " + user.get(3);
	
				outEvent.add(r);
	
				output.setItems(outEvent);
	
				String a = "Filiere: " + user.get(4);
	
				outEvent.add(a);
	
				output.setItems(outEvent);
			
	
			}
		}catch(Exception e) 
		{
			System.out.println("Aucun visage detecté");
		}
		count++;

		putOnLog("Face Recognition Activated !");

		stopRecBtn.setDisable(false);

	}

	@FXML
	protected void stopRecognise() 
	{

		faceDetect.setIsRecFace(false);
		faceDetect.clearOutput();
		stopmakeList();
		count =0;
		

		this.user.clear();
	
		recogniseBtn.setText("Reconnaissance");

		stopRecBtn.setDisable(true);
		
		beginList.setDisable(true);

		putOnLog("Face Recognition Deactivated !");

	}

	@FXML
	protected void startMotion() {

		faceDetect.setMotion(true);
		putOnLog("motion Detector Activated !");

	}

	@FXML
	protected void saveFace() throws SQLException {

		//Input Validation
		if (fname.getText().trim().isEmpty()  || code.getText().trim().isEmpty() || matricule.getText().trim().isEmpty() || lname.getText().trim().isEmpty() || age.getText().trim().isEmpty() || filiereE.equals("")  ) {

			new Thread(() -> {

				try {
					warning.setVisible(true);

					Thread.sleep(4000);

					warning.setVisible(false);

				} catch (InterruptedException ex) {
				}

			}).start();

		} else if(code.getText().length()<4 || code.getText().length()>4) {
			
			new Thread(() -> {

				try {
					warningcode.setVisible(true);

					Thread.sleep(4000);

					warningcode.setVisible(false);

				} catch (InterruptedException ex) {
				}

			}).start();
			
		} /*
			 * else if(!faceDetect.faceDetected()) {
			 * 
			 * new Thread(() -> {
			 * 
			 * try { warningface.setVisible(true);
			 * 
			 * Thread.sleep(4000);
			 * 
			 * warningface.setVisible(false);
			 * 
			 * } catch (InterruptedException ex) { }
			 * 
			 * }).start(); }
			 */ else if(database.userExist(Integer.parseInt(code.getText()))) {
			saveBtn.setDisable(true);
			pdPane.setDisable(true);
			dialogface.setVisible(true);
			dialogface.setDisable(false);
		}
		
		else {
			//Progressbar
			pb.setVisible(true);

			savedLabel.setVisible(true);

			new Thread(() -> {

				try {
					faceDetect.setFname(fname.getText());

					faceDetect.setFname(fname.getText());
					faceDetect.setLname(lname.getText());
					faceDetect.setAge(Integer.parseInt(age.getText()));
					faceDetect.setCode(Integer.parseInt(code.getText()));
					faceDetect.setSec(filiereE);
					faceDetect.setId(matricule.getText());
					

					database.setFname(fname.getText());
					database.setLname(lname.getText());
					database.setAge(Integer.parseInt(age.getText()));
					database.setCode(Integer.parseInt(code.getText()));
					database.setSec(filiereE);
					database.setId(matricule.getText());
					

					database.insert();
					faceDetect.setSaveFace(true);
					
					javafx.application.Platform.runLater(new Runnable(){
						
						@Override
						 public void run() {
							pb.setProgress(100);
						 }
						 });


					

					savedLabel.setVisible(true);
					Thread.sleep(4000);
					
					javafx.application.Platform.runLater(new Runnable(){
						
						@Override
						 public void run() {
							pb.setVisible(false);
						 }
						 });


					javafx.application.Platform.runLater(new Runnable(){
						
						@Override
						 public void run() {
					 savedLabel.setVisible(false);
						 }
						 });

					
					
				} catch (InterruptedException ex) {
				}

			}).start();

			
						
			String path = filePath;

			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();
			
			//Image reader from the mentioned folder
		tile.getChildren().clear();
			for (final File file : listOfFiles) {

				imageView1 = createImageView(file);
				tile.getChildren().addAll(imageView1);
				
			}
		

		} 

	}
	
	@FXML
	protected void oui() {
		//Progressbar
		pb.setVisible(true);

		savedLabel.setVisible(true);
		dialogface.setVisible(false);
		dialogface.setDisable(true);

		new Thread(() -> {

			try {

				faceDetect.setFname(fname.getText());

				faceDetect.setFname(fname.getText());
				faceDetect.setLname(lname.getText());
				faceDetect.setAge(Integer.parseInt(age.getText()));
				faceDetect.setCode(Integer.parseInt(code.getText()));
				
				faceDetect.setId(matricule.getText());
				
				javafx.application.Platform.runLater(new Runnable(){
					
					@Override
					 public void run() {
						pb.setProgress(100);
					 }
					 });


				

				savedLabel.setVisible(true);
				Thread.sleep(4000);
				
				javafx.application.Platform.runLater(new Runnable(){
					
					@Override
					 public void run() {
						pb.setVisible(false);
					 }
					 });


				javafx.application.Platform.runLater(new Runnable(){
					
					@Override
					 public void run() {
				 savedLabel.setVisible(false);
					 }
					 });

			} catch (InterruptedException ex) {
			}

		}).start();

		faceDetect.setSaveFace(true);
		saveBtn.setDisable(false);
		pdPane.setDisable(false);
		
		String path = filePath;

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		//Image reader from the mentioned folder
	tile.getChildren().clear();
	
		for (final File file : listOfFiles) {

			imageView1 = createImageView(file);
			tile.getChildren().addAll(imageView1);
			
		}
	
	}
	
	@FXML
	protected void non() {
		dialogface.setVisible(false);
		dialogface.setDisable(true);
		saveBtn.setDisable(false);
		pdPane.setDisable(false);
	}

	@FXML
	protected void stopCam() throws SQLException {

		faceDetect.stop();

		startCam.setVisible(true);
		stopBtn.setVisible(false);
		faceDetect.setIsMakeList(false);

		/* this.saveFace=true; */

		putOnLog("Cam Stream Stopped!");

		recogniseBtn.setDisable(true);
		saveBtn.setDisable(true);
		dataPane.setDisable(true);
		eventPane.setDisable(true);
		stopRecBtn.setDisable(true);
		beginList.setDisable(true);

		
		database.db_close();
		putOnLog("Database Connection Closed");
		isDBready=false;
	}

	@FXML
	protected void ocrStart() {

		try {

			Text text1 = new Text(ocrObj.init());

			text1.setStyle("-fx-font-size: 14; -fx-fill: blue;");

			ocr.getChildren().add(text1);

		} catch (FontFormatException e) {

			e.printStackTrace();
		}

	}

	@FXML
	protected void capture() {

		faceDetect.setOcrMode(true);

	}

	@FXML
	protected void startGesture() {

		faceDetect.stop();
		cot.init();

		Thread th = new Thread(cot);
		th.start();

		gesture.setVisible(false);
		gestureStop.setVisible(true);

	}

	@FXML
	protected void startEyeDetect() {

		faceDetect.setEyeDetection(true);
		eyeBtn.setDisable(true);

	}

	@FXML
	protected void upperBodyStart() {

		faceDetect.setUpperBody(true);
		;
		upperBodyBtn.setDisable(true);

	}

	@FXML
	protected void fullBodyStart() {

		faceDetect.setFullBody(true);
		fullBodyBtn.setDisable(true);

	}

	@FXML
	protected void smileStart() {

		faceDetect.setSmile(true);
		smileBtn.setDisable(true);

	}

	@FXML
	protected void stopGesture() {

		cot.stop();
		faceDetect.start();

		gesture.setVisible(true);
		gestureStop.setVisible(false);

	}

	@FXML
	protected void shapeStart() {

		// faceDetect.stop();

		SquareDetector shapeFrame = new SquareDetector();
		shapeFrame.loop();

	}

	private ImageView createImageView(final File imageFile) {

		try {
			final Image img = new Image(new FileInputStream(imageFile), 120, 0, true, true);
			imageView1 = new ImageView(img);

			imageView1.setStyle("-fx-background-color: BLACK");
			imageView1.setFitHeight(120);

			imageView1.setPreserveRatio(true);
			imageView1.setSmooth(true);
			imageView1.setCache(true);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return imageView1;
	}

}
