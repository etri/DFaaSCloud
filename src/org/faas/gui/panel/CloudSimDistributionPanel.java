package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.cloudbus.cloudsim.distributions.ExponentialDistr;
import org.cloudbus.cloudsim.distributions.GammaDistr;
import org.cloudbus.cloudsim.distributions.LognormalDistr;
import org.cloudbus.cloudsim.distributions.LomaxDistribution;
import org.cloudbus.cloudsim.distributions.ParetoDistr;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.distributions.WeibullDistr;
import org.cloudbus.cloudsim.distributions.ZipfDistr;
import org.faas.gui.swing.IntTextField;
import org.faas.topology.DistributionModel;
import org.faas.topology.DistributionParameter;
import org.faas.utils.Logger;
import org.faas.utils.distribution.LocationDistribution;
import org.faas.utils.distribution.NormalDistribution;
import org.faas.utils.distribution.TraceBasedTrafficDistribution;
import org.faas.utils.distribution.DeterministicDistribution;

public class CloudSimDistributionPanel extends JPanel {

	private DistributionModel distModel;

	private JComboBox distribution;

	private JTextField jtf1;
	private JTextField jtf2;
	private JTextField jtf3;
	
	private JTextField coreDist;
	private JTextField edgeDist;
	private JTextField fogDist;
	private JTextField userDist;
	
	private TraceBasedTrafficPanel traceBasedTrafficPanel = new TraceBasedTrafficPanel();
	
	private JLabel lab1;
	private JLabel lab2;
	private JLabel lab3;
	
	private JLabel coreDistLabel;
	private JLabel edgeDistLabel;
	private JLabel fogDistLabel;
	private JLabel userDistLabel;
	
	private JLabel distLabel;
	
	private String[] distributionType;

	int type = TYPE_EXPONENTIAL;
	
	public static final int TYPE_LOCATION = 0;
	public static final int TYPE_EXPONENTIAL = 1;
	public static final int TYPE_GAMMA = 2;
	public static final int TYPE_LOGNORMAL = 3;
	public static final int TYPE_LOMAX = 4;
	public static final int TYPE_PARETO = 5;
	public static final int TYPE_UNIFORM = 6;
	public static final int TYPE_WEIBULL = 7;
	public static final int TYPE_ZIPF = 8;
	public static final int TYPE_ZIPF_WITH_BASE = 9;
	public static final int TYPE_NORMAL = 10;
	public static final int TYPE_DETERM = 11;
	public static final int TYPE_TRAFFIC = 12;

	public boolean isPrompt = true;

	private Border orgBorder;

	public CloudSimDistributionPanel() {
		this(TYPE_EXPONENTIAL);
		
		distLabel.setVisible(false);

	}
	public CloudSimDistributionPanel(int type) {
		this(type,null);
	}
	
