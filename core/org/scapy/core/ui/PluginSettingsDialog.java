package org.scapy.core.ui;

import org.scapy.core.event.EventDispatcher;
import org.scapy.core.event.impl.PluginEvent;
import org.scapy.core.event.listeners.PluginAdapter;
import org.scapy.core.plugin.Plugin;
import org.scapy.core.plugin.PluginManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class PluginSettingsDialog extends JDialog implements ListSelectionListener, ActionListener {

	private final DefaultListModel<String> listModel = new DefaultListModel<>();
	private final JList<String> pluginList = new JList<>(listModel);
	private final CardLayout cardLayout = new CardLayout();
	private final JPanel centerPanel = new JPanel(new BorderLayout());
	private final JPanel cardPanel = new JPanel(cardLayout);
	private final JPanel bottomPanel = new JPanel();
	private final JButton stopButton = new JButton("Stop");
	private final JButton pauseResumeButton = new JButton("Pause");

	PluginSettingsDialog() {
		super(GameWindow.getWindow(), "Plugin Settings");
		pluginList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pluginList.addListSelectionListener(this);
		pluginList.setFixedCellWidth(175);
		centerPanel.setPreferredSize(new Dimension(400, 150));
		centerPanel.add(bottomPanel, BorderLayout.PAGE_END);
		centerPanel.add(cardPanel);
		bottomPanel.add(stopButton);
		bottomPanel.add(pauseResumeButton);
		stopButton.setEnabled(false);
		stopButton.addActionListener(this);
		pauseResumeButton.setEnabled(false);
		pauseResumeButton.addActionListener(this);
		add(new JScrollPane(pluginList), BorderLayout.LINE_START);
		add(centerPanel);
		EventDispatcher.instance.addListener(new PluginEvents());
		pack();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedIndex = pluginList.getSelectedIndex();
		if (selectedIndex != -1) {
			cardLayout.show(cardPanel, listModel.get(selectedIndex));
			pauseResumeButton.setText(selectedPlugin().isPaused() ? "Resume" : "Pause");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		String text = button.getText();
		switch (text) {
			case "Stop":
				selectedPlugin().stop();
				break;
			case "Pause":
				selectedPlugin().pause();
				break;
			case "Resume":
				selectedPlugin().resume();
				break;
		}
	}

	void addPlugin(Plugin plugin) {
		JPanel settingsPanel = plugin.getSettingsPanel();
		String name = plugin.name();
		if (settingsPanel == null) {
			settingsPanel = new JPanel();
			settingsPanel.add(new JLabel("This plugin does not implement a settings panel."));
		}
		listModel.addElement(name);
		cardPanel.add(settingsPanel, name);
		pluginList.setSelectedValue(name, true);
		if (!stopButton.isEnabled() || !pauseResumeButton.isEnabled()) {
			stopButton.setEnabled(true);
			pauseResumeButton.setEnabled(true);
		}
		validate();
	}

	void removePlugin(Plugin plugin) {
		String pluginName = plugin.name();
		int pluginIndex = listModel.indexOf(pluginName);
		if (pluginIndex != -1) {
			listModel.removeElement(pluginName);
			cardPanel.remove(pluginIndex);
			if (listModel.isEmpty()) {
				stopButton.setEnabled(false);
				pauseResumeButton.setEnabled(false);
			} else {
				if (pluginIndex == 0) {
					pluginList.setSelectedIndex(0);
				} else if (pluginIndex == listModel.size()) {
					pluginList.setSelectedIndex(listModel.size() - 1);
				} else {
					pluginList.setSelectedIndex(pluginIndex - 1);
				}
			}
			validate();
		}
	}

	private Plugin selectedPlugin() {
		return PluginManager.instance.get(listModel.get(pluginList.getSelectedIndex()));
	}

	private class PluginEvents extends PluginAdapter {

		@Override
		public void onPause(final PluginEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (pluginList.getSelectedIndex() != -1 && e.getSource() == selectedPlugin()) {
						pauseResumeButton.setText("Resume");
					}
				}
			});
		}

		@Override
		public void onResume(final PluginEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (pluginList.getSelectedIndex() != -1 && e.getSource() == selectedPlugin()) {
						pauseResumeButton.setText("Pause");
					}
				}
			});
		}
	}
}