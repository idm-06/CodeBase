package dev.oim.validation;

import java.util.HashMap;

import oracle.core.ojdl.logging.ODLLevel;
import oracle.core.ojdl.logging.ODLLogger;
import oracle.iam.platform.kernel.ValidationException;
import oracle.iam.platform.kernel.ValidationFailedException;
import oracle.iam.platform.kernel.spi.ValidationHandler;
import oracle.iam.platform.kernel.vo.BulkOrchestration;
import oracle.iam.platform.kernel.vo.Orchestration;
public class Validation implements ValidationHandler{
	private static final ODLLogger logger = ODLLogger.getODLLogger(Validation.class.getName());
	@Override
	public void initialize(HashMap<String, String> hashMap) {
		logger.log(ODLLevel.NOTIFICATION, "Initialize");
	}

	@Override
	public void validate(long processID, long eventID, Orchestration orchestration)throws ValidationException, ValidationFailedException {
		logger.log(ODLLevel.NOTIFICATION, "validate with orchestration");
		logger.log(ODLLevel.NOTIFICATION, "processID:"+processID +"and eventID:"+ eventID);
	}

	@Override
	public void validate(long processID, long eventID, BulkOrchestration bulkOrchestration)throws ValidationException, ValidationFailedException {
		logger.log(ODLLevel.NOTIFICATION, "validate with bulkOrchestration");
		logger.log(ODLLevel.NOTIFICATION, "processID:"+processID +"and eventID:"+ eventID);
		
	}

}
