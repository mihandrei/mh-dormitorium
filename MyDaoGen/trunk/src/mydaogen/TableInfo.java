package mydaogen;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * immutable table info
 */
final class TableInfo {

    public final List<ColumnInfo> cols;
    public final List<String> pk;
    public final String name;
    //indexes columninfos by column name for o(1) access. probably unnecesary
    private Map<String,ColumnInfo> index = new HashMap<String, ColumnInfo>(); 

    public TableInfo(String tname, List<ColumnInfo> cols2, List<String> pk) {
        this.cols = Collections.unmodifiableList(cols2); 
        this.pk = Collections.unmodifiableList(pk);
        this.name= tname;
        
        for(ColumnInfo ci:this.cols){
        	index.put(ci.name, ci);
        }
    }
    
    public ColumnInfo getColumn(String name){
    	return index.get(name);
    }
}
