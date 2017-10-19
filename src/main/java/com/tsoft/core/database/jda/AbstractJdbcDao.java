package com.tsoft.core.database.jda;


import com.tsoft.core.database.dialect.Dialect;
import com.tsoft.core.database.exception.AccessDataException;
import com.tsoft.core.database.exception.JdaDuplicateEntryException;
import com.tsoft.core.database.exception.JdaTooBigColumnLengthException;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Abstract jdbc dao with basic functions.
 */
public abstract class AbstractJdbcDao extends JdbcDaoSupport implements JDA, DisposableBean {
	private final static org.apache.commons.logging.Log Log = LogFactory.getLog(AbstractJdbcDao.class);

    private final static Pattern PATTERN_PS = Pattern.compile("\\?");
    private final static Pattern PATTERN_ESC = Pattern.compile("[\\$\\\\]");
    protected String dialect;
    protected String sqlMappingFile;
    protected String[] sqlMappingFiles;
    protected Dialect dbDialect;
    protected SqlMapping sqlMapping;
    protected boolean useScrollableResultSets;
    protected boolean showSql;
    protected boolean showRawSql;
    protected int batchSqlNum;
    protected int batchInterval;
    protected SqlCache sqlCache;
    
    /**
     * @return Returns the dialect.
     */
    public Dialect getDbDialect() {
        return dbDialect;
    }
    
    public String getDialect(){
    	return this.dialect;
    }
    
    /**
     * @param dialect The dialect to set.
     */
    public void setDialect(String dialect) {
        this.dialect = dialect;
    } 
    
    /**
     * @return Returns the useScrollableResultSets.
     */
    public boolean useScrollableResultSets() {
        return useScrollableResultSets;
    }
    /**
     * @param useScrollableResultSets The useScrollableResultSets to set.
     */
    public void setUseScrollableResultSets(boolean useScrollableResultSets) {
        this.useScrollableResultSets = useScrollableResultSets;
    }

    /**
     * @param sqlMappingFile The sqlMappingFile to set.
     */
    public void setSqlMappingFile(String sqlMappingFile) {
        this.sqlMappingFile = sqlMappingFile;
    }
    
    /**
     * @param sqlMappingFiles The sqlMappingFile to set.
     */
    public void setSqlMappingFiles(String[] sqlMappingFiles) {
        this.sqlMappingFiles = sqlMappingFiles;
    }
    
    /**
     * @return Returns the showSql.
     */
    public boolean showSql() {
        return showSql;
    }
    
