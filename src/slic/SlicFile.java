package slic;

/*
 *  Read a SLIC file and create a list of directories. 
 *  
 *  The intent of this slic package and its classes is to create a SLIC representation
 *  that is independent of the user's goals and the application's representation. For
 *  example if the user is only interested in C files or wants to group directories
 *  into a logical groups then this level of structure is maintained by the owner of
 *  the SlicFile.   
 */

import java.io.*;      
import utils.*;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


public class SlicFile
{

   List<SlicDir> dirList = new ArrayList<SlicDir>();  // List of all directories
   
   GenericTree<SlicDir>     dirTree = new GenericTree<SlicDir>();
   GenericTreeNode<SlicDir> rootDirNode;
   
   String  projStr;
   
   public SlicFile (File aFile, String project)
   {
      projStr = project;
      
      processFile(aFile);
      if (dirCount() > 0) printDirTree(rootDirNode);
   
   } // SlicFile() 
   
   public GenericTreeNode<SlicDir> getRootDirNode()
   {
      return rootDirNode;
      
   } // getDirTree()

   public GenericTree<SlicDir> getDirTree()
   {
      return dirTree;
      
   } // getDirTree()
   
   public List<SlicDir> getDirList()
   {
      return dirList;
      
   } // getDirList()

   public int dirCount()
   {
      return dirList.size();
      
   } // dirCount()

   protected void processFile(File aFile)
   {
   
      boolean captureDir = false;
      boolean firstDir = true;
      String dirLine = null;
      List<String> totalList = new ArrayList<String>();
      
      try 
      {
        //use buffering, reading one line at a time
        //FileReader always assumes default encoding is OK!
        BufferedReader inFile =  new BufferedReader(new FileReader(aFile));
        try 
        {
          String line = null; //not declared within while loop
          /*
          * readLine is a bit quirky :
          * it returns the content of a line MINUS the newline.
          * it returns null only for the END of the stream.
          * it returns an empty String if two newlines appear in a row.
          */
          while (( line = inFile.readLine()) != null)
          {

             if (line.indexOf(Slic.KEYWORD_FOLDER_START) > 0)
             {
                dirLine = line;
                captureDir = true;
                
             } // End if folder found

             if (captureDir)
             {
                
                if (line.indexOf(Slic.KEYWORD_LANG_TOTAL) > 0)
                {
                   totalList.add(line);
                }
                if (line.indexOf(Slic.KEYWORD_FOLDER_END) > 0)
                {
                   SlicDir slicDir = new SlicDir(dirLine, totalList, projStr);
                   System.out.println("SlicDir C = " + slicDir.getLangTotal(Slic.LANG_ABBR_C));
                   captureDir = false;
                   // This evolved. Originally had hierarchy and late change made it easy to repeat first directory
                   // and not break anything.
                   if (firstDir)
                   {
                      rootDirNode = new GenericTreeNode<SlicDir>(slicDir);
                      dirTree.setRoot(rootDirNode);
                      addChildDir(slicDir);
                      firstDir = false;
                   }
                   else
                   {
                      addChildDir(slicDir);
                   }
                   dirList.add(slicDir);
                   totalList.clear();
                }
                   
             } // End if capturing directory
                
          } // End while reading the file
          
        } // End read line try
        finally 
        {
          inFile.close();
        }
      } // End file try
      catch (IOException ex){
        ex.printStackTrace();
      }
      
   } // End processFile()

   /*
    * My original thought was to make this smart an directory groupings based
    * on path names. After some thought it seems better to originally create 
    * a single tree node with every directory part of the root. A user will be
    *  able to able create logical groupings and these groups should be retained 
    *  across executions.  
    *  
    *  This function may be removed once the application is built.
    */
   public void addChildDir(SlicDir slicDir)
   {
      rootDirNode.addChild(new GenericTreeNode<SlicDir>(slicDir));
      
   } // addChildDir()

   /*
    * Originally this was a utility to help addChildDir(). Now that I have gutted 
    * that function this function is not currently needed. No reason to delete it. 
    */
   public int compareDirFolders(SlicDir topDir, SlicDir newDir)
   {
   
      int      i, result;
      String   topDirPath[] = topDir.getDirPathFolders();
      String   newDirPath[] = newDir.getDirPathFolders();

      for (i=0; i < topDirPath.length; i++)
      {
         if (!topDirPath[i].equals(newDirPath[i]))
            break;
         
      } // End folder loop

      result = newDirPath.length - i;
      
      return result;
      
   } // compareDirFolders()
   
   public void printDirTree(GenericTreeNode<SlicDir> dirTreeNode)
   {
      
      System.out.println(dirTreeNode.getData().getDirStr());
      if (dirTreeNode.hasChildren())
      {
         List<GenericTreeNode<SlicDir>> childList = dirTreeNode.getChildren();;
         Iterator<GenericTreeNode<SlicDir>> childIt = childList.iterator();

         while(childIt.hasNext())
         {
            GenericTreeNode<SlicDir> nextNode = childIt.next();
            printDirTree(nextNode);
         }
         
      } // End if have children
      
   } // printDirTree()

   /** Simple test harness.   */
   public static void main (String... aArguments) throws IOException {
      
     File testFile = new File("C:\\dmccomas\\projects\\gnc-fsw-proto\\tools\\sloc\\test_sloc.txt");
     //File testFile = new File("C:\\dmccomas\\projects\\gnc-fsw-proto\\tools\\sloc\\gpm_fsw_b4_sloc_raw.txt");
     System.out.println("******** Before slic");
     SlicFile slic = new SlicFile(testFile, "Test");
     System.out.println("******** After slic: " + slic.getDirTree().getNumberOfNodes());
     System.out.println("path = " + new java.io.File(".").getCanonicalPath());


   } // End main()
   
} // End class SlicFile

