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
    
      CmdList.set(CMD_FC_NOOP,  new CmdPkt(PREFIX_STR, "No Op", CMD_MID, CMD_FC_NOOP));
      CmdList.set(CMD_FC_RESET, new CmdPkt(PREFIX_STR, "Reset", CMD_MID, CMD_FC_RESET));

      // @todo - Error: The initial constructor doesn't allocate space for the command parameters!!
      int CmdDataLen = 7;
      byte[] CmdDataBuf = new byte[CmdDataLen];
      CmdPkt AddPktCmd = new CmdPkt(PREFIX_STR, "Add Pkt", CMD_MID, CMD_FC_ADD_PKT, CmdDataBuf, CmdDataLen);
      AddPktCmd.addParam(new CmdParam("Message ID",3840, 2));
      AddPktCmd.addParam(new CmdParam("Pkt Size",50, 2));
      AddPktCmd.addParam(new CmdParam("SB QoS",0, 2));
      AddPktCmd.addParam(new CmdParam("Buffer Cnt",1, 1));
      CmdList.set(CMD_FC_ADD_PKT, AddPktCmd);
      
   } // defineCmds
   
   public void defineTlm() {
      
      TlmList.add(TLM_MID_HK);
      TlmList.add(TLM_MID_EVENT_MSG);
         
   } // defineTlm
   
   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      byte[] TlmPkt = TlmMsg.getPacket();
      
      String Message = new String(TlmPkt,44,122); // OS_MAX_API_NAME = 20, CFE_EVS_MAX_MESSAGE_LENGTH = 122
      String MsgStr  = Message.substring(0,Message.indexOf('\0')) + "\n";
    
      return MsgStr; 
      
   } // getTlmStr

} // End class EvsApp
