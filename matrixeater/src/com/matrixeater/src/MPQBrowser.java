package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.mdl.Bitmap;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import net.infonode.docking.View;

import javax.swing.*;
import java.awt.*;

public class MPQBrowser {
    static View createMPQBrowser(MainPanel mainPanel, final ImageIcon imageIcon) {
        final com.hiveworkshop.wc3.gui.mpqbrowser.MPQBrowser mpqBrowser = new com.hiveworkshop.wc3.gui.mpqbrowser.MPQBrowser(MpqCodebase.get(), filepath -> {
            if (filepath.toLowerCase().endsWith(".mdx")) {
                FileUtils.loadFile(mainPanel, MpqCodebase.get().getFile(filepath), true);
            } else if (filepath.toLowerCase().endsWith(".blp")) {
                FileUtils.loadBLPPathAsModel(mainPanel, filepath);
            } else if (filepath.toLowerCase().endsWith(".png")) {
                FileUtils.loadBLPPathAsModel(mainPanel, filepath);
            } else if (filepath.toLowerCase().endsWith(".dds")) {
                FileUtils.loadBLPPathAsModel(mainPanel, filepath, null, 1000);
            }
        }, path -> {
            final int modIndex = Math.max(path.lastIndexOf(".w3mod/"), path.lastIndexOf(".w3mod\\"));
            String finalPath;
            if (modIndex == -1) {
                finalPath = path;
            } else {
                finalPath = path.substring(modIndex + ".w3mod/".length());
            }
            final ModelPanel modelPanel = mainPanel.currentModelPanel;
            if (modelPanel != null) {
                if (modelPanel.getModel().getFormatVersion() > 800) {
                    finalPath = finalPath.replace("\\", "/"); // Reforged prefers forward slash
                }
                modelPanel.getModel().add(new Bitmap(finalPath));
                mainPanel.modelStructureChangeListener.texturesChanged();
            }
        });
        final View view = new View("Data Browser", imageIcon, mpqBrowser);
        view.getWindowProperties().setCloseEnabled(true);
        return view;
    }

    static View createMPQBrowser(MainPanel mainPanel) {
        return createMPQBrowser(
                mainPanel, new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)));
    }
}
