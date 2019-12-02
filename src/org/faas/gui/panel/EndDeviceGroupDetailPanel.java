package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.faas.gui.DFaaSGui;
import org.faas.gui.core.Graph;
import org.faas.gui.dialog.AddActuator;
import org.faas.gui.dialog.AddSensor;
import org.faas.topology.Actuator;
import org.faas.topology.EndDeviceGroup;
import org.faas.topology.NetworkTopology;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.topology.Sensor;

public class EndDeviceGroupDetailPanel extends JPanel implements EndElementListener {

	private JTable sensorTable = new JTable();
	private JTable actuatorTable = new JTable();
	
	private JButton butAddSensor = new JButton("+");
	private JButton butDeleteSensor = new JButton("-");
	
	private JButton butAddActuator = new JButton("+");
	private JButton butDeleteActuator = new JButton("-");
	
	private SensorDetailPanel sensorDetailPanel = new SensorDetailPanel(this);
	private ActuatorDetailPanel actuatorDetailPanel = new ActuatorDetailPanel(this);
	
	private DefaultTableModel sensorModel;
	private DefaultTableModel actuatorModel;
	
	public EndDeviceGroupDetailPanel() {
		super.setLayout(new BorderLayout());
		
		JTabbedPane tabPane = new JTabbedPane();

		JPanel sensorPane = new JPanel(new BorderLayout());
		JPanel sensorTopPane = new JPanel();
		sensorTopPane.add(butAddSensor);
		sensorTopPane.add(butDeleteSensor);
		sensorPane.add(sensorTopPane,BorderLayout.NORTH);
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(250);
		split.setLeftComponent(new JScrollPane(sensorTable));
		split.setRightComponent(sensorDetailPanel);
		sensorPane.add(split,BorderLayout.CENTER);
		
		tabPane.add("Sensors",sensorPane);
		
		JPanel actuatorPane = new JPanel(new BorderLayout());
		JPanel actuatorTopPane = new JPanel();
		actuatorTopPane.add(butAddActuator);
		actuatorTopPane.add(butDeleteActuator);
		actuatorPane.add(actuatorTopPane,BorderLayout.NORTH);
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(250);
		split.setLeftComponent(new JScrollPane(actuatorTable));
		split.setRightComponent(actuatorDetailPanel);
		actuatorPane.add(split,BorderLayout.CENTER);
		tabPane.add("Actuators",actuatorPane);
		
		super.add(tabPane,BorderLayout.CENTER);

		sensorTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting()) {
					return;
				}
				showSensorDetails();
			}
		});
		
		actuatorTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting()) {
					return;
				}
				showActuatorDetails();
			}
		});
		
		
		butAddSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				addSensor();
			}
		});
		butDeleteSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				deleteSensor();
			}
		});
		butAddActuator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				addActuator();
			}
		});
		butDeleteActuator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				deleteActuator();
			}
		});
		
	}
	
	private Object[] packRowData(Sensor sensor) {
		Object rowData[] = new Object[3];
		Actuator paired = null;
		if (sensor.getActuatorId()!=0) {
			paired = (Actuator)helper.getNode(sensor.getActuatorId());
		}

		SensorTableCellRenderer.Data data = new SensorTableCellRenderer.Data(sensor,paired);

		rowData[0] = data;
		rowData[1] = data;
		rowData[2] = data;
		return rowData;
	}
	
	private Object[] packRowData(Actuator actuator) {
		Object rowData[] = new Object[3];
		Sensor paired = null;
		if (actuator.getSensorId()!=0) {
			paired = (Sensor)helper.getNode(actuator.getSensorId());
		}

		ActuatorTableCellRenderer.Data data = new ActuatorTableCellRenderer.Data(actuator,paired);

		rowData[0] = data;
		rowData[1] = data;
		rowData[2] = data;
		return rowData;
	}
	
	@Override
	public void sensorAdded(Sensor sensor) {
		helper = NetworkTopologyHelper.create(networkTopology);

		DefaultTableModel sensorModel = (DefaultTableModel)sensorTable.getModel();
		sensorModel.addRow(packRowData(sensor));
		
		showActuatorDetails();
		showSensorDetails();
		
		actuatorModel.fireTableDataChanged();
		sensorModel.fireTableDataChanged();
	}

	@Override
	public void actuatorAdded(Actuator actuator) {
		helper = NetworkTopologyHelper.create(networkTopology);

		DefaultTableModel actuatorModel = (DefaultTableModel)actuatorTable.getModel();
		actuatorModel.addRow(packRowData(actuator));
		
		showActuatorDetails();
		showSensorDetails();
		
		actuatorModel.fireTableDataChanged();
		sensorModel.fireTableDataChanged();
	}

	@Override
	public void sensorUpdated(Sensor sensor) {
		sensorModel.fireTableDataChanged();
		actuatorModel.fireTableDataChanged();
	}
	
	@Override
	public void actuatorUpdated(Actuator actuator) {
		sensorModel.fireTableDataChanged();
		actuatorModel.fireTableDataChanged();
	}
	
	private void addSensor() {
		new AddSensor(graph,gui,this, networkTopology,endDeviceGroup);
	}
	
	public void deleteSensor() {
		int index = sensorTable.getSelectionModel().getMinSelectionIndex();
		if (index == -1) return;

		Sensor sensor = ((SensorTableCellRenderer.Data)sensorTable.getValueAt(index, 0)).sensor;

		endDeviceGroup.deleteSensor(sensor);
		sensorModel.removeRow(index);
		sensorModel.fireTableDataChanged();
		actuatorModel.fireTableDataChanged();
	}
	
	public void addActuator() {
		new AddActuator(graph,gui,this, networkTopology,endDeviceGroup);
	}
	
	public void deleteActuator() {
		int index = actuatorTable.getSelectionModel().getMinSelectionIndex();
		if (index == -1) return;
		
		Actuator actuator = ((ActuatorTableCellRenderer.Data)actuatorTable.getValueAt(index, 0)).actuator;

		endDeviceGroup.deleteActuator(actuator);
		actuatorModel.removeRow(index);
		actuatorModel.fireTableDataChanged();
		sensorModel.fireTableDataChanged();
	}
	
	private void showSensorDetails() {
		int index = sensorTable.getSelectionModel().getMinSelectionIndex();
		
		Sensor sensor = null;
		if (index != -1) {
			sensor = ((SensorTableCellRenderer.Data)sensorTable.getValueAt(index, 0)).sensor;
		}
		final Sensor sensor2 = sensor;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				sensorDetailPanel.show(networkTopology, sensor2);
			}
		});
	}
	
	private void showActuatorDetails() {
		int index = actuatorTable.getSelectionModel().getMinSelectionIndex();
		
		Actuator  actuator = null;
		if (index != -1) {
			actuator = ((ActuatorTableCellRenderer.Data)actuatorTable.getValueAt(index, 0)).actuator;
		}

		Actuator actuator2 = actuator;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				actuatorDetailPanel.show(networkTopology, actuator2);
			}
		});
	}
	
	private NetworkTopology networkTopology;
	private NetworkTopologyHelper helper;
	Graph graph;
	DFaaSGui gui;
	EndDeviceGroup endDeviceGroup;
	String[] sensorTableColumnNames = {"EventSource ID", "End Device Group Id", "EventSink ID"};
	String[] actuatorTableColumnNames = {"EventSink ID", "End Device Group Id", "EventSource ID"};
	
	public void show(Graph graph,DFaaSGui gui,NetworkTopology networkTopology,EndDeviceGroup endDeviceGroup) {
		this.networkTopology = networkTopology;
		helper = NetworkTopologyHelper.create(networkTopology);
		this.graph = graph;
		this.gui = gui;
		this.endDeviceGroup = endDeviceGroup;

		sensorModel = new DefaultTableModel(sensorTableColumnNames,0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
			@Override
			public void fireTableDataChanged() {

				Vector dataList = super.getDataVector();
				for (int i=0;i<dataList.size();i++) {
					SensorTableCellRenderer.Data data = (SensorTableCellRenderer.Data)((Vector)dataList.get(i)).get(0);
					data.paired = (Actuator)helper.getNode(data.sensor.getActuatorId());
				}
				
				super.fireTableDataChanged();
			}

		};
		sensorTable.setModel(sensorModel);
		
		actuatorModel = new DefaultTableModel(actuatorTableColumnNames,0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public void fireTableDataChanged() {

				Vector dataList = super.getDataVector();
				for (int i=0;i<dataList.size();i++) {
					ActuatorTableCellRenderer.Data data = (ActuatorTableCellRenderer.Data)((Vector)dataList.get(i)).get(0);
					data.paired = (Sensor)helper.getNode(data.actuator.getSensorId());
				}
				
				super.fireTableDataChanged();
			}
			
		};
		actuatorTable.setModel(actuatorModel);
		
		for (int i = 0; i < actuatorTable.getColumnCount(); i++) {
			actuatorTable.getColumnModel().getColumn(i).setCellRenderer(new ActuatorTableCellRenderer());
		}
		
		for (int i = 0; i < sensorTable.getColumnCount(); i++) {
			sensorTable.getColumnModel().getColumn(i).setCellRenderer(new SensorTableCellRenderer());
		}
		
		Sensor sensors[] = endDeviceGroup.getSensorList().toArray(new Sensor[endDeviceGroup.getSensorList().size()]);
		for (int i=0;i<sensors.length;i++) {
			Object []rowData = packRowData(sensors[i]);
			sensorModel.addRow(rowData);
		}
		sensorModel.fireTableDataChanged();
		
		Actuator actuators[] = endDeviceGroup.getActuatorList().toArray(new Actuator[endDeviceGroup.getActuatorList().size()]);
		for (int i=0;i<actuators.length;i++) {
			Object []rowData = packRowData(actuators[i]);
			actuatorModel.addRow(rowData);
		}
		actuatorModel.fireTableDataChanged();
	}
}
