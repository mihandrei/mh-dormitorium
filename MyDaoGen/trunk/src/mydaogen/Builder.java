package mydaogen;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

public class Builder {
	
	private static StringTemplate load(String group,String template){
		InputStream str = Builder.class.getResourceAsStream(group);
		StringTemplateGroup templateGroup = new StringTemplateGroup(new InputStreamReader(str));

		return templateGroup.getInstanceOf(template);
	}
	
	/**
	 *Fills in the template with the table meta data.
	 */
	private static void fillTemplate(StringTemplate template,TableInfo t, String packageName){
		template.setAttribute("package", packageName);

		template.setAttribute("table", t.name);
		template.setAttribute("entitytype", entityName(t.name));

		Set<String> imports = new TreeSet<String>();
		
		for (ColumnInfo col : t.cols) {
			template.setAttribute("cols", col.name); //the columns
			template.setAttribute("fnames", col.name.toLowerCase()); //the coresponding field name
			template.setAttribute("fNames", propertyName(col.name)); //and property

			String[] ss = splitType(col.type);
			String package_import = ss[0];
			String type = ss[1];

			template.setAttribute("ftypes", type);

			if (!package_import.equals("java.lang")) {
				imports.add(package_import);
			}
		}
		
		for (String name : t.pk) {
			String type = splitType(t.getColumn(name).type)[1];
			template.setAttribute("pktypes", type);
			template.setAttribute("pkcols", name);
			template.setAttribute("pknames", name);
			template.setAttribute("pkNames", propertyName(name));
		}
		
		for (String imp : imports) {
			template.setAttribute("imports", imp);
		}
	}

	public static String buildDao(TableInfo t, String packageName) {
		StringTemplate template = 	load("db.st","dbdef");
		fillTemplate(template, t, packageName);
		return template.toString();
	}
	
	public static String buildEntity(TableInfo t, String packageName) {
		StringTemplate template = 	load("entity.st","entitydef");
		fillTemplate(template, t, packageName);
		return template.toString();
	}
	
	public static String buildDaoFileName(String tname) {		
		return entityName(tname) + "DB.java";
	}

	public static String buildEntityFileName(String tname) {
		return entityName(tname) + ".java";
	}
	
	private static String[] splitType(String s) {
		int ldot = s.lastIndexOf(".");
		return new String[] { s.substring(0, ldot), s.substring(ldot + 1) };
	}
	
	/**
	 * the entity name of a table name
	 */
	private static String entityName(String s) {
		s = s.replace(" ", "_");
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	/**
	 *property name of a column 
	 */
	private static String propertyName(String s){
		return entityName(s);
	}

}
