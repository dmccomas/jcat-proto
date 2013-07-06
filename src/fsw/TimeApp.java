package fsw;

import ccsds.*;

public class TimeApp extends FswApp
{

   static final public String  PREFIX_STR = "TIME";
      
   static final public int CMD_MID      = 0x1805;   
   static final public int TLM_MID_HK   = 0x0805;   

   public TimeApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End TimeApp
   
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

   public String[] getTlmStrArray(CcsdsTlmPkt TlmMsg) 
   {
      loadTlmStrArrayHdr(TlmMsg);
      
      return TlmStrArray;
      
   } // getTlmStrArray()


} // End class TimeApp
