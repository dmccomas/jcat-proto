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
 * Contains one release
 * 
 */
public class SlocRelease
{

   String           verStr;
   List<SlocGroup>  groupList;

   List<GenericTreeNode<SlicDir>> removedDirList;  // Maintain list of removed directories for XML restoration

   /*
    * This constructor is used when starting with a SLIC file since you have a default group 
    */
   public SlocRelease(String version, SlocGroup group)
   {
      verStr  = version;
      groupList = new ArrayList<SlocGroup>(); 
      groupList.add(group);
      computeGroupTotals();

      removedDirList = new ArrayList<GenericTreeNode<SlicDir>>();
      
   } // End SlocProject() 

   /*
    * This constructor is used when starting creating releases from an XML file. Groups are added
    * as the XML file is processed.
    */
   public SlocRelease(String version)
   {
      verStr  = version;
      groupList = new ArrayList<SlocGroup>(); 

      removedDirList = new ArrayList<GenericTreeNode<SlicDir>>();
      
   } // End SlocProject() 

   public void computeGroupTotals(){
      
      
      Iterator<SlocGroup> groupIt = groupList.iterator();
      while(groupIt.hasNext())
      {
         SlocGroup group = groupIt.next();
         // Slic.GROUP_TOTAL_DIR is used as a special label so when this function is called after a toatl
         // directory has been created once, the directory will be replaced by a new totals directory
         SlicDir groupDirTotal = new SlicDir(null, null, (group.getName() + Slic.GROUP_TOTAL_DIR).toUpperCase());
         boolean firstDir = true;
         HashMap<String,Integer> groupLangTotal = new HashMap<String,Integer>();
         List<GenericTreeNode<SlicDir>> dirList = group.getDirList();     
         
         GenericTreeNode<SlicDir> savedTotalDir = null;
         Iterator<GenericTreeNode<SlicDir>> dirIt = dirList.iterator();
         while(dirIt.hasNext()) {

            GenericTreeNode<SlicDir> dir = dirIt.next();
            //System.out.println("computeTotals dir = " + dir.getData().getDirStr());
            if (dir.getData().getDirStr().endsWith(Slic.GROUP_TOTAL_DIR))
            {
               // If I deleted immediately I got a concurrent access exception.
               savedTotalDir = dir;
            }
            else {
               // Language totals are zeroed when directory created
               if (firstDir) {
                  for (int i=0, n=Slic.LANG.length; i < n; i++)
                     groupLangTotal.put(Slic.LANG[i], dir.getData().getLangTotal(Slic.LANG[i]));
                  firstDir = false;
               }
               else {
                  for (int i=0, n=Slic.LANG.length; i < n; i++)
                     groupLangTotal.put(Slic.LANG[i], groupLangTotal.get(Slic.LANG[i]) + dir.getData().getLangTotal(Slic.LANG[i]));
               }
            } // End if not group total
               
         } // End dir loop
         
         // Protect against an empty group
         if (groupLangTotal.isEmpty()) {
            for (int i=0, n=Slic.LANG.length; i < n; i++)
               groupDirTotal.setLangTotal(Slic.LANG[i], 0);
         }
         else {
            for (int i=0, n=Slic.LANG.length; i < n; i++)
               groupDirTotal.setLangTotal(Slic.LANG[i], groupLangTotal.get(Slic.LANG[i]));
         }
          group.addDir(groupDirTotal);
         if (savedTotalDir != null)
            group.removeDir(savedTotalDir);
         
      } // End group loop
 
   } // End computeGroupTotals()
   

   public void createReport(File reportFile, boolean groupOnly) {
      
      try {
         
         FileWriter outFile = new FileWriter(reportFile); 

         try {
            
            //Write first line describing contents
            String outLine = "group,directory";
            for (int i=0, n=Slic.LANG.length; i < n; i++)
               outLine += "," + Slic.LANG[i];
            outFile.write(outLine + "\n"); 
            
            Iterator<SlocGroup> groupIt = groupList.iterator();
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
   

   public List<SlocGroup> getGroupList ()
   {
      
      return groupList;
      
   }
 
   /*
    *  Loop through groups and remove directory when found directory passed in. This
    *  is used by GUI when user selects a directory to remove
    */
   public boolean removeSlicDir(GenericTreeNode<SlicDir> dir) {
      
      Iterator<SlocGroup> groupIt = groupList.iterator();
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
   
   public void addToRemoveDirList(GenericTreeNode<SlicDir> dir) {
      
            removedDirList.add(dir);  
      
   }

   
   public List<GenericTreeNode<SlicDir>> getRemovedDirList()
   {
      return removedDirList;
   }
    
   public void createGroup (String groupName, List dirList) {
   
      SlocGroup group = new SlocGroup(groupName);
      groupList.add(group);
      changeDirGroup (groupName, dirList);
      
   }
   
   public void changeDirGroup (String groupName, List dirList) {

      for (Iterator dirIt = dirList.iterator(); dirIt.hasNext();) {
         Object dirObj = (Object) dirIt.next(); 
         // Ignore non-directories (e.g. could be a group)
         if (dirObj instanceof GenericTreeNode) {
            GenericTreeNode<SlicDir> dir = ((GenericTreeNode<SlicDir>)dirObj); 
            removeSlicDir(dir);
            dir.getData().SetDirGroup(groupName);
            getSlocGroup(dir.getData().getDirGroup()).addDir(dir);
         }
         
      } // End dirList loop
      
      computeGroupTotals();
      
   } // End changeDirGroup()
   
   SlocGroup getSlocGroup(String groupName){
      
      Iterator<SlocGroup> groupIt = groupList.iterator();
      while(groupIt.hasNext())
      {
         SlocGroup group = groupIt.next();
         if (group.getName().compareTo(groupName) == 0)
            return group;
      }
      
      return null;
      
   } // End get SlocGroup()

   public boolean renameGroup (String oldGroupName, String newGroupName) {
      
      SlocGroup group = getSlocGroup(oldGroupName);
      
      // Check for bad parameter or duplicate group name
      if (group == null) return false;
      if (getSlocGroup(newGroupName) != null)  return false;
      
      List<GenericTreeNode<SlicDir>> dirList = group.getDirList();     
      for (Iterator<GenericTreeNode<SlicDir>> dirIt = dirList.iterator(); dirIt.hasNext();) {
         GenericTreeNode<SlicDir> dir = dirIt.next();
         dir.getData().SetDirGroup(newGroupName);
      }
      group.setName(newGroupName);
      
      computeGroupTotals();  // Force new group name to be used in total directory
      
      return true;
      
   } // End renameGroup()
   
   public String getVersion () {

      return verStr;
      
   } // End getVersion()

   public void setVersion (String version) {

      verStr = version;
      
   } // End setProjName()

   public void printRemovedDirList()
   {
      
      System.out.println("Version " + verStr + "'s removed directories: \n");
      
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
