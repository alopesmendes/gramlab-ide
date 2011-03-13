/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.MyDropTarget;
import fr.umlv.unitex.exceptions.InvalidDestinationEncodingException;
import fr.umlv.unitex.exceptions.InvalidSourceEncodingException;
import fr.umlv.unitex.listeners.LanguageListener;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.ConvertCommand;
import fr.umlv.unitex.transcoding.Transcoder;

/**
 * This class provides a file transcoding internal frame.
 *
 * @author Sébastien Paumier
 */
public class TranscodingFrame extends JInternalFrame {
    private final JList srcEncodingList = new JList(Transcoder.getAvailableEncodings());
    private final JList destEncodingList = new JList(Transcoder.getAvailableEncodings());
    private final JRadioButton replace = new JRadioButton("Replace");
    private final JRadioButton renameSourceWithPrefix = new JRadioButton(
            "Rename source with prefix");
    private final JRadioButton renameSourceWithSuffix = new JRadioButton(
            "Rename source with suffix");
    private final JRadioButton nameDestWithPrefix = new JRadioButton(
            "Name destination with prefix");
    private final JRadioButton nameDestWithSuffix = new JRadioButton(
            "Name destination with suffix");
    private final JTextField prefixSuffix = new JTextField("");
    private final DefaultListModel listModel = new DefaultListModel();
    private final JList fileList = new JList(listModel);
    private final JButton addFiles = new JButton("Add Files");
    private final JButton removeFiles = new JButton("Remove Files");
    private final JButton transcode = new JButton("Transcode");
    private final JButton cancel = new JButton("Cancel");
    private ToDo toDo;
    private boolean closeAfterWork=false;

