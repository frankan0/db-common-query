# db-common-query
一个通用数据库Query工具，基于jdbcTemplate，可以实现类似mybatis的SQL管理功能；

使用该工具的时候结合hibernate来进行开发，开发效率是非常高的。

使用例子：

### 如果使用SpringBoot 则配置bean即可：
```java
    @Bean
    public JDA commonJda(){
        JdbcDaoImpl jda = new JdbcDaoImpl();
        jda.setDataSource(masterDataSource());
        jda.setDialect("com.tsoft.core.database.dialect.MySQLDialect");
        List<String> mappings = Lists.newArrayList();
        mappings.add("/sql/sql-mapping.xml");
        jda.setSqlMappingFiles(mappings.toArray(new String[1]));
        jda.setUseScrollableResultSets(true);
        jda.setShowSql(true);
        jda.setShowRawSql(true);
        return jda;
    }
   
```
DataSource 正常配置即可；

如果使用 spring XML 配置方式：

```xml
     <bean id="jda" class="com.tsoft.base.database.jda.JdbcDaoImpl">
		<property name="dataSource" ref="dataSource" />
		<property name="dialect" value="com.tsoft.base.database.jda.dialect.MySQLDialect" />
		<property name="sqlMappingFiles">
			<list>
				<value>/config/sql/sql-mapping.xml</value>
			</list>
		</property>
		<property name="useScrollableResultSets" value="true" />
		<property name="showSql" value="true" />
		<property name="showRawSql" value="true" />
	</bean>
	
```

SQL 写入 mapping.xml 文件里面：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<sql-mappings-list>

	<named-query name="xs.live.find.user"><![CDATA[
		select  * from xs_live_m_user where id=?
	]]></named-query>

</sql-mappings-list>

```

使用JDA：
```java
    @Service
    public class UserService {
    
        @Autowired
        private UserRespository userRespository;
        
        // 注入JDA
        @Autowired
        private JDA jda;
    
        public User findUserById(int id){
            return userRespository.findOne(id);
        }
    
        public User findUser(int id) {
            // 使用JDA 
            Query query = jda.createQuery("xs.live.find.user");
            User user = (User)query.findOne(new Object[]{id}, User.class);
            return user;
        }
    }
```

    