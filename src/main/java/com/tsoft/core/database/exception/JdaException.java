
package com.tsoft.core.database.exception;


import org.springframework.dao.DataAccessException;

/**
 * JDA异常
 *
 */
public class JdaException extends DataAccessException {
	private static final long serialVersionUID = -7289756580689317887L;
	/**
     * @param arg0
     */
    public JdaException(String arg0) {
        super(arg0);
    }
    /**
     * @param arg0
     * @param arg1
     */
    public JdaException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
