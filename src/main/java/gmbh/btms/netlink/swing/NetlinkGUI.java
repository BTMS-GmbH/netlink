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

import gmbh.btms.netlink.UpdateWorker;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;

public class NetlinkGUI extends JFrame implements PropertyChangeListener {

	/**
	 * The log.
	 */
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(NetlinkGUI.class);

	private JPanel contentPane;
	private JLabel lblLocationName;
	private JLabel lblPublisherName;
	private JLabel lblApplicationVersion;
	private JLabel lblMainMessage;
	private JLabel lblCurrentAction;
	private JProgressBar progressBar;
	private UpdateWorker updateWorker;
	private ResourceBundle messages;

	/**
	 * Create the frame.
	 */
	public NetlinkGUI() {
		initGUI();
		setLocationRelativeTo(null);
	}

	private void initGUI() {

		messages = ResourceBundle.getBundle("messages", Locale.getDefault());

		setTitle("BTMS Launcher");
		setResizable(false);

		setUndecorated(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 284);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);

		JPanel panelHeader = new JPanel();
		panelHeader.setSize(40, 40);
		panelHeader.setBackground(UIManager.getColor("info"));
		SpringLayout sl_panelHeader = new SpringLayout();
		panelHeader.setLayout(sl_panelHeader);

		lblMainMessage = new JLabel("New label");
		sl_panelHeader.putConstraint(SpringLayout.NORTH, lblMainMessage, 23, SpringLayout.NORTH, panelHeader);
		sl_panelHeader.putConstraint(SpringLayout.WEST, lblMainMessage, 8, SpringLayout.WEST, panelHeader);
		lblMainMessage.setHorizontalAlignment(SwingConstants.LEFT);
		panelHeader.add(lblMainMessage);
		lblMainMessage.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblMainMessage.setBackground(Color.WHITE);

		JLabel lblLOGO = new JLabel("");
		sl_panelHeader.putConstraint(SpringLayout.NORTH, lblLOGO, 0, SpringLayout.NORTH, panelHeader);
		sl_panelHeader.putConstraint(SpringLayout.EAST, lblLOGO, 0, SpringLayout.EAST, panelHeader);
		panelHeader.add(lblLOGO);
		lblLOGO.setIcon(new ImageIcon(this.getClass().getResource("/logo.gif")));

		JPanel panelBody = new JPanel();
		SpringLayout sl_panelBody = new SpringLayout();
		panelBody.setLayout(sl_panelBody);

		JLabel lblVersion = new JLabel(messages.getString("lbl.Name"));
		sl_panelBody.putConstraint(SpringLayout.NORTH, lblVersion, 10, SpringLayout.NORTH, panelBody);
		sl_panelBody.putConstraint(SpringLayout.WEST, lblVersion, 10, SpringLayout.WEST, panelBody);
		sl_panelBody.putConstraint(SpringLayout.SOUTH, lblVersion, -147, SpringLayout.SOUTH, panelBody);
		panelBody.add(lblVersion);

		lblApplicationVersion = new JLabel("");
		sl_panelBody.putConstraint(SpringLayout.WEST, lblApplicationVersion, 129, SpringLayout.WEST, panelBody);
		sl_panelBody.putConstraint(SpringLayout.EAST, lblVersion, -26, SpringLayout.WEST, lblApplicationVersion);
		sl_panelBody.putConstraint(SpringLayout.NORTH, lblApplicationVersion, 0, SpringLayout.NORTH, lblVersion);
		panelBody.add(lblApplicationVersion);

		JLabel lblPublisher = new JLabel(messages.getString("lbl.Publisher"));
		sl_panelBody.putConstraint(SpringLayout.NORTH, lblPublisher, 6, SpringLayout.SOUTH, lblVersion);
		sl_panelBody.putConstraint(SpringLayout.WEST, lblPublisher, 10, SpringLayout.WEST, panelBody);
		sl_panelBody.putConstraint(SpringLayout.SOUTH, lblPublisher, -121, SpringLayout.SOUTH, panelBody);
		sl_panelBody.putConstraint(SpringLayout.EAST, lblPublisher, -491, SpringLayout.EAST, panelBody);
		panelBody.add(lblPublisher);

		lblPublisherName = new JLabel("");
		sl_panelBody.putConstraint(SpringLayout.NORTH, lblPublisherName, 36, SpringLayout.NORTH, panelBody);
		sl_panelBody.putConstraint(SpringLayout.WEST, lblPublisherName, 0, SpringLayout.WEST, lblApplicationVersion);
		panelBody.add(lblPublisherName);

		JLabel lblLocation = new JLabel(messages.getString("lbl.Location"));
		sl_panelBody.putConstraint(SpringLayout.NORTH, lblLocation, 6, SpringLayout.SOUTH, lblPublisher);
		sl_panelBody.putConstraint(SpringLayout.WEST, lblLocation, 10, SpringLayout.WEST, panelBody);
		sl_panelBody.putConstraint(SpringLayout.EAST, lblLocation, 0, SpringLayout.EAST, lblVersion);
		panelBody.add(lblLocation);

