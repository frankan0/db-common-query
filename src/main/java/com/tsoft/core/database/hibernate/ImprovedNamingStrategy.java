package com.tsoft.core.database.hibernate;


public class ImprovedNamingStrategy extends org.hibernate.cfg.ImprovedNamingStrategy {

	private static final long serialVersionUID = -7925674946234337403L;

	public String tableName(String tableName) {
		return ImprovedNamingStrategy.addUnderscores(tableName);
	}

	protected static String addUnderscores(String name) {
		StringBuilder buf = new StringBuilder( name.replace('.', '_') );
		for (int i=1; i<buf.length()-1; i++) {
			if (
				Character.isLowerCase( buf.charAt(i-1) ) &&
				Character.isUpperCase( buf.charAt(i) ) &&
				Character.isLowerCase( buf.charAt(i+1) )
			) {
				buf.insert(i++, '_');
			}
		}
		return buf.toString();
	}
}