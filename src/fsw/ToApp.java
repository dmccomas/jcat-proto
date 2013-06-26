package fsw;

import ccsds.CcsdsTlmPkt;

public class ToApp extends FswApp
{

   // These must match the cFE's test lab TO (UDP based) definitions
   static final public String  IP_ADDR = "192.168.1.81";
   //static final public String  IP_ADDR = "127.0.0.1";
   static final public int     IP_PORT = 1235;

   static final public int CMD_MID        = 0x1880;   
   static final public int CMD_FC_NOOP    = 0;
   static final public int CMD_FC_RESET   = 1;
   static final public int CMD_FC_ADD_PKT = 2;
   static final public int CMD_FC_REM_PKT = 4;
   static final public int CMD_FC_ENA_TLM = 6;

   static final public int CMD_MID_SEND_HK = 0x1890;
 
   static final public int TLM_MID_HK      = 0x0880;   
   static final public int TLM_MID_DAT_TYP = 0x0881;   

   static final public String  PREFIX_STR = "TO";
   
   public ToApp (String Prefix, String Name) {
      
      super(Prefix, Name);
      
   } // End TlmOutput
   
   public void defineCmds() {

      // Command variables used to 
      CmdPkt Cmd;
      int CmdDataLen;
      byte[] CmdDataBuf = null;

      CmdList.set(CMD_FC_NOOP,  new CmdPkt(PREFIX_STR, "No Op", CMD_MID, CMD_FC_NOOP));
      CmdList.set(CMD_FC_RESET, new CmdPkt(PREFIX_STR, "Reset", CMD_MID, CMD_FC_RESET));
     
      CmdDataLen = 15;
      CmdDataBuf = new byte[CmdDataLen];
      CmdDataBuf[0] = 0x31; // 127.
      CmdDataBuf[1] = 0x32;
      CmdDataBuf[2] = 0x37;
      CmdDataBuf[3] = 0x2E;
      CmdDataBuf[4] = 0x30; // 000.
      CmdDataBuf[5] = 0x30;
      CmdDataBuf[6] = 0x30;
      CmdDataBuf[7] = 0x2E;
      CmdDataBuf[8] = 0x30; // 000.
      CmdDataBuf[9] = 0x30;
      CmdDataBuf[10] = 0x30;
      CmdDataBuf[11] = 0x2E;
      CmdDataBuf[12] = 0x30; // 01
      CmdDataBuf[13] = 0x30;
      CmdDataBuf[14] = 0x31;
      
      Cmd = new CmdPkt(PREFIX_STR, "Ena Tlm", CMD_MID, CMD_FC_ENA_TLM, CmdDataBuf, CmdDataLen);
      CmdList.set(CMD_FC_ENA_TLM,Cmd);

      // @todo - Error: The initial constructor doesn't allocate space for the command parameters!!
      CmdDataLen = 7;
      CmdDataBuf = new byte[CmdDataLen];
      CmdPkt AddPktCmd = new CmdPkt(PREFIX_STR, "Add Pkt", CMD_MID, CMD_FC_ADD_PKT, CmdDataBuf, CmdDataLen);
      AddPktCmd.addParam(new CmdParam("Message ID",0x0800, 2));  // // 3840 = 0xF00 (ExApp), 2048 = 0x800 (ES HK)
      AddPktCmd.addParam(new CmdParam("Pkt Size",50, 2));
      AddPktCmd.addParam(new CmdParam("SB QoS",0, 2));
      AddPktCmd.addParam(new CmdParam("Buffer Cnt",1, 1));
      CmdList.set(CMD_FC_ADD_PKT, AddPktCmd);
      
      CmdDataLen = 2;
      CmdDataBuf = new byte[CmdDataLen];
      CmdPkt RemPktCmd = new CmdPkt(PREFIX_STR, "Rem Pkt", CMD_MID, CMD_FC_REM_PKT, CmdDataBuf, CmdDataLen);
      RemPktCmd.addParam(new CmdParam("Message ID",3840, 2));
      CmdList.set(CMD_FC_REM_PKT, RemPktCmd);
      
   } // defineCmds

   public void defineTlm() {
      
      TlmList.add(TLM_MID_HK);
         
   } // defineTlm
  
   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      //return TLM_STR_TBD; 
      return null;
      
   } // getTlmStr
   
} // End class TlmOutput
