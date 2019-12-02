package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.cloudbus.cloudsim.core.CloudSim;
import org.faas.DFaaSConstants;
import org.faas.SimulationManager;
import org.faas.network.HIINetwork;
import org.faas.stats.MonitoringData;
import org.faas.stats.MonitoringData.NodeGroupQueueSum;
import org.faas.stats.MonitoringData.QueueResourcePoolStats;
import org.faas.stats.QueueModality;
import org.faas.stats.ResourcePoolModality;
import org.faas.stats.ResourceStatsData.FunctionWaitingState;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.stats.ResourceUtilization;

public class MonitoringPanel extends JPanel {

	private DecimalFormat decimalFormatter = new DecimalFormat("###.###");
	
	// node group id, JPanel
	private Map<Integer,QueueSumPanel> queueSumPanelMap = new HashMap<Integer,QueueSumPanel>();
	private Map<Integer,ResourceStatPanel> resourcePanelMap = new HashMap<Integer,ResourceStatPanel>();
	
	private JLabel labSimulationTime = new JLabel();
	private JLabel labRequested = new JLabel();
	private JLabel labFinished = new JLabel();
	private JLabel labViolation = new JLabel();
	private JLabel labGaps = new JLabel();
	
	private JLabel labAverageFunctionExecutionCost = new JLabel();
	private JLabel labSLOViolationRatio = new JLabel();
	
	private JPanel topPanel = new JPanel(new BorderLayout()); //= new JPanel(new GridLayout(2,1));
	private JPanel queuePanels = new JPanel(new GridLayout(0,2));
	private JPanel resourceStatPanels = new JPanel(new GridLayout(0,2));
	private ResourceUtilizationPanel resourceUtilizationPanel = new ResourceUtilizationPanel();
	
	private Font plainFont;
	private Font boldFont;
	public MonitoringPanel() {
		
		plainFont = new Font("Lucida Grande", Font.PLAIN, 25); // default size 13
		boldFont = new Font("Lucida Grande", Font.PLAIN, 30);
		
		super.setLayout(new BorderLayout());
		
		JPanel topPanel1 = new JPanel();
		topPanel1.setLayout(new GridLayout(1,4));

		JPanel topPanel2 = new JPanel();
		//topPanel2.setLayout(new GridLayout(1,2));

		addNameValue(topPanel1,"Simulation time : ",labSimulationTime);
		addNameValue(topPanel1,"Number of functions requested : ",labRequested);
		addNameValue(topPanel1,"Number of functions completed : ",labFinished);
		addNameValue(topPanel1,"Number of functions with SLO violation : ",labViolation);
		
		addNameValue(topPanel2,"Average Function Execution Cost : ",labAverageFunctionExecutionCost ,plainFont ,boldFont);
		addNameValue(topPanel2,"      SLO Violation Ratio(%) : ",labSLOViolationRatio,plainFont , boldFont);
		
		topPanel.add(topPanel1,BorderLayout.NORTH);
		topPanel.add(topPanel2,BorderLayout.CENTER);
		
		super.add(topPanel,BorderLayout.NORTH);
		
		JPanel pane = new JPanel(new BorderLayout());
		super.add(pane,BorderLayout.CENTER);

		JPanel utilizationPanel = new JPanel(new BorderLayout());
		utilizationPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		utilizationPanel.add(new JLabel(" -Resource Utilization per Node Group"),BorderLayout.NORTH);
		utilizationPanel.add(resourceUtilizationPanel,BorderLayout.CENTER);
		pane.add(utilizationPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		centerPanel.add(new JLabel(" -Function Execution Status per Node Group"),BorderLayout.NORTH);
		centerPanel.add(queuePanels,BorderLayout.CENTER);
		pane.add(centerPanel, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));
		p.add(new JLabel(" -Resource & Queue Modality per Node group"));
		p.add(labGaps);
		southPanel.add(p,BorderLayout.NORTH);
		southPanel.add(resourceStatPanels,BorderLayout.CENTER);
		pane.add(southPanel, BorderLayout.SOUTH);
		
		this.setVisible(false);

	}
	
	public void clear() {
		this.setVisible(false);
		
		queuePanels.removeAll();
		resourceStatPanels.removeAll();
		resourceUtilizationPanel.clear();
		
		queueSumPanelMap.clear();
		resourcePanelMap.clear();
		
		labSimulationTime.setText("");
		labRequested.setText("");
		labFinished.setText("");
		labViolation.setText("");
		
	}
	
