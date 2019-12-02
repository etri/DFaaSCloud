package org.faas.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.cloudbus.cloudsim.core.CloudSim;
import org.faas.AnalyticsEngine;
import org.faas.DFaaSConstants;
import org.faas.SimulationConfig;
import org.faas.SimulationManager;
import org.faas.db.DatabaseLogger;
import org.faas.gui.core.*;
import org.faas.gui.core.NodeGroupGui;
import org.faas.gui.dialog.AddEndDeviceGroup;
import org.faas.gui.dialog.AddFogDevice;
import org.faas.gui.dialog.AddLink;
import org.faas.gui.dialog.AddPhysicalEdge;
import org.faas.gui.dialog.AddPhysicalNode;
import org.faas.gui.dialog.DeleteElement;
import org.faas.gui.panel.DetailPanel;
import org.faas.gui.panel.FunctionProfileManagePanel;
import org.faas.gui.panel.GlobalSettingPanel;
import org.faas.gui.panel.MonitoringPanel;
import org.faas.gui.panel.AnalyticsEngineStatPanel;
import org.faas.gui.swing.JPanelConsole;
import org.faas.rmi.DFaaSSimulator;
import org.faas.rmi.SimulatorServer;
import org.faas.rmi.SimulatorServerIF;
import org.faas.stats.MonitoringData;
import org.faas.stats.collector.MonitoringDataCollector;
import org.faas.topology.Actuator;
import org.faas.topology.EndDeviceGroup;
import org.faas.topology.NetworkTopology;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.topology.NodeGroup;
import org.faas.topology.Sensor;
import org.faas.topology.test.NetworkTopologyTest;
import org.faas.utils.JVMUtil;
import org.faas.utils.JsonUtil;
import org.faas.utils.Logger;


public class DFaaSGui extends JFrame implements NetworkTopologyListener {
	
	private static final long serialVersionUID = -2238414769964738933L;
	
	public String currentJsonFile = "network_topology.json";
	
	private JPanel contentPane;
	
	/** Import file names */
	private String physicalTopologyFile = "";  //physical
	private String deploymentFile = "";        //virtual
	private String workloads_background = "";  //workload
	private String workloads = "";             //workload

	private JPanel panel;
	private JPanel graphPanel;
	
	private Graph physicalGraph;
	private GraphView physicalGraphView;
	
	private JButton btnRun;
	
	private String mode;  //'m':manual; 'i':import

	private DetailPanel detailPane = new DetailPanel();
	
	private JTabbedPane tabPane = new JTabbedPane();
	private GlobalSettingPanel globalSettingPanel;
	private FunctionProfileManagePanel functionProfileManagePanel;
	private MonitoringPanel monitoringPanel;
	private AnalyticsEngineStatPanel resultPanel;
	
	private JLabel statusBar = new JLabel("Status.");

	// CWE-500 : public -> private, add getMe()
	private static DFaaSGui me;
	
	public boolean useRmi = false; // run simulation in a separated process.
	
	public boolean isRunning = false;
	
	private Timer refreshTimer;

	public static DFaaSGui getMe() { return me; }
	public GlobalSettingPanel getGlobalSettingPanel() { return globalSettingPanel; }
	public FunctionProfileManagePanel getFunctionProfileManagePanel() { return functionProfileManagePanel; }
	public MonitoringPanel getMonitoringPanel() { return monitoringPanel; }
	public AnalyticsEngineStatPanel getResultPanel() { return resultPanel; }

	private void rmiMode() {
		// java argument : -Djava.security.policy=policy -Djava.rmi.server.hostname=localhost
		
		System.setProperty("java.rmi.server.hostname","localhost");
	    System.setProperty("java.security.policy","policy");
		
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "server";
            SimulatorServerIF engine = new SimulatorServer();
            SimulatorServerIF stub = (SimulatorServerIF) UnicastRemoteObject.exportObject(engine, 50053);
            Registry registry = LocateRegistry.createRegistry(50053);
            registry.rebind(name, stub);

            System.out.println(name+" ready.");
        } catch (Exception e) {
			Logger.error("DFaaSGui:rmiMode","Exception: " + e);
			// CWE-209
			//e.printStackTrace();

            JOptionPane.showMessageDialog(DFaaSGui.this, "DFaaSSim is already running.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
	}
	
	public DFaaSGui() {
		me = this;
		
		MonitoringDataCollector.useLocal();
		
		if (useRmi) {
			rmiMode();
		} else {
			DFaaSSimulator.createLocal();
		}
		
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 800));
        setLocationRelativeTo(null);
        //setResizable(false);
        
