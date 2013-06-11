package app;

import java.util.Iterator; 
import java.util.List;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class RegroupDialog extends Dialog
{
   private SlocProject project;
   private String      selGroup;
   private boolean     newGroup;
   
   // Constructors
   public RegroupDialog(Shell parent, SlocProject proj) {
      // Pass the default styles here
      super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
      setText("Regroup Selected Directories...");
      project = proj;
      selGroup = null;
      newGroup = false;
    }

   public String getGroupName() {
     return selGroup;
   }

   public String open() {
     
     // Create the dialog window
     Shell shell = new Shell(getParent(), getStyle());
     shell.setText(getText());
     createContents(shell);
     shell.pack();
     shell.open();
     Display display = getParent().getDisplay();
     while (!shell.isDisposed()) {
       if (!display.readAndDispatch()) {
         display.sleep();
       }
     }
     // Return the entered value, or null
     return selGroup;
     
   } // End open()

   private void createContents(final Shell shell) {

      shell.setLayout(new GridLayout(2, false));

      // Instruction label
      Label instructLabel = new Label(shell, SWT.NONE);
      instructLabel.setText("Enter a new group or select existing group from dropdown");
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalSpan = 2;
      instructLabel.setLayoutData(gridData);

      // New Group
      Label newGroupLabel = new Label(shell, SWT.NONE);
      newGroupLabel.setText("New Group Name:");
      gridData = new GridData();
      newGroupLabel.setLayoutData(gridData);

      // Display the input box
      final Text newGroupText = new Text(shell, SWT.BORDER);
      gridData = new GridData(GridData.FILL_HORIZONTAL);
      newGroupText.setLayoutData(gridData);

      // Create existing group selection drop down
      new Label(shell, SWT.NONE).setText("Group:");
      final Combo groupCombo = new Combo(shell, SWT.DROP_DOWN | SWT.SINGLE | SWT.READ_ONLY);
      List<SlocGroup> groupList = project.getGroupList();
      Iterator<SlocGroup> groupIt = groupList.iterator();
      groupCombo.add(" ");
      while(groupIt.hasNext()) {
         SlocGroup group = groupIt.next();
         groupCombo.add(group.getName());
      } // End group loop

     // OK button handler
     Button ok = new Button(shell, SWT.PUSH);
     ok.setText("OK");
     gridData = new GridData(GridData.FILL_HORIZONTAL);
     ok.setLayoutData(gridData);
     ok.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         selGroup = null;
         if (groupCombo.getSelectionIndex() > 0) {
            selGroup = groupCombo.getItem(groupCombo.getSelectionIndex());
         }
         else {
            selGroup = newGroupText.getText();
            newGroup = true;
         }
         shell.close();
       }
     });

     // Cancel button handler
     Button cancel = new Button(shell, SWT.PUSH);
     cancel.setText("Cancel");
     gridData = new GridData(GridData.FILL_HORIZONTAL);
     cancel.setLayoutData(gridData);
     cancel.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         selGroup = null;
         newGroup = false;
         shell.close();
       }
     });

     // Set the OK button as the default, so
     // user can type input and press Enter
     // to dismiss
     shell.setDefaultButton(ok);
   }   
   
   public boolean isNewGroup() {
      return newGroup; 
   }
   
} // End class RegroupDialog
