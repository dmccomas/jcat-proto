package utils;

/*
 * Based on Data Structures with Java by John R. Hubbard, however, his code was written before generics so
 * I started with his code and generized it.
 */

import java.util.*;

public class BinaryTree<T>
{ 
   
protected T             root;
protected BinaryTree<T> left, right, parent;
protected int           size;

public BinaryTree()
{
}

public BinaryTree(T root)
{ 
   this.root = root;
   size = 1;

}

public BinaryTree(T root, BinaryTree<T> left, BinaryTree<T> right)
{ 
   this(root);

   if (left != null)
   { 
      this.left = left;
      left.parent = this;
      size += left.size();
    
   }

   if (right != null)
   {
      this.right = right;
      right.parent = this;
      size += right.size();
   }

} // End BinaryTree()

public boolean equals(BinaryTree<T> tree)
{ 
   
   return (    tree.root.equals(root)
            && tree.left.equals(left)
            && tree.right.equals(right)
            && tree.parent.equals(parent)
            && tree.size == size);

}

public int hashCode()
{ 
   return root.hashCode() + left.hashCode() + right.hashCode() + size;
}

public java.util.Iterator iterator()
{ 
   return new java.util.Iterator()  // anonymous inner class
{ 
      private boolean rootDone;
  private java.util.Iterator lit, rit;  // child iterators
  public boolean hasNext()
  { return !rootDone || lit != null && lit.hasNext()
                     || rit != null && rit.hasNext();
  }
  public Object next()
  { if (rootDone)
    { if (lit != null && lit.hasNext()) return lit.next();
      if (rit != null && rit.hasNext()) return rit.next();
      return null;
    }
    if (left != null) lit = left.iterator();
    if (right != null) rit = right.iterator();
    rootDone = true;
    return root;
  }
  public void remove()
  { throw new UnsupportedOperationException(); 
  }
};

} // End inner class iterator

public int size()
{
   return size;
}

} // End class BinaryTree
