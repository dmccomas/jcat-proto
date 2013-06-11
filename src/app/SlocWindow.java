/*******************************************************************************
 * 
 * This application reads in a SLIC output file. Allows the user to create       
 * groups, and generate reports that can be used by other applications like Excel.
 * see HelpDialog class for more user information.
 * 
 * Notes:
 *  1. Originally started support for language selection in project configuration 
 *     but it didn't seem worth the complexity both from a user perspective 
 *     and from a programming perspective. as a user I actually liked seeing all
 *     of the languages.
 *  2. I made right-click popup menus global for listeners, I'm a SWT newbee so not 
 *     sure if better way like the regular menu actions. 
 * 
 * TODO - Learn Java log utilities and move debug message it  
 * TODO - Add version view: Row=Dir & Col=Ver. One language displayed.   
 * TODO - Removed directories across releases gets tricky. They currently repeat
 *        in XML because when new version loaded and user accepts highlighted removed
 *        directories and they get added to the project list. Need logic to and/or
 *        dialogs to control removed dir list. It actually doesn't do any harm that 
 *        I see unless user wants include a previously removed directory. 
 * TODO - Test removed directories from multiple groups
 * TODO - Show removed directory list
 * TODO - Remove extra column when window starts up. I can't figure this out.
 *        Noticed table examples in book have the same column.
 * TODO - For file dialogs may want 
 *        - List of most recent files
 *        - Default path: String AppDirPath = new java.io.File(".").getCanonicalPath();
 *******************************************************************************/

package app;

import slic.*;              
import utils.*;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.io.File;
import java.io.FileInputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


/**
 *  
 */
public class SlocWindow extends ApplicationWindow 
{

   StyledText  textLog;

   String      projName    = null;
   SlocProject project     = null;
   String      currVerStr  = null;
   int         currVerInt  = 0;
   
   //~Vector<SlocProject> projectList = new Vector<SlocProject>();
   //~SlocProject projectList[];
   
   Label       slicWinLabel;
   Tree        slicFileTree;
   TreeViewer  slicFileViewer;
      
   Action actionNew;
   Action actionOpen;
   Action actionSave;
   Action actionSaveAs;
   Action actionExit;

   Action actionConfigProject;
   Action actionRenameGroup;
   Action actionGenReport;
   
   Action actionDisplayAbout;
   Action actionDisplayHelp;

   // Right click tree submenu
   Menu dirMenu;
   MenuItem dirDel;
   MenuItem dirRegroup;
   MenuItem dirRenameGroup;

    /**
    * @param parentShell
    */
   public SlocWindow(Shell parentShell) 
   {
      super(parentShell);

      createActions();

      addStatusLine();
      addToolBar(SWT.FLAT);
      addMenuBar();
      
      // TODO - Possibly add default behavior based on previous application execution

   } // End SLOCWindow()
   
   private void learnTreeItem(TreeItem[] items) {
    
      System.out.println("learnTreeItem() - items.length = " + items.length);
      
      for (int i=0; i < items.length; i++) {
         System.out.println("learnTreeItem() - items["+i+"] = " + items[i].getText());
         System.out.println("learnTreeItem() - items["+i+"].ItemCount = " + items[i].getItemCount());
      }
      
      TreeItem[] items2 = items[0].getItems();
      for (int i=0; i < items2.length; i++) {
         System.out.println("learnTreeItem() - items2["+i+"] = " + items2[i].getText());
      }
      
   } // End learnTreeItem()
   
   /*
    * Call this after a new version is loaded. Select all of the directories previously removed so the
    * user has he option to remove then again or change their selection
    */
   private void selectRemovedDirectories() {
      
      System.out.println("selectRemovedDirectories()\n");

      TreeItem[] removedTreeItemArray = new TreeItem[1000]; // If user has selected more than 1000 then need better tool/process
      int  j = 0;
      
      TreeItem[] items = slicFileTree.getItems()[0].getItems(); // Only one root item that contains all of the children
      List<GenericTreeNode<SlicDir>> removedDirList = project.getRemovedDirList();
      
      // Since different types can't do simple contains() check & performance is no an issue
      Iterator<GenericTreeNode<SlicDir>> dirIt = removedDirList.iterator();
      while(dirIt.hasNext())
      {
         GenericTreeNode<SlicDir> dir = dirIt.next();
         System.out.println("selectRemovedDirectories() - Checking removed directory " + dir.getData().getDirStr());
         for (int i=0; i < items.length; i++) {
            if (items[i].getText().matches(dir.getData().getDirStr())) {
               System.out.println("learnTreeItem() - matched directory = " + items[i].getText());
               removedTreeItemArray[j++] = items[i];
               // Only highlighted last selection: slicFileTree.setSelection(items[i]);
            }
         }
         
      } // End removed dir loop
           
      slicFileTree.setSelection(removedTreeItemArray);
      
   } // End selectRemovedDirectories()

