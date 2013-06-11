package app;
/**
** SlocXml   
**
** Notes:
** 1. Coding Style: El is used to temporarily hold an element and can be reused within a code
**    block. XxxEl is used to hold a specific element and should not be reassigned. 
*/

import java.io.*;  
import java.util.Iterator;
import java.util.List;

import org.jdom2.*;
import org.jdom2.output.*;

import slic.Slic;
import slic.SlicDir;
import utils.GenericTreeNode;


/*
** Manage an application's XML document. The user of this class
** does not need to know any XML details so any XML changes should not ripple 
** past this class. This class has knowledge application classes and/or models
** so changes to these may impact this class.
*/
public class SlocToXml
{

   static private Element Root;
   static private Document Document;

   public SlocToXml(SlocProject project)
   {

      Root = new Element(Sloc.XML_EL_ROOT);
      Root.setAttribute(new Attribute(Sloc.XML_AT_NAME, project.getName()));
      Root.setAttribute(new Attribute(Sloc.XML_AT_VERSION, project.getVersion()));
      Document = new Document(Root);
      
      Element El;
      
      List<SlocRelease> releaseList = project.getReleaseList();

      System.out.println("slocToXml:: Release = " + releaseList.size());
      Iterator<SlocRelease> releaseIt = releaseList.iterator();
      while(releaseIt.hasNext())
      {
         System.out.println("Processing release directory");
         SlocRelease release = releaseIt.next();

         El = new Element(Sloc.XML_EL_RELEASE);
         El.setAttribute(new Attribute(Sloc.XML_AT_VERSION, release.getVersion()));
         processRelease(El, release);
         Root.addContent(El);

      } // End release loop

      El = new Element(Sloc.XML_EL_REMDIR);
      processRemovedDirList(El, project.getRemovedDirList());
      Root.addContent(El);
      
   } // End SlocXml()

   
   public void processRelease(Element releaseEl, SlocRelease release)
   {
      Element El;
      List<SlocGroup> groupList = release.getGroupList();
      
      Iterator<SlocGroup> groupIt = groupList.iterator();
      while(groupIt.hasNext())
      {
         SlocGroup group = groupIt.next();

         El = new Element(Sloc.XML_EL_GROUP);
         El.setAttribute(new Attribute(Sloc.XML_AT_NAME, group.getName()));
         processGroup(El, group);
         releaseEl.addContent(El);

      } // End group loop

   } // End processRelease()
   
   public void processGroup(Element groupEl, SlocGroup group)
   {
      
      List<GenericTreeNode<SlicDir>> dirList = group.getDirList();     
      Iterator<GenericTreeNode<SlicDir>> dirIt = dirList.iterator();
                  
      while(dirIt.hasNext()) 
      {
   
         GenericTreeNode<SlicDir> dir = dirIt.next();
         Element El = new Element(Sloc.XML_EL_DIR);
         El.setAttribute(new Attribute(Sloc.XML_AT_NAME, dir.getData().getDirStr()));
         
         for (int i=0, n=Slic.LANG.length; i < n; i++) {
            El.setAttribute(new Attribute(Sloc.XML_AT_LANG[i], dir.getData().getLangTotalStr(Slic.LANG[i])));   
         }
         groupEl.addContent(El);

      } // End dir loop
      
   } // End processGroup()

   
   public void processRemovedDirList(Element removedDirEl, List<GenericTreeNode<SlicDir>> dirList)
   {
      
      Iterator<GenericTreeNode<SlicDir>> dirIt = dirList.iterator();
                  
      while(dirIt.hasNext()) 
      {
   
         GenericTreeNode<SlicDir> dir = dirIt.next();
         Element El = new Element(Sloc.XML_EL_DIR);
         El.setAttribute(new Attribute(Sloc.XML_AT_NAME, dir.getData().getDirStr()));
         
         for (int i=0, n=Slic.LANG.length; i < n; i++) {
            El.setAttribute(new Attribute(Sloc.XML_AT_LANG[i], dir.getData().getLangTotalStr(Slic.LANG[i])));   
         }
         removedDirEl.addContent(El);

      } // End dir loop
      
   } // End processRemovedDirList()
   
   public void writeDoc(String FileName)
   {

      // serialize it into a file
      try
      {
         Format myFormat = Format.getRawFormat().setIndent("  ").setLineSeparator("\n");
         FileOutputStream out = new FileOutputStream(FileName);
         XMLOutputter serializer = new XMLOutputter(myFormat);
         serializer.output(Document, out);
         out.flush();
         out.close();
      } catch (IOException e)
      {
         System.err.println(e);
      }

   } // End writeDoc()

} // End class SlocXml

