package com.tsoft.core.database.dialect;

import com.tsoft.core.database.utils.UncDate;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Dialect of different database.
 *
 */
public abstract class Dialect {
	private final static org.apache.commons.logging.Log Log = LogFactory.getLog(Dialect.class);
    /**
     * 取得当前数据库名称
     * @return
     */
    public abstract String getDialectName();
	
	/**
	 * Add a <tt>LIMIT</tt> clause to the given SQL <tt>SELECT</tt>
	 * @return the modified SQL
	 */
	public String getLimitString(String querySelect, int page, int pageSize) {
		throw new UnsupportedOperationException(this.getDialectName() + "不支持分页查询");
	}
	
	/**
	 * 当主键重复时,JDBC返回的ErrorCode值
	 * @return
	 */
	public int getErrorCodeForDuplicateEntry(){
	    return Integer.MIN_VALUE;
	}
	
	/**
	 * 当插入字段长度过长时,JDBC返回的ErrorCode值
	 * @return
	 */
	public int getErrorCodeForTooBigColumnLength(){
	    return Integer.MIN_VALUE;
	} 
	
	/**
	 * 是否支持返回指定长度的结果集
	 * @return
	 */
	public boolean supportsLimit(){
	    return false;
	}
	
	/**
	 * sql语句是否支持指定返回结果集的起始位置
	 * @return
	 */
	public boolean supportsLimitOffset(){
	    return supportsLimit();
	}
	
	public String buildCountSQL(String sql){
	    return "SELECT COUNT(*) AS count_ FROM ( "  + sql + " ) count_0";
	}
	
	/**
	 * 对SQL查询语句进行必要的过滤
	 * @param sql
	 * @return
	 */
	public String filter(String sql){
		return sql;
	}
	
	/**
	 * 根据不同的数据库生成原生SQL的值。
	 * @param param Java表现的值
	 * @return
	 */
	public String buildRawSqlValue(Object param){
		if(param == null){
			return "NULL";
		}
		
		String sqlv = null;
		
		if(param instanceof Number){
			sqlv = param.toString();
		}else if(param instanceof String){
			sqlv = "'" + param + "'";
		}else if(param instanceof java.sql.Date){
			sqlv = "'" + UncDate.shortDate((Date)param) + "'";
		}else if(param instanceof Time){
			sqlv = "'" + UncDate.formatDateTime((Date)param, "HH:mm:ss") + "'";
		}else if(param instanceof Timestamp || param instanceof Date){
			sqlv = "'" + UncDate.longTime((Date)param) + "'";
		}else{
			sqlv = "'" + param + "'";
		}

		return sqlv;
	}

	/**
	 * 根据不同的数据库生成原生SQL的值。
	 * @param param Java表现的值
	 * @param type SQL类型
	 * @return
	 */
	public String buildRawSqlValue(Object param, int type){
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
				sqlv = "'" + UncDate.shortDate((Date)param) + "'";
				break;
			case Types.TIME :
				sqlv = "'" + UncDate.formatDateTime((Date)param, "HH:mm:ss") + "'";
				break;
			case Types.TIMESTAMP :
				sqlv = "'" + UncDate.longTime((Date)param) + "'";
				break;
			default:
				sqlv = "'" + param + "'";
		}

