package com.tsoft.core.database.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 公用程序<br>
 * 字符串处理
 */
public class UncString {
	private final static char[] CHAR_SEQUENCE = {
		'0', '1', '2', '3', '4', '5', '6', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'_'
	};
	
	private final static Random RAND = new Random();
    public static final int HIGHEST_SPECIAL = '>';
    public static final int SCRIPT_HIGHEST_SPECIAL = '\\';
    public static final int CSV_HIGHEST_SPECIAL = ',';
    public static final int HTML_HIGHEST_SPECIAL = HIGHEST_SPECIAL;
    
    public static char[][] specialCharactersRepresentation = new char[HIGHEST_SPECIAL + 1][];
    public static char[][] scriptCharactersRepresentation = new char[SCRIPT_HIGHEST_SPECIAL + 1][];
    public static char[][] csvCharactersRepresentation = new char[CSV_HIGHEST_SPECIAL + 1][];
    public static char[][] htmlCharactersRepresentation = new char[HTML_HIGHEST_SPECIAL + 1][];
    public final static char[] HTML_UNICODE_NEW_LINE1 = "&#x2028;".toCharArray();
    public final static char[] HTML_UNICODE_NEW_LINE2 = "&#x2029;".toCharArray();
    
    static {
        specialCharactersRepresentation['&'] = "&amp;".toCharArray();
        specialCharactersRepresentation['<'] = "&lt;".toCharArray();
        specialCharactersRepresentation['>'] = "&gt;".toCharArray();
        specialCharactersRepresentation['"'] = "&#034;".toCharArray();
        specialCharactersRepresentation['\''] = "&#039;".toCharArray();
        
        scriptCharactersRepresentation['\n'] = "\\n".toCharArray();
        scriptCharactersRepresentation['\r'] = "\\r".toCharArray();
        scriptCharactersRepresentation['"'] = "\\\"".toCharArray();
        scriptCharactersRepresentation['\''] = "\\'".toCharArray();
        scriptCharactersRepresentation['\\'] = "\\\\".toCharArray();
        
        csvCharactersRepresentation['\n'] = new char[] {'\n'};
        csvCharactersRepresentation['\r'] = new char[] {'\r'};
        csvCharactersRepresentation['"'] = new char[] {'"', '"'};
        csvCharactersRepresentation[','] = new char[] {','};
        
        htmlCharactersRepresentation['&'] = "&amp;".toCharArray();
        htmlCharactersRepresentation['<'] = "&lt;".toCharArray();
        htmlCharactersRepresentation['>'] = "&gt;".toCharArray();
        htmlCharactersRepresentation['"'] = "&quot;".toCharArray();
        htmlCharactersRepresentation[' '] = "&nbsp;".toCharArray();
        htmlCharactersRepresentation['\n'] = "<br/>".toCharArray();
        htmlCharactersRepresentation['\r'] = new char[0];
    }	
	
    /**
     * 将字符串从 ISO-8859-1 --> GBK<br>
     */
    public static String toGB2312( String inStr )
    {

      String _tempStr = "";

      if ( inStr == null )
      {
         _tempStr = "";

      } else {

         try {

            _tempStr = new String( inStr.getBytes("ISO-8859-1"),"GB2312");

         } catch (Exception e) {

            _tempStr = "";

         }
      }

      return _tempStr;

    }


   /**
    * 字符串替换
    */
   public static String replace (String inSource, String inOldStr, String inNewStr )
   {

      String _tempStr = "";

      int _pos = 0, _temp = inOldStr.length();

      _pos = inSource.indexOf( inOldStr );

      while ( _pos >= 0 )
      {

         _tempStr = _tempStr + inSource.substring( 0, _pos ) + inNewStr;

         inSource = inSource.substring( _pos + _temp);

         _pos = inSource.indexOf( inOldStr );

      }

      return _tempStr + inSource;
   }

