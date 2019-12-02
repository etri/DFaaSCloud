package org.faas.gui.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.faas.topology.EndDeviceGroup;
import org.faas.topology.NodeIF;
import org.faas.utils.DFaaSUtils;

public class GraphCanvas extends JPanel implements MouseMotionListener, MouseListener {

	private Graph graph;
	
	private Image imgHost;
	private Image imgSensor;
	private Image imgSwitch;
	private Image imgAppModule;
	private Image imgActuator;
	private Image imgSensorModule;
	private Image imgActuatorModule;
	private Image imgEndDeviceGroup;
	
	private SelectableElementIF selectedElement;

	private GraphCanvasListener graphCanvasListener;
	
	private ElementSelectionHistory history = new ElementSelectionHistory();
	
	public void setGraphCanvasListener(GraphCanvasListener graphCanvasListener) {
		this.graphCanvasListener = graphCanvasListener;
	}
	
	public GraphCanvas(Graph graph) {
		this.graph = graph;
		
		imgHost = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/images/host.png"));
		imgSwitch = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/images/disk.png"));
		imgAppModule = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/images/module.png"));
		imgSensor = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/images/sensor.png"));
		imgActuator = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/images/actuator.png"));
		imgSensorModule = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/images/sensorModule.png"));
		imgActuatorModule = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/images/actuatorModule.png"));
		imgEndDeviceGroup = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/images/endDeviceGroup.png"));
		
		this.addMouseMotionListener(this);
		this.addMouseListener(this);

	}
	
	public void setGraph(Graph newGraph){
		this.graph = newGraph;
		this.coordForNodes = null;
		this.history.clear();
		
		boolean found = false;
		if (selectedElement != null) {
			if (selectedElement instanceof Node) {
				Node selectedNode = (Node)selectedElement;
				NodeIF selectedNodeIF = (NodeIF)selectedNode.getData();

				clearSelection();
				
				Iterator<Node> ite = newGraph.getAdjacencyList().keySet().iterator();
				while (ite.hasNext()) {
					Node node = ite.next();
					NodeIF nodeIF = (NodeIF)node.getData();
					if (nodeIF.getId() == selectedNodeIF.getId()) {
						this.setSelection(node);
						found = true;
						break;
					}
				}
			} else {
				// TODO 
			}
		}
		if (found == false) {
			selectedElement = null;
		}
		
		repaint();
	}
	
	public ElementSelectionHistory getHistory() {
		return this.history;
	}
	
	public EndDeviceGroup getSelectedEndDeviceNode() {
		if (selectedElement instanceof NodeGroupGui) {
			if (((NodeGroupGui)selectedElement).getData() instanceof EndDeviceGroup) {
				return (EndDeviceGroup)((NodeGroupGui)selectedElement).getData();
			}
		}
		return null;
	}
	
	public SelectableElementIF getSelectedElement() {
		return this.selectedElement;
	}
	
	private void elementSelected(Object element) {
		
		clearSelection();
		
		if (element instanceof Edge) {
			Edge link = (Edge)element;
			if (graphCanvasListener != null) {
				graphCanvasListener.linkSelected(link);
			}

			//selectedElement = link;
			setSelection(link);
			repaint();
		
		} if (element instanceof NodeGroupGui) {
			NodeGroupGui fogDevice = (NodeGroupGui)element;
			
			if (graphCanvasListener != null) {
				graphCanvasListener.nodeSelected(fogDevice);
			}

			//selectedElement = fogDevice;
			setSelection(fogDevice);
			repaint();
			
			history.nodeSelected(fogDevice);
		}
		
	}
	
	private void setSelection(SelectableElementIF node) {
		selectedElement = node;
		selectedElement.setSelected(true);
		if (selectedElement instanceof Node) {
			Node pairedNode = this.graph.getPairedNode((Node)selectedElement);
			if (pairedNode != null) {
				pairedNode.setSelected(true);
			}
		}

	}
	