		return sqlv;
	}

	/**
	 * @return 返回指针类型(Cursor Type)
	 */
	public int getCursorType() {
		throw new UnsupportedOperationException(this.getDialectName() + "未指定cursorType");
	}

	/**
	 * 给PreparedStatement赋参数值。作为基类的Dialect提供默认实现，不同的数据库可能针对部分数据类型需要提供不同的实现。
	 * @param pstmt PreparedStatement实例
	 * @param parameterIndex 参数的index
	 * @param param 参数值
	 * @throws SQLException 当赋值过程中发生java.sql.SQLException时
	 */
	public void setParametersForPS(PreparedStatement pstmt, int parameterIndex, Object param) throws SQLException {
	    if(param == null){
	        //可能会有问题
	        Log.warn("cannot identify parameter type for NULL.");
	        pstmt.setObject(parameterIndex, null);
	    }else{
		    if(param instanceof String){
		        pstmt.setString(parameterIndex, (String)param);
		    }else if(param instanceof Date){
		        if(param instanceof java.sql.Date){
			        pstmt.setDate(parameterIndex, (java.sql.Date)param);
			    }else if(param instanceof Time){
			        pstmt.setTime(parameterIndex, (Time)param);
			    }else if(param instanceof Timestamp){
			        pstmt.setTimestamp(parameterIndex, (Timestamp)param);
			    }else{
			        pstmt.setTimestamp(parameterIndex, new Timestamp(((Date)param).getTime()));
			    }
		    }else if(param instanceof Calendar){
		        pstmt.setTimestamp(parameterIndex, new Timestamp(((Calendar)param).getTimeInMillis()));
		    }else if(param instanceof Integer){
		        pstmt.setInt(parameterIndex, ((Integer)param).intValue());
		    }else if(param instanceof Long){
		        pstmt.setLong(parameterIndex, ((Long)param).longValue());
		    }else if(param instanceof Float){
		        pstmt.setFloat(parameterIndex, ((Float)param).floatValue());
		    }else if(param instanceof Double){
		        pstmt.setDouble(parameterIndex, ((Double)param).doubleValue());
		    }else if(param instanceof BigDecimal){
		        pstmt.setBigDecimal(parameterIndex, (BigDecimal)param);
		    }else if(param instanceof Clob){
		        pstmt.setClob(parameterIndex, (Clob)param);
		    }else{
		        pstmt.setObject(parameterIndex, param);
		    }
	    }
	}

	/**
	 * 给PreparedStatement赋参数值。作为基类的Dialect提供默认实现，不同的数据库可能针对部分数据类型需要提供不同的实现。
	 * @param pstmt PreparedStatement实例
	 * @param parameterIndex 参数的index
	 * @param param 参数值
	 * @param paramType 参数的SQL类型
	 * @throws SQLException 当赋值过程中发生java.sql.SQLException时
	 */
	public void setParametersForPS(PreparedStatement pstmt, int parameterIndex, Object param, int paramType) throws SQLException {
	    if(param == null){
	        pstmt.setNull(parameterIndex, paramType);
	    }else{
		    if (paramType == Types.VARCHAR) {
				pstmt.setString(parameterIndex, param.toString());
			}
			else if (paramType == Types.DATE) {
				if (param instanceof Date) {
					if (param instanceof java.sql.Date) {
						pstmt.setDate(parameterIndex, (java.sql.Date) param);
					}
					else {
						pstmt.setDate(parameterIndex, new java.sql.Date(((Date) param).getTime()));
					}
				}
				else if (param instanceof Calendar) {
					Calendar cal = (Calendar) param;
					pstmt.setDate(parameterIndex, new java.sql.Date(cal.getTimeInMillis()), cal);
				}
				else {
					pstmt.setObject(parameterIndex, param, Types.DATE);
				}
			}
			else if (paramType == Types.TIME) {
				if (param instanceof Date) {
					if (param instanceof Time) {
						pstmt.setTime(parameterIndex, (Time) param);
					}
					else {
						pstmt.setTime(parameterIndex, new Time(((Date) param).getTime()));
					}
				}
				else if (param instanceof Calendar) {
					Calendar cal = (Calendar) param;
					pstmt.setTime(parameterIndex, new Time(cal.getTime().getTime()), cal);
				}
				else {
					pstmt.setObject(parameterIndex, param, Types.TIME);
				}
			}
			else if (paramType == Types.TIMESTAMP) {
				if (param instanceof Date) {
					if (param instanceof Timestamp) {
						pstmt.setTimestamp(parameterIndex, (Timestamp) param);
					}
					else {
						pstmt.setTimestamp(parameterIndex, new Timestamp(((Date) param).getTime()));
					}
				}
				else if (param instanceof Calendar) {
					Calendar cal = (Calendar) param;
					pstmt.setTimestamp(parameterIndex, new Timestamp(cal.getTime().getTime()), cal);
				}
				else {
					pstmt.setObject(parameterIndex, param, Types.TIMESTAMP);
				}
			}
			else {
			    pstmt.setObject(parameterIndex, param, paramType);
			}
	    }
	}
}
