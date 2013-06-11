package app;
/**
** ObjSaxHandler.java  
*
* This is similar to SlicFile in how the directory tree is created.
* 
*/
import java.util.*;  

import org.jdom2.Attribute; 
import org.xml.sax.*; 
import org.xml.sax.helpers.*;

import app.*;
import slic.*;
import utils.GenericTree;
import utils.GenericTreeNode;

public class SlocSaxHandler extends DefaultHandler
{

   private SlocProject   project;
   private SlocRelease   release;
   private boolean       buildingRelease;
   private String        groupName;
   private boolean       buildingGroup;
   private boolean       buildingRemDirList;
   private boolean       firstDir;
   
   List<String> totalList = new ArrayList<String>();;
   
   GenericTree<SlicDir>     dirTree = new GenericTree<SlicDir>();
   GenericTreeNode<SlicDir> rootDirNode;

   public SlocSaxHandler(SlocProject proj)
   {
      project = proj;
      buildingRelease = false;
      buildingGroup = false;
      buildingRemDirList = false;
      firstDir = true;
      
   } // End SlocSaxHandler()

   public void startElement(String uri, String name, String qName,
         Attributes attr) throws SAXException
   {

      System.out.println("startElement: " + name);

      if (name.equals(Sloc.XML_EL_ROOT))
      {
         project.setName(attr.getValue(Sloc.XML_AT_NAME));
         
      } // End Root Element

      else if (name.equals(Sloc.XML_EL_RELEASE))
      {
         
         System.out.println("Release " + attr.getValue(Sloc.XML_AT_VERSION) + ": " + buildingRelease + "," + buildingGroup);
         // If building a release then must have been building a group
         if (buildingRelease)
         {
            release.createGroup(groupName, rootDirNode.getChildren());  // Use name saved at group start
            project.addRelease(release);
         }
         
         release = new SlocRelease(attr.getValue(Sloc.XML_AT_VERSION));
         
         buildingRelease = true;
         buildingGroup = false;
         firstDir = true;
         
      } // End if Version Element
      else if (name.equals(Sloc.XML_EL_GROUP))
      {
    
         if (buildingGroup)
         {
            release.createGroup(groupName, rootDirNode.getChildren());  // Use name saved at group start
            
         }
         
         buildingGroup = true;
         groupName = attr.getValue(Sloc.XML_AT_NAME);
         
      } // End Group Element

      else if (name.equals(Sloc.XML_EL_DIR))
      {

         String       dirString;
         
         /*
          *  Recreate a slid directory total text line. Only logical lines are used
          *  
          *                                      COM-    LOG    PHY    RAW
          *    SOURCE FILE   LANG     FILE SIZE  MENTS   SLOC   SLOC   SLOC
          *
          *  Folder TOTAL      C       50.7 kB    915    257    398   1484 
          *
          */
          for (int i=0, n=Slic.LANG.length; i < n; i++) {
             dirString = "    " + Slic.KEYWORD_LANG_TOTAL + "   " +
                         Slic.LANG[i] +
                         "  00.0  kB  000  " + 
                         attr.getValue(Sloc.XML_AT_LANG[i]) +
                         "  000  000";
             totalList.add(dirString);
             //System.out.println("dirString: " + dirString);
          }

          SlicDir slicDir = new SlicDir("RESULTS FOR FOLDER: " + attr.getValue(Sloc.XML_AT_NAME),totalList,groupName);
          GenericTreeNode<SlicDir> dir = new GenericTreeNode<SlicDir>(slicDir);
          
          if (buildingGroup)
          {
             if (firstDir)
             {
                rootDirNode = dir;
                dirTree.setRoot(rootDirNode);
                rootDirNode.addChild(new GenericTreeNode<SlicDir>(slicDir));  // Can't use 'dir' or will create an infinite loop
                firstDir = false;
             }
             else
             {
                rootDirNode.addChild(dir);
             }
          } // End if buildingGroup
          else if (buildingRemDirList)
          {
             release.addToRemoveDirList(dir);
             
          } // End if buildingRemDir
          else
             throw new SAXException("Improper location of directory element");
          
          totalList.clear();

      } // End Directory element

      else if (name.equals(Sloc.XML_EL_TOTAL))
      {

         // Not currently used. Originally thought total directory would be tagged

      } // End Total Element

      else if (name.equals(Sloc.XML_EL_REMDIR))
      {

         // If building a release then must have been building a group
         if (buildingRelease || buildingGroup)
         {
            System.out.println("Removed directory: " + groupName + "," + buildingRelease + "," + buildingGroup +","+release+","+project);
            release.createGroup(groupName, rootDirNode.getChildren());  // Use name saved at group start
            project.addRelease(release);
         }
         else 
         {
            throw new SAXException("Remove directory element received without a preceeding Release and Group");
         }
            
         buildingRelease = false;
         buildingGroup = false;
         buildingRemDirList = true;
         firstDir = true;
         
      } // End Remove Directory Element

      else
         throw new SAXException("Element " + name + " not valid");

     
   } // End startElement()

   public void characters(char ch[], int start, int length) throws SAXException
   {
      String data = new String(ch, start, length).trim();
      if (data.length() > 0)
      {
         System.out.println("characters: " + data);
      }
      
   } // End characters()

   public void endElement(String uri, String name, String qName)
         throws SAXException
   {
      
   } // endElement()

} // End class ObjSaxHandler


