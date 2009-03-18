package mydaogen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public class Main {


	public static void main(String[] args) throws SQLException, ClassNotFoundException {

		Properties config = null;
		try {
			if (args.length == 1) {// conf file path
				config = loadConfiguration(new FileInputStream(args[0]));
			} else { //default conf file
				config = loadConfiguration(new FileInputStream("dbconf.properties"));
			}
		} catch (FileNotFoundException e) {
			System.err.println("Sorry! Cannot open file.");
		} catch (IOException e) {
			System.err.println("Sorry! Cannot open file.");
		}

		String dbDriver = config.getProperty("dbDriver");
		String dbName = config.getProperty("dbName");
		String dbUser = config.getProperty("dbUser");
		String dbPassword = config.getProperty("dbPassword");
		String baseDir = config.getProperty("baseDir");
		String packageName = config.getProperty("packageName");
		
		Connection con = getDbConnection(dbDriver, dbName, dbUser, dbPassword);
		Map<String, Table> meta = MySQLSchemataReader.getMeta(con);
		con.close();

		for (String tname : meta.keySet()) {
			Table t = meta.get(tname);
			String claz = Builder.buildClass(t, packageName);
			String dao = Builder.buildDao(t, packageName);
			try {
				writeFile(baseDir + "/" + Builder.capFirst(tname) + ".java", claz);
				writeFile(baseDir + "/" + Builder.capFirst(tname) + "DB.java", dao);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static Properties loadConfiguration(InputStream is) throws IOException {
		Properties properties = new Properties();
		properties.load(is);
		return properties;
	}

	private static void writeFile(String path, String content) throws IOException {
		FileWriter fw = new FileWriter(path);
		fw.write(content);
		fw.close();
	}

	private static Connection getDbConnection(String dbDriver, String dbName, String dbUser, String dbPassword) throws ClassNotFoundException,
			SQLException {
		Connection conn;
		Class.forName(dbDriver);
		conn = DriverManager.getConnection(dbName, dbUser, dbPassword);
		return conn;
	}
}
