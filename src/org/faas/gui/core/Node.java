package org.faas.gui.core;

import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.faas.topology.Actuator;
import org.faas.topology.EndDeviceGroup;
import org.faas.topology.NodeGroup;
import org.faas.topology.Sensor;


/**
 * The model that represents node (host or vm) for the graph.
 * 
 */
public class Node implements Serializable , SelectableElementIF {
	private static final long serialVersionUID = 823544330517091616L;

	private Coordinates coord;
	private String name;
	private String type;
	private boolean isPlaced;
	
	private Shape shape;
	
	private int width = 40;
	private int height = 40;

	private int defaultHeight = 40;

	private Object data;
	private List<NodeGroupGui> children = new ArrayList<NodeGroupGui>();
	
	public Node() {
		setPlaced(false);
		coord = new Coordinates();
	}

	public Node(String name, String type) {
		this.name = name;
		this.type = type;
		setPlaced(false);
		coord = new Coordinates();
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	private boolean isSelected;
	
	@Override
	public void setSelected(boolean bool) {
		isSelected = bool;
	}

	@Override
	public boolean isSelected() {
		return isSelected;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public int getId() {
		int id = -1;
		if (this.data instanceof NodeGroup) {
			return ((NodeGroup)data).getId();
		} else if (this.data instanceof EndDeviceGroup) {
			return ((EndDeviceGroup)data).getId();
		} else if (this.data instanceof Sensor) {
			return ((Sensor)data).getId();
		} else if (this.data instanceof Actuator) {
			return ((Actuator)data).getId();
		}
		return id;
	}
	
	public void setCoordinate(Coordinates coord) {
		this.coord.setX(coord.getX());
		this.coord.setY(coord.getY());
		
		if (shape == null) {
			
			RoundRectangle2D.Double rect = new RoundRectangle2D.Double();
			double x = coord.getX();
			double y = coord.getY();

			if (data instanceof NodeGroup) {
				rect.setRoundRect(x-(width/2), y-(height/2), (double)width, (double)height,10,10);
			} else if (data instanceof Sensor) {
				rect.setRoundRect(x-(width/2), y-(height/2), (double)width, (double)height,10,10);
			} else if (data instanceof Actuator) {
				rect.setRoundRect(x-(width/2), y-(height/2), (double)width, (double)height,10,10);
			} else if (data instanceof EndDeviceGroup) {
//				width = 40;
//				height = 60;
//				EndDeviceGroup endDeviceGroup = (EndDeviceGroup)data;
//				width = (endDeviceGroup.getSensorList().size()+endDeviceGroup.getActuatorList().size())*(width+10)+20;
//
//				if (width < 40) width = 60;

				rect.setRoundRect(x-(width/2), y-(defaultHeight/2), (double)width, (double)height,10,10);
				
			}
			
			shape = rect;
		}
	}

	public Coordinates getCoordinate() {
		return coord;
	}
	
	public boolean contains(double x, double y) {
		if (shape == null) return false;
		
		return shape.contains(x, y);
	}

	public Shape getShape() {
		return shape;
	}
	
	public void setShape(Shape shape) {
		this.shape = shape;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		// CWE-569 : == -> hashCode() == hashCode()
		if (this.hashCode() == obj.hashCode())
			return true;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Node [name=" + name + " type=" + type + "]";
	}

	public boolean isPlaced() {
		return isPlaced;
	}

	public void setPlaced(boolean isPlaced) {
		this.isPlaced = isPlaced;
	}

	public boolean hasChildren() {
		return children.size()>0 ? true : false;
	}
	
	public void addChild(NodeGroupGui child) {
		children.add(child);
	}

}
