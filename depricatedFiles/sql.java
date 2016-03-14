//import com.mysql.jdbc.driver;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class sql {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/EMP";
	
	static final String USER = "root";
	static final String PASS = "root";

	
	public static void main(String[] args){
		Connection conn = null;
		Statement stmt = null;
		System.out.println("hello");
		try {
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * from file_table";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				String filename = rs.getString("filename");
				String licences = rs.getString("licences");
				System.out.println("filename: " + filename + " licences: " + licences + "\n");
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}
}
