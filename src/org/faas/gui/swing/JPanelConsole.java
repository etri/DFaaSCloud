package org.faas.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class JPanelConsole extends JPanel {
    public static final long serialVersionUID = 21362469L;

    private JTextArea textFailCount;
    private JTextPane textPane;
    private PipedOutputStream pipeOut;
    private PipedInputStream pipeIn;


    public JPanelConsole() {
    	this(-1);
    }
    
    public JPanelConsole(int maxLines) {
        super(new BorderLayout());
        
        textFailCount = new JTextArea();
        JScrollPane jsp = new JScrollPane();
        jsp.getViewport().add(textFailCount);
        this.add(jsp, BorderLayout.NORTH);
        
        textPane = new JTextPane();
        textPane.setFont(new Font("Serif", Font.PLAIN, 15));
        jsp = new JScrollPane();
        jsp.getViewport().add(textPane);
        this.add(jsp, BorderLayout.CENTER);

        redirectSystemStreams();

        textPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        if (maxLines > 0) {
        	textPane.getDocument().addDocumentListener(
        		    new LimitLinesDocumentListener(maxLines) );
        }
        
        for (int i=0;i<failedKeys.length;i++) {
        	failedMap.put(failedKeys[i], 0);
        }
    }

    public void clear() {
    	textFailCount.setText("");
    	textPane.setText("");
    	failedMap.clear();
    }
    
    private void increaseFailCount(String key) {
    	int count = 0;
    	if (failedMap.get(key)==null) {
    		count = 1;
    	} else {
    		count = failedMap.get(key) + 1;
    	}
    	failedMap.put(key, count);
    }

    private Map<String,Integer> failedMap = new HashMap<String,Integer>();
    private static String failedKeys[] = new String[] {"failed by MIPS","failed by storage","failed by RAM","failed by BW"};
    private void updateTextPane(final String text) {
    	
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Document doc = textPane.getDocument();
                try {
                	boolean add = false;
                	if (text.contains(failedKeys[0])) {
                		increaseFailCount(failedKeys[0]);
                	} else if (text.contains(failedKeys[1])) {
                		increaseFailCount(failedKeys[1]);
                	} else if (text.contains(failedKeys[2])) {
                		increaseFailCount(failedKeys[2]);
                	} else if (text.contains(failedKeys[3])) {
                		increaseFailCount(failedKeys[3]);
                	} else {
                		add =true;
                	}
                	if (add) {
                        doc.insertString(doc.getLength(), text, null);
                	} else {
                		Iterator<String> ite = failedMap.keySet().iterator();
            			StringBuffer sb = new StringBuffer();
                		while (ite.hasNext()) {
                			String key = ite.next();
                			sb.append(key).append(":").append(failedMap.get(key)).append("\n");
                		}
                		textFailCount.setText(sb.toString());
                	}
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
                textPane.setCaretPosition(doc.getLength() - 1);
            }
        });
    }


    private void redirectSystemStreams() {
      OutputStream out = new OutputStream() {
        @Override
        public void write(final int b) throws IOException {
          updateTextPane(String.valueOf((char) b));
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
          updateTextPane(new String(b, off, len));
        }

        @Override
        public void write(byte[] b) throws IOException {
          write(b, 0, b.length);
        }
      };

      System.setOut(new PrintStream(out, true));
      System.setErr(new PrintStream(out, true));
    }


}