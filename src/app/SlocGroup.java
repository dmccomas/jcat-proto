package app;

/*
 * The class allows users to logically group directories. 
 *
 */

import slic.*;  

import java.util.ArrayList;
import java.util.List;
import utils.GenericTreeNode;

public class SlocGroup
{

   private String name;
   private List<GenericTreeNode<SlicDir>> dirList;
   
   public SlocGroup (String groupName) {
      
      name = groupName;
      dirList = new ArrayList<GenericTreeNode<SlicDir>>();
      
   }

   public SlocGroup (String groupName, List<GenericTreeNode<SlicDir>> initDirList) {
      
      name = groupName;
      dirList = initDirList;
      
   }
   
   public void addDir(SlicDir dir) {
      
      dirList.add(new GenericTreeNode<SlicDir>(dir));
      
   }

   public void addDir(GenericTreeNode<SlicDir> dir) {
      
      dirList.add(dir);
      
   }

   public String getName() {
      
      return name;
      
   }

   public void setName(String newName) {
      
      name = newName;
      
   }
   
   public List<GenericTreeNode<SlicDir>> getDirList() {
      
      return dirList;
      
   }

   public boolean removeDir(GenericTreeNode<SlicDir> dir) {
      
      return dirList.remove(dir);
      
   }
   
} // End class SlocGroup
