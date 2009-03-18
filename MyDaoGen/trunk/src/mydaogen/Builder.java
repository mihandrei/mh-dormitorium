package mydaogen;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

public class Builder {


    public static String buildClass(Table t, String packageName) {
        InputStream str = Builder.class.getResourceAsStream("entity.st");
        StringTemplateGroup group = new StringTemplateGroup(new InputStreamReader(str));

        StringTemplate query = group.getInstanceOf("entitydef");
        query.setAttribute("package", packageName);


        query.setAttribute("class", capFirst(t.name));

        Set<String> imports = new HashSet<String>();
        for (String name : t.cols.keySet()) {
            query.setAttribute("fnames", name);
            query.setAttribute("fNames", capFirst(name));

            String[] ss = splitType(t.cols.get(name));
            String package_import = ss[0];
            String type = ss[1];

            query.setAttribute("ftypes", type);

            if (!package_import.equals("java.lang")) {
                imports.add(package_import);
            }
        }

        for (String imp : imports) {
            query.setAttribute("imports", imp);
        }

        return query.toString();
    }

    public static String buildDao(Table t, String packageName) {
        InputStream str = Main.class.getResourceAsStream("db.st");
        StringTemplateGroup group = new StringTemplateGroup(new InputStreamReader(str));

        StringTemplate query = group.getInstanceOf("dbdef");
        query.setAttribute("package", packageName);

        query.setAttribute("table", t.name);
        query.setAttribute("entitytype", capFirst(t.name));

        for (String name : t.cols.keySet()) {
            String type = splitType(t.cols.get(name))[1];
            //ignoring imports for now
            query.setAttribute("types", type);
            query.setAttribute("cols", name);
            query.setAttribute("Cols", capFirst(name));
        }

        for (String name : t.pk) {
            String type = splitType(t.cols.get(name))[1];
            query.setAttribute("pktypes", type);
            query.setAttribute("pkcol", name);
        }
        return query.toString();
    }

    static String[] splitType(String s) {
        int ldot = s.lastIndexOf(".");
        return new String[]{s.substring(0, ldot), s.substring(ldot + 1)};
    }

    static String capFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}

