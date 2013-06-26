package fsw;

import ccsds.*;

public class TblApp extends FswApp
{

   static final public String  PREFIX_STR = "TBL";
      
   static final public int CMD_MID      = 0x1804;   
   static final public int TLM_MID_HK   = 0x0804;   

   public TblApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End TblApp
   
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

} // End class TblApp
