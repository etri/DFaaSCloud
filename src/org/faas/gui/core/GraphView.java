package org.faas.gui.core;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.faas.topology.NetworkTopology;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.topology.NodeIF;

/** Panel that displays a graph */
public class GraphView extends JPanel {

	private static final long serialVersionUID = 1L;

	private GraphCanvas canvas;
	//private Graph graph;
	
	private NetworkTopology networkTopology;

	public GraphView(final Graph graph) {

		//this.graph = graph;
		
		initComponents(graph);
	}
	
	public void setNetworkTopology(NetworkTopology networkTopology) {
		this.networkTopology = networkTopology;
	}
	
	public GraphCanvas getGraphCanvas() {
		return canvas;
	}
	
	public ElementSelectionHistory getHistory() {
		return canvas.getHistory();
	}
	
	public void setGraphCanvasListener(GraphCanvasListener listener) {
		this.canvas.setGraphCanvasListener(listener);
	}
	
	public boolean deleteSelectedElement() {
		SelectableElementIF element = canvas.getSelectedElement();
		if (element == null) {
			return false;
		}
		
		NetworkTopologyHelper helper = NetworkTopologyHelper.create(networkTopology);
		
		if (element instanceof Node) {
			Node node = (Node)element;
			NodeIF nodeData = (NodeIF)node.getData();
			helper.deleteNode(nodeData.getId());
		} else if (element instanceof Edge) {
			org.faas.topology.Link link = (org.faas.topology.Link)((Edge)element).getLinkData();
			helper.deleteLlink(link);
		}
		
		return true;
	}
	
	@SuppressWarnings("serial")
	private void initComponents(Graph graph) {

		canvas = new GraphCanvas(graph);
		
		JScrollPane scrollPane = new JScrollPane(canvas);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(scrollPane);
	}

	/*private void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
		Graphics2D g = (Graphics2D) g1.create();
		System.out.println("Drawing arrow");
		double dx = x2 - x1, dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		int len = (int) Math.sqrt(dx * dx + dy * dy);
		AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g.transform(at);

		// Draw horizontal arrow starting in (0, 0)
		QuadCurve2D.Double curve = new QuadCurve2D.Double(0,0,50+0.5*len,50,len,0);
		g.draw(curve);
		g.fillPolygon(new int[] { len, len - ARR_SIZE, len - ARR_SIZE, len }, new int[] { 0, -ARR_SIZE, ARR_SIZE, 0 }, 4);
	}*/
	
	public void setGraph(Graph newGraph){
		this.canvas.setGraph(newGraph);
	}
}
