package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.modeledit.util.TextureExporter;
import com.hiveworkshop.wc3.mdl.EditableModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TextureExporterImpl implements TextureExporter {
    private MainPanel mainPanel;

    TextureExporterImpl(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public JFileChooser getFileChooser() {
        return FileUtils.exportTextureDialog;
    }

    @Override
    public void showOpenDialog(final String suggestedName, final TextureExporterClickListener fileHandler,
                               final Component parent) {
        if (FileUtils.exportTextureDialog.getCurrentDirectory() == null) {
            final EditableModel current = mainPanel.currentMDL();
            if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
                FileUtils.fc.setCurrentDirectory(current.getFile().getParentFile());
            } else if (mainPanel.profile.getPath() != null) {
                FileUtils.fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
            }
        }
        if (FileUtils.exportTextureDialog.getCurrentDirectory() == null) {
            FileUtils.exportTextureDialog.setSelectedFile(
                    new File(FileUtils.exportTextureDialog.getCurrentDirectory() + File.separator + suggestedName));
        }
        final int showOpenDialog = FileUtils.exportTextureDialog.showOpenDialog(parent);
        if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
            final File file = FileUtils.exportTextureDialog.getSelectedFile();
            if (file != null) {
                fileHandler.onClickOK(file, FileUtils.exportTextureDialog.getFileFilter());
            } else {
                JOptionPane.showMessageDialog(parent, "No import file was specified");
            }
        }
    }

    @Override
    public void exportTexture(final String suggestedName, final TextureExporterClickListener fileHandler,
                              final Component parent) {

        if (FileUtils.exportTextureDialog.getCurrentDirectory() == null) {
            final EditableModel current = mainPanel.currentMDL();
            if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
                FileUtils.fc.setCurrentDirectory(current.getFile().getParentFile());
            } else if (mainPanel.profile.getPath() != null) {
                FileUtils.fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
            }
        }
        if (FileUtils.exportTextureDialog.getCurrentDirectory() == null) {
            FileUtils.exportTextureDialog.setSelectedFile(
                    new File(FileUtils.exportTextureDialog.getCurrentDirectory() + File.separator + suggestedName));
        }

        final int x = FileUtils.exportTextureDialog.showSaveDialog(parent);
        if (x == JFileChooser.APPROVE_OPTION) {
            final File file = FileUtils.exportTextureDialog.getSelectedFile();
            if (file != null) {
                try {
                    if (file.getName().lastIndexOf('.') >= 0) {
                        fileHandler.onClickOK(file, FileUtils.exportTextureDialog.getFileFilter());
                    } else {
                        JOptionPane.showMessageDialog(parent, "No file type was specified");
                    }
                } catch (final Exception e2) {
                    ExceptionPopup.display(e2);
                    e2.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(parent, "No output file was specified");
            }
        }
    }
}
