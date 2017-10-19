package com.tsoft.core.database.jda;

import com.tsoft.core.database.exception.AccessDataException;
import com.tsoft.core.database.exception.JdaDuplicateEntryException;
import com.tsoft.core.database.exception.JdaTooBigColumnLengthException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;


/**
 * Sql 缓存类。使用PreparedStatement时实现缓存并批量更新的操作。
 *
 */
public class SqlCache implements DisposableBean {
	private final Log logger = LogFactory.getLog(SqlCache.class);
	
	private final static int DEFAULT_BATCH_INTERVAL = 300;	//default to 5 minutes
	private JDA jda;
	private int batchSqlNum;
	private long batchInterval;
	private List sqlBatchList;
	private long lastExec;
	private int sqlCount;
	private boolean ordered;
	private final Object LOCK = new Object();
	private java.util.Timer timer;
	
	/**
	 * @param jda
	 * @param batchSqlNum
	 * @param batchInterval
	 */
	public SqlCache(JDA jda, int batchSqlNum, int batchInterval) {
		super();
		this.jda = jda;
		this.batchSqlNum = batchSqlNum;
		this.batchInterval = (batchInterval <= 0 ? DEFAULT_BATCH_INTERVAL : batchInterval) * 1000L;
		this.sqlBatchList = new java.util.ArrayList();
		this.sqlCount = 0;
		this.ordered = true;
		if(batchSqlNum <= 0 && batchInterval <= 0)
			throw new IllegalArgumentException("illegal arguments: either batchSqlNum or batchInterval should be positive.");
		
		this.lastExec = System.currentTimeMillis();
		timer = new java.util.Timer();
		timer.schedule(new SqlCacheTask(this), batchInterval, batchInterval);
	}
	
	/**
	 * whether the sql command is executed in added order
	 * @return the ordered
	 */
	public boolean isOrdered() {
		return ordered;
	}

	/**
	 * @param ordered the ordered to set
	 */
	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	public void addSql(String sql, Object[] params, int[] paramTypes) {
		add(sql, params, paramTypes, SqlBatch.TYPE_SQL);
	}
	
	public void addCall(String procName, Object[] params, int[] paramTypes) {
		add(procName, params, paramTypes, SqlBatch.TYPE_PROC);
	}
	
	private final void add(String sql, Object[] params, int[] paramTypes, int batchType) {
		if(sql == null || sql.length() == 0)
			return;
	
		//get last SqlBatch
		SqlBatch sqlBatch;
		if(ordered) {			
			synchronized(LOCK) {			
				if(this.sqlBatchList.size() == 0){
					sqlBatch = new SqlBatch(jda, sql, batchType);
					this.sqlBatchList.add(sqlBatch);
				} else {
					SqlBatch lastSqlBatch = (SqlBatch)this.sqlBatchList.get(this.sqlBatchList.size() - 1);
					if(lastSqlBatch.type == batchType && sql.equalsIgnoreCase(lastSqlBatch.sql)) {
						sqlBatch = lastSqlBatch;
					} else {
						sqlBatch = new SqlBatch(jda, sql, batchType);
						this.sqlBatchList.add(sqlBatch);
					}				
				}
				
				this.sqlCount++;
			}
		} else {
			//unordered, use a optimized strategy
			sqlBatch = new SqlBatch(jda, sql, batchType);
			synchronized(LOCK) {
				int index;
				if((index = this.sqlBatchList.indexOf(sqlBatch)) > -1) {
					sqlBatch = (SqlBatch)this.sqlBatchList.get(index);
				} else {
					this.sqlBatchList.add(sqlBatch);
				}
				
				this.sqlCount++;
			}
		}
		
		sqlBatch.addBatch(params, paramTypes);
		if(batchSqlNum > 0 && this.sqlCount >= batchSqlNum)
			flush();
	}
	
