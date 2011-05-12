package create.simulator.window;

import java.awt.*;

public class FilledBoxLayout implements LayoutManager
{
	private static final long serialVersionUID = 2390874945L;
	
	public static final int AXIS_HORIZONTAL = 0x501;
	public static final int AXIS_VERTICAL = 0x502;
	
	protected int axis;
	
	public FilledBoxLayout()
	{
		this(AXIS_VERTICAL);
	}
	
	public FilledBoxLayout(int axis)
	{
		this.axis = axis;
	}
	
	public Dimension maximumLayoutSize(Container target)
	{
		if (axis == AXIS_HORIZONTAL)
		{ // Horizontal
			Dimension size = new Dimension();
			for (Component c : target.getComponents())
			{
				size.height = Math.max(size.height, c.getMaximumSize().height);
				size.width += c.getMaximumSize().width;
			}
			return size;
		}
		else // Vertical
		{
			Dimension size = new Dimension();
			for (Component c : target.getComponents())
			{
				size.width = Math.max(size.width, c.getMaximumSize().width);
				size.height += c.getMaximumSize().height;
			}
			return size;
		}
	} // end maximumLayoutSize()
	
	public Dimension minimumLayoutSize(Container target)
	{
		if (axis == AXIS_HORIZONTAL)
		{ // Horizontal
			Dimension size = new Dimension();
			for (Component c : target.getComponents())
			{
				size.height = Math.max(size.height, c.getMinimumSize().height);
				size.width += c.getMinimumSize().width;
			}
			return size;
		}
		else // Vertical
		{
			Dimension size = new Dimension();
			for (Component c : target.getComponents())
			{
				size.width = Math.max(size.width, c.getMinimumSize().width);
				size.height += c.getMinimumSize().height;
			}
			return size;
		}
	} // end minimumLayoutSize()
	
	public Dimension preferredLayoutSize(Container target)
	{
		if (axis == AXIS_HORIZONTAL)
		{ // Horizontal
			Dimension size = new Dimension();
			for (Component c : target.getComponents())
			{
				size.height = Math.max(size.height, c.getPreferredSize().height);
				size.width += c.getPreferredSize().width;
			}
			return size;
		}
		else // Vertical
		{
			Dimension size = new Dimension();
			for (Component c : target.getComponents())
			{
				size.width = Math.max(size.width, c.getPreferredSize().width);
				size.height += c.getPreferredSize().height;
			}
			return size;
		}
	} // end preferredLayoutSize()
	
	public void layoutContainer(Container target)
	{
		if (axis == AXIS_HORIZONTAL)
		{ // Horizontal
			int height = target.getHeight(), curX = 0;
			for (Component cur : target.getComponents())
			{
				cur.setBounds(curX, 0, cur.getPreferredSize().width, height);
				curX += cur.getWidth();
			}
		}
		else // Vertical
		{
			int width = target.getWidth(), curY = 0;
			for (Component cur : target.getComponents())
			{
				cur.setBounds(0, curY, width, cur.getPreferredSize().height);
				curY += cur.getHeight();
			}
		}
	} // end layoutContainer()

	public void addLayoutComponent(String name, Component comp)
	{
		// Unused method
	}

	public void removeLayoutComponent(Component comp)
	{
		// Unused method
	}
} // end FilledBoxLayout class
