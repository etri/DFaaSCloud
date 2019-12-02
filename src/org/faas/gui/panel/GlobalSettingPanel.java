package org.faas.gui.panel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import org.faas.SimulationConfig;
import org.faas.SimulationManager;
import org.faas.entities.DFaaSFunctionScheduler;
import org.faas.gui.DFaaSGui;
import org.faas.gui.swing.*;
import org.faas.topology.FunctionProfileList;

public class GlobalSettingPanel extends JPanel {

	private JCheckBox jcbInfiniteRunning = new JCheckBox("infinite");
	
	private LongTextField jtfInitialSeed = new LongTextField(10);
	private JLabel jtfWorkingDir = new JLabel();
	private JLabel jtfConfigPath = new JLabel();

	private DoubleTextField jtfFuncProfileStatsDisplayInterval = new DoubleTextField(5);

	private DoubleTextField jtfSimulationDuration = new DoubleTextField(5);
	private DoubleTextField jtfINPControllerDecisionTime = new DoubleTextField();
	private DoubleTextField jtfInfraManagerMonitoringInterval = new DoubleTextField();
	
	private DoubleTextField jtfResourceModalityGap = new DoubleTextField();
	private IntTextField jtfResourceModalitySize = new IntTextField();

	private JTextField jtfHost = new JTextField("localhost");
	private IntTextField jtfPort = new IntTextField();

	private JTextField jtfDbHost = new JTextField();
	private IntTextField jtfDbPort = new IntTextField();
	
	private JCheckBox jcbDbLogging = new JCheckBox();
	
	private SimulationConfig simulationConfig = SimulationConfig.getInstance();
	
	private DFaaSGui gui;

	GridBagConstraints c = new GridBagConstraints();

	private void placeName(int row,String name) {
		c.gridx = 0;
		c.gridy = row;
		c.weightx = 0.3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		JLabel nameLab = new JLabel(name);
		nameLab.setOpaque(true);
		nameLab.setBackground(Color.darkGray);
		nameLab.setForeground(Color.white);
		nameLab.setBorder(BorderFactory.createLineBorder(Color.white));
		nameLab.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 10));
		bagPanel.add(nameLab,c);
	}

	JPanel bagPanel = new JPanel();
	private void placeComponent(int row,JComponent comp) {
		c.gridx = 2;
		c.gridy = row;
		c.weightx = 0.7;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		bagPanel.add(comp,c);
	}

	public GlobalSettingPanel(DFaaSGui gui) {
		this.gui = gui;

		super.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
		super.setLayout(new BorderLayout());
		super.add(bagPanel,BorderLayout.PAGE_START);

		bagPanel.setLayout(new GridBagLayout());

		//JPanel inputPanel = new JPanel();
		//NullLayoutUtil layoutUtils = new NullLayoutUtil(inputPanel);
		//layoutUtils.setCol2StartX(280);
		
		int row = 0;

		placeName(row, "Initial Seed");
		placeComponent(row, jtfInitialSeed);

		row++;
		placeName(row, "Simulation Running Duration (sec)");
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEADING));
		p.add(this.jtfSimulationDuration);
		p.add(this.jcbInfiniteRunning);
		placeComponent(row, p);

		row++;
		placeName(row, "INP Controller Decision Time (ms)");
		placeComponent(row, jtfINPControllerDecisionTime);
		
		row++;
		placeName(row, "Infra Manager Monitoring Interval (ms)");
		placeComponent(row, jtfInfraManagerMonitoringInterval);

		row++;
		placeName(row, "Function Profile Stats Display Interval (sec)");
		placeComponent(row, jtfFuncProfileStatsDisplayInterval);

		row++;
		placeName(row, "Resource/Queue Modality Interval (ms)");
		placeComponent(row, jtfResourceModalityGap);
		
		row++;
		placeName(row, "Resource/Queue Modality Size");
		placeComponent(row, jtfResourceModalitySize);

