package com.tsoft.core.database.jda;


import com.tsoft.core.database.dialect.Dialect;
import com.tsoft.core.database.exception.AccessDataException;
import com.tsoft.core.database.exception.JdaDuplicateEntryException;
import com.tsoft.core.database.exception.JdaTooBigColumnLengthException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


/**
 * <BR>JDBC Data Access 接口类.<BR>
 * <BR>直接调用底层的JDBC执行数据库操作。
 * <BR>对于Select查询，默认返回List类型的结果集。
 * <BR>结果集的每一行即为一个使用列名/列值作为键值对的Map。
 * <BR>例如，执行sql语句：
 * <BR>Select u.id as id, u.name as name From User u
 * <BR>假设查询得到的结果集三条数据：
 * <BR>	id&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name
 * <BR>-----------------------
 * <BR>	liaoxj&nbsp;&nbsp;&nbsp;&nbsp;廖雄杰
 * <BR>	guoq&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;郭潜
 * <BR>	zhenzh&nbsp;&nbsp;&nbsp;&nbsp;甄志会
 * <BR>-----------------------	
 * <BR>下面这段程序将会输出这个结果：
 * <BR>
 * <BR>JDA jda;
 * <BR>String sql = "Select u.id as id, u.name as name From User u";
 * <BR>List rs = jda.find(sql);
 * <BR>
 * <BR>System.out.println("id				name");
 * <BR>System.out.println("-----------------------");
 * <BR>if(rs != null){
 * <BR>&nbsp;&nbsp;Iterator itr = rs.iterator();
 * <BR>&nbsp;&nbsp;while(itr.hasNext()){
 * <BR>&nbsp;&nbsp;&nbsp;&nbsp;Map row = (Map)itr.next();
 * <BR>&nbsp;&nbsp;&nbsp;&nbsp;String id = (String)row.get("id");
 * <BR>&nbsp;&nbsp;&nbsp;&nbsp;String name = (String)row.get("name");
 * <BR>&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(id + &quot;&nbsp;&nbsp;&nbsp;&nbsp;&quot; + "name");
 * <BR>&nbsp;&nbsp;}
 * <BR>}
 * <BR>System.out.println("-----------------------");
 * <BR>
 * <BR>该类支持类似于Hibernate中Named Query的命名查询。
 * <BR>命名查询可配置文件中配置，定义下面的命名查询文件(sql-mapping.xml):
 * <BR>	&lt;?xml version="1.0" encoding="GB2312"?&gt;;
 *	<BR>&lt;sql-mappings-list&gt;
 * <BR> &nbsp;&nbsp;&lt;named-query name="SelectUser"&gt;&lt;![CDATA[
 *		&nbsp;&nbsp;Select u.id as id, u.name as name From User u
 *	&nbsp;&nbsp;]]&gt;&lt;/named-query&gt;	
 * <BR> &nbsp;&nbsp;&gt;named-query name="SelectGroup"&gt;&lt;![CDATA[
 *		&nbsp;&nbsp;Select g.id as id, g.name as name From Group g
 *		&nbsp;&nbsp;]]>&lt;/named-query&gt;
 *	&lt;/sql-mappings-list&gt;
 * <BR> 
 * <BR>上面的那段小程序与下面这种方式是等效的：
 * <BR>JDA jda;
 * <BR>List rs = jda.findByNamedQuery("SelectUser");
 * <BR>......
 * <BR>
 * <BR>这样做的好处是可以集中管理Sql语句，提高系统的可扩展性，
 * <BR>当将来需要对Sql语句进行优化或者更换数据库时，只需要修改上面的配置文件就可以了。
 * 
 */
public interface JDA {
	Dialect getDbDialect();
	
	Connection connection() throws SQLException;
	
	void closeConnection(Connection connection);


	Object findOne(String sql,Class targetBeanClass);

