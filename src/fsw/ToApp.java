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
      CmdPkt cmdPkt;
      int    cmdDataLen;
      byte[] cmdDataBuf = null;

      CmdList.set(CMD_FC_NOOP,  new CmdPkt(PREFIX_STR, "No Op", CMD_MID, CMD_FC_NOOP, 0));
      CmdList.set(CMD_FC_RESET, new CmdPkt(PREFIX_STR, "Reset", CMD_MID, CMD_FC_RESET, 0));
     
      cmdDataLen = 15;
      cmdDataBuf = new byte[cmdDataLen];
      cmdDataBuf[0] = 0x31; // 127.
      cmdDataBuf[1] = 0x32;
      cmdDataBuf[2] = 0x37;
      cmdDataBuf[3] = 0x2E;
      cmdDataBuf[4] = 0x30; // 000.
      cmdDataBuf[5] = 0x30;
      cmdDataBuf[6] = 0x30;
      cmdDataBuf[7] = 0x2E;
      cmdDataBuf[8] = 0x30; // 000.
      cmdDataBuf[9] = 0x30;
      cmdDataBuf[10] = 0x30;
      cmdDataBuf[11] = 0x2E;
      cmdDataBuf[12] = 0x30; // 001
      cmdDataBuf[13] = 0x30;
      cmdDataBuf[14] = 0x31;
      cmdPkt = new CmdPkt(PREFIX_STR, "Ena Tlm", CMD_MID, CMD_FC_ENA_TLM, cmdDataBuf, cmdDataLen);
      CmdList.set(CMD_FC_ENA_TLM,cmdPkt);

/* The following doesn't work & not sure why
      cmdPkt = new CmdPkt(PREFIX_STR, "Ena Tlm", CMD_MID, CMD_FC_ENA_TLM, 15);  // One IP address parameter
      cmdPkt.addParam(new CmdStrParam("IP Address","127.000.000.001", 15));     
      CmdList.set(CMD_FC_ENA_TLM,cmdPkt);
*/
      cmdPkt = new CmdPkt(PREFIX_STR, "Add Pkt", CMD_MID, CMD_FC_ADD_PKT, 7);
      cmdPkt.addParam(new CmdIntParam("Message ID","2048", 2));  // // 3840 = 0xF00 (ExApp), 2048 = 0x800 (ES HK)
      cmdPkt.addParam(new CmdIntParam("Pkt Size","50", 2));
      cmdPkt.addParam(new CmdIntParam("SB QoS","0", 2));
      cmdPkt.addParam(new CmdIntParam("Buffer Cnt","1", 1));
      cmdPkt.loadParamList();
      CmdList.set(CMD_FC_ADD_PKT, cmdPkt);
      
      cmdPkt = new CmdPkt(PREFIX_STR, "Rem Pkt", CMD_MID, CMD_FC_REM_PKT, 2); // One 2 byte parameter
      cmdPkt.addParam(new CmdIntParam("Message ID","3840", 2));
      cmdPkt.loadParamList();
      CmdList.set(CMD_FC_REM_PKT, cmdPkt);
      
   } // defineCmds

   public void defineTlm() {
      
      TlmList.add(TLM_MID_HK);
         
   } // defineTlm
  
   public String getTlmStr(CcsdsTlmPkt TlmMsg) 
   {

      //return TLM_STR_TBD; 
      return null;
      
   } // getTlmStr

   public String[] getTlmStrArray(CcsdsTlmPkt TlmMsg) 
   {
      loadTlmStrArrayHdr(TlmMsg);
      
      return TlmStrArray;
      
   } // getTlmStrArray()

   
} // End class TlmOutput
