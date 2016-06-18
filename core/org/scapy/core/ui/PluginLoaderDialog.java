package org.scapy.core.ui;

import org.scapy.core.Gamepack;
import org.scapy.core.plugin.Plugin;
import org.scapy.core.plugin.PluginLoader;
import org.scapy.core.plugin.PluginLoader.PluginLoadException;
import org.scapy.core.plugin.PluginManager;
import org.scapy.core.plugin.PluginManifest;
import org.scapy.utils.WebUtilities;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

class PluginLoaderDialog extends JDialog {

    private final JFileChooser fileChooser = new JFileChooser();
    private final JTextField addressField = new JTextField(35);
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[] { "Name", "Author(s)", "Description", "Version" }, 1);
    private final JTable informationTable = new JTable(tableModel);
    private final JButton clearButton = new JButton("Clear");
    private final JButton loadButton = new JButton("Load");
    private final JButton startButton = new JButton("Start");
    private final JPanel topPanel = new JPanel();
    private final JPanel centerPanel = new JPanel(new BorderLayout());
    private final JPanel bottomPanel = new JPanel();
    private volatile Plugin plugin;

    PluginLoaderDialog() {
        super(GameWindow.getWindow(), "Load Plugin", true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Plugin archives (.jar)", "jar"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        informationTable.setEnabled(false);
        addButtonListeners();
        topPanel.add(new JLabel("Remote Address:"));
        topPanel.add(addressField);
        JPanel informationPanel = new JPanel(new BorderLayout());
        informationPanel.add(informationTable.getTableHeader(), BorderLayout.PAGE_START);
        informationPanel.add(informationTable);
        centerPanel.add(informationPanel);
        bottomPanel.add(clearButton);
        bottomPanel.add(loadButton);
        bottomPanel.add(startButton);
        add(topPanel, BorderLayout.PAGE_START);
        add(bottomPanel, BorderLayout.PAGE_END);
        setAutoRequestFocus(true);
        setResizable(false);
        pack();
    }

    private void clearInformation() {
        addressField.setText("");
        plugin = null;
        if (tableModel.getRowCount() > 0) {
            tableModel.getDataVector().clear();
            tableModel.fireTableDataChanged();
            remove(centerPanel);
            pack();
        }
    }

    private void addButtonListeners() {
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                clearInformation();
            }
        });
        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object pluginSource = null;
                String address = addressField.getText();
                if (address.isEmpty()) {
                    if (fileChooser.showDialog(PluginLoaderDialog.this, "Select Plugin") == JFileChooser.APPROVE_OPTION) {
                        pluginSource = fileChooser.getSelectedFile();
                    }
                } else {
                    pluginSource = address;
                }
                if (pluginSource != null) {
                    new LoadTask(pluginSource).execute();
                }
            }
        });
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (plugin != null) {
                    new SwingWorker() {

                        @Override
                        protected Object doInBackground() throws Exception {
                            PluginManager.instance.startPlugin(plugin);
                            return null;
                        }

                        @Override
                        protected void done() {
                            String errorMessage = "";
                            try {
                                get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                                Throwable cause = e.getCause();
                                if (cause instanceof IllegalArgumentException) {
                                    errorMessage = cause.getMessage();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                errorMessage = "Could not start the plugin.";
                            }
                            if (errorMessage.isEmpty()) {
                                clearInformation();
                                setVisible(false);
                            } else {
                                JOptionPane.showMessageDialog(PluginLoaderDialog.this, errorMessage, "Plugin Start Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }.execute();
                }
            }
        });
    }

    private class LoadTask extends SwingWorker<Plugin, Object> {

        private Object pluginSource;

        private LoadTask(Object pluginSource) {
            this.pluginSource = pluginSource;
        }

        @Override
        protected Plugin doInBackground() throws Exception {
            if (pluginSource instanceof String) {
                pluginSource = WebUtilities.download(pluginSource.toString());
            }
            return PluginLoader.load(Gamepack.create(pluginSource));
        }

        @Override
        protected void done() {
            String errorMessage = "";
            Plugin loadedPlugin = null;
            try {
                loadedPlugin = get();
            } catch (ExecutionException e) {
                e.printStackTrace();
                Throwable cause = e.getCause();
                if (cause instanceof IOException) {
                    errorMessage = "Could not download the plugin archive.";
                } else if (cause instanceof ReflectiveOperationException) {
                    errorMessage = "Error initialing the main plugin file.";
                } else if (cause instanceof PluginLoadException) {
                    errorMessage = "Bad plugin archive format. " + cause.getMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "Could not load the plugin.";
            }
            if (errorMessage.isEmpty()) {
                plugin = loadedPlugin;
                PluginManifest manifest = plugin.manifest();
                StringBuilder authors = new StringBuilder();
                for (String author : manifest.authors()) {
                    if (author != null) {
                        authors.append(author);
                        authors.append(", ");
                    }
                }
                if (authors.length() > 0) {
                    authors.delete(authors.length() - 2, authors.length());
                }
                tableModel.insertRow(0, new Object[] { manifest.name(), authors, manifest.description(), manifest.version() });
                tableModel.setRowCount(1);
                add(centerPanel);
                pack();
            } else {
                JOptionPane.showMessageDialog(PluginLoaderDialog.this, errorMessage, "Plugin Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}