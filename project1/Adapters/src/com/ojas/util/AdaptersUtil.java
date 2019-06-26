package com.ojas.util;

import oracle.core.ojdl.logging.ODLLevel;
import oracle.core.ojdl.logging.ODLLogger;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.platform.Platform;

public class AdaptersUtil {
	
	public static final ODLLogger LOGGER = ODLLogger.getODLLogger(AdapterConstants.ADAPTER_LOG);
	public static final String CLASSNAME = AdaptersUtil.class.getName();
	
	public String getUserLogin(String userKey) {
		String userLogin = null;
		UserManager umgr = Platform.getService(UserManager.class);
		try {
			userLogin = umgr.getDetails(userKey, null, false).getLogin();
		} catch (Exception e){
			LOGGER.log(ODLLevel.ERROR, CLASSNAME + " getUserLogin() : " + e);
		}
		return userLogin;
		
	}
	public void getUserDetails(String userKey) {
		System.out.println("*******//////sneha////////*************");
		
	}

}