	private void clearSelection() {
		if (selectedElement != null) {
			selectedElement.setSelected(false);
			if (selectedElement instanceof Node) {
				Node pairedNode = this.graph.getPairedNode((Node)selectedElement);
				if (pairedNode != null) {
					pairedNode.setSelected(false);
				}
			}

		}
		selectedElement = null;
	}
	
	@Override
    public void mouseDragged(MouseEvent e) {
    	
    }

	@Override
    public void mouseMoved(MouseEvent e) {
//		Object element = graph.findElement(e.getX(), e.getY());
//		
//		if (element != null) {
//			elementSelected(element);
//		}
    }
    
	@Override
	public void mouseClicked(MouseEvent e) {
		Object element = graph.findElement(e.getX(), e.getY());

		if (element != null) {
			//System.out.println(this.getClass().getName()+" "+element);
			elementSelected(element);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void paintInit(Graphics g) {
		if (coordForNodes == null) {
			JPanel canvas = this;

			coordForNodes = new HashMap<Node, Coordinates>();

			/*int offsetX = canvas.getWidth() / 2;
			int offsetY = canvas.getHeight() / 2;*/
			//System.out.println("sys:"+canvas.getWidth() + ":" + canvas.getHeight());

			/*double angle = 2 * Math.PI / graph.getAdjacencyList().keySet().size();
			int radius = offsetY / 2 - 20;*/

			int maxLevel=-1, minLevel= DFaaSUtils.MAX;
			Map<Integer, List<Node>> levelMap = new HashMap<Integer, List<Node>>();
			List<Node> endpoints = new ArrayList<Node>(); 
			for (Node node : graph.getAdjacencyList().keySet()) {
				if(node.getType().equals("FOG_DEVICE")){
					int level = ((NodeGroupGui)node).getLevel();
					if(!levelMap.containsKey(level))
						levelMap.put(level, new ArrayList<Node>());
					levelMap.get(level).add(node);
					
					if(level > maxLevel)
						maxLevel = level;
					if(level < minLevel)
						minLevel = level;
				} else if (node.getType().equals("END_DEVICE")) {
					endpoints.add(node);
				} else if(node.getType().equals("SENSOR") || node.getType().equals("ACTUATOR")){
					//endpoints.add(node);
				}
			}
			
			//double yDist = canvas.getHeight()/(maxLevel-minLevel+3);
			double yDist = canvas.getHeight()/(maxLevel-minLevel+3);
			//System.out.println("===================\n================\n=============");
			
			Map<Integer, List<PlaceHolder>> levelToPlaceHolderMap = new HashMap<Integer, List<PlaceHolder>>();
			
			int k=1;
			for(int i=minLevel;i<=maxLevel;i++, k++){
				
				double xDist = canvas.getWidth()/(levelMap.get(i).size()+1);
				
				for(int j=1;j<=levelMap.get(i).size();j++){
					int x = (int)xDist*j;
					int y = (int)yDist*k;
					if(!levelToPlaceHolderMap.containsKey(i))
						levelToPlaceHolderMap.put(i, new ArrayList<PlaceHolder>());
					levelToPlaceHolderMap.get(i).add(new PlaceHolder(x, y));
					
					//coordForNodes.put(node, new Coordinates(x, y));
					//node.setCoordinate(new Coordinates(x, y));
				}
			}
			
			List<PlaceHolder> endpointPlaceHolders = new ArrayList<PlaceHolder>();
			
			Collections.sort(endpoints,new Comparator<Node>() {

				@Override
				public int compare(Node o1, Node o2) {
					// TODO Auto-generated method stub
					return o1.getId() - o2.getId();
				}
				
			});
			double xDist = canvas.getWidth()/(endpoints.size()+1);
			for(int i=0;i<endpoints.size();i++){
				NodeGroupGui node = (NodeGroupGui)endpoints.get(i);
				int x = (int)xDist*(i+1);
				int y = (int)yDist*k;
				endpointPlaceHolders.add(new PlaceHolder(x, y));
				
				coordForNodes.put(node, new Coordinates(x, y));
				node.setCoordinate(new Coordinates(x, y));
				
//				if (node.hasChildren()) {
//					List<NodeGroupGui> children = node.getChildren();
//					int cx = (int)node.getShape().getBounds2D().getX()+40;//+(int)(node.getShape().getBounds2D().getWidth()/2);
//					int cy = (int)node.getShape().getBounds2D().getY()+(int)(node.getShape().getBounds2D().getHeight()/2);
//					cx += 1;
//					//cy += 15;
//					cy -= 5;
//					for (int j=0;j<children.size();j++) {
//						NodeGroupGui child = children.get(j);
//						
//						coordForNodes.put(child, new Coordinates(cx, cy));
//						child.setCoordinate(new Coordinates(cx, cy));
//						
//						cx += (45);
//					}
//				}
			}
			
			coordForNodes = getCoordForNodes(levelToPlaceHolderMap, endpointPlaceHolders, levelMap, endpoints, minLevel, maxLevel);
			//System.out.println("COORD MAP"+coordForNodes);

		}
	}
	
	private Map<Node, Coordinates> coordForNodes;

	private Font plainFont = new Font("Serif", Font.PLAIN, 14);
	private Font boldFont = new Font("Serif", Font.BOLD, 14);

	private Color selectionColor = Color.RED;
	
	@Override
	public void paint(Graphics g) {
		g.setFont(boldFont);
		
		Graphics2D g2 = (Graphics2D)g;
		
		JPanel canvas = this;
		
		if (graph.getAdjacencyList() == null) {
			return;
		}

		g.setColor(Color.white);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		
		Map<Node, List<Node>> drawnList = new HashMap<Node, List<Node>>();

		paintInit(g);

		int height = 40;
		FontMetrics f = g.getFontMetrics();
		int nodeHeight = Math.max(height, f.getHeight());
		int nodeWidth = nodeHeight;
		
		
		// draw edges first
		// TODO: we draw one edge two times at the moment because we have an undirected graph. But this
		// shouldn`t matter because we have the same edge costs and no one will see in. Perhaps refactor later.
		for (Entry<Node, List<Edge>> entry : graph.getAdjacencyList().entrySet()) {

			Coordinates startNode = coordForNodes.get(entry.getKey());
			//System.out.println("Start Node : "+entry.getKey().getName());

			int linkSize = entry.getValue().size() - 1;
			//System.out.println(linkSize+"<<<"+entry.getKey().getName());
			for (Edge edge : entry.getValue()) {

				Coordinates targetNode = coordForNodes.get(edge.getNode());
				//System.out.println("Target Node : "+edge.getNode().getName());
				Shape edgeShape = edge.getShape();
				if (edgeShape == null) {
					int startYGap = 45;
					int endYGap = -25;		
					if (edge.isUpLink()) {
						edgeShape = new Line2D.Double(startNode.getX()-10, startNode.getY()+endYGap
								, targetNode.getX()-10, targetNode.getY()+startYGap);
					} else {
						edgeShape = new Line2D.Double(startNode.getX()+10, startNode.getY()+startYGap
								, targetNode.getX()+10, targetNode.getY()+endYGap);
					}
					
					edge.setShape(edgeShape);
				}
				
				if (edge.isSelected()) {
					//g2.fill(edge.getShape2());
					g.setColor(selectionColor);
					//g2.draw(edge.getShape2());
					//g2.fill(edge.getShape2());
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
				
				//g2.draw(edgeShape);
				drawArrowLine(g,edgeShape);

				//drawArrow(g2, startNode.getX(), startNode.getY(), targetNode.getX(), targetNode.getY());
				// add drawn edges to the drawnList
				if (drawnList.containsKey(entry.getKey())) {
					drawnList.get(entry.getKey()).add(edge.getNode());
				} else {
					List<Node> nodes = new ArrayList<Node>();
					nodes.add(edge.getNode());
					drawnList.put(entry.getKey(), nodes);
				}

			}
		}
		
		// draw nodes
		
		for (Entry<Node, Coordinates> entry : coordForNodes.entrySet()) {
			// first paint a single node for testing.
			g.setColor(Color.black);
			// int nodeWidth = Math.max(width, f.stringWidth(entry.getKey().getNodeText()) + width / 2);

			Coordinates wrapper = entry.getValue();
			Node node = entry.getKey();
			String nodeName = entry.getKey().getName();
			
//			if (node.isSelected()) {
//				g.setColor(Color.LIGHT_GRAY);
//				//g2.draw(node.getShape());
//				g2.fill(node.getShape());
//			}
			

			g.setColor(Color.BLACK);
			String type = entry.getKey().getType();
			switch(type){
				case "host":
					g.drawImage(imgHost, wrapper.getX() - nodeWidth / 2, wrapper.getY() - nodeHeight / 2, nodeWidth, nodeHeight, this);
					break;
				case "APP_MODULE":
					g.drawImage(imgAppModule, wrapper.getX() - nodeWidth / 2, wrapper.getY() - nodeHeight / 2, nodeWidth, nodeHeight, this);
					g.drawString(nodeName, wrapper.getX() - f.stringWidth(nodeName) / 2, wrapper.getY() + nodeHeight);
					break;
				case "core":
				case "edge":
					g.drawImage(imgSwitch, wrapper.getX() - nodeWidth / 2, wrapper.getY() - nodeHeight / 2, nodeWidth, nodeHeight, this);
					break;
				case "END_DEVICE":
					//g.drawString(nodeName, wrapper.getX() - f.stringWidth(nodeName) / 2, wrapper.getY() + nodeHeight + 17);
					//g.setColor(Color.LIGHT_GRAY);
					//g2.draw(node.getShape());
					g.drawString(nodeName, wrapper.getX() - f.stringWidth(nodeName) / 2, wrapper.getY() + nodeHeight);
					g.drawImage(imgEndDeviceGroup, wrapper.getX() - nodeWidth / 2, wrapper.getY() - nodeHeight / 2, nodeWidth, nodeHeight, this);
					break;
				case "FOG_DEVICE":
					g.drawImage(imgHost, wrapper.getX() - nodeWidth / 2, wrapper.getY() - nodeHeight / 2, nodeWidth, nodeHeight, this);
					g.drawString(nodeName, wrapper.getX() - f.stringWidth(nodeName) / 2, wrapper.getY() + nodeHeight - 5);
					break;
				case "SENSOR":
					g.drawImage(imgSensor, wrapper.getX() - nodeWidth / 2, wrapper.getY() - nodeHeight / 2, nodeWidth, nodeHeight, this);
					//g.drawString(nodeName, wrapper.getX() - f.stringWidth(nodeName) / 2, wrapper.getY() + nodeHeight);
					nodeName = ((org.faas.topology.Sensor)entry.getKey().getData()).getId()+"";
					g.drawString(nodeName, wrapper.getX() - f.stringWidth(nodeName) / 2, wrapper.getY() + nodeHeight-10);
					break;
				case "ACTUATOR":
					g.drawImage(imgActuator, wrapper.getX() - nodeWidth / 2, wrapper.getY() - nodeHeight / 2, nodeWidth, nodeHeight, this);
					//g.drawString(nodeName, wrapper.getX() - f.stringWidth(nodeName) / 2, wrapper.getY() + nodeHeight);
					nodeName = ((org.faas.topology.Actuator)entry.getKey().getData()).getId()+"";
					g.drawString(nodeName, wrapper.getX() - f.stringWidth(nodeName) / 2, wrapper.getY() + nodeHeight-10);
					break;
				case "SENSOR_MODULE":
					g.drawImage(imgSensorModule, wrapper.getX() - nodeWidth / 2, wrapper.getY() - nodeHeight / 2, nodeWidth, nodeHeight, this);
					g.drawString(nodeName, wrapper.getX() - f.stringWidth(nodeName) / 2, wrapper.getY() + nodeHeight);
					break;
				case "ACTUATOR_MODULE":
					g.drawImage(imgActuatorModule, wrapper.getX() - nodeWidth / 2, wrapper.getY() - nodeHeight / 2, nodeWidth, nodeHeight, this);
					g.drawString(nodeName, wrapper.getX() - f.stringWidth(nodeName) / 2, wrapper.getY() + nodeHeight);
					break;
				default:
					break;
			}
			
			if (node.isSelected()) {
				//System.out.println(node+" selected");
				g.setColor(selectionColor);
				g2.draw(node.getShape());
				//g2.fill(node.getShape());
			}

		}

	}
	

	private void drawArrowLine(Graphics g,Shape shape) {
		Line2D.Double lineShape = (Line2D.Double)shape;
		drawArrowLine(g,(int)lineShape.getX1(),(int)lineShape.getY1(),(int)lineShape.getX2(),(int)lineShape.getY2(),10,4);
	}
	
	/**
	 * Draw an arrow line between two points.
	 * @param g the graphics component.
	 * @param x1 x-position of first point.
	 * @param y1 y-position of first point.
	 * @param x2 x-position of second point.
	 * @param y2 y-position of second point.
	 * @param d  the width of the arrow.
	 * @param h  the height of the arrow.
	 */
	private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h) {
	    int dx = x2 - x1, dy = y2 - y1;
	    double D = Math.sqrt(dx*dx + dy*dy);
	    double xm = D - d, xn = xm, ym = h, yn = -h, x;
	    double sin = dy / D, cos = dx / D;

	    x = xm*cos - ym*sin + x1;
	    ym = xm*sin + ym*cos + y1;
	    xm = x;

	    x = xn*cos - yn*sin + x1;
	    yn = xn*sin + yn*cos + y1;
	    xn = x;

	    int[] xpoints = {x2, (int) xm, (int) xn};
	    int[] ypoints = {y2, (int) ym, (int) yn};

	    g.drawLine(x1, y1, x2, y2);
	    g.fillPolygon(xpoints, ypoints, 3);
	}
	
	protected Map<Node, Coordinates> getCoordForNodes(
			Map<Integer, List<PlaceHolder>> levelToPlaceHolderMap,
			List<PlaceHolder> endpointPlaceHolders,
			Map<Integer, List<Node>> levelMap, List<Node> endpoints, int minLevel, int maxLevel) {
		
		Map<Node, Coordinates> coordForNodesMap = new HashMap<Node, Coordinates>();
		Map<Node, List<Node>> childrenMap = createChildrenMap();
		
		coordForNodesMap.putAll(coordForNodes);
		
		for(Node node : graph.getAdjacencyList().keySet()) {
			node.setPlaced(false);
		}
		for(Node node : endpoints) {
			node.setPlaced(false);
		}
		
		if(maxLevel < 0)
			return new HashMap<Node, Coordinates>();
		
		int j=0;
		for(PlaceHolder placeHolder : levelToPlaceHolderMap.get(minLevel)){
			Node node = levelMap.get(minLevel).get(j);
			placeHolder.setNode(node);
			node.setCoordinate(placeHolder.getCoordinates());
			coordForNodesMap.put(node, node.getCoordinate());
			node.setPlaced(true);
			j++;
		}
		
		for(int level = minLevel+1;level <= maxLevel; level++){
			List<PlaceHolder> upperLevelNodes = levelToPlaceHolderMap.get(level-1);
			List<Node> nodes = levelMap.get(level);
			Collections.sort(nodes,new Comparator<Node>() {

				@Override
				public int compare(Node o1, Node o2) {
					// TODO Auto-generated method stub
					return o1.getId() - o2.getId();
				}
				
			});
			int i=0;
//			for(PlaceHolder parentPH : upperLevelNodes){
//				List<Node> children = childrenMap.get(parentPH.getNode());
//				for(Node child : children){
//					PlaceHolder childPlaceHolder = levelToPlaceHolderMap.get(level).get(i);
//					childPlaceHolder.setOccupied(true);
//					childPlaceHolder.setNode(child);
//					child.setCoordinate(childPlaceHolder.getCoordinates());
//					coordForNodesMap.put(child, child.getCoordinate());
//					child.setPlaced(true);
//					i++;
//				}
//			}
			for(Node node : nodes){
				if(!node.isPlaced()){
					PlaceHolder placeHolder = levelToPlaceHolderMap.get(level).get(i);
					placeHolder.setOccupied(true);
					placeHolder.setNode(node);
					node.setCoordinate(placeHolder.getCoordinates());
					coordForNodesMap.put(node, node.getCoordinate());
					node.setPlaced(true);
					i++;
				}
			}
		}
		int i=0;
//		for(PlaceHolder parentPH : levelToPlaceHolderMap.get(maxLevel)){
//			List<Node>children = childrenMap.get(parentPH.getNode());
//			for(Node child : children){
//				PlaceHolder placeHolder = endpointPlaceHolders.get(i);
//				placeHolder.setOccupied(true);
//				placeHolder.setNode(child);
//				child.setCoordinate(placeHolder.getCoordinates());
//				coordForNodesMap.put(child, child.getCoordinate());
//				child.setPlaced(true);
//				i++;
//			}
//		}

		Collections.sort(endpoints,new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				// TODO Auto-generated method stub
				return o1.getId() - o2.getId();
			}
			
		});
		
		for(Node node : endpoints){
			if(!node.isPlaced()){
				PlaceHolder placeHolder = endpointPlaceHolders.get(i);
				placeHolder.setOccupied(true);
				placeHolder.setNode(node);
				node.setCoordinate(placeHolder.getCoordinates());
				coordForNodesMap.put(node, node.getCoordinate());
				node.setPlaced(true);
				i++;
				
			}
		}
		return coordForNodesMap;
	}
	
