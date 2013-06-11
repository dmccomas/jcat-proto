package app;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class HelpDialog extends Dialog
{

   final String helpStr = "SLOC is a tool to help manage and analyze SLIC output files. SLIC is a source line counting tool and is easy to\n" +
                          "run using its default configuration at a project's root directory and have it generate a text file containing a\n" +
                          "count of all of a project's files. However this output may contain unwanted directories (not be included in SLOC\n" +
                          "count) and many times it is helpful to logically group directories within a project to get subtotals. For example\n" +
                          "files in heritage cirectories or directories from a reuse library. SLOC can help with these tasks.  SLOC is typically\n" +
                          "used in one of two ways:\n\n" +
                          "     1. Input a SLIC file and generate a comma separated report that can be imported to Excel\n" +
                          "         Directories are then managed within Excel and different software release code counts can\n" +
                          "         be maintained in different tabs\n" +
                          "     2. Input a SLIC file and manipulate the directories within SLOC. SLOC allows you to save a\n" +
                          "         project's information in an XML file. New releases can be added to the XML database. Note\n" +
                          "         each time you run SLIC it must be run from the same base directory in order for directories to\n" +
                          "         be managed correctly. Even if you use SLOC to manage directories you may still want to export\n +" +
                          "         a CSV file to Excel\n\n" +
                          "Steps to generate a comma separated report for importing to Excel:\n" +
                          "     1. Run SLIC from the top-most directory in a project and direct the ouptut to a text file\n" +
                          "         Using a .txt extension is recommended.\n" +
                          "     2. Start SLOC and select File->New to import the SLIC file. You will be prompted for a project\n" +
                          "         name and software version number. Note the project name will be used to as the default SLOC\n" +
                          "         'group' name and all of the project directories will be in this group.\n" +
                          "     3. Select Options->Generate Report to create the CSV file. " +
                          "     4. Start Excel and import the CSV file.\n\n" +
                          "Steps to manage a project within SLOC\n" +
                          "     1. TBD\n\n";
   
   // Constructors
   
   public HelpDialog(Shell parent) {
     // Pass the default styles here
     this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
   }
   public HelpDialog(Shell parent, int style) {
     // Let users override the default styles
     super(parent, style);
     setText("Instructions for use ...");
   }

   public void open() {
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
   } // End open()

   private void createContents(final Shell shell) {

      shell.setLayout(new GridLayout(1, false));
      
      StyledText helpText = new StyledText( shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      GridData data = new GridData(GridData.FILL_HORIZONTAL);
      helpText.setLayoutData(data);
      StyleRange styleRange1 = new StyleRange();
      styleRange1.start = helpText.getCharCount();
      styleRange1.length = helpStr.length();
      styleRange1.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
      styleRange1.fontStyle = SWT.NORMAL;
      
      helpText.append(helpStr + "\r\n");
      helpText.setStyleRange(styleRange1);
      helpText.setSelection(helpText.getCharCount());


      // OK button handler
      Button ok = new Button(shell, SWT.PUSH);
      ok.setText("OK");
      data = new GridData(GridData.CENTER);
      ok.setLayoutData(data);
      ok.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            shell.close();
         }
         });

     shell.setDefaultButton(ok);

   } // End createContents   
   
} // End class ProjInputDialog
