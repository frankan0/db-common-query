package com.tsoft.core.database.jda;

import com.tsoft.core.database.dialect.Dialect;
import com.tsoft.core.database.exception.AccessDataException;
import com.tsoft.core.database.exception.JdaDuplicateEntryException;
import com.tsoft.core.database.exception.JdaTooBigColumnLengthException;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * Simple JDA implementation with spring support(using JdbcTemplate utils).
 */
public class JdbcDaoImpl extends AbstractJdbcDao implements JDA {
    private final static org.apache.commons.logging.Log Log = LogFactory.getLog(JdbcDaoImpl.class.getName());

	public Object findOne(String sql, Class targetBeanClass) {
		Object result = null ;
		try
		{
			if(showSql) {
				Log.info("findOne :" + sql);
				long start = System.currentTimeMillis();
				result = this.getJdbcTemplate().queryForObject(sql,new BeanPropertyRowMapper(targetBeanClass));
				Log.info("find in " + (System.currentTimeMillis() - start) + " ms");
			} else {
				result = this.getJdbcTemplate().queryForObject(sql,targetBeanClass);
			}
		}catch(DataAccessException dae){
			Log.error("查询数据库出错: " + dae.getMessage(), dae);
		}

		return result;
	}

	public Object findOne(String sql, Object[] params, Class targetBeanClass) {
		Object result = null ;
		try
		{
			if(showSql) {
				Log.info("findOne :" + sql);
				long start = System.currentTimeMillis();
				result = this.getJdbcTemplate().queryForObject(sql,params,new BeanPropertyRowMapper(targetBeanClass));
				Log.info("find in " + (System.currentTimeMillis() - start) + " ms");
			} else {
				result = this.getJdbcTemplate().queryForObject(sql,params,targetBeanClass);
			}
		}catch(DataAccessException dae){
			Log.error("查询数据库出错: " + dae.getMessage(), dae);
		}
		return result;
	}

