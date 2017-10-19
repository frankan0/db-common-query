package com.tsoft.core.database.exception;

/**
 * 访问数据库异常
 * @author BurningICE
 *
 */
public class AccessDataException extends SystemException {
	private static final long serialVersionUID = -4204927523204202029L;
	/**
     * 
     */
    public AccessDataException() {
        super();
    }
    /**
     * @param s
     */
    public AccessDataException(String s) {
        super(s);
    }
    /**
     * @param msg
     * @param nestedThrowable
     */
    public AccessDataException(String msg, Throwable nestedThrowable) {
        super(msg, nestedThrowable);
    }
    /**
     * @param nestedThrowable
     */
    public AccessDataException(Throwable nestedThrowable) {
        super(nestedThrowable);
    }
}
