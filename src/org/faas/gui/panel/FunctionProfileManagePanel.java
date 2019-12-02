package org.faas.gui.panel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.cloudbus.cloudsim.distributions.LognormalDistr;
import org.faas.DFaaSConstants;
import org.faas.gui.DFaaSGui;
import org.faas.topology.ComputingResourceRequirements;
import org.faas.topology.DataModel;
import org.faas.topology.DistributionModel;
import org.faas.topology.DistributionParameter;
import org.faas.topology.FunctionProfile;
import org.faas.topology.FunctionProfileList;
import org.faas.utils.Util;
import org.faas.utils.distribution.LocationDistribution;

public class FunctionProfileManagePanel extends JPanel implements FunctionProfilePanel.FunctionProfileChangeListener {

	private FunctionProfilePanel functionProfilePanel;
	
	private MyListModel listModel;
	private JList list = new JList();

	private DFaaSGui gui;
	
	public FunctionProfileManagePanel(DFaaSGui gui) {
	
		this.gui = gui;
		functionProfilePanel = new FunctionProfilePanel(gui);
				
		super.setLayout(new BorderLayout());
		
		JSplitPane jsp = new JSplitPane();
		jsp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		jsp.setDividerLocation(200);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listModel=new MyListModel();
		for (int i=0;i<FunctionProfileList.getInstance().getFunctionProfileList().size();i++) {
			listModel.addElement(FunctionProfileList.getInstance().getFunctionProfileList().get(i));
		}
		list.setModel(listModel);
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel leftTopPanel = new JPanel(new GridLayout(2,3));

		JButton butImport = new JButton("Import");
		leftTopPanel.add(butImport);
		JButton butExport = new JButton("Export");
		leftTopPanel.add(butExport);

		JButton butSave = new JButton("Save");
		leftTopPanel.add(butSave);
		
		JPanel p = new JPanel();
		JButton butAdd = new JButton("+");
		//butAdd.setPreferredSize(new Dimension(25,25));
		p.add(butAdd);
		JButton butDelete = new JButton("-");
		//butDelete.setPreferredSize(new Dimension(25,25));
		p.add(butDelete);
		leftTopPanel.add(p);
		
		leftPanel.add(leftTopPanel,BorderLayout.NORTH);
		leftPanel.add(list,BorderLayout.CENTER);
		jsp.setLeftComponent(leftPanel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(this.functionProfilePanel);
		jsp.setRightComponent(scrollPane);
		
		super.add(BorderLayout.CENTER,jsp);
		
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (e.getValueIsAdjusting()) {
					index = list.getSelectedIndex();
					final FunctionProfile fp = (FunctionProfile)listModel.getElementAt(index);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							functionProfilePanel.setVisible(true);
							functionProfilePanel.show(fp);
						}
					});
				}
			}
			
		});
		
		functionProfilePanel.listener = this;
		
		butSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				updateFunctionProfilePanel();
				if (FunctionProfileList.save()) {
					gui.showStatusMessage("Function Profile saved.");
				}
			}
		});
		
		butAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				addFunctionProfile();
			}
		});
		
		butDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				deleteFunctionProfile();
			}
		});
		
		butImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				importFunctionProfile();
			}
			
		});
		
		butExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				exportFunctionProfile();
			}
			
		});
		
		functionProfilePanel.setVisible(false);
	}

	public void updateFunctionProfilePanel() {
		index = list.getSelectedIndex();
		if(index >= 0) {
			final FunctionProfile fp = (FunctionProfile) listModel.getElementAt(index);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					functionProfilePanel.setVisible(true);
					functionProfilePanel.show(fp);
				}
			});
		}
	}

	private void addFunctionProfile() {
		FunctionProfile profile = packFunctionProfile();
		FunctionProfileList.getInstance().addFunctionProfile(profile);
		
		listModel.addElement(profile);
	}
	
	private void deleteFunctionProfile() {
		if (list.getSelectedValue() == null) {
			return;
		}
		
		
		FunctionProfile profile = (FunctionProfile)list.getSelectedValue();

		if (gui.getNetworkTopology().isFunctionProfileInUse(profile.getFunctionProfileId())) {
			JOptionPane.showMessageDialog(this, "사용중인 Function Profile 입니다.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		FunctionProfileList.getInstance().deleteFunctionProfile(profile);
		listModel.removeElement(profile);
		
		functionProfilePanel.setVisible(false);
	}
	
	private void importFunctionProfile() {
		String type = "json";
    	JFileChooser fileopen = new JFileChooser(System.getProperty("user.dir"));
        //JFileChooser fileopen = new JFileChooser(SimulationConfig.getInstance().getWorkingDir());
        FileFilter filter = new FileNameExtensionFilter(type.toUpperCase()+" Files", type);
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(this, "Import file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();

            FunctionProfile importedFunctionProfile = FunctionProfile.read(file.getAbsolutePath());
			if(importedFunctionProfile != null) {
				for (int i = 0; i < listModel.size(); i++) {
					FunctionProfile profile = (FunctionProfile)listModel.getElementAt(i);
					if(profile.getFunctionProfileId().equals(importedFunctionProfile.getFunctionProfileId())) {
						int reply = JOptionPane.showConfirmDialog(this, "현재 사용중인 Function Profile("
										+ profile.getName()
										+ ")과 UUID가 동일합니다.\nImport를 위해 새로운 UUID를 부여하시겠습니까?",
								"Import Function Profile",
								JOptionPane.YES_NO_OPTION);
						if(reply == JOptionPane.YES_OPTION) {
							importedFunctionProfile.setFunctionProfileId(Util.getUUID());
							FunctionProfileList.getInstance().addFunctionProfile(importedFunctionProfile);
							listModel.addElement(importedFunctionProfile);
						}
					}
					break;
				}
			} else {
				JOptionPane.showMessageDialog(this, file.getAbsolutePath() + "은 Function Profile이 아닙니다.", "Error", JOptionPane.ERROR_MESSAGE);
			}
        }

        updateFunctionProfilePanel();

	}
	
	public void exportFunctionProfile() {
		String type = "json";
		

		index = list.getSelectedIndex();
		if (index == -1) {
			return;
		}
		final FunctionProfile fp = (FunctionProfile)listModel.getElementAt(index);
		
    	JFileChooser fileopen = new JFileChooser(System.getProperty("user.dir"));
		//JFileChooser fileopen = new JFileChooser(SimulationConfig.getInstance().getWorkingDir());
    	String newFileName = "FunctionProfile";
    	if (fp.getName() != null) {
    		newFileName = fp.getName();
    	}
    	fileopen.setSelectedFile(new File(newFileName+".json"));
        FileFilter filter = new FileNameExtensionFilter(type.toUpperCase()+" Files", type);
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showSaveDialog(this);

        if (ret == JFileChooser.APPROVE_OPTION) {
            String fileName = fileopen.getSelectedFile().getAbsolutePath();
		    if (FunctionProfile.save(fp, fileName) == true) {
				gui.showStatusMessage("save success.");
			} else {
				gui.showStatusMessage("save failure.");
			}
        }

		updateFunctionProfilePanel();
	}
	
	private int index;
	public void functionProfileChanged() {
		listModel.update(index);
	}
	
	public static FunctionProfile packFunctionProfile() {
		FunctionProfile fp = new FunctionProfile();
		
		fp.setFunctionProfileId(Util.getUUID());
		fp.setName("Name "+FunctionProfileList.getInstance().getFunctionProfileList().size());

		fp.setFunctionGrade(DFaaSConstants.CORE_NODE_GROUP);
		fp.setViolationUnitCost(10.9);
		fp.setMaximumCompletionDuration(130);
		
		// computingResourceRequirements
		
		ComputingResourceRequirements computingResourceRequirements = new ComputingResourceRequirements();
		
		computingResourceRequirements.setNumberOfCpuCores(3);
		computingResourceRequirements.setMemorySize(10);
		
		DistributionModel requiredMipsModel = new DistributionModel();
		
		requiredMipsModel.initClassName(LognormalDistr.class);
		requiredMipsModel.addParameter(new DistributionParameter("mean",4000.0));
		requiredMipsModel.addParameter(new DistributionParameter("dev",1000.0));
		
		computingResourceRequirements.setRequiredMipsModel(requiredMipsModel);
		
		fp.setComputingResourceRequirements(computingResourceRequirements);
		
		//function request arrival process model
		
		DistributionModel functionRequestArrivalProcessModel = new DistributionModel();
		
		functionRequestArrivalProcessModel.initClassName(LognormalDistr.class);
		functionRequestArrivalProcessModel.addParameter(new DistributionParameter("mean",1000.0));
		functionRequestArrivalProcessModel.addParameter(new DistributionParameter("dev",10.0));
		
		fp.addFunctionRequestArrivalProcessModel(functionRequestArrivalProcessModel);

		DistributionModel functionTrafficSegmentModel = new DistributionModel();
		
		functionTrafficSegmentModel.initClassName(LognormalDistr.class);
		functionTrafficSegmentModel.addParameter(new DistributionParameter("mean",1000.0));
		functionTrafficSegmentModel.addParameter(new DistributionParameter("dev",10.0));
		
		fp.addFunctionTrafficSegmentModels(functionRequestArrivalProcessModel);

		// requestMessageSizeModel
		DistributionModel requestMessageSizeModel = new DistributionModel();
		
		requestMessageSizeModel.initClassName(LognormalDistr.class);
		requestMessageSizeModel.addParameter(new DistributionParameter("mean",90.0));
		requestMessageSizeModel.addParameter(new DistributionParameter("dev",100.0));
		
		fp.setRequestMessageSizeModel(requestMessageSizeModel);
		
		// responseMessageSizeModel
		DistributionModel responseMessageSizeModel = new DistributionModel();
		
		responseMessageSizeModel.initClassName(LognormalDistr.class);
		responseMessageSizeModel.addParameter(new DistributionParameter("mean",40.0));
		responseMessageSizeModel.addParameter(new DistributionParameter("dev",50.0));
		
		fp.setResponseMessageSizeModel(responseMessageSizeModel);
		
		// inputDataModel
		
		DataModel inputDataModel = new DataModel();
		DistributionModel locationDistributionModel = new DistributionModel();
		DistributionModel dataSizeDistributionModel = new DistributionModel();
		locationDistributionModel.initClassName(LocationDistribution.class);
		locationDistributionModel.addParameter(new DistributionParameter("coreDist",0.4));
		locationDistributionModel.addParameter(new DistributionParameter("edgeDist",0.3));
		locationDistributionModel.addParameter(new DistributionParameter("fogDist",0.15));
		locationDistributionModel.addParameter(new DistributionParameter("userDist",0.15));

		dataSizeDistributionModel.initClassName(LognormalDistr.class);
		dataSizeDistributionModel.addParameter(new DistributionParameter("mean",1.0));
		dataSizeDistributionModel.addParameter(new DistributionParameter("dev",10.0));
		
		inputDataModel.addModel(locationDistributionModel, dataSizeDistributionModel);
		fp.setInputDataModel(inputDataModel);

		// outputDataModel
		
		DataModel outputDataModel = new DataModel();
		locationDistributionModel = new DistributionModel();
		dataSizeDistributionModel = new DistributionModel();
		locationDistributionModel.initClassName(LocationDistribution.class);
		locationDistributionModel.addParameter(new DistributionParameter("coreDist",0.15));
		locationDistributionModel.addParameter(new DistributionParameter("edgeDist",0.3));
		locationDistributionModel.addParameter(new DistributionParameter("fogDist",0.15));
		locationDistributionModel.addParameter(new DistributionParameter("userDist",0.4));

		dataSizeDistributionModel.initClassName(LognormalDistr.class);
		dataSizeDistributionModel.addParameter(new DistributionParameter("mean",1.0));
		dataSizeDistributionModel.addParameter(new DistributionParameter("dev",10.0));
		
		outputDataModel.addModel(locationDistributionModel, dataSizeDistributionModel);
		
		fp.setOutputDataModel(outputDataModel);

		//

		return fp;
	}
	
	class MyListModel extends DefaultListModel {
		
	    public void update(int index)
	    {
	        fireContentsChanged(this, index, index);
	    }
	}
	
}
