package il.ac.tau.cs.databases.atlas.core.exception;

import org.apache.log4j.Logger;


/**
 * Created by user on 13/05/2015.
 */

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
