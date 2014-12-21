package com.cboe.mdxUtil;

import java.util.List;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.mdxUtil.MDXSubGroupConfigurationService;
import com.cboe.interfaces.mdxUtil.MDXSubGroupConfigurationServiceHome;

public class MDXSubGroupConfigurationServiceHomeImpl extends ClientBOHome
		implements MDXSubGroupConfigurationServiceHome {

	public MDXSubGroupConfigurationService create(String mdxGroup, List<String> configurationGroups, List<String> configurationSubGroups) throws Exception
    {
		MDXSubGroupConfigurationServiceImpl mdxSubGroupConfigService = new MDXSubGroupConfigurationServiceImpl(mdxGroup, configurationGroups, configurationSubGroups);
        addToContainer(mdxSubGroupConfigService);
        mdxSubGroupConfigService.create(String.valueOf(mdxSubGroupConfigService.hashCode()));
		return mdxSubGroupConfigService;
    }

    public void clientInitialize() throws Exception{}
    
    public void clientStart(){}

}