        setTitle("DFaaS Simulator");
        contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		
		tabPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		
		initUI();

		packTab();
		
		contentPane.add(tabPane,BorderLayout.CENTER);
		
		JPanel statusBarPanel = new JPanel(new BorderLayout());
		statusBarPanel.setBorder(BorderFactory.createEtchedBorder());
		statusBarPanel.add(statusBar,BorderLayout.CENTER);
		statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPane.add(statusBarPanel,BorderLayout.SOUTH);
		
		pack();
		setVisible(true);
	}
	
	public void packTab() {
		tabPane.removeAll();
		JScrollPane scrollPane = new JScrollPane();
		globalSettingPanel = new GlobalSettingPanel(this);
		scrollPane.getViewport().add(globalSettingPanel);

		tabPane.add("Simulation Config", scrollPane);
		tabPane.add("Function profiles",functionProfileManagePanel = new FunctionProfileManagePanel(this));

		tabPane.add("Network topology",initGraph());

		monitoringPanel = new MonitoringPanel();
		MonitoringData.setMonitoringPanel(monitoringPanel);
		JScrollPane jspMonitoringPanel = new JScrollPane();
		jspMonitoringPanel.getViewport().add(monitoringPanel);

		tabPane.add("Monitoring",jspMonitoringPanel);

		resultPanel = new AnalyticsEngineStatPanel();
		JScrollPane jspResultPanel = new JScrollPane();
		jspResultPanel.getViewport().add(resultPanel);

		tabPane.add("Function Profile Stats",jspResultPanel);
		
		
		tabPane.add("Console",initConsolePanel());
		
	}
	
	public void showStatusMessage(String message) {
		statusBar.setText(message);
		
		ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	statusBar.setText("Status.");
            }
        };
        Timer timer = new Timer(3000 ,taskPerformer);
        timer.setRepeats(false);
        timer.start();
	}
	
	public NetworkTopology getNetworkTopology() {
		return topology;
	}
	
	private JPanelConsole console;
	private JPanel initConsolePanel() {
		
		JPanel p = new JPanel(new BorderLayout());
		
		JPanel topPanel = new JPanel();
		
		JButton butClear = new JButton("Clear logs");
		butClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				console.clear();
			}
		});
		topPanel.add(butClear);
		
		p.add(topPanel,BorderLayout.NORTH);
		p.add(console=new JPanelConsole(500),BorderLayout.CENTER);
		
		return p;
	}
	
	public final void initUI() {
		//setUIFont (new javax.swing.plaf.FontUIResource("Serif",Font.BOLD,18));

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        graphPanel = new JPanel(new java.awt.GridLayout(1, 2));
        
		initBar();
		doPosition();
	}
	
	/** position window */
	private void doPosition() {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;

		int x = (width / 2 - 1280 / 2);
		int y = (height / 2 - 800 / 2);
		// One could use the dimension of the frame. But when doing so, one have to call this method !BETWEEN! pack and
		// setVisible. Otherwise the calculation will go wrong.

		this.setLocation(x, y);
	}
	
	/** Initialize project menu and tool bar */
    private final void initBar() {
    	//---------- Start ActionListener ----------
    	ActionListener readPhyTopoListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	physicalTopologyFile = importFile("josn");
            	checkImportStatus();
		    }
		};
		ActionListener readVirTopoListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	deploymentFile = importFile("json");
            	checkImportStatus();
		    }
		};
		ActionListener readWorkloadBkListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	workloads_background = importFile("cvs");
		    	checkImportStatus();
		    }
		};
		ActionListener readWorkloadListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	workloads = importFile("cvs");
		    	checkImportStatus();
		    }
		};
		
		ActionListener addFogDeviceListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openAddFogDeviceDialog();
		    }
		};
		
		ActionListener addEndDeviceListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openAddEndDeviceDialog();
		    }
		};
		
		ActionListener addPhysicalNodeListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openAddPhysicalNodeDialog();
		    }
		};

		ActionListener addPhysicalEdgeListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openAddPhysicalEdgeDialog();
		    }
		};
		
		ActionListener addLinkListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openAddLinkDialog();
		    }
		};
		
		ActionListener deleteElementListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteElementDialog();
			}
		};
		
		ActionListener addActuatorListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openAddActuatorDialog();
		    }
		};
		
		ActionListener addSensorListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openAddSensorDialog();
		    }
		};
		
		ActionListener importPhyTopoListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	String fileName = importFile("json");
		    }
		};
		
		ActionListener newPhyTopoListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	currentJsonFile = null;
		    	topology = NetworkTopologyTest.createEmptyNetworkTopology();
		    	//topology.setFunctionProfileList(FunctionProfileList.getInstance().getFunctionProfileList());
		    	NetworkTopologyHelper.create(topology);
		    	
				//setTitle("DFaaS Topology Creator - ");
		    	physicalGraph = packGraph();
		    	physicalGraphView.setGraph(physicalGraph);
		    	physicalGraphView.setNetworkTopology(topology);
		    	physicalGraphView.repaint();
		    }
		};
		
		ActionListener savePhyTopoListener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	saveNetworkTopology();
		    }
		};
		
		ActionListener startSimulationListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				if (isRunning) return;
				
				isRunning = true;
				
				int delay = (int)(SimulationConfig.getInstance().getFuncProfileStatsDisplayInterval()*1000); // milliseconds
				ActionListener taskPerformer = new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						// ...Perform a task...
						MonitoringData.refresh2();
						resultPanel.show(DFaaSSimulator.getFunctionStats());
					}
				};

				MonitoringData.clear();
				resultPanel.clear();
				monitoringPanel.clear();
				MonitoringDataCollector.getInstance().init();

				tabPane.setSelectedIndex(3);

				if (useRmi) {

//					resultPanel.clear();
//					monitoringPanel.clear();
//					MonitoringDataCollector.getInstance().init();

					try {
						JVMUtil.exec(org.faas.gui.DFaaSMain.class, SimulationManager.getConfigPath());

					} catch (Exception ee) {
						ee.printStackTrace();
					}

				} else {
					Runnable r = new Runnable() {
						public void run() {
							
							//Log.enable(); // cloudSim logger
							Logger.enable(); // fogSim logger
							//Logger.disable();
							Logger.setLogLevel(Logger.ERROR);
							
							int num_user = 1; // number of cloud users
							Calendar calendar = Calendar.getInstance();
							boolean trace_flag = false; // mean trace events

							if (SimulationConfig.getInstance().getSimulationRunningDuration() <=0) {
								SimulationManager.setSimulationDuration(Double.MAX_VALUE);
							} else {
								SimulationManager.setSimulationDuration(SimulationConfig.getInstance().getSimulationRunningDuration());
							}
							
							CloudSim.init(num_user, calendar, trace_flag);

//							resultPanel.clear();
//							monitoringPanel.clear();
//							MonitoringDataCollector.getInstance().init();
							
							SimulationManager.startSimulation(topology);

							resultPanel.show(AnalyticsEngine.getInstance().getFunctionStats());
							isRunning = false;
							
							refreshTimer.stop();
							MonitoringData.refresh2();
						}
					};

					Thread t = new Thread(r);
					//t.setPriority(Thread.MIN_PRIORITY);
					t.start();					
				}
				
				if (refreshTimer != null) refreshTimer.stop(); 
				
				refreshTimer = new Timer(2000, taskPerformer);
				refreshTimer.setInitialDelay(delay);
				refreshTimer.start(); 

			}
		};
		
		ActionListener stopSimulationListener = new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		System.out.println("STOP requested!");
        		
        		isRunning = false;
        		
				tabPane.setSelectedIndex(3);
				
				DFaaSSimulator.stop();
				DFaaSSimulator.shutDown();

				if (refreshTimer != null) refreshTimer.stop();
				MonitoringData.refresh2();

        	}
        };
				
		//---------- End ActionListener ----------
    	
        //---------- Start Creating project tool bar ----------
        JToolBar toolbar = new JToolBar();

        ImageIcon iDeleteElement = new ImageIcon(
                getClass().getResource("/images/deleteElement.png"));

        ImageIcon iEndDeviceGroup = new ImageIcon(
                getClass().getResource("/images/endDeviceGroup.png"));

        ImageIcon iSensor = new ImageIcon(
                getClass().getResource("/images/sensor.png"));
        ImageIcon iActuator = new ImageIcon(
                getClass().getResource("/images/actuator.png"));
        ImageIcon iFogDevice = new ImageIcon(
                getClass().getResource("/images/dc.png"));
        ImageIcon iLink = new ImageIcon(
                getClass().getResource("/images/hline2.png"));
        ImageIcon iHOpen = new ImageIcon(
                getClass().getResource("/images/openPhyTop.png"));
        ImageIcon iHSave = new ImageIcon(
                getClass().getResource("/images/savePhyTop.png"));
        
        ImageIcon run = new ImageIcon(
                getClass().getResource("/images/play.png"));
        ImageIcon exit = new ImageIcon(
                getClass().getResource("/images/exit.png"));

        ImageIcon iStop = new ImageIcon(getClass().getResource("/images/stop.png"));

        ImageIcon iNew = new ImageIcon(getClass().getResource("/images/new.png"));

        JButton btnNewNetworkTopology = new JButton(iNew);
        btnNewNetworkTopology.setToolTipText("New Network Topology");
        
        final JButton btnSensor = new JButton(iSensor);
        btnSensor.setToolTipText("Add EventSource");
        final JButton btnActuator = new JButton(iActuator);
        btnActuator.setToolTipText("Add EventSink");
        
        final JButton btnFogDevice = new JButton(iFogDevice);
        btnFogDevice.setToolTipText("Add Fog Device");
        JButton btnEndDeviceGroup = new JButton(iEndDeviceGroup);
        btnEndDeviceGroup.setToolTipText("End Device Group");
        final JButton btnLink = new JButton(iLink);
        btnLink.setToolTipText("Add Link");
        final JButton btnDeleteNode = new JButton(iDeleteElement);
        btnDeleteNode.setToolTipText("Delete Element");
        
        final JButton btnHopen = new JButton(iHOpen);
        btnHopen.setToolTipText("Open Physical Topology");
        final JButton btnHsave = new JButton(iHSave);
        btnHsave.setToolTipText("Save Physical Topology");
        
        btnRun = new JButton(run);
        btnRun.setToolTipText("Start simulation");
        JButton btnExit = new JButton(exit);
        btnExit.setToolTipText("Exit CloudSim");
        
        JButton btnStop = new JButton(iStop);
        btnStop.setToolTipText("Stop Simulation");
        
        toolbar.setAlignmentX(0);
        
        btnSensor.addActionListener(addSensorListener);
        btnActuator.addActionListener(addActuatorListener);
        btnFogDevice.addActionListener(addFogDeviceListener);
        btnEndDeviceGroup.addActionListener(addEndDeviceListener);
        btnLink.addActionListener(addLinkListener);
        btnDeleteNode.addActionListener(deleteElementListener);
        
        btnNewNetworkTopology.addActionListener(newPhyTopoListener);
        btnHopen.addActionListener(importPhyTopoListener);
        btnHsave.addActionListener(savePhyTopoListener);
        
        btnStop.addActionListener(stopSimulationListener);
        
        btnRun.addActionListener(startSimulationListener);
        
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	if (SimulationConfig.getInstance().getDbLogging()) {
            		DatabaseLogger.getInstance().close();
            	}
				DFaaSSimulator.shutDown();

            	System.exit(0);
            }

        });       
        
        toolbar.add(btnEndDeviceGroup);
        //toolbar.add(btnSensor);
        //toolbar.add(btnActuator);
        toolbar.add(btnFogDevice);
        toolbar.add(btnLink);
        toolbar.add(btnDeleteNode);
        
        toolbar.addSeparator();

        toolbar.add(btnNewNetworkTopology);
        toolbar.add(btnHopen);
        toolbar.add(btnHsave);

        /*toolbar.add(btnSensorModule);
        toolbar.add(btnActuatorModule);
        toolbar.add(btnModule);
        toolbar.add(btnAppEdge);*/
        
        toolbar.addSeparator();
        
        toolbar.add(btnRun);
        toolbar.add(btnStop);

        toolbar.addSeparator();
        
        toolbar.add(btnExit);

        panel.add(toolbar);
        
        contentPane.add(panel, BorderLayout.NORTH);
        //---------- End Creating project tool bar ----------
        
        
        
    	//---------- Start Creating project menu bar ----------
    	//1-1
        JMenuBar menubar = new JMenuBar();
        //ImageIcon iconNew = new ImageIcon(getClass().getResource("/src/new.png"));

        //2-1
        JMenu graph = new JMenu("Graph");
        graph.setEnabled(false);
        graph.setMnemonic(KeyEvent.VK_G);
        
        //Graph by importing json and cvs files
        final JMenuItem MiPhy = new JMenuItem("Physical Topology");
        final JMenuItem MiVir = new JMenuItem("Virtual Topology");
        final JMenuItem MiWl1 = new JMenuItem("Workload Background");
        final JMenuItem MiWl2 = new JMenuItem("Workload");
        //Graph drawing elements
        final JMenu MuPhy = new JMenu("Physical");
        JMenuItem MiFogDevice = new JMenuItem("Add Fog Device");
        JMenuItem MiPhyEdge = new JMenuItem("Add Edge");
        JMenuItem MiPhyOpen = new JMenuItem("Import Physical Topology");
        JMenuItem MiPhySave = new JMenuItem("Save Physical Topology");
        MuPhy.add(MiFogDevice);
        MuPhy.add(MiPhyEdge);
        MuPhy.add(MiPhyOpen);
        MuPhy.add(MiPhySave);
        
        MiPhy.addActionListener(readPhyTopoListener);
        MiVir.addActionListener(readVirTopoListener);
        MiWl1.addActionListener(readWorkloadBkListener);
        MiWl2.addActionListener(readWorkloadListener);
             
        MiFogDevice.addActionListener(addFogDeviceListener);
        MiPhyEdge.addActionListener(addPhysicalEdgeListener);
        MiPhyOpen.addActionListener(importPhyTopoListener);
        MiPhySave.addActionListener(savePhyTopoListener);

        graph.add(MuPhy);
        //graph.add(MuVir);
        graph.add(MiPhy);
        //graph.add(MiVir);
        graph.add(MiWl1);
        graph.add(MiWl2);

        //2-2
        JMenu view = new JMenu("View");
        view.setEnabled(false);
        view.setMnemonic(KeyEvent.VK_F);
        
        //switch mode between manual mode (to create graph by hand) and import mode (to create graph from file)
		ActionListener actionSwitcher = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        try {
		    	    String cmd = e.getActionCommand();
		    	    if("Canvas" == cmd){
		    	    	btnSensor.setVisible(true);
		    	    	btnActuator.setVisible(true);
		    	    	btnFogDevice.setVisible(true);
		    	    	btnLink.setVisible(true);
		    	    	btnHopen.setVisible(true);
		    	    	btnHsave.setVisible(true);
		    	    	
		    	    	MiPhy.setVisible(false);
		    	    	MiVir.setVisible(false);
		    	    	MiWl1.setVisible(false);
		    	    	MiWl2.setVisible(false);
		    	    	MuPhy.setVisible(true);
		    	    	//MuVir.setVisible(true);
		    	    	
		    	    	btnRun.setVisible(false);
		    	    	btnRun.setEnabled(false);
		    	    	
		    	    	mode = "m";
		    	    	
		    	    }else if("Execution" == cmd){
		    	    	btnSensor.setVisible(false);
		    	    	btnActuator.setVisible(false);
		    	    	btnFogDevice.setVisible(false);
		    	    	btnLink.setVisible(false);
		    	    	btnHopen.setVisible(false);
		    	    	btnHsave.setVisible(false);
		    	    	
		    	    	MiPhy.setVisible(true);
		    	    	MiVir.setVisible(true);
		    	    	MiWl1.setVisible(true);
		    	    	MiWl2.setVisible(true);
		    	    	MuPhy.setVisible(false);
		    	    	//MuVir.setVisible(false);
		    	    	
		    	    	btnRun.setVisible(true);
		    	    	btnRun.setEnabled(false);
		    	    	
		    	    	mode = "i";
		    	    }
		    	    //System.out.println(e.getActionCommand());
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		};
        JRadioButtonMenuItem manualMode = new JRadioButtonMenuItem("Canvas");
        manualMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        manualMode.addActionListener(actionSwitcher);
        JRadioButtonMenuItem importMode = new JRadioButtonMenuItem("Execution");
        importMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        importMode.addActionListener(actionSwitcher);
        ButtonGroup group = new ButtonGroup();
        group.add(manualMode);
        group.add(importMode);
        
        JMenuItem fileExit = new JMenuItem("Exit");
        fileExit.setMnemonic(KeyEvent.VK_C);
        fileExit.setToolTipText("Exit CloudSim");
        fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
            ActionEvent.CTRL_MASK));

        fileExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }

        });

        view.add(manualMode);
        view.add(importMode);
        view.addSeparator();
        view.add(fileExit);        

        
        //3-1
        menubar.add(view);
        menubar.add(graph);

        //4-1
        setJMenuBar(menubar);
        //----- End Creating project menu bar -----
        
        
        
        //----- Start Initialize menu and tool bar -----
        manualMode.setSelected(true);
        mode = "m";
        
        //btnHost.setVisible(true);
        btnSensor.setVisible(true);
        btnActuator.setVisible(true);
        btnFogDevice.setVisible(true);
        btnLink.setVisible(true);
    	btnHopen.setVisible(true);
    	btnHsave.setVisible(true);
    	
    	MiPhy.setVisible(false);
    	MiVir.setVisible(false);
    	MiWl1.setVisible(false);
    	MiWl2.setVisible(false);
    	MuPhy.setVisible(true);
    	//MuVir.setVisible(true);
    	
