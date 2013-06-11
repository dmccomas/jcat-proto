package app;

import slic.*;   

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.List;
import utils.GenericTreeNode;


class SlocTreeContentProvider implements ITreeContentProvider
{
   
   public Object[] getChildren(Object parentElement)
   {
      
      //System.out.println("getChildren(). parentElement type = " + parentElement.getClass());
      if (parentElement instanceof SlocGroup) {
         //System.out.println("List with size = " + ((List<?>) parentElement).size());
         return ((SlocGroup)parentElement).getDirList().toArray(); 
      }
      else {
         List<GenericTreeNode<SlicDir>> childList = ((GenericTreeNode<SlicDir>)parentElement).getChildren();
         //System.out.println("TreeNode with size = " + ((GenericTreeNode<SlicDir>)parentElement).getNumberOfChildren());
         return childList.toArray();
      }
   } // getChildren()

   public Object getParent(Object element){
      
      if (element instanceof SlocGroup) {
         System.out.println("SlocTreeContentProvider::getParent: SlocGroup");
         return null;
      }
      System.out.println("SlocTreeContentProvider::getParent");
      return ((GenericTreeNode<SlicDir>)element).getParent();

   } // End getParent()

   public boolean hasChildren(Object element){
      
 
      if (element instanceof SlocGroup) {
         //System.out.println("SlocTreeContentProvider::hasChildren: SlocGroup");
         return (((SlocGroup)element).getDirList().size() > 0);
      }
      //System.out.println("SlocTreeContentProvider::hasChildren = " + ((GenericTreeNode<SlicDir>)element).hasChildren() + " dirStr = " + ((GenericTreeNode<SlicDir>)element).getData().getDirStr());
      return ((GenericTreeNode<SlicDir>)element).hasChildren();

   } // End hasChildren()

   public Object[] getElements(Object element)
   {
      // element is already the root node, so return it in the expected format
      return ((List<SlocGroup>)element).toArray();
      
   } // End getElements()

   public void dispose() {
      System.out.println("SlocTreeContentProvider::dispose()");
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
      System.out.println("SlocTreeContentProvider::inputChanged()");

   }
   
} // End class SlocTreeContentProvider
