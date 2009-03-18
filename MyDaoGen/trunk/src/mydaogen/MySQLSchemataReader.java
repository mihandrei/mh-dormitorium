package mydaogen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class MySQLSchemataReader {

    public static Set<String> getTableNames(Connection con) throws SQLException {
        DatabaseMetaData md = con.getMetaData();
        ResultSet rs = md.getTables(null, null, null, null);

        Set<String> ret = new HashSet<String>();
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            ret.add(tableName);
        }
        rs.close();
        
        return ret;
   }

     public static List<String> getPK(Connection con,String table) throws SQLException {
        DatabaseMetaData md = con.getMetaData();
        ResultSet rs = md.getPrimaryKeys(null, null, table );

        List<String> ret = new ArrayList<String>();
        while (rs.next()) {
            String col = rs.getString("COLUMN_NAME");
            ret.add(col);
        }
        rs.close();

        return ret;
    }
     
    public static Map<String,String> getCol(Connection con,String table) throws SQLException{
        PreparedStatement stm = con.prepareStatement("select * from " + table + " LIMIT 0");
        
        ResultSet rs = stm.executeQuery();
        ResultSetMetaData meta = rs.getMetaData();

        Map<String,String> ret = new HashMap<String, String>();
        for (int i=0;i<meta.getColumnCount();i++){
            String name = meta.getColumnName(i+1);
            String type = meta.getColumnClassName(i+1);
            ret.put(name, type);
        }
        rs.close();
        return ret;
    }
    
     public static Map<String,Table> getMeta(Connection con) throws SQLException{
        Set<String> tnames = getTableNames(con);
        
        Map<String,Table> ret = new HashMap<String,Table>();
        
        for(String tname:tnames){
            Map<String, String> cols = getCol(con, tname);
            List<String> pk = getPK(con, tname);

            ret.put(tname, new Table(tname,cols,pk));
        }

        return ret;
     }
    
}

