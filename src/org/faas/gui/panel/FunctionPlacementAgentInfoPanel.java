package org.faas.gui.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.faas.dfaasfunctionplacementpolicy.FunctionPlacementAgent;
import org.faas.topology.FunctionPlacementAgentInfo;
import org.faas.utils.ReflectionUtil;

public class FunctionPlacementAgentInfoPanel extends JPanel {

	private JComboBox comboClass = new JComboBox();
	private JComboBox comboConstructor = new JComboBox();
	
	private JPanel panelParams = new JPanel(new GridLayout(0,1));
	
	public FunctionPlacementAgentInfoPanel() {
		comboClass.setMinimumSize(new Dimension(20,20));
		comboConstructor.setMinimumSize(new Dimension(20,20));

		super.setBorder(BorderFactory.createTitledBorder("Function Placement Agent Info."));
		
		List<ClassComboItem> items = new ArrayList<ClassComboItem>();
		
		List<Class> agentClasses = FunctionPlacementAgent.getAgentList();
		comboClass.addItem(new ClassComboItem(null));
		for (int i=0;i<agentClasses.size();i++) {
			comboClass.addItem(new ClassComboItem(agentClasses.get(i)));
		}

		if (comboClass.getItemCount()>0) {
			comboClass.setSelectedIndex(0);
		}
		
		packConstructorCombo();
		if (comboConstructor.getItemCount()>0) {
			comboConstructor.setSelectedIndex(0);
		}
		
		packConstructorParams();
		
		JPanel panelCombos = new JPanel(new GridLayout(0,1));
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		p.add(new JLabel("Class : "));
		p.add(comboClass);
		panelCombos.add(p);

		p = new JPanel(new FlowLayout(FlowLayout.LEADING));
		p.add(new JLabel("Constructor : "));
		p.add(comboConstructor);
		panelCombos.add(p);
		
		add(panelCombos);
		add(panelParams);
		
		comboClass.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					packConstructorCombo();
					if (comboConstructor.getItemCount()>0) {
						comboConstructor.setSelectedIndex(0);
					}
					
					packConstructorParams();

				}
			}
			
		});
		
		comboConstructor.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					packConstructorParams();
				}
			}
			
		});
	}
	
	public void show(FunctionPlacementAgentInfo agentInfo) {
		
		if (agentInfo == null) {
			comboClass.setSelectedIndex(0);
			this.packConstructorCombo();

			comboConstructor.setSelectedIndex(0);
			this.packConstructorParams();
		} else {
			int index = 0;
			for (;index<comboClass.getItemCount();index++) {
				if (((ClassComboItem)comboClass.getItemAt(index)).theSame(agentInfo.getClassName())) {
					break;
				}
			}
			if (comboClass.getItemCount() > index) {
				comboClass.setSelectedIndex(index);
			} else {
				comboClass.setSelectedIndex(0);
			}
			this.packConstructorCombo();

			index = 0;
			for (;index<comboConstructor.getItemCount();index++) {
				if (((ConstructorComboItem)comboConstructor.getItemAt(index)).theSame(agentInfo.getParameterValues())) {
					break;
				}
			}
			if (comboConstructor.getItemCount() > index) {
				comboConstructor.setSelectedIndex(index);
			} else {
				comboConstructor.setSelectedIndex(0);
			}
			
			this.packConstructorParams(agentInfo.getParameterValues());

		}
	}

	public FunctionPlacementAgentInfo getAgentInfo() {
		FunctionPlacementAgentInfo agentInfo = new FunctionPlacementAgentInfo();
		
		if (comboClass.getSelectedIndex() == 0) return null;
		if (comboConstructor.getSelectedIndex() == 0) return null;
		
		ClassComboItem classItem = (ClassComboItem)comboClass.getSelectedItem();
		
		agentInfo.setClassName(classItem.getClassName());
		
		List parameters = new ArrayList();
		Component children[] = panelParams.getComponents();
		for (int i=0;i<children.length;i++) {
			if ((children[i] instanceof ParameterPanel) == false) {
				continue;
			}
			Object value = ((ParameterPanel)children[i]).getValue();
			if (value == null) return null;
			parameters.add(value);
		}
		agentInfo.setParameterValues(parameters);
		
		return agentInfo;
	}
	
	private void packConstructorCombo() {
		comboConstructor.removeAllItems();
		comboConstructor.addItem(new ConstructorComboItem(null));
		
		if (comboClass.getSelectedIndex() > 0) {
			ClassComboItem classItem = (ClassComboItem)comboClass.getSelectedItem();
			Constructor constructors[] = ReflectionUtil.getConstructors(classItem.agentClass);
			if (constructors!=null) {
				for (int i=0;i<constructors.length;i++) {
					comboConstructor.addItem(new ConstructorComboItem(constructors[i]));
				}
			}
		}
	}
	
	private void packConstructorParams() {
		packConstructorParams(new ArrayList());
	}
	
	private void packConstructorParams(List paramValueList) {
		panelParams.removeAll();
		
		if (comboConstructor.getSelectedIndex() > 0) {
			ConstructorComboItem coustructorItem = (ConstructorComboItem)comboConstructor.getSelectedItem();
			Parameter params[] = coustructorItem.constructor.getParameters();
			if (params.length > 0) {
				for (int i=0;i<params.length;i++) {
					panelParams.add(new ParameterPanel(params[i],paramValueList.size() > i ? paramValueList.get(i):"" ));
				}
			}
			panelParams.setVisible(params.length==0 ? false:true);
		} else {
			panelParams.setVisible(false);
		}

	}
	
	class ClassComboItem {
		Class agentClass;
		
		ClassComboItem(Class agentClass) {
			this.agentClass = agentClass;
		}
		
		public String getClassName() {
			return agentClass.getName();
		}
		
		public boolean theSame(String className) {
			if (agentClass == null) return false;
			
			return getClassName().equals(className);
		}
		
		public String toString() {
			if (agentClass == null) return "";
			return agentClass.getSimpleName();
		}
	}
	
	class ConstructorComboItem {
		Constructor constructor;
		
		ConstructorComboItem(Constructor constructor) {
			this.constructor = constructor;
		}
		
		public boolean theSame(List valueList) {
			if (constructor == null) return false;
			
			Parameter params[] = constructor.getParameters();
			if (valueList.size() != params.length) return false;

			if (params != null) {
				for (int i=0;i<params.length;i++) {
					String type = params[i].getType().getSimpleName();
					String valueType = valueList.get(i).getClass().getSimpleName();
					
					if (valueType.toLowerCase().contains(type.toLowerCase()) == false) {
						return false;
					}
				}
			}
			
			return true;
		}
		
		public String toString() {
			if (constructor == null) return "";
			
			StringBuffer sb = new StringBuffer();
			sb.append("( ");
			
			Parameter params[] = constructor.getParameters();
			if (params != null) {
				for (int i=0;i<params.length;i++) {
					String type = params[i].getType().getSimpleName();
					String name = params[i].getName();
					if(!params[i].isNamePresent()) {
						//System.err.println(name); TODO constructor parameters named arg0, arg1. compiler option change needed. 
					}
					sb.append(type).append(" ").append(name);
					if (i<params.length-1) {
						sb.append(",");
					}
				}
			}
			
			sb.append(" )");
			
			return sb.toString();
		}
		
	}

	class ParameterPanel extends JPanel {
		
		JLabel labName = new JLabel();
		JTextField jtfValue = new JTextField(10);
		Parameter param;
		
		ParameterPanel(Parameter param,Object paramValue) {
			this.param = param;
			
			//labName.setText(param.getType().getSimpleName());
			labName.setText(param.getName());
			jtfValue.setText(paramValue.toString());
			
			add(labName);
			add(jtfValue);
		}
		
		Object getValue() {
			String str = jtfValue.getText();
			
			Object value = null;
			if (str!=null && str.length()>0) {
				//value = param.getType().cast(str);
				String type = param.getType().getName().toLowerCase();
				if (type.contains("int")) {
					value = Integer.parseInt(str);
				} else if (type.contains("double")) {
					value = Double.parseDouble(str);
				} else if (type.contains("float")) {
					value = Float.parseFloat(str);
				} else if (type.contains("long")) {
					value = Long.parseLong(str);
				} else if (type.contains("string")) {
					value = str;
				}
			}
			return value;
		}
	}
	
	public static void main(String []argv) {
		JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(300, 200));

        f.getContentPane().add(new FunctionPlacementAgentInfoPanel());
		
        f.setSize(500,200);
		f.setVisible(true);
	}
}
