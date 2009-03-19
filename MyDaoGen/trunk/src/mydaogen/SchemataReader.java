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

public final class SchemataReader {

	private static Set<String> getTableNames(DatabaseMetaData md) throws SQLException {
		ResultSet rs = md.getTables(null, null, null, null);

		Set<String> ret = new HashSet<String>();
		while (rs.next()) {
			ret.add(rs.getString("TABLE_NAME"));
		}
		rs.close();
		return ret;
	}

	private static List<String> getPK(DatabaseMetaData md, String table) throws SQLException {
		ResultSet rs = md.getPrimaryKeys(null, null, table);

		List<String> ret = new ArrayList<String>();
		while (rs.next()) {
			ret.add(rs.getString("COLUMN_NAME"));
		}
		rs.close();
		return ret;
	}

	private static List<ColumnInfo> getCol(Connection con, String table) throws SQLException {
		PreparedStatement stm = con.prepareStatement("select * from " + table + " LIMIT 0");

		ResultSet rs = stm.executeQuery();
		ResultSetMetaData meta = rs.getMetaData();
		rs.close();

		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		for (int i = 0; i < meta.getColumnCount(); i++) {
			String name = meta.getColumnName(i + 1);
			boolean autoinc = meta.isAutoIncrement(i + 1);
			String type = meta.getColumnClassName(i + 1);
			ret.add(new ColumnInfo(name, type, autoinc));
		}
		return ret;
	}

	public static Map<String, TableInfo> getMeta(Connection con) throws SQLException {
		DatabaseMetaData meta = con.getMetaData();
		Set<String> tnames = getTableNames(meta);

		Map<String, TableInfo> ret = new HashMap<String, TableInfo>();

		for (String tname : tnames) {
			ret.put(tname, new TableInfo(tname, getCol(con, tname), getPK(meta, tname)));
		}
		return ret;
	}

}