	Object findOne(String sql,Object[] params ,Class targetBeanClass);

	
    /**
     * 执行Sql查询。
     * @param sql Sql语句。
     * @return 结果集。
     * <BR><B>注：结果集的每一行被打包成一个Map加入List，Map使用列名作为Key，列值为value保存在每一行的Map中。列名忽略大小写。</B>
     * <BR>例如，执行sql语句：
     * <BR>Select u.id as id, u.name as name From User u
     * <BR>假设查询得到的结果集三条数据：
     * <BR>	id&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name
     * <BR>-----------------------
     * <BR>	liaoxj&nbsp;&nbsp;&nbsp;&nbsp;廖雄杰
     * <BR>	guoq&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;郭潜
     * <BR>	zhenzh&nbsp;&nbsp;&nbsp;&nbsp;甄志会
     * <BR>-----------------------	
     * <BR>下面这段程序将会输出这个结果：
     * <BR>
     * <BR>JDA jda;
     * <BR>String sql = "Select u.id as id, u.name as name From User u";
     * <BR>List rs = jda.find(sql);
     * <BR>
     * <BR>System.out.println("id				name");
     * <BR>System.out.println("-----------------------");
     * <BR>if(rs != null){
     * <BR>		Iterator itr = rs.iterator();
     * <BR>		while(itr.hasNext()){
     * <BR>			Map row = (Map)itr.next();
     * <BR>			String id = (String)row.get("id");
     * <BR>			String name = (String)row.get("name");
     * <BR>			System.out.println(id + "			" + "name");
     * <BR>		}
     * <BR>}
     * <BR>System.out.println("-----------------------");
     */
    List find(String sql);
    /**
     * 执行Sql查询。
     * @param sql Sql语句。
     * @return 结果集。
     * <BR><B>注：结果集的每一行被打包成一个Map加入List，Map使用列名作为Key，列值为value保存在每一行的Map中。列名忽略大小写。</B>
     * <BR>例如，执行sql语句：
     * <BR>Select u.id as id, u.name as name From User u
     * <BR>假设查询得到的结果集三条数据：
     * <BR>	id&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name
     * <BR>-----------------------
     * <BR>	liaoxj&nbsp;&nbsp;&nbsp;&nbsp;廖雄杰
     * <BR>	guoq&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;郭潜
     * <BR>	zhenzh&nbsp;&nbsp;&nbsp;&nbsp;甄志会
     * <BR>-----------------------	
     * <BR>下面这段程序将会输出这个结果：
     * <BR>
     * <BR>JDA jda;
     * <BR>String sql = "Select u.id as id, u.name as name From User u";
     * <BR>List rs = jda.find(sql);
     * <BR>
     * <BR>System.out.println("id				name");
     * <BR>System.out.println("-----------------------");
     * <BR>if(rs != null){
     * <BR>		Iterator itr = rs.iterator();
     * <BR>		while(itr.hasNext()){
     * <BR>			Map row = (Map)itr.next();
     * <BR>			String id = (String)row.get("id");
     * <BR>			String name = (String)row.get("name");
     * <BR>			System.out.println(id + "			" + "name");
     * <BR>		}
     * <BR>}
     * <BR>System.out.println("-----------------------");
     */
    List find(String sql, boolean autoFlush);
    /**
     * 执行Sql查询。
     * @param sql Sql语句。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     * <BR><B>结果集的每一行将根据列名映射成一个targetBeanClass类的实例。列名和Bean的属性名必须一致（区分大小写）。
     * <BR>由于结果集中列名的大小写与数据库有关系，建议书写sql查询语句时尽量为每一列指定别名。
     * <BR>如果所有Bean属性名均为小写，可将forceLowerCaseOnMapping参数指定为true，JDA在映射时将自动把列名转成小写再与Bean属性进行映射
     * </B>
     * <BR>e.g.:
     * <BR>------- User.java ---------------
     * <BR>public class User{
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;private String id;
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;private String name;
     * <BR>
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;public String getId(){
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return this.id;
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <BR>
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;public void setId(String id){
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.id = id;
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <BR>}
     * <BR>----------------------------------
     * <BR>
     * <BR>执行sql语句：
     * <BR>Select u.id as id, u.name as name From User u
     * <BR>
     * <BR>假设查询得到的结果集三条数据：
     * <BR>	id&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name
     * <BR>-----------------------
     * <BR>	liaoxj&nbsp;&nbsp;&nbsp;&nbsp;廖雄杰
     * <BR>	guoq&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;郭潜
     * <BR>	zhenzh&nbsp;&nbsp;&nbsp;&nbsp;甄志会
     * <BR>-----------------------	
     * <BR>下面这段程序将会输出这个结果：
     * <BR>
     * <BR>JDA jda;
     * <BR>String sql = "Select u.id as id, u.name as name From User u";
     * <BR>List rs = jda.find(sql, User.class);
     * <BR>
     * <BR>System.out.println("id				name");
     * <BR>System.out.println("-----------------------");
     * <BR>if(rs != null){
     * <BR>		Iterator itr = rs.iterator();
     * <BR>		while(itr.hasNext()){
     * <BR>			User user = (User)itr.next();
     * <BR>			String id = user.getId();
     * <BR>			String name = user.getName();
     * <BR>			System.out.println(id + "			" + "name");
     * <BR>		}
     * <BR>}
     * <BR>System.out.println("-----------------------");
     */
    List find(String sql, Class targetBeanClass);
    /**
     * 
     * @param sql sql查询语句
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集
     */
    List find(String sql, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 
     * @param sql sql查询语句
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集
     */
    List find(String sql, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    /**
     * 执行Sql查询。
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @return 结果集。
     */
    List find(String sql, Object[] params);
    /**
     * 执行Sql查询。
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @return 结果集。
     */
    List find(String sql, Object[] params, boolean autoFlush);
    /**
     * 执行sql查询
     * @param sql sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @param targetBeanClass 目标Bean Class
     * @return
     */
    List find(String sql, Object[] params, Class targetBeanClass);
    /**
     * 执行sql查询
     * @param sql sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return
     */
    List find(String sql, Object[] params, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行sql查询
     * @param sql sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return
     */
    List find(String sql, Object[] params, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    /**
     * 执行Sql查询。
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @return 结果集。
     */
    List find(String sql, Object[] params, int[] paramTypes);
    /**
     * 执行Sql查询。
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @return 结果集。
     */
    List find(String sql, Object[] params, int[] paramTypes, boolean autoFlush);
    /**
     * 执行Sql查询。
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List find(String sql, Object[] params, int[] paramTypes, Class targetBeanClass);
    /**
     * 执行Sql查询。
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集。
     */
    List find(String sql, Object[] params, int[] paramTypes, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行Sql查询。
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集。
     */
    List find(String sql, Object[] params, int[] paramTypes, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List find(String sql, int page, int pageSize);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List find(String sql, int page, int pageSize, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List find(String sql, int page, int pageSize, Class targetBeanClass);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集。
     */
    List find(String sql, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集。
     */
    List find(String sql, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List find(String sql, Object[] params, int page, int pageSize);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List find(String sql, Object[] params, int page, int pageSize, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List find(String sql, Object[] params, int page, int pageSize, Class targetBeanClass);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集。
     */
    List find(String sql, Object[] params, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集。
     */
    List find(String sql, Object[] params, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List find(String sql, Object[] params, int[] paramTypes, int page, int pageSize);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @return 结果集。
     */
    List find(String sql, Object[] params, int[] paramTypes, int page, int pageSize, boolean autoFlush);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @return 结果集。
     */
    List find(String sql, Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集。
     */
    List find(String sql, Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping);
    /**
     * 执行Sql查询。（分页）
     * @param sql sql语句
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @param page 当前页码。 从1开始。
     * @param pageSize 页大小。当pageSize <= 0 时，不分页。
     * @param targetBeanClass 目标Bean Class
     * @param forceLowerCaseOnMapping 当结果集的列与targetBean的属性映射时，是否将列名强制转换成小写字符进行映射
     * @return 结果集。
     */
    List find(String sql, Object[] params, int[] paramTypes, int page, int pageSize, Class targetBeanClass, boolean forceLowerCaseOnMapping, boolean autoFlush);
    /**
     * 查询执行sql后的记录总数。<BR>等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @param sql Select查询语句（不含Count）。
     * @return 执行sql后的记录总数。
     */
    int count(String sql);
    /**
     * 查询执行sql后的记录总数。<BR>等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @param sql Select查询语句（不含Count）。
     * @return 执行sql后的记录总数。
     */
    int count(String sql, boolean autoFlush);
    /**
     * 查询执行sql后的记录总数。<BR>等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @param sql Select查询语句（不含Count）。
     * @param params 需要传递到存储过程中的参数。
     * @return 执行sql后的记录总数。
     */
    int count(String sql, Object[] params);
    /**
     * 查询执行sql后的记录总数。<BR>等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @param sql Select查询语句（不含Count）。
     * @param params 需要传递到存储过程中的参数。
     * @return 执行sql后的记录总数。
     */
    int count(String sql, Object[] params, boolean autoFlush);
    /**
     * 查询执行sql后的记录总数。<BR>等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @param sql Select查询语句（不含Count）。
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @return 执行sql后的记录总数。
     */
    int count(String sql, Object[] params, int[] paramTypes);
    /**
     * 查询执行sql后的记录总数。<BR>等价于执行select count(*) from (<sql>), <sql>为原来得select语句。
     * @param sql Select查询语句（不含Count）。
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @return 执行sql后的记录总数。
     */
    int count(String sql, Object[] params, int[] paramTypes, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @param sql Sql查询语句
     * @return 字符串值。
     */
    String getString(String sql);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @param sql Sql查询语句
     * @return 字符串值。
     */
    String getString(String sql, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 字符串值。
     */
    String getString(String sql, Object[] params);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 字符串值。
     */
    String getString(String sql, Object[] params, int[] paramTypes);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 字符串值。
     */
    String getString(String sql, Object[] params, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为字符串类型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 字符串值。
     */
    String getString(String sql, Object[] params, int[] paramTypes, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @return 整型值。
     */
    int getInt(String sql);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @return 整型值。
     */
    int getInt(String sql, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    int getInt(String sql, Object[] params);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    int getInt(String sql, Object[] params, int[] paramTypes);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    int getInt(String sql, Object[] params, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    int getInt(String sql, Object[] params, int[] paramTypes, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @return 整型值。
     */
    long getLong(String sql);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @return 整型值。
     */
    long getLong(String sql, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    long getLong(String sql, Object[] params);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    long getLong(String sql, Object[] params, int[] paramTypes);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    long getLong(String sql, Object[] params, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列并且返回值为整型才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 整型值。
     */
    long getLong(String sql, Object[] params, int[] paramTypes, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @return 返回值。
     */
    Object getObject(String sql);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @return 返回值。
     */
    Object getObject(String sql, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 返回值。
     */
    Object getObject(String sql, Object[] params);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 返回值。
     */
    Object getObject(String sql, Object[] params, int[] paramTypes);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 返回值。
     */
    Object getObject(String sql, Object[] params, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @return 返回值。
     */
    Object getObject(String sql, Object[] params, int[] paramTypes, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param clazz 返回值类型
     * @return
     */
    Object getObject(String sql, Class clazz);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param clazz 返回值类型
     * @return
     */
    Object getObject(String sql, Class clazz, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @param clazz 返回值类型
     * @return
     */    
    Object getObject(String sql, Object[] params, Class clazz);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @param clazz 返回值类型
     * @return
     */    
    Object getObject(String sql, Object[] params, int[] paramTypes, Class clazz);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @param clazz 返回值类型
     * @return
     */    
    Object getObject(String sql, Object[] params, Class clazz, boolean autoFlush);
    /**
     * 取得查询结果。<BR>确定查询结果集只有一行一列才能调用此函数。
     * @param sql Sql查询语句
     * @param params 需要传递到存储过程中的参数。
     * @param clazz 返回值类型
     * @return
     */    
    Object getObject(String sql, Object[] params, int[] paramTypes, Class clazz, boolean autoFlush);   
    /**
     * 执行Insert,update,delete等语句
     * @param sql sql语句。
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(String sql) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 执行Insert,update,delete等语句
     * @param sql sql语句。
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(String sql, boolean forceNoCache) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 执行Insert,update,delete等语句
     * @param sql sql语句。
     * @param params 需要传递到存储过程中的参数。
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(String sql, Object[] params) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException, JdaDuplicateEntryException;
    /**
     * 执行Insert,update,delete等语句
     * @param sql sql语句。
     * @param params 需要传递到存储过程中的参数。
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(String sql, Object[] params, boolean forceNoCache) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 执行Insert,update,delete等语句
     * @param sql sql语句。
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(String sql, Object[] params, int[] paramTypes) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 执行Insert,update,delete等语句
     * @param sql sql语句。
     * @param params 需要传递到存储过程中的参数。
     * @param paramTypes 传递参数的类型。java.sql.Types的类型常量。
     * @return 受影响的记录数。-1表示执行sql语句失败。
     * @throws JdaDuplicateEntryException 主键重复。执行insert语句时可能触发此异常。
     * @throws JdaTooBigColumnLengthException 字段值长度超出该列最大允许长度时触发此异常。
     */
    int execute(String sql, Object[] params, int[] paramTypes, boolean forceNoCache) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException, JdaDuplicateEntryException;
    /**
     * <BR>批量执行Sql语句。
     * <BR>e.g. 向User表中批量插入3条记录：
     * <BR>JDA jda;
     * <BR>String sql = "Insert Into User (id,name,age) values (?,?,?)";
     * <BR>Object[][] params = 
     * <BR>{
     * <BR>&nbsp;&nbsp;{"liaoxj","廖雄杰",new Integer(24)},
     * <BR>&nbsp;&nbsp;{"zhenzh","甄志会",new Integer(23)},
     * <BR>&nbsp;&nbsp;{"guoq","郭潜",new Integer(26)}
     * <BR>};
     * <BR>jda.executeBatch(sql, params);
     * @param sql 欲执行的Sql语句。
     * @param params 包含参数值的二维数组。数组的长度即为批量执行的Sql语句的个数。
     * @return
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     */
    int[] executeBatch(String sql, Object[][] params) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * <BR>批量执行Sql语句。
     * <BR>e.g. 向User表中批量插入3条记录：
     * <BR>JDA jda;
     * <BR>String sql = "Insert Into User (id,name,age) values (?,?,?)";
     * <BR>Object[][] params = 
     * <BR>{
     * <BR>&nbsp;&nbsp;{"liaoxj","廖雄杰",new Integer(24)},
     * <BR>&nbsp;&nbsp;{"zhenzh","甄志会",new Integer(23)},
     * <BR>&nbsp;&nbsp;{"guoq","郭潜",new Integer(26)}
     * <BR>};
     * <BR>jda.executeBatch(sql, params);
     * @param sql 欲执行的Sql语句。
     * @param params 包含参数值的二维数组。数组的长度即为批量执行的Sql语句的个数。
     * @param paramTypes 传递参数的类型。与params对应。java.sql.Types的类型常量。
     * @return
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     */
    int[] executeBatch(String sql, Object[][] params, int[] paramTypes) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 执行存储过程查询
     * @param procName 存储过程名称
     * @return
     */
    List findCall(String procName);
    /**
     * 执行存储过程查询
     * @param procName 存储过程名称
     * @return
     */
    List findCall(String procName, boolean autoFlush);    
    /**
     * 执行存储过程查询
     * @param procName 存储过程名称
     * @param params （输入）参数
     * @return
     */
    List findCall(String procName, Object[] params);
    /**
     * 执行存储过程查询
     * @param procName 存储过程名称
     * @param params （输入）参数
     * @return
     */
    List findCall(String procName, Object[] params, boolean autoFlush);
    /**
     * 执行存储过程查询
     * @param procName 存储过程名称
     * @param params （输入）参数
     * @param paramTypes （输入）参数类型
     * @return
     */
    List findCall(String procName, Object[] params, int[] paramTypes);
    /**
     * 执行存储过程查询
     * @param procName 存储过程名称
     * @param params （输入）参数
     * @param paramTypes （输入）参数类型
     * @return
     */
    List findCall(String procName, Object[] params, int[] paramTypes, boolean autoFlush);
    /**
     * 执行存储过程。
     * <BR>JDA提供简单的存储过程调用机制，只能进行更新操作，并且不接受OUT参数。对于一般的查询操作，推荐使用PreparedStatement的find方法。
     * <BR>executeCall方法根据procName和params自动构建CallableStatement所需要的Call String。
     * 
     * @param procName 存储过程名称
     * @return 受影响的记录数
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     */
    int executeCall(String procName) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 执行存储过程。
     * <BR>JDA提供简单的存储过程调用机制，只能进行更新操作，并且不接受OUT参数。对于一般的查询操作，推荐使用PreparedStatement的find方法。
     * <BR>executeCall方法根据procName和params自动构建CallableStatement所需要的Call String。
     * 
     * @param procName 存储过程名称
     * @return 受影响的记录数
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     */
    int executeCall(String procName, boolean forceNoCache) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 执行存储过程。
     * <BR>JDA提供简单的存储过程调用机制，只能进行更新操作，并且不接受OUT参数。对于一般的查询操作，推荐使用PreparedStatement的find方法。
     * <BR>executeCall方法根据procName和params自动构建CallableStatement所需要的Call String。
     * 
     * @param procName 存储过程名称
     * @param params （输入）参数
     * @return 受影响的记录数
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     */
    int executeCall(String procName, Object[] params) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 执行存储过程。
     * <BR>JDA提供简单的存储过程调用机制，只能进行更新操作，并且不接受OUT参数。对于一般的查询操作，推荐使用PreparedStatement的find方法。
     * <BR>executeCall方法根据procName和params自动构建CallableStatement所需要的Call String。
     * 
     * @param procName 存储过程名称
     * @param params （输入）参数
     * @return 受影响的记录数
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     */
    int executeCall(String procName, Object[] params, boolean forceNoCache) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 执行存储过程。
     * <BR>JDA提供简单的存储过程调用机制，只能进行更新操作，并且不接受OUT参数。对于一般的查询操作，推荐使用PreparedStatement的find方法。
     * <BR>executeCall方法根据procName和params自动构建CallableStatement所需要的Call String。
     * 
     * @param procName 存储过程名称。
     * @param params （输入）参数
     * @param paramTypes （输入）参数类型
     * @return 受影响的记录数
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     */
    int executeCall(String procName, Object[] params, int[] paramTypes) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 执行存储过程。
     * <BR>JDA提供简单的存储过程调用机制，只能进行更新操作，并且不接受OUT参数。对于一般的查询操作，推荐使用PreparedStatement的find方法。
     * <BR>executeCall方法根据procName和params自动构建CallableStatement所需要的Call String。
     * 
     * @param procName 存储过程名称。
     * @param params （输入）参数
     * @param paramTypes （输入）参数类型
     * @return 受影响的记录数
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     */
    int executeCall(String procName, Object[] params, int[] paramTypes, boolean forceNoCache) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 批量执行存储过程
     *
     */
    int[] executeCallBatch(String procName, Object[][] params) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 批量执行存储过程
     * @param procName
     * @param params
     * @param paramTypes
     * @return
     * @throws JdaDuplicateEntryException
     * @throws JdaTooBigColumnLengthException
     * @throws AccessDataException
     */
    int[] executeCallBatch(String procName, Object[][] params, int[] paramTypes) throws JdaDuplicateEntryException,JdaTooBigColumnLengthException,AccessDataException;
    /**
     * 从配置文件中取得Sql查询
     * @param name 查询的名称
     * @return
     */
    String getQueryString(String name);
    /**
     * 创建命名查询。
     * 查询Sql语句保存在配置文件中。
     * @param name 查询的名称
     * @return
     */
    Query createQuery(String name);
}
