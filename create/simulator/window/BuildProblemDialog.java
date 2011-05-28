package create.simulator.window;

import create.simulator.utils.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BuildProblemDialog implements ActionListener
{
	protected JFrame parent;
	
	protected JDialog dialog;
	
	protected JLabel returnValue;
	protected JTextArea errorMessage, commentMessage;
	protected JTabbedPane messageTabs;
	
	protected JButton close;
	
	public BuildProblemDialog(JFrame parent)
	{
		this.parent = parent;
		
		dialog = new JDialog(parent, "Create Simulator - Build Issues", true);
		dialog.setMinimumSize(new Dimension(600, 300));
		
		returnValue = new JLabel("Build complete successfully.");
		
		errorMessage = new JTextArea();
		errorMessage.setEditable(false);
		JScrollPane errorScroll = new JScrollPane(errorMessage);
		
		commentMessage = new JTextArea();
		commentMessage.setEditable(false);
		JScrollPane commentScroll = new JScrollPane(commentMessage);
		
		close = new JButton("Close");
		close.addActionListener(this);
		
		messageTabs = new JTabbedPane();
		messageTabs.add("Standard messages", commentScroll);
		messageTabs.add("Error messages", errorScroll);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(close);
		
		dialog.getContentPane().add(returnValue, BorderLayout.NORTH);
		dialog.getContentPane().add(messageTabs, BorderLayout.CENTER);
		dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		dialog.pack();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		dialog.setVisible(false);
	}
	
	public void showMessage(BuildProblem problem)
	{
		returnValue.setText(String.format("Return value: %d", problem.getReturnValue()));
		
		commentMessage.setText(problem.getCommentData());
		errorMessage.setText(problem.getErrorData());
		
		messageTabs.setSelectedIndex(0);
		
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}
}
