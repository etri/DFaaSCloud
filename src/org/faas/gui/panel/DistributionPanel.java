package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.faas.topology.DistributionModel;
import org.faas.topology.DistributionParameter;
import org.faas.utils.Logger;
import org.faas.utils.distribution.LocationDistribution;
import org.faas.utils.distribution.DeterministicDistribution;
import org.faas.utils.distribution.NormalDistribution;
import org.faas.utils.distribution.UniformDistribution;

@Deprecated
public class DistributionPanel extends JPanel {

	private DistributionModel distModel;

	private JComboBox distribution;

	private JTextField uniformLowerBound;
	private JTextField uniformUpperBound;
	private JTextField deterministicValue;
	private JTextField normalMean;
	private JTextField normalStdDev;
	private JTextField coreDist;
	private JTextField edgeDist;
	private JTextField fogDist;
	private JTextField userDist;
	
	JLabel normalMeanLabel;
	JLabel normalStdDevLabel;
	JLabel uniformLowLabel;
	JLabel uniformUpLabel;
	JLabel deterministicValueLabel;
	JLabel coreDistLabel;
	JLabel edgeDistLabel;
	JLabel fogDistLabel;
	JLabel userDistLabel;
	
	JLabel distLabel;
	
	String[] distributionType = {"Normal", "Uniform", "Deterministic", "LocationDistribution"};
	
//	public static final int TYPE_NORMAL = 0;
//	public static final int TYPE_UNIFORM = 1;
//	public static final int TYPE_DETERMINISTIC = 2;
	
	int type = TYPE_NONE;
		
	public static final int TYPE_LOCATION = 0;
	public static final int TYPE_NONE = 1;
	
	public DistributionPanel() {
		this(TYPE_NONE);
	}
	public DistributionPanel(int type) {
		this.type = type;
		
		if (type == TYPE_NONE) {
			distributionType = new String[]{"Normal", "Uniform", "Deterministic"};
		} else if (type == TYPE_LOCATION) {
			distributionType = new String[]{"Normal", "Uniform", "Deterministic", "LocationDistribution"};
		}
		super.setLayout(new BorderLayout());

        //JPanel springPanel = new JPanel(new SpringLayout());
	    JPanel springPanel = new JPanel();
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEADING);
	    springPanel.setLayout(flowLayout);
	    
