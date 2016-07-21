/*
 * Generic exception class for Google Static Maps URL builder. 
 */
package google.staticmaps.builder;

public class MapException extends Exception{

    public MapException()
    {
	super();
    }
    
    public MapException(String string)
    {
        super(string);
    }
    
    public MapException(String message, Throwable cause) 
    {
        super(message, cause);
    }
    
    public MapException(Throwable cause) 
    {
        super(cause);
    }
}
