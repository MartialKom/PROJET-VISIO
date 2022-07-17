package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

class Database {
	public int matricule;

	public String fname;
	public String Lname;
	
	public int age;
	public String sec;
	public String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public final String Database_name = "visio";
	public final String Database_user = "root";
	public final String Database_pass = "";

	public Connection con;

	public boolean init() throws SQLException {
		
		String lien="jdbc:mysql://localhost:3306/visio";
		String user="root";
		String pass="";
		try {
			Class.forName("com.mysql.jdbc.Driver");

			try {
				this.con = DriverManager.getConnection(lien,user,pass);
			} catch (SQLException e) {

				System.out.println("Error: Database Connection Failed ! Please check the connection Setting");

				return false;

			}

		} catch (ClassNotFoundException e) {

			e.printStackTrace();

			return false;
		}

		return true;
	}

	public void insert() {
		String sql = "INSERT INTO face_bio (code, first_name, last_name, age, filiere,matricule) VALUES (?, ?, ?, ?,?,?)";

		PreparedStatement statement = null;
		try {
			statement = (PreparedStatement) con.prepareStatement(sql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			statement.setInt(1, this.matricule);
			statement.setString(2, this.fname);

			statement.setString(3, this.Lname);
			
			statement.setInt(4, this.age);
			statement.setString(5, this.sec);
			statement.setString(6, this.id);

			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("A new face data was inserted successfully!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ArrayList<String> getUser(int inCode) throws SQLException {

		ArrayList<String> user = new ArrayList<String>();

		try {

			Database app = new Database();

			String sql = "select * from face_bio where code=" + inCode + " limit 1";

			Statement s = con.createStatement();

			ResultSet rs = s.executeQuery(sql);

			while (rs.next()) {

			

				user.add(0, Integer.toString(rs.getInt(1)));
				user.add(1, rs.getString(2));
				user.add(2, rs.getString(3));
				user.add(3, Integer.toString(rs.getInt(4)));
				user.add(4, rs.getString(5));
				user.add(5,rs.getString(6));

			
			}

			con.close(); // closing connection
		} catch (Exception e) {
			e.getStackTrace();
		}
		return user;
	}
	
	public boolean userExist(int code) {
		System.out.println(code);
		Database app = new Database();
		try {
			
			String sql = "select * from face_bio where code="+ code +" limit 1";

			Statement s = con.createStatement();

			ResultSet rs = s.executeQuery(sql);
			
			if(!rs.next()) {
				System.out.println("aucun résultat");
				return false;
						}
			else {
				System.out.println("Resultat trouvé");
				return true;
			}
			
		}catch(Exception e) {
			return false;
		}
		
	}
	
	public int idCourse(String nom, String filiere) {
		int id=0;
		try {
			String sql = "select id from cours where nom_C=? and filiere=? limit 1 ";
			
			PreparedStatement prepare = null;
			prepare = (PreparedStatement) con.prepareStatement(sql);
			prepare.setString(1, nom);
			prepare.setString(2, filiere);
			
			
			ResultSet rs = prepare.executeQuery();
			
			while(rs.next()) {
			id = rs.getInt("id");}
			return id;
			
		}catch(Exception e) {
			System.out.println("idCourse: "+e.getMessage());
		}
		
		return id;
	}
	
	public int createCourse(String nom, String filiere, String nomprof) {
		int id = 0;
		
		try {
			
			String sql = "select * from cours where nom_C=? and filiere=? limit 1 ";
			PreparedStatement prepare = null;
			prepare = (PreparedStatement) con.prepareStatement(sql);
			prepare.setString(1, nom);
			prepare.setString(2, filiere);
		
			ResultSet r = prepare.executeQuery();
			
			if(r.next()) 
			{
				id = idCourse(nom, filiere);
				return id;
			} else 
			{
				String req ="insert into cours(nom_C,filiere,departement,nom_Prof) values(?,?,?,?) ";
				
				
				PreparedStatement statement =null;
				
				
					statement = (PreparedStatement) con.prepareStatement(req);
					statement.setString(1, nom);
					statement.setString(2, filiere);
					statement.setString(3, Main.departement);
					statement.setString(4, nomprof);
					int result = statement.executeUpdate();
					
					if(result>0) 
					{
						id = idCourse(nom, filiere);
						return id;
					}
					
			}
		}
		catch(Exception e) {
			System.out.println("createCourse: "+e.getMessage());
			return id;
		}
		
		return id;		
	}
	
	
	
	public void insererPresence(int idCours, String matricule, String date, String periode) {
		
		String sql = "select * from presence where id_c=? and matricule_E=? and dateP=? and periode=?";
		
		PreparedStatement prepare1 = null;
		
		try {
			prepare1 = (PreparedStatement) con.prepareStatement(sql);
			
			prepare1.setInt(1, idCours);
			prepare1.setString(2, matricule);
			prepare1.setString(3, date);
			prepare1.setString(4, periode);
			
			ResultSet r = prepare1.executeQuery();
			
			if(r.next()) {
				System.out.println("l'étudiant est déjà dans la liste de presence");
				return;
			}
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		
		String req = "insert into presence values (?,?,?,?)";
		
		PreparedStatement statement =null;
		
		try {
			statement = (PreparedStatement) con.prepareStatement(req);
			statement.setInt(1, idCours);
			statement.setString(2, matricule);
			statement.setString(3, date);
			statement.setString(4, periode);
			statement.executeUpdate();
			
			System.out.println("Insertion dans la table présence réussie");
		} catch (SQLException e) {
			
			System.out.println(e.getMessage());
		}
		
	}
	
	public boolean verifFiliere(int idCours, String filiereE) {
		
		String req = " select filiere from cours where id=? ";
		String filiereCours = null;
		try {
			
			PreparedStatement prepare = null;
			
			prepare = (PreparedStatement) con.prepareStatement(req);
			prepare.setInt(1, idCours);
			
			ResultSet r =prepare.executeQuery();
			while(r.next()) {
				 filiereCours = r.getString("filiere");
			}
			System.out.println("la filiere du cours est: "+filiereCours);
			
			if(filiereCours.equals(filiereE)) return true;
			else return false;
		}catch(Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		
	}

	public void db_close() throws SQLException
	{
		try {
			con.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public int getCode() {
		return matricule;
	}

	public void setCode(int code) {
		this.matricule = code;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return Lname;
	}

	public void setLname(String lname) {
		Lname = lname;
	}



	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSec() {
		return sec;
	}

	public void setSec(String sec) {
		this.sec = sec;
	}

}
