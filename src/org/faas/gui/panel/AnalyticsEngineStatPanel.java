package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.faas.DFaaSConstants;
import org.faas.stats.DataDurationStats;
import org.faas.stats.DataStats;
import org.faas.stats.FunctionStats;
import org.faas.stats.StatValue;
import org.faas.topology.FunctionProfile;
import org.faas.topology.NetworkTopologyHelper;

public class AnalyticsEngineStatPanel extends JPanel {

	private DecimalFormat decimalFormatter = new DecimalFormat("###.###");

	private static AnalyticsEngineStatPanel me;
	
	private JTabbedPane tabPane = new JTabbedPane();
	Map<String,FunctionStatsResultPanel> panelMap = new HashMap<String,FunctionStatsResultPanel>();

	public AnalyticsEngineStatPanel() {
		me = this;
		super.setLayout(new BorderLayout());
		super.add(tabPane,BorderLayout.CENTER);
	}
	
	public static AnalyticsEngineStatPanel getInstance() {
		return me;
	}
	
	public void clear() {
		tabPane.removeAll();
		panelMap.clear();
		super.getParent().validate();
	}

	public void show(List<FunctionStats> functionStats) {

		if (functionStats == null) return;
		
		Iterator<FunctionStatsResultPanel> ite = panelMap.values().iterator();
		while (ite.hasNext()) {
			ite.next().clear();
		}
		
		for (int i=0;i<functionStats.size();i++) {
			FunctionStats functionStatValue = functionStats.get(i);
			
			String profileName = NetworkTopologyHelper.getInstance().getFunctionProfile(functionStatValue.getFunctionProfileId()).getName();
			FunctionStatsResultPanel panel = panelMap.get(functionStatValue.getFunctionProfileId());
			if (panel == null) {
				panel = new FunctionStatsResultPanel();
				panelMap.put(functionStatValue.getFunctionProfileId(),panel);
				tabPane.addTab(profileName, panel);
				super.getParent().validate();
			}
			
			panel.show(functionStatValue);
		}
		
	}
	
	class FunctionStatsResultPanel extends JPanel {
		JTable statTable = new JTable();
		StatValueTableModel model = new StatValueTableModel();
		
		FunctionStatsResultPanel() {
//			statTable.setFocusable(false);
//			statTable.setEnabled(false);
			super.setLayout(new BorderLayout());
			JScrollPane jsp = new JScrollPane();
			jsp.getViewport().add(statTable);
			super.add(jsp, BorderLayout.CENTER);
			
			statTable.setModel(model);

		}
		
		void clear() {
			model = new StatValueTableModel();
			statTable.setModel(model);
		}

