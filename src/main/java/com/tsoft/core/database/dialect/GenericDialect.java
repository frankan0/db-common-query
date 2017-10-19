package com.tsoft.core.database.dialect;

/**
 * Dialect 的通用实现。
 * 对于特定的数据库应尽量实用特定的Dialect实现
 *
 */
public class GenericDialect extends Dialect {

    public String getDialectName() {
        return "Unkown";
    }
}
