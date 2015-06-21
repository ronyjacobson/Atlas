package il.ac.tau.cs.databases.atlas.core.exception;

import org.apache.log4j.Logger;


/**
 * Error signaling the the person one tried to add already exists.
 */

@SuppressWarnings("serial")
public class PersonExistsError extends AtlasServerException {

    protected static final Logger logger = Logger.getLogger(PersonExistsError.class.getName());
    
    public PersonExistsError(String name) {
        super();
    	String msg =String.format("User %s already exists in Atlas.", name);
    	logger.error(msg);
    }
}
