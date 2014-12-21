class PushSupplierImpl 
   extends org.omg.CosEventComm._PushSupplierImplBase
   implements org.omg.CosEventComm.PushSupplier {
  
  public void disconnect_push_supplier(){
    System.out.println( "Disconnected callback ");
  }
};