    /**
     * @param showSql The showSql to set.
     */
    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }
    
    
    /**
	 * @return the useRawSql
	 */
	public boolean showRawSql() {
		return showRawSql;
	}
	
	/**
	 * @param showRawSql the useRawSql to set
	 */
	public void setShowRawSql(boolean showRawSql) {
		this.showRawSql = showRawSql;
	}
	
	/**
	 * @param batchInterval the batchInterval to set
	 */
	public void setBatchInterval(int batchInterval) {
		this.batchInterval = batchInterval;
	}

	/**
	 * @param batchSqlNum the batchSqlNum to set
	 */
	public void setBatchSqlNum(int batchSqlNum) {
		this.batchSqlNum = batchSqlNum;
	}

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.support.JdbcDaoSupport#initDao()
     */
    protected void initDao() throws Exception {
        //设置SqlExceptionTranslator
        if(dialect == null || dialect.length() == 0)
            throw new Exception("未指定Dialect");
         
        //初始化Dialect
        try{
            Class dialectClass = Class.forName(dialect);
            dbDialect = (Dialect)dialectClass.newInstance();
            
            //Set sqlMapping
            if(this.sqlMappingFiles != null && this.sqlMappingFiles.length > 0) {
            	this.sqlMapping = new SqlMapping();
            	sqlMapping.configureFromClassPath(this.sqlMappingFiles);
            } else if(this.sqlMappingFile != null && this.sqlMappingFile.length() > 0) {
                this.sqlMapping = new SqlMapping();
            	sqlMapping.configureFromClassPath(this.sqlMappingFile);
            }
            
            //init sql cache
            if(this.batchInterval > 0 || this.batchSqlNum > 0) {
            	this.sqlCache = new SqlCache(this, this.batchSqlNum, this.batchInterval);
            }
        }catch(Exception ex){
            Log.error(ex.getMessage(), ex);
        }

    }
    
    /* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
		if(this.sqlMapping != null) {
			this.sqlMapping.destroy();
		}
		
		if(sqlCache != null) {
			sqlCache.destroy();
		}
	}

	
	public Connection connection() throws SQLException {
		return this.getDataSource().getConnection();
	}
	
	public void closeConnection(Connection con) {
		try {
			if(con != null)
				con.close();
		} catch (SQLException e) {
			Log.error("cannot close the connection: " + e.getMessage(), e);
		}
	}
	
	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getQueryString(java.lang.String)
     */
    public String getQueryString(String name) {
        if(sqlMapping == null)
            return null;
            
        String sql = sqlMapping.getNamedQuery(name);
        if(sql == null){
            //没有找到
            Log.error("没有找到名称为 " + name + "的查询!");
        }
        
        return sql;
    }
    
    /* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#createQuery(java.lang.String)
     */
    public Query createQuery(String name) {
        return new QueryImpl(this, this.sqlMapping, name);
    }
    

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#find(java.lang.String)
     */
    public List find(String sql) {
        return find(sql, true);
    }

    /* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, boolean)
	 */
	public List find(String sql, boolean autoFlush) {
		return find(sql, (Object[])null, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Class)
	 */
	public List find(String sql, Class targetBeanClass) {
        return find(sql, targetBeanClass, false);
	}
	
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Class, boolean)
	 */
	public List find(String sql, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return find(sql, (Object[])null, targetBeanClass, forceLowerCaseOnMapping, true);
	}
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Class, boolean, boolean)
	 */
	public List find(String sql, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		return find(sql, (Object[])null, targetBeanClass, forceLowerCaseOnMapping, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[])
     */
    public List find(String sql, Object[] params) {
        return find(sql, params, true);
    }
    
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], java.lang.Class)
	 */
	public List find(String sql, Object[] params, Class targetBeanClass) {
        return find(sql, params, targetBeanClass, false);
	}	
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], java.lang.Class, boolean)
	 */
	public List find(String sql, Object[] params, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return find(sql, params, targetBeanClass, forceLowerCaseOnMapping, true);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], int[], java.lang.Class)
	 */
	public List find(String sql, Object[] params, int[] paramTypes, Class targetBeanClass) {
        return find(sql, params, paramTypes, targetBeanClass, false);
	}
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], int[], java.lang.Class, boolean)
	 */
	public List find(String sql, Object[] params, int[] paramTypes, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return find(sql, params, paramTypes, targetBeanClass, forceLowerCaseOnMapping, true);
	}
	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, int, int)
     */
    public List find(String sql, int page, int pageSize) {
        return find(sql, page, pageSize, true);
    }
    
    
    /* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, int, int, boolean)
	 */
	public List find(String sql, int page, int pageSize, boolean autoFlush) {
		return find(sql, (Object[])null, page, pageSize, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, int, int, java.lang.Class)
	 */
	public List find(String sql, int page, int pageSize, Class targetBeanClass) {
		return find(sql, page, pageSize, targetBeanClass, false);
	}	
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, int, int, java.lang.Class, boolean)
	 */
	public List find(String sql, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return find(sql, page, pageSize, targetBeanClass, forceLowerCaseOnMapping, true);
	}
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, int, int, java.lang.Class, boolean, boolean)
	 */
	public List find(String sql, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		return find(sql, (Object[])null, page, pageSize, targetBeanClass, forceLowerCaseOnMapping, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], int, int)
     */
    public List find(String sql, Object[] params, int page, int pageSize) {       
        return find(sql, params, page, pageSize,  true);
    }

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], int, int, java.lang.Class)
	 */
	public List find(String sql, Object[] params, int page, int pageSize, Class targetBeanClass) {
        return find(sql, params, page, pageSize, targetBeanClass, false);
	}
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], int, int, java.lang.Class, boolean)
	 */
	public List find(String sql, Object[] params, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return find(sql, params, page, pageSize, targetBeanClass, forceLowerCaseOnMapping, true);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], int[], int, int, java.lang.Class)
	 */
	public List find(String sql, Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass) {
		return find(sql, params, paramTypes, page, pageSize, targetBeanClass, false);
	}
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], int[], int, int, java.lang.Class, boolean)
	 */
	public List find(String sql, Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return find(sql, params, paramTypes, page, pageSize, targetBeanClass, forceLowerCaseOnMapping, true);
	}
	
	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], int[], int, int)
     */
    public List find(String sql, Object[] params, int[] paramTypes, int page,
            int pageSize) {
        return find(sql, params, paramTypes, page, pageSize, true);        
    }
    
	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#find(java.lang.String, java.lang.Object[], int[])
     */
    public List find(String sql, Object[] params, int[] paramTypes) {
        return find(sql, params, paramTypes, true);
    }
    
	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#count(java.lang.String)
     */
    public int count(String sql) {
        return count(sql, true);
    }

    /* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#count(java.lang.String, boolean)
	 */
	public int count(String sql, boolean autoFlush) {
		return getInt(dbDialect.buildCountSQL(sql), autoFlush);
	}

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#count(java.lang.String, java.lang.Object[])
     */
    public int count(String sql, Object[] params) {
    	return count(sql, params, true);
    }

    
    /* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#count(java.lang.String, java.lang.Object[], boolean)
	 */
	public int count(String sql, Object[] params, boolean autoFlush) {
		return getInt(dbDialect.buildCountSQL(sql), params, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#count(java.lang.String, java.lang.Object[], int[])
     */
    public int count(String sql, Object[] params, int[] paramTypes) {
        return count(sql, params, paramTypes, true);
    }

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#count(java.lang.String, java.lang.Object[], int[], boolean)
	 */
	public int count(String sql, Object[] params, int[] paramTypes, boolean autoFlush) {
		return getInt(dbDialect.buildCountSQL(sql), params, paramTypes, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getString(java.lang.String)
     */
    public String getString(String sql) {
        return getString(sql, true);
    }

    /* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getString(java.lang.String, boolean)
	 */
	public String getString(String sql, boolean autoFlush) {
		return (String)getObject(sql, String.class, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getString(java.lang.String, java.lang.Object[])
     */
    public String getString(String sql, Object[] params) {
        return getString(sql, params, true);
    }

    /* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getString(java.lang.String, java.lang.Object[], boolean)
	 */
	public String getString(String sql, Object[] params, boolean autoFlush) {
		return (String)getObject(sql, params, String.class, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getString(java.lang.String, java.lang.Object[], int[])
	 */
	public String getString(String sql, Object[] params, int[] paramTypes) {
		return getString(sql, params, paramTypes, true);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getString(java.lang.String, java.lang.Object[], int[], boolean)
	 */
	public String getString(String sql, Object[] params, int[] paramTypes, boolean autoFlush) {
		return (String)getObject(sql, params, paramTypes, String.class, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getInt(java.lang.String)
     */
    public int getInt(String sql) {
        return getInt(sql, true);
    }

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getInt(java.lang.String, java.lang.Object[])
     */
    public int getInt(String sql, Object[] params) {        
        return getInt(sql, params, true);
    }

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getInt(java.lang.String, java.lang.Object[], int[])
	 */
	public int getInt(String sql, Object[] params, int[] paramTypes) {
		return getInt(sql, params, paramTypes, true);
	}


	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getLong(java.lang.String)
     */
    public long getLong(String sql) {
        return getLong(sql, true);
    }

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getLong(java.lang.String, java.lang.Object[])
     */
    public long getLong(String sql, Object[] params) {
        return getLong(sql, params, true);
    }

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getLong(java.lang.String, java.lang.Object[], int[])
	 */
	public long getLong(String sql, Object[] params, int[] paramTypes) {
		return getLong(sql, params, paramTypes);
	}
	
	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getObject(java.lang.String)
     */
    public Object getObject(String sql) {
        return getObject(sql, true);
    }
    
    /* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getObject(java.lang.String, boolean)
	 */
	public Object getObject(String sql, boolean autoFlush) {
		return getObject(sql, Object.class, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getObject(java.lang.String, java.lang.Object[])
     */
    public Object getObject(String sql, Object[] params) {
        return getObject(sql, params, true);
    }
    
    /* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getObject(java.lang.String, java.lang.Object[], boolean)
	 */
	public Object getObject(String sql, Object[] params, boolean autoFlush) {
		return getObject(sql, params, Object.class, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getObject(java.lang.String, java.lang.Class)
     */
    public Object getObject(String sql, Class clazz) {
        return getObject(sql, clazz, true);
    }
    

	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#getObject(java.lang.String, java.lang.Object[], java.lang.Class)
     */
    public Object getObject(String sql, Object[] params, Class clazz) {
        return getObject(sql, params, clazz, true);
    }

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getObject(java.lang.String, java.lang.Object[], int[], java.lang.Class)
	 */
	public Object getObject(String sql, Object[] params, int[] paramTypes, Class clazz) {
		return getObject(sql, params, paramTypes, clazz, true);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getObject(java.lang.String, java.lang.Object[], int[])
	 */
	public Object getObject(String sql, Object[] params, int[] paramTypes) {
		return getObject(sql, params, paramTypes, true);
	}
	
    /* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#getObject(java.lang.String, java.lang.Object[], int[], boolean)
	 */
	public Object getObject(String sql, Object[] params, int[] paramTypes, boolean autoFlush) {
		return getObject(sql, params, paramTypes, Object.class, autoFlush);
	}
	
	/* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#execute(java.lang.String)
     */
    public int execute(String sql) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException, AccessDataException {
    	return execute(sql, this.sqlCache == null);
    }

    /* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#execute(java.lang.String, java.lang.Object[])
     */
    public int execute(String sql, Object[] params) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException, AccessDataException {
    	return execute(sql, params, this.sqlCache == null);
    }
    
    
    /* (non-Javadoc)
     * @see com.xiangshang360.database.jda.JDA#execute(java.lang.String, java.lang.Object[], int[])
     */
    public int execute(String sql, Object[] params, int[] paramTypes)
            throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException{
    	return execute(sql, params, paramTypes, this.sqlCache == null);
    }
    
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#findCall(java.lang.String)
	 */
	public List findCall(String procName) {
		return findCall(procName, true);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#findCall(java.lang.String, boolean)
	 */
	public List findCall(String procName, boolean autoFlush) {
		return findCall(procName, null, null, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#findCall(java.lang.String, java.lang.Object[])
	 */
	public List findCall(String procName, Object[] params) {
		return findCall(procName, params, true);
	}
	
    /* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#findCall(java.lang.String, java.lang.Object[], boolean)
	 */
	public List findCall(String procName, Object[] params, boolean autoFlush) {
		return findCall(procName, params, null, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#findCall(java.lang.String, java.lang.Object[], int[])
	 */
	public List findCall(String procName, final Object[] params, final int[] paramTypes) {
		return findCall(procName, params, paramTypes, true);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#executeCall(java.lang.String, java.lang.Object[], int[])
	 */
	public int executeCall(String procName, final Object[] params, final int[] paramTypes) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
		return executeCall(procName, params, paramTypes, this.sqlCache == null);
	}
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#executeCall(java.lang.String, java.lang.Object[])
	 */
	public int executeCall(String procName, Object[] params) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
		return executeCall(procName, params, null);
	}
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#executeCall(java.lang.String)
	 */
	public int executeCall(String procName) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
		return executeCall(procName, null, null);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#executeCall(java.lang.String, boolean)
	 */
	public int executeCall(String procName, boolean forceNoCache) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
		return executeCall(procName, null, null, forceNoCache);
	}

	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#executeCall(java.lang.String, java.lang.Object[], boolean)
	 */
	public int executeCall(String procName, Object[] params, boolean forceNoCache) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
		return executeCall(procName, null, forceNoCache);
	}
	
	/* (non-Javadoc)
	 * @see com.xiangshang360.database.jda.JDA#executeCallBatch(java.lang.String, java.lang.Object[][])
	 */
	public int[] executeCallBatch(String procName, Object[][] params) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
		return executeCallBatch(procName, params, null);
	}
    
	protected void setParametersForPS(PreparedStatement pstmt, Object[] params){
        try{
			if(params != null){
				//设置参数
				for(int i=0;i<params.length;i++){
					this.dbDialect.setParametersForPS(pstmt, i + 1, params[i]);
				}
			}
        }catch(SQLException se){
            Log.error("给存储过程参数赋值时出错！", se);
        }
    }
    
	protected void setParametersForPS(PreparedStatement pstmt, Object[] params, int[] paramTypes){
        try{
			if(params != null){
				if(paramTypes == null) {
					setParametersForPS(pstmt, params);
					return;
				}
				
				//设置参数
				for(int i=0;i<params.length;i++){			    
					this.dbDialect.setParametersForPS(pstmt, i + 1, params[i], paramTypes[i]);
				}
			}
        }catch(SQLException se){
            Log.error("给存储过程参数赋值时出错！", se);
        }
    }
    
	/**
	 * ResultSetExtractor implementation that returns an ArrayList of HashMaps.
	 */
	protected static class ColumnMapListResultSetExtractor implements ResultSetExtractor {

	    private int firstRow;
	    private int maxRows;
	    private boolean isResultSetsScrollable;
	    
	    public ColumnMapListResultSetExtractor(){}
	    
        /**
         * @param firstRow
         * @param maxRows
         * @param isResultSetsScrollable
         */
        public ColumnMapListResultSetExtractor(int firstRow, int maxRows,
                boolean isResultSetsScrollable) {
            this.firstRow = firstRow;
            this.maxRows = maxRows;
            this.isResultSetsScrollable = isResultSetsScrollable;
        }
        
		public Object extractData(ResultSet rs) throws SQLException {
		    if(rs == null)
		        return null;
		    
		    advance(rs, firstRow, isResultSetsScrollable);
		    
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			List listOfRows = new ArrayList();
			int rows = 0;
			while (rs.next()) {
			    //使用LinkedHashMap,可以保留顺序
				Map mapOfColValues = new IgnoreCaseLinkedHashMap(numberOfColumns);
				for (int i = 1; i <= numberOfColumns; i++) {
					String colName = rsmd.getColumnLabel(i);
					
					Object value = getValue(rs, rsmd, i);
					mapOfColValues.put(colName, value);
				}
				listOfRows.add(mapOfColValues);
			    rows++;
				if(rows == maxRows)
				    break;
			}
			
			return listOfRows;
		}
		
		private void advance(ResultSet rs, int firstRow, boolean isResultSetsScrollable) throws SQLException{
			if ( firstRow > 0 ) {
				if ( isResultSetsScrollable ) {
					// we can go straight to the first required row
					rs.absolute(firstRow - 1);
				}
				else {
					// we need to step through the rows one row at a time (slow)
					for ( int m=1; m<firstRow; m++ ) rs.next();
				}
			}   
		}
	}
	
	/*
	 * 忽略key的大小写 
	 * @author Fantasy
	 */
	public static class IgnoreCaseLinkedHashMap extends LinkedHashMap{
		private static final long serialVersionUID = -6636196974843180391L;
		private Set keysSet;	    
	    
        /**
         * 
         */
        public IgnoreCaseLinkedHashMap() {
            super();
            keysSet = new LinkedHashSet();
        }
        /**
         * @param initialCapacity
         */
        public IgnoreCaseLinkedHashMap(int initialCapacity) {
            super(initialCapacity);
            keysSet = new LinkedHashSet(initialCapacity);
        }
	        /* (non-Javadoc)
         * @see java.util.Map#clear()
         */
        public void clear() {
            super.clear();
            keysSet.clear();
        }
        
        /* (non-Javadoc)
         * @see java.util.Map#get(java.lang.Object)
         */
        public Object get(Object key) {
            if(key instanceof String){
                //若为字符串型，忽略大小写
                return super.get(((String)key).toUpperCase());
            }else{
                return super.get(key);
            }
        }
        
        /* (non-Javadoc)
         * @see java.util.Map#containsKey(java.lang.Object)
         */
        public boolean containsKey(Object key) {
            if(key instanceof String){
                //若为字符串型，忽略大小写
                return super.containsKey(((String)key).toUpperCase());
            }else{
                return super.containsKey(key);
            }
        }
        /* (non-Javadoc)
         * @see java.util.Map#keySet()
         */
        public Set keySet() {
            return keysSet;
        }
        /* (non-Javadoc)
         * @see java.util.Map#put(java.lang.Object, java.lang.Object)
         */
        public Object put(Object key, Object value) {
            keysSet.add(key);
            
            if(key instanceof String){
                //若为字符串型，忽略大小写                
                return super.put(((String)key).toUpperCase(), value);
            }else{
                return super.put(key, value);
            }            
        }
        
        /* (non-Javadoc)
         * @see java.util.Map#putAll(java.util.Map)
         */
        public void putAll(Map m) {
            if(m == null)
                return;
            
            Iterator entries = m.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                put(entry.getKey(), entry.getValue());
            }
        }
	}
	
    
	/**
	 * ResultSetExtractor implementation that returns an ArrayList of HashMaps.
	 */
	protected static class ObjectListResultSetExtractor implements ResultSetExtractor {
		private Class targetClass;
		private boolean forceLowerCaseOnMapping;
	    private int firstRow;
	    private int maxRows;
	    private boolean isResultSetsScrollable;
	    
	    public ObjectListResultSetExtractor(Class targetClass){
	    	this.targetClass = targetClass;
	    }
	    
	    public ObjectListResultSetExtractor(Class targetClass, boolean forceLowerCaseOnMapping){
	    	this.targetClass = targetClass;
	    	this.forceLowerCaseOnMapping = forceLowerCaseOnMapping;
	    }
	    
        /**
         * @param firstRow
         * @param maxRows
         * @param isResultSetsScrollable
         */
        public ObjectListResultSetExtractor(Class targetClass,
        		int firstRow, int maxRows,
                boolean isResultSetsScrollable) {
        	this.targetClass = targetClass;
            this.firstRow = firstRow;
            this.maxRows = maxRows;
            this.isResultSetsScrollable = isResultSetsScrollable;
        }
        
        public ObjectListResultSetExtractor(Class targetClass,
        		int firstRow, int maxRows,
                boolean isResultSetsScrollable, boolean forceLowerCaseOnMapping) {
        	this.targetClass = targetClass;
            this.firstRow = firstRow;
            this.maxRows = maxRows;
            this.isResultSetsScrollable = isResultSetsScrollable;
            this.forceLowerCaseOnMapping = forceLowerCaseOnMapping;
        }
        
		public Object extractData(ResultSet rs) throws SQLException {
		    if(rs == null)
		        return null;
		    
		    advance(rs, firstRow, isResultSetsScrollable);
		    
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			List listOfRows = new ArrayList();
			int rows = 0;
			
			try{
				while (rs.next()) {
				    //create a bean instance to hold the result
					Object bean = targetClass.newInstance();
					//wrap the bean instance into a BeanMap to set properties via cglib
					BeanMap bmap = BeanMap.create(bean);
					for (int i = 1; i <= numberOfColumns; i++) {
						//set properties					
						String name = rsmd.getColumnLabel(i);
						if(this.forceLowerCaseOnMapping){
							name = name.toLowerCase();
						}
						
						Class type = bmap.getPropertyType(name);
						Object value = getValue(rs, rsmd, i, type);
						if(value != null) {
							// null value will cause NPE for BeanMap
							bmap.put(name, value);
						}
					}
					listOfRows.add(bean);
				    rows++;
					if(rows == maxRows)
					    break;
				}
			}catch(InstantiationException ie){
				throw new SQLException("InstanitiationException when create an instance of target class: " + targetClass.getName() + ": " + ie.getMessage());
			}catch(IllegalAccessException iae){
				throw new SQLException("IllegalAccessException when create an instance of target class: " + targetClass.getName() + ": " + iae.getMessage());
			}
			
			return listOfRows;
		}
		
		private void advance(ResultSet rs, int firstRow, boolean isResultSetsScrollable) throws SQLException{
			if ( firstRow > 0 ) {
				if ( isResultSetsScrollable ) {
					// we can go straight to the first required row
					rs.absolute(firstRow - 1);
				}
				else {
					// we need to step through the rows one row at a time (slow)
					for ( int m=1; m<firstRow; m++ ) rs.next();
				}
			}   
		}
	}
	
	protected final static void checkParamsAndTypes(Object[] params, int[] types){
		int pl = (params == null?0:params.length);
		int tl = (types == null?0:types.length);
		
		if(pl != tl)
			throw new IllegalArgumentException("数组参数param和paramTypes不匹配");
	}
	
	protected static Object getValue(ResultSet rs, ResultSetMetaData rsmd, int column, Class type){
		if(type == null){
			return getValue(rs, rsmd, column);
		}
		
		Object value = null;
		try{
			Object objectValue = rs.getObject(column);
			if(objectValue == null) {
				// fix the bug which will returns 0 if the value is actually null for primary types
				return null;
			}
			
			if(type == String.class){
				value = rs.getString(column);
			}else if(type == int.class || type == Integer.class){
				value = new Integer(rs.getInt(column));
			}else if(type == long.class || type == Long.class){
				value = new Long(rs.getLong(column));
			}else if(type == float.class || type == Float.class){
				value = new Float(rs.getFloat(column));
			}else if(type == double.class || type == Double.class){
				value = new Double(rs.getDouble(column));
			}else if(type == BigInteger.class){
				BigDecimal bval = rs.getBigDecimal(column);
				if(bval != null){
					value = bval.toBigInteger();
				}
			}else if(type == BigDecimal.class){
				value = rs.getBigDecimal(column);
			}else if(type == Number.class){
				value = rs.getBigDecimal(column);
			}else if(type == java.util.Date.class){
				value = rs.getTimestamp(column);
			}else if(type == java.sql.Date.class){
				value = rs.getDate(column);
			}else if(type == java.sql.Timestamp.class){
				value = rs.getTimestamp(column);
			}else if(type == java.sql.Time.class){
				value = rs.getTime(column);
			}else if(type == boolean.class || type == Boolean.class){
				value = new Boolean(rs.getBoolean(column));
			}else if(type == java.sql.Clob.class){
				value = rs.getClob(column);
			}else if(type == java.sql.Blob.class){
				value = rs.getBlob(column);
			}else{
				//getValue automatically
				value = getValue(rs, rsmd, column);
			}
		}catch(SQLException se){
			Log.error("encounter sql exception when getValue from column " + column);
		}
		
		return value;
	}
	
	protected static Object getValue(ResultSet rs, ResultSetMetaData rsmd, int column){
		Object value = null;
		try{
			Object objectValue = rs.getObject(column);
			if(objectValue == null) {
				// fix the bug which will returns 0 if the value is actually null for primary types
				return null;
			}
			
			int type = rsmd.getColumnType(column);
			switch (type) {
				case Types.CHAR:
				case Types.VARCHAR:
					value = rs.getString(column);
					break;
				case Types.TINYINT:
				case Types.SMALLINT:
				case Types.INTEGER:
				case Types.BIT:
					value = new Integer(rs.getInt(column));
					break;
				case Types.NUMERIC:
					value = rs.getBigDecimal(column);
					break;
				case Types.REAL:
					value = new Double(rs.getDouble(column));
					break;
				case Types.DECIMAL:
					value = rs.getBigDecimal(column);
					break;
				case Types.DOUBLE:
					value = new Double(rs.getDouble(column));
					break;
				case Types.FLOAT:
					value = new Float(rs.getFloat(column));
					break;
				case Types.BIGINT:
					value = new Long(rs.getLong(column));
					break;
				case Types.DATE:
					/*
					value = rs.getDate(column);
					break;
					*/
				case Types.TIMESTAMP:
					value = rs.getTimestamp(column);
					break;
				case Types.TIME:
					value = rs.getTime(column);
					break;
				case Types.BLOB:
					value = rs.getBlob(column);
					break;
				case Types.CLOB:
					value = rs.getClob(column);
					break;				
				case Types.BOOLEAN:
					value = new Boolean(rs.getBoolean(column));
					break;
				case Types.NULL:
					break;
				case Types.ARRAY:
					value = rs.getArray(column);
					break;
				case Types.BINARY:
					value = rs.getBinaryStream(column);
					break;
				case Types.REF:
					value = rs.getRef(column);
					break;
				default:
					value = rs.getObject(column);				
			}
		}catch(SQLException se){
			Log.error("encounter sql exception when getValue from column " + column);
		}
		
		return value;
	}

	protected String buildRawSql(String sql, Object[] params){
		return this.buildRawSql(sql, params, null);
	}
	
	protected String buildRawSql(String sql, Object[] params, int[] types){
		if(sql == null){
			return null;
		}
		
		if(params == null || params.length == 0){
			return sql;
		}
		
		String rawSql = null;
		Matcher matcher = PATTERN_PS.matcher(sql);
		int index = 0;
		
		try {
			StringBuffer sbuf = new StringBuffer();
			while(matcher.find()){
				if(index >= params.length){
					throw new IllegalArgumentException("length of params do not match the number of params in sql: " + sql);
				}
				
				if(types != null && index >= types.length){
					throw new IllegalArgumentException("length of types do not match the number of params in sql: " + sql);
				}
				
				String rawSqlValue = null;
				if(types != null){
					rawSqlValue = this.dbDialect.buildRawSqlValue(params[index], types[index]);
				}else{
					rawSqlValue = this.dbDialect.buildRawSqlValue(params[index]);
				}
				
				if(rawSqlValue == null) {
					matcher.appendReplacement(sbuf, "NULL");
				} else {
					matcher.appendReplacement(sbuf, escapeReplacement(rawSqlValue));
				}
				index++;
			}
			
			matcher.appendTail(sbuf);
			rawSql = sbuf.toString();
		} catch(Throwable t) {
			Log.error("failed to buildRawSql for " + sql);
			rawSql = "failed to build raw sql, original: [" + sql + "]";
		}
		
		return rawSql;
	}
	
	private static String escapeReplacement(String str) {
		if(str == null || str.length() == 0)
			return str;
		
		Matcher m = PATTERN_ESC.matcher(str);
		StringBuffer sbuf = new StringBuffer();
		while(m.find()) {
			String found = m.group();
			if(found.charAt(0) == '$') {
				m.appendReplacement(sbuf, "\\\\\\$");
			} else {
				m.appendReplacement(sbuf, "\\\\\\\\");
			}
		}
		m.appendTail(sbuf);
		return sbuf.toString();
	}
}
