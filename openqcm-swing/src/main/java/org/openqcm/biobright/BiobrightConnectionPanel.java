package org.openqcm.biobright;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openqcm.config.Configuration;
import org.openqcm.config.ConfigurationFacade;

import static org.ardulink.gui.connectionpanel.GridBagConstraintsBuilder.constraints;

public class BiobrightConnectionPanel extends JPanel {
	private JTextField urlTextField;
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	private JButton btnUpdate;

	/**
	 * Create the panel.
	 */
	public BiobrightConnectionPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		JLabel lblUrl = new JLabel("URL:");
		add(lblUrl, constraints(0, 0).build());
		
		urlTextField = new JTextField();
		urlTextField.setColumns(50);
		add(urlTextField, constraints(0, 1).fillHorizontal().build());
		
		JLabel lblUsername = new JLabel("Username:");
		add(lblUsername, constraints(1, 0).build());
		
		usernameTextField = new JTextField();
		usernameTextField.setColumns(50);
		add(usernameTextField, constraints(1, 1).fillHorizontal().build());
		
		JLabel lblPassword = new JLabel("Password:");
		add(lblPassword, constraints(2, 0).build());
		
		passwordTextField = new JTextField();
		passwordTextField.setColumns(50);
		add(passwordTextField, constraints(2, 1).build());
		
		btnUpdate = new JButton("Update Configuration");
		add(btnUpdate, constraints(3, 0).gridwidth(2).fillHorizontal().build());
		
		btnUpdate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveConfiguration();
			}
		});

		loadConfiguration();
		
	}

	private void loadConfiguration() {
		Configuration configuration = ConfigurationFacade.getConfiguration();
		urlTextField.setText(configuration.getBiobrightUrl());
		usernameTextField.setText(configuration.getBiobrightUserName());
		passwordTextField.setText(configuration.getBiobrightPassword());
	}
	
	private void saveConfiguration() {
		Configuration configuration = ConfigurationFacade.getConfiguration();

		configuration.setBiobrightUrl(urlTextField.getText());
		configuration.setBiobrightUserName(usernameTextField.getText());
		configuration.setBiobrightPassword(passwordTextField.getText());
		
		try {
			ConfigurationFacade.saveConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error saving config: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public ConnectionInfo getConnectionInfo() {
		return new ConnectionInfo(urlTextField.getText(), usernameTextField.getText(), passwordTextField.getText());
	}

}