		lblLocationName = new JLabel("");
		sl_panelBody.putConstraint(SpringLayout.NORTH, lblLocationName, 6, SpringLayout.SOUTH, lblPublisherName);
		sl_panelBody.putConstraint(SpringLayout.WEST, lblLocationName, 0, SpringLayout.WEST, lblApplicationVersion);
		panelBody.add(lblLocationName);

		lblCurrentAction = new JLabel("Current Action");
		sl_panelBody.putConstraint(SpringLayout.NORTH, lblCurrentAction, 28, SpringLayout.SOUTH, lblLocation);
		sl_panelBody.putConstraint(SpringLayout.WEST, lblCurrentAction, 10, SpringLayout.WEST, panelBody);
		panelBody.add(lblCurrentAction);

		JButton btnCancelButton = new JButton("CANCEL");
		sl_panelBody.putConstraint(SpringLayout.EAST, btnCancelButton, -10, SpringLayout.EAST, panelBody);
		panelBody.add(btnCancelButton);
		btnCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (updateWorker != null && !updateWorker.isDone() && !updateWorker.isCancelled()) {
					updateWorker.cancel(true);
				}
			}
		});

		progressBar = new JProgressBar();
		sl_panelBody.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, panelBody);
		sl_panelBody.putConstraint(SpringLayout.EAST, progressBar, -24, SpringLayout.WEST, btnCancelButton);
		sl_panelBody.putConstraint(SpringLayout.SOUTH, btnCancelButton, 0, SpringLayout.SOUTH, progressBar);
		sl_panelBody.putConstraint(SpringLayout.SOUTH, progressBar, 26, SpringLayout.SOUTH, lblCurrentAction);
		sl_panelBody.putConstraint(SpringLayout.NORTH, progressBar, 6, SpringLayout.SOUTH, lblCurrentAction);
		panelBody.add(progressBar);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(panelBody, GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
						.addComponent(panelHeader, GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
								          .addComponent(panelHeader, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
								          .addGap(1)
								          .addComponent(panelBody, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)
								          .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}

	/**
	 * Invoked when task's progress property changes.
	 *
	 * @param evt the evt
	 */
	public void propertyChange(PropertyChangeEvent evt) {

		if ("exception" == evt.getPropertyName()) {
			Throwable ex = (Throwable) evt.getNewValue();
			String stackTrace = ExceptionUtils.getStackTrace(ex);
			ExceptionDialog dialog = new ExceptionDialog();
			if (ex instanceof ContextedRuntimeException) {
				ContextedRuntimeException e = (ContextedRuntimeException)ex;
				StringBuilder msg = new StringBuilder(e.getRawMessage());
				List<Pair<String, Object>> context = ((ContextedRuntimeException) ex).getContextEntries();
				Pair<String, Object>contextValue = context.get(0);
				msg.append("\n").append(contextValue.getKey()).append(" : ").append(contextValue.getValue());
				dialog.getEditor().setText(msg.toString());
				StringBuilder longmessage = new StringBuilder();
				context.forEach(pair -> longmessage.append(pair.getKey()).append(" : ").append(pair.getValue()).append("\n"));
				longmessage.append(stackTrace);
				dialog.getExceptionText().setText(longmessage.toString());
			} else {
				dialog.getEditor().setText(ex.getLocalizedMessage());
				dialog.getExceptionText().setText(stackTrace);
			}

			dialog.setVisible(true);
		}

		if ("error" == evt.getPropertyName()) {
			JOptionPane.showMessageDialog(this, messages.getString(evt.getNewValue().toString()), messages.getString("error.Dialog"), JOptionPane.ERROR_MESSAGE);
		}
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}

		if ("state" == evt.getPropertyName()) {
			if (updateWorker != null && updateWorker.isCancelled() || updateWorker.isDone()) {
				shutdownGUI();
			}
		}

		if ("action" == evt.getPropertyName()) {
			lblCurrentAction.setText((String) evt.getNewValue());
		}
		if ("information.title" == evt.getPropertyName()) {
			lblMainMessage.setText((String) evt.getNewValue());
		}
		if ("application.version" == evt.getPropertyName()) {
			lblApplicationVersion.setText((String) evt.getNewValue());
		}
		if ("information.codebase" == evt.getPropertyName()) {
			lblLocationName.setText(evt.getNewValue().toString());
		}
		if ("information.vendor" == evt.getPropertyName()) {
			lblPublisherName.setText((String) evt.getNewValue());
		}
	}

	private void shutdownGUI() {

		dispose();
	}

	public void update() {


		updateWorker = new UpdateWorker();
		updateWorker.addPropertyChangeListener(this);
		updateWorker.execute();

	}
}
