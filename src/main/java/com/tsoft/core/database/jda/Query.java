package com.tsoft.core.database.jda;


import com.tsoft.core.database.exception.AccessDataException;
import com.tsoft.core.database.exception.JdaDuplicateEntryException;
import com.tsoft.core.database.exception.JdaTooBigColumnLengthException;

import java.util.List;

/**
 * <BR>提供使用配置文件配置Sql查询的接口，Sql查询的语句存在配置文件中。
 * <BR>并支持带参数变量的Sql语句，可以在程序动态设定这些参数。
 * <BR>参数名使用:开头
 * <BR>e.g. 配置文件如下：
 * <BR>&lt;?xml version="1.0" encoding="GB2312"?&gt;
 *	<BR>&lt;sql-mappings-list&gt;
 * <BR>  &lt;named-query name="Stat.1"&gt;SELECT :user FROM user&lt;/named-query&gt;
 * <BR> &lt;/sql-mappings-list&gt;
 * <BR>
 * <BR>Query query;
 * <BR>query.setParameter("user", "uid,uname");
 * <BR>List rs = query.list();
 * <BR>
 * <BR>上面这段程序将会执行 SELECT uid,uname FROM user 的查询。
 */
public interface Query {
	/**
	 * 给指定的变量赋值
	 * @param name
	 * @param value
	 */
	void setVariable(String name, String value);
    /**
     * 给指定参数赋值。
     * @param name
     * @param value
     */
    void setParameter(String name, Object value);


    /**
     * 查询单个对象
     * @param targetBeanClass
     * @return
     */
    Object findOne(Class targetBeanClass);

