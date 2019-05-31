package com.ojas.eventhandlers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import oracle.core.ojdl.logging.ODLLevel;
import oracle.core.ojdl.logging.ODLLogger;
import oracle.iam.identity.exception.UserSearchException;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.Platform;
import oracle.iam.platform.authz.exception.AccessDeniedException;
import oracle.iam.platform.entitymgr.EntityManager;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;
import oracle.iam.platform.kernel.spi.PostProcessHandler;
import oracle.iam.platform.kernel.vo.AbstractGenericOrchestration;
import oracle.iam.platform.kernel.vo.BulkEventResult;
import oracle.iam.platform.kernel.vo.BulkOrchestration;
import oracle.iam.platform.kernel.vo.EventResult;
import oracle.iam.platform.kernel.vo.Orchestration;

import com.ojas.util.EventHandlerConstants;

/**
 * The Class PopulateEmailPostProcessEventHandler.
 *
 * @author mahesh 
 * 
 */

public class PopulateEmailPostProcessEventHandler implements PostProcessHandler {

	/** The Constant LOGGER. */
	public static final ODLLogger LOGGER = ODLLogger.getODLLogger(EventHandlerConstants.EVENTHANDLER_LOG);
	
	/** The mail count. */
	static int mailCount = 0;

	/**
	 * Instantiates a new populate email post process event handler.
	 */
	public PopulateEmailPostProcessEventHandler() {

	}

	/**
	 * Initialize.
	 *
	 * @param arg0 the arg 0
	 */

	@Override
	public void initialize(HashMap<String, String> arg0) {
		LOGGER.info("Inside Initial method of " + PopulateEmailPostProcessEventHandler.class.getName());
	}

	/**
	 * Cancel.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @return true, if successful
	 */
	@Override
	public boolean cancel(long arg0, long arg1,AbstractGenericOrchestration arg2) {
		LOGGER.info("Inside cancel method of " + PopulateEmailPostProcessEventHandler.class.getName());
		return false;
	}

	/**
	 * Compensate.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 */
	@Override
	public void compensate(long arg0, long arg1, AbstractGenericOrchestration arg2) {
		LOGGER.info("Inside compensate method of " + PopulateEmailPostProcessEventHandler.class.getName());
	}

	/**
	 * Execute.
	 *
	 * @param processId the process id
	 * @param eventId the event id
	 * @param orchestration the orchestration
	 * @return the event result
	 */
	@Override
	public EventResult execute(long processId, long eventId,Orchestration orchestration) {
		LOGGER.entering(PopulateEmailPostProcessEventHandler.class.getName(), " execute() Orchestration method::");

		HashMap<String, Serializable> parameters = orchestration.getParameters();

		String entityId = orchestration.getTarget().getEntityId();
		LOGGER.info("Entity ID : " + entityId);
		String targetType = orchestration.getTarget().getType();
		LOGGER.info("Target Type : " + targetType);
		try {
			
			String firstName = (String) parameters.get("First Name");
			String lastName = (String) parameters.get("Last Name");
			String originalEmail = (String) parameters.get("Email");
			LOGGER.info("First Name : " + firstName);
			LOGGER.info("Last Name : " + lastName);
			LOGGER.info("Email : " + originalEmail);
			if(null == originalEmail || originalEmail.equals("")) {
				if(null!= firstName) {
					originalEmail = firstName + "." + lastName + "@ojas-it.com";
				}
				else {
					originalEmail = "test." + lastName + "@ojas-it.com";
				}
			}
			
			String updatedMail = validateEmail(originalEmail, firstName, lastName);
			
			LOGGER.log(ODLLevel.INFO,"PopulateEmailPostProcessHandler","Updated Email : " + updatedMail);
			HashMap<String, Object> mapAttrs = new HashMap<String, Object>();
			mapAttrs.put("Email", updatedMail);

			EntityManager entMgr = Platform.getService(EntityManager.class);
			entMgr.modifyEntity(targetType, entityId, mapAttrs);

		} catch (Exception e) {
			LOGGER.log(ODLLevel.ERROR, "exception occurred : " + e);
		}
		LOGGER.exiting("PopulateEmailPostProcessEventHandler", "execute() Orchestration method::");

		return new EventResult();
	}

	/**
	 * Validate email.
	 *
	 * @param email the email
	 * @param firstName the first name
	 * @param lastName the last name
	 * @return the string
	 */
	private String validateEmail(String email, String firstName, String lastName) {
		LOGGER.entering("PopulateEmailPostProcessEventHandler", "validateEmail()");
		UserManager userManager = Platform.getService(UserManager.class);
		String updatedMail = null;
		SearchCriteria criteria = new SearchCriteria("User Login", "*", SearchCriteria.Operator.EQUAL);
		try {
			int i = 0;
			List<User> users = userManager.search(criteria, null, null);
			
			for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
				User user = (User) iterator.next();
				if(user.getEmail() != null) {
					LOGGER.info("Email ID of the user " + user.getLogin() + " : " + user.getEmail());
					if(user.getEmail().equals(email)) {
						i++;
						break;
					}
				}
			}
			if(i == 0) {
				updatedMail = email;
			} else {
				if(firstName != null){
				updatedMail = firstName + "." + lastName + "@ojas-it.com";
				LOGGER.info("no existing email matching with current generated mail : " + email);
				}
				else {
					LOGGER.info("There is an existing Email : " + email);
					updatedMail = "test." + lastName+ mailCount + "@ojas-it.com";
					LOGGER.info("Updated mail after modifying existing email : " + updatedMail);
					mailCount++;
					validateEmail(updatedMail, firstName, lastName);
				}
			}
			LOGGER.info("Updated Email : " + updatedMail);
			
		} catch (UserSearchException e) {
			LOGGER.log(ODLLevel.ERROR, "UserSearchException occurred : " + e);
		} catch (AccessDeniedException e) {
			LOGGER.log(ODLLevel.ERROR, "AccessDeniedException occurred : " + e);
		}
		LOGGER.exiting("PopulateEmailPostProcessEventHandler", "validateEmail()");
		return updatedMail;
	}

	/**
	 * Execute.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @return the bulk event result
	 */
	@Override
	public BulkEventResult execute(long arg0, long arg1, BulkOrchestration arg2) {
		return null;
	}

}
