package com.tsoft.core.database.jda;

import com.tsoft.core.database.dialect.Dialect;
import com.tsoft.core.database.exception.JdaDuplicateEntryException;
import com.tsoft.core.database.exception.JdaTooBigColumnLengthException;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 继承spring的SQLErrorCodeSQLExceptionTranslator。
 * <BR>处理主键冲突和插入列过大的异常。
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UncSQLErrorCodeSQLExceptionTranslator extends
        SQLErrorCodeSQLExceptionTranslator {
    private final static org.apache.commons.logging.Log Log = LogFactory.getLog(UncSQLErrorCodeSQLExceptionTranslator.class);
    private Dialect dialect;
    
    
    /**
     * @return Returns the dialect.
     */
    public Dialect getDialect() {
        return dialect;
    }
    /**
     * @param dialect The dialect to set.
     */
    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }
    
    /**
     * 
     */
    public UncSQLErrorCodeSQLExceptionTranslator() {
        super();
    }
    
    public UncSQLErrorCodeSQLExceptionTranslator(Dialect dialect){
        super();
        setDialect(dialect);
    }
    
    public UncSQLErrorCodeSQLExceptionTranslator(DataSource dataSource, Dialect dialect){
        super(dataSource);
        setDialect(dialect);
    }
    /* (non-Javadoc)
     * @see org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator#customTranslate(java.lang.String, java.lang.String, java.sql.SQLException)
     */
    protected DataAccessException customTranslate(String task, String sql, SQLException sqlex) {
        if(dialect == null){
            Log.error("未知数据库类型");
            return null;
        }
        
        int errCode = sqlex.getErrorCode();
        if(errCode == dialect.getErrorCodeForDuplicateEntry()){
            return new JdaDuplicateEntryException(task, sqlex);
        }else if(errCode == dialect.getErrorCodeForTooBigColumnLength()){
            return new JdaTooBigColumnLengthException(task, sqlex);
        }
        
        return super.customTranslate(task, sql, sqlex);
    }
}
