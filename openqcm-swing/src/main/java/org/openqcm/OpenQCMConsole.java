/**
 * 
 * This file is part of openQCM.
 * 
 * openQCM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * openQCM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with openQCM.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.openqcm;

import static java.util.logging.Level.SEVERE;
import static org.openqcm.biobright.PublishingInfo.Type.FREQUENCY;
import static org.openqcm.biobright.PublishingInfo.Type.TEMPERATURE;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.ardulink.core.AbstractListenerLink;
import org.ardulink.core.events.CustomEvent;
import org.ardulink.core.events.CustomListener;
import org.ardulink.legacy.Link;
import org.openqcm.ardulink.ArdulinkConnectionDialog;
import org.openqcm.biobright.BiobrightClient;
import org.openqcm.biobright.BiobrightConnectionDialog;
import org.openqcm.biobright.ConnectionInfo;
import org.openqcm.biobright.PublishingInfo;

public class OpenQCMConsole extends JFrame implements CustomListener {

	private static final long serialVersionUID = 7156718994340002449L;

	// Store file for data recording
    File sf;
    // Write file for data recording
    private FileWriter fw;
    // size of circular buffer
    private final int bufferSize = 10;
    // frequency circular buffer for eliminating signal glitches using median
    private ArrayCircularBuffer<Double> bufferFrequency = new ArrayCircularBuffer<Double>(bufferSize/2);
    // frequency circular buffer for averaging frequency data
    private ArrayCircularBuffer<Integer> bufferFrequencyTemp = new ArrayCircularBuffer<Integer>(bufferSize);
    // temperauture circular buffer for averaging temperature data
    private ArrayCircularBuffer<Integer> bufferTemperature = new ArrayCircularBuffer<Integer>(bufferSize);
    // temperature circular buffer for smoothing data
    // ArrayCircularBuffer bufferTemperatureTemp = new ArrayCircularBuffer(bufferSize/2);
    // nominal quartz crystal frequency
    private int nominalFrequency = 6000000;

    // Arduino half timer clock
    private static final int ALIAS = 8000000;
    
    private Link link;
 
    private static Logger logger = Logger.getLogger(OpenQCMConsole.class.getName());
	
    private ChartDynamicData chartData;
	
	private JPanel contentPane;
    private JComboBox<String> frequencyBox;
    private JButton clearChartBtn;
    private JFormattedTextField frequencyCurrent;
    private JLabel clearChartLabel;
    private JLabel temperatureChartLabel;
    private JLabel frequencyLabel;
    private JLabel temperatureLabel;
    private JLabel quartzFreqLabel;
    private JPanel jPanelBottom;
    private JPanel jPanelChart;
    private JLabel logoImage;
    private JToggleButton saveFileBtn;
    private JToggleButton showTemperatureBtn;
    private JToggleButton connectBtn;
    private JFormattedTextField temperatureCurrent;
    private JTextField qcmDataChartTextField;
    private JToggleButton biobrightToggleButton;

    private BiobrightClient biobrightClient; 
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
        	logger.log(SEVERE, null, ex);
        }
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OpenQCMConsole frame = new OpenQCMConsole();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public OpenQCMConsole() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
        jPanelBottom = new JPanel();
        saveFileBtn = new JToggleButton();
        connectBtn = new JToggleButton();
        qcmDataChartTextField = new JTextField();
        jPanelChart = new JPanel();
        chartData = new ChartDynamicData();
        logoImage = new JLabel();
        clearChartBtn = new JButton();
        showTemperatureBtn = new JToggleButton();
        frequencyCurrent = new JFormattedTextField();
        temperatureCurrent = new JFormattedTextField();
        clearChartLabel = new JLabel();
        temperatureChartLabel = new JLabel();
        frequencyLabel = new JLabel();
        temperatureLabel = new JLabel();
        frequencyBox = new JComboBox<String>();
        quartzFreqLabel = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("openQCM 1.2");
        setIconImages(null);
        setMinimumSize(new java.awt.Dimension(720, 500));
        setName("applicationFrame"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanelBottom.setMinimumSize(new java.awt.Dimension(341, 52));

        saveFileBtn.setText("Save File");
        saveFileBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileBtnActionPerformed(evt);
            }
        });

        connectBtn.setText("Connect");
        connectBtn.setMaximumSize(new java.awt.Dimension(60, 32));
        connectBtn.setMinimumSize(new java.awt.Dimension(60, 32));
        connectBtn.setPreferredSize(new java.awt.Dimension(60, 32));
        connectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	connectBtnActionPerformed(evt);
            }
        });

        qcmDataChartTextField.setEditable(false);
        qcmDataChartTextField.setBackground(new java.awt.Color(220, 220, 220));
        qcmDataChartTextField.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        qcmDataChartTextField.setHorizontalAlignment(JTextField.CENTER);
        qcmDataChartTextField.setText("QCM Data Chart");
        qcmDataChartTextField.setBorder(null);
        
        biobrightToggleButton = new JToggleButton("BioBrigth Connect");
        biobrightToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	biobrightActionPerformed(evt);
            }
        });


        GroupLayout jPanelBottomLayout = new GroupLayout(jPanelBottom);
        jPanelBottomLayout.setHorizontalGroup(
        	jPanelBottomLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanelBottomLayout.createSequentialGroup()
        			.addComponent(saveFileBtn, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(qcmDataChartTextField, 484, 484, 484)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(connectBtn, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
        		.addComponent(biobrightToggleButton, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
        );
        jPanelBottomLayout.setVerticalGroup(
        	jPanelBottomLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanelBottomLayout.createSequentialGroup()
        			.addGroup(jPanelBottomLayout.createParallelGroup(Alignment.LEADING)
        				.addComponent(connectBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        				.addComponent(qcmDataChartTextField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(saveFileBtn, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(biobrightToggleButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap())
        );
        jPanelBottom.setLayout(jPanelBottomLayout);

        jPanelChart.setBackground(new java.awt.Color(0, 142, 192));

        logoImage.setIcon(new ImageIcon(getClass().getResource("openQCM-logo.png")));

        clearChartBtn.setText("Clear");
        clearChartBtn.setBorder(null);
        clearChartBtn.setBorderPainted(false);
        clearChartBtn.setOpaque(false);
        clearChartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearChartBtnActionPerformed(evt);
            }
        });

        showTemperatureBtn.setText("Show");
        showTemperatureBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTemperatureBtnActionPerformed(evt);
            }
        });

        frequencyCurrent.setEditable(false);
        frequencyCurrent.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        frequencyCurrent.setHorizontalAlignment(JTextField.CENTER);
        frequencyCurrent.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        temperatureCurrent.setEditable(false);
        temperatureCurrent.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        temperatureCurrent.setHorizontalAlignment(JTextField.CENTER);
        temperatureCurrent.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        clearChartLabel.setForeground(new java.awt.Color(255, 255, 255));
        clearChartLabel.setText("Clear Chart");

        temperatureChartLabel.setForeground(new java.awt.Color(255, 255, 255));
        temperatureChartLabel.setText("Temperature Chart");

        frequencyLabel.setForeground(new java.awt.Color(255, 255, 255));
        frequencyLabel.setText("Frequency (Hz)");

        temperatureLabel.setForeground(new java.awt.Color(255, 255, 255));
        temperatureLabel.setText("Temperature (°C)");

        frequencyBox.setModel(new DefaultComboBoxModel<String>(new String[] { "6   MHz", "10 MHz" }));
        frequencyBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frequencyBoxActionPerformed(evt);
            }
        });

        quartzFreqLabel.setForeground(new java.awt.Color(255, 255, 255));
        quartzFreqLabel.setText("Quartz Frequency");

        GroupLayout jPanelChartLayout = new GroupLayout(jPanelChart);
        jPanelChart.setLayout(jPanelChartLayout);
        jPanelChartLayout.setHorizontalGroup(
            jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout.createSequentialGroup()
                .addGroup(jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelChartLayout.createSequentialGroup()
                        .addComponent(logoImage, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addGroup(jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(clearChartBtn, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                            .addComponent(clearChartLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(showTemperatureBtn, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                            .addComponent(temperatureChartLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(frequencyCurrent, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                            .addComponent(frequencyLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(temperatureCurrent, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                            .addComponent(temperatureLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(quartzFreqLabel)
                            .addComponent(frequencyBox, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelChartLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chartData, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelChartLayout.setVerticalGroup(
            jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelChartLayout.createSequentialGroup()
                        .addGroup(jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(clearChartLabel)
                            .addComponent(temperatureChartLabel)
                            .addComponent(frequencyLabel)
                            .addGroup(jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(temperatureLabel)
                                .addComponent(quartzFreqLabel)))
                        .addGap(1, 1, 1)
                        .addGroup(jPanelChartLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(clearChartBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(showTemperatureBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(frequencyCurrent)
                            .addComponent(temperatureCurrent)
                            .addComponent(frequencyBox)))
                    .addComponent(logoImage, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartData, GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelBottom, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanelChart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelChart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBottom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);		
	}

    private void saveFileBtnActionPerformed(ActionEvent evt) {

        // if the button is pressed
        if (saveFileBtn.isSelected() == true) {
            saveFileBtn.setText("Stop Save");
            // open a file chooser
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Text Files", "txt", "dat"
            );
            chooser.setFileFilter(filter);
            int option = chooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                sf = chooser.getSelectedFile();
                saveFileBtn.setText("Stop Save");
                qcmDataChartTextField.setText(sf.getName() + " - QCM Data Chart");
                //saveFile = true;
                //jFormattedTextField3.setText(sf.getName());

            } else {
                JOptionPane.showMessageDialog(
                        null, "No file selected", "Error", JOptionPane.ERROR_MESSAGE);
                saveFileBtn.setText("Save File");
                saveFileBtn.setSelected(false);
                qcmDataChartTextField.setText("QCM Data Chart");
            }
        } // if the button is released
        else if (saveFileBtn.isSelected() == false) {
            saveFileBtn.setText("Save File");
            qcmDataChartTextField.setText("QCM Data Chart");
        }

    }
    
    private void clearChartBtnActionPerformed(ActionEvent evt) {
        chartData.clearChart();
    }
    
    private void showTemperatureBtnActionPerformed(ActionEvent evt) {
        if (showTemperatureBtn.isSelected() == true) {
            showTemperatureBtn.setText("Hide");
        } else if (showTemperatureBtn.isSelected() == false) {
            showTemperatureBtn.setText("Show");
        }
    }
    
    
    private void biobrightActionPerformed(ActionEvent evt) {
        if (biobrightToggleButton.isSelected() == true) {
            // open the popup frame for biobright connection
        	BiobrightConnectionDialog dlg = new BiobrightConnectionDialog(this, "Biobright Connection Dialog", "Connection params:");
            try {
            	ConnectionInfo connectionInfo = dlg.getBiobrightConnectionPanel().getConnectionInfo();
            	disconnectBioBright();
            	
            	biobrightClient = new BiobrightClient(connectionInfo);
            	biobrightClient.connect();
            	if(!isBioBrightConnected()) {
            		throw new RuntimeException("Connection failed.");
            	}
            	
            	biobrightToggleButton.setText("BioBrigth Disconnect");
            }
            catch(Exception e) {
            	JOptionPane.showMessageDialog(this, e.getMessage(), "Something went wrong...", JOptionPane.ERROR_MESSAGE);
            	biobrightToggleButton.setSelected(false);
            	disconnectBioBright();
            }
        	
        	
        } else {
        	disconnectBioBright();
        	biobrightToggleButton.setText("BioBrigth Connect");
        }
	}

    private boolean isBioBrightConnected() {
    	return biobrightClient != null && biobrightClient.isConnected();
    }
    
    private void disconnectBioBright() {
    	try {
        	if(isBioBrightConnected()) {
        		biobrightClient.disconnect();
        	}
    	} catch(Exception e1) {
    		// OK is good enough
    	} finally {
    		biobrightClient = null;
    	}
	}

	private void connectBtnActionPerformed(ActionEvent evt) {

        if (connectBtn.isSelected() == true) {
            // open the popup frame for serial connection
            ArdulinkConnectionDialog dlg = new ArdulinkConnectionDialog(this, "Ardulink Connection Dialog", "Links");
            try {
                link = dlg.getArdulinkConnectionPanel().createLink();
                if(link.getDelegate() instanceof AbstractListenerLink) {
                    ((AbstractListenerLink)link.getDelegate()).addCustomListener(this);
                } else {
                	link.disconnect();
                	link = null;
                	throw new RuntimeException("Selected Link isn't a Listener Link...");
                }
                
                connectBtn.setText("Disconnect");
            }
            catch(Exception e) {
            	JOptionPane.showMessageDialog(this, e.getMessage(), "Something went wrong...", JOptionPane.ERROR_MESSAGE);
                connectBtn.setSelected(false);
            }

        } else {
            try {
				((AbstractListenerLink)link.getDelegate()).removeCustomListener(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
        	link.disconnect();
            chartData.clearChart();
            connectBtn.setText("Connect");

            link = null;
            
            // stop save file
            saveFileBtn.setText("Save File");
            saveFileBtn.setSelected(false);
        }
        
    }
    
    private void formWindowOpened(WindowEvent evt) {
        java.net.URL url = getClass().getResource("openQCM-icon-30x30.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        this.setIconImage(img);
    }
    
    // select the quartz crystal 6 MHz or 10 MHz nominal frequency 
    private void frequencyBoxActionPerformed(java.awt.event.ActionEvent evt) {
        String getFrequencyAlias = (String) frequencyBox.getSelectedItem();

        if (getFrequencyAlias == "6   MHz"){
            System.out.println("6");
            nominalFrequency = 6000000;
            chartData.clearChart();
        }
        else if (getFrequencyAlias == "10 MHz"){
            nominalFrequency = 10000000;
            chartData.clearChart();
        }
    }

	@Override
	public void customEventReceived(CustomEvent customEvent) {
        String messageString = (String)customEvent.getValue();

        // if the message starts with the string "RAWMONITOR" display and store data
        if (messageString.startsWith("RAWMONITOR")) {
            // print the value on the screen
        	logger.fine(messageString);
            messageString = messageString.substring("RAWMONITOR".length());
            String[] dataSplits = messageString.split("_");
            int dataFrequency = Integer.parseInt(dataSplits[0]);
            int dataTemperature = Integer.parseInt(dataSplits[1]);
    
            /* 
             * Frequency Median implemented using Apache commons Math
             * frequency data are affected by some glitches due to the 
             * algorithm for counting pulses during a fixed time interval
             * median is a robust algorithm for smoothing frequency data
             * and for eliminating outliers
             * Frequency data processing algorithm: averaging and calculate median 
             */
            
            // insert new frequency data in circuar buffer and calculate the average
            bufferFrequencyTemp.insert(dataFrequency);
            double sum = 0;
            for (int i = 0; i < bufferFrequencyTemp.size(); i++) {
                sum = sum + bufferFrequencyTemp.getData(i);
            }
            // Average frequency data 
            double averageFrequency = sum / bufferFrequencyTemp.size();
            // insert new average frequency data in circuar buffer and calculate median
            bufferFrequency.insert(averageFrequency);
            // read the circular buffer
            int count = bufferFrequency.size();
            double[] values = new double[count];
            for (int i = 0; i < count; i++) {
            	values[i] = bufferFrequency.getData(i);
            } 
            Median median = new Median();
            // calculate the median of frequency data
            double meanFrequency = median.evaluate(values);
            // alias arduino timer 
            if (nominalFrequency == 10000000) {
            	meanFrequency = (2 * ALIAS) - meanFrequency;
            } 
            
            // insert temperature data in circuar buffer and calculate the average 
            bufferTemperature.insert(dataTemperature);
            double sumT = 0;
            for (int i = 0; i < bufferTemperature.size(); i++) {
                sumT = sumT + bufferTemperature.getData(i);
            }
            // Average temperature data
            double meanTemperature = sumT / bufferTemperature.size();
            // TODO divide by 10 for decimal
            meanTemperature = meanTemperature/10;
            
            // display data
            frequencyCurrent.setText(String.format("%.1f", meanFrequency));
            temperatureCurrent.setText(String.format("%.1f", meanTemperature));

            // add new data in dynamic chart. Frequency data plot by default
            chartData.addFrequencyData(meanFrequency);
            chartData.addTemperatureData(meanTemperature);
            
            // show temperature data in dynamic chart
            if (showTemperatureBtn.isSelected() == true) {
                //chartData.addTemperatureData(meanTemperature);
                chartData.showChartTemperature();
            }
            // hide temperature  
            else if (showTemperatureBtn.isSelected() == false){
                chartData.hideChartTemperature();
            }
            // check domain axis
            chartData.checkDomainAxis();

            // store data 
            if (saveFileBtn.isSelected() == true) {
                try {
                    fw = new FileWriter(sf.getAbsoluteFile(), true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    Calendar cal = Calendar.getInstance();
                    cal.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YY" + "\t" + "HH:mm:ss");
                    bw.write(
                            sdf.format(cal.getTime()) + "\t" 
                            + String.format("%.1f", meanFrequency)  + "\t" 
                            + String.format("%.1f", meanTemperature) + "\r\n"
                    );
                    bw.close();
                } catch (Exception e) {
                    // do nothing... TODO
                }

            }
            
            // call biobright
            if(isBioBrightConnected()) {
            	long now = System.currentTimeMillis();
            	biobrightClient.publish(new PublishingInfo(FREQUENCY, now, String.format("%.1f", meanFrequency), "deviceIDFake1"));
            	biobrightClient.publish(new PublishingInfo(TEMPERATURE, now, String.format("%.1f", meanTemperature), "deviceIDFake1"));
            }

        }		
	}
}