//		row++;
//		placeName(row, "학습 Agent Host");
//		placeComponent(row, jtfHost);
//		jtfHost.setEnabled(false);
//		
//		row++;
//		placeName(row, "학습 Agent Port");
//		placeComponent(row, jtfPort);
//		jtfPort.setEnabled(false);

		row++;
		placeName(row, "DB Logging");
		placeComponent(row, jcbDbLogging);
		
		
		row++;
		placeName(row, "DB Host");
		placeComponent(row, jtfDbHost);
		
		row++;
		placeName(row, "DB Port");
		placeComponent(row, jtfDbPort);

		row++;
		placeName(row, "Config Path");
		JPanel subPanel = new JPanel(new BorderLayout());
		JButton butConfigPath = new JButton("...");
		//butConfigPath.setPreferredSize(new Dimension(25,25));
		subPanel.add(butConfigPath,BorderLayout.WEST);
		subPanel.add(jtfConfigPath,BorderLayout.CENTER);
		placeComponent(row, subPanel);

//		row++;
//		placeName(row, "Working Directory");
//		subPanel = new JPanel(new BorderLayout());
//		JButton butSelectFolder = new JButton("...");
//		//butSelectFolder.setPreferredSize(new Dimension(25,25));
//		subPanel.add(butSelectFolder,BorderLayout.WEST);
//		subPanel.add(jtfWorkingDir,BorderLayout.CENTER);
//		placeComponent(row, subPanel);

		jtfHost.setText(simulationConfig.getAgentHost());
		jtfPort.setText(simulationConfig.getAgentPort()+"");
		jtfDbHost.setText(simulationConfig.getDbHost());
		jtfFuncProfileStatsDisplayInterval.setText(simulationConfig.getFuncProfileStatsDisplayInterval()+"");
		jtfDbPort.setText(simulationConfig.getDbPort()+"");
		jtfInitialSeed.setText(simulationConfig.getInitialSeed()+"");
		jtfInfraManagerMonitoringInterval.setText(simulationConfig.getInfraManagerMonitoringInterval()+"");
		jtfResourceModalitySize.setText(simulationConfig.getResourceModalitySize()+"");
		jtfResourceModalityGap.setText(simulationConfig.getResourceModalityGap()+"");
		jtfINPControllerDecisionTime.setText(simulationConfig.getInpControllerDecisionTime()+"");
		jtfSimulationDuration.setText(simulationConfig.getSimulationRunningDuration()+"");
		//jtfWorkingDir.setText(simulationConfig.getWorkingDir());
		jtfConfigPath.setText(SimulationManager.getConfigPath());
		jcbInfiniteRunning.setSelected(simulationConfig.isInfiniteRunning());
		jcbDbLogging.setSelected(simulationConfig.getDbLogging());
		
		jtfSimulationDuration.setEnabled(!simulationConfig.isInfiniteRunning());
		
		jtfDbHost.setEnabled(simulationConfig.getDbLogging());
		jtfDbPort.setEnabled(simulationConfig.getDbLogging());
		
		//super.add(inputPanel,BorderLayout.CENTER);
		
		JButton butApply = new JButton("Apply");
		butApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				applyChanges();
			}
		});
		subPanel = new JPanel();
		subPanel.add(butApply);
		super.add(subPanel,BorderLayout.SOUTH);
		
