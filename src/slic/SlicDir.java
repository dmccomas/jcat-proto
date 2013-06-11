package slic;

/*
 * 
** @author dcmccom2    
**
** This class contains the relevant information from a SLIC output file. Currently
** the individual file details are not retained.
**
** The following is an example SLIC output:
_____________________________________________________________________________
RESULTS FOR FOLDER: .

                                                    COM-    LOG    PHY    RAW
                 SOURCE FILE   LANG     FILE SIZE  MENTS   SLOC   SLOC   SLOC
                 -----------   ----     ---------  -----   ----   ----   ----
                  setvars.sh     SH        4.1 kB     94      7      7    112

                _____________________________________________________________
                Folder TOTAL     SH        4.1 kB     94      7      7    112
          Folder GRAND TOTAL    ---        4.1 kB     94      7      7    112

_____________________________________________________________________________
RESULTS FOR FOLDER: ./apps/cdh_lib/fsw/src

                                                    COM-    LOG    PHY    RAW
                 SOURCE FILE   LANG     FILE SIZE  MENTS   SLOC   SLOC   SLOC
                 -----------   ----     ---------  -----   ----   ----   ----
                   cdh_dio.c      C       10.5 kB    140    106    157    338
                   cdh_dsb.c      C      110.1 kB   1376   1111   1716   3706
                   cdh_dsb.h      C       23.7 kB    333    257    298    901
               cdh_filesys.c      C       11.7 kB    197     93    134    363
                 cdh_instr.c      C       20.0 kB    332    184    298    724
               cdh_libinit.c      C        3.3 kB     99     20     26    153
                cdh_packet.c      C        7.8 kB    157     38     55    260
          cdh_pagedramdisk.h      C        4.4 kB     76     42     46    112
              cdh_pidreset.c      C       10.6 kB    179    106    138    357
               cdh_ramdisk.c      C       13.0 kB    129    144    243    416
                cdh_schapi.c      C        1.7 kB     37      9     17     64
                   cdh_ssr.c      C      128.8 kB   1269   1267   1878   3769
                   cdh_ssr.h      C       11.8 kB    185     99    120    364
           cdh_ssrstrategy.c      C       23.3 kB    248    175    273    650
           cdh_ssrstrategy.h      C        3.4 kB     77     13     22    124
                 cdh_tmapi.c      C        3.4 kB     50     20     26     95
                 cdh_toapi.c      C        1.0 kB     27      6     10     47

                _____________________________________________________________
                Folder TOTAL      C      388.5 kB   4911   3690   5457  12443
          Folder GRAND TOTAL    ---      388.5 kB   4911   3690   5457  12443


 */

import java.util.*;  
import java.util.regex.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;


public class SlicDir
{

   String   dirGroup;          // Each directory can be assigned a group name
   String   dirStr;
   String   dirPathFolders[];   // List of folder names in the path
   HashMap<String,Integer> langTotal = new HashMap<String,Integer>();
   
   // Dummy for testing
   public SlicDir ()
   {
      
      dirGroup = "null";
      dirStr = "null";
      initLangTotal();
      
   } // SlicDir()

   /*
    * Decided not to pass all of the SLIC information to keep this simple. Can easily
    * be expanded as needed. 
    */
   public SlicDir (String dirLine, List<String> totalList, String group)
   {
      
      dirGroup = group;
      initLangTotal();
      
      System.out.println("SlicDir(): " + dirLine);
      // A null dirLine is a special case that allows the caller to set a specific directory name
      if (dirLine != null) {

         dirStr = dirLine.substring(dirLine.indexOf(Slic.KEYWORD_FOLDER_START)+Slic.KEYWORD_FOLDER_START.length()+1,
               dirLine.length());
         System.out.println("SlicDir(): " + dirStr);
         dirPathFolders = dirStr.split("/");
         parseTotalList(totalList);

      }
      else
         dirStr = group;
         
   } // SlicDir()

   /*
    * Here's examples of the tokens after split with white space removal:
     
      Total Line:                Folder TOTAL      C      388.5 kB   4911   3690   5457  12443
      Token length = 10
      Token[0]=
      Token[1]=Folder
      Token[2]=TOTAL
      Token[3]=C
      Token[4]=388.5
      Token[5]=kB
      Token[6]=4911
      Token[7]=3690
      Token[8]=5457
      Token[9]=12443
      Total Line:          Folder GRAND TOTAL    ---      388.5 kB   4911   3690   5457  12443
      Token length = 11
      Token[0]=
      Token[1]=Folder
      Token[2]=GRAND
      Token[3]=TOTAL
      Token[4]=---
      Token[5]=388.5
      Token[6]=kB
      Token[7]=4911
      Token[8]=3690
      Token[9]=5457
      Token[10]=12443
   */
   
   public void parseTotalList(List<String> totalList)
   {
      Iterator<String> total = totalList.iterator();
      Pattern noWhite = Pattern.compile("[,\\s]+");
  
      while(total.hasNext())
      {
        String totalLine = total.next();
        String tokens[] = noWhite.split(totalLine);
        
        /*
        System.out.println("Total Line:" + totalLine);
        System.out.println("Token length = " + tokens.length);
        for (int i=0; i < tokens.length; i++)
        {
           System.out.println("Token["+i+"]="+tokens[i]);
        }
        */
        // Use language keyword token[3|4] for hash table
        if (tokens.length == 10) // Language total
        {
           langTotal.put(tokens[3],Integer.parseInt(tokens[7]));
        }
        else // Assume grand total
        {
           langTotal.put(tokens[4],Integer.parseInt(tokens[8]));
    
        }
        
      } // End while total 
   } // End parseTotalList()
   
   public void initLangTotal()
   {

      for (int i=0, n=Slic.LANG.length; i < n; i++) {
         langTotal.put(Slic.LANG[i], 0);
      }
      langTotal.put(Slic.LANG_ABBR_TOTAL,     0);

   } // End initLangTotal()

   public String getDirStr() {
      return dirStr;
  }
   
   public String[] getDirPathFolders()
   {
      return dirPathFolders;
   }

   public Integer getLangTotal(String lang)
   {
      return langTotal.get(lang);
   }

   public void setLangTotal(String lang, Integer total)
   {
      langTotal.put(lang, total);
   }

   public String getLangTotalStr(String lang)
   {
      return langTotal.get(lang).toString();
   }

   public String getDirGroup()
   {
      return dirGroup;
   }
   
   public void SetDirGroup(String group)
   {
      dirGroup = group;
   }
   
} // End class SlicDir
