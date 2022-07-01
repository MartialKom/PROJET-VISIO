package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class Liste 

{


	
	public void editerListe(String matricule, String nom, String prenom, String filiere, String path) 
	{
		
		String ligne = matricule+"#"+nom+"#"+prenom+"#"+filiere+"#";
	    FileInputStream file;
	    File fichier = new File(path);
	    
		try 
		{
			FileWriter writer = new FileWriter(fichier,true);
			BufferedWriter bw = new BufferedWriter(writer);
			
			file = new FileInputStream(path);
			Scanner scanner = new Scanner(file); 
			
			while(scanner.hasNextLine()) 
			{
				
				if(scanner.nextLine().equalsIgnoreCase(ligne)) 
				{
					System.out.println("L'étudiant est déja sur la liste de présence");
					return;
				}
			}
			scanner.close();
			
			//Ecriture dans le ficier
			bw.append(ligne);
			bw.newLine();
			System.out.println("Nom enregistré");
			bw.close();
			writer.close();
			
			
			
			
		} catch (Exception e) 
		{
			
			System.out.println("Exception lors de l'édition du fichier: "+e.getMessage());
		}   
	       
		
	}
	
}
