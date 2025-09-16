package compiler.catalog;

/**
 * 目录异常类
 */
public class CatalogException extends Exception {
    public CatalogException(String message) {
        super(message);
    }
    
    public CatalogException(String message, Throwable cause) {
        super(message, cause);
    }
}
