
package com.tsoft.core.database.exception;


/**
 * 系统异常。若在Service或Controller实现中抛出此异常，webbase将自动捕捉并进行处理。
 *
 */
public class SystemException extends Exception {
	private static final long serialVersionUID = -5742736398809116520L;

	public SystemException() {
        super();
    }

    public SystemException(String s ) {
        super( s );
    }

    public SystemException(String msg, Throwable nestedThrowable ) {
        super( msg, nestedThrowable );
    }

    public SystemException(Throwable nestedThrowable ) {
        super( nestedThrowable );
    }
}