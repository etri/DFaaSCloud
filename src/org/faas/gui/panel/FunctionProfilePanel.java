package org.faas.gui.panel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import org.cloudbus.cloudsim.distributions.ExponentialDistr;
import org.faas.DFaaSConstants;
import org.faas.SimulationConfig;
import org.faas.gui.DFaaSGui;
import org.faas.gui.swing.ComboElement;
import org.faas.gui.swing.DoubleTextField;
import org.faas.gui.swing.IntTextField;
import org.faas.topology.DataModel;
import org.faas.topology.DistributionModel;
import org.faas.topology.DistributionParameter;
import org.faas.topology.FunctionProfile;
import org.faas.topology.FunctionProfileList;
import org.faas.utils.distribution.LocationDistribution;

public class FunctionProfilePanel extends JPanel {

	static interface FunctionProfileChangeListener {
		public void functionProfileChanged();
	}

	private FunctionProfile functionProfile;
	private FunctionProfile orgFunctionProfile;

	private JButton jbSave = new JButton("Apply");
	
	private JTextField name = new JTextField(20);
	private JLabel labFunctionProfileId = new JLabel("N/A");
	private JComboBox jcbFunctionGrade = new JComboBox();
	private IntTextField tfMaximumCompletionDuration = new IntTextField();
	private DoubleTextField tfViolationUnitCost = new DoubleTextField();
	private ComputingResourceRequirementsPanel computingResourceRequirementsPanel = new ComputingResourceRequirementsPanel();
	
	//@Deprecated
	//private CloudSimDistributionPanel functionRequestArrivalDistPanel = new CloudSimDistributionPanel();
	
	private CloudSimDistributionPanel requestMessageSizeDistPanel = new CloudSimDistributionPanel();
	private CloudSimDistributionPanel responseMessageSizeDistPanel = new CloudSimDistributionPanel();
	
	private JPanel inputDataListPanel = new JPanel(new BorderLayout());
	private JPanel outputDataListPanel = new JPanel(new BorderLayout());
	
	private JPanel functionRequestDistPanel = new JPanel(new BorderLayout());
	
	//private CloudSimDistributionPanel trafficSegmentDistPanel = new CloudSimDistributionPanel(CloudSimDistributionPanel.TYPE_EXPONENTIAL,"Traffic Segment (Min.):");
	
	private List<CloudSimDistributionPanel> functionTrafficSegmentDistPanels = new ArrayList<CloudSimDistributionPanel>();
	private List<CloudSimDistributionPanel> functionRequestArrivalDistPanels = new ArrayList<CloudSimDistributionPanel>();
	
	private List<CloudSimDistributionPanel> inputDataPanelList = new ArrayList<CloudSimDistributionPanel>();
	private List<CloudSimDistributionPanel> outputDataPanelList = new ArrayList<CloudSimDistributionPanel>();

	FunctionProfileChangeListener listener;
	private DFaaSGui gui;

	private SimulationConfig simulationConfig = SimulationConfig.getInstance();

	GridBagConstraints c = new GridBagConstraints();

	private void placeName(int row,String name) {
		c.gridx = 0;
		c.gridy = row;
		c.anchor = GridBagConstraints.BASELINE;
		JLabel nameLab = new JLabel(name);
		nameLab.setOpaque(true);
		nameLab.setBackground(Color.white);
		bagPanel.add(nameLab,c);
	}
	
	JPanel bagPanel = new JPanel();