	public void flush() {
		List sqlBatchListCopy;
		synchronized(LOCK) {
			sqlBatchListCopy = this.sqlBatchList;
			this.sqlBatchList = new java.util.ArrayList();
			this.sqlCount = 0;
		}
		
		this.lastExec = System.currentTimeMillis();
		int flushCount = (sqlBatchListCopy == null ? 0 : sqlBatchListCopy.size());
		if(flushCount > 0) {				
			for(int i = 0; i < flushCount; i++) {
				try {
					SqlBatch sqlBatch = (SqlBatch)sqlBatchListCopy.get(i);
					sqlBatch.executeBatch();
				} catch (JdaDuplicateEntryException e) {
					logger.error("dupplicate entry: " + e.getMessage());
				} catch (JdaTooBigColumnLengthException e) {
					logger.error("Too big length for column: " + e.getMessage());
				} catch (AccessDataException e) {
					logger.error("Access data error: " + e.getMessage());
				}
			}
			
			if(logger.isDebugEnabled())
				logger.debug("flushing sqlCache. Sql Count: " + this.sqlCount);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
		if(logger.isInfoEnabled()) 
			logger.info("destroying sqlCache...");
		
		if(this.timer != null) {
			try {
				this.timer.cancel();
			} catch(Throwable t) {
				logger.error("cannot cancel timer: " + t.getMessage(), t);
			}
		}
		
		flush();
		
		this.sqlBatchList = null;
		this.sqlCount = 0;
	}


	private static class SqlCacheTask extends java.util.TimerTask {
		private SqlCache sqlCache;
		public SqlCacheTask(SqlCache sqlCache) {
			this.sqlCache = sqlCache;
		}
		
		public void run() {
			if(System.currentTimeMillis() - sqlCache.lastExec > sqlCache.batchInterval) {
				sqlCache.flush();
			}
		}
	}
	
	private static class SqlBatch {
		private final static Log logger = LogFactory.getLog(SqlBatch.class);
		
		public final static int TYPE_SQL = 0;
		public final static int TYPE_PROC = 1;
		
		protected JDA jda;
		protected String sql;
		protected List params;
		protected int[] paramTypes;
		protected int type;
		protected int count;
		
		public SqlBatch(JDA jda, String sql) {
			this(jda, sql, TYPE_SQL);
		}
		
		public SqlBatch(JDA jda, String sql, int type) {
			this.jda = jda;
			this.sql = sql;
			this.type = type;
			this.params = new java.util.ArrayList();
			this.count = 0;
		}
		
		public void addBatch(Object[] params, int[] paramTypes) {			
			//check paramTypes
			if(this.paramTypes == null) {
				this.paramTypes = paramTypes;
			} else if(!java.util.Arrays.equals(this.paramTypes, paramTypes)) {
				throw new IllegalArgumentException("paramTypes not match");
			}
			
			this.params.add(params);
			count++;
		}

		public int[] executeBatch() throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException {
			int[] returnVal = null;
			if(count > 0) {
				if(type == TYPE_SQL) {
					if(count > 1) {
						if(logger.isDebugEnabled())
							logger.debug("executeBatch: " + count);
						
						if(paramTypes != null) {
							returnVal = jda.executeBatch(sql, (Object[][])params.toArray(new Object[params.size()][]), paramTypes);
						} else {
							returnVal = jda.executeBatch(sql, (Object[][])params.toArray(new Object[params.size()][]));
						}
					} else {
						if(logger.isDebugEnabled())
							logger.debug("execute");
						
						if(paramTypes != null) {
							int retVal = jda.execute(sql, (Object[])params.get(0), paramTypes, true);
							returnVal = new int[]{retVal};
						} else {
							int retVal = jda.execute(sql, (Object[])params.get(0), true);
							returnVal = new int[]{retVal};
						}
					}
				} else if(type == TYPE_PROC) {
					if(count > 1) {
						if(logger.isDebugEnabled())
							logger.debug("executeCallBatch: " + count);
						if(paramTypes != null) {
							returnVal = jda.executeCallBatch(sql, (Object[][])params.toArray(new Object[params.size()][]), paramTypes);
						} else {
							returnVal = jda.executeCallBatch(sql, (Object[][])params.toArray(new Object[params.size()][]));
						}
					} else {
						if(logger.isDebugEnabled())
							logger.debug("executeCall");
						int retVal = jda.executeCall(sql, (Object[])params.get(0), paramTypes, true);
						returnVal = new int[]{retVal};
					}
				}
				
				//clear params
				this.params = new java.util.ArrayList();
				count = 0;
			}
			
			return returnVal;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((sql == null) ? 0 : sql.hashCode());
			result = PRIME * result + type;
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final SqlBatch other = (SqlBatch) obj;
			if (sql == null) {
				if (other.sql != null)
					return false;
			} else if (!sql.equalsIgnoreCase(other.sql))
				return false;
			if (type != other.type)
				return false;
			return true;
		}
	}
}
