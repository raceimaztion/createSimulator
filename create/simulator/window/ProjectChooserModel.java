package create.simulator.window;

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;

public class ProjectChooserModel extends DefaultComboBoxModel
{
	private static final long serialVersionUID = 9287598745L;
	
	private String[] projectList;
	
	public ProjectChooserModel()
	{
		projectList = MainLauncher.getSketchNames();
		
		Arrays.sort(projectList);
		
		for (String project : projectList)
			addElement(project);
	}
	
	public void updateProjectList()
	{
		int index = getIndexOf(getSelectedItem());
		String selected = projectList[index];
		
		String[] newProjectList = MainLauncher.getSketchNames();
		Arrays.sort(newProjectList);
		
		// If the arrays are the same, do nothing.
		boolean equal = true;
		if (projectList.length == newProjectList.length)
		{
			for (int i=projectList.length-1; i >= 0 && equal; i--)
				if (!projectList[i].equals(newProjectList[i]))
					equal = false;
		}
		if (equal)
			return;
		
		removeAllElements();
		
		for (String project : newProjectList)
			addElement(project);
		
		projectList = newProjectList;
		
		// Find the previously-selected element and select it
		int newIndex = Arrays.binarySearch(newProjectList, selected);
		if (newIndex >= 0)
			setSelectedItem(selected);
		else if (projectList.length > 0)
			setSelectedItem(projectList[Math.min(index, projectList.length-1)]);
		else
			setSelectedItem(null);
	} // end updateProjectList()
	
	public String getSelectedProject()
	{
		return projectList[getIndexOf(getSelectedItem())];
	}
}