    /**
     * 字符串替换
     */
    public static String replaceIgnoreCase (String inSource, String inOldStr, String inNewStr )
    {

        String _tempStr = "";

        int _pos = 0, _temp = inOldStr.length();

        String _source = inSource.toLowerCase();

        String _oldStr = inOldStr.toLowerCase();

        _pos = _source.indexOf( _oldStr);

        while ( _pos >= 0 )
        {

            _tempStr = _tempStr + inSource.substring( 0, _pos ) + inNewStr;

            _source = _source.substring( _pos + _temp );

            inSource = inSource.substring( _pos + _temp );

            _pos = _source.indexOf( _oldStr);

        }

        return _tempStr + inSource;
   }

    /**
     * 将字符串转换成 int, 如果转换失败则返回 0
     * @param inStr 输入的字符串
     * @return int
     */
    public static int toInt( String inStr )
    {

        return toInt ( inStr, 0 );

    }

    /**
     * 将字符串转换成 int, 如果转换失败则返回 inDefault
     * @param  inStr 输入的字符串
     * @param  inDefault 默认值
     * @return int
     */
    public static int toInt( String inStr, int inDefault )
    {
    	if(inStr == null){
    		return inDefault;
    	}
    	
        int _temp = inDefault;

        try {

            _temp = Integer.valueOf( inStr ).intValue();

        } catch ( NumberFormatException e ) {

            _temp = inDefault;

        }

        return _temp;

    }

    /**
     * 将 null 值转换成空字符串
     */
    public static String Null2Empty( String inStr )
    {

        if ( inStr == null )

            return "";

        else

            return inStr;

    }

    /**
     * 将 null 值转换成 "nbsp;"
     */
    public static String Null2NBSP( String inStr )
    {

        if ( inStr == null )
        {

            return Null2Default ( inStr, "&nbsp;" );

        } else {

            if ( inStr.length() < 1 )
            {

                return "&nbsp;";

            } else {

                return inStr;

            }
        }

    }

    /**
     * 将 null 值转换成指定字符
     */
    public static String Null2Default( String inStr, String inDefault )
    {

        if ( inStr == null )

            return inDefault;

        else

            return inStr;

    }


    /**
     * 将字符串转换成 long, 如果转换失败则返回 inDefault
     * @param  inStr
     * @param  inDefault
     * @return long
     */
    public static long toLong ( String inStr, long inDefault )
    {
    	if(inStr == null){
    		return inDefault;
    	}
    	
        long _temp = inDefault;

        try {

            _temp = Long.valueOf( inStr ).longValue();

        } catch ( NumberFormatException e ) {

            _temp = inDefault;

        }

        return _temp;
    }

    /**
     * 将字符串转换成 long, 如果转换失败则返回 0
     * @param  inStr
     * @return long
     */
    public static long toLong( String inStr )
    {

        return toLong( inStr, 0 );

    }
    /**
     * 将字符串转换成 long, 如果转换失败则返回 inDefault
     * @param  inStr
     * @param  inDefault
     * @return double
     */
    public static double toDouble ( String inStr, double inDefault )
    {
    	if(inStr == null){
    		return inDefault;
    	}
    	
        double _temp = inDefault;

        try {

            _temp = Double.valueOf( inStr ).doubleValue();

        } catch ( NumberFormatException e ) {

            _temp = inDefault;

        }

        return _temp;
    }

    /**
     * 将字符串转换成 long, 如果转换失败则返回 0
     * @param  inStr
     * @return long
     */
    public static double toDouble( String inStr )
    {

        return toDouble( inStr, 0 );

    }
    
    /**
     * 将字符串转换成 long, 如果转换失败则返回 inDefault
     * @param  inStr
     * @param  inDefault
     * @return float
     */
    public static float toFloat ( String inStr, float inDefault )
    {
    	if(inStr == null){
    		return inDefault;
    	}
    	
        float _temp = inDefault;

        try {

            _temp = Float.parseFloat(inStr);

        } catch ( NumberFormatException e ) {

            _temp = inDefault;

        }

        return _temp;
    }

