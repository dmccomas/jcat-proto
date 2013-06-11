package app;

import java.io.*;   
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import slic.*;
import utils.GenericTreeNode;

/*
 * Manage a project's slic file. This class imposes structure on the slic file directories. The
 * SlicFile class simple creates a list of slic directories. This class allows the user to
 * logically group directories.
 * 
 * Each release can have a removed directory list but only the most recent release's removed
 * directory list is sued. 
 * 
 * Two constructors are provided:
 * 1. First time project's pass the project name and all of the SLIC file directories
 *    are placed in a default group with the name of the project.
 * 2. A project XML file is used to retain a project's organization across executions.
*/
public class SlocProject
{

   private        SlicFile  slicFile;
   private static XMLReader xmlReader;
   
   private String  projStr;
   
   private List<SlocRelease>              releaseList;     // List of version releases
   private List<GenericTreeNode<SlicDir>> removedDirList;  // Maintain list of removed directories for XML restoration

   
   /*
    * This constructor is used to create a new project starting with a SLIC file. Each SLIC file can only
    * have one release so the releaseList is initialized.
    */
   public SlocProject(File slicFileName, String project, String version)
   {
      projStr = project;

      removedDirList = new ArrayList<GenericTreeNode<SlicDir>>();
      releaseList    = new ArrayList<SlocRelease>();

      slicFile = new SlicFile(slicFileName, project);
      if (slicFile.dirCount() > 0)
      {
         releaseList.add(new SlocRelease(version, new SlocGroup(project, slicFile.getRootDirNode().getChildren())));
      }
               
   } // End SlocProject() 

   public SlocProject(File xmlFile)
   {

      slicFile = null;
      removedDirList = new ArrayList<GenericTreeNode<SlicDir>>();
      releaseList    = new ArrayList<SlocRelease>();
      
      // XML file will fill in the details and create groups
      try
      {
         // SAX parser initialization
         SAXParserFactory spf = SAXParserFactory.newInstance();
         spf.setNamespaceAware(true);
         SAXParser saxParser = spf.newSAXParser();
         xmlReader = saxParser.getXMLReader();
         xmlReader.setContentHandler(new SlocSaxHandler(this));

         FileInputStream input = new FileInputStream(xmlFile);
         xmlReader.parse(new InputSource(input));
         input.close();
    
         removedDirList = getCurrRelease().getRemovedDirList();
            
         System.out.println("SlocProject constructor: " + this.getName());
         
      } // End try
      catch (Exception e)
      {
         System.out.println(e);
      }
      
      System.out.println("SlocProject constructor: " + this.getName());

      printRemovedDirList();
      
   } // End SlocProject()

   
   public void createReport(File reportFile, boolean groupOnly) {
      
      try {
         
         FileWriter outFile = new FileWriter(reportFile); 

         try {
            
            //Write first line describing contents
            String outLine = "group,directory";
            for (int i=0, n=Slic.LANG.length; i < n; i++)
               outLine += "," + Slic.LANG[i];
            outFile.write(outLine + "\n"); 
            
            Iterator<SlocGroup> groupIt = getGroupList().iterator();
            while(groupIt.hasNext())
            {
 
               SlocGroup group = groupIt.next();

               if (groupOnly){
                
                  List<GenericTreeNode<SlicDir>> dirList = group.getDirList();     
                  Iterator<GenericTreeNode<SlicDir>> dirIt = dirList.iterator();
                  GenericTreeNode<SlicDir> dir = null;
                  // Last directory has group totals
                  while(dirIt.hasNext()) dir = dirIt.next();
                  if (dir != null){
                     outLine = group.getName() + "," + dir.getData().getDirStr();
                     System.out.println(outLine);
                     for (int i=0, n=Slic.LANG.length; i < n; i++) {
                        outLine += "," + dir.getData().getLangTotalStr(Slic.LANG[i]);
                     }
                     outFile.write(outLine + "\n"); 
                  }
                  
               } // End if groupOnly
               else {
                  
                  List<GenericTreeNode<SlicDir>> dirList = group.getDirList();     
                  Iterator<GenericTreeNode<SlicDir>> dirIt = dirList.iterator();
                  while(dirIt.hasNext()) {
   
                     GenericTreeNode<SlicDir> dir = dirIt.next();
                     outLine = group.getName() + "," + dir.getData().getDirStr();
                     System.out.println(outLine);
                     for (int i=0, n=Slic.LANG.length; i < n; i++) {
                        outLine += "," + dir.getData().getLangTotalStr(Slic.LANG[i]);
                     }
   
                     outFile.write(outLine + "\n"); 
                             
                  } // End dir loop
               } // End if !groupOnly
         
            } // End group loop
 
         } // End write try
         finally 
         {
            outFile.close(); 
         }
      } // End file try
      catch (IOException ex){
        ex.printStackTrace();
      }
      
   } // End createReport()
   

