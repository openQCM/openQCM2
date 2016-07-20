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
package org.openqcm.biobright;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openqcm.AbstractConnectionDialog;

public class BiobrightConnectionDialog extends AbstractConnectionDialog {
	
	private static final long serialVersionUID = -8507363700565401367L;

	public BiobrightConnectionDialog(JFrame parent, String title, String message) {
		super(parent, title, message);
	}

    public BiobrightConnectionPanel getBiobrightConnectionPanel() {
		return (BiobrightConnectionPanel)getConnectionPanel();
	}
	
	
	@Override
	public JPanel createConnectionPanel() {
		return new JPanel();
	}
    
}