	/* (non-Javadoc)
         * @see ccom.tsoft.core.database.jda.JDA#find(java.lang.String, java.lang.Object[], boolean)
         */
	public List find(String sql, Object[] params, boolean autoFlush) {
        List result = null;
        
        try
        {
            if(showSql) {
                Log.info("find:" + (showRawSql?this.buildRawSql(sql, params):sql));
            
	            if(this.sqlCache != null && autoFlush)
	            	this.sqlCache.flush();
	            
	            long start = System.currentTimeMillis();
	            result = (List)this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params), new ColumnMapListResultSetExtractor());
	            Log.info("find in " + (System.currentTimeMillis() - start) + " ms");
            } else {
            	if(this.sqlCache != null && autoFlush)
	            	this.sqlCache.flush();
	            
	            result = (List)this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params), new ColumnMapListResultSetExtractor());
            }
        }catch(DataAccessException dae){
            Log.error("查询数据库出错: " + dae.getMessage(), dae);
        }
        
        return result;
	}
	
	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#find(java.lang.String, java.lang.Object[], java.lang.Class, boolean, boolean)
	 */
	public List find(String sql, Object[] params, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		List result = null;
        
        try
        {
            if(showSql) {
                Log.info("find:" + (showRawSql?this.buildRawSql(sql, params):sql));
            
	            //filter sql when necessary
	            if(!forceLowerCaseOnMapping)
	            	sql = this.dbDialect.filter(sql);
	            
	            if(this.sqlCache != null && autoFlush)
	            	this.sqlCache.flush();
	            
	            long start = System.currentTimeMillis();
	            result = (List)this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params), new ObjectListResultSetExtractor(targetBeanClass, forceLowerCaseOnMapping));
	            Log.info("find in " + (System.currentTimeMillis() - start) + " ms");
            } else {
            	 //filter sql when necessary
	            if(!forceLowerCaseOnMapping)
	            	sql = this.dbDialect.filter(sql);
	            
	            if(this.sqlCache != null && autoFlush)
	            	this.sqlCache.flush();
	            
	            result = (List)this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params), new ObjectListResultSetExtractor(targetBeanClass, forceLowerCaseOnMapping));
            }
        }catch(DataAccessException dae){
            Log.error("查询数据库出错: " + dae.getMessage(), dae);
        }
        
        return result;
	}    
    
    /* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#find(java.lang.String, java.lang.Object[], int[], boolean)
	 */
	public List find(String sql, Object[] params, int[] paramTypes, boolean autoFlush) {
		List result = null;
        
        if(params == null || params.length == 0)
            return find(sql, autoFlush);
        
        checkParamsAndTypes(params, paramTypes);
        
        try
        {
            if(showSql) {
                Log.info("find:" + (showRawSql?this.buildRawSql(sql, params, paramTypes):sql));
            
	            if(this.sqlCache != null && autoFlush)
	            	this.sqlCache.flush();
	            
	            long start = System.currentTimeMillis();
	            result = (List)this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes), new ColumnMapListResultSetExtractor());
	            Log.info("find in " + (System.currentTimeMillis() - start) + " ms");
            } else {
            	if(this.sqlCache != null && autoFlush)
	            	this.sqlCache.flush();
	            result = (List)this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes), new ColumnMapListResultSetExtractor());
            }
        }catch(DataAccessException dae){
            Log.error("查询数据库出错: " + dae.getMessage(), dae);
        }
        
        return result;
	}
	
	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#find(java.lang.String, java.lang.Object[], int[], java.lang.Class, boolean, boolean)
	 */
	public List find(String sql, Object[] params, int[] paramTypes, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		List result = null;
        
        if(params == null || params.length == 0)
            return find(sql, targetBeanClass, forceLowerCaseOnMapping, autoFlush);
        
        checkParamsAndTypes(params, paramTypes);
        
        try
        {
            if(showSql) {
                Log.info("find:" + (showRawSql?this.buildRawSql(sql, params, paramTypes):sql));
            
	            //filter sql when necessary
	            if(!forceLowerCaseOnMapping)
	            	sql = this.dbDialect.filter(sql);
	            
	            if(this.sqlCache != null && autoFlush)
	            	this.sqlCache.flush();
	            
	            long start = System.currentTimeMillis();
	            result = (List)this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes), new ObjectListResultSetExtractor(targetBeanClass, forceLowerCaseOnMapping));
	            Log.info("find in " + (System.currentTimeMillis() - start) + " ms");
            } else {
            	//filter sql when necessary
	            if(!forceLowerCaseOnMapping)
	            	sql = this.dbDialect.filter(sql);
	            
	            if(this.sqlCache != null && autoFlush)
	            	this.sqlCache.flush();
	            
	            result = (List)this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes), new ObjectListResultSetExtractor(targetBeanClass, forceLowerCaseOnMapping));
            }
        }catch(DataAccessException dae){
            Log.error("查询数据库出错: " + dae.getMessage(), dae);
        }
        
        return result;
	}
    
    /* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#find(java.lang.String, java.lang.Object[], int, int, boolean)
	 */
	public List find(String sql, Object[] params, int page, int pageSize, boolean autoFlush) {
		try
        {
            if(dbDialect.supportsLimit() && dbDialect.supportsLimitOffset()){
	            String pagingSql = this.dbDialect.getLimitString(sql, page, pageSize);
	            return find(pagingSql, params);
            }else{
                List result;
                
            	if(showSql) {
                    Log.info("find:" + (showRawSql?this.buildRawSql(sql, params):sql));
                
	                if(this.sqlCache != null && autoFlush)
	                	this.sqlCache.flush();
	                
	                long start = System.currentTimeMillis();
	                result = (List)this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params),
	                        new ColumnMapListResultSetExtractor((page - 1)*pageSize + 1, pageSize, useScrollableResultSets));
	                Log.info("find in " + (System.currentTimeMillis() - start) + " ms");
            	} else {
                    if(this.sqlCache != null && autoFlush)
                    	this.sqlCache.flush();
                    
                    result = (List)this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params),
                            new ColumnMapListResultSetExtractor((page - 1)*pageSize + 1, pageSize, useScrollableResultSets));
            	}
                return result;
            }
        }catch(UnsupportedOperationException e){
            Log.error(dbDialect.getDialectName() + "不支持分页查询");
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#find(java.lang.String, java.lang.Object[], int, int, java.lang.Class, boolean, boolean)
	 */
	public List find(String sql, Object[] params, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		try
        {
            if(dbDialect.supportsLimit() && dbDialect.supportsLimitOffset()){
	            String pagingSql = this.dbDialect.getLimitString(sql, page, pageSize);
	            return find(pagingSql, params, targetBeanClass, forceLowerCaseOnMapping);
            }else{
            	List result;
                if(showSql) {
                    Log.info("find:" + (showRawSql?this.buildRawSql(sql, params):sql));
                
	                //filter sql when necessary
	                if(!forceLowerCaseOnMapping)
	                	sql = this.dbDialect.filter(sql);
	                
	                
	                if(this.sqlCache != null && autoFlush)
	                	this.sqlCache.flush();
	                
	                long start = System.currentTimeMillis();
	                result = (List)this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params),
	                        new ObjectListResultSetExtractor(targetBeanClass, (page - 1)*pageSize + 1, pageSize, useScrollableResultSets, forceLowerCaseOnMapping));
                	Log.info("find in " + (System.currentTimeMillis() - start) + " ms");
                } else {
                    //filter sql when necessary
                    if(!forceLowerCaseOnMapping)
                    	sql = this.dbDialect.filter(sql);
                    
                    
                    if(this.sqlCache != null && autoFlush)
                    	this.sqlCache.flush();
                    
                    
                    result = (List)this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params),
                            new ObjectListResultSetExtractor(targetBeanClass, (page - 1)*pageSize + 1, pageSize, useScrollableResultSets, forceLowerCaseOnMapping));
                }
                
                return result;
            }
        }catch(UnsupportedOperationException e){
            Log.error(dbDialect.getDialectName() + "不支持分页查询");
        }
        return null;
	}
    
    /* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#find(java.lang.String, java.lang.Object[], int[], int, int, boolean)
	 */
	public List find(String sql, Object[] params, int[] paramTypes, int page, int pageSize, boolean autoFlush) {
		if(params == null || params.length == 0)
            return find(sql, page, pageSize, autoFlush);
        
        checkParamsAndTypes(params, paramTypes);
        
        try
        {
            if(dbDialect.supportsLimit() && dbDialect.supportsLimitOffset()){
	            String pagingSql = this.dbDialect.getLimitString(sql, page, pageSize);
	            return find(pagingSql, params, paramTypes);
            }else{
            	List result;
                if(showSql) {
                    Log.info("find:" + (showRawSql?this.buildRawSql(sql, params, paramTypes):sql));                
                
	                if(this.sqlCache != null && autoFlush)
	                	this.sqlCache.flush();                
	                
	                long start = System.currentTimeMillis();
	                result = (List)this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes),
	                        new ColumnMapListResultSetExtractor((page - 1)*pageSize + 1, pageSize, useScrollableResultSets));
	                Log.info("find in " + (System.currentTimeMillis() - start) + " ms");
                } else {
                    if(this.sqlCache != null && autoFlush)
                    	this.sqlCache.flush();                
                    
                    result = (List)this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes),
                            new ColumnMapListResultSetExtractor((page - 1)*pageSize + 1, pageSize, useScrollableResultSets));
                }
                return result;
            }
        }catch(UnsupportedOperationException e){
            Log.error(dbDialect.getDialectName() + "不支持分页查询");
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#find(java.lang.String, java.lang.Object[], int[], int, int, java.lang.Class, boolean, boolean)
	 */
	public List find(String sql, Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		 if(params == null || params.length == 0)
	            return find(sql, page, pageSize, targetBeanClass, autoFlush);
	        
	        checkParamsAndTypes(params, paramTypes);
	        
	        try
	        {
	            if(dbDialect.supportsLimit() && dbDialect.supportsLimitOffset()){
		            String pagingSql = this.dbDialect.getLimitString(sql, page, pageSize);
		            return find(pagingSql, params, paramTypes, targetBeanClass, forceLowerCaseOnMapping);
	            }else{
	            	List result;
	                if(showSql) {
	                    Log.info("find:" + (showRawSql?this.buildRawSql(sql, params, paramTypes):sql));
	                
		                //filter sql when necessary
		                if(!forceLowerCaseOnMapping)
		                	sql = this.dbDialect.filter(sql);
		                
		                if(this.sqlCache != null && autoFlush)
		                	this.sqlCache.flush();
		                
		                long start = System.currentTimeMillis();
		                result = (List)this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes),
		                        new ObjectListResultSetExtractor(targetBeanClass, (page - 1)*pageSize + 1, pageSize, useScrollableResultSets, forceLowerCaseOnMapping));
		                Log.info("find in " + (System.currentTimeMillis() - start) + " ms");
	                } else {
		                //filter sql when necessary
		                if(!forceLowerCaseOnMapping)
		                	sql = this.dbDialect.filter(sql);
		                
		                if(this.sqlCache != null && autoFlush)
		                	this.sqlCache.flush();
		                
		                result = (List)this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes),
		                        new ObjectListResultSetExtractor(targetBeanClass, (page - 1)*pageSize + 1, pageSize, useScrollableResultSets, forceLowerCaseOnMapping));
	                }
	                
	                return result;	                
	            }
	        }catch(UnsupportedOperationException e){
	            Log.error(dbDialect.getDialectName() + "不支持分页查询");
	        }
	        return null;
	}

    /* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#getInt(java.lang.String, boolean)
	 */
	public int getInt(String sql, boolean autoFlush) {
		int result = 0;
        
        try
        {            
            if(this.sqlCache != null && autoFlush)
            	this.sqlCache.flush();
            
            result = this.getJdbcTemplate().queryForObject(sql,Integer.class);
        }catch(DataAccessException dae){
            Log.error("查询数据库出错: " + dae.getMessage(), dae);
        }
        
        return result;
	}

    /* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#getInt(java.lang.String, java.lang.Object[], boolean)
	 */
	public int getInt(String sql, Object[] params, boolean autoFlush) {
		if(params == null || params.length == 0)
            return getInt(sql, autoFlush);

        int result = 0;
        
		 try
		 {
            if(showSql)
                Log.info("getInt:" + ((showRawSql?this.buildRawSql(sql, params):sql)));
            
            if(this.sqlCache != null && autoFlush)
            	this.sqlCache.flush();
            
            long start = System.currentTimeMillis();
		    Object val = this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params), new IntColumnResultSetExtractor());
		    if(showSql)
		    	Log.info("getInt in " + (System.currentTimeMillis() - start) + " ms");
		    
		    if(val != null)
		    	result = ((Number)val).intValue();
		 }catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		 }
		 
		 return result;
	}

    /* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#getInt(java.lang.String, java.lang.Object[], int[], boolean)
	 */
	public int getInt(String sql, Object[] params, int[] paramTypes, boolean autoFlush) {
		if(params == null || params.length == 0)
            return getInt(sql, autoFlush);

		checkParamsAndTypes(params, paramTypes);
		
        int result = 0;
        
		 try
		 {
            if(showSql)
                Log.info("getInt:" + ((showRawSql?this.buildRawSql(sql, params):sql)));
            
            if(this.sqlCache != null && autoFlush)
            	this.sqlCache.flush();
            
            long start = System.currentTimeMillis();
		    Object val = this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes), new IntColumnResultSetExtractor());
		    if(showSql)
		    	Log.info("getInt in " + (System.currentTimeMillis() - start) + " ms");
		    
		    if(val != null)
		    	result = ((Number)val).intValue();
		 }catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		 }
		 
		 return result;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#getLong(java.lang.String, boolean)
	 */
	public long getLong(String sql, boolean autoFlush) {
		long result = 0;
        
		 try
		 {
		 	if(showSql)
		 		Log.info("getLong:" + sql);
		 	
		 	if(this.sqlCache != null && autoFlush)
           	this.sqlCache.flush();
	        
		 	long start = System.currentTimeMillis();
		    result = this.getJdbcTemplate().queryForObject(sql,Integer.class);
		    if(showSql)
		    	Log.info("getLong in " + (System.currentTimeMillis() - start) + " ms");
		 }catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		 }
		 
		 return result;
	}

    /* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#getLong(java.lang.String, java.lang.Object[], boolean)
	 */
	public long getLong(String sql, Object[] params, boolean autoFlush) {
		if(params == null || params.length == 0)
            return getLong(sql, autoFlush);

        long result = 0;
        
		 try
		 {
		     if(showSql)
	                Log.info("getLong:" + (showRawSql?this.buildRawSql(sql, params):sql));
            
            if(this.sqlCache != null && autoFlush)
            	this.sqlCache.flush();
            
            long start = System.currentTimeMillis();
            Object val = this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params), new LongColumnResultSetExtractor());
            if(showSql)
		    	Log.info("getLong in " + (System.currentTimeMillis() - start) + " ms");
            
		     if(val != null)
		    	 result = ((Number)val).longValue();
		 }catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		 }
		 
		 return result;
	}
    
    /* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#getLong(java.lang.String, java.lang.Object[], int[], boolean)
	 */
	public long getLong(String sql, Object[] params, int[] paramTypes, boolean autoFlush) {
		if(params == null || params.length == 0)
            return getLong(sql, autoFlush);

		checkParamsAndTypes(params, paramTypes);
		
        long result = 0;
        
		 try
		 {
			 if(showSql)
	        	Log.info("getLong:" + (showRawSql?this.buildRawSql(sql, params):sql));
            
            if(this.sqlCache != null && autoFlush)
            	this.sqlCache.flush();
            
            long start = System.currentTimeMillis();
            Object val = this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params), new LongColumnResultSetExtractor());
            if(showSql)
		    	Log.info("getLong in " + (System.currentTimeMillis() - start) + " ms");
            
		     if(val != null)
		    	 result = ((Number)val).longValue();
		 }catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		 }
		 
		 return result;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.AbstractJdbcDao#getString(java.lang.String, boolean)
	 */
	public String getString(String sql, boolean autoFlush) {
		String result = null;
        
		try
		{
			if(showSql)
				Log.info("getString:" + sql);
	            
			if(this.sqlCache != null && autoFlush)
          	this.sqlCache.flush();
	        
			long start = System.currentTimeMillis();
		    result = (String)this.getJdbcTemplate().query(sql, new StringColumnResultSetExtractor());
		    if(showSql)
		    	Log.info("getString in " + (System.currentTimeMillis() - start) + " ms"); 
		}catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		}
		 
		return result;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.AbstractJdbcDao#getString(java.lang.String, java.lang.Object[], boolean)
	 */
	public String getString(String sql, Object[] params, boolean autoFlush) {
		if(params == null || params.length == 0)
            return getString(sql, autoFlush);

        String result = null;
        
		 try
		 {
		     if(showSql)
	                Log.info("getString:" + (showRawSql?this.buildRawSql(sql, params):sql));
            
            if(this.sqlCache != null && autoFlush)
            	this.sqlCache.flush();

            long start = System.currentTimeMillis();
            result = (String)this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params), new StringColumnResultSetExtractor());
            if(showSql)
		    	Log.info("getString in " + (System.currentTimeMillis() - start) + " ms"); 
		 }catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		 }
		 
		 return result;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.AbstractJdbcDao#getString(java.lang.String, java.lang.Object[], int[], boolean)
	 */
	public String getString(String sql, Object[] params, int[] paramTypes, boolean autoFlush) {
		if(params == null || params.length == 0)
            return getString(sql, autoFlush);

		checkParamsAndTypes(params, paramTypes);
		
        String result = null;
        
		 try
		 {
		     if(showSql)
	                Log.info("getString:" + (showRawSql?this.buildRawSql(sql, params):sql));
            
            if(this.sqlCache != null && autoFlush)
            	this.sqlCache.flush();
            
            long start = System.currentTimeMillis();
            result = (String)this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes), new StringColumnResultSetExtractor());
            if(showSql)
		    	Log.info("getString in " + (System.currentTimeMillis() - start) + " ms"); 
		 }catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		 }
		 
		 return result;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#getObject(java.lang.String, java.lang.Class, boolean)
	 */
	public Object getObject(String sql, Class clazz, boolean autoFlush) {
		Object result = null;
        
		 try
		 {
		     if(showSql)
	                Log.info("getObject:" + sql);
	            
           if(this.sqlCache != null && autoFlush)
           this.sqlCache.flush();
           
           long start = System.currentTimeMillis();
		   result = this.getJdbcTemplate().queryForObject(sql, clazz);
		   if(showSql)
		    	Log.info("getObject in " + (System.currentTimeMillis() - start) + " ms"); 
		 }catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		 }
		 
		 return result;
	}
	
    /* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#getObject(java.lang.String, java.lang.Object[], java.lang.Class, boolean)
	 */
	public Object getObject(String sql, Object[] params, Class clazz, boolean autoFlush) {
		if(params == null || params.length == 0)
            return getObject(sql, autoFlush);
        
        Object result = null;
        
		 try
		 {
		     if(showSql)
	                Log.info("getObject:" + (showRawSql?this.buildRawSql(sql, params):sql));
	            
            if(this.sqlCache != null && autoFlush)
            	this.sqlCache.flush();
            
            long start = System.currentTimeMillis();
            List rs = (List)this.getJdbcTemplate().query(sql, new ArgPreparedStatementSetter(this.dbDialect, params), 
		    		 									new RowMapperResultSetExtractor(new SingleColumnRowMapper(clazz), 1));
            if(showSql)
            	Log.info("getObject in " + (System.currentTimeMillis() - start) + " ms"); 
            result = DataAccessUtils.requiredUniqueResult(rs);
		 }catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		 }
		 
		 return result;
	}


	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#getObject(java.lang.String, java.lang.Object[], int[], java.lang.Class, boolean)
	 */
	public Object getObject(String sql, Object[] params, int[] paramTypes, Class clazz, boolean autoFlush) {
		if(params == null || params.length == 0)
            return getObject(sql, autoFlush);
        
		checkParamsAndTypes(params, paramTypes);
		
        Object result = null;
        
		 try
		 {
		     if(showSql)
	                Log.info("getObject:" + (showRawSql?this.buildRawSql(sql, params):sql));
	            
            if(this.sqlCache != null && autoFlush)
            	this.sqlCache.flush();

            long start = System.currentTimeMillis();
		     List rs = (List)this.getJdbcTemplate().query(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes), 
		    		 									new RowMapperResultSetExtractor(new SingleColumnRowMapper(clazz), 1));
		     if(showSql)
			    	Log.info("getObject in " + (System.currentTimeMillis() - start) + " ms"); 
		     result = DataAccessUtils.requiredUniqueResult(rs);
		 }catch(DataAccessException dae){
		     Log.error("查询数据库出错: " + dae.getMessage(), dae);
		 }
		 
		 return result;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#execute(java.lang.String, boolean)
	 */
	public int execute(String sql, boolean forceNoCache) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
        int result = -1;
        if(forceNoCache || this.sqlCache == null) {
	        try
	        {
	            if(showSql) {
	                Log.info("execute:" + sql);
	                long start = System.currentTimeMillis();
	                result = this.getJdbcTemplate().update(sql);
	                Log.info("executed in " + (System.currentTimeMillis() - start) + " ms");
	            } else {
	            	result = this.getJdbcTemplate().update(sql);
	            }
	        }catch(JdaDuplicateEntryException de){
	            Log.error("执行数据库操作出错: 违反唯一约束条件!");
	            throw de;
	        }catch(JdaTooBigColumnLengthException tbe){
	            Log.error("执行数据库操作出错: 列长度超出数据库允许的最大长度!");
	            throw tbe; 
	        }catch(DataAccessException dae){
	            Log.error("执行数据库操作出错: " + dae.getMessage(), dae);
	            throw new AccessDataException(dae.getMessage(), dae);
	        }
        } else {
        	//add sql to cache
        	this.sqlCache.addSql(sql, null, null);
        }
        return result;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#execute(java.lang.String, java.lang.Object[], boolean)
	 */
	public int execute(String sql, Object[] params, boolean forceNoCache) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
        if(params == null || params.length == 0)
            return execute(sql, forceNoCache);        
        
        int result = -1;
        if(forceNoCache || this.sqlCache == null) {
	        try
	        {
	            if(showSql) {
	                Log.info("execute:" + (showRawSql?this.buildRawSql(sql, params):sql));
	                long start = System.currentTimeMillis();
	                result = this.getJdbcTemplate().update(sql, new ArgPreparedStatementSetter(this.dbDialect, params));
	                Log.info("executed in " + (System.currentTimeMillis() - start) + " ms");
	            } else {
	            	result = this.getJdbcTemplate().update(sql, new ArgPreparedStatementSetter(this.dbDialect, params));
	            }
	        }catch(JdaDuplicateEntryException de){
	            Log.error("执行数据库操作出错: 主键重复!");
	            throw de;
	        }catch(JdaTooBigColumnLengthException tbe){
	            Log.error("执行数据库操作出错: 列长度超出数据库允许的最大长度!");
	            throw tbe; 
	        }catch(DataAccessException dae){
	            Log.error("执行数据库操作出错: " + dae.getMessage(), dae);
	            throw new AccessDataException(dae.getMessage(), dae);
	        }
        } else {
        	//add sql to cache
        	this.sqlCache.addSql(sql, params, null);
        }
        return result;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#execute(java.lang.String, java.lang.Object[], int[], boolean)
	 */
	public int execute(String sql, Object[] params, int[] paramTypes, boolean forceNoCache) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
        if(params == null || params.length == 0)
            return execute(sql, forceNoCache);
        
        int result = -1;
        if(forceNoCache || this.sqlCache == null) {
	        try
	        {
	            if(showSql) {
	                Log.info("execute:" + (showRawSql?this.buildRawSql(sql, params, paramTypes):sql));
	                long start = System.currentTimeMillis();
	                result = this.getJdbcTemplate().update(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes));
	                Log.info("executed in " + (System.currentTimeMillis() - start) + " ms");
	            } else {
	            	result = this.getJdbcTemplate().update(sql, new ArgTypePreparedStatementSetter(this.dbDialect, params, paramTypes));
	            }
	        }catch(JdaDuplicateEntryException de){
	            Log.error("执行数据库操作出错: 主键重复!");
	            throw de;
	        }catch(JdaTooBigColumnLengthException tbe){
	            Log.error("执行数据库操作出错: 列长度超出数据库允许的最大长度!");
	            throw tbe; 
	        }catch(DataAccessException dae){
	            Log.error("执行数据库操作出错: " + dae.getMessage(), dae);
	            throw new AccessDataException(dae.getMessage(), dae);
	        }
        } else {
        	//add sql to cache
        	this.sqlCache.addSql(sql, params, paramTypes);
        }
        
        return result;
	}
	
    /* (non-Javadoc)
     * @see ccom.tsoft.core.database.jda.JDA#executeBatch(java.lang.String, java.lang.Object[][])
     */
    public int[] executeBatch(String sql, final Object[][] params)
            throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
        int[] result = {-1};
        
        if(params == null || params.length == 0){
            return new int[]{this.execute(sql)};
        }
        
        BatchPreparedStatementSetter psSetter =
            new BatchPreparedStatementSetter() {
	            public void setValues(PreparedStatement ps, int i) throws SQLException {
            	   setParametersForPS(ps, params[i]);
	            }
	            public int getBatchSize() {
	                return params.length;
	            }
            };
        
        try
        {
            if(showSql) {
                Log.info("executeBatch:" + sql);
                long start = System.currentTimeMillis();
                result = this.getJdbcTemplate().batchUpdate(sql, psSetter);
                Log.info("executedBatch in " + (System.currentTimeMillis() - start) + " ms");
            } else {
            	result = this.getJdbcTemplate().batchUpdate(sql, psSetter);
            }
        }catch(JdaDuplicateEntryException de){
            Log.error("执行数据库操作出错: 主键重复!");
            throw de;
        }catch(JdaTooBigColumnLengthException tbe){
            Log.error("执行数据库操作出错: 列长度超出数据库允许的最大长度!");
            throw tbe; 
        }catch(DataAccessException dae){
            Log.error("执行数据库操作出错: " + dae.getMessage(), dae);
            throw new AccessDataException(dae.getMessage(), dae);
        }
        return result;
    }
    
	/* (non-Javadoc)
     * @see ccom.tsoft.core.database.jda.JDA#executeBatch(java.lang.String, java.lang.Object[][], int[][])
     */
    public int[] executeBatch(String sql, final Object[][] params, final int[] paramTypes)
            throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
        int[] result = {-1};
        
        if(params == null || params.length == 0){
            return new int[]{this.execute(sql)};
        }
        
        BatchPreparedStatementSetter psSetter =
            new BatchPreparedStatementSetter() {
	            public void setValues(PreparedStatement ps, int i) throws SQLException {
            	   setParametersForPS(ps, params[i], paramTypes);
	            }
	            public int getBatchSize() {
	                return params.length;
	            }
            };
        
        try
        {
            if(showSql) {
                Log.info("executeBatch:" + sql);
                long start = System.currentTimeMillis();
                result = this.getJdbcTemplate().batchUpdate(sql, psSetter);
                Log.info("executedBatch in " + (System.currentTimeMillis() - start) + " ms");
            } else {
            	result = this.getJdbcTemplate().batchUpdate(sql, psSetter);
            }
        }catch(JdaDuplicateEntryException de){
            Log.error("执行数据库操作出错: 主键重复!");
            throw de;
        }catch(JdaTooBigColumnLengthException tbe){
            Log.error("执行数据库操作出错: 列长度超出数据库允许的最大长度!");
            throw tbe; 
        }catch(DataAccessException dae){
            Log.error("执行数据库操作出错: " + dae.getMessage(), dae);
            throw new AccessDataException(dae.getMessage(), dae);
        }
        return result;
    }
	
	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#findCall(java.lang.String, java.lang.Object[], int[], boolean)
	 */
	public List findCall(String procName, final Object[] params, final int[] paramTypes, boolean autoFlush) {
		checkParamsAndTypes(params, paramTypes);
		
		List result = null;
		
    	//construct call string like: {call procName(?, ?)} or {call procName} with no args
    	StringBuffer sb = new StringBuffer("{call ");
    	sb.append(procName);
    	sb.append('(');
    	if(params != null && params.length > 0){
    		for(int i=1;i<params.length;i++){
    			sb.append("?,");
    		}
    		sb.append("?,");
    	}
    	
    	sb.append("?)}");
    	
    	String callString = sb.toString();
        if(showSql)
            Log.info("findCall:" + callString);
        
        if(this.sqlCache != null && autoFlush)
        	this.sqlCache.flush();
        
        try {
        	final long start = System.currentTimeMillis();
	        result = (List)this.getJdbcTemplate().execute(callString, new CallableStatementCallback(){
				/* (non-Javadoc)
				 * @see org.springframework.jdbc.core.CallableStatementCallback#doInCallableStatement(java.sql.CallableStatement)
				 */
				public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					if(paramTypes == null){
						setParametersForPS(cs, params);
					}else{
						setParametersForPS(cs, params, paramTypes);
					}
					
					ResultSet rs = null;
					cs.registerOutParameter((params == null ? 1 : params.length + 1), dbDialect.getCursorType());
					boolean flag = cs.execute();
					if(flag){
						//result
						rs = cs.getResultSet();
					} else {
						rs = (ResultSet)cs.getObject(1);
					}
					
					if(rs != null) {
						ResultSet rsToUse = rs;
						try {
							NativeJdbcExtractor nativeJdbcExtractor = JdbcDaoImpl.this.getJdbcTemplate().getNativeJdbcExtractor();
							if(nativeJdbcExtractor != null) {
								rsToUse = nativeJdbcExtractor.getNativeResultSet(rs);
							}
							ColumnMapListResultSetExtractor rse = new ColumnMapListResultSetExtractor();
							return rse.extractData(rsToUse);
						} finally {
							JdbcUtils.closeResultSet(rs);
						}
					}
					
					return null;
				}
	        	
	        });
            if(this.showSql)
            	Log.info("findCall in " + (System.currentTimeMillis() - start) + " ms");
        } catch(DataAccessException dae) {
        	Log.error("查询数据库出错: " + dae.getMessage(), dae);
        }
        
        return result;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#executeCall(java.lang.String, java.lang.Object[], int[], boolean)
	 */
	public int executeCall(String procName, final Object[] params, final int[] paramTypes, boolean forceNoCache) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
        int result = -1;
        
        checkParamsAndTypes(params, paramTypes);
        
        if(forceNoCache || this.sqlCache == null) {
	        try
	        {
	        	//construct call string like: {call procName(?, ?)} or {call procName} with no args
	        	StringBuffer sb = new StringBuffer("{call ");
	        	sb.append(procName);
	        	
	        	if(params != null && params.length > 0){
	        		sb.append('(').append('?');
	        		for(int i=1;i<params.length;i++){
	        			sb.append(",?");
	        		}
	        		sb.append(')');
	        	}
	        	
	        	sb.append('}');
	        	
	        	String callString = sb.toString();
	            if(showSql)
	                Log.info("executeCall:" + callString);
	            
	            long start = System.currentTimeMillis();
	            Object obj = this.getJdbcTemplate().execute(callString, new CallableStatementCallback(){
					/* (non-Javadoc)
					 * @see org.springframework.jdbc.core.CallableStatementCallback#doInCallableStatement(java.sql.CallableStatement)
					 */
					public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
						if(paramTypes == null){
							setParametersForPS(cs, params);
						}else{
							setParametersForPS(cs, params, paramTypes);
						}
						
						boolean flag = cs.execute();
						if(flag){
							//a ResultSet is returned? Query is not allowed executed via a call
							return new Integer(-2);
						}
						
						return new Integer(cs.getUpdateCount());
					}
	            	
	            });
	            
	            if(showSql)
	            	Log.info("executeCall in " + (System.currentTimeMillis() - start) + " ms");
	            
	            result = ((Integer)obj).intValue();
	            if(result == -2){
	            	// a ResultSet is returned
	            	Log.warn("不支持使用存储过程进行查询：" + callString);
	            }
	        }catch(JdaDuplicateEntryException de){
	            Log.error("执行数据库操作出错: 主键重复!");
	            throw de;
	        }catch(JdaTooBigColumnLengthException tbe){
	            Log.error("执行数据库操作出错: 列长度超出数据库允许的最大长度!");
	            throw tbe; 
	        }catch(DataAccessException dae){
	            Log.error("执行数据库操作出错: " + dae.getMessage(), dae);
	            throw new AccessDataException(dae.getMessage(), dae);
	        }
        } else {
        	//add call to sqlCache
        	this.sqlCache.addCall(procName, params, paramTypes);
        }
        
        return result;
	}

	/* (non-Javadoc)
	 * @see ccom.tsoft.core.database.jda.JDA#executeCallBatch(java.lang.String, java.lang.Object[][], int[])
	 */
	public int[] executeCallBatch(String procName, final Object[][] params, final int[] paramTypes) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
        try
        {
        	//construct call string like: {call procName(?, ?)} or {call procName} with no args
        	StringBuffer sb = new StringBuffer("{call ");
        	sb.append(procName);
        	
        	if(params != null && params[0].length > 0){
        		sb.append('(').append('?');
        		for(int i=1;i<params[0].length;i++){
        			sb.append(",?");
        		}
        		sb.append(')');
        	}
        	
        	sb.append('}');
        	
        	String callString = sb.toString();
            if(showSql)
                Log.info("executeCallBatch:" + callString);
            
            long start = System.currentTimeMillis();
            int[] result = (int[])this.getJdbcTemplate().execute(callString, new CallableStatementCallback(){
				/* (non-Javadoc)
				 * @see org.springframework.jdbc.core.CallableStatementCallback#doInCallableStatement(java.sql.CallableStatement)
				 */
				public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					int batchSize = params.length;
					if (JdbcUtils.supportsBatchUpdates(cs.getConnection())) {
						for (int i = 0; i < batchSize; i++) {
							setParametersForPS(cs, params[i], paramTypes);
							cs.addBatch();
						}
						return cs.executeBatch();
					}
					else {
						int[] rowsAffectedArray = new int[batchSize];
						for (int i = 0; i < batchSize; i++) {
							setParametersForPS(cs, params[i], paramTypes);
							rowsAffectedArray[i] = cs.executeUpdate();
						}
						
						return rowsAffectedArray;
					}
				}
            	
            });
            
            if(showSql)
            	Log.info("executeCallBatch in " + (System.currentTimeMillis() - start) + " ms");
            
            return result;
        }catch(JdaDuplicateEntryException de){
            Log.error("执行数据库操作出错: 主键重复!");
            throw de;
        }catch(JdaTooBigColumnLengthException tbe){
            Log.error("执行数据库操作出错: 列长度超出数据库允许的最大长度!");
            throw tbe; 
        }catch(DataAccessException dae){
            Log.error("执行数据库操作出错: " + dae.getMessage(), dae);
            throw new AccessDataException(dae.getMessage(), dae);
        }
	}
    
    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.support.JdbcDaoSupport#initDao()
     */
    protected void initDao() throws Exception {
        super.initDao();
        if(this.getDataSource() != null)
        	this.getJdbcTemplate().setExceptionTranslator(new UncSQLErrorCodeSQLExceptionTranslator(this.getDataSource(), dbDialect));
        else
        	this.getJdbcTemplate().setExceptionTranslator(new UncSQLErrorCodeSQLExceptionTranslator(dbDialect));
    }
    
    static class ArgPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {
    	private Dialect dialect;
    	private final Object[] args;

    	/**
    	 * Create a new ArgTypePreparedStatementSetter for the given arguments.
    	 * @param dialect the arguments to set
    	 * @param args the corresponding SQL types of the arguments
    	 */
    	public ArgPreparedStatementSetter(Dialect dialect, Object[] args) {
    		this.dialect = dialect;
    		this.args = args;
    	}


    	public void setValues(PreparedStatement ps) throws SQLException {
    		if (this.args != null) {
    			for (int i = 0; i < this.args.length; i++) {
    				this.dialect.setParametersForPS(ps, i + 1, args[i]);
    			}
    		}
    	}

    	public void cleanupParameters() {
    		StatementCreatorUtils.cleanupParameters(this.args);
    	}
    }
    
    static class ArgTypePreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {
    	private Dialect dialect;
    	private final Object[] args;
    	private final int[] argTypes;

    	/**
    	 * Create a new ArgTypePreparedStatementSetter for the given arguments.
    	 * @param args the arguments to set
    	 * @param argTypes the corresponding SQL types of the arguments
    	 */
    	public ArgTypePreparedStatementSetter(Dialect dialect, Object[] args, int[] argTypes) {
    		if ((args != null && argTypes == null) || (args == null && argTypes != null) ||
    				(args != null && args.length != argTypes.length)) {
    			throw new InvalidDataAccessApiUsageException("args and argTypes parameters must match");
    		}
    		this.dialect = dialect;
    		this.args = args;
    		this.argTypes = argTypes;
    	}


    	public void setValues(PreparedStatement ps) throws SQLException {
    		if (this.args != null) {
    			if(argTypes == null) {
	    			for (int i = 0; i < this.args.length; i++) {
	    				this.dialect.setParametersForPS(ps, i + 1, args[i]);
	    			}
    			} else {
    				for (int i = 0; i < this.args.length; i++) {
	    				this.dialect.setParametersForPS(ps, i + 1, args[i], argTypes[i]);
	    			}
    			}
    		}
    	}

    	public void cleanupParameters() {
    		StatementCreatorUtils.cleanupParameters(this.args);
    	}
    }
    
    static class IntColumnResultSetExtractor implements ResultSetExtractor {

		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
		 */
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			if(rs.next()) {
				return new Integer(rs.getInt(1));
			}
			return null;
		}	
    }
    
    static class LongColumnResultSetExtractor implements ResultSetExtractor {

		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
		 */
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			if(rs.next()) {
				return new Long(rs.getLong(1));
			}
			return null;
		}	
    }
    
    static class StringColumnResultSetExtractor implements ResultSetExtractor {

		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
		 */
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			if(rs.next()) {
				return rs.getString(1);
			}
			return null;
		}	
    }
}
