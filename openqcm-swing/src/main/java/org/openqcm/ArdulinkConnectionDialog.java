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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ardulink.gui.connectionpanel.ConnectionPanel;

public class ArdulinkConnectionDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -8507363700565401366L;

	private ConnectionPanel connectionPanel;
	
    public ArdulinkConnectionDialog(JFrame parent, String title, String message) {
        super(parent, title, true);

        // message panel
        JPanel messagePane = new JPanel();
        messagePane.add(new JLabel(message));
        getContentPane().add(messagePane, BorderLayout.NORTH);
        
        // show the Ardulink swing component fo serial connection
        connectionPanel = new ConnectionPanel();

        getContentPane().add(connectionPanel, BorderLayout.CENTER);

        JPanel buttonPane = new JPanel();

        // add dialog button
        JButton button = new JButton("OK");
        button.addActionListener(this);
        buttonPane.add(button);
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setSize(640, 480);
        // display at the centre of the screen
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public ConnectionPanel getConnectionPanel() {
		return connectionPanel;
	}

	@Override
    public void actionPerformed(ActionEvent ae) {

        setVisible(false);
        dispose();
    }
}
