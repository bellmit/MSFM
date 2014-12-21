package com.cboe.infrastructureServices.traderService;

public class TestPOAImpl extends com.cboe.infrastructureServices.interfaces.adminService.POA_Admin {

	public TestPOAImpl( ) {
	}

	public void defineProperty(com.cboe.infrastructureServices.interfaces.adminService.Property aProperty)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyValue, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedProperty {}

	public java.lang.String getPropertyValue(java.lang.String propertyName)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.PropertyNotFound, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName {return null;}

	public void defineProperties(com.cboe.infrastructureServices.interfaces.adminService.Property[] nproperties)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.MultipleExceptions {}

	public com.cboe.infrastructureServices.interfaces.adminService.Property[] getProperties() {return null;}

	public com.cboe.infrastructureServices.interfaces.adminService.Command[] getAllCommands() {return null;}

	public com.cboe.infrastructureServices.interfaces.adminService.Command getCommand(java.lang.String commandName)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand {return null;}

	public boolean executeCommand(com.cboe.infrastructureServices.interfaces.adminService.CommandHolder command)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidParameter {return false;}

	public org.omg.CORBA.Object get_typed_consumer() {return null;}

	public void push(org.omg.CORBA.Any data) 
		throws org.omg.CosEventComm.Disconnected {}

	public void disconnect_push_consumer() {}

}
