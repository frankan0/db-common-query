
package com.tsoft.core.database.dialect;

import com.tsoft.core.database.utils.UncDate;

import java.sql.Types;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Dialect 的Oracle实现
 *
 */
public class OracleDialect extends Dialect {
    private static final int ERR_CODE_DUP_ENTRY = 1;
    private static final int ERR_CODE_TO_BIG_COLUMN_LENGTH = 1401;
    
    private static final Pattern PTN_SELECT_CLAUSE = Pattern.compile("(\\s*SELECT\\s+)(.*?)(\\s+FROM\\s+.*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern PTN_RESERVE_COL_NAME_CASE = Pattern.compile("(\\b\\w+\\b\\s+AS\\s+)\\b(\\w+)\\b", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    

    public String getDialectName() {
        return "Oracle";
    }    
    

	public int getCursorType() {
		return oracle.jdbc.OracleTypes.CURSOR;
	}


	public boolean supportsLimit() {
		return true;
	}
	

	public boolean supportsLimitOffset() {
		return true;
	}

	@Override
	public String getLimitString(String sql, int page, int pageSize) {
		if( pageSize > 0 )
		{
		    StringBuffer pagingSelect = new StringBuffer(100);
			int intStart = (page - 1)*pageSize;
			int intEnd = intStart + pageSize;
			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");

			pagingSelect.append(filter(sql));

			pagingSelect.append(" ) row_ where rownum <= " + intEnd + ") where rownum_ > " + intStart);
			return pagingSelect.toString();
		}else{
		    return sql;
		}
	}
    

    public int getErrorCodeForDuplicateEntry() {
        return ERR_CODE_DUP_ENTRY;
    }

    public int getErrorCodeForTooBigColumnLength() {
        // TODO Auto-generated method stub
        return ERR_CODE_TO_BIG_COLUMN_LENGTH;
    }

	/**
	 * 过滤SQL查询语句。
	 * 默认的Oracle查询对列不区分大小写，该过滤方法强制给列别名用引号（""）。
	 * e.g. SELECT id as id, name as name FROM User
	 * 过滤后返回：
	 * SELECT id as "id", name as "name" FROM User
	 * 这样可以保证返回查询结果集中(ResultSetMetadata)，列名大小写不变
	 * 
	 * @param sql SQL查询语句
	 * @return 过滤后的SQL查询语句
	 */
	public String filter(String sql) {
		//check whether sql should be filtered, only Select-Clause will be filtered
		if(sql == null)
			return null;
		
		Matcher m = PTN_SELECT_CLAUSE.matcher(sql);
		if(!m.matches()){
			//not a select sql
			return sql;
		}
		
		StringBuffer sb = new StringBuffer();
		String header = m.group(1);
		String fields = m.group(2);
		String remainder = m.group(3);
		sb.append(header);
		
		Matcher matcher = PTN_RESERVE_COL_NAME_CASE.matcher(fields);
		while(matcher.find()){
			String s1 = matcher.group(1);
			String s2 = matcher.group(2);
			String replacement = s1 + "\"" + s2 + "\"";
			matcher.appendReplacement(sb, replacement);
		}
		matcher.appendTail(sb);
		
		sb.append(remainder);
		return sb.toString();
	}
	

	public String buildRawSqlValue(Object param) {

		if(param == null){
			return "NULL";
		}
		
		String sqlv = null;
		
		if(param instanceof Number){
			sqlv = param.toString();
		}else if(param instanceof String){
			sqlv = "'" + param + "'";
		}else if(param instanceof java.sql.Date){
			sqlv = "TO_DATE('" + UncDate.shortDate((Date)param) + "','YYYY-MM-DD')";
		}else if(param instanceof java.sql.Time){
			sqlv = "TO_DATE('" + UncDate.formatDateTime((Date)param, "HH:mm:ss") + "','HH24:MI:SS')";
		}else if(param instanceof java.sql.Timestamp || param instanceof Date){
			sqlv = "TO_DATE('" + UncDate.longTime((Date)param) + "','YYYY-MM-DD HH24:MI:SS')";
		}else{
			sqlv = "'" + param + "'";
		}
		
		return sqlv;
	}

	public String buildRawSqlValue(Object param, int type) {
		String sqlv = null;
		if(param == null){
			return "NULL";
		}
		
		switch(type){
			case Types.NULL :
				sqlv = "NULL";
				break;
			case Types.INTEGER :
			case Types.BIGINT :
			case Types.BIT :
			case Types.DECIMAL :
			case Types.DOUBLE :
			case Types.FLOAT :
			case Types.NUMERIC :
			case Types.REAL :
			case Types.SMALLINT :
			case Types.TINYINT :
				//number
				sqlv = param.toString();
				break;
			case Types.CHAR :
			case Types.VARCHAR :
				sqlv = "'" + param + "'";
				break;
			case Types.DATE :
				sqlv = "TO_DATE('" + UncDate.shortDate((Date)param) + "','YYYY-MM-DD')";
				break;
			case Types.TIME :
				sqlv = "TO_DATE('" + UncDate.formatDateTime((Date)param, "HH:mm:ss") + "','HH24:MI:SS')";
				break;
			case Types.TIMESTAMP :
				sqlv = "TO_DATE('" + UncDate.longTime((Date)param) + "','YYYY-MM-DD HH24-MI-SS')";
				break;
			default:
				sqlv = "'" + param + "'";
		}
		
		return sqlv;
	}

	public static void main(String[] args){
		OracleDialect dialect = new OracleDialect();
		String sql1 = "select a.x as x, b.y as y  from (select a.x as x, b.y as y) a";
		String sql2 = "select a.x as \"x\", b.y as \"y\"  from (select a.x as x, b.y as y) a";
		System.out.println(sql1 + " -> " + dialect.filter(sql1));
		System.out.println(sql2 + " -> " + dialect.filter(sql2));
	}
}
