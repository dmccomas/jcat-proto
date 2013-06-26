package fsw;

import ccsds.*;

public class SbApp extends FswApp
{

   static final public String  PREFIX_STR = "SB";
      
   static final public int CMD_MID      = 0x1805;   
   static final public int TLM_MID_HK   = 0x0803;   

   public SbApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End SbApp
   
   public void defineCmds() {
    
      // Add commands here if wanted in GUI drop down
      
   } // defineCmds
   
   public void defineTlm() {
      
      TlmList.add(TLM_MID_HK);
         
   } // defineTlm
   
   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      // Add logic to process strings you want displayed
      return null; 
      
   } // getTlmStr

} // End class SbApp
