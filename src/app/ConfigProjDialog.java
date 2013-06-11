package app;

import slic.*;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ConfigProjDialog extends Dialog
{

   private String   projName;
   private String   verStr;

   // Constructors
   
   public ConfigProjDialog(Shell parent) {
     // Pass the default styles here
     this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
   }
   public ConfigProjDialog(Shell parent, int style) {
     // Let users override the default styles
     super(parent, style);
     setText("Project Settings...");
   }

   public String getProjName() {
     return projName;
   }

   public String getVerStr() {
     return verStr;
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
     return projName;
   }

   private void createContents(final Shell shell) {

      shell.setLayout(new GridLayout(2, false));

      // Input for project name
      Label projLabel = new Label(shell, SWT.NONE);
      projLabel.setText("Project name:");
      GridData data = new GridData();
      projLabel.setLayoutData(data);

      // Display the input box
      final Text projText = new Text(shell, SWT.BORDER);
      data = new GridData(GridData.FILL_HORIZONTAL);
      projText.setLayoutData(data);

      // Input for version
      Label verLabel = new Label(shell, SWT.NONE);
      verLabel.setText("Version:");
      data = new GridData();
      verLabel.setLayoutData(data);

      // Display the input box
      final Text verText = new Text(shell, SWT.BORDER);
      data = new GridData(GridData.FILL_HORIZONTAL);
      verText.setLayoutData(data);

     // OK button handler
     Button ok = new Button(shell, SWT.PUSH);
     ok.setText("OK");
     data = new GridData(GridData.FILL_HORIZONTAL);
     ok.setLayoutData(data);
     ok.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         projName = projText.getText();
         verStr = verText.getText();
         shell.close();
       }
     });

     // Cancel button handler
     Button cancel = new Button(shell, SWT.PUSH);
     cancel.setText("Cancel");
     data = new GridData(GridData.FILL_HORIZONTAL);
     cancel.setLayoutData(data);
     cancel.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         projName = null;
         shell.close();
       }
     });

     // Set the OK button as the default, so
     // user can type input and press Enter
     // to dismiss
     shell.setDefaultButton(ok);
   }   
   
} // End class ProjInputDialog
