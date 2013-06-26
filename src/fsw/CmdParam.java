package fsw;

/*
** @todo - Add support for variable types, fields widths etc. Read-only?
** @author dmccomas
**
*/
public class CmdParam
{

   private String  Name;
   private int     Value;
   private int     DefValue;
   private int     NumBytes;
   
   public CmdParam(String Name, int DefValue, int NumBytes)
   {
      this.Name = Name;
      this.DefValue = DefValue;
      this.Value = DefValue;
      this.NumBytes = NumBytes;
      
   } // End CmdParam()
   
   public String getName() {
      return Name;
   }
   public void setName(String Name) {
      this.Name = Name;
   }

   public int getValue() {
      return Value;
   }
   public void setValue(int Value) {
      this.Value = Value;
   }
   
   public int getDefValue() {
      return DefValue;
   }
   public void setDefValue(int DefValue) {
      this.DefValue = DefValue;
   }
   
   public int getNumBytes() {
      return NumBytes;
   }
   public void setNumBytes(int NumBytes) {
      this.NumBytes = NumBytes;
   }
} // End class CmdParam
