package create.simulator.window;

import java.awt.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import org.fife.ui.rsyntaxtextarea.*;

public class ProjectTab implements PropertyChangeListener, ChangeListener
{
	/**
	 * The TextEditorPane we're controlling.
	 */
	protected TextEditorPane editor;
	/**
	 * The JTabbedPane we're cooperating with.
	 */
	protected JTabbedPane tabbedPane;
	/**
	 * The CreateProject we're coordinating.
	 */
	protected CreateProject project;
	/**
	 * This is the Component that's actually in the JTabbed pane
	 */
	protected Component component;
	/**
	 * The list of listeners.
	 */
	protected Vector<TabSelectionListener> listeners = new Vector<TabSelectionListener>();
	
	protected boolean isUs;
	
	public ProjectTab(CreateProject project, TextEditorPane editor, JTabbedPane tabbedPane)
	{
		this(project, editor, tabbedPane, editor);
	}
	
	public ProjectTab(CreateProject project, TextEditorPane editor, JTabbedPane tabbedPane, Component component)
	{
		this.project = project;
		this.editor = editor;
		this.tabbedPane = tabbedPane;
		
		isUs = false;
		
		editor.addPropertyChangeListener(TextEditorPane.DIRTY_PROPERTY, this);
	}
	
	public void dispose()
	{
		editor.removePropertyChangeListener(TextEditorPane.DIRTY_PROPERTY, this);
	}
	
	public void saveModule() throws IOException
	{
		editor.save();
	}
	
	public boolean isDirty()
	{
		return editor.isDirty();
	}
	
	public void addTabSelectionListener(TabSelectionListener l)
	{
		listeners.add(l);
	}
	
	public void removeTabSelectionListener(TabSelectionListener l)
	{
		listeners.remove(l);
	}

	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getSource() == editor)
		{
			int index = tabbedPane.indexOfTabComponent(component);
			if (index >= 0)
			{
				if (editor.isDirty())
				{
					tabbedPane.setTitleAt(index, project.getProjectName() + "*");
				}
				else
				{
					tabbedPane.setTitleAt(index, project.getProjectName());
				}
				
				if (isUs)
				{
					for (TabSelectionListener listener : listeners)
						listener.selectedTabChanged(this);
				}
			}
			else
				throw new ArrayIndexOutOfBoundsException();
		}
	} // end propertyChange()
	
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == tabbedPane)
		{
			isUs = (tabbedPane.getSelectedComponent() == editor);
			if (isUs)
			{
				for (TabSelectionListener listener : listeners)
					listener.selectedTabChanged(this);
			}
		}
	}
	
	public String getProjectName()
	{
		return project.getProjectName();
	}
	
	public String getModuleName()
	{
		return null;
	}
	
	public CreateProject getProject()
	{
		return project;
	}
	
	public TextEditorPane getEditor()
	{
		return editor;
	}
}
