package slic;

/**
** SLIC.java
**
** Define global constants regarding the SLIC tool.
** 
*/

public class Slic
{

   /*****************************************************************************
   ** 
   ** Abbreviations used in output text file
   */ 

   static final public String LANG_ABBR_ASSEMBLER = "ASM";
   static final public String LANG_ABBR_C         = "C";
   static final public String LANG_ABBR_CPLUS     = "C++";
   static final public String LANG_ABBR_PYTHON    = "Python";
   static final public String LANG_ABBR_MATLAB    = "Matlab";
   static final public String LANG_ABBR_PERL      = "Perl";
   static final public String LANG_ABBR_SHELL     = "SH";
   static final public String LANG_ABBR_TOTAL     = "---";

   static final public String [] LANG = { 
      Slic.LANG_ABBR_ASSEMBLER,
      Slic.LANG_ABBR_C,
      Slic.LANG_ABBR_CPLUS,
      Slic.LANG_ABBR_PYTHON,
      Slic.LANG_ABBR_MATLAB,
      Slic.LANG_ABBR_PERL,
      Slic.LANG_ABBR_SHELL
      };

   /*****************************************************************************
   ** 
   ** Parsing Information
   */

   static final public String KEYWORD_FOLDER_START = "FOLDER:";
   static final public String KEYWORD_LANG_TOTAL   = "Folder TOTAL";
   static final public String KEYWORD_FOLDER_END   = "GRAND TOTAL";

   /*****************************************************************************
    ** 
    ** Constants
    */

    static final public String GROUP_TOTAL_DIR = "_TOTALS";
   
} // End class SLIC
