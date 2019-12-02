package org.faas.gui.core;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The model that represents an edge with two vertexes, for physical link and virtual edge.
 * 
 */
public class Edge implements Serializable , SelectableElementIF {
	private static final long serialVersionUID = -356975278987708987L;

	private Node source = null;
	private Node dest = null;
	
	private double latency = 0.0;
	private String name = "";
	private long bandwidth = 0;

	private org.faas.topology.Link link;
	private Shape shape;
	private Shape shape2;
	
	public Edge() {
		
	}
	/**
	 * Constructor.
	 * 
	 * @param node the node that belongs to the edge.
	 */
	public Edge(Node to) {
		this.dest = to;
	}
	
	public Edge(Node from, Node to) {
		this.source = from;
		this.dest = to;
	}
	
	/** physical topology link */
	public Edge(Node to, double latency) {
		this.dest = to;
		this.latency = latency;
	}

	/** virtual virtual edge */
	public Edge(Node to, String name, long bw) {
		this.dest = to;
		this.name = name;
		this.bandwidth = bw;
	}
	
	/** copy edge */
	public Edge(Node to, Map<String, Object> info){
		this.dest = to;
		if(info.get("name")!=null){
			this.name = (String) info.get("name");
		}
		if(info.get("bandwidth")!=null){
			this.bandwidth = (long) info.get("bandwidth");
		}
		if(info.get("latency")!=null){
			this.latency = (double) info.get("latency");
		}
	}
	
	private boolean isSelected;
	
	@Override
	public void setSelected(boolean bool) {
		// TODO Auto-generated method stub
		isSelected = bool;
	}
	@Override
	public boolean isSelected() {
		return isSelected;
	}
	
	public boolean isUpLink() {
		return source.getId() > dest.getId();
	}

	public boolean contains(double x, double y) {
		if (shape2 == null) return false;
		
		return shape2.contains(x, y);
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public Shape getShape2() {
		return shape2;
	}
	
	public void setShape(Shape shape) {

		this.shape = shape;

		double x1= ((Line2D.Double)shape).getX1();
		double x2= ((Line2D.Double)shape).getX2();
		double y1= ((Line2D.Double)shape).getY1();
		double y2= ((Line2D.Double)shape).getY2();

		int half = 10;

		GeneralPath.Double path = new GeneralPath.Double();
		path.moveTo(x1-half, y1);
		path.lineTo(x1+half, y1);
		path.lineTo(x2+half, y2);
		path.lineTo(x2-half, y2);
		path.closePath();

		shape2 = path;
		
//		RoundRectangle2D.Double rect = new RoundRectangle2D.Double();
//		double x = 0;
//		double y = 0;
//		double width = half*2;
//		double height = Math.abs(y2-y1);
//		
//		if (x1 < x2) x = x1;
//		else x = x2;
//		if (y1 < y2) y = y1;
//		else y = y2;
//		
//		rect.setRoundRect(x-half, y, width, height,10,10);
//
//		shape2 = rect;
	}
	
	public org.faas.topology.Link getLinkData() {
		return link;
	}
	
	public void setLink(org.faas.topology.Link link) {
		this.link = link;
	}

	public Node getNode() {
		return dest;
	}

	public long getBandwidth() {
		return bandwidth;
	}
	
	public double getLatency() {
		return latency;
	}
	
	public Map<String, Object> getInfo() {
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("name", this.name);
		info.put("bandwidth",this.bandwidth);
		info.put("latency", this.latency);
		return info;
	}
	
	public void setInfo(Map<String, Object> info){
		if(info.get("name")!=null){
			this.name = (String) info.get("name");
		}
		if(info.get("bandwidth")!=null){
			this.bandwidth = (long) info.get("bandwidth");
		}
		if(info.get("latency")!=null){
			this.latency = (double) info.get("latency");
		}
	}

	@Override
	public String toString() {
		return "Edge [dest=" + dest + "]";
	}

}
