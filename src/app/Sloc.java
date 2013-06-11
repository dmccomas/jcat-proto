package app;
/**
** SLOC.java
**
** Define global constants
** 
** All classes should use these definitions to reduce coupling to a single point.
** XML elements and attribute names should be defined here.
** 
*/
import slic.*;

public class Sloc
{


   /*****************************************************************************
	** 
	** XML attributes and elements
	*/

   // - Elements

   static final public String XML_EL_ROOT    = "SlocProject";
   static final public String XML_EL_RELEASE = "Release";
   static final public String XML_EL_GROUP   = "Group";
   static final public String XML_EL_DIR     = "Dir";
   static final public String XML_EL_TOTAL   = "Total";
   static final public String XML_EL_REMDIR  = "RemovedDir";
   
	// - Common Attributes

   static final public String XML_AT_NAME    = "name";
   static final public String XML_AT_VERSION = "version";

   static final public String XML_AT_LANG_ASSEMBLER = "asm";
   static final public String XML_AT_LANG_C         = "c";
   static final public String XML_AT_LANG_CPLUS     = "cplus";                   // Can't have '++' in name
   static final public String XML_AT_LANG_PYTHON    = "python";
   static final public String XML_AT_LANG_MATLAB    = "matlab";
   static final public String XML_AT_LANG_PERL      = "perl";
   static final public String XML_AT_LANG_SHELL     = "sh";
   static final public String XML_AT_LANG_TOTAL     = "total";
   
   // Must agree with Slic language array
   static final public String [] XML_AT_LANG = { 
      XML_AT_LANG_ASSEMBLER,
      XML_AT_LANG_C,
      XML_AT_LANG_CPLUS,
      XML_AT_LANG_PYTHON,
      XML_AT_LANG_MATLAB,
      XML_AT_LANG_PERL,
      XML_AT_LANG_SHELL,
      XML_AT_LANG_TOTAL
      };

} // End class Sloc
