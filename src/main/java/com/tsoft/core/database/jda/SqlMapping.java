package com.tsoft.core.database.jda;
import com.tsoft.core.database.exception.SystemException;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;


/**
 * SqlMapping类。从classpath://sql-mapping.xml文件中将SQL语句加载至内存。
 *
 */
public class SqlMapping {
    private final static org.apache.commons.logging.Log Log = LogFactory.getLog(SqlMapping.class);
    private static final boolean NAMESPACE_AWARE = true;
    private static final boolean ALLOW_JAVA_ENCODINGS = true;
    
    private Map namedQueries;
    
    public SqlMapping() {
    	this.namedQueries = new java.util.HashMap();
    }
    
    /**
     * 设置命名查询Map
     * @param namedQueriesMap
     */
    protected void setNamedQueries(Map namedQueriesMap){
    	if(namedQueriesMap != null) {
    		namedQueries.putAll(namedQueriesMap);
    	}
    }
    
    /**
     * 获取命名查询。
     * @param name 命名查询的名称。
     * @return 返回命名查询对应的SQL语句。
     */
    public String getNamedQuery(String name){
        return containsQuery(name)?(String)namedQueries.get(name):null;
    }
    
    /**
     * 从inputSource中配置SqlMapping
     * @param inputSource inputSource
     * @throws SystemException 当解析inputSource发生异常时将抛出SystemExceptions
     */
    public void configure(InputSource inputSource) throws SystemException {
        try{
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setValidating(false);
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();
            final XmlConfigurator xmlConfigurator = new XmlConfigurator(this);
            xmlReader.setErrorHandler(xmlConfigurator);
            setSAXFeature(xmlReader, "http://xml.org/sax/features/namespaces", NAMESPACE_AWARE);
            setSAXFeature(xmlReader, "http://xml.org/sax/features/namespace-prefixes", !NAMESPACE_AWARE);
            setSAXFeature(xmlReader, "http://apache.org/xml/features/allow-java-encodings", ALLOW_JAVA_ENCODINGS);
            saxParser.parse(inputSource, xmlConfigurator);
        }catch(Exception ex){
            throw new SystemException("映射Sql时发生未知异常: " + ex.getMessage(), ex);
        }        
    }
    /**
     * 从reader配置SqlMapping
     * @param reader reader
     * @throws SystemException 当解析reader发生异常时将抛出SystemExceptions
     */
    public void configure(Reader reader) throws SystemException{
        configure(new InputSource(reader));
    }
    
    /**
     * 从inputStream配置SqlMapping
     * @param inputStream inputStream
     * @throws SystemException 当解析inputStream发生异常时将抛出SystemExceptions
     */
    public void configure(InputStream inputStream)throws SystemException{
        configure(new InputSource(inputStream));
    }
    
    /**
     * 从文件名(fileName)配置SqlMapping
     * @param fileName 文件名
     * @throws SystemException 当解析该文件发生异常时将抛出SystemExceptions
     */
    public void configure(String fileName) throws SystemException{
        try
        {
            configure(new InputSource(new FileReader(fileName)));
        }catch(FileNotFoundException fnfex){
            throw new SystemException("找不到Sql映射文件: " + fileName);
        }
    }
    
    /**
     * 从文件名(fileName)配置SqlMapping
     * @param fileNames 文件名
     * @throws SystemException 当解析该文件发生异常时将抛出SystemExceptions
     */
    public void configure(String[] fileNames) throws SystemException{
    	if(fileNames != null) {
    		for(int i = 0; i < fileNames.length; i++) {
		        try
		        {
		            configure(new InputSource(new FileReader(fileNames[i])));
		        }catch(FileNotFoundException fnfex){
		            throw new SystemException("找不到Sql映射文件: " + fileNames[i]);
		        }
    		}
    	}
    }
    
    /**
     * 从ClassPath中配置, 开头的'/'为Class Path的根
     * @param fileNameFromClasspath
     */
    public void configureFromClassPath(String fileNameFromClasspath) throws SystemException{
        if(fileNameFromClasspath == null){
            Log.error("Sql映射文件路径为空");
            return;
        }
        InputStream is ;
        
        try{
        	if(Log.isDebugEnabled())
        		Log.debug("Configure SqlMapping from classpath: " + fileNameFromClasspath);
            is = SqlMapping.class.getResourceAsStream(fileNameFromClasspath) ;
        }catch(Exception e){
             Log.error("未找到Sql映射文件: " + fileNameFromClasspath);
             return;
        }
        
        configure(is);
    }
    
    /**
     * 从ClassPath中配置, 开头的'/'为Class Path的根
     * @param fileNamesFromClasspath
     */
    public void configureFromClassPath(String[] fileNamesFromClasspath) throws SystemException{
        if(fileNamesFromClasspath == null){
            Log.error("Sql映射文件路径为空");
            return;
        }
        InputStream is ;
        
        for(int i = 0; i < fileNamesFromClasspath.length; i++) {
	        try{
	        	if(Log.isDebugEnabled())
	        		Log.debug("Configure SqlMapping from classpath: " + fileNamesFromClasspath[i]);
	            is = SqlMapping.class.getResourceAsStream(fileNamesFromClasspath[i]) ;
	        }catch(Exception e){
	             Log.error("未找到Sql映射文件: " + fileNamesFromClasspath[i]);
	             return;
	        }
	        
	        configure(is);
        }
    }
    
    /**
     * 销毁当前SqlMapping实例
     *
     */
    public void destroy() {
    	this.namedQueries.clear();
    	this.namedQueries = null;
    }
    
    private boolean containsQuery(String name){
        if(name == null || name.length() == 0)
            return false;
        
        return namedQueries.containsKey(name);
    }
    
    // only log warning on problems with recognition and support of features
    // we'll probably pull through anyway...
    private static void setSAXFeature(XMLReader xmlReader, String feature, boolean state) {
        try {
            if(xmlReader.getFeature(feature) != state){	
                //解析过程中setFeature会导致SAXNotSupportedException
                //加入该行判断可避免该问题
                xmlReader.setFeature(feature, state);	
            }
        } catch (SAXNotRecognizedException e) {
            Log.warn("Feature: '" + feature + "' not recognised by xml reader " + XMLReader.class.getName() + ".");
        } catch (SAXNotSupportedException e) {
            Log.warn("Feature: '" + feature + "' not supported by xml reader " + XMLReader.class.getName() + ".");
        }
    }
}
