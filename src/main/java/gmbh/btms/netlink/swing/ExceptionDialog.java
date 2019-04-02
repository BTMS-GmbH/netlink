/*
 *  Copyright 2019 [https://btms.gmbh]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package gmbh.btms.netlink.swing;

import gmbh.btms.netlink.IncidentReport;
import gmbh.btms.netlink.RuntimeConfig;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;

/**
 * ExceptionDialog
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class ExceptionDialog extends JDialog {

	private JPanel contentPane;
	private JTextArea lblErrorMessage;
	private JLabel lblMainMessage;
	private JLabel lblError;
	private JTextArea exceptionText;

	private Path rootDirectory = null;
	private Path applicationPath = null;

	/**
	 * Create the frame.
	 */
	public ExceptionDialog() {

		super(null, ModalityType.APPLICATION_MODAL);
		initGUI();
		setLocationRelativeTo(null);
	}

	private void initGUI() {

		ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.getDefault());
		setTitle("BTMS Launcher");

		setUndecorated(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 729, 467);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);

		JPanel panelHeader = new JPanel();
		panelHeader.setSize(40, 40);
		panelHeader.setBackground(UIManager.getColor("info"));
		SpringLayout sl_panelHeader = new SpringLayout();
		panelHeader.setLayout(sl_panelHeader);

		lblMainMessage = new JLabel(messages.getString("exceptionDialog.lblMainMessage"));
		sl_panelHeader.putConstraint(SpringLayout.NORTH, lblMainMessage, 23, SpringLayout.NORTH, panelHeader);
		sl_panelHeader.putConstraint(SpringLayout.WEST, lblMainMessage, 8, SpringLayout.WEST, panelHeader);
		lblMainMessage.setHorizontalAlignment(SwingConstants.LEFT);
		panelHeader.add(lblMainMessage);
		lblMainMessage.setFont(new Font("Tahoma", Font.PLAIN, 22));

		JLabel lblLOGO = new JLabel("");
		sl_panelHeader.putConstraint(SpringLayout.NORTH, lblLOGO, 0, SpringLayout.NORTH, panelHeader);
		sl_panelHeader.putConstraint(SpringLayout.EAST, lblLOGO, 0, SpringLayout.EAST, panelHeader);
		panelHeader.add(lblLOGO);
		lblLOGO.setIcon(new ImageIcon("logo.gif"));

		JPanel panelBody = new JPanel();
		SpringLayout sl_panelBody = new SpringLayout();
		panelBody.setLayout(sl_panelBody);

		lblError = new JLabel(messages.getString("exceptionDialog.lblError"));
		sl_panelBody.putConstraint(SpringLayout.WEST, lblError, 10, SpringLayout.WEST, panelBody);
		panelBody.add(lblError);

		lblErrorMessage = new JTextArea("ERROR DESCRIPTION");
		lblErrorMessage.setEditable(false);
		lblErrorMessage.setFont(new Font("Tahoma", Font.PLAIN, 16));
		sl_panelBody.putConstraint(SpringLayout.WEST, lblErrorMessage, 136, SpringLayout.WEST, panelBody);
		sl_panelBody.putConstraint(SpringLayout.EAST, lblErrorMessage, -10, SpringLayout.EAST, panelBody);
		sl_panelBody.putConstraint(SpringLayout.NORTH, lblError, 0, SpringLayout.NORTH, lblErrorMessage);
		sl_panelBody.putConstraint(SpringLayout.EAST, lblError, -6, SpringLayout.WEST, lblErrorMessage);
		sl_panelBody.putConstraint(SpringLayout.NORTH, lblErrorMessage, 10, SpringLayout.NORTH, panelBody);
		panelBody.add(lblErrorMessage);

		JButton btnOkButton = new JButton("OK");
		sl_panelBody.putConstraint(SpringLayout.SOUTH, btnOkButton, -21, SpringLayout.SOUTH, panelBody);
		sl_panelBody.putConstraint(SpringLayout.EAST, btnOkButton, -10, SpringLayout.EAST, panelBody);
		panelBody.add(btnOkButton);
		btnOkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				dispose();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(panelHeader, GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
								          .addComponent(panelBody, GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
								          .addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
								          .addComponent(panelHeader, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
								          .addGap(1)
								          .addComponent(panelBody, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE))
		);

		JButton btnReportButton = new JButton("REPORT");
		sl_panelBody.putConstraint(SpringLayout.NORTH, btnReportButton, 0, SpringLayout.NORTH, btnOkButton);
		sl_panelBody.putConstraint(SpringLayout.EAST, btnReportButton, -13, SpringLayout.WEST, btnOkButton);
		panelBody.add(btnReportButton);
		boolean enabled = applicationPath != null && rootDirectory != null;
		btnReportButton.setEnabled(enabled);
		btnReportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				IncidentReport report = new IncidentReport();
				report.createReport(applicationPath, rootDirectory);
				setCursor(Cursor.getDefaultCursor());
				dispose();
			}
		});

		exceptionText = new JTextArea();
		sl_panelBody.putConstraint(SpringLayout.SOUTH, lblError, -46, SpringLayout.NORTH, exceptionText);
		sl_panelBody.putConstraint(SpringLayout.SOUTH, lblErrorMessage, -19, SpringLayout.NORTH, exceptionText);
		sl_panelBody.putConstraint(SpringLayout.NORTH, exceptionText, 85, SpringLayout.NORTH, panelBody);
		sl_panelBody.putConstraint(SpringLayout.SOUTH, exceptionText, -19, SpringLayout.NORTH, btnOkButton);
		sl_panelBody.putConstraint(SpringLayout.WEST, exceptionText, 10, SpringLayout.WEST, panelBody);
		sl_panelBody.putConstraint(SpringLayout.EAST, exceptionText, -10, SpringLayout.EAST, panelBody);
		exceptionText.setWrapStyleWord(true);
		exceptionText.setText("TEST");
		exceptionText.setRows(10);
		exceptionText.setTabSize(4);
		exceptionText.setEditable(false);
		panelBody.add(exceptionText);
		contentPane.setLayout(gl_contentPane);

	}

	public ExceptionDialog(RuntimeConfig configuration) {

		if (configuration != null) {
			rootDirectory = configuration.getLocalFileRoot();
			applicationPath = configuration.getLocalFileCache();
			if (applicationPath == null) {
				applicationPath = configuration.getLocalFileRoot();
			}
		}
		initGUI();
		setLocationRelativeTo(null);
	}

	public JTextArea getEditor() {
		return lblErrorMessage;
	}

	public JTextArea getExceptionText() {
		return exceptionText;
	}
}
