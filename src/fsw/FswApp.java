/*
** Purpose: 
**
** @author dmccomas
**
** @todo - DefineCmds() - Allow XML file source 
** @todo - For TLM I allowed GUI class to render TLM 
*/
package fsw;

import java.util.ArrayList;  

import ccsds.*;

/*
** Base class for FSW application models
**
** Notes:
**  1. Name Convention: Start all command and telemetry related constants
**     with "CMD_" and "TLM_". Use second field to ID category like
**     function code (FC) or message ID (MID). For example "CMD_FC_" and
**     "CMD_MID_"
**
** TODO - Should error message methods be used? Allows app prefix etc. to be used.
** TODO - Should Tlm string helper functions be moved to TlmPkt class?
*/
public abstract class FswApp
{

   public static final int CMD_MAX = 20;
   public static final int TLM_MAX = 10;
   
   public static final int    NULL_CMD_INT = 99;
   public static final String NULL_APP_STR = "Null";
   public static final String NULL_CMD_STR = "Null Command";
   
   public static final String TLM_STR_TBD = "Telemetry message string method not implemented\n";
   public static final String TLM_STR_ERR = "Invalid telemetry message ID for this application";

   private String Prefix;   // Abbreviated application name (Upper case without underscore)
   private String Name;     // Full application name (Mixed case)
   
   protected ArrayList<CmdPkt>  CmdList;
   protected ArrayList<Integer> TlmList;  // TODO - Couple with CCSDS stream ID type 
   protected String[]           TlmStrArray;
   
   protected FswApp (String Prefix, String Name) {
      
      int i;
      CmdPkt NullCmd = new CmdPkt (NULL_APP_STR, NULL_CMD_STR, NULL_CMD_INT, NULL_CMD_INT, 0);

      this.Prefix = Prefix;
      this.Name   = Name;

      CmdList = new ArrayList<CmdPkt>(CMD_MAX);
      TlmList = new ArrayList<Integer>(TLM_MAX);
      TlmStrArray = new String[1024];  //@todo - Picked 1024 as safe max length
      
      for (i=0; i < CMD_MAX; i++)
         CmdList.add(NullCmd);
      
      //for (i=0; i < TLM_MAX; i++)
      //   CmdList.add(NullCmd);

      System.out.println("FswApp: Name = " + Name +", Prefix = " + Prefix);
      System.out.println("FswApp:CmdList size = " + CmdList.size());
      System.out.println("FswApp:TlmList size = " + TlmList.size());
     
      defineCmds();
      defineTlm();
      
      System.out.println("FswApp: Created " + Name);
      		
   } // End FswApp

   public abstract void defineCmds();
   public abstract void defineTlm();

   /*
   ** This method can be as simple or complex as a child class wants to make it. I 
   ** thought about putting a MsgID Hashtable in this base class that could be used
   ** to look up the method to generate a string (eventually a table for a GUI) but this
   ** may be overkill for some applications. Therefore I left it up to the child class
   ** to provide all of the implementation details (if-then, switch, etc.). 
   **
   ** There are some constant strings that should be used for reporting information. 
   */
   public abstract String getTlmStr(CcsdsTlmPkt TlmMsg);
  
   /*
    * This method is intended to simplify teh needs of a GUI. The application provides
    * a function that parses the packet and creates a string for each telemetry packet
    * element.
    * 
    */
   public abstract String[] getTlmStrArray(CcsdsTlmPkt TlmMsg);

   public ArrayList<CmdPkt> getCmdList() {
      
      return CmdList;
      
   } // getCmdList()
    
   public ArrayList<Integer> getTlmList() {
      
      return TlmList;
      
   } // getTlmList()

   public void setPrefix(String Prefix) {
      
      this.Prefix = Prefix;
      
   } // setPrefix()

   public String getPrefix() {
      
      return Prefix;
      
   } // getPrefix()

   public String getName() {
      
      return Name;
      
   } // getName()

   /***************************************************************************
    **
    ** Helper methods that provide generic TLM-to-String parsing
    **
    ** TODO - Make length variable with better line formats
    */
    
    static public String ParseRawData(byte[] RawData)
    {
       
       String Message = new String();

       Message = "\n---------------------------------\nPacket: App ID = 0x";
       Message += ByteToHexStr(RawData[1])+ ByteToHexStr(RawData[0]) + "\n";
       //return ( (( (Packet[CCSDS_IDX_STREAM_ID] & 0x00FF) | (Packet[CCSDS_IDX_STREAM_ID+1] << 8)) & CCSDS_MSK_MSG_ID) );

       //Truncate to 16 bytes since it's just a warm fuzzy
       int DataLen = 16;
       if (RawData.length < 16) DataLen = RawData.length;

       for (int i=0; i < DataLen; i++)
       {
          Message += ByteToHexStr(RawData[i]) + " ";
       }

       return Message;

    } // End ParseRawData()