    /**
     * 将字符串转换成 long, 如果转换失败则返回 0
     * @param  inStr
     * @return float
     */
    public static float toFloat( String inStr )
    {

        return toFloat( inStr, 0 );

    }
    
    /**
     * 将包含长整型数的字符串转化为Long值。
     * 若转化失败，返回null。
     * @param strInteger
     * @return
     */
    public static Integer parseInteger(String strInteger){
      Integer _integer = null;

      if(strInteger == null || "".equals(strInteger)) return null;

      try {
        _integer = new Integer(strInteger);
      }
      catch (Exception ex) {
        _integer = null;
      }
      return _integer;
    }

    /**
     * 将包含长整型数的字符串转化为Long值。
     * 若转化失败，返回null。
     * @param strLong
     * @return
     */
    public static Long parseLong(String strLong){
      Long _long = null;

      if(strLong == null || "".equals(strLong)) return null;

      try {
        _long = new Long(strLong);
      }
      catch (Exception ex) {
        _long = null;
      }
      return _long;
    }


    /**
     * 将包含长整型数的字符串转化为Long值。
     * 若转化失败，返回null。
     * @param strDbl
     * @return
     */
    public static Double parseDouble(String strDbl){
      Double num = null;

      if(strDbl == null || "".equals(strDbl)) return null;

      try {
          num = new Double(strDbl);
      }
      catch (Exception ex) {
          num = null;
      }
      return num;
    }
    
    public static Float parseFloat(String strFloat){
        Float num = null;

        if(strFloat == null || "".equals(strFloat)) return null;

        try {
            num = new Float(strFloat);
        }
        catch (Exception ex) {
            num = null;
        }
        return num;
      }
    
    /**
     * 字符串重复
     */
    public static String repeat (String inStr, int inTimes )
    {

        String _rtn = "";

        for ( int k = 0; k < inTimes; k ++ )
        {

            _rtn += inStr;

        }

        return _rtn;

    }

    /**
     * 取左面 inLen 个字节
     */
    public static String left (String inStr, int inLen )
    {

        String _rtn = "";

        if ( inStr.getBytes().length > inLen )
        {

            if ( inStr.length() < inLen )
            {

                inLen = inStr.length();

            }

            for ( int k = inLen; k > 0; k--)
            {

                _rtn = inStr.substring(0, k);

                if ( _rtn.getBytes().length <= inLen )
                {

                    break;

                }

            }

        } else {

            _rtn = inStr;

        }

        return _rtn;

    }
    
    /*
     * 合并路径 
     * @param p1
     * @param p2
     * @return
     */
    public static String combinePath(String p1, String p2){
        String path = null;
        
        if(p1 == null || p2 == null){
            return (p1 == null)?p2:p1;
        }
        
        //p1, p2都不为空
        if((p1.endsWith(File.separator) || p1.endsWith("/")) && 
                (p2.startsWith(File.separator) || p2.startsWith("/"))){
            path = p1 + p2.substring(1);
        }else if(!(p1.endsWith(File.separator) || p1.endsWith("/")) && 
                 !(p2.startsWith(File.separator) || p2.startsWith("/")) ){
            path = p1 + File.separator + p2;
        }else{
            path = p1 + p2;
        }
        
        return path;
    }

    /*
     * 返回文件名的扩展名 
     * @param fileName 文件名
     * @return 文件扩展名
     */
    public static String extendedName( String fileName )
    {
        if(fileName == null || fileName.equals("")){
            return "";
        }
        
        if( fileName.lastIndexOf('.') > 0 )
        {
        	return fileName.substring( fileName.lastIndexOf('.') );
        }else{
        	return "";
        }
    }
    
