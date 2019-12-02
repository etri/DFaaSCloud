package org.faas.gui.dialog;

import javax.swing.SwingUtilities;

import org.faas.gui.DFaaSGui;
import org.faas.gui.core.GraphView;
import org.faas.gui.core.NetworkTopologyListener;
import org.faas.topology.NetworkTopology;

public class DeleteElement {

	private NetworkTopologyListener networkTopologyListener;

	public DeleteElement(final DFaaSGui gui,GraphView graphView) {

		this.networkTopologyListener = gui;
		
		if (graphView.deleteSelectedElement()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					networkTopologyListener.networkTopologyChanged();
				}
			});

		}
		
	}
}
