package com.tsoft.core.database.dialect;

/**
 * Dialect 的MySQL实现
 *
 */
public class MySQLDialect extends Dialect {
    private static final int ERR_CODE_DUP_ENTRY = 1062;
    private static final int ERR_CODE_TO_BIG_COLUMN_LENGTH = 1074;

    public String getDialectName() {
        return "MySQL";
    }

    

    @Override
	public String getLimitString(String sql, int page, int pageSize) {
		if( pageSize > 0 )
		{
		    StringBuffer pagingSelect = new StringBuffer(100);
			int intStart = (page - 1)*pageSize;
			pagingSelect.append(sql);
			pagingSelect.append(" limit " + intStart + ", " + pageSize);
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

	public boolean supportsLimit() {
		return true;
	}
}
