package il.ac.tau.cs.databases.atlas.core.exception;

import org.apache.log4j.Logger;


/**
 * An exception of the Atlas server.
 * Thrown when Sql failed, connection failed etc...
 */

@SuppressWarnings("serial")
public class AtlasServerException extends Exception {

    protected static final Logger logger = Logger.getLogger(AtlasServerException.class.getName());
    
    public AtlasServerException(String msg) {
        super(msg);
    	logger.error(msg);
    }

	public AtlasServerException() {
		super();
	}
}
