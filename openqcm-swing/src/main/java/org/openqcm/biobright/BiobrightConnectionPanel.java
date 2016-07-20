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
		gridBagLayout.columnWidths = new int[]{225, 225, 0};
		gridBagLayout.rowHeights = new int[]{75, 75, 75, 75, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblUrl = new JLabel("URL:");
		GridBagConstraints gbc_lblUrl = new GridBagConstraints();
		gbc_lblUrl.fill = GridBagConstraints.BOTH;
		gbc_lblUrl.insets = new Insets(0, 0, 5, 5);
		gbc_lblUrl.gridx = 0;
		gbc_lblUrl.gridy = 0;
		add(lblUrl, gbc_lblUrl);
		
		urlTextField = new JTextField();
		GridBagConstraints gbc_urlTextField = new GridBagConstraints();
		gbc_urlTextField.fill = GridBagConstraints.BOTH;
		gbc_urlTextField.insets = new Insets(0, 0, 5, 0);
		gbc_urlTextField.gridx = 1;
		gbc_urlTextField.gridy = 0;
		add(urlTextField, gbc_urlTextField);
		urlTextField.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username:");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.fill = GridBagConstraints.BOTH;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 1;
		add(lblUsername, gbc_lblUsername);
		
		usernameTextField = new JTextField();
		GridBagConstraints gbc_usernameTextField = new GridBagConstraints();
		gbc_usernameTextField.fill = GridBagConstraints.BOTH;
		gbc_usernameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_usernameTextField.gridx = 1;
		gbc_usernameTextField.gridy = 1;
		add(usernameTextField, gbc_usernameTextField);
		usernameTextField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.fill = GridBagConstraints.BOTH;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 2;
		add(lblPassword, gbc_lblPassword);
		
		passwordTextField = new JTextField();
		GridBagConstraints gbc_passwordTextField = new GridBagConstraints();
		gbc_passwordTextField.fill = GridBagConstraints.BOTH;
		gbc_passwordTextField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordTextField.gridx = 1;
		gbc_passwordTextField.gridy = 2;
		add(passwordTextField, gbc_passwordTextField);
		passwordTextField.setColumns(10);
		
		btnUpdate = new JButton("Update Configuration");
		GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
		gbc_btnUpdate.gridwidth = 2;
		gbc_btnUpdate.fill = GridBagConstraints.BOTH;
		gbc_btnUpdate.insets = new Insets(0, 0, 0, 5);
		gbc_btnUpdate.gridx = 0;
		gbc_btnUpdate.gridy = 3;
		add(btnUpdate, gbc_btnUpdate);
		
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