   /*
    * Prompt user for new project XML file. This is called when another project is loaded but the user
    * wants to start over so it is considered a new project.
    */
   private void promptNewXmlProject() {
      
      // Prompt user for project file (XML). 
      FileDialog xmlFileDlg = new FileDialog(getShell(), SWT.OPEN);
      xmlFileDlg.setFilterNames(new String[] {"SLOC project files (*.xml)"});
      xmlFileDlg.setFilterExtensions(new String[] {"*.xml"});
      String xmlFile = xmlFileDlg.open();
      if (xmlFile != null) {
         project = new SlocProject(new File(xmlFile));
         projName    = project.getName(); 
         currVerStr  = project.getVersion();
         setProjWinText();
         refreshTreeView();
      }
      
   } // End promptNewXmlProject()

   /*
    * Prompt user for new project SLIC file. Called whenever user starting a new project
    * with a SLIC output file.
    */
   private void promptNewSlicProject() {
      
      FileDialog newFileDlg = new FileDialog(getShell(), SWT.OPEN);
      newFileDlg.setFilterNames(new String[] {"SLIC output text files (*.txt)"});
      newFileDlg.setFilterExtensions(new String[] {"*.txt"});
      String newFile = newFileDlg.open();
      // TODO - Add default path if needed: String AppDirPath = new java.io.File(".").getCanonicalPath();
      if (newFile != null) {
         // TODO = Should old project be deleted or rely on garbage collection? 
         getProjectInfo();
         project = new SlocProject(new File(newFile), projName, currVerStr);
         if (project.releaseCount() > 0) {
            setProjWinText();
            slicFileViewer.setInput(project.getGroupList());
            slicFileViewer.expandAll();
         }
         else
            logError("No directories in SLIC output file");
         
      } // End if newFile != null
      
   } // End promptNewSlicProject()
   
   private void createActions() 
   {
      /*
       *  New - Project
       *  
       *  New projects always start with a SLIC output file.
       */
      actionNew = new Action() 
      {
         public void run() 
         {
            
            logMessage("New project called", true);
            promptNewSlicProject();
            
         } // End run()
      }; // End Action()
      actionNew.setText("New");
      actionNew.setToolTipText("Create a new project");
      actionNew.setImageDescriptor(ImageDescriptor.createFromFile(null, "../icons/new.gif"));

      /*
       *  Open - Either an existing project's XML file or a new version to add to a project's database
       *
       *  If no project currently open then prompt for existing XML. If an XML file has been open then most likely
       *  the user wants to open a SLIC file containing the next version of the project's software. 
       */
      actionOpen = new Action() 
      {
         public void run() 
         {

            if (project == null) {
        
               promptNewXmlProject();
               
            } // End if project == null
            else {
               
               MessageBox confirmVersionMB = new MessageBox(getShell(),SWT.ICON_QUESTION | SWT.YES | SWT.NO);
               confirmVersionMB.setText("Confirm new project version");
               confirmVersionMB.setMessage("Is this a new version (SLIC output file) for project " + project.getName() + "?");
               int answer = confirmVersionMB.open();

               if (answer == SWT.YES) {
                  
                  FileDialog newFileDlg = new FileDialog(getShell(), SWT.OPEN);
                  newFileDlg.setFilterNames(new String[] {"SLIC output text files (*.txt)"});
                  newFileDlg.setFilterExtensions(new String[] {"*.txt"});
                  String newFile = newFileDlg.open();
                  if (newFile != null) {
                     loadNewProjectVersion(newFile);
                     refreshTreeView();
                     //debug learnTreeItem(slicFileTree.getItems());
                     selectRemovedDirectories();

                  }

               } // End if new version 
               else
               {

                  FileTypeDialog fileTypeDialog = new FileTypeDialog(getShell());
                  fileTypeDialog.open();

                  if (!fileTypeDialog.cancelled())
                  {

                     if (fileTypeDialog.xmlSelected())
                        promptNewXmlProject();
                     else
                        promptNewSlicProject();
                     
                  } // End if dialog not cancelled

                  
               } // End if not new version
               
               
            } // End if a project is currently loaded
         } // End run()
      }; // End Action()
      actionOpen.setText("Open");
      actionOpen.setToolTipText("Open existing project");
      actionOpen.setImageDescriptor(ImageDescriptor.createFromFile(null, "../icons/open.gif"));

      // Save - Current Project
      actionSave = new Action() 
      {
         public void run() 
         {
            // Save a project
            if (project != null){

               // TODO - Add default path if needed: String AppDirPath = new java.io.File(".").getCanonicalPath();
               FileDialog saveFileDlg = new FileDialog(getShell(), SWT.SAVE);
               saveFileDlg.setFilterNames(new String[] {"Project File (*.xml)"});
               saveFileDlg.setFilterExtensions(new String[] {"*.xml"});
               String saveFile = saveFileDlg.open();
               
               if (saveFile != null) {
                  
                  logMessage("Save filename = " + saveFile,true);
                  // TODO - Get directory path 
                  SlocToXml xmlReport = new SlocToXml(project);
                  xmlReport.writeDoc(saveFile);
                  
               } // End if SaveFile != null
            } // End if project != null
            
         } // End run()
      }; // End Action()
      actionSave.setText("Save");
      actionSave.setToolTipText("Save current project");
      actionSave.setImageDescriptor(ImageDescriptor.createFromFile(null, "../icons/save.gif"));

      // SaveAs -  Current Project
      actionSaveAs = new Action() 
      {
         public void run() 
         {
            // Save a project
            if (project != null){

               // TODO - Add default path if needed: String AppDirPath = new java.io.File(".").getCanonicalPath();
               FileDialog saveFileDlg = new FileDialog(getShell(), SWT.SAVE);
               saveFileDlg.setFilterNames(new String[] {"Project File (*.xml)"});
               saveFileDlg.setFilterExtensions(new String[] {"*.xml"});
               String saveFile = saveFileDlg.open();
               
               if (saveFile != null) {
                  
                  logMessage("Save filename = " + saveFile,true);
                  // TODO - Get directory path 
                  // TODO - Fix file name
                  SlocToXml xmlReport = new SlocToXml(project);
                  xmlReport.writeDoc("temp.xml");
                  
               }
            } // End if project != null
            
         } // End run()
      }; // End Action()
      actionSaveAs.setText("SaveAs");
      actionSaveAs.setToolTipText("Save current project");
      actionSaveAs.setImageDescriptor(ImageDescriptor.createFromFile(null, "../icons/SaveAs16.gif"));

      // Exit
      actionExit = new Action() {
         public void run()
         {
            if(! MessageDialog.openConfirm(getShell(), "Confirm", "Are you sure you want to exit?")) 
               return;
            // TODO - Add dirty file checks and saving

            close();
         } // End run()
      };
      actionExit.setText("Exit");
      
      // Configure Project
      actionConfigProject = new Action() 
      {
         public void run() 
         {
            getProjectInfo();
            
         } // End run()
      }; // End Action()
      actionConfigProject.setText("Configure Project...");
      actionConfigProject.setToolTipText("Enter project information");

      // Rename Group
      actionRenameGroup = new Action() 
      {
         public void run() 
         {
            renameGroup(null);
            
         } // End run()
      }; // End Action()
      actionRenameGroup.setText("Rename group...");
      actionRenameGroup.setToolTipText("Rename a group");
      
      // Generate Report
      actionGenReport = new Action() 
      {
         public void run() 
         {
            // Generate a report
            if (project != null) {
               ReportDialog repDialog = new ReportDialog(getShell());
               repDialog.open();

               if (!repDialog.cancelled())
               {
                  // TODO - Add default path if needed: String AppDirPath = new java.io.File(".").getCanonicalPath();
                  FileDialog saveFileDlg = new FileDialog(getShell(), SWT.SAVE);
                  saveFileDlg.setFilterNames(new String[] {"CSV report text files (*.csv)", "CSV report text files (*.txt)"});
                  saveFileDlg.setFilterExtensions(new String[] {"*.csv", "*.txt"});
                  String saveFile = saveFileDlg.open();
                  
                  if (saveFile != null) {
                     
                     logMessage("Group only = " + repDialog.groupOnly(),true);
                     logMessage("Save filename = " + saveFile,true);
                     project.createReport(new File(saveFile), repDialog.groupOnly());
                     
                  }
               } // End if not canceled
            } // End if project != null
            else {
               MessageDialog.openInformation(getShell(), "Generate Report", "A project has not been loaded or created.");
            }
         } // End run()
      }; // End Action()
      actionGenReport.setText("Generate Report...");
      actionGenReport.setToolTipText("Generate a report");
      actionGenReport.setImageDescriptor(ImageDescriptor.createFromFile(null, "../icons/file.gif"));
      
      actionDisplayAbout = new Action() {
         public void run() {
            MessageDialog.openInformation(getShell(), "About", "SLIC Output Analyzer v1.0\nCreated by\nNASA Goddard Spaceflight Center\n Flight Software Systems Branch\nDavid McComas");
         }
      };
      actionDisplayAbout.setText("About");
      actionDisplayAbout.setImageDescriptor(ImageDescriptor.createFromFile(null, "../icons/questionmark.gif"));
      
      actionDisplayHelp = new Action() {
         public void run() {
            HelpDialog help = new HelpDialog(getShell());
            help.open();
         }
      };
      actionDisplayHelp.setText("Help");
      actionDisplayHelp.setToolTipText("Instructions for use");
      
   } // End createActions()


   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.window.ApplicationWindow#createMenuManager()
    */
   protected MenuManager createMenuManager()
   {
      MenuManager bar = new MenuManager();

      MenuManager menuFile = new MenuManager("&File");
      menuFile.add(actionNew);
      menuFile.add(actionOpen);
      menuFile.add(new Separator());
      menuFile.add(actionSave);
      menuFile.add(actionSaveAs);
      menuFile.add(new Separator());
      menuFile.add(actionExit);
      
      MenuManager menuOptions = new MenuManager("Options");
      menuOptions.add(actionConfigProject);
      menuOptions.add(actionRenameGroup);
      menuOptions.add(actionGenReport);

      MenuManager menuHelp = new MenuManager("&Help");
      menuHelp.add(actionDisplayAbout);
      menuHelp.add(actionDisplayHelp);

      bar.add(menuFile);
      bar.add(menuOptions);
      bar.add(menuHelp);
      bar.updateAll(true);

      return bar;
      
   } // End MenuManager()

   public static void addAction(
      ToolBarManager manager,
      Action action,
      boolean displayText) {
      if (!displayText) {
         manager.add(action);
         return;
      } else {
         ActionContributionItem item = new ActionContributionItem(action);
         item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
         manager.add(item);
      }
   }

   
   /* (non-Javadoc)
    * @see org.eclipse.jface.window.ApplicationWindow#createToolBarManager(int)
    */
   protected ToolBarManager createToolBarManager(int style) {
      ToolBarManager manager = super.createToolBarManager(style);

      addAction(manager, actionNew, true);
      addAction(manager, actionOpen, true);

      addAction(manager, actionSave, true);
      addAction(manager, actionSaveAs, true);

      manager.add(new Separator());

      addAction(manager, actionGenReport, true);
      manager.add(new Separator());

      addAction(manager, actionDisplayHelp, true);      

      manager.update(true);      
      
      return manager;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
    */
   protected Control createContents(Composite parent) 
   {
   
      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new FillLayout(SWT.VERTICAL));

      // the vertical sashform (controls are set vertically and sash is horizontal)
      SashForm verticalForm = new SashForm(composite, SWT.VERTICAL);

      // SLIC File Browser

      Composite compositeSlicWin = new Composite(verticalForm, SWT.NULL);
      GridLayout gridLayout = new GridLayout(1,true);
      gridLayout.horizontalSpacing = 1;
      gridLayout.verticalSpacing = 1;
      compositeSlicWin.setLayout(gridLayout);

      Group compositeSlicWinTop = new Group(compositeSlicWin, SWT.NULL);
      compositeSlicWinTop.setText("SLIC File");
      GridLayout gridLayout2 = new GridLayout(1, true);
      gridLayout2.marginHeight = 0;
      compositeSlicWinTop.setLayout(gridLayout2);
      compositeSlicWinTop.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      slicWinLabel = new Label(compositeSlicWinTop, SWT.NULL);
      slicWinLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setProjWinText();

      slicFileTree = new Tree(compositeSlicWin, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      //slicFileTree.setLayout(new GridLayout(1,true));
      slicFileTree.setLayoutData(new GridData(GridData.FILL_BOTH));
      TreeColumn slicCol1 = new TreeColumn(slicFileTree, SWT.BORDER);
      slicFileTree.setLinesVisible(true);
      slicCol1.setAlignment(SWT.LEFT);
      slicCol1.setText("Directories");
      slicCol1.setWidth(250);

      for (int i=0, n=Slic.LANG.length; i < n; i++) {
         TreeColumn slicCol2 = new TreeColumn(slicFileTree, SWT.BORDER);
         slicCol2.setAlignment(SWT.CENTER);
         slicCol2.setText(Slic.LANG[i]);
         slicCol2.setWidth(100);
      }
      slicFileTree.setHeaderVisible(true);
      
      // Directory popup menu
      dirMenu = new Menu(slicFileTree);
      dirDel = new MenuItem(dirMenu, SWT.PUSH);
      dirDel.setText("Delete");
      dirRegroup = new MenuItem(dirMenu, SWT.None);
      dirRegroup.setText("Regroup...");
      dirRenameGroup = new MenuItem(dirMenu, SWT.None);
      dirRenameGroup.setText("Rename Group...");
      slicFileTree.setMenu(dirMenu);

      slicFileViewer = new TreeViewer(slicFileTree);
      slicFileViewer.setContentProvider(new SlocTreeContentProvider());
      slicFileViewer.setLabelProvider(new SlocTreeLabelProvider());
      //slicFileViewer.setInput(project.getGroupList());
      slicFileViewer.expandAll();

      // the log box.
      textLog = new StyledText( verticalForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

      // resize sashform children.
      verticalForm.setWeights(new int[]{4, 1});
      
      // Popup menu selection listeners 
      linkListeners();
      
      getToolBarControl().setBackground(
         new Color(getShell().getDisplay(), 230, 230, 230));

      getShell().setImage(new Image(getShell().getDisplay(), "../icons/file.gif"));
      getShell().setText("SLIC Output Analyzer v1.0");
      
      return composite;
      
   } // End createContent()

   // I didn't see how to use the window menu's action design for a popup menu so I used selection listeners.
   private void linkListeners() {

      System.out.println("linkListeners()");
      dirDel.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
            //Unused: System.out.println("dirDel.widgetDefaultSelected(). Event = " + e.text);
         }
         public void widgetSelected(SelectionEvent e) {
            System.out.println("dirDel.widgetSelected(). Event = " + e.text);
            MessageBox confirmDeleteMB = new MessageBox(getShell(),SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            confirmDeleteMB.setText("Confirm Delete");
            confirmDeleteMB.setMessage("Are you sure you want to delete the selected directories?");
            int answer = confirmDeleteMB.open();
            if (answer == SWT.YES) {
               IStructuredSelection dirSelection = (IStructuredSelection) slicFileViewer.getSelection();
               if (dirSelection.isEmpty())
                  logError("Empty Selection");
               else {
                  StringBuffer toShow = new StringBuffer();
                  for (Iterator dirIt = dirSelection.iterator(); dirIt.hasNext();) {
                     Object dirObj = (Object) dirIt.next(); 
                     if (dirObj instanceof SlocGroup) {
                        logError("Group delete not supported"); // TODO - add group delete support
                     }
                     if (dirObj instanceof GenericTreeNode) {
                        GenericTreeNode<SlicDir> dir = ((GenericTreeNode<SlicDir>)dirObj);
                        project.removeSlicDir(dir);
                        project.printRemovedDirList(); // Debug
                     }
                     toShow.append(dirObj.getClass());
                     toShow.append(", ");
                  }
                  // remove the trailing comma space pair
                  if(toShow.length() > 0) {
                      toShow.setLength(toShow.length() - 2);
                  }
                  logMessage(toShow.toString(),false);
                  
                  refreshTreeView();
                  
               }// End if not empty selection
            } // End if confirmed delete
            
         } // End widgetSelected()
      }); // End dirDel.addSelectionListener()

      dirRegroup.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
            //Unused: System.out.println("dirRegroup.widgetDefaultSelected(). Event = " + e.text);
         }
         public void widgetSelected(SelectionEvent e) {
            System.out.println("dirRegroup.widgetSelected(). Event = " + e.text);
            RegroupDialog regroupDialog = new RegroupDialog(getShell(),project);
            String group = regroupDialog.open();
            if (group == null)
               return;
            else {
               IStructuredSelection dirSelection = (IStructuredSelection) slicFileViewer.getSelection();
               if (dirSelection.isEmpty())
                  logError("Empty Selection, no action taken.");
               else {
                  if (regroupDialog.isNewGroup()) {
                     logMessage ("Moved selection to new group: " + group, true);
                     project.createGroup(group, dirSelection.toList());
                  }
                  else {
                     logMessage ("Moved selection to existing group: " + group, true);
                     project.changeDirGroup(group, dirSelection.toList());
                  }   
                  refreshTreeView();

               }
            }// End if not empty selection
         } // End widgetSelected()
      }); // End dirRegroup.addSelectionListener()

      
      dirRenameGroup.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
            //Unused: System.out.println("dirRename.widgetDefaultSelected(). Event = " + e.text);
         }
         public void widgetSelected(SelectionEvent e) {
            System.out.println("dirRename.widgetSelected(). Event = " + e.text);
            // If a group or directory is delected then use the first selection as the default in the dialog drop down
            String defGroup = null;
            IStructuredSelection dirSelection = (IStructuredSelection) slicFileViewer.getSelection();
            if (!dirSelection.isEmpty()) {
             
               Object dirObj = (Object) dirSelection.getFirstElement();
               if (dirObj instanceof SlocGroup)
                  defGroup = ((SlocGroup)dirObj).getName();   
               else 
                  // Assume SlicDir
                  defGroup = ((GenericTreeNode<SlicDir>)dirObj).getData().getDirGroup();
            }

            renameGroup(defGroup);
            
         } // End widgetSelected()
      });
      
      dirMenu.addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(Event event) {
            System.out.println("delDir.MouseUpListener(). Button = " + event.button);
         } // End handleEvent()
      });
      
      dirMenu.addMenuListener(new MenuListener() {
         public void menuHidden(MenuEvent event) {
            
            System.out.println("dirMenu.MenuListener(). Event = " + event.toString());
            System.out.println("dirMenu.MenuListener(). Widget = " + event.widget.toString());
         }
         public void menuShown(MenuEvent event) {
            System.out.println("dirMenu.MenuListener(). Event = " + event.toString());
         }
      }); // End dirRenameGroup.addSelectionListener()

      /* Eventually learned about and used the menu listeners above. Never finished this approach... 
      slicFileViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
          System.out.println("SelectionChangedListener()");
          // if the selection is empty clear the label
          if(event.getSelection().isEmpty()) {
              logMessage("",true);
              return;
          }
          if(event.getSelection() instanceof IStructuredSelection) {
              IStructuredSelection selection = (IStructuredSelection)event.getSelection();
              StringBuffer toShow = new StringBuffer();
              for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
                 Object domain = (Object) iterator.next(); 
                 //Object domain = (Model) iterator.next();
                 //String value = labelProvider.getText(domain);
                 //toShow.append(value);
                 toShow.append(domain.getClass());
                 toShow.append(", ");
              }
              // remove the trailing comma space pair
              if(toShow.length() > 0) {
                  toShow.setLength(toShow.length() - 2);
              }
              logMessage(toShow.toString(),true);
          }
      }
   });
   
      slicFileTree.addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(Event event) {
            System.out.println("slicFileTree.MouseUpListener(). Event = " + event.toString());
            System.out.println("slicFileTree.MouseUpListener(). Button = " + event.button);
            IStructuredSelection selection =
               (IStructuredSelection) slicFileViewer.getSelection();
            if (selection.isEmpty())
               logMessage("Empty Selection",true);
            else {
               StringBuffer toShow = new StringBuffer();
               for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
                  Object domain = (Object) iterator.next(); 
                  //Object domain = (Model) iterator.next();
                  //String value = labelProvider.getText(domain);
                  //toShow.append(value);
                  toShow.append(domain.getClass());
                  toShow.append(", ");
               }
               // remove the trailing comma space pair
               if(toShow.length() > 0) {
                   toShow.setLength(toShow.length() - 2);
               }
               logMessage(toShow.toString(),true);
            }
       }
      });
      */
      
   } // End linkListeners()
   
   // Refresh and recalculate group totals
   public void refreshTreeView() {
   
      project.computeGroupTotals();
      slicFileViewer.setInput(project.getGroupList());
      slicFileViewer.expandAll();
      
   } // End refreshTreeView()
   
   // Set project name and version
   // Used to include languages but easier to include all Slic supported languages
   private void getProjectInfo() {
     
      ConfigProjDialog projDialog = new ConfigProjDialog(getShell());
      projName = projDialog.open();
      if (projName == null)
         return;
      currVerStr = projDialog.getVerStr();
     
      if (project != null) setProjWinText();

      logMessage("New project " + projName + ", " + projDialog.getVerStr(), true);
      
   } // End getProjectInfo()

   // Load a new project version. Assumes project name is loaded
   private boolean loadNewProjectVersion(String newFile) {
     
      boolean RetStatus = false;
      
      String newVersion;
      
      ExistingProjDialog projDialog = new ExistingProjDialog(getShell(), projName);
      
      newVersion = projDialog.open();
      if (newVersion != null) {
         
         SlocProject newProject = new SlocProject(new File(newFile), projName, newVersion);
         if (newProject.releaseCount() > 0)
         {
            newProject.setVersion(newVersion);
            project.addRelease(newProject.getCurrRelease());
            currVerStr = newVersion;
            setProjWinText();
            logMessage("Loaded new project version " + newVersion, true);
         }
         else
            logError("No directories in SLIC output file");
      
      } // End if newVersion != null
      
      return RetStatus;
      
   } // End loadNewProjectVersion()
   
   private void setProjWinText() {

      String proj, ver;
      proj = projName    == null ? "X"   : projName;
      ver  = currVerStr  == null ? "x.x" : currVerStr;

      slicWinLabel.setText("Project: " + proj + "   Version: " + ver);
      
      if (project != null)
      {
         project.setName(projName);
         project.setVersion(currVerStr);
      }
      
   } // End setSlicWintext

   private void renameGroup(String defGroup) {

      RenameGroupDialog renameDialog = new RenameGroupDialog(getShell(),project, defGroup);
      String groupName[] = renameDialog.open();
      logMessage ("group[0] = " + groupName[0], false);
      logMessage ("group[1] = " + groupName[1], false);
      if ( groupName[0] == null || groupName[1] == null) {
         logError ("Group name missing. No group was renamed");
         return;
      }
      else {
         if (project.renameGroup(groupName[0], groupName[1])) {
            logMessage ("Renamed group from " + groupName[0] + " to " + groupName[1], true);
            refreshTreeView();
         }
         else
            logError ("Renamed failed: duplicate or missing name provided");
      }
   
   } // End renameGroup()
   
   private void logMessage(String message, boolean showInStatusBar) {
      StyleRange styleRange1 = new StyleRange();
      styleRange1.start = textLog.getCharCount();
      styleRange1.length = message.length();
      styleRange1.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
      styleRange1.fontStyle = SWT.NORMAL;
      
      textLog.append(message + "\r\n");
      textLog.setStyleRange(styleRange1);
      textLog.setSelection(textLog.getCharCount());
      
      if(showInStatusBar) {
         setStatus(message);
      }
   } // End logMessage()

   private void logError(String message) {
      StyleRange styleRange1 = new StyleRange();
      styleRange1.start = textLog.getCharCount();
      styleRange1.length = message.length();
      styleRange1.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_RED);
      styleRange1.fontStyle = SWT.NORMAL;    
      
      textLog.append(message + "\r\n");
      textLog.setStyleRange(styleRange1);
      textLog.setSelection(textLog.getCharCount());
   }

   public static void main(String[] args) {
      ApplicationWindow window = new SlocWindow(null);
      window.setBlockOnOpen(true);

      window.open();
      Display.getCurrent().dispose();
   }
   
} // End class SlocWindow
