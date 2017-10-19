
package com.tsoft.core.database.exception;


/**
 * Jda重复Entry异常
 *
 */
public class JdaDuplicateEntryException extends JdaException {

	private static final long serialVersionUID = -5577853537769392639L;
	/**
     * @param arg0
     */
    public JdaDuplicateEntryException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    /**
     * @param arg0
     * @param arg1
     */
    public JdaDuplicateEntryException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }
}
