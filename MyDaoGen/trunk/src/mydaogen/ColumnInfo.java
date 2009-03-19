package mydaogen;

class ColumnInfo {

	public final boolean autoinc;
	public final String type;
	public final String name;

	public ColumnInfo(String name, String type, boolean autoinc) {
		this.name=name;
		this.type=type;
		this.autoinc=autoinc;
	}

}