		void show(FunctionStats functionStats) {
			
			//model.addRow(functionStats.getFunctionProfileId()+" "+DFaaSConstants.NODE_NAMES[functionStats.getNodeGroupType()],null);
			model.addRow("["+DFaaSConstants.getEntityName(functionStats.getNodeGroupType())+"]",null);
			
			model.addRow("	*Function stats",null);
			model.addRow("		FunctionRunningDuration stats",functionStats.getFunctionRunningDurationStat());
			model.addRow("		ProcessingDuration stats",functionStats.getProcessingDurationStats());
			model.addRow("		CompletionDuration stats",functionStats.getCompletionDurationStats());
			model.addRow("		ReqMessageSize stats",functionStats.getReqMessageSizeStat());
			model.addRow("		RspMessageSize stats",functionStats.getRspMessageSizeStat());

			DataStats inputDataSizeStats = functionStats.getInputDataSizeStats();
			model.addRow("",null);
			model.addRow("	*Input data size stats",null);
			model.addRow("		End stats",inputDataSizeStats.getDataStatMap().get(DFaaSConstants.END_EDVICE_GROUP));
			model.addRow("		Fog stats",inputDataSizeStats.getDataStatMap().get(DFaaSConstants.FOG_NODE_GROUP));
			model.addRow("		Edge stats",inputDataSizeStats.getDataStatMap().get(DFaaSConstants.EDGE_NODE_GROUP));
			model.addRow("		Core stats",inputDataSizeStats.getDataStatMap().get(DFaaSConstants.CORE_NODE_GROUP));

			DataStats outputDataSizeStats = functionStats.getOutputDataSizeStats();
			model.addRow("",null);
			model.addRow("	*Output data size stats",null);
			model.addRow("		End stats",outputDataSizeStats.getDataStatMap().get(DFaaSConstants.END_EDVICE_GROUP));
			model.addRow("		Fog stats",outputDataSizeStats.getDataStatMap().get(DFaaSConstants.FOG_NODE_GROUP));
			model.addRow("		Edge stats",outputDataSizeStats.getDataStatMap().get(DFaaSConstants.EDGE_NODE_GROUP));
			model.addRow("		Core stats",outputDataSizeStats.getDataStatMap().get(DFaaSConstants.CORE_NODE_GROUP));

			DataDurationStats inputDataDurationStats = functionStats.getInputDataDurationStats();
			model.addRow("",null);
			model.addRow("	*Input data duration stats",null);
			model.addRow("		End stats",inputDataDurationStats.getDurationOfEDData());
			model.addRow("		Fog stats",inputDataDurationStats.getDurationOfFogData());
			model.addRow("		Edge stats",inputDataDurationStats.getDurationOfEdgeData());
			model.addRow("		Core stats",inputDataDurationStats.getDurationOfCoreData());

			DataDurationStats outputDataDurationStats = functionStats.getOutputDataDurationStats();
			model.addRow("",null);
			model.addRow("	*Output data duration stats",null);
			model.addRow("		End stats",outputDataDurationStats.getDurationOfEDData());
			model.addRow("		Fog stats",outputDataDurationStats.getDurationOfFogData());
			model.addRow("		Edge stats",outputDataDurationStats.getDurationOfEdgeData());
			model.addRow("		Core stats",outputDataDurationStats.getDurationOfCoreData());

			model.addRow("",null);
			model.addRow("",null);

			model.fireTableDataChanged();
		}
	}
	
	class StatValueTableModel extends AbstractTableModel {
		
		class StatValueRow {
			String name;
			StatValue statValue;
			
			StatValueRow(String name,StatValue statValue) {
				this.name = name;
				this.statValue = statValue;
			}
		}
		
		String columnNames[] = new String[] {"", "Min","Max", "Mean", "Median", "Variance"};
		List<StatValueRow> rowData = new ArrayList<StatValueRow>();
		
		StatValueTableModel() {
		}

		void addRow(String name,StatValue statValue) {
			rowData.add(new StatValueRow(name,statValue));
		}
		
		public String getColumnName(int col) {
	        return columnNames[col];
	    }
	    public int getRowCount() { return rowData.size(); }
	    public int getColumnCount() { return columnNames.length; }
	    public Object getValueAt(int row, int col) {
	    	StatValueRow data = rowData.get(row);
	    	
	    	if (col == 0) {
	    		return data.name;
	    	} else if (col == 1) {
	    		return data.statValue==null?"":decimalFormatter.format(data.statValue.getMin());
	    	} else if (col == 2) {
	    		return data.statValue==null?"":decimalFormatter.format(data.statValue.getMax());
	    	} else if (col == 3) {
	    		return data.statValue==null?"":decimalFormatter.format(data.statValue.getMean());
	    	} else if (col == 4) {
	    		return data.statValue==null?"":decimalFormatter.format(data.statValue.getMedian());
	    	} else if (col == 5) {
	    		return data.statValue==null?"":decimalFormatter.format(data.statValue.getVariance());
	    	}
	        return "";
	    }
	    public boolean isCellEditable(int row, int col) { 
	    	return false; 
	    }
	}
	
//	class StatValueTableCellRenderer implements TableCellRenderer {
//		  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
//		      boolean hasFocus, int row, int column) {
//		    JTextField editor = new JTextField();
//		    if (value != null)
//		      editor.setText(value.toString());
//		    editor.setBackground((row % 2 == 0) ? Color.white : Color.cyan);
//		    return editor;
//		  }
//		}
}