	public static String formatNumber(Number num, String pattern){
		String fmtNum = null;
	
		if(num == null)
			//return null;
		    num = new Double(0.0);
		
		DecimalFormat format = (DecimalFormat)DecimalFormat.getNumberInstance();
		format.applyPattern(pattern);
		//format.setDecimalSeparatorAlwaysShown(true);
		
		try{
			fmtNum = format.format(num);
		}catch(Exception ex){
			//Format failed
		}
	
		return fmtNum;
	}
	
	public static String formatNumber(Number num){
	    return formatNumber(num, "0.000E0");
	}
	
	public static String formatNumber(double d, String pattern){
		String fmtNum = null;
		
		DecimalFormat format = (DecimalFormat)DecimalFormat.getNumberInstance();
		format.applyPattern(pattern);
		//format.setDecimalSeparatorAlwaysShown(true);
	
		try{
			fmtNum = format.format(d);
		}catch(Exception ex){
			//Format failed
		}
	
		return fmtNum;
	}
	
	public static String formatNumber(double d){
		return formatNumber(d, "0.000E0");
	}
	/*
    public static String escapeHTML(String html){
    	if(html == null || html.length() == 0)
    		return "";
    		
        String regex = "[\\r\\s\"\\&<>]";
        
        StringBuilder content = new StringBuilder();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        while(matcher.find()){
            String chr = matcher.group();
            String replace = "";
            if(" ".equals(chr)){
                replace = "&nbsp;";
            }else if("&".equals(chr)){
                replace = "&amp;";
            }else if("<".equals(chr)){
                replace = "&lt;";
            }else if(">".equals(chr)){
                replace = "&gt;";
            }else if("\"".equals(chr)){
                replace = "&quot;";
            }else if("\r".equals(chr)){
                replace = "<br>";
            }
            matcher.appendReplacement(content, replace);
        }
        matcher.appendTail(content);
        
        return content.toString();
    }
    */
	
	public static String escapeHTML(String html) {
		return escapeHTML(html, 1);
	}
	
    public static String escapeHTML(String html, int tabIndent) {
    	if(html == null)
    		return "";
    	
        int start = 0;
        int length = html.length();
        char[] arrayBuffer = html.toCharArray();
        StringBuilder escapedBuffer = null;

        int lastCarriage = -1;
        for (int i = 0; i < length; i++) {
            char c = arrayBuffer[i];
            if (c == '\u2028' || c == '\u2029') {
            	char[] escaped = (c == '\u2028' ? HTML_UNICODE_NEW_LINE1 : HTML_UNICODE_NEW_LINE2);
            	 // create StringBuilder to hold escaped xml string
                if (start == 0) {
                    escapedBuffer = new StringBuilder(length + 32);
                }
                // add unescaped portion
                if (start < i) {
                    escapedBuffer.append(arrayBuffer,start,i-start);
                }
                start = i + 1;
                // add escaped xml
                if(escaped.length > 0) escapedBuffer.append(escaped);
            } else if (c <= HTML_HIGHEST_SPECIAL) {
            	if( c == '\t') {
            		// create StringBuilder to hold escaped xml string
                    if (start == 0) {
                        escapedBuffer = new StringBuilder(length + 32);
                    }
                    // add unescaped portion
                    if (start < i) {
                        escapedBuffer.append(arrayBuffer,start,i-start);
                    }
                    start = i + 1;
                    
            		if(tabIndent > 0) {
            			int spaces = tabIndent - ((i - lastCarriage - 1) % tabIndent);
            			for(int j = 0; j < spaces; j++) escapedBuffer.append("&nbsp;");
            		}
            	} else {
            		char[] escaped = htmlCharactersRepresentation[c];
                    if (escaped != null) {
                        // create StringBuilder to hold escaped xml string
                        if (start == 0) {
                            escapedBuffer = new StringBuilder(length + 32);
                        }
                        // add unescaped portion
                        if (start < i) {
                            escapedBuffer.append(arrayBuffer,start,i-start);
                        }
                        start = i + 1;
                        // add escaped xml
                        if(escaped.length > 0) escapedBuffer.append(escaped);
                    }
                    
                    if(c == '\n') {
                    	lastCarriage = i;
                    }
            	}
            }
        }
        // no xml escaping was necessary
        if (start == 0) {
            return html;
        }
        // add rest of unescaped portion
        if (start < length) {
            escapedBuffer.append(arrayBuffer,start,length-start);
        }
        return escapedBuffer.toString();
    }
    