    /**
     * 查询带参数单个对象，返回对应的对象类型实体；
     * @param params
     * @param targetBeanClass
     * @return
     */
    Object findOne(Object[] params,Class targetBeanClass);
    /**
     * 执行Sql查询。
     * @return 结果集。
     * 注：结果集的每一行被打包成一个Map加入List，Map使用列名作为Key，列值为value保存在每一行的Map中。
     */
    List list();
    /**
     * 执行Sql查询。
     * @return 结果集。
     * 注：结果集的每一行被打包成一个Map加入List，Map使用列名作为Key，列值为value保存在每一行的Map中。
     */
    List list(boolean autoFlush);
    /**
     * 执行Sql查询。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     * 注：结果集的每一行被打包成一个Map加入List，Map使用列名作为Key，列值为value保存在每一行的Map中。
     */
    List list(Class targetBeanClass);
    /**
     * 执行Sql查询。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集。
     * 注：结果集的每一行被打包成一个Map加入List，Map使用列名作为Key，列值为value保存在每一行的Map中。
     */
    List list(Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行Sql查询。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集。
     * 注：结果集的每一行被打包成一个Map加入List，Map使用列名作为Key，列值为value保存在每一行的Map中。
     */
    List list(Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);    
    /**
     * 执行Sql查询。
     * @param params 需要传递到存储过程中的参数。
     * @return 结果集。
     */
    List list(Object[] params);
    /**
     * 执行Sql查询。
     * @param params 需要传递到存储过程中的参数。
     * @return 结果集。
     */
    List list(Object[] params, boolean autoFlush);    
    /**
     * 执行Sql查询。
     * @param params 需要传递到存储过程中的参数。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List list(Object[] params, Class targetBeanClass);
    /**
     * 执行Sql查询。
     * @param params 需要传递到存储过程中的参数。
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List list(Object[] params, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行Sql查询。
     * @param params 需要传递到存储过程中的参数。
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List list(Object[] params, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    /**
     * 执行Sql查询。
     * @param params 需要传递到存储过程中的参数。
     * @return 结果集。
     */    
    List list(Object[] params, int[] paramTypes);
    /**
     * 执行Sql查询。
     * @param params 需要传递到存储过程中的参数。
     * @return 结果集。
     */    
    List list(Object[] params, int[] paramTypes, boolean autoFlush);    
    /**
     * 执行Sql查询。
     * @param params 需要传递到存储过程中的参数。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */    
    List list(Object[] params, int[] paramTypes, Class targetBeanClass);
    /**
     * 执行Sql查询。
     * @param params 需要传递到存储过程中的参数。
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */    
    List list(Object[] params, int[] paramTypes, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行Sql查询。
     * @param params 需要传递到存储过程中的参数。
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */    
    List list(Object[] params, int[] paramTypes, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List scroll(int page, int pageSize);
    /**
     * 执行Sql查询。（分页）
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List scroll(int page, int pageSize, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List scroll(int page, int pageSize, Class targetBeanClass);
    /**
     * 执行Sql查询。（分页）
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List scroll(int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行Sql查询。（分页）
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List scroll(int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List scroll(Object[] params, int page, int pageSize);
    /**
     * 执行Sql查询。（分页）
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List scroll(Object[] params, int page, int pageSize, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List scroll(Object[] params, int page, int pageSize, Class targetBeanClass);
    /**
     * 执行Sql查询。（分页）
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List scroll(Object[] params, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行Sql查询。（分页）
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List scroll(Object[] params, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List scroll(Object[] params, int[] paramTypes, int page, int pageSize);
    /**
     * 执行Sql查询。（分页）
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List scroll(Object[] params, int[] paramTypes, int page, int pageSize, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List scroll(Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass);
    /**
     * 执行Sql查询。（分页）
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List scroll(Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行Sql查询。（分页）
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List scroll(Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    /**
     * 查询执行sql后的记录总数。等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @return 执行sql后的记录总数。
     */
    int count();
    /**
     * 查询执行sql后的记录总数。等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @return 执行sql后的记录总数。
     */
    int count(boolean autoFlush);
    /**
     * 查询执行sql后的记录总数。等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @param params 需要传递到存储过程中的参数。
     * @return 执行sql后的记录总数。
     */
    int count(Object[] params);
    /**
     * 查询执行sql后的记录总数。等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @param params 需要传递到存储过程中的参数。
     * @return 执行sql后的记录总数。
     */
    int count(Object[] params, boolean autoFlush);
    /**
     * 查询执行sql后的记录总数。等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @param params 需要传递到存储过程中的参数。
     * @return 执行sql后的记录总数。
     */
    int count(Object[] params, int[] paramTypes);
    /**
     * 查询执行sql后的记录总数。等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @param params 需要传递到存储过程中的参数。
     * @return 执行sql后的记录总数。
     */
    int count(Object[] params, int[] paramTypes, boolean autoFlush); 
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @return 字符串值。
     */
    String getString();
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @return 字符串值。
     */
    String getString(boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 字符串值。
     */
    String getString(Object[] params);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 字符串值。
     */
    String getString(Object[] params, int[] paramTypes);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 字符串值。
     */
    String getString(Object[] params, boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 字符串值。
     */
    String getString(Object[] params, int[] paramTypes, boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @return 整型值。
     */
    int getInt();
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @return 整型值。
     */
    int getInt(boolean autoFlush);    
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    int getInt(Object[] params);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    int getInt(Object[] params, int[] paramTypes);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    int getInt(Object[] params, boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    int getInt(Object[] params, int[] paramTypes, boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @return 整型值。
     */
    long getLong();
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @return 整型值。
     */
    long getLong(boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    long getLong(Object[] params);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    long getLong(Object[] params, int[] paramTypes);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    long getLong(Object[] params, boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    long getLong(Object[] params, int[] paramTypes, boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @return 返回值。
     */
    Object getObject();
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @return 返回值。
     */
    Object getObject(boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 返回值。
     */
    Object getObject(Object[] params);
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 返回值。
     */
    Object getObject(Object[] params, int[] paramTypes);
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 返回值。
     */
    Object getObject(Object[] params, boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @return 返回值。
     */
    Object getObject(Object[] params, int[] paramTypes, boolean autoFlush);   
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @param clazz 返回值类型
     * @return
     */
    Object getObject(Class clazz);
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param clazz 返回值类型
     * @return
     */
    Object getObject(Class clazz, boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @param clazz 返回值类型
     * @return
     */    
    Object getObject(Object[] params, Class clazz);
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @param clazz 返回值类型
     * @return
     */    
    Object getObject(Object[] params, int[] paramTypes, Class clazz);   
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @param clazz 返回值类型
     * @return
     */    
    Object getObject(Object[] params, Class clazz, boolean autoFlush);
    /**
     * 取得查询结果。确定查询结果集只有一行一列才能调用此函数。
     * @param params 需要传递到存储过程中的参数。
     * @param clazz 返回值类型
     * @return
     */    
    Object getObject(Object[] params, int[] paramTypes, Class clazz, boolean autoFlush);
    /**
     * 执行Insert,update,delete等语句
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute() throws JdaDuplicateEntryException,JdaTooBigColumnLengthException, AccessDataException;
    /**
     * 执行Insert,update,delete等语句
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(boolean forceNoCache) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException, AccessDataException;
    /**
     * 执行Insert,update,delete等语句
     * @param params 需要传递到存储过程中的参数。
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(Object[] params)throws JdaDuplicateEntryException,JdaTooBigColumnLengthException, AccessDataException;
    /**
     * 执行Insert,update,delete等语句
     * @param params 需要传递到存储过程中的参数。
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(Object[] params, boolean forceNoCache)throws JdaDuplicateEntryException,JdaTooBigColumnLengthException, AccessDataException;      
    /**
     * 执行Insert,update,delete等语句
     * @param params 需要传递到存储过程中的参数。
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(Object[] params, int[] paramTypes)throws JdaDuplicateEntryException,JdaTooBigColumnLengthException, AccessDataException;
    /**
     * 执行Insert,update,delete等语句
     * @param params 需要传递到存储过程中的参数。
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(Object[] params, int[] paramTypes, boolean forceNoCache)throws JdaDuplicateEntryException,JdaTooBigColumnLengthException, AccessDataException;  
    /**
     * 批量执行Sql语句。
     * <BR>e.g. 向User表中批量插入3条记录：
     * <BR>Query query;
     * <BR>String sql = "Insert Into User (id,name,age) values (?,?,?)";
     * <BR>Object[][] params = 
     * <BR>{
     * <BR>&nbsp;&nbsp;{"liaoxj","廖雄杰",new Integer(24)},
     * <BR>&nbsp;&nbsp;{"zhenzh","甄志会",new Integer(23)},
     * <BR>&nbsp;&nbsp;{"guoq","郭潜",new Integer(26)}
     * <BR>};
     * <BR>query.executeBatch(sql, params);
     * @param params 包含参数值的二维数组。数组的长度即为批量执行的Sql语句的个数。
     * @return
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     */
    int[] executeBatch(Object[][] params) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException, AccessDataException;
    
    /**
     * 批量执行Sql语句。
     * <BR>e.g. 向User表中批量插入3条记录：
     * <BR>Query query;
     * <BR>String sql = "Insert Into User (id,name,age) values (?,?,?)";
     * <BR>Object[][] params = 
     * <BR>{
     * <BR>&nbsp;&nbsp;{"liaoxj","廖雄杰",new Integer(24)},
     * <BR>&nbsp;&nbsp;{"zhenzh","甄志会",new Integer(23)},
     * <BR>&nbsp;&nbsp;{"guoq","郭潜",new Integer(26)}
     * <BR>};
     * <BR>query.executeBatch(sql, params);
     * @param params 包含参数值的二维数组。数组的长度即为批量执行的Sql语句的个数。
     * @return
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     */
    int[] executeBatch(Object[][] params, int[] paramTypes) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException, AccessDataException;
}
