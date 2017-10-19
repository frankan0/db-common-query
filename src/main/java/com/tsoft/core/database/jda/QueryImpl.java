package com.tsoft.core.database.jda;

import com.tsoft.core.database.exception.AccessDataException;
import com.tsoft.core.database.exception.JdaDuplicateEntryException;
import com.tsoft.core.database.exception.JdaTooBigColumnLengthException;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Query的默认实现
 */
public class QueryImpl implements Query {
    private static org.apache.commons.logging.Log Log = LogFactory.getLog(QueryImpl.class.getName());
    
	private final static Pattern PATTERN_PARAM_VAR = Pattern.compile("(?:\\:([a-zA-Z_][a-zA-Z_0-9]*)\\b)|(?:\\$\\{([a-zA-Z_][a-zA-Z_0-9]*)\\})");
	
    private JDA jda;
    private String name;
    private SqlMapping sqlMapping;
    private Map parameters;
    private Map variables;
    
    public QueryImpl(JDA jda, SqlMapping sqlMapping, String name){
        this.jda = jda;
        this.sqlMapping = sqlMapping;
        setName(name);
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
    	if(parameters == null){
    		parameters = new HashMap();
    	}
    	
        parameters.put(name, value);
    }
        
    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#setVariable(java.lang.String, java.lang.String)
	 */
	public void setVariable(String name, String value) {
		if(variables == null){
			variables = new HashMap();
		}
		
		variables.put(name, value);
	}


	public Object findOne(Class targetBeanClass) {
		return this.jda.findOne(this.toString(),targetBeanClass);
	}

	public Object findOne(Object[] params, Class targetBeanClass) {
		return this.jda.findOne(this.toString(),params,targetBeanClass);
	}

