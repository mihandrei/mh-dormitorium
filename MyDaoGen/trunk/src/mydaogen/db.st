group db;

dbdef(package,entitytype,table,
	  fnames,fNames,ftypes,
	  pknames,pkNames, pktypes,
	  cols,pkcols,
	  fAutoinc,fautoinctype,not_auto_cols,not_auto_fNames ) ::=<<
package <package>;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class <entitytype>DB {
  
    <query(...)>

    <if (pknames)>
    <getbypk(...)>
    <endif>

    <insert(...)>
    
    <update(...)>

    <if (pknames)>
    <updatepk(...)>
    <endif>
}
>>


query(entitytype,table,ftypes,cols) ::=<<

public List\<<entitytype>\> query(Connection con,String whereClause,Object ... args)throws SQLException{
      PreparedStatement stm = con.prepareStatement("select * from <table> " +  whereClause);
      for(int i=0;i\<args.length;i++){
         stm.setObject(i + 1, args[i]);
      }

      ResultSet rs = stm.executeQuery();

        List\<<entitytype>\> ret = new ArrayList\<<entitytype>\>();
        while (rs.next()) {
            <ftypes,fnames,cols:{t,n,c|<t> <n> =(<t>) rs.getObject("<c>");
}>
            ret.add(new <entitytype>(<fnames; separator=","> ));
        }
        rs.close();
        return ret;
}
>>

getbypk(entitytype,pktypes,pknames,pkcols) ::=<<
public <entitytype> getbypk(Connection con <pktypes,pknames:{t,n|,<t> <n>}>)throws SQLException{
    String whereClause = "where <pkcols:{c|<c> = ?};separator = " and ">";

    List\<<entitytype>\> res = query(con,whereClause <pknames:{n|,<n>}>);
    if(res.size() == 0)
        return null;
    else
        return res.get(0);
}
>>

insert(entitytype,table,not_auto_cols,fNames,not_auto_fNames,fAutoinc,fautoinctype) ::=<<
public <entitytype> insert(Connection con, <entitytype> <table>)throws SQLException{
	String sql = "insert into <table>(<not_auto_cols; separator = ",">) values (<not_auto_cols:{c|?}; separator = ",">)";

	PreparedStatement stm = con.prepareStatement(sql <if (fAutoinc)>, Statement.RETURN_GENERATED_KEYS <endif>);
		
	<not_auto_fNames:{N|stm.setObject(<i>, <table>.get<N>());
}>
	stm.executeUpdate();
    
    
    <if (fAutoinc)>
    ResultSet rs = stm.getGeneratedKeys();
    rs.next();
    <! hack ; assumes ever autoincr column is an integer; done because casting
    to fautoinctype fails . ex: getobject returns long instead of the expected integer!>
    return <table>.set<fAutoinc>(rs.getInt(1));
    <else>	
    return <table>;
    <endif>
     
}
>>

update(table)::=<<
public void update(Connection con, String setClause, Object... args) throws SQLException {
	PreparedStatement stm = con.prepareStatement("update <table> " + setClause);
	for (int i = 0; i \< args.length; i++) {
		stm.setObject(i + 1, args[i]);
	}
	stm.executeUpdate();
}
>>

updatepk(entitytype, table, pkcols, fNames, not_auto_cols, pkNames)::=<<
public void update(Connection con, <entitytype> <table>) throws SQLException{
	String setClause = "set <not_auto_cols:{c|<c> = ?}; separator = ","> where <pkcols:{c|<c> = ?};separator = " and ">";
	update(con, setClause, <fNames:{N| <table>.get<N>()};separator = ",">,<pkNames:{N| <table>.get<N>()};separator = ",">);
}
>>
