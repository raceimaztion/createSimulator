package create.simulator.window;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import gnu.io.*;

public class SerialPortChooserDialog implements ActionListener
{	
	protected JDialog dialog;
	
	protected JScrollPane scroller;
	protected JList listSerialPorts;
	protected Vector<String> portNamesList;
	protected Vector<CommPortIdentifier> portIdentifiersList;
	protected JLabel message;
	
	protected String selectedName;
	protected Integer selectedIndex;
	
	protected JButton buttonOkay, buttonCancel;
	
	public SerialPortChooserDialog(Frame owner)
	{
		dialog = new JDialog(owner, true);
		dialog.setTitle("CreateSimulator - Choose a serial port");
		
		/*
		 * JDialog dialog(owner, true);
		 * 
		 * JDialog *dialog;
		 * dialog = new JDialog(owner, true);
		 */
		
		portNamesList = new Vector<String>();
		portIdentifiersList = new Vector<CommPortIdentifier>();
		
		listSerialPorts = new JList(portNamesList);
		
		scroller = new JScrollPane(listSerialPorts);
		message = new JLabel("");
		
		buttonOkay = new JButton("Okay");
		buttonCancel = new JButton("Cancel");
		
		buttonOkay.addActionListener(this);
		buttonCancel.addActionListener(this);
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(buttonOkay);
		panel.add(buttonCancel);
		
		dialog.getContentPane().add(message, BorderLayout.NORTH);
		dialog.getContentPane().add(scroller, BorderLayout.CENTER);
		dialog.getContentPane().add(panel, BorderLayout.SOUTH);
		
		dialog.pack();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(buttonOkay))
		{
			selectedIndex = listSerialPorts.getSelectedIndex();
			selectedName = portNamesList.elementAt(selectedIndex);
			dialog.setVisible(false);
		}
		else if (e.getSource().equals(buttonCancel))
		{
			selectedIndex = null;
			selectedName = null;
			dialog.setVisible(false);
		}
	}
	
	/**
	 * Opens the dialog to choose a port.
	 * @return Returns null if no port was chosen.
	 */
	@SuppressWarnings("unchecked")
	public CommPortIdentifier choosePort(String message)
	{
		// Use the given message
		this.message.setText(message);
		
		// Update the list of serial ports
		selectedIndex = listSerialPorts.getSelectedIndex();
		if (selectedIndex >= 0)
			selectedName = portNamesList.get(selectedIndex);
		else
			selectedName = null;
		
		Enumeration en = CommPortIdentifier.getPortIdentifiers();
		portNamesList.clear();
		portIdentifiersList.clear();
		Object obj;
		while ((obj = en.nextElement()) != null)
		{
			if (obj instanceof CommPortIdentifier && ((CommPortIdentifier)obj).getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				portIdentifiersList.add((CommPortIdentifier)obj);
				portNamesList.add(((CommPortIdentifier)obj).getName());
			}
		}
		
		listSerialPorts.setModel(new DefaultComboBoxModel(portNamesList));
		if (selectedName == null)
			listSerialPorts.setSelectedIndex(0);
		else if (portNamesList.contains(selectedName))
			listSerialPorts.setSelectedIndex(portNamesList.indexOf(selectedName));
		else
			listSerialPorts.setSelectedIndex(Math.min(portNamesList.size(), selectedIndex));
		
		// Show the dialog
		dialog.setLocationRelativeTo(dialog.getOwner());
		dialog.setVisible(true);
		
		if (selectedIndex != null)
			return portIdentifiersList.get(selectedIndex);
		else
			return null;
	}
}
