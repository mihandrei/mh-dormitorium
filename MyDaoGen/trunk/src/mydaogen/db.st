group db;

dbdef(package,entitytype,table,types,cols,Cols,pktypes,pkcol) ::=<<
package <package>;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class <entitytype>DB {
  
    <query(...)>

    <getbypk(...)>
    
    <insert(...)>
}
>>


query(entitytype,table,types,cols) ::=<<

public List\<<entitytype>\> query(Connection con,String whereClause,Object ... args)throws SQLException{
      PreparedStatement stm = con.prepareStatement("select * from <table> " +  whereClause);
      for(int i=0;i\<args.length;i++){
         stm.setObject(i + 1, args[i]);
      }

      ResultSet rs = stm.executeQuery();

        List\<<entitytype>\> ret = new ArrayList\<<entitytype>\>();
        while (rs.next()) {
            <types,cols:{t,c|<t> <c> =(<t>) rs.getObject("<c>");
}>
            ret.add(new <entitytype>(<cols; separator=","> ));
        }
        rs.close();
        return ret;
}
>>

getbypk(entitytype,pktypes,pkcol) ::=<<
public <entitytype> getbypk(Connection con <pktypes,pkcol:{t,c|,<t> <c>}>)throws SQLException{
    String whereClause = "where <pkcol:{c|<c> = ?}>";
    return query(con,whereClause <pkcol:{c|,<c>}>).get(0);
}
>>

insert(entitytype,table,types,cols,Cols) ::=<<
public void insert(Connection con, <entitytype> r)throws SQLException{
	PreparedStatement stm = con.prepareStatement("insert into <table>(<cols; separator = ",">) values (<cols:{c|?}; separator = ",">)");
		
	<cols, Cols:{c, C|stm.setObject(<i>, r.get<C>());
}>
	stm.executeUpdate();
    
}
>>