	/* (non-Javadoc)
             * @see com.tsoft.core.database.jda.Query#list()
             */
    public List list() {
        return list(true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(boolean)
	 */
	public List list(boolean autoFlush) {
		return jda.find(this.toString(), autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#list(java.lang.Object[])
     */
    public List list(Object[] params) {
        return list(params, true);
    }       

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Object[], boolean)
	 */
	public List list(Object[] params, boolean autoFlush) {
		return jda.find(this.toString(), params, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#list(java.lang.Object[], int[])
     */
    public List list(Object[] params, int[] paramTypes) {
        return list(params, paramTypes, true);
    }
    
    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Object[], int[], boolean)
	 */
	public List list(Object[] params, int[] paramTypes, boolean autoFlush) {
		return jda.find(this.toString(), params, paramTypes, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#scroll(int, int)
     */
    public List scroll(int page, int pageSize) {
        return scroll(page, pageSize, true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(int, int, boolean)
	 */
	public List scroll(int page, int pageSize, boolean autoFlush) {
		return jda.find(this.toString(), page, pageSize, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#scroll(java.lang.Object[], int, int)
     */
    public List scroll(Object[] params, int page, int pageSize) {
        return scroll(params, page, pageSize, true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(java.lang.Object[], int, int, boolean)
	 */
	public List scroll(Object[] params, int page, int pageSize, boolean autoFlush) {
		return jda.find(this.toString(), params, page, pageSize, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#scroll(java.lang.Object[], int[], int, int)
     */
    public List scroll(Object[] params, int[] paramTypes, int page, int pageSize) {
        return scroll(params, paramTypes, page, pageSize, true);
    }
    
    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(java.lang.Object[], int[], int, int, boolean)
	 */
	public List scroll(Object[] params, int[] paramTypes, int page, int pageSize, boolean autoFlush) {
		return jda.find(this.toString(), params, paramTypes, page, pageSize, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Class)
	 */
	public List list(Class targetBeanClass) {
		return list(targetBeanClass, false);
	}	
	
	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Class, boolean)
	 */
	public List list(Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return list(targetBeanClass, forceLowerCaseOnMapping, true);
	}
	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Class, boolean, boolean)
	 */
	public List list(Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		return jda.find(this.toString(), targetBeanClass, forceLowerCaseOnMapping, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Object[], java.lang.Class)
	 */
	public List list(Object[] params, Class targetBeanClass) {
		return list(params, targetBeanClass, false);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Object[], java.lang.Class, boolean)
	 */
	public List list(Object[] params, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return list(params, targetBeanClass, forceLowerCaseOnMapping, true);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Object[], java.lang.Class, boolean, boolean)
	 */
	public List list(Object[] params, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		return jda.find(this.toString(), params, targetBeanClass, forceLowerCaseOnMapping, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Object[], int[], java.lang.Class)
	 */
	public List list(Object[] params, int[] paramTypes, Class targetBeanClass) {
		return list(params, paramTypes, targetBeanClass, false);
	}	

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Object[], int[], java.lang.Class, boolean)
	 */
	public List list(Object[] params, int[] paramTypes, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return list(params, paramTypes, targetBeanClass, forceLowerCaseOnMapping, true);
	}
	
	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#list(java.lang.Object[], int[], java.lang.Class, boolean, boolean)
	 */
	public List list(Object[] params, int[] paramTypes, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		return jda.find(this.toString(), params, paramTypes, targetBeanClass, forceLowerCaseOnMapping, autoFlush);
	}
	
	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(int, int, java.lang.Class)
	 */
	public List scroll(int page, int pageSize, Class targetBeanClass) {
		return scroll(page, pageSize, targetBeanClass, false);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(int, int, java.lang.Class, boolean)
	 */
	public List scroll(int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return scroll(page, pageSize, targetBeanClass, forceLowerCaseOnMapping, true);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(int, int, java.lang.Class, boolean, boolean)
	 */
	public List scroll(int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		return jda.find(this.toString(), page, pageSize, targetBeanClass, forceLowerCaseOnMapping, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(java.lang.Object[], int, int, java.lang.Class)
	 */
	public List scroll(Object[] params, int page, int pageSize, Class targetBeanClass) {
		return scroll(params, page, pageSize, targetBeanClass, false);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(java.lang.Object[], int, int, java.lang.Class, boolean)
	 */
	public List scroll(Object[] params, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return scroll(params, page, pageSize, targetBeanClass, forceLowerCaseOnMapping, true);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(java.lang.Object[], int, int, java.lang.Class, boolean, boolean)
	 */
	public List scroll(Object[] params, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		return jda.find(this.toString(), params, page, pageSize, targetBeanClass, forceLowerCaseOnMapping, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(java.lang.Object[], int[], int, int, java.lang.Class)
	 */
	public List scroll(Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass) {
		return scroll(params, paramTypes, page, pageSize, targetBeanClass, false);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(java.lang.Object[], int[], int, int, java.lang.Class, boolean)
	 */
	public List scroll(Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping) {
		return scroll(params, paramTypes, page, pageSize, targetBeanClass, forceLowerCaseOnMapping, true);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#scroll(java.lang.Object[], int[], int, int, java.lang.Class, boolean, boolean)
	 */
	public List scroll(Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush) {
		return jda.find(this.toString(), params, paramTypes, page, pageSize, targetBeanClass, forceLowerCaseOnMapping, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#count()
     */
    public int count() {
        return count(true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#count(boolean)
	 */
	public int count(boolean autoFlush) {
		return jda.count(this.toString(), autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#count(java.lang.Object[])
     */
    public int count(Object[] params) {
        return count(params, true);
    }

    
    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#count(java.lang.Object[], boolean)
	 */
	public int count(Object[] params, boolean autoFlush) {
		return jda.count(this.toString(), params, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#count(java.lang.Object[], int[])
     */
    public int count(Object[] params, int[] paramTypes) {
        return count(params, paramTypes, true);
    }
    
    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#count(java.lang.Object[], int[], boolean)
	 */
	public int count(Object[] params, int[] paramTypes, boolean autoFlush) {
		return jda.count(this.toString(), params, paramTypes, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#getString()
     */
    public String getString() {
        return getString(true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getString(boolean)
	 */
	public String getString(boolean autoFlush) {
		return jda.getString(this.toString(), autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#getString(java.lang.Object[])
     */
    public String getString(Object[] params) {
        return getString(params, true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getString(java.lang.Object[], boolean)
	 */
	public String getString(Object[] params, boolean autoFlush) {
		return jda.getString(this.toString(), params, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getString(java.lang.Object[], int[], boolean)
	 */
	public String getString(Object[] params, int[] paramTypes, boolean autoFlush) {
		return jda.getString(this.toString(), params, paramTypes, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getString(java.lang.Object[], int[])
	 */
	public String getString(Object[] params, int[] paramTypes) {
		return jda.getString(this.toString(), params, paramTypes);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#getInt()
     */
    public int getInt() {
        return getInt(true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getInt(boolean)
	 */
	public int getInt(boolean autoFlush) {
		return jda.getInt(this.toString(), autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#getInt(java.lang.Object[])
     */
    public int getInt(Object[] params) {
        return getInt(params, true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getInt(java.lang.Object[], boolean)
	 */
	public int getInt(Object[] params, boolean autoFlush) {
		return jda.getInt(this.toString(), params, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getInt(java.lang.Object[], int[], boolean)
	 */
	public int getInt(Object[] params, int[] paramTypes, boolean autoFlush) {
		return jda.getInt(this.toString(), params, paramTypes, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getInt(java.lang.Object[], int[])
	 */
	public int getInt(Object[] params, int[] paramTypes) {
		return jda.getInt(this.toString(), params, paramTypes);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#getLong()
     */
    public long getLong() {
        return getLong(true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getLong(boolean)
	 */
	public long getLong(boolean autoFlush) {
		return jda.getLong(this.toString(), autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#getLong(java.lang.Object[])
     */
    public long getLong(Object[] params) {
        return getLong(params, true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getLong(java.lang.Object[], boolean)
	 */
	public long getLong(Object[] params, boolean autoFlush) {
		return jda.getLong(this.toString(), params, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getLong(java.lang.Object[], int[], boolean)
	 */
	public long getLong(Object[] params, int[] paramTypes, boolean autoFlush) {
		return jda.getLong(this.toString(), params, paramTypes, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getLong(java.lang.Object[], int[])
	 */
	public long getLong(Object[] params, int[] paramTypes) {
		return jda.getLong(this.toString(), params, paramTypes);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#getObject()
     */
    public Object getObject() {
        return getObject(true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getObject(boolean)
	 */
	public Object getObject(boolean autoFlush) {
		return jda.getObject(this.toString(), autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#getObject(java.lang.Object[])
     */
    public Object getObject(Object[] params) {
        return getObject(params, true);
    }

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getObject(java.lang.Object[], boolean)
	 */
	public Object getObject(Object[] params, boolean autoFlush) {
		return jda.getObject(this.toString(), params, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#getObject(java.lang.Class)
     */
    public Object getObject(Class clazz) {
        return getObject(clazz, true);
    }
    
    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getObject(java.lang.Class, boolean)
	 */
	public Object getObject(Class clazz, boolean autoFlush) {
		return jda.getObject(this.toString(), clazz, autoFlush);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#getObject(java.lang.Object[], java.lang.Class)
     */
    public Object getObject(Object[] params, Class clazz) {
        return getObject(params, clazz, true);
    }
	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getObject(java.lang.Object[], java.lang.Class, boolean)
	 */
	public Object getObject(Object[] params, Class clazz, boolean autoFlush) {
		return jda.getObject(this.toString(), params, clazz, autoFlush);
	}

    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getObject(java.lang.Object[], int[], boolean)
	 */
	public Object getObject(Object[] params, int[] paramTypes, boolean autoFlush) {
		return jda.getObject(this.toString(), params, paramTypes, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getObject(java.lang.Object[], int[], java.lang.Class, boolean)
	 */
	public Object getObject(Object[] params, int[] paramTypes, Class clazz, boolean autoFlush) {
		return jda.getObject(this.toString(), params, paramTypes, clazz, autoFlush);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getObject(java.lang.Object[], int[], java.lang.Class)
	 */
	public Object getObject(Object[] params, int[] paramTypes, Class clazz) {
		return jda.getObject(this.toString(), params, paramTypes, clazz);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#getObject(java.lang.Object[], int[])
	 */
	public Object getObject(Object[] params, int[] paramTypes) {
		return jda.getObject(this.toString(), params, paramTypes);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#execute()
     */
    public int execute() throws JdaDuplicateEntryException,
			JdaTooBigColumnLengthException, AccessDataException {
        return jda.execute(this.toString());
    }

    /* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#execute(java.lang.Object[])
     */
    public int execute(Object[] params) throws JdaDuplicateEntryException,
            JdaTooBigColumnLengthException, AccessDataException {
        return jda.execute(this.toString(), params);
    }     
    
    /* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#execute(java.lang.Object[], int[])
     */
    public int execute(Object[] params, int[] paramTypes)
            throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
        return jda.execute(this.toString(), params, paramTypes);
    }
    
    /* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#execute(boolean)
	 */
	public int execute(boolean forceNoCache) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
		return jda.execute(this.toString(), forceNoCache);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#execute(java.lang.Object[], boolean)
	 */
	public int execute(Object[] params, boolean forceNoCache) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
		return jda.execute(this.toString(), params, forceNoCache);
	}

	/* (non-Javadoc)
	 * @see com.tsoft.core.database.jda.Query#execute(java.lang.Object[], int[], boolean)
	 */
	public int execute(Object[] params, int[] paramTypes, boolean forceNoCache) throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
		return jda.execute(this.toString(), params, paramTypes, forceNoCache);
	}

	/* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#executeBatch(java.lang.Object[][])
     */
    public int[] executeBatch(Object[][] params)
            throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
        return jda.executeBatch(this.toString(), params);
    }    
    
    /* (non-Javadoc)
     * @see com.tsoft.core.database.jda.Query#executeBatch(java.lang.Object[][], int[])
     */
    public int[] executeBatch(Object[][] params, int[] paramTypes)
            throws JdaDuplicateEntryException, JdaTooBigColumnLengthException, AccessDataException {
        return jda.executeBatch(this.toString(), params, paramTypes);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String sql = this.getNamedQuery(this.name);
        if(sql == null)
            return null;
        
        //替换参数
        if(parameters == null && variables == null){
        	//no parameters or variables
        	return sql;
        }

  	   	Matcher matcher = PATTERN_PARAM_VAR.matcher(sql);
  	   	StringBuffer strBuf = new StringBuffer();
  	   	while(matcher.find()){
  	   	    String paramName = matcher.group(1);
  	   	    String varName = matcher.group(2);
  	   	    
  	   	    String replace = null;
  	   	    
  	   	    if(paramName != null){
	  	   	   Object value = (parameters == null ? null : parameters.get(paramName));
	  	   	   replace = this.jda.getDbDialect().buildRawSqlValue(value);
  	   	    }else if(varName != null){
  	   	    	replace = (variables == null ? null :(String)variables.get(varName));
  	   	    }
  	  
  	   	    if(replace == null){
  	   	    	replace = "";
  	   	    }
  	   	    
  	   	    matcher.appendReplacement(strBuf,replace);
  	   	    
  	   	}
  	   	matcher.appendTail(strBuf);
        return strBuf.toString();
    }    

    private String getNamedQuery(String name){
        if(sqlMapping == null)
            return null;
            
        String sql = sqlMapping.getNamedQuery(name);
        if(sql == null){
            //没有找到
            Log.error("没有找到名称为 " + name + "的查询!");
        }
        
        return sql;
    }
}