	private Map<Node, List<Node>> createChildrenMap(){
		Map<Node, List<Node>> childrenMap = new HashMap<Node, List<Node>>();
		for(Node node : graph.getAdjacencyList().keySet()){
			if(node.getType().equals("FOG_DEVICE") && !childrenMap.containsKey(node))
				childrenMap.put(node, new ArrayList<Node>());

			List<Edge> edgeList = graph.getAdjacencyList().get(node);
			
			for(Edge edge : edgeList){
				Node neighbour = edge.getNode();
				if(node.getType().equals("SENSOR") || node.getType().equals("ACTUATOR")){
					if(!childrenMap.containsKey(neighbour)){
						childrenMap.put(neighbour, new ArrayList<Node>());
					}
					childrenMap.get(neighbour).add(node);
				} else if(neighbour.getType().equals("SENSOR") || neighbour.getType().equals("ACTUATOR")){
					if(!childrenMap.containsKey(node)){
						childrenMap.put(node, new ArrayList<Node>());
					}
					childrenMap.get(node).add(neighbour);
				}else {
					Node child = (((NodeGroupGui)node).getLevel() > ((NodeGroupGui)neighbour).getLevel())?node:neighbour;
					Node parent = (((NodeGroupGui)node).getLevel() < ((NodeGroupGui)neighbour).getLevel())?node:neighbour;
					if(!childrenMap.containsKey(parent)){
						childrenMap.put(parent, new ArrayList<Node>());
					}
					childrenMap.get(parent).add(child);
				}
			}
		}
		return childrenMap;
	}
}
