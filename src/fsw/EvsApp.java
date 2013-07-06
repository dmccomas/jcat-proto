package fsw;

import ccsds.*;

public class EvsApp extends FswApp
{

   static final public String  PREFIX_STR = "EVS";
      
   static final public int CMD_MID      = 0x1801;   
   static final public int CMD_FC_NOOP  = 1;
   static final public int CMD_FC_RESET = 2;
   static final public int CMD_FC_ADD_PKT = 3;

   static final public int TLM_MID_HK        = 0x0801;   
   static final public int TLM_MID_EVENT_MSG = 0x0808;
   
   public EvsApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End EvsApp
   
   public void defineCmds() {
    
      CmdList.set(CMD_FC_NOOP,  new CmdPkt(PREFIX_STR, "No Op", CMD_MID, CMD_FC_NOOP, 0));
      CmdList.set(CMD_FC_RESET, new CmdPkt(PREFIX_STR, "Reset", CMD_MID, CMD_FC_RESET, 0));

   } // defineCmds
   
   public void defineTlm() {
      
      TlmList.add(TLM_MID_HK);
      TlmList.add(TLM_MID_EVENT_MSG);
         
   } // defineTlm
   
   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      byte[] TlmPkt = TlmMsg.getPacket();
      
      // @todo - Having trouble with format and no time to debug
      String MsgA = new String(TlmPkt,12,122); // OS_MAX_API_NAME = 20, CFE_EVS_MAX_MESSAGE_LENGTH = 122
      String MsgB = new String(TlmPkt,44,122); // OS_MAX_API_NAME = 20, CFE_EVS_MAX_MESSAGE_LENGTH = 122
      String MsgStr  = MsgA.substring(0,MsgA.indexOf('\0')) + ": " + MsgB.substring(0,MsgB.indexOf('\0')) + "\n";
    
      return MsgStr; 
      
   } // getTlmStr

   public String[] getTlmStrArray(CcsdsTlmPkt TlmMsg) 
   {
      loadTlmStrArrayHdr(TlmMsg);
      
      return TlmStrArray;
      
   } // getTlmStrArray()

   
} // End class EvsApp
