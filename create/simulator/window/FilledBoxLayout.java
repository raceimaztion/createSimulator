package create.simulator.window;

import java.awt.*;
import java.util.*;

public class FilledBoxLayout implements LayoutManager
{
	private static final long serialVersionUID = 2390874945L;
	
	public static final int AXIS_HORIZONTAL = 0x501;
	public static final int AXIS_VERTICAL = 0x502;
	
	public static final String PACK_FILL = "fill";
	public static final String PACK_NOFILL = "nofill";
	
	protected int axis;
	protected Vector<Component> packedFill = new Vector<Component>();
	
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
			// Check the estimated width of the container
			int width = 0;
			for (Component cur : target.getComponents())
				width += cur.getPreferredSize().width;
			
			int extra = target.getSize().width - width;
			int extraEach = extra/Math.max(1, packedFill.size());
			
			// Now resize the components
			int height = target.getHeight(), curX = 0;
			for (Component cur : target.getComponents())
			{
				if (packedFill.contains(cur))
				{
					cur.setBounds(curX, 0, cur.getPreferredSize().width + extraEach, height);
					curX += cur.getWidth() + extraEach;
				}
				else
				{
					cur.setBounds(curX, 0, cur.getPreferredSize().width, height);
					curX += cur.getWidth();
				}
			}
		}
		else // Vertical
		{
			// Check the estimated height of the container
			int height = 0;
			for (Component cur : target.getComponents())
				height += cur.getPreferredSize().height;
			
			int extra = target.getSize().height;
			int extraEach = extra/Math.max(1, packedFill.size());
			
			// Now resize the components
			int width = target.getWidth(), curY = 0;
			for (Component cur : target.getComponents())
			{
				if (packedFill.contains(cur))
				{
					cur.setBounds(0, curY, width, cur.getPreferredSize().height + extraEach);
					curY += cur.getHeight() + extraEach;
				}
				else
				{
					cur.setBounds(0, curY, width, cur.getPreferredSize().height);
					curY += cur.getHeight();
				}
			}
		}
	} // end layoutContainer()

	public void addLayoutComponent(String name, Component comp)
	{
		if (name != null && name.equals(PACK_FILL))
			packedFill.add(comp);
	}

	public void removeLayoutComponent(Component comp)
	{
		packedFill.remove(comp);
	}
} // end FilledBoxLayout class
