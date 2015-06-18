package il.ac.tau.cs.databases.atlas.exception;

import org.apache.log4j.Logger;


/**
 * Created by user on 13/05/2015.
 */

public class PersonExistsError extends AtlasServerException {

    protected static final Logger logger = Logger.getLogger(PersonExistsError.class.getName());
    
    public PersonExistsError(String name) {
        super();
    	String msg =String.format("User %s already exists in Atlas.", name);
    	logger.error(msg);
    }
}
