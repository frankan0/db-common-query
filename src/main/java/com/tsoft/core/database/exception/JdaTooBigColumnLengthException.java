package com.tsoft.core.database.exception;

/**
 * Jda 数据列超出长度限制异常
 *
 */
public class JdaTooBigColumnLengthException extends JdaException {

	private static final long serialVersionUID = 6951816523023108779L;
	/**
     * @param arg0
     */
    public JdaTooBigColumnLengthException(String arg0) {
        super(arg0);
    }
    /**
     * @param arg0
     * @param arg1
     */
    public JdaTooBigColumnLengthException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
