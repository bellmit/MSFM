package com.cboe.interfaces.mdxUtil;

import java.util.List;


public interface MDXSubGroupConfigurationServiceHome {

	public final static String HOME_NAME = "MDXSubGroupConfigurationServiceHome";
    public MDXSubGroupConfigurationService create(String mdxGroup, List<String> configurationGroups, List<String> configurationSubGroups) throws Exception;
}
