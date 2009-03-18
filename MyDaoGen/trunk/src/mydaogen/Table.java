package mydaogen;

import java.util.List;
import java.util.Map;


public class Table {

    public final Map<String, String> cols;
    public final List<String> pk;
    public final String name;


    Table(String tname, Map<String, String> cols, List<String> pk) {
        this.cols = cols;
        this.pk = pk;
        this.name= tname;
    }
}
