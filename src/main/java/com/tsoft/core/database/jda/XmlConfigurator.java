package com.tsoft.core.database.jda;

import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

/**
 * Xml handler 类。
 * 解析sql-mapping并将解析完的结果存入sqlMapping实例。
 */
public class XmlConfigurator extends DefaultHandler {
    private static org.apache.commons.logging.Log Log = LogFactory.getLog(XmlConfigurator.class.getName());
    
    private static final String ROOT_ELEMENT_NAME = "sql-mappings-list"; 
    private SqlMapping sqlMapping;
    private HashMap namedQueries = new HashMap();
    private String name;
    private StringBuffer content = new StringBuffer();
    private boolean insideRoot;
    
    /**
     * 构建XmlConfigurator
     * @param sqlMapping 接收解析结果的sqlMapping实例
     */
    public XmlConfigurator(SqlMapping sqlMapping){
        super();
        this.sqlMapping = sqlMapping;
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        this.namedQueries.clear();
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        
        content.setLength(0);
        
        String eName = this.getElementName(uri, localName, qName);
        if(eName.equals(ROOT_ELEMENT_NAME)){
            if(insideRoot){
                throw new SAXException("<" + ROOT_ELEMENT_NAME + ">元素中不能包含另一个<" + ROOT_ELEMENT_NAME + ">元素");
            }
            
            insideRoot = true;
        }else if(eName.equals("named-query")){
            name = attributes.getValue("name");
            if(name == null || name.length() == 0){
                throw new SAXException("没有为查询指定名称!");
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) throws SAXException {

        if(insideRoot){
            this.content.append(ch, start, length);
        }
    }  
    
    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        String eName = this.getElementName(uri, localName, qName);
        if(eName.equals(ROOT_ELEMENT_NAME)){
            insideRoot = false;
        }else if(eName.equals("named-query")){
            this.addNamedQuery(name, content.toString().trim());
        }
       
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        sqlMapping.setNamedQueries(this.namedQueries);
    }
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException ex) throws SAXException {
        Log.warn("解析Sql映射文件 [第" + ex.getLineNumber() + "行]时警告: " + ex.getMessage() , ex);
    }
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException ex) throws SAXException {
        Log.error("解析Sql映射文件 [第" + ex.getLineNumber() + "行]时发生错误: " + ex.getMessage() , ex);
        throw ex;
    }
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException ex) throws SAXException {
        Log.error("解析Sql映射文件 [第" + ex.getLineNumber() + "行]时发生致命错误: " + ex.getMessage() , ex);
        throw ex;
    }
    
    // If no namespace use qname, else use lname.
    private String getElementName(String uri, String lname, String qname) {
        if (uri == null || "".equals(uri)) {
            return qname;
        } else {
            return lname;
        }
    }
    
    private void addNamedQuery(String name, String value){
        Log.debug("Mapping named query -> " + name + "...");
        this.namedQueries.put(name, value);
    }
}
