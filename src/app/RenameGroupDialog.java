package app;

import java.util.Iterator; 
import java.util.List;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class RenameGroupDialog extends Dialog
{
   private SlocProject project;
   private String      defOldGroup;
   private String      oldGroupName;
   private String      newGroupName;
   
   // Constructors
   public RenameGroupDialog(Shell parent, SlocProject proj, String defGroup) {
      // Pass the default styles here
      super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
      setText("Regroup Selected Directories...");
      project = proj;
      defOldGroup = defGroup;
      oldGroupName = null;
      newGroupName = null;
    }

   public String getNewGroupName() {
     return newGroupName;
   }


   public String getOldGroupName() {
     return oldGroupName;
   }

   public String[] open() {
     
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
     String [] retString = new String[2];
     retString[0] = getOldGroupName();
     retString[1] = getNewGroupName();
     return retString;
     
   } // End open()

   private void createContents(final Shell shell) {

      shell.setLayout(new GridLayout(2, false));

      // Instruction label
      Label instructLabel = new Label(shell, SWT.NONE);
      instructLabel.setText("Select group to rename from dropdown and enter a new name");
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalSpan = 2;
      instructLabel.setLayoutData(gridData);

      // Create existing group selection drop down
      new Label(shell, SWT.NONE).setText("Select group to rename:");
      final Combo groupCombo = new Combo(shell, SWT.DROP_DOWN | SWT.SINGLE | SWT.READ_ONLY);
      List<SlocGroup> groupList = project.getGroupList();
      Iterator<SlocGroup> groupIt = groupList.iterator();
      groupCombo.add(" ");
      int index = 0, defIndex = 0;
      while(groupIt.hasNext()) {
         index++;
         SlocGroup group = groupIt.next();
         groupCombo.add(group.getName());
         if (defOldGroup != null)
            if (group.getName().compareTo(defOldGroup) == 0)
               defIndex = index;
      } // End group loop
      groupCombo.select(defIndex);
      // New Group name
      Label newGroupLabel = new Label(shell, SWT.NONE);
      newGroupLabel.setText("Enter new group name:");
      gridData = new GridData();
      newGroupLabel.setLayoutData(gridData);

      // Display the input box
      final Text newGroupNameText = new Text(shell, SWT.BORDER);
      gridData = new GridData(GridData.FILL_HORIZONTAL);
      newGroupNameText.setLayoutData(gridData);


     // OK button handler
     Button ok = new Button(shell, SWT.PUSH);
     ok.setText("OK");
     gridData = new GridData(GridData.FILL_HORIZONTAL);
     ok.setLayoutData(gridData);
     ok.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         if (groupCombo.getSelectionIndex() > 0) {
            oldGroupName = groupCombo.getItem(groupCombo.getSelectionIndex());
            newGroupName = newGroupNameText.getText();
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
         oldGroupName = null;
         newGroupName = null;
         shell.close();
       }
     });

     // Set the OK button as the default, so
     // user can type input and press Enter
     // to dismiss
     shell.setDefaultButton(ok);
   }   
     
} // End class RenameGroupDialog