    public static String toHex(byte buffer[]) {
    	StringBuilder sb = new StringBuilder(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(Character.forDigit((buffer[i] & 0xf0) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 0x0f, 16));
        }
        return sb.toString();
    }
    
    public static int randomInt(){
    	return RAND.nextInt();
    }
    
    public static int randomInt(int upperBound){
    	return RAND.nextInt(upperBound);
    }
    
    public static int randomInt(int lowerBound, int upperBound){
    	return RAND.nextInt(upperBound - lowerBound) + lowerBound;
    }
    
    public static long randomLong(){
    	return RAND.nextLong();    	
    }
    
    public static long randomLong(long lowerBound, long upperBound){
    	return (long)(RAND.nextDouble() * (upperBound - lowerBound)) + lowerBound;
    }
    
    public static long randomLong(long upperBound){
    	return randomLong(0, upperBound);
    }
    
    public static String randomString(int size){
    	if(size <= 0){
    		return "";
    	}
    	
    	StringBuilder sb = new StringBuilder(size);
    	for(int i = 0; i < size; i++){
    		int randCharIndex = randomInt(CHAR_SEQUENCE.length - 1);
    		sb.append(CHAR_SEQUENCE[randCharIndex]);
    	}
    	
    	return sb.toString();
    }
    
	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * Trims tokens and omits empty tokens.
	 * <p>The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using <code>delimitedListToStringArray</code>
	 * @param str the String to tokenize
	 * @param delimiters the delimiter characters, assembled as String
	 * (each of those characters is individually considered as delimiter).
	 * @return an array of the tokens
	 * @see StringTokenizer
	 * @see String#trim
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * <p>The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using <code>delimitedListToStringArray</code>
	 * @param str the String to tokenize
	 * @param delimiters the delimiter characters, assembled as String
	 * (each of those characters is individually considered as delimiter)
	 * @param trimTokens trim the tokens via String's <code>trim</code>
	 * @param ignoreEmptyTokens omit empty tokens from the result array
	 * (only applies to tokens that are empty after trimming; StringTokenizer
	 * will not consider subsequent delimiters as token in the first place).
	 * @return an array of the tokens
	 * @see StringTokenizer
	 * @see String#trim
	 */
	public static String[] tokenizeToStringArray(
			String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

		StringTokenizer st = new StringTokenizer(str, delimiters);
		List tokens = new ArrayList();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}
	
	/**
	 * Copy the given Collection into a String array.
	 * The Collection must contain String elements only.
	 * @param collection the Collection to copy
	 * @return the String array (<code>null</code> if the Collection
	 * was <code>null</code> as well)
	 */
	public static String[] toStringArray(Collection collection) {
		if (collection == null) {
			return null;
		}
		return (String[]) collection.toArray(new String[collection.size()]);
	}
	
	public static String encodeURL(String s){
		return encodeURL(s, "UTF-8");
	}
	