	private void addNameValue(JPanel p,String title,JLabel labValue) {
		addNameValue(p, title, labValue, super.getFont(),super.getFont());
	}
	
	private void addNameValue(JPanel p, String title,JLabel labValue, Font font1, Font font2) {
		
		JPanel rowPanel = new JPanel();
		//rowPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		rowPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		JLabel labTitle = new JLabel();
		labTitle.setText(title);
		
		rowPanel.add(labTitle);
		rowPanel.add(labValue);
		
		labTitle.setFont(font1);
		labValue.setFont(font2);
		
		p.add(rowPanel);
	}
	
	public void requestUpdate() {
		final List<MonitoringData> list = new ArrayList<MonitoringData>();
		list.addAll(MonitoringData.getList());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (int i=0;i<list.size();i++) {
					show(list.get(i));
				}
				list.clear();
			}
		});
	}
	
	public void showClock(double clock) {
		if (this.isVisible() == false) {
			this.setVisible(true);
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				labSimulationTime.setText(decimalFormatter.format(clock)+"");
			}
		});
	}
	
	public void show(MonitoringData data) {
		if (this.isVisible() == false) {
			this.setVisible(true);
		}
		if (data == null) return;
		
		labSimulationTime.setText(decimalFormatter.format(data.getSimulationTime())+"");
		labRequested.setText(data.getRequestedFunctionCount()+"");
		labFinished.setText(data.getFinishedFunctionCount()+"");
		labViolation.setText(data.getViolationFunctionCount()+"");
		
		labAverageFunctionExecutionCost.setText(decimalFormatter.format(data.getTotalCost()/data.getFinishedFunctionCount())+"");
		
		double finishedCount = (double)data.getFinishedFunctionCount();
		double violationCount = (double)data.getViolationFunctionCount();
		double sloViolationRatio = violationCount/finishedCount*100;
		
		if (sloViolationRatio > 100) sloViolationRatio = 100;
		
		labSLOViolationRatio.setText(decimalFormatter.format(sloViolationRatio)+"");
		
		List<NodeGroupQueueSum> queueSumList = new ArrayList<NodeGroupQueueSum>(data.getNodeGroupQueueSumMap().values());
//			@Override
//			public int compare(NodeGroupQueueSum o1, NodeGroupQueueSum o2) {
//				return o1.getNodeGroupType() - o2.getNodeGroupType();
//			}
//		});
		
		Iterator<NodeGroupQueueSum> nodeGroupQueueSumIte = queueSumList.iterator();
		while (nodeGroupQueueSumIte.hasNext()) {
			NodeGroupQueueSum nodeGroupQueueSum = nodeGroupQueueSumIte.next();
			int nodeGroupId = nodeGroupQueueSum.getNodeGroupId();
			int nodeGroupType = NetworkTopologyHelper.getInstance().getNode(HIINetwork.getInstance().toTopologyId(nodeGroupId)).getType();//nodeGroupQueueSum.getNodeGroupType();
			QueueSumPanel queuePanel = queueSumPanelMap.get(nodeGroupId);
			
			if (queuePanel == null) {
				queuePanel = new QueueSumPanel(nodeGroupId);
				queuePanel.setBorder(BorderFactory.createTitledBorder(DFaaSConstants.getEntityName(nodeGroupType)+" #"+HIINetwork.getInstance().toTopologyId(nodeGroupId)));
				queueSumPanelMap.put(nodeGroupId, queuePanel);
				queuePanels.add(queuePanel);
			}
			queuePanel.show(nodeGroupQueueSum);
		}
		
		List<QueueResourcePoolStats> poolStatsList = new ArrayList<QueueResourcePoolStats>(data.getResourceStatsMap().values());
//		Collections.sort(poolStatsList, new Comparator<QueueResourcePoolStats>() {
//			@Override
//			public int compare(QueueResourcePoolStats o1, QueueResourcePoolStats o2) {
//				return o1.getNodeGroupType() - o2.getNodeGroupType();
//			}
//		});
		
		Iterator<QueueResourcePoolStats> resourceStatsIte = poolStatsList.iterator();
		while (resourceStatsIte.hasNext()) {
			QueueResourcePoolStats resourceStats = resourceStatsIte.next();
			int nodeGroupId = resourceStats.getNodeGroupId();
			int nodeGroupType = NetworkTopologyHelper.getInstance().getNode(HIINetwork.getInstance().toTopologyId(nodeGroupId)).getType();//resourceStats.getNodeGroupType();
			ResourceStatPanel resourceStatPanel = this.resourcePanelMap.get(nodeGroupId);
			if (resourceStatPanel == null) {
				resourceStatPanel = new ResourceStatPanel();
				resourceStatPanel.setBorder(BorderFactory.createTitledBorder(DFaaSConstants.getEntityName(nodeGroupType)+" #"+HIINetwork.getInstance().toTopologyId(nodeGroupId)));
				resourcePanelMap.put(nodeGroupId, resourceStatPanel);
				resourceStatPanels.add(resourceStatPanel);
			}
			resourceStatPanel.show(resourceStats);
		}
		
		resourceUtilizationPanel.show(data.getResourceUtilizationList());

	}
	
	class ResourceUtilizationPanel extends JPanel {
		
		JTable cpuTable = new JTable();
		JTable memoryTable = new JTable();

		CpuUtilizationTableModel cpuModel = new CpuUtilizationTableModel();
		MemoryUtilizationTableModel memoryModel = new MemoryUtilizationTableModel();
		
		ResourceUtilizationPanel() {
			super.setLayout(new GridLayout(1,2));

			JScrollPane jsp;
			int width=100,height=110;
			
			cpuTable.setModel(cpuModel);
			jsp = new JScrollPane();
			jsp.setPreferredSize(new Dimension(width,height));
			jsp.setBorder(BorderFactory.createTitledBorder("CPU"));
			jsp.getViewport().add(cpuTable);
			super.add(jsp);

			memoryTable.setModel(memoryModel);
			jsp = new JScrollPane();
			jsp.setPreferredSize(new Dimension(width,height));
			jsp.setBorder(BorderFactory.createTitledBorder("Memory"));
			jsp.getViewport().add(memoryTable);
			super.add(jsp);

		}
		
		void show(List<ResourceUtilization> resourceUtilizationList) {
			//if (resourceUtilizationList == null) return;
			
			cpuModel.refreshRowdata(resourceUtilizationList);
			cpuModel.fireTableDataChanged();
			
			memoryModel.refreshRowdata(resourceUtilizationList);
			memoryModel.fireTableDataChanged();
			
		}
		
		void clear() {
			show(new ArrayList());
		}
	}
	
	class QueueSumPanel extends JPanel {
		JTable waitingTable = new JTable();
		JTable runningTable = new JTable();
		JTable finishedTable = new JTable();
		
		QueueSummaryTableModel waitingModel = new QueueSummaryTableModel();
		QueueSummaryTableModel runningModel = new QueueSummaryTableModel();
		QueueSummaryTableModel finishedModel = new QueueSummaryTableModel();
		
		QueueSumPanel(int nodeGroupId) {
			super(new GridLayout(1,3));
			
			//JPanel centerPanel = new JPanel(new GridLayout(1,3));
			JScrollPane jsp;
			int width=100,height=100;
			
			waitingTable.setModel(waitingModel);
			runningTable.setModel(runningModel);
			finishedTable.setModel(finishedModel);
			
			jsp = new JScrollPane();
			jsp.setPreferredSize(new Dimension(width,height));
			jsp.setBorder(BorderFactory.createTitledBorder("Waiting"));
			jsp.getViewport().add(waitingTable);
			super.add(jsp);

			jsp = new JScrollPane();
			jsp.setPreferredSize(new Dimension(width,height));
			jsp.setBorder(BorderFactory.createTitledBorder("Running"));
			jsp.getViewport().add(runningTable);
			super.add(jsp);

			jsp = new JScrollPane();
			jsp.setPreferredSize(new Dimension(width,height));
			jsp.setBorder(BorderFactory.createTitledBorder("Finished"));
			jsp.getViewport().add(finishedTable);
			super.add(jsp);

			//super.add(centerPanel, BorderLayout.CENTER);
		}
		
		void show(NodeGroupQueueSum nodeGroupQueueSum) {
			waitingModel.refreshRowdata(nodeGroupQueueSum.getWaitingQueueSumData());
			runningModel.refreshRowdata(nodeGroupQueueSum.getRunningQueueSumData());
			finishedModel.refreshRowdata(nodeGroupQueueSum.getFinishedQueueSumData());
			
			waitingModel.fireTableDataChanged();
			runningModel.fireTableDataChanged();
			finishedModel.fireTableDataChanged();
		}
	}
	
	class ResourceStatPanel extends JPanel {
		
		JTable resourceTable = new JTable();
		JTable queueTable = new JTable();

		JTable functoinWaitingStateTable = new JTable();
		
		ResourcePoolModalityTableModel resourceModel = new ResourcePoolModalityTableModel();
		QueueModalityTableModel queueModel = new QueueModalityTableModel();
		
		ResourceStatPanel() {
			super.setLayout(new GridLayout(1,3));

			JScrollPane jsp;
			int width=100,height=110;
			
			resourceTable.setModel(resourceModel);
			resourceTable.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(170);
			jsp = new JScrollPane();
			jsp.setPreferredSize(new Dimension(width,height));
			jsp.setBorder(BorderFactory.createTitledBorder("Resource Pool Modality"));
			jsp.getViewport().add(resourceTable);
			super.add(jsp);

			queueTable.setModel(queueModel);
			queueTable.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(170);
			jsp = new JScrollPane();
			jsp.setPreferredSize(new Dimension(width,height));
			jsp.setBorder(BorderFactory.createTitledBorder("Queue Modality"));
			jsp.getViewport().add(queueTable);
			super.add(jsp);

//			functoinWaitingStateTable.setModel(functionWaitingStateTableModel);
//			functoinWaitingStateTable.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(170);
//			jsp = new JScrollPane();
//			jsp.setPreferredSize(new Dimension(width,height));
//			jsp.setBorder(BorderFactory.createTitledBorder("Function Waiting State"));
//			jsp.getViewport().add(functoinWaitingStateTable);
//			super.add(jsp);
		}
		
		void show(QueueResourcePoolStats resourceStats) {
			resourceModel.refreshRowdata(new ArrayList(resourceStats.getResourcePoolModalityMap().values()));
			resourceModel.fireTableDataChanged();
			
			queueModel.refreshRowdata(new ArrayList(resourceStats.getQueueModalityMap().values()));
			queueModel.fireTableDataChanged();
			
		}
	}
	
	class QueueSummaryTableModel extends AbstractTableModel {
		
		class GradeCount {
			GradeCount(int grade,int count) {
				this.grade = grade;
				this.count = count;
			}
			int grade;
			int count;
		}
		
		String columnNames[] = new String[] {"Grade","Count"};
		List<GradeCount> rowData = new ArrayList<GradeCount>();
		
		void refreshRowdata(MonitoringData.NodeGroupQueueSumData newRowData) {
			rowData.clear();
			if (newRowData== null) return;
			Iterator<Integer> gradeIte = newRowData.getGrades();
			while (gradeIte.hasNext()) {
				int grade = gradeIte.next();
				int count = newRowData.getCount(grade);
				rowData.add(new GradeCount(grade,count));
			}
		}
		public String getColumnName(int col) {
	        return columnNames[col];
	    }
	    public int getRowCount() { return rowData.size(); }
	    public int getColumnCount() { return columnNames.length; }
	    public Object getValueAt(int row, int col) {
	    	GradeCount data = rowData.get(row);
	    	if (col == 0) {
	    		return data.grade;
	    	} else if (col == 1) {
	    		return data.count;
	    	}
	        return "";
	    }
	    public boolean isCellEditable(int row, int col)
	        { return false; }
	    public void setValueAt(Object value, int row, int col) {
	    	// never called
//	        rowData[row][col] = value;
//	        fireTableCellUpdated(row, col);
	    }
	}
	
	class ResourcePoolModalityTableModel extends AbstractTableModel {
		
		String columnNames[] = new String[] {"remaining duration (ms)","request","cores"};
		List<ResourcePoolModality> rowData = new ArrayList<ResourcePoolModality>();
		
		void refreshRowdata(List<ResourcePoolModality> newRowData) {
			rowData = newRowData;
		}
		public String getColumnName(int col) {
	        return columnNames[col];
	    }
	    public int getRowCount() { return rowData.size(); }
	    public int getColumnCount() { return columnNames.length; }
	    public Object getValueAt(int row, int col) {
	    	ResourcePoolModality data = rowData.get(row);
	    	if (col == 0) {
	    		return data.getRemainingFunctionRunningDuration();
	    	} else if (col == 1) {
	    		return data.getNumberOfFunctionRequests();
	    	} else if (col == 2) {
	    		return data.getUsingNumberOfCore();
	    	}
	        return "";
	    }
	    public boolean isCellEditable(int row, int col)
	        { return false; }
	    public void setValueAt(Object value, int row, int col) {
	    	// never called
//	        rowData[row][col] = value;
//	        fireTableCellUpdated(row, col);
	    }
	}
	
	class QueueModalityTableModel extends AbstractTableModel {
		
		String columnNames[] = new String[] {"expected duration (ms)","requests","cores"};
		List<QueueModality> rowData = new ArrayList<QueueModality>();
		
		void refreshRowdata(List<QueueModality> newRowData) {
			rowData = newRowData;
		}
		public String getColumnName(int col) {
	        return columnNames[col];
	    }
	    public int getRowCount() { return rowData.size(); }
	    public int getColumnCount() { return columnNames.length; }
	    public Object getValueAt(int row, int col) {
	    	QueueModality data = rowData.get(row);
	    	if (col == 0) {
	    		return data.getExpectedFunctionRunningDuration();
	    	} else if (col == 1) {
	    		return data.getNumberOfFunctionRequests();
	    	} else if (col == 2) {
	    		return data.getUsingNumberOfCore();
	    	}
	        return "";
	    }
	    public boolean isCellEditable(int row, int col)
	        { return false; }
	    public void setValueAt(Object value, int row, int col) {
	    	// never called
//	        rowData[row][col] = value;
//	        fireTableCellUpdated(row, col);
	    }
	}
	
	class FunctionWaitingStateTableModel extends AbstractTableModel {
		
		String columnNames[] = new String[] {"expected duration"/*,"requests"*/,"functions"};
		List<FunctionWaitingState> rowData = new ArrayList<FunctionWaitingState>();
		
		void refreshRowdata(List<FunctionWaitingState> newRowData) {
			rowData = newRowData;
		}
		public String getColumnName(int col) {
	        return columnNames[col];
	    }
	    public int getRowCount() { return rowData.size(); }
	    public int getColumnCount() { return columnNames.length; }
	    public Object getValueAt(int row, int col) {
	    	FunctionWaitingState data = rowData.get(row);
	    	if (col == 0) {
	    		return data.getExpectedFunctionRunningDurationStr();
//	    	} else if (col == 1) {
//	    		return "";//data.getNumberOfFunctionRequests(); TODO
	    	} else if (col == 1) {
	    		return data.getFunctionNumber();
	    	}
	        return "";
	    }
	    public boolean isCellEditable(int row, int col)
	        { return false; }
	    public void setValueAt(Object value, int row, int col) {
	    }
	}
	
	class CpuUtilizationTableModel extends AbstractTableModel {
		
		String columnNames[] = new String[] {"Node Group","Capacity (cores)","Used (cores)","Utilization"};
		List<ResourceUtilization> rowData = new ArrayList<ResourceUtilization>();
		
		void refreshRowdata(List<ResourceUtilization> newRowData) {
			rowData = newRowData;
		}
		public String getColumnName(int col) {
	        return columnNames[col];
	    }
	    public int getRowCount() { return rowData.size(); }
	    public int getColumnCount() { return columnNames.length; }
	    public Object getValueAt(int row, int col) {
	    	ResourceUtilization data = rowData.get(row);
	    	if (col == 0) {
	    		return data.getNodeId();
	    	} else if (col == 1) {
	    		return decimalFormatter.format(data.getTotalCores());
	    	} else if (col == 2) {
	    		return decimalFormatter.format(data.getUsedCores());
	    	} else if (col == 3) {
	    		return decimalFormatter.format(data.getCpuUtilization());
	    	}
	        return "";
	    }
	    public boolean isCellEditable(int row, int col)
	        { return false; }
	    public void setValueAt(Object value, int row, int col) {
	    }
	}
	
	class MemoryUtilizationTableModel extends AbstractTableModel {
		
		String columnNames[] = new String[] {"Node Group","Capacity (MB)","Used (MB)","Utilization"};
		List<ResourceUtilization> rowData = new ArrayList<ResourceUtilization>();
		
		void refreshRowdata(List<ResourceUtilization> newRowData) {
			rowData = newRowData;
		}
		public String getColumnName(int col) {
	        return columnNames[col];
	    }
	    public int getRowCount() { return rowData.size(); }
	    public int getColumnCount() { return columnNames.length; }
	    public Object getValueAt(int row, int col) {
	    	ResourceUtilization data = rowData.get(row);
	    	if (col == 0) {
	    		return data.getNodeId();
	    	} else if (col == 1) {
	    		return decimalFormatter.format(data.getTotalRam());
	    	} else if (col == 2) {
	    		return decimalFormatter.format(data.getUsedRam());
	    	} else if (col == 3) {
	    		return decimalFormatter.format(data.getMemoryUtilization());
	    	}
	        return "";
	    }
	    public boolean isCellEditable(int row, int col)
	        { return false; }
	    public void setValueAt(Object value, int row, int col) {
	    }
	}
}
