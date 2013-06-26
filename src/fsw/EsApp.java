package fsw;

import ccsds.*;

public class EsApp extends FswApp
{

   static final public String  PREFIX_STR = "ES";
      
   static final public int CMD_MID      = 0x1806;   
   static final public int CMD_FC_NOOP  = 0;
   static final public int CMD_FC_RESET = 1;
   static final public int TLM_MID_HK   = 0x0800;   

   public EsApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End EsApp
   
   public void defineCmds() {
    
      // Add commands here if wanted in GUI drop down
      
   } // defineCmds
   
   public void defineTlm() {
      
      TlmList.add(TLM_MID_HK);
         
   } // defineCmds
  
   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      // Add logic to process strings you want displayed
      //return null; 
      return ParseRawData(TlmMsg.getPacket());
      
   } // getTlmStr

} // End class EsApp