		distLabel = new JLabel("Distribution Type: ", JLabel.TRAILING);
		springPanel.add(distLabel);	
		distribution = new JComboBox(distributionType);
		distLabel.setLabelFor(distribution);
		distribution.setSelectedIndex(-1);
		distribution.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JComboBox ctype = (JComboBox)e.getSource();
				String item = (String)ctype.getSelectedItem();
				updatePanel(item);				
			}
		});
		
		int textSize = 5;
		
		springPanel.add(distribution);		
		
		normalMeanLabel = new JLabel("Mean: ");
		springPanel.add(normalMeanLabel);	
		normalMean = new JTextField(textSize);
		normalMeanLabel.setLabelFor(normalMean);
		springPanel.add(normalMean);
		
		normalStdDevLabel = new JLabel("StdDev: ");
		springPanel.add(normalStdDevLabel);	
		normalStdDev = new JTextField(textSize);
		normalStdDevLabel.setLabelFor(normalStdDev);
		springPanel.add(normalStdDev);
		
		uniformLowLabel = new JLabel("Min: ");
		springPanel.add(uniformLowLabel);	
		uniformLowerBound = new JTextField(textSize);
		uniformLowLabel.setLabelFor(uniformLowerBound);
		springPanel.add(uniformLowerBound);
		
		uniformUpLabel = new JLabel("Max: ");
		springPanel.add(uniformUpLabel);	
		uniformUpperBound = new JTextField(textSize);
		uniformUpLabel.setLabelFor(uniformUpperBound);
		springPanel.add(uniformUpperBound);
		
		deterministicValueLabel = new JLabel("Value: ");
		springPanel.add(deterministicValueLabel);	
		deterministicValue = new JTextField(textSize);
		deterministicValueLabel.setLabelFor(deterministicValue);
		springPanel.add(deterministicValue);		
		
		coreDistLabel = new JLabel("Core dist: ");
		springPanel.add(coreDistLabel);	
		coreDist = new JTextField(textSize);
		coreDistLabel.setLabelFor(coreDist);
		springPanel.add(coreDist);		

		edgeDistLabel = new JLabel("Edge dist: ");
		springPanel.add(edgeDistLabel);	
		edgeDist = new JTextField(textSize);
		edgeDistLabel.setLabelFor(edgeDist);
		springPanel.add(edgeDist);		

		fogDistLabel = new JLabel("Fog dist: ");
		springPanel.add(fogDistLabel);	
		fogDist = new JTextField(textSize);
		fogDistLabel.setLabelFor(fogDist);
		springPanel.add(fogDist);		

		userDistLabel = new JLabel("ED dist: ");
		springPanel.add(userDistLabel);	
		userDist = new JTextField(textSize);
		userDistLabel.setLabelFor(userDist);
		springPanel.add(userDist);		

		super.add(springPanel, BorderLayout.CENTER);
		
		//super.setPreferredSize(new Dimension(800,40));
		
		super.setBorder(BorderFactory.createEtchedBorder());


		if (type == TYPE_NONE) {
			distributionType = new String[]{"Normal", "Uniform", "Deterministic"};
			distLabel.setText("Size (MB):");
			select(0);
		} else if (type == TYPE_LOCATION) {
			distributionType = new String[]{"Normal", "Uniform", "Deterministic", "LocationDistribution"};
			distLabel.setText("Location:");
			distribution.setVisible(false);
			select(3);
		}

	}
	
    protected void updatePanel(String item) {
		switch(item){
		case "Normal":
			normalMean.setVisible(true);
			normalStdDev.setVisible(true);
			uniformLowerBound.setVisible(false);
			uniformUpperBound.setVisible(false);
			deterministicValue.setVisible(false);
			
			normalMeanLabel.setVisible(true);
			normalStdDevLabel.setVisible(true);
			uniformLowLabel.setVisible(false);
			uniformUpLabel.setVisible(false);
			deterministicValueLabel.setVisible(false);
			
			coreDist.setVisible(false);
			edgeDist.setVisible(false);
			fogDist.setVisible(false);
			userDist.setVisible(false);

			coreDistLabel.setVisible(false);
			edgeDistLabel.setVisible(false);
			fogDistLabel.setVisible(false);
			userDistLabel.setVisible(false);
			break;
		case "Uniform":
			normalMean.setVisible(false);
			normalStdDev.setVisible(false);
			uniformLowerBound.setVisible(true);
			uniformUpperBound.setVisible(true);
			deterministicValue.setVisible(false);

			normalMeanLabel.setVisible(false);
			normalStdDevLabel.setVisible(false);
			uniformLowLabel.setVisible(true);
			uniformUpLabel.setVisible(true);
			deterministicValueLabel.setVisible(false);

			coreDist.setVisible(false);
			edgeDist.setVisible(false);
			fogDist.setVisible(false);
			userDist.setVisible(false);

			coreDistLabel.setVisible(false);
			edgeDistLabel.setVisible(false);
			fogDistLabel.setVisible(false);
			userDistLabel.setVisible(false);
			break;
		case "Deterministic":
			normalMean.setVisible(false);
			normalStdDev.setVisible(false);
			uniformLowerBound.setVisible(false);
			uniformUpperBound.setVisible(false);
			deterministicValue.setVisible(true);

			normalMeanLabel.setVisible(false);
			normalStdDevLabel.setVisible(false);
			uniformLowLabel.setVisible(false);
			uniformUpLabel.setVisible(false);
			deterministicValueLabel.setVisible(true);

			coreDist.setVisible(false);
			edgeDist.setVisible(false);
			fogDist.setVisible(false);
			userDist.setVisible(false);

			coreDistLabel.setVisible(false);
			edgeDistLabel.setVisible(false);
			fogDistLabel.setVisible(false);
			userDistLabel.setVisible(false);

			break;
		case "LocationDistribution":
			normalMean.setVisible(false);
			normalStdDev.setVisible(false);
			uniformLowerBound.setVisible(false);
			uniformUpperBound.setVisible(false);
			deterministicValue.setVisible(false);

			normalMeanLabel.setVisible(false);
			normalStdDevLabel.setVisible(false);
			uniformLowLabel.setVisible(false);
			uniformUpLabel.setVisible(false);
			deterministicValueLabel.setVisible(false);
			
			coreDist.setVisible(true);
			edgeDist.setVisible(true);
			fogDist.setVisible(true);
			userDist.setVisible(true);

			coreDistLabel.setVisible(true);
			edgeDistLabel.setVisible(true);
			fogDistLabel.setVisible(true);
			userDistLabel.setVisible(true);
			
			break;
		default:
			break;
		}
		
	}
    
    private void select(int type) {
    	this.distribution.setSelectedIndex(type);
    }
	
	public void show(DistributionModel distModel) {
		this.distModel = distModel;
		
		String className = distModel.getClassName();
		List<DistributionParameter> parameterList = distModel.getParameterList();
		if (className.contains("Normal")) {
			// double mean, double stdDev
			try {
				normalMean.setText(parameterList.get(0).getValue().toString());
				normalStdDev.setText(parameterList.get(1).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("DistributionPanel:show","Exception: " + e);
			}
			select(0);
		} else if (className.contains("Uniform")) {
			// double min, double max
			try {
				uniformLowerBound.setText(parameterList.get(0).getValue().toString());
				uniformUpperBound.setText(parameterList.get(1).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("DistributionPanel:show","Exception: " + e);
			}
			select(1);
		} else if (className.contains("Deterministic")) {
			//
			deterministicValue.setText(parameterList.get(0).getValue().toString());
			select(2);
		} else if (className.contains("Location")) {
			//
			try {
				coreDist.setText(parameterList.get(0).getValue().toString());
				edgeDist.setText(parameterList.get(1).getValue().toString());
				fogDist.setText(parameterList.get(2).getValue().toString());
				userDist.setText(parameterList.get(3).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("DistributionPanel:show","Exception: " + e);
			}
			select(3);
		}
	}
	
	public void updateValues() {
		String item = this.distribution.getSelectedItem().toString();
		distModel.clearParameters();
		
		switch(item){
		case "Normal":
			distModel.initClassName(NormalDistribution.class);
			try {
				distModel.addParameter(new DistributionParameter("mean",Double.parseDouble(normalMean.getText())));
				distModel.addParameter(new DistributionParameter("stdDev",Double.parseDouble(this.normalStdDev.getText())));
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("DistributionPanel:updateValues","Exception: " + e);
			}
			break;
		case "Uniform":
			distModel.initClassName(UniformDistribution.class);
			try {
				distModel.addParameter(new DistributionParameter("min",Double.parseDouble(this.uniformLowerBound.getText())));
				distModel.addParameter(new DistributionParameter("max",Double.parseDouble(this.uniformUpperBound.getText())));
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("DistributionPanel:updateValues","Exception: " + e);
			}
			break;
		case "Deterministic":
			distModel.initClassName(DeterministicDistribution.class);
			try {
				distModel.addParameter(new DistributionParameter("value",Double.parseDouble(this.deterministicValue.getText())));
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("DistributionPanel:updateValues","Exception: " + e);
			}
			break;
		case "LocationDistribution":
			distModel.initClassName(LocationDistribution.class);
			try {
				distModel.addParameter(new DistributionParameter("coreDist",Double.parseDouble(this.coreDist.getText())));
				distModel.addParameter(new DistributionParameter("edgeDist",Double.parseDouble(this.edgeDist.getText())));
				distModel.addParameter(new DistributionParameter("fogDist",Double.parseDouble(this.fogDist.getText())));
				distModel.addParameter(new DistributionParameter("userDist",Double.parseDouble(this.userDist.getText())));
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("DistributionPanel:updateValues","Exception: " + e);
			}
			break;
		default:
			break;
		}
	}
}