    /*
    ** There's probably an easier way but I was having problems with sign extension
    ** and not knowing Java well I just hacked a solution.
    */
    
    static public String ByteToHexStr(byte Byte)
    {

       String HexStr = Integer.toHexString((short)Byte&0x00FF); // Need to mask to prevent long strings

       if (HexStr.length() == 1)
          HexStr = "0" + HexStr;

       else if (HexStr.length() > 2)
          HexStr = "**";
       
       return HexStr.toUpperCase();

    } // End ByteToHexStr()

    /***************************************************************************
     **
     ** TlmStrArray helper functions
     **
     ** 1. Use ArrayList for speed because it does not allocate a new array on the heap. 
     ** 2. Java doesn't have unsigned integers. Use 'long' that should be 64 bits and avoid sign bit
     */
     
     public void loadTlmStrArrayHdr(CcsdsTlmPkt TlmMsg)
     {
        
        TlmStrArray[0] = String.valueOf((TlmMsg.getStreamId()));
        TlmStrArray[1] = String.valueOf((TlmMsg.getSeqCount()));
        TlmStrArray[2] = String.valueOf((TlmMsg.getLength()));
        TlmStrArray[3] = "Seconds";    // @todo - Seconds
        TlmStrArray[4] = "SubSeconds"; // @todo - SubSeconds

     } // End loadTlmStrArrayHdr()

     public void loadTlmStrArrayUint8(int TlmStrIndex, 
                                     byte[] RawData,  int RawIndex)
     {
        
        TlmStrArray[TlmStrIndex] = String.valueOf( (0x00FF & RawData[RawIndex])); // I think this solves unsigned  
        
     } // End loadTlmStrArrayUint8()

     public void loadTlmStrArrayUint16(int TlmStrIndex, 
                                       byte[] RawData,  int RawIndex)
     {

        long longInt = 0x0000FFFF & (RawData[RawIndex]|((RawData[RawIndex+1] << 8)));
        TlmStrArray[TlmStrIndex] = String.valueOf(longInt);  

     } // End loadTlmStrArrayUint16()
     
     public void loadTlmStrArrayUint32(int TlmStrIndex, 
           byte[] RawData,  int RawIndex)
     {

        long longIntA = (RawData[RawIndex]   | ((RawData[RawIndex+1] << 8)));
        long longIntB = (RawData[RawIndex+2] | ((RawData[RawIndex+3] << 8)));
        long longInt  = (longIntA            | longIntB << 16);

        TlmStrArray[TlmStrIndex] = String.valueOf(longInt);  
 
     } // End loadTlmStrArrayUint32()
     
} // End class FswApp
   

 /***      
 if (TlmMsg[0] == 0x08 && TlmMsg[1] == 0x08)
 {
    
    String Message = new String(TlmMsg,44,122); // OS_MAX_API_NAME = 20, CFE_EVS_MAX_MESSAGE_LENGTH = 122
    String MsgStr  = Message.substring(0,Message.indexOf('\0')) + "\n";

    EventsText.append(MsgStr);

    //Some junk from CmdTlmApplet
    //String Message = new String(RawData,CcsdsCmdPkt.CCSDS_IDX_CMD_DATA+36,122); // OS_MAX_API_NAME = 20, CFE_EVS_MAX_MESSAGE_LENGTH = 122
    // Why did I use CMD constant for the data?
    //System.out.println("Indexof \n = " + Message.indexOf('\0'));
    //System.out.println("Got an event message. RawData length = " + RawData.length);
    //EventText.append(Message.substring(0,Message.indexOf('\0')) + "\n");
    //String Message = ParseDatagram(DataPacket);
    // See app_msgids.h, cfe_evs.h, osconfig.h (extra 4 bytes in evs msg not accounted for)

 } // End if x0808
 else
 {

    TlmText.append("MsgReader() - Received non-TLM\n");

 } // End if not 0x0808
 **/      


 /*
 * A couple of key App IDs:
 * 0x0841: Event
 * 0x0815: CI CLCW (don't print)
 * 0x080B: Fast HK (Don't print)
       
 ***/       
   
