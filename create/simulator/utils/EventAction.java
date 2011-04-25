package create.simulator.utils;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class EventAction extends AbstractAction
{
	private static final long serialVersionUID = 123904875L;
	
	protected Vector<ActionListener> listeners = new Vector<ActionListener>();
	
	public EventAction(String description)
	{
		super(description);
	}
	
	public EventAction(String description, Icon icon)
	{
		super(description, icon);
	}
	
	public EventAction(String description, String command)
	{
		super(description);
		super.putValue(Action.ACTION_COMMAND_KEY, command);
	}
	
	public EventAction(String description, String command, Icon icon)
	{
		super(description, icon);
		putValue(Action.ACTION_COMMAND_KEY, command);
	}
	
	public void addActionListener(ActionListener l)
	{
		listeners.add(l);
	}
	
	public void removeActionListener(ActionListener l)
	{
		listeners.remove(l);
	}
	
	/**
	 * Triggers actionPerformed method calls on all the listeners.
	 */
	public void actionPerformed(final ActionEvent e)
	{
		for (ActionListener l : listeners)
			l.actionPerformed(e);
	}
	
	/**
	 * Create and return a new EventAction with the specified ActionListener.
	 * @param description The description to use for this EventAction.
	 * @param l The ActionListener to associate with this EventAction.
	 * @return The newly-created EventAction.
	 */
	public static EventAction createEventAction(String description, ActionListener l)
	{
		EventAction ea = new EventAction(description);
		ea.addActionListener(l);
		return ea;
	}
	
	/**
	 * Create and return a new EventAction with the specified ActionListener.
	 * @param description The description to use for this EventAction.
	 * @param icon The icon to give this EventAction.
	 * @param l The ActionListener to associate with this EventAction.
	 * @return The newly-created EventAction.
	 */
	public static EventAction createEventAction(String description, Icon icon, ActionListener l)
	{
		EventAction ea = new EventAction(description, icon);
		ea.addActionListener(l);
		return ea;
	}
	
	/**
	 * Create and return a new EventAction with the specified ActionListener.
	 * @param description The description to use for this EventAction.
	 * @param command The command to give this EventAction.
	 * @param l The ActionListener to associate with this EventAction.
	 * @return The newly-created EventAction.
	 */
	public static EventAction createEventAction(String description, String command, ActionListener l)
	{
		EventAction ea = new EventAction(description, command);
		ea.addActionListener(l);
		return ea;
	}
	
	/**
	 * Create and return a new EventAction with the specified ActionListener.
	 * @param description The description to use for this EventAction.
	 * @param command The command to give this EventAction.
	 * @param icon The icon to give this EventAction.
	 * @param l The ActionListener to associate with this EventAction.
	 * @return The newly-created EventAction.
	 */
	public static EventAction createEventAction(String description, String command, Icon icon, ActionListener l)
	{
		EventAction ea = new EventAction(description, command, icon);
		ea.addActionListener(l);
		return ea;
	}
	
	/**
	 * Create and return a new JButton that wraps the given EventAction.
	 * @param ea The EventAction to have the JButton wrap.
	 * @return The new JButton.
	 */
	public static JButton createActionButton(EventAction ea)
	{
		return new JButton(ea);
	}
	
	/**
	 * Create and return a new JButton that wraps the given EventAction for use in a JToolBar.
	 * @param ea The EventAction to have the JButton wrap.
	 * @return The new JButton.
	 */
	public static JButton createActionToolbarButton(EventAction ea)
	{
		JButton button = new JButton(ea);
		if (button.getIcon() != null)
		{
			// If we have an icon, use the button's label as its tooltip-text instead  
			button.setToolTipText(button.getText());
			button.setText("");
		}
		return button;
	}
	
	/**
	 * Create and return a new JMenuItem that wraps the given EventAction.
	 * @param ea The EventAction to have the JMenuItem wrap.
	 * @param ea The EventAction to have the JMenuItem wrap.
	 */
	public static JMenuItem createActionMenuItem(EventAction ea)
	{
		JMenuItem item = new JMenuItem(ea);
		item.setIcon(null);
		return item;
	}
	
	/**
	 * Create and return a new JMenuItem that wraps the given EventAction.
	 * @param ea The EventAction to have the JMenuItem wrap.
	 * @param accelerator The KeyStroke used to activate this menu item.
	 * @param ea The EventAction to have the JMenuItem wrap.
	 */
	public static JMenuItem createActionMenuItem(EventAction ea, KeyStroke accelerator)
	{
		JMenuItem item = new JMenuItem(ea);
		item.setIcon(null);
		item.setAccelerator(accelerator);
		return item;
	}
}