	public CloudSimDistributionPanel(int type, String label) {
		this.type = type;
		
		distributionType = new String[]{"Location", "Exponential", "Gamma", "Lognormal","Lomax","Pareto","Uniform","Weibull","Zipf","ZipfWithBase","Normal","Deterministic", "TraceBasedTraffic"};

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
				updatePanel(ctype.getSelectedIndex());
			}
		});
		
		int textSize = 5;
		
		springPanel.add(distribution);		
		
		lab1 = new JLabel("lab1: ");
		springPanel.add(lab1);	
		jtf1 = new JTextField(textSize);
		lab1.setLabelFor(jtf1);
		springPanel.add(jtf1);
		springPanel.add(traceBasedTrafficPanel);
		
		lab2 = new JLabel("lab2: ");
		springPanel.add(lab2);	
		jtf2 = new JTextField(textSize);
		lab2.setLabelFor(jtf2);
		springPanel.add(jtf2);

		lab3 = new JLabel("lab3: ");
		springPanel.add(lab3);	
		jtf3 = new JTextField(textSize);
		lab3.setLabelFor(jtf3);
		springPanel.add(jtf3);

		
		coreDistLabel = new JLabel("Core: ");
		springPanel.add(coreDistLabel);	
		coreDist = new JTextField(textSize);
		coreDistLabel.setLabelFor(coreDist);
		springPanel.add(coreDist);		

		edgeDistLabel = new JLabel("Edge: ");
		springPanel.add(edgeDistLabel);	
		edgeDist = new JTextField(textSize);
		edgeDistLabel.setLabelFor(edgeDist);
		springPanel.add(edgeDist);		

		fogDistLabel = new JLabel("Fog: ");
		springPanel.add(fogDistLabel);	
		fogDist = new JTextField(textSize);
		fogDistLabel.setLabelFor(fogDist);
		springPanel.add(fogDist);		

		userDistLabel = new JLabel("ED: ");
		springPanel.add(userDistLabel);	
		userDist = new JTextField(textSize);
		userDistLabel.setLabelFor(userDist);
		springPanel.add(userDist);		

		super.add(springPanel, BorderLayout.CENTER);
		
		//super.setPreferredSize(new Dimension(800,40));
		
		super.setBorder(orgBorder = BorderFactory.createEtchedBorder());

		if (label == null) {
			if (type == TYPE_LOCATION) {
				distLabel.setText("Location Dist:");
				distribution.setVisible(false);
			} else {
				distLabel.setText("Size (MB):");
			}
		} else {
			distLabel.setText(label);
		}
		select(type);

	}
	
	protected void updatePanel(int type) {

		traceBasedTrafficPanel.setVisible(false);
		jtf1.setVisible(false);
		jtf2.setVisible(false);
		jtf3.setVisible(false);

		lab1.setVisible(false);
		lab2.setVisible(false);
		lab3.setVisible(false);

		coreDistLabel.setVisible(false);
		edgeDistLabel.setVisible(false);
		fogDistLabel.setVisible(false);
		userDistLabel.setVisible(false);

		coreDist.setVisible(false);
		edgeDist.setVisible(false);
		fogDist.setVisible(false);
		userDist.setVisible(false);
		
		switch(type){
		case TYPE_LOCATION:
			coreDistLabel.setVisible(true);
			edgeDistLabel.setVisible(true);
			fogDistLabel.setVisible(true);
			userDistLabel.setVisible(true);

			coreDist.setVisible(true);
			edgeDist.setVisible(true);
			fogDist.setVisible(true);
			userDist.setVisible(true);

			break;
		case TYPE_EXPONENTIAL:
			lab1.setVisible(true);
			lab1.setText("Mean");
			
			jtf1.setVisible(true);

			break;
		case TYPE_GAMMA:
			lab1.setVisible(true);
			lab1.setText("Alpha");
			lab2.setVisible(true);
			lab2.setText("Beta");

			jtf1.setVisible(true);
			jtf2.setVisible(true);

			break;
		case TYPE_LOGNORMAL:
			lab1.setVisible(true);
			lab1.setText("Mean");
			lab2.setVisible(true);
			lab2.setText("Dev");
		
			jtf1.setVisible(true);
			jtf2.setVisible(true);

			break;
		case TYPE_LOMAX:
			lab1.setVisible(true);
			lab1.setText("Shape");
			lab2.setVisible(true);
			lab2.setText("Location");
			lab3.setVisible(true);
			lab3.setText("Shift");

			jtf1.setVisible(true);
			jtf2.setVisible(true);
			jtf3.setVisible(true);

			break;
		case TYPE_PARETO:
			lab1.setVisible(true);
			lab1.setText("Shape");
			lab2.setVisible(true);
			lab2.setText("Location");

			jtf1.setVisible(true);
			jtf2.setVisible(true);

			break;
		case TYPE_UNIFORM:
			lab1.setVisible(true);
			lab1.setText("Min");
			lab2.setVisible(true);
			lab2.setText("Max");

			jtf1.setVisible(true);
			jtf2.setVisible(true);

			break;
		case TYPE_WEIBULL:
			lab1.setVisible(true);
			lab1.setText("Alpha");
			lab2.setVisible(true);
			lab2.setText("Beta");
			
			jtf1.setVisible(true);
			jtf2.setVisible(true);

			break;
		case TYPE_ZIPF:
			lab1.setVisible(true);
			lab1.setText("Shape");
			lab2.setVisible(true);
			lab2.setText("Population");
		
			jtf1.setVisible(true);
			jtf2.setVisible(true);

			break;
		case TYPE_ZIPF_WITH_BASE:
			lab1.setVisible(true);
			lab1.setText("Shape");
			lab2.setVisible(true);
			lab2.setText("Population");
			lab3.setVisible(true);
			lab3.setText("BaseQuanity");
		
			jtf1.setVisible(true);
			jtf2.setVisible(true);
			jtf3.setVisible(true);

			break;
		case TYPE_NORMAL:
			lab1.setVisible(true);
			lab1.setText("Mean");
			lab2.setVisible(true);
			lab2.setText("StdDev");
			
			jtf1.setVisible(true);
			jtf2.setVisible(true);
	
			break;
		case TYPE_DETERM:
			lab1.setVisible(true);
			lab1.setText("Value");
			
			jtf1.setVisible(true);

			break;
		case TYPE_TRAFFIC:
			lab1.setVisible(true);
			lab1.setText(/*"Traffic File"*/"");
			
			traceBasedTrafficPanel.setVisible(true);

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

		int type = findType(className);
				
		switch(type){
		case TYPE_LOCATION:
			try {
				coreDist.setText(parameterList.get(0).getValue().toString());
				edgeDist.setText(parameterList.get(1).getValue().toString());
				fogDist.setText(parameterList.get(2).getValue().toString());
				userDist.setText(parameterList.get(3).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		case TYPE_EXPONENTIAL:

			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		case TYPE_GAMMA:
			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
				jtf2.setText(parameterList.get(1).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}
			
			break;
		case TYPE_LOGNORMAL:
			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
				jtf2.setText(parameterList.get(1).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}
			
			break;
		case TYPE_LOMAX:
			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
				jtf2.setText(parameterList.get(1).getValue().toString());
				jtf3.setText(parameterList.get(1).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		case TYPE_PARETO:
			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
				jtf2.setText(parameterList.get(1).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		case TYPE_UNIFORM:
			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
				jtf2.setText(parameterList.get(1).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		case TYPE_WEIBULL:
			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
				jtf2.setText(parameterList.get(1).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		case TYPE_ZIPF:
			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
				jtf2.setText(parameterList.get(1).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		case TYPE_ZIPF_WITH_BASE:
			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
				jtf2.setText(parameterList.get(1).getValue().toString());
				jtf3.setText(parameterList.get(2).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		case TYPE_NORMAL:
			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
				jtf2.setText(parameterList.get(1).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		case TYPE_DETERM:
			try {
				jtf1.setText(parameterList.get(0).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		case TYPE_TRAFFIC:
			try {
				traceBasedTrafficPanel.setFileName(parameterList.get(0).getValue().toString());
				traceBasedTrafficPanel.setColumn(((Integer)parameterList.get(1).getValue()).intValue());
				traceBasedTrafficPanel.setSeparator(parameterList.get(2).getValue().toString());
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:show","Exception: " + e);
			}

			break;
		default:
			break;
		}
		
		select(type);
    }
    
	private void prompt(String msg){
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

    public void updateValues() {
		
    	String item = this.distribution.getSelectedItem().toString();
    	
		distModel.clearParameters();
		
		int type = findType(item);
		
		switch(type){
		case TYPE_LOCATION:
			distModel.initClassName(LocationDistribution.class);;
			try {
				double coreDistValue = Double.parseDouble(this.coreDist.getText());
				double edgeDistValue = Double.parseDouble(this.edgeDist.getText());
				double fogDistValue = Double.parseDouble(this.fogDist.getText());
				double userDistValue = Double.parseDouble(this.userDist.getText());
				
				if (coreDistValue + edgeDistValue + fogDistValue + userDistValue != 1.0) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("각 값의 총 합은 1 이어야 합니다.");
					return;
				}
				
				distModel.addParameter(new DistributionParameter("coreDist",coreDistValue));
				distModel.addParameter(new DistributionParameter("edgeDist",edgeDistValue));
				distModel.addParameter(new DistributionParameter("fogDist",fogDistValue));
				distModel.addParameter(new DistributionParameter("userDist",userDistValue));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				super.setBorder(BorderFactory.createLineBorder(Color.red));
				if(isPrompt)
					prompt("Only numbers are allowed.");
				return;
			}

			break;
		case TYPE_EXPONENTIAL:
			distModel.initClassName(ExponentialDistr.class);
			try {
				double mean = Double.parseDouble(this.jtf1.getText());
				if (mean <= 0) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("Mean must be greater than 0.0");
					return;
				}
				distModel.addParameter(new DistributionParameter("mean",mean));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}

			break;
		case TYPE_GAMMA:
			distModel.initClassName(GammaDistr.class);
			try {
				int alpha = (int)Double.parseDouble(this.jtf1.getText());
				double beta = Double.parseDouble(this.jtf2.getText());
				if (alpha <= 0 || beta <= 0.0) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("Alpha and beta must be greater than 0.0");
					return;
				}
				distModel.addParameter(new DistributionParameter("alpha",alpha));
				distModel.addParameter(new DistributionParameter("beta",beta));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}
			
			break;
		case TYPE_LOGNORMAL:
			distModel.initClassName(LognormalDistr.class);
			try {
				double mean = Double.parseDouble(this.jtf1.getText());
				double dev = Double.parseDouble(this.jtf2.getText());
				if (mean <= 0.0 || dev <= 0.0) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("Mean and deviation must be greater than 0.0");
					return;
				}
				distModel.addParameter(new DistributionParameter("mean",mean));
				distModel.addParameter(new DistributionParameter("dev",dev));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}
			
			break;
		case TYPE_LOMAX:
			distModel.initClassName(LomaxDistribution.class);
			try {
				double shift = Double.parseDouble(this.jtf3.getText());
				double location = Double.parseDouble(this.jtf2.getText());
				if (shift > location) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("Shift must be smaller or equal than location");
					return;
				}
				distModel.addParameter(new DistributionParameter("shape",Double.parseDouble(this.jtf1.getText())));
				distModel.addParameter(new DistributionParameter("location",location));
				distModel.addParameter(new DistributionParameter("shift",shift));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}

			break;
		case TYPE_PARETO:
			distModel.initClassName(ParetoDistr.class);
			try {
				double shape = Double.parseDouble(this.jtf1.getText());
				double location = Double.parseDouble(this.jtf2.getText());
				if (shape <= 0.0 || location <= 0.0) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("Mean and deviation must be greater than 0.0");
					return;
				}
				distModel.addParameter(new DistributionParameter("shape",shape));
				distModel.addParameter(new DistributionParameter("location",location));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}

			break;
		case TYPE_UNIFORM:
			distModel.initClassName(UniformDistr.class);
			try {
				double min = Double.parseDouble(this.jtf1.getText());
				double max = Double.parseDouble(this.jtf2.getText());
				if (min >= max) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("Maximum must be greater than the minimum.");
					return;
				}
				distModel.addParameter(new DistributionParameter("min",min));
				distModel.addParameter(new DistributionParameter("max",max));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}

			break;
		case TYPE_WEIBULL:
			distModel.initClassName(WeibullDistr.class);
			try {
				double alpha = Double.parseDouble(this.jtf1.getText());
				double beta = Double.parseDouble(this.jtf2.getText());
				if (alpha <= 0.0 || beta <= 0.0) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("Alpha and beta must be greater than 0.0");
					return;
				}
				distModel.addParameter(new DistributionParameter("alpha",alpha));
				distModel.addParameter(new DistributionParameter("beta",beta));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}

			break;
		case TYPE_ZIPF:
			distModel.initClassName(ZipfDistr.class);
			try {
				double shape = Double.parseDouble(this.jtf1.getText());
				int population = Integer.parseInt(this.jtf2.getText());
				if (shape <= 0.0 || population < 1) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("Mean must be greater than 0.0 and population greater than 0");
					return;
				}
				distModel.addParameter(new DistributionParameter("shape",shape));
				distModel.addParameter(new DistributionParameter("population",population));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}

			break;
		case TYPE_ZIPF_WITH_BASE:
			distModel.initClassName(ZipfDistr.class);
			try {
				double shape = Double.parseDouble(this.jtf1.getText());
				int population = Integer.parseInt(this.jtf2.getText());
				double baseQuanity = Double.parseDouble(this.jtf3.getText());
				if (shape <= 0.0 || population < 1) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("Mean must be greater than 0.0 and population greater than 0");
					return;
				}
				distModel.addParameter(new DistributionParameter("shape",shape));
				distModel.addParameter(new DistributionParameter("population",population));
				distModel.addParameter(new DistributionParameter("baseQuanity",baseQuanity));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}

			break;
		case TYPE_NORMAL:
			distModel.initClassName(NormalDistribution.class);
			try {
				double mean = Double.parseDouble(this.jtf1.getText());
				double stdDev = Double.parseDouble(this.jtf2.getText());
				if (mean <= 0.0 || stdDev <= 0.0) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("Mean and StdDev must be greater than 0.0");
					return;
				}
				distModel.addParameter(new DistributionParameter("shape",mean));
				distModel.addParameter(new DistributionParameter("population",stdDev));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}

			break;
		case TYPE_DETERM:
			distModel.initClassName(DeterministicDistribution.class);
			try {
				double value = Double.parseDouble(this.jtf1.getText());
				distModel.addParameter(new DistributionParameter("value",value));
			} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}

			break;
		case TYPE_TRAFFIC:
			distModel.initClassName(TraceBasedTrafficDistribution.class);
			try {
				String fileName = traceBasedTrafficPanel.getFileName();
				int column = traceBasedTrafficPanel.getColumn();
				String separator = traceBasedTrafficPanel.getSeparator();

				if (fileName.length() == 0) {
					super.setBorder(BorderFactory.createLineBorder(Color.red));
					if(isPrompt)
						prompt("File is empty.");
					return;
				}
				distModel.addParameter(new DistributionParameter("file name",fileName));
				distModel.addParameter(new DistributionParameter("column",column));
				distModel.addParameter(new DistributionParameter("separator",separator));
			} catch (Exception e) {
				// CWE-390 add code
				Logger.error("CloudSimDistributionPanel:updateValues","Exception: " + e);
			}

			break;
		default:
			break;
		}
		
		super.setBorder(orgBorder);

    }

    private int findType(String className) {
    	for (int i=0;i<distributionType.length;i++) {
    		if (className.contains(distributionType[i])) {
    			return i;
    		}
    	}
    	return -1;
    }
    
    private class TraceBasedTrafficPanel extends JPanel implements ActionListener {
		private JButton butFile = new JButton("...");
		private JLabel labFile = new JLabel();
		private IntTextField jtfColumn = new IntTextField(2);
		private JTextField jtfSeparator = new JTextField(2);
		
		public TraceBasedTrafficPanel() {
			labFile.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			butFile.addActionListener(this);

			butFile.setPreferredSize(new Dimension(25,25));

			super.setLayout(new BorderLayout());
			
			super.add(butFile,BorderLayout.WEST);
			
			JPanel pane = new JPanel();
			pane.add(labFile);
			pane.add(new JLabel(", column:"));
			pane.add(jtfColumn);
			pane.add(new JLabel(", separator:"));
			pane.add(jtfSeparator);
			
			super.add(pane,BorderLayout.CENTER);
		}
		
		public void setFileName(String fileName) {
			labFile.setText(fileName);
		}
		
		public String getFileName() {
			return labFile.getText().trim();
		}
		
		public void setColumn(int column) {
			jtfColumn.setText(column+"");
		}
		
		public int getColumn() {
			int column = jtfColumn.getIntValue();
			if (column == 0) column = 1;
			return column;
		}
		
		public void setSeparator(String separator) {
			jtfSeparator.setText(separator);
		}
		
		public String getSeparator() {
			String separator = jtfSeparator.getText().trim();
			if (separator.length() == 0) {
				separator = "\t ";
				jtfSeparator.setText(separator);
			} else { 
				separator += " ";
			}
			
			return separator;
		}
		
		public void actionPerformed(ActionEvent event) {
	    	JFileChooser fileopen = new JFileChooser(System.getProperty("user.dir"));
	        //fileopen.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
	        fileopen.setAcceptAllFileFilterUsed(false);
	        
	        int ret = fileopen.showOpenDialog(this);

	        if (ret == JFileChooser.APPROVE_OPTION) {
	        	File file = fileopen.getSelectedFile();
	        	if (file.isFile()) {
	                String absPath = file.getAbsolutePath();

	                String userDir = System.getProperty("user.dir");
	                
	                String traceFileName = absPath.substring(userDir.length()+1);
	                
	                labFile.setText(traceFileName);
	        	}
	        }
		}
		
    }
}
