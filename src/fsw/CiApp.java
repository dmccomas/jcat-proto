package fsw;

import ccsds.CcsdsTlmPkt;

public class CiApp extends FswApp
{

   // These must match the cFE's test lab CI (UDB based) definitions
   static final public Integer IP_PORT = 1234;
   
   static final public Integer CMD_MID      = 0x1881;   
   static final public Integer CMD_FC_NOOP  = 1;
   static final public Integer CMD_FC_RESET = 3;

   static final public Integer CMD_MID_SEND_HK = 0x1891;
 
   static final public Integer TLM_MID_HK = 0x0881;   

   static final public String  PREFIX_STR = "CI";
   
   public CiApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End CmdIngest
   
   public void defineCmds() {
    
      CmdList.set(CMD_FC_NOOP,  new CmdPkt(PREFIX_STR, "No Op", CMD_MID, CMD_FC_NOOP));
      CmdList.set(CMD_FC_RESET, new CmdPkt(PREFIX_STR, "Reset", CMD_MID, CMD_FC_RESET));
      
   } // defineCmds

   public void defineTlm() {
      
      TlmList.add(TLM_MID_HK);
         
   } // defineCmds

   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      return TLM_STR_TBD; 
      
   } // getTlmStr
   
} // End class CmdIngest