	public static String encodeURL(String s, String encoding){
		if(s == null || s.equals("")){
			return s;
		}
		
		try {
			return URLEncoder.encode(s, encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static String decodeURL(String s){
		return decodeURL(s, "UTF-8");
	}
	
	public static String decodeURL(String s, String encoding){
		if(s == null || s.equals("")){
			return s;
		}
		
		try {
			return URLDecoder.decode(s, encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
    public static String escapeXml(String s) {
    	if(s == null)
    		return "";
    	
        int start = 0;
        int length = s.length();
        char[] arrayBuffer = s.toCharArray();
        StringBuilder escapedBuffer = null;

        for (int i = 0; i < length; i++) {
            char c = arrayBuffer[i];
            if (c <= HIGHEST_SPECIAL) {
                char[] escaped = specialCharactersRepresentation[c];
                if (escaped != null) {
                    // create StringBuilder to hold escaped xml string
                    if (start == 0) {
                        escapedBuffer = new StringBuilder(length + 5);
                    }
                    // add unescaped portion
                    if (start < i) {
                        escapedBuffer.append(arrayBuffer,start,i-start);
                    }
                    start = i + 1;
                    // add escaped xml
                    escapedBuffer.append(escaped);
                }
            }
        }
        // no xml escaping was necessary
        if (start == 0) {
            return s;
        }
        // add rest of unescaped portion
        if (start < length) {
            escapedBuffer.append(arrayBuffer,start,length-start);
        }
        return escapedBuffer.toString();
    }
    
    public static String escapeScript(String s) {
    	if(s == null)
    		return "";
    	
        int start = 0;
        int length = s.length();
        char[] arrayBuffer = s.toCharArray();
        StringBuilder escapedBuffer = null;

        for (int i = 0; i < length; i++) {
            char c = arrayBuffer[i];
            if (c <= SCRIPT_HIGHEST_SPECIAL) {
                char[] escaped = scriptCharactersRepresentation[c];
                if (escaped != null) {
                    // create StringBuilder to hold escaped xml string
                    if (start == 0) {
                        escapedBuffer = new StringBuilder(length + 5);
                    }
                    // add unescaped portion
                    if (start < i) {
                        escapedBuffer.append(arrayBuffer,start,i-start);
                    }
                    start = i + 1;
                    // add escaped xml
                    escapedBuffer.append(escaped);
                }
            }
        }
        // no xml escaping was necessary
        if (start == 0) {
            return s;
        }
        // add rest of unescaped portion
        if (start < length) {
            escapedBuffer.append(arrayBuffer,start,length-start);
        }
        return escapedBuffer.toString();
    }
    
    public static String escapeCSV(String s) {
    	return escapeCSV(s, true, false);
    }
    
    public static String escapeCSV(String s, boolean preserveCRLF, boolean alwaysWithQuotes) {
    	if(s == null)
    		return alwaysWithQuotes ? "\"\"" : "";
    	
        int start = 0;
        int length = s.length();
        char[] arrayBuffer = s.toCharArray();
        StringBuilder escapedBuffer = null;
        boolean isEscaped = false;
        for (int i = 0; i < length; i++) {
            char c = arrayBuffer[i];
            if (c <= CSV_HIGHEST_SPECIAL) {
                char[] escaped = csvCharactersRepresentation[c];
                if (escaped != null) {
                    // create StringBuilder to hold escaped xml string
                    if (start == 0) {
                        escapedBuffer = new StringBuilder(length + 5);
                    }
                    // add unescaped portion
                    if (start < i) {
                        escapedBuffer.append(arrayBuffer,start,i-start);
                    }
                    start = i + 1;
                    if(preserveCRLF || (c != '\r' && c != '\n')) {
                    	// add escaped xml
                    	escapedBuffer.append(escaped);
                    }
                    
                    isEscaped = true;
                }
            }
        }
        // no xml escaping was necessary
        if (start == 0) {
            return alwaysWithQuotes ? "\"" + s + "\"" : s;
        }
        // add rest of unescaped portion
        if (start < length) {
            escapedBuffer.append(arrayBuffer,start,length-start);
        }
        
        if(alwaysWithQuotes || isEscaped) {
        	escapedBuffer.insert(0, '"').append('"');
        }
        return escapedBuffer.toString();
    }

    /**
     * 保留小数点后两位，采用四舍五入方法
     */
    public static double getDouble1(double pDouble) {
        BigDecimal bd = new BigDecimal(String.valueOf(pDouble));
        BigDecimal bd1 = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        pDouble = bd1.doubleValue();
        Double.doubleToLongBits(pDouble);
        return pDouble;
    }


}