    TranscodingFrame() {
        super("Transcode Files", true, true);
        setContentPane(constructPanel());
        setBounds(100, 100, 500, 500);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        srcEncodingList.setSelectedValue(Transcoder
                .getEncodingForLanguage(Config.getCurrentLanguage()), true);
        Config.addLanguageListener(new LanguageListener() {
            public void languageChanged() {
                srcEncodingList.setSelectedValue(Transcoder
                        .getEncodingForLanguage(Config.getCurrentLanguage()), true);
            }
        });
        destEncodingList.setSelectedValue("LITTLE-ENDIAN", true);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                listModel.removeAllElements();
                toDo = null;
            }
        });
    }

    private JPanel constructPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel tmp = new JPanel(new GridLayout(1, 2));
        tmp.add(constructSrcEncodingPanel());
        tmp.add(constructDestEncodingPanel());
        JPanel up = new JPanel(new BorderLayout());
        up.add(tmp, BorderLayout.CENTER);
        up.add(constructFileNamePanel(), BorderLayout.EAST);
        panel.add(up, BorderLayout.NORTH);
        JPanel down = new JPanel(new BorderLayout());
        down.add(constructFileListPanel(), BorderLayout.CENTER);
        down.add(constructButtonPanel(), BorderLayout.EAST);
        panel.add(down, BorderLayout.CENTER);
        return panel;
    }

    private JPanel constructSrcEncodingPanel() {
        JPanel srcEncodingPanel = new JPanel(new BorderLayout());
        srcEncodingPanel
                .add(new JLabel("Source encoding:"), BorderLayout.NORTH);
        srcEncodingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        srcEncodingPanel.add(new JScrollPane(srcEncodingList),
                BorderLayout.CENTER);
        return srcEncodingPanel;
    }

    private JPanel constructDestEncodingPanel() {
        JPanel destEncodingPanel = new JPanel(new BorderLayout());
        destEncodingPanel.add(new JLabel("Destination encoding:"),
                BorderLayout.NORTH);
        destEncodingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        destEncodingPanel.add(new JScrollPane(destEncodingList),
                BorderLayout.CENTER);
        return destEncodingPanel;
    }

    private JPanel constructFileNamePanel() {
        JPanel fileNamePanel = new JPanel(new GridLayout(7, 1));
        fileNamePanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        fileNamePanel.add(replace);
        fileNamePanel.add(renameSourceWithPrefix);
        fileNamePanel.add(renameSourceWithSuffix);
        fileNamePanel.add(nameDestWithPrefix);
        fileNamePanel.add(nameDestWithSuffix);
        fileNamePanel.add(new JLabel("Prefix/suffix:"));
        fileNamePanel.add(prefixSuffix);
        ButtonGroup bg = new ButtonGroup();
        bg.add(replace);
        bg.add(renameSourceWithPrefix);
        bg.add(renameSourceWithSuffix);
        bg.add(nameDestWithPrefix);
        bg.add(nameDestWithSuffix);
        renameSourceWithSuffix.setSelected(true);
        prefixSuffix.setText("-old");
        return fileNamePanel;
    }

    private JPanel constructFileListPanel() {
        JPanel fileListPanel = new JPanel(new BorderLayout());
        fileListPanel.setBorder(new TitledBorder("Selected files:"));
        fileListPanel.add(new JScrollPane(fileList), BorderLayout.CENTER);
        MyDropTarget.newTranscodeDropTarget(fileList);
        return fileListPanel;
    }

    private JPanel constructButtonPanel() {
        final TranscodingFrame zis = this;
        addFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int returnVal = Config.getTranscodeDialogBox().showOpenDialog(
                        zis);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    // we return if the user has clicked on CANCEL
                    return;
                }
                File[] graphs = Config.getTranscodeDialogBox()
                        .getSelectedFiles();
                for (File graph : graphs) {
                    if (!listModel.contains(graph)) {
                        listModel.addElement(graph);
                    }
                }
            }
        });
        removeFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Object[] graphs = fileList.getSelectedValues();
                for (Object graph : graphs) {
                    listModel.removeElement(graph);
                }
            }
        });
        transcode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String src = (String) srcEncodingList.getSelectedValue();
                if (src == null) {
                    JOptionPane.showMessageDialog(null,
                            "You must select an input encoding", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String dest = (String) destEncodingList.getSelectedValue();
                if (dest == null) {
                    JOptionPane.showMessageDialog(null,
                            "You must select a destination encoding", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String preSuf = prefixSuffix.getText();
                if (!replace.isSelected() && preSuf.equals("")) {
                    JOptionPane.showMessageDialog(null,
                            "You must specify a prefix/suffix", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ConvertCommand command;
                try {
                    command = new ConvertCommand().src(src).dest(dest);
                } catch (InvalidDestinationEncodingException e) {
                    e.printStackTrace();
                    return;
                } catch (InvalidSourceEncodingException e) {
                    e.printStackTrace();
                    return;
                }
                if (replace.isSelected())
                    command = command.replace();
                else if (renameSourceWithPrefix.isSelected())
                    command = command.renameSourceWithPrefix(preSuf);
                else if (renameSourceWithSuffix.isSelected()) {
                    command = command.renameSourceWithSuffix(preSuf);
                } else if (nameDestWithPrefix.isSelected()) {
                    command = command.renameDestWithPrefix(preSuf);
                } else {
                    command = command.renameDestWithSuffix(preSuf);
                }
                final ConvertCommand cmd = command;
                final ToDo d = toDo;
                toDo = null;
                setVisible(false);
                int l = listModel.getSize();
                for (int i = 0; i < l; i++) {
                    cmd.file((File) listModel.getElementAt(i));
                }
                Launcher.exec(cmd, closeAfterWork, d);
            }
        });
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                doDefaultCloseAction();
            }
        });
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel tmp = new JPanel(new GridLayout(4, 1));
        tmp.add(addFiles);
        tmp.add(removeFiles);
        tmp.add(transcode);
        tmp.add(cancel);
        buttonPanel.add(tmp, BorderLayout.NORTH);
        buttonPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        return buttonPanel;
    }

    /**
     * @return the list model of the conversion frame
     */
    public DefaultListModel getListModel() {
        return listModel;
    }

    void configure(File file, ToDo toDo1, boolean closeAfterWork) {
        listModel.removeAllElements();
        this.closeAfterWork=closeAfterWork;
        this.toDo = toDo1;
        if (toDo1 != null) {
            listModel.addElement(file);
            addFiles.setEnabled(false);
            removeFiles.setEnabled(false);
        } else {
            addFiles.setEnabled(true);
            removeFiles.setEnabled(true);
        }
    }
}