//    	btnRun.setVisible(false);
//    	btnRun.setEnabled(false);
        //----- End Initialize menu and tool bar -----

    }
    
	protected void openAddLinkDialog() {
		int sourceNodeId = -1;
		int destNodeId = -1;
		
		if (physicalGraphView.getHistory().getNodes().size()==1) {
			sourceNodeId = physicalGraphView.getHistory().getNodes().get(0).getId();
		} else if (physicalGraphView.getHistory().getNodes().size()>=2) {
			sourceNodeId = physicalGraphView.getHistory().getNodes().get(0).getId();
			destNodeId = physicalGraphView.getHistory().getNodes().get(1).getId();
		}
		
		AddLink phyEdge = new AddLink(physicalGraph, DFaaSGui.this, this.topology, sourceNodeId, destNodeId);
    	physicalGraphView.repaint();
		
	}
	
	protected void deleteElementDialog() {
		new DeleteElement(this, this.physicalGraphView);
	}

	protected void openAddFogDeviceDialog() {
		AddFogDevice fogDevice = new AddFogDevice(physicalGraph, DFaaSGui.this,topology);
    	physicalGraphView.repaint();
		
	}
	
	protected void openAddEndDeviceDialog() {
		new AddEndDeviceGroup(physicalGraph, DFaaSGui.this,topology);
	}
	
	public void saveNetworkTopology() {
    	try {
    		if (topology.validate() == false) {
    			String err = topology.getValidateResult();
    			JOptionPane.showMessageDialog(DFaaSGui.this, err, "Error", JOptionPane.ERROR_MESSAGE);
    			return;
    		}
    		
	    	if (currentJsonFile == null) {
	    		if (saveFile("json")) {
	    			showStatusMessage("Network topology "+currentJsonFile+" saved");
	    		}
	    	} else {
	    		if (JsonUtil.write(topology, SimulationManager.getConfigPath()+"/"+currentJsonFile)) {
	    			showStatusMessage("Network topology "+currentJsonFile+" saved");
	    		}
	    	}
		} catch (IOException e1) {
			Logger.error("DFaaSGui:saveNetworkTopology","Exception: " + e1);
			// CWE-209
			//e1.printStackTrace();
		}

	}
	
    
    /** dialog opening */
    private void openAddPhysicalNodeDialog(){
    	AddPhysicalNode phyNode = new AddPhysicalNode(physicalGraph, DFaaSGui.this);
    	physicalGraphView.repaint();
    }
    private void openAddPhysicalEdgeDialog(){
    	AddPhysicalEdge phyEdge = new AddPhysicalEdge(physicalGraph, DFaaSGui.this);
    	physicalGraphView.repaint();
    }

	protected void openAddActuatorDialog() {
    	if (physicalGraphView.getGraphCanvas().getSelectedEndDeviceNode() == null) {
    		// TODO alert message here
    	} else {
			//AddActuator actuator = new AddActuator(physicalGraph, DFaaSGui.this,topology,physicalGraphView.getGraphCanvas().getSelectedEndDeviceNode());
			physicalGraphView.repaint();
    	}
    }

    protected void openAddSensorDialog() {
    	if (physicalGraphView.getGraphCanvas().getSelectedEndDeviceNode() == null) {
    		// TODO alert message here
    	} else {
    		//AddSensor sensor = new AddSensor(physicalGraph, DFaaSGui.this,topology,physicalGraphView.getGraphCanvas().getSelectedEndDeviceNode());
    		physicalGraphView.repaint();
    	}
	}
    

	
	private NetworkTopology topology;
	
	private Graph packGraph() {
		Graph graph = new Graph();

		List<org.faas.topology.Link> linkList = topology.getLinkList();
		Map<Integer,Node> nodeMap = new HashMap<Integer,Node>();
		List<NodeGroup> nodeList = topology.getNodeList();
		for (int i=0;i<nodeList.size();i++) {
			int nodeGroupType = nodeList.get(i).getType();
			
			NodeGroupGui node = new NodeGroupGui();
			node.setName(DFaaSConstants.getEntityName(nodeList.get(i).getType())+"-"+nodeList.get(i).getId());
			if (nodeGroupType == DFaaSConstants.CORE_NODE_GROUP) {
				node.setLevel(1);
			} else if (nodeGroupType == DFaaSConstants.EDGE_NODE_GROUP) {
				node.setLevel(2);
			} else if (nodeGroupType == DFaaSConstants.FOG_NODE_GROUP) {
				node.setLevel(3);
			}
			node.setData(nodeList.get(i));
			node.setType("FOG_DEVICE");
			
			graph.addNode(node);
			
			nodeMap.put(nodeList.get(i).getId(), node);
		}
		
		List<EndDeviceGroup> endDeviceList = topology.getEndDeviceList();
		for (int i=0;i<endDeviceList.size();i++) {
			NodeGroupGui node = new NodeGroupGui();
			node.setName("END_DEVICE-"+endDeviceList.get(i).getId());
			node.setLevel(4);
			node.setData(endDeviceList.get(i));
			node.setType("END_DEVICE");

			graph.addNode(node);
			
			EndDeviceGroup endDeviceGroup = endDeviceList.get(i);
			nodeMap.put(endDeviceGroup.getId(), node);

			// TODO sensor, actuator 가 각 2개 이상일때 쌍으로 표시 되도록 변경 필요.
			// -
			List sensorActuatorList = new ArrayList();
			sensorActuatorList.addAll(endDeviceGroup.getSensorList());
			sensorActuatorList.addAll(endDeviceGroup.getActuatorList());
			for (int j=0;j<sensorActuatorList.size();j++) {

				NodeGroupGui child;

				if (sensorActuatorList.get(j) instanceof Sensor) {
					child = new NodeGroupGui();
					child.setData(sensorActuatorList.get(j));
					child.setName("SENSOR-"+((Sensor)sensorActuatorList.get(j)).getId());
					child.setType("SENSOR");
					node.addChild(child);
					graph.addNode(child);
				} else if (sensorActuatorList.get(j) instanceof Actuator) {
					child = new NodeGroupGui();
					child.setData(sensorActuatorList.get(j));
					child.setName("ACTUATOR-"+((Actuator)sensorActuatorList.get(j)).getId());
					child.setType("ACTUATOR");
					node.addChild(child);
					graph.addNode(child);
				}

			}

		}
		
		for (int i=0;i<linkList.size();i++) {
			Edge edge = new Edge(nodeMap.get(linkList.get(i).getSourceId()),nodeMap.get(linkList.get(i).getDestId()));
			edge.setLink(linkList.get(i));
			graph.addEdge(nodeMap.get(linkList.get(i).getSourceId()),edge);
		}

		
		return graph;
	}
	

	private Graph loadGraph() {
		
//		String recentFile = SimulationConfig.getInstance().getRecentNetworkTopologyFile();
//		if (recentFile != null) {
//			return loadGraph(SimulationConfig.getInstance().getConfigPath(),recentFile);
//		}
		
		return loadGraph(SimulationManager.getConfigPath(),"network_topology.json");
	}
	
	private Graph loadGraph(String path,String fileName) {
		
		//super.setTitle("DFaaS Topology Creator - "+fileName);
		topology = NetworkTopology.load(new File(path,fileName));
		//topology.setFunctionProfileList(FunctionProfileList.getInstance().getFunctionProfileList());
		NetworkTopologyHelper.create(topology);
		
		currentJsonFile = fileName;
        SimulationConfig.getInstance().setRecentNetworkTopologyFile(currentJsonFile);

		return packGraph();
	}
	
	public void networkTopologyChanged() {
    	physicalGraph = packGraph();
    	physicalGraphView.setGraph(physicalGraph);
    	physicalGraphView.setNetworkTopology(topology);
    	physicalGraphView.repaint();
	}

	/** initialize Canvas */
    private JComponent initGraph(){
    	
    	graphPanel.removeAll();
    	
    	physicalGraph = new Graph();
    	//virtualGraph = new Graph();
    	
    	// test code
    	//physicalGraph = Bridge.jsonToGraph("/Users/javajune/work.personal/iFogSim-master/topologies/vr_game_topo", 0);
    	physicalGraph = loadGraph();
    	
    	physicalGraphView = new GraphView(physicalGraph);
		physicalGraphView.setNetworkTopology(topology);

		physicalGraphView.setGraphCanvasListener(new GraphCanvasListener() {

			@Override
			public void nodeSelected(Node node) {
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Object data = node.getData();
						if (data instanceof NodeGroup) {
							detailPane.show((NodeGroup)data);
						} else if (data instanceof EndDeviceGroup) {
							detailPane.show(physicalGraph,DFaaSGui.this,topology,(EndDeviceGroup)data);
						} else if (data instanceof Sensor) {
							//detailPane.show(topology, (Sensor)data);
						} else if (data instanceof Actuator) {
							//detailPane.show(topology, (Actuator)data);
						}
					}
				});
			}

			@Override
			public void linkSelected(Edge link) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						detailPane.show(link.getLinkData());
					}
				});
			}
    	
    	});
    	
		graphPanel.add(physicalGraphView);
		
		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jsp.setDividerLocation(900);

		JScrollPane scroll1 = new JScrollPane();
		scroll1.getViewport().add(graphPanel);
		
		//graph.setPreferredSize(new Dimension(1024,(int)graph.getSize().getHeight()));
		jsp.setLeftComponent(scroll1);
		jsp.setRightComponent(detailPane);
		
		return jsp;
    }
    
    /** common utility */
    /** load network topology **/
    private String importFile(String type){
    	JFileChooser fileopen = new JFileChooser(System.getProperty("user.dir"));
        //JFileChooser fileopen = new JFileChooser(SimulationConfig.getInstance().getWorkingDir());
        FileFilter filter = new FileNameExtensionFilter(type.toUpperCase()+" Files", type);
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(panel, "Import file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();

            importNetworkTopology(SimulationManager.getConfigPath(),file.getName());
        }
        return "";
    }
    
    public void importNetworkTopology(String path, String fileName) {
        physicalGraph = loadGraph(path,fileName);
        
    	physicalGraphView.setGraph(physicalGraph);
    	physicalGraphView.repaint();
    	
    }
    
    /** save network topology */
    private boolean saveFile(String type) throws IOException{
    	JFileChooser fileopen = new JFileChooser(System.getProperty("user.dir"));
    	//JFileChooser fileopen = new JFileChooser(SimulationConfig.getInstance().getWorkingDir());
        FileFilter filter = new FileNameExtensionFilter(type.toUpperCase()+" Files", type);
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showSaveDialog(panel);

        if (ret == JFileChooser.APPROVE_OPTION) {
            String name = fileopen.getSelectedFile().getAbsolutePath();

            if (name.endsWith("json") || name.endsWith("JSON")) {
            	currentJsonFile = name;
            } else {
            	currentJsonFile = name+".json";
            }

            //super.setTitle("DFaaS Topology Creator - "+currentJsonFile);
            
            SimulationConfig.getInstance().setRecentNetworkTopologyFile(currentJsonFile);
            
            return JsonUtil.write(topology, currentJsonFile);
        }
        
        return false;
    }
    
    private static void setUIFont(javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
          Object key = keys.nextElement();
          Object value = UIManager.get (key);
          if (value != null && value instanceof javax.swing.plaf.FontUIResource)
            UIManager.put (key, f);
          }
    }
    
    private void checkImportStatus(){
    	if((physicalTopologyFile!=null && !physicalTopologyFile.isEmpty()) &&
    	   (deploymentFile!=null && !deploymentFile.isEmpty()) &&
           (workloads_background!=null && !workloads_background.isEmpty()) &&
    	   (workloads!=null && !workloads.isEmpty())){
    		btnRun.setEnabled(true);
    	}else{
    		btnRun.setEnabled(false);
    	}
    }
    
    
    
    /** Application entry point */
	public static void main(String args[]) throws InterruptedException {
		
		if (args.length > 0) {
			// CWE-99 : add pathParam code, CWE-22 ??
			String pathParam  = args[0];
			if (pathParam.length() > 0) {
				File configPath = new File(pathParam);
				if (configPath.exists() && configPath.isDirectory()) {
					SimulationManager.setConfigPath(pathParam);
				}
			}
		}
		
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	DFaaSGui sdn = new DFaaSGui();
                sdn.setVisible(true);
            }
        });
	}
}
