package fsw;

import java.util.ArrayList; 

import ccsds.CcsdsCmdPkt;

/*
** 
** @author dmccomas
**
*/
public class CmdPkt
{
   
   private String  AppPrefix;
   private String  Name;
   private ArrayList<CmdParam>  ParamList = new ArrayList<CmdParam>();

   CcsdsCmdPkt CmdPkt;
   
   /*
   ** Constructor - No command parameters
   */
   public CmdPkt (String AppPrefix, String Name, int MsgId, int FuncCode) {
   
      this.AppPrefix = AppPrefix;
      this.Name      = Name;
      
      CmdPkt = new CcsdsCmdPkt(MsgId, CcsdsCmdPkt.CCSDS_CMD_HDR_LEN, FuncCode);
      CmdPkt.ComputeChecksum();
      
   } // End CmdPkt()
   
   /*
   ** Constructor - With command parameters
   */
    public CmdPkt (String AppPrefix, String Name, Integer MsgId, Integer FuncCode, byte[] DataBuf, int DataLen ) {

       this.AppPrefix = AppPrefix;
       this.Name      = Name;
       
       CmdPkt = new CcsdsCmdPkt(MsgId,CcsdsCmdPkt.CCSDS_CMD_HDR_LEN + DataLen,FuncCode);
       CmdPkt.LoadData(DataBuf, DataLen);

    } // End CmdPkt()

   
   public CcsdsCmdPkt LoadParams(byte[] DataBuffer, int DataLen)
   {      

      if (DataLen > 0)
      {
         CmdPkt.LoadData(DataBuffer,DataLen);
      }
     
      CmdPkt.ComputeChecksum();

      return CmdPkt;
     
   } // LoadParams()

   public CcsdsCmdPkt LoadParams(ArrayList<CmdParam>  ParamList)
   {      

      byte[] DataBuffer = null;

      if (!ParamList.isEmpty())
      {
         int i, DataLen=0, DataIndex=0;
      
         for (i=0; i < ParamList.size(); i++)
         {
            DataLen += ParamList.get(i).getNumBytes();
            System.out.println("CmdPkt::LoadParams() - Param["+i+"]="+ParamList.get(i).getValue());
         }
               
         DataBuffer = new byte[DataLen];
        
         for (i=0; i < ParamList.size(); i++)
         {
            if (ParamList.get(i).getNumBytes() == 1)
            {
               System.out.println("CmdPkt::LoadParams() - 1 byte parameter"); 
               DataBuffer[DataIndex++] = new Integer(ParamList.get(i).getValue()).byteValue();
               System.out.println("DataBuffer["+(DataIndex-1)+"]="+DataBuffer[DataIndex-1]);
            }
            else if (ParamList.get(i).getNumBytes() == 2)
            {
               System.out.println("CmdPkt::LoadParams() - 2 byte parameter"); 
               int Temp = ParamList.get(i).getValue();
               DataBuffer[DataIndex++] = new Integer(Temp & 0xFF).byteValue();
               DataBuffer[DataIndex++] = new Integer((Temp & 0xFF00 ) >> 8).byteValue();
               System.out.println("DataBuffer["+(DataIndex-2)+"]="+DataBuffer[DataIndex-2]);
               System.out.println("DataBuffer["+(DataIndex-1)+"]="+DataBuffer[DataIndex-1]);
            }
            else
            {
            
               // @todo - Resolve illegal parameter bytes definition              
            
            }
              
           } // End parameter loop
           
         CmdPkt.LoadData(DataBuffer,DataLen);
           
      } // ParamList not empty
      else
      {
         // Currently default to a null buffer             
      }

     
      CmdPkt.ComputeChecksum();

      return CmdPkt;
     
   } // LoadParams()
   
   
   public CcsdsCmdPkt getCcsdsPkt()
   {      
      return CmdPkt;
   
   } // getCcsdsPkt()
   
   public String getAppPrefix()
   {      
      return AppPrefix;
   
   } // getAppPrefix()

   public String getName()
   {      
      return Name;
   
   } // getName()

   public void addParam(CmdParam Param)
   {      
      ParamList.add(Param);
   
   } // addParam()

   public ArrayList<CmdParam> getParamList()
   {      
      return ParamList;
   
   } // getParamList()

   public boolean hasParam()
   {      
      return !ParamList.isEmpty();
   
   } // hasParam()

   /*
   ** Create a command parameter byte array that can easily be used with CCSDS Packet
   ** methods.
   ** 
   ** CmdParam   - String of comma separated
   ** ParamBytes - Number of bytes in each parameter
   ** ParamCnt   - Number of parameters  
   */
   byte[] createCmdByteArray(String CmdParam, int ParamBytes[], int ParamCnt)
   {      
      byte[] DataBuffer = null;

      String [] Param = CmdParam.split(",");
     
      if (Param.length == ParamCnt)
      {
         int i, DataLen=0, DataIndex=0;
      
           
         for (i=0; i < ParamCnt; i++)
         {
            DataLen += ParamBytes[i];
            System.out.println("Param["+i+"]="+Param[i]);
         }
               
         DataBuffer = new byte[DataLen];
        
         for (i=0; i < ParamCnt; i++)
         {
            if (ParamBytes[i] == 1)
            {
               DataBuffer[DataIndex++] = new Integer(Integer.parseInt(Param[i])).byteValue();
               System.out.println("DataBuffer"+(DataIndex-1)+"]="+DataBuffer[DataIndex-1]);
            }
            else if (ParamBytes[i] == 2)
            {
               int Temp = Integer.parseInt(Param[i]);
               DataBuffer[DataIndex++] = new Integer(Temp & 0xFF).byteValue();
               DataBuffer[DataIndex++] = new Integer((Temp & 0xFF00 ) >> 8).byteValue();
               System.out.println("DataBuffer"+(DataIndex-2)+"]="+DataBuffer[DataIndex-2]);
               System.out.println("DataBuffer"+(DataIndex-1)+"]="+DataBuffer[DataIndex-1]);
            }
            else
            {
            
               // @todo - Resolve illegal parameter bytes definition              
            
            }
              
           } // End parameter loop
           
      }
      else
      {
         // Currently default to a null buffer             
      }

     return DataBuffer;
       
   } //  LoadDataBuf()
   
} // End class CmdPkt