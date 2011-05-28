package create.simulator.window;

import create.simulator.icons.*;
import create.simulator.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import gnu.io.*;
import javax.swing.*;
import javax.swing.border.*;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

public class EditorWindow implements ActionListener, WindowListener, TabSelectionListener
{
	public static final String COMMAND_SAVE_MODULE = "save-module";
	public static final String COMMAND_NEW_PROJECT = "new-project";
	public static final String COMMAND_LOAD_PROJECT = "load-project";
	public static final String COMMAND_CLOSE_PROJECT = "close-project";
	public static final String COMMAND_PROJECT_PROPERTIES = "project-properties";
	public static final String COMMAND_PROJECT_EXIT = "exit-project";
	public static final String COMMAND_EDIT_FIND = "edit-find";
	public static final String COMMAND_RUN_SIMULATOR = "run-simulator";
	public static final String COMMAND_RUN_SERIAL= "run-serial";
	public static final String COMMAND_RUN_EMBEDDED = "run-embedded";
	public static final String COMMAND_HELP_ABOUT = "help-about";
	public static final String COMMAND_CHOOSE_PROJECT = "choose-project";
	
	public static final Icon ICON_SOURCE = IconLoader.getIcon("source.png");
	
	/**
	 * The CreateProject associated with this EditorWindow.
	 * If this is null, don't show the editing panel, instead show the project choosing panel
	 */
	protected CreateProject project = null;
	
	protected JFrame window;
	
	protected CardLayout windowLayout;
	
	/**
	 * Contains all the widgets necessary for creating a new project.
	 */
	protected JPanel creatorPanel;
	protected static final String CREATOR_PANEL = "CREATOR";
	protected JTextField creatorName;
	protected JButton creatorCreate;
	
	/**
	 * Contains all the widgets necessary for selecting an existing project or creating a new one.
	 */
	protected JPanel chooserPanel;
	protected static final String CHOOSER_PANEL = "CHOOSER";
	
	protected JScrollPane chooserScroller;
	
	protected JList chooserList;
	protected ProjectChooserModel chooserModel;
	
	protected JButton chooserLoad;
	
	/**
	 * Contains all the widgets necessary for the development and testing of projects. 
	 */
	protected JPanel editingPanel;
	protected static final String EDITING_PANEL = "EDITING";
	
	protected JToolBar editorToolbar;
	protected JTabbedPane editorTabs;
	protected JLabel statusBar;
	
	protected Vector<ProjectTab> projectTabs = new Vector<ProjectTab>();
	protected ProjectTab selectedTab;
	
	protected EventAction actionModuleSave;
	protected EventAction actionProjectNew;
	protected EventAction actionProjectLoad;
	protected EventAction actionProjectClose;
	protected EventAction actionProjectProperties;
	protected EventAction actionProjectExit;
	
	protected Action actionEditCut;
	protected Action actionEditCopy;
	protected Action actionEditPaste;
	protected EventAction actionEditFind;
	
	protected EventAction actionRunSimulator;
	protected EventAction actionRunSerial;
	protected EventAction actionRunEmbedded;
	
	protected EventAction actionHelpAbout;
	
	protected Container mainContainer;
	
	protected BuildProblemDialog buildProblemDialog;
	protected SerialPortChooserDialog portChooserDialog;
	
	/**
	 * Creates a new EditorWindow with no CreateProject.
	 */
	public EditorWindow()
	{
		setup();
	}
	
	/**
	 * Creates a new EditorWindow with the given CreateProject to start editing.
	 * @param project The CreateProject to edit.
	 */
	public EditorWindow(CreateProject project)
	{
		setup();
		setProject(project);
	}
	
	/**
	 * Used by all constructors to set up the window.
	 */
	private void setup()
	{
		window = new JFrame("Create Simulator: Editor");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setMinimumSize(new Dimension(300, 450));
		window.addWindowListener(this);
		
		windowLayout = new CardLayout();
		mainContainer = window.getContentPane();
		mainContainer.setLayout(windowLayout);
		
		// Create all the EventActions:
		actionModuleSave = EventAction.createEventAction("Save module", COMMAND_SAVE_MODULE, this);
		actionProjectNew = EventAction.createEventAction("New project...", COMMAND_NEW_PROJECT, CreateUtils.loadIcon("new-project.png"), this);
		actionProjectLoad = EventAction.createEventAction("Load project...", COMMAND_LOAD_PROJECT, this);
		actionProjectClose = EventAction.createEventAction("Close project", COMMAND_CLOSE_PROJECT, this);
		actionProjectProperties = EventAction.createEventAction("Project properties...", COMMAND_PROJECT_PROPERTIES, this);
		actionProjectExit = EventAction.createEventAction("Exit", COMMAND_PROJECT_EXIT, this);
		actionEditFind = EventAction.createEventAction("Find...", COMMAND_EDIT_FIND, this);
		actionRunSimulator = EventAction.createEventAction("Run simulator...", COMMAND_RUN_SIMULATOR, this);
		actionRunSerial = EventAction.createEventAction("Run serial control...", COMMAND_RUN_SERIAL, this);
		actionRunEmbedded = EventAction.createEventAction("Run on Command Module...", COMMAND_RUN_EMBEDDED, this);
		
		actionEditCut = RTextArea.getAction(RTextArea.CUT_ACTION);
		actionEditCopy = RTextArea.getAction(RTextArea.COPY_ACTION);
		actionEditPaste = RTextArea.getAction(RTextArea.PASTE_ACTION);
		
		actionModuleSave.setEnabled(false);
		
		// The Editing card:
		editingPanel = new JPanel(new BorderLayout());
		
		editorToolbar = new JToolBar();
		editorToolbar.setFloatable(false);
		editorToolbar.add(EventAction.createActionToolbarButton(actionProjectNew));
		editorToolbar.addSeparator();
		editorToolbar.add(EventAction.createActionToolbarButton(actionEditCut));
		editorToolbar.add(EventAction.createActionToolbarButton(actionEditCopy));
		editorToolbar.add(EventAction.createActionToolbarButton(actionEditPaste));
		editorToolbar.addSeparator();
		editorToolbar.add(EventAction.createActionToolbarButton(actionEditFind));
		editingPanel.add(editorToolbar, BorderLayout.NORTH);
		
		editorTabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		editingPanel.add(editorTabs, BorderLayout.CENTER);
		
		statusBar = new JLabel("Ready");
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		editingPanel.add(statusBar, BorderLayout.SOUTH);
		
		JMenuBar menubar = new JMenuBar();
		
		JMenu menuProject = new JMenu("Project");
		menuProject.add(EventAction.createActionMenuItem(actionModuleSave));
		menuProject.addSeparator();
		menuProject.add(EventAction.createActionMenuItem(actionProjectNew));
		menuProject.add(EventAction.createActionMenuItem(actionProjectLoad));
		menuProject.add(EventAction.createActionMenuItem(actionProjectProperties));
		menuProject.add(EventAction.createActionMenuItem(actionProjectClose));
		menuProject.addSeparator();
		menuProject.add(EventAction.createActionMenuItem(actionProjectExit));
		menubar.add(menuProject);
		
		JMenu menuEdit = new JMenu("Edit");
		menuEdit.add(EventAction.createActionMenuItem(actionEditCut));
		menuEdit.add(EventAction.createActionMenuItem(actionEditCopy));
		menuEdit.add(EventAction.createActionMenuItem(actionEditPaste));
		menuEdit.addSeparator();
		menuEdit.add(EventAction.createActionMenuItem(actionEditFind));
		menubar.add(menuEdit);
		
		JMenu menuRun = new JMenu("Run");
		menuRun.add(EventAction.createActionMenuItem(actionRunEmbedded));
		menuRun.add(EventAction.createActionMenuItem(actionRunSerial));
		menuRun.add(EventAction.createActionMenuItem(actionRunSimulator));
		menubar.add(menuRun);
		
		mainContainer.add(editingPanel, EDITING_PANEL);
		window.setJMenuBar(menubar);
		
		// The Chooser card:
		chooserPanel = new JPanel(new BorderLayout(3, 3));
		
		chooserModel = new ProjectChooserModel();
		chooserList = new JList(chooserModel);
		chooserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		chooserLoad = new JButton("Load project");
		chooserLoad.addActionListener(this);
		chooserLoad.setActionCommand(COMMAND_CHOOSE_PROJECT);
		
		chooserPanel.add(new JLabel("Choose a project to load:"), BorderLayout.NORTH);
		chooserPanel.add(chooserList, BorderLayout.CENTER);
		chooserPanel.add(chooserLoad, BorderLayout.SOUTH);
		
		mainContainer.add(chooserPanel, CHOOSER_PANEL);
		
		// Add KeyStroke shortcuts to the EventActions:
		actionModuleSave.addKeyStroke(KeyStroke.getKeyStroke("ctrl S"), (JComponent)mainContainer);
		
		// Set the chooser panel as the default
		windowLayout.show(mainContainer, CHOOSER_PANEL);
		
		window.pack();
		window.setLocationRelativeTo(null);
		
		buildProblemDialog = new BuildProblemDialog(window);
		portChooserDialog = new SerialPortChooserDialog(window);
	} // end setup()
	
	/**
	 * Initializes the window with everything needed to display the given CreateProject.
	 * @param project
	 */
	protected void setProject(CreateProject project)
	{
		if (project == null)
		{
			closeProject();
			return;
		}
		
		this.project = project;
		
		windowLayout.show(mainContainer, EDITING_PANEL);
		
		String[] moduleNames = project.getModuleNames();
		for (String name : moduleNames)
		{
			// Load a tab for this module
			TextEditorPane editor = project.loadModule(name);
			if (editor == null)
				continue;
			
			RTextScrollPane scroller = new RTextScrollPane(editor, true);
			
			ProjectTab tab = new ProjectTab(project, editor, editorTabs, scroller);
			tab.addTabSelectionListener(this);
			projectTabs.add(tab);
			editorTabs.addTab(name, ICON_SOURCE, scroller);
		}
		
		if (projectTabs.size() > 0)
		{
			editorTabs.setSelectedIndex(0);
			selectedTab = projectTabs.get(0);
		}
	}
	
	/**
	 * Closes the current CreateProject and restores the window to its "pre-project" state.
	 */
	protected void closeProject()
	{
		if (project == null)
			return;
		
		project = null;
		selectedTab = null;
		
		projectTabs.removeAllElements();
		editorTabs.removeAll();
		
		windowLayout.show(mainContainer, CHOOSER_PANEL);
	}
	
	/**
	 * Makes the EditorWindow visible.
	 */
	public void show()
	{
		window.setVisible(true);
	}
	
	/**
	 * Makes the EditorWindow invisible.
	 */
	public void hide()
	{
		window.setVisible(false);
	}
	
	/**
	 * Sets the visibility of the EditorWindow.
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		window.setVisible(visible);
	}
	
	/**
	 * Check whether the EditorWindow is visible.
	 * @return
	 */
	public boolean isVisible()
	{
		return window.isVisible();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command == null)
		{
			// All EventActions are supposed to have commands associated with them.
			System.out.println("Received an ActionEvent with no command!");
			return;
		}
		
		System.out.printf("Received a command: %s\n", command);
		
		if (command.equals(COMMAND_SAVE_MODULE))
		{
			// Project -> Save module
			if (selectedTab != null)
			{
				try
				{
					selectedTab.getEditor().save();
				}
				catch (IOException er)
				{
					
				}
			}
		}
		else if (command.equals(COMMAND_NEW_PROJECT))
		{
			// Project -> New project...
			if (project != null)
			{
				// Open a new window
			}
			else
			{
				// Use this window
			}
		}
		else if (command.equals(COMMAND_LOAD_PROJECT))
		{
			// Project -> Load project...
			EditorWindow newWindow = new EditorWindow();
			newWindow.show();
		}
		else if (command.equals(COMMAND_CLOSE_PROJECT))
		{
			// Project -> Close project
			closeProject();
		}
		else if (command.equals(COMMAND_PROJECT_PROPERTIES))
		{
			// Project -> Project properties...
		}
		else if (command.equals(COMMAND_PROJECT_EXIT))
		{
			// Project -> Exit
			window.setVisible(false);
			window.dispose();
		}
		else if (command.equals(COMMAND_EDIT_FIND))
		{
			// Edit -> Find
		}
		else if (command.equals(COMMAND_HELP_ABOUT))
		{
			// Help -> About
		}
		else if (command.equals(COMMAND_CHOOSE_PROJECT))
		{
			// Choose -> Load project
			String projectName = chooserModel.getSelectedProject();
			
			CreateProject newProject = CreateProject.loadProject(projectName);
			if (newProject != null)
				setProject(newProject);
		}
		else if (command.equals(COMMAND_RUN_EMBEDDED))
		{
			// Run -> Run on Command Module...
			try
			{
				// Compile the project
				project.buildEmbeddedProject();
				
				// Choose a serial port
				CommPortIdentifier portId = portChooserDialog.choosePort("<html>Choose a serial port to download the code to the Command Module with:</html>");
				if (portId == null)
					return;
				else
					project.downloadToRobot(portId);
				
				// Tell the project to download to the robot
				
				JOptionPane.showMessageDialog(window, "Compile completed successfully.", "Create Simulator", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (BuildProblem er)
			{
				buildProblemDialog.showMessage(er);
			}
		}
		else if (command.equals(COMMAND_RUN_SERIAL))
		{
			// Run -> Run serial control...
			try
			{
				project.buildSerialProject();
				
				JOptionPane.showMessageDialog(window, "Compile completed successfully.", "Create Simulator", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (BuildProblem er)
			{
				buildProblemDialog.showMessage(er);
			}
		}
		else if (command.equals(COMMAND_RUN_SIMULATOR))
		{
			// Run -> Run simulator...
			try
			{
				project.buildSimulatorProject();
				
				// If we got here, we succeeded
				JOptionPane.showMessageDialog(window, "Compile completed successfully.", "Create Simulator", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (BuildProblem er)
			{
				buildProblemDialog.showMessage(er);
			}
		}
	} // end actionPerformed()

	public void windowActivated(WindowEvent e)
	{
		
	}

	public void windowClosed(WindowEvent e)
	{
		
	}

	public void windowClosing(WindowEvent e)
	{
		
	}

	public void windowDeactivated(WindowEvent e)
	{
		
	}

	public void windowDeiconified(WindowEvent e)
	{
		
	}

	public void windowIconified(WindowEvent e)
	{
		
	}

	public void windowOpened(WindowEvent e)
	{
		
	}

	public void selectedTabChanged(ProjectTab selectedTab)
	{
		System.out.println("EditorWindow.selectedTabChanged()");
		this.selectedTab = selectedTab;
		actionModuleSave.setEnabled(selectedTab.isDirty());
	}
}