   public int releaseCount()
   {
      return releaseList.size();
      
   } // End releaseCount()

   public SlocRelease getCurrRelease()
   {
      return releaseList.get(releaseList.size()-1);
      
   } // End getCurrRelease()

   
   // Get list of releases
   public List<SlocRelease> getReleaseList()
   {
      return releaseList;
   }
   
   // Get the group list for the current release
   public List<SlocGroup> getGroupList()
   {
      return getCurrRelease().getGroupList();
   }
 
   public List<GenericTreeNode<SlicDir>> getRemovedDirList()
   {
      return removedDirList;
   }
   
   /*
    *  Loop through groups and remove directory when found directory passed in. This
    *  is used by GUI when user selects a directory to remove
    */
   public boolean removeSlicDir(GenericTreeNode<SlicDir> dir) {
      
      Iterator<SlocGroup> groupIt = getGroupList().iterator();
      while(groupIt.hasNext())
      {
         List<GenericTreeNode<SlicDir>> dirList = groupIt.next().getDirList();     
         if (dirList.remove(dir))
         {
            removedDirList.add(dir);  
            return true;
         }
      }
      return false;
      
   }

   /* Add the passed in directory to the removed directory list. This is 
    * used when restoring a project from an XML file.
    */
   
   public void addRelease(SlocRelease release) {
      
      releaseList.add(release);  
      
   } // End addRelease()

   /* Add the passed in directory to the removed directory list. This is 
    * used when restoring a project from an XML file.
    */
   
   public void addToRemoveDirList(GenericTreeNode<SlicDir> dir) {
      
      removedDirList.add(dir);  
      
   }

   public SlicFile getSlicFile ()
   {
      return slicFile;
      
   }

   public void computeGroupTotals() {
      
      getCurrRelease().computeGroupTotals();
      
   } // End computeGroupTotals()
   
   public void changeDirGroup (String groupName, List dirList) {
      
      getCurrRelease().changeDirGroup(groupName, dirList);
      
   } // End changeDirGroup()
   
   public void createGroup (String groupName, List dirList) {
   
      getCurrRelease().createGroup(groupName, dirList);
      
   } // End createGroup()
      
   SlocGroup getSlocGroup(String groupName){
      
      Iterator<SlocGroup> groupIt = getGroupList().iterator();
      while(groupIt.hasNext())
      {
         SlocGroup group = groupIt.next();
         if (group.getName().compareTo(groupName) == 0)
            return group;
      }
      
      return null;
      
   } // End get SlocGroup()

   public boolean renameGroup (String oldGroupName, String newGroupName) {
      
      return getCurrRelease().renameGroup(oldGroupName, newGroupName);
      
   } // End renameGroup()
   
   public String getName () {

      return projStr;
      
   } // End getName()

   public void setName (String project) {

      projStr = project;
      
   } // End setName()

   // Return version of current release
   public String getVersion () {

      return getCurrRelease().getVersion();
      
   } // End getVersion()

   public void setVersion (String version) {

      getCurrRelease().setVersion(version);
      
   } // End setVersion()

   public void printRemovedDirList()
   {
      
      System.out.println(projStr + "'s removed directories: \n");
      
      Iterator<GenericTreeNode<SlicDir>> dirIt = removedDirList.iterator();
      while(dirIt.hasNext()) {

         GenericTreeNode<SlicDir> dir = dirIt.next();
         printDirTree(dir);
            
      } // End dir loop
      
   } // printRemovedDirList()

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
   
} // End class SlocProject
