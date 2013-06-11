package app;

import org.eclipse.swt.graphics.Image; 
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;

import slic.*;
import utils.GenericTreeNode;

public class SlocTreeLabelProvider implements ITableLabelProvider
{
   
   public Image getColumnImage(Object element, int columnIndex)
   {
      return null;
   }

   public String getColumnText(Object element, int columnIndex)
   {
      
      //System.out.println("getColumnText() element type = " + element.getClass());
      if (element instanceof SlocGroup) {
         if (columnIndex == 0)
            return ((SlocGroup)element).getName();
         else
            return "";
      }   
      else {
         //System.out.println("getColumnText() dir = " + ((GenericTreeNode<SlicDir>)element).getData().getDirStr());
         if (columnIndex == 0)
            return ((GenericTreeNode<SlicDir>)element).getData().getDirStr();
         else
            return ((GenericTreeNode<SlicDir>)element).getData().getLangTotalStr(Slic.LANG[columnIndex-1]);
      } 
   } // End getColumn()

   public void addListener(ILabelProviderListener listener){}

   public void dispose(){}

   public boolean isLabelProperty(Object element, String property){
      return false;
   }

   public void removeListener(ILabelProviderListener listener){}
   
} // End class SLOCTreeLabelProvider     