	private void placeComponent(int row,JComponent comp) {
		c.gridx = 1;
		c.gridy = row;
		bagPanel.add(comp,c);
	}
	public FunctionProfilePanel(DFaaSGui gui) {
		this.gui = gui;

		super.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		super.setLayout(new BorderLayout());
		super.add(bagPanel,BorderLayout.PAGE_START);
		
		bagPanel.setLayout(new GridBagLayout());

		JPanel p;
		
		int row = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		placeName(row,"Function profile id");
		placeComponent(row,labFunctionProfileId);
		
		row++;
		placeName(row,"Function profile name");
		placeComponent(row,name);
		
		row++;
		placeName(row,"Function Grade");
		List<ComboElement> gradeList = new ArrayList<ComboElement>();
		gradeList.add(new ComboElement(0,"N/A"));
		gradeList.add(new ComboElement(DFaaSConstants.FOG_NODE_GROUP,DFaaSConstants.getEntityName(DFaaSConstants.FOG_NODE_GROUP)));
		gradeList.add(new ComboElement(DFaaSConstants.EDGE_NODE_GROUP,DFaaSConstants.getEntityName(DFaaSConstants.EDGE_NODE_GROUP)));
		gradeList.add(new ComboElement(DFaaSConstants.CORE_NODE_GROUP,DFaaSConstants.getEntityName(DFaaSConstants.CORE_NODE_GROUP)));
		jcbFunctionGrade.setModel(new DefaultComboBoxModel(gradeList.toArray()));
		p = new JPanel(new BorderLayout());
		p.add(jcbFunctionGrade,BorderLayout.WEST);
		placeComponent(row,p);
		c.weightx = 1;
		
		row++;
		placeName(row,"Maximum Completion Duration (ms)");
		placeComponent(row,tfMaximumCompletionDuration);
		
		row++;
		placeName(row,"Violation Unit Cost");
		placeComponent(row,tfViolationUnitCost);
		
		//addName(getY(0),"Function profile id");

		row++;
		placeName(row,"Computing Resource Requirements");
		placeComponent(row,computingResourceRequirementsPanel);
		
		row+=3;
		placeName(row, "Function Request Arrival Dist");
		//placeComponent(row, functionRequestArrivalDistPanel);
		placeComponent(row, functionRequestDistPanel);

		row++;
		placeName(row, "Request Message Size Dist (MB)");
		placeComponent(row, requestMessageSizeDistPanel);

		row++;
		placeName(row, "Response Message Size Dist (MB)");
		placeComponent(row, responseMessageSizeDistPanel);

		row++;
		//p = new JPanel(new GridLayout(0,1));
		//p.setPreferredSize(new Dimension(1400,300));
		placeName(row, "Input Data List");
		placeComponent(row, inputDataListPanel);
		//p.setBorder(BorderFactory.createEtchedBorder());
		//p.add(inputDataListPanel);
		//p.add(outputDataListPanel);

		row++;
		placeName(row, "Output Data List");
		placeComponent(row, outputDataListPanel);

		inputDataListPanel.setBorder(BorderFactory.createEtchedBorder());
		outputDataListPanel.setBorder(BorderFactory.createEtchedBorder());
		functionRequestDistPanel.setBorder(BorderFactory.createEtchedBorder());
		
		//row++;
		p = new JPanel(new BorderLayout());
		p.add(jbSave,BorderLayout.WEST);
		super.add(p,BorderLayout.SOUTH);
		jbSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						save();
						orgFunctionProfile.from(functionProfile);
						listener.functionProfileChanged();
					}
				});
			}
		});

		jcbFunctionGrade.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				ComboElement element = (ComboElement)e.getItem();
				int newGrade = ((Integer)(element).getValue()).intValue();
				functionProfile.setFunctionGrade(newGrade);
			}
			
		});
	}

	public void show(FunctionProfile fp) {
		orgFunctionProfile = fp;
		functionProfile = fp.copy();

		name.setText(fp.getName());
		labFunctionProfileId.setText(fp.getFunctionProfileId());
		jcbFunctionGrade.setSelectedItem(new ComboElement(fp.getFunctionGrade(),DFaaSConstants.getEntityName(fp.getFunctionGrade())));
		tfMaximumCompletionDuration.setText(fp.getMaximumCompletionDuration()+"");
		tfViolationUnitCost.setText(fp.getViolationUnitCost()+"");
		computingResourceRequirementsPanel.show(fp.getComputingResourceRequirements());
		
		//functionRequestArrivalDistPanel.show(fp.getFunctionRequestArrivalProcessModel());
		if (fp.getFunctionTrafficSegmentModel() == null) {
			DistributionModel distModel = new DistributionModel();
			//distModel = new DistributionModel();
			distModel.initClassName(ExponentialDistr.class);
			distModel.addParameter(new DistributionParameter("value",0.0));
			fp.setFunctionTrafficSegmentModel(distModel);
			distModel.addParameter(new DistributionParameter("mean",0.0));
			functionProfile.setFunctionTrafficSegmentModel(distModel);
		}
		//trafficSegmentDistPanel.show(fp.getFunctionTrafficSegmentModel());
		
		refreshRequestArrivalModel();
		
		requestMessageSizeDistPanel.show(fp.getRequestMessageSizeModel());
		responseMessageSizeDistPanel.show(fp.getResponseMessageSizeModel());
		
		refreshDataModel();
	}
	
	private void refreshRequestArrivalModel() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refreshRequestArrivalModel(functionRequestDistPanel);
				FunctionProfilePanel.this.getParent().validate();
			}
		});

	}
	private void refreshRequestArrivalModel(JPanel parentPanel) {
		
		JPanel panel = new JPanel(new GridLayout(0,1));
		
		functionRequestDistPanel.removeAll();
		functionRequestArrivalDistPanels.clear();
		functionTrafficSegmentDistPanels.clear();
		
		List<DistributionModel> arrivalList = functionProfile.getFunctionRequestArrivalProcessModels();
		List<DistributionModel> trafficList = functionProfile.getFunctionTrafficSegmentModels();

		for (int i=0;i<arrivalList.size();i++) {
			JPanel subPanel = new JPanel(new BorderLayout());

			CloudSimDistributionPanel arrivalDistPanel = new CloudSimDistributionPanel(CloudSimDistributionPanel.TYPE_EXPONENTIAL,"Arrival (ms):");
			CloudSimDistributionPanel trafficDistPanel = new CloudSimDistributionPanel(CloudSimDistributionPanel.TYPE_EXPONENTIAL,"Traffic (Min):");
			DistributionModel arrivalModel = arrivalList.get(i);
			DistributionModel trafficModel = trafficList.get(i);
			
			JButton butDelete = new JButton("-");
			//butDelete.setPreferredSize(new Dimension(20,20));
			//butDelete.setMaximumSize(new Dimension(20,20));
			butDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					arrivalList.remove(arrivalModel);
					trafficList.remove(trafficModel);
					save(false);
					refreshRequestArrivalModel();
				}
			});
			JPanel p = new JPanel();
			p.add(butDelete);
			subPanel.add(p,BorderLayout.WEST);

			JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
			p2.add(arrivalDistPanel);
			p2.add(trafficDistPanel);
			
			subPanel.add(p2,BorderLayout.CENTER);
			
			functionRequestArrivalDistPanels.add(arrivalDistPanel);
			functionTrafficSegmentDistPanels.add(trafficDistPanel);
			
			arrivalDistPanel.show(arrivalModel);
			trafficDistPanel.show(trafficModel);
			
			if (arrivalList.size()<2) {
				trafficDistPanel.setVisible(false);
			} 
			panel.add(subPanel);
		}
		JButton butInsert = new JButton("+");
		//butInsert.setPreferredSize(new Dimension(20,20));
		//butInsert.setMaximumSize(new Dimension(20,20));
		butInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				DistributionModel newArrivalModel = new DistributionModel();
				newArrivalModel.initClassName(ExponentialDistr.class);
				newArrivalModel.addParameter(new DistributionParameter("value",0.0));
				arrivalList.add(newArrivalModel);

				DistributionModel newTrafficModel = new DistributionModel();
				newTrafficModel.initClassName(ExponentialDistr.class);
				newTrafficModel.addParameter(new DistributionParameter("value",0.0));
				trafficList.add(newTrafficModel);
				save();
				refreshRequestArrivalModel();
			}
		});
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		buttonPanel.add(butInsert);
		
		//parentPanel.add(trafficSegmentDistPanel,BorderLayout.NORTH);
		parentPanel.add(panel, BorderLayout.CENTER);
		parentPanel.add(buttonPanel,BorderLayout.SOUTH);
		

	}
	
	private void refreshDataModel() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refreshDataModel(inputDataListPanel,inputDataPanelList,functionProfile.getInputDataModel(),functionProfile.getInputDataModel().getDataSizeDistributionList(),functionProfile.getInputDataModel().getLocationDistributionList());
				refreshDataModel(outputDataListPanel,outputDataPanelList,functionProfile.getOutputDataModel(),functionProfile.getOutputDataModel().getDataSizeDistributionList(),functionProfile.getOutputDataModel().getLocationDistributionList());
			
				FunctionProfilePanel.this.getParent().validate();
			}
		});
	}

	private void refreshDataModel(JPanel parentPanel,List<CloudSimDistributionPanel> panelList
			,DataModel model
			,List<DistributionModel> sizeList,List<DistributionModel> locationList) {
		
		JPanel panel = new JPanel(new GridLayout(0,1));
		
		parentPanel.removeAll();
		panelList.clear();

		int i = 0;

		//Dimension d = null;
		for (i=0;i<sizeList.size();i++) {
			JPanel subPanel = new JPanel(new BorderLayout());

			CloudSimDistributionPanel sizeDistPanel = new CloudSimDistributionPanel(CloudSimDistributionPanel.TYPE_EXPONENTIAL);
			CloudSimDistributionPanel locationDistPanel = new CloudSimDistributionPanel(CloudSimDistributionPanel.TYPE_LOCATION);
			final DistributionModel sizeModel = sizeList.get(i);
			final DistributionModel locationModel = locationList.get(i);
			
			JButton butDelete = new JButton("-");
			//butDelete.setPreferredSize(new Dimension(20,20));
			//butDelete.setMaximumSize(new Dimension(20,20));
			butDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					sizeList.remove(sizeModel);
					locationList.remove(locationModel);
					save(false);
					refreshDataModel();
				}
			});

			JPanel p = new JPanel();
			p.add(butDelete);
			subPanel.add(p,BorderLayout.WEST);

			JPanel subPanel2 = new JPanel(new GridLayout(2,1));
			subPanel2.add(sizeDistPanel);

			subPanel2.add(locationDistPanel);
			
			subPanel.add(subPanel2,BorderLayout.CENTER);
			
			panelList.add(sizeDistPanel);
			sizeDistPanel.show(sizeList.get(i));

			panelList.add(locationDistPanel);
			locationDistPanel.show(locationList.get(i));
			
			panel.add(subPanel);
			
		}
		
		
		JButton butInsert = new JButton("+");
		//butInsert.setPreferredSize(new Dimension(20,20));
		//butInsert.setMaximumSize(new Dimension(20,20));
		butInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				DistributionModel locationDistributionModel = new DistributionModel();
				locationDistributionModel.initClassName(LocationDistribution.class);
				locationDistributionModel.addParameter(new DistributionParameter("coreDist",0.25));
				locationDistributionModel.addParameter(new DistributionParameter("edgeDist",0.25));
				locationDistributionModel.addParameter(new DistributionParameter("fogDist",0.25));
				locationDistributionModel.addParameter(new DistributionParameter("userDist",0.25));
				
				DistributionModel dataSizeDistributionModel = new DistributionModel();
				dataSizeDistributionModel.initClassName(ExponentialDistr.class);
				dataSizeDistributionModel.addParameter(new DistributionParameter("value",0.0));
				model.addModel(locationDistributionModel, dataSizeDistributionModel);
				save();
				refreshDataModel();
			}
		});
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		buttonPanel.add(butInsert);
		parentPanel.add(buttonPanel,BorderLayout.SOUTH);

		parentPanel.add(panel, BorderLayout.CENTER);

	}

	private void save() {
		save(true);
	}

	private void save(boolean isPrompt) {
		if (functionProfile == null) {
			return;
		}
		
		if (jcbFunctionGrade.getSelectedIndex() == 0) {
			JOptionPane.showMessageDialog(this, "Function Grade를 지정하세요.", "Error", JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		functionProfile.setName(name.getText().length()==0?null:name.getText());
		ComboElement element = (ComboElement)jcbFunctionGrade.getSelectedItem();
		int newGrade = ((Integer)(element).getValue()).intValue();
		functionProfile.setFunctionGrade(newGrade);
		
		functionProfile.setMaximumCompletionDuration(tfMaximumCompletionDuration.getIntValue());
		functionProfile.setViolationUnitCost(tfViolationUnitCost.getDoubleValue());
		
		computingResourceRequirementsPanel.updateValues();
		
		//trafficSegmentDistPanel.updateValues();
		for (int i=0;i<functionTrafficSegmentDistPanels.size();i++) {
			functionTrafficSegmentDistPanels.get(i).isPrompt = isPrompt;
			functionTrafficSegmentDistPanels.get(i).updateValues();
		}
		for (int i=0;i<functionRequestArrivalDistPanels.size();i++) {
			functionRequestArrivalDistPanels.get(i).isPrompt = isPrompt;
			functionRequestArrivalDistPanels.get(i).updateValues();
		}
		
		requestMessageSizeDistPanel.updateValues();
		responseMessageSizeDistPanel.updateValues();
		
		for (int i=0;i<inputDataPanelList.size();i++) {
			inputDataPanelList.get(i).isPrompt = isPrompt;
			inputDataPanelList.get(i).updateValues();
		}
		for (int i=0;i<outputDataPanelList.size();i++) {
			outputDataPanelList.get(i).isPrompt = isPrompt;
			outputDataPanelList.get(i).updateValues();
		}
		
		if (FunctionProfileList.save()) {
			gui.showStatusMessage("Function Profile "+functionProfile.getName()+" saved.");
		}

	}
}