//		butSelectFolder.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent event) {
//				newWorkingDir();
//			}
//		});
		
		butConfigPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				newConfigPath();
			}
		});
		
		jcbInfiniteRunning.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jtfSimulationDuration.setEnabled(!jcbInfiniteRunning.isSelected());
			}
		});
		
		jcbDbLogging.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jtfDbHost.setEnabled(jcbDbLogging.isSelected());
				jtfDbPort.setEnabled(jcbDbLogging.isSelected());
			}
		});
		
		
		
		//jtfWorkingDir.setEditable(false);
	}
	
	public double getINPControllerDecisionTime() {
		return Double.parseDouble(jtfINPControllerDecisionTime.getText());
	}
	
	private void applyChanges() {
		
		simulationConfig.setAgentHost(jtfHost.getText());
		simulationConfig.setAgentPort(jtfPort.getIntValue());
		simulationConfig.setInitialSeed(jtfInitialSeed.getLongValue());
		simulationConfig.setInfraManagerMonitoringInterval(jtfInfraManagerMonitoringInterval.getDoubleValue());
		simulationConfig.setResourceModalitySize(jtfResourceModalitySize.getIntValue());
		simulationConfig.setResourceModalityGap(jtfResourceModalityGap.getDoubleValue());
		
		simulationConfig.setFuncProfileStatsDisplayInterval(jtfFuncProfileStatsDisplayInterval.getDoubleValue());
		
		simulationConfig.setInpControllerDecisionTime(this.jtfINPControllerDecisionTime.getDoubleValue());
		simulationConfig.setSimulationRunningDuration(this.jtfSimulationDuration.getDoubleValue());
		///simulationConfig.setWorkingDir(jtfWorkingDir.getText());
		simulationConfig.setInfiniteRunning(jcbInfiniteRunning.isSelected());
		simulationConfig.setDbLogging(jcbDbLogging.isSelected());
		
		SimulationManager.setInfraManagerDelay(simulationConfig.getInfraManagerMonitoringInterval());
		DFaaSFunctionScheduler.setDecisionTime(simulationConfig.getInpControllerDecisionTime());
		//SimulationManager.setSimulationDuration(simulationConfig.getSimulationRunningDuration());
		
		simulationConfig.save();

		gui.getFunctionProfileManagePanel().updateFunctionProfilePanel();
		gui.showStatusMessage("Simulation Configuraion saved.");
	}
	
	private void newConfigPath() {
    	JFileChooser fileopen = new JFileChooser(/*simulationConfig.getWorkingDir()*/System.getProperty("user.dir"));
        fileopen.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        fileopen.setAcceptAllFileFilterUsed(false);
        
        int ret = fileopen.showOpenDialog(this);

        if (ret == JFileChooser.APPROVE_OPTION) {
        	File file = fileopen.getSelectedFile();
        	if (file.isDirectory()) {
                String absPath = file.getAbsolutePath();
                String name = file.getName();

                String userDir = System.getProperty("user.dir");
                
                String newConfigPath = absPath.substring(userDir.length()+1);
                SimulationConfig.load(newConfigPath);

                SimulationManager.setConfigPath(newConfigPath);
                
        		File fpFile = new File(SimulationManager.getConfigPath(), FunctionProfileList.fileName);
        		if (fpFile.exists()) {
                    FunctionProfileList.load();
        		} else {
                    FunctionProfileList.save();
        		}
                
                File tpFile = new File(SimulationManager.getConfigPath(), DFaaSGui.getMe().currentJsonFile);
                if (tpFile.exists()) {
                	//DFaaSGui.me.importNetworkTopology(SimulationConfig.getInstance().getConfigPath(), DFaaSGui.me.currentJsonFile);
                } else {
                	DFaaSGui.getMe().saveNetworkTopology();
                }
                DFaaSGui.getMe().packTab();
                
                //jtfWorkingDir.setText(name);
                
                //simulationConfig.setWorkingDir(absPath);
        	}
            
        }
	}
	
	private void newWorkingDir() {
//    	JFileChooser fileopen = new JFileChooser(simulationConfig.getWorkingDir());
//        fileopen.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
//
//        int ret = fileopen.showSaveDialog(this);
//
//        if (ret == JFileChooser.APPROVE_OPTION) {
//        	File file = fileopen.getSelectedFile();
//        	if (file.isDirectory()) {
//                String absPath = file.getAbsolutePath();
//                //String name = file.getName();
//
//                jtfWorkingDir.setText(absPath);
//                simulationConfig.setWorkingDir(absPath);
//        	}
//            
//        }
	}
}
