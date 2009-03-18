package mydaogen;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class Main {

    static String baseDir = "src/mh/dao";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Connection con = DB.getDbConnection();
        Map<String, Table> meta = MySQLSchemataReader.getMeta(con);
        con.close();

        for (String tname : meta.keySet()) {
            Table t = meta.get(tname);
            String claz = Builder.buildClass(t);
            String dao = Builder.buildDao(t);
            try {
                writeFile(baseDir + "/" + Builder.capFirst(tname) + ".java", claz);
                writeFile(baseDir + "/" + Builder.capFirst(tname) + "DB.java", dao);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void writeFile(String path, String content) throws IOException {
        FileWriter fw = new FileWriter(path);
        fw.write(content);
        fw.close();
    }
}
