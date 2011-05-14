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
	private Component component;
	/**
	 * The list of listeners.
	 */
	protected Vector<TabSelectionListener> listeners = new Vector<TabSelectionListener>();
	/**
	 * Whether we are the currently-selected tab.
	 */
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
		this.component = component;
		
		isUs = false;
		
		tabbedPane.addChangeListener(this);
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
		if (e.getSource() != editor)
			return;
		
		int index = tabbedPane.indexOfComponent(component);
		if (index >= 0)
		{
			if (editor.isDirty())
				tabbedPane.setTitleAt(index, editor.getFileName() + "*");
			else
				tabbedPane.setTitleAt(index, editor.getFileName());
		}
		else
			System.err.println("ProjectTab.propertyChange(): Component given is not in the JTabbedPane!");
		
		if (isUs)
		{
			for (TabSelectionListener listener : listeners)
				listener.selectedTabChanged(this);
		}
	} // end propertyChange()
	
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == tabbedPane)
		{
			isUs = (tabbedPane.getSelectedComponent() == component);
			System.out.print("ProjectTab.stateChanged(): isUs=");
			System.out.println(isUs);
			if (isUs)
			{
				for (TabSelectionListener listener : listeners)
					listener.selectedTabChanged(this);
			}
		}
	} // end stateChanged()
	
	public String getProjectName()
	{
		return project.getProjectName();
	}
	
	public String getModuleName()
	{
		return editor.getFileName();
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
