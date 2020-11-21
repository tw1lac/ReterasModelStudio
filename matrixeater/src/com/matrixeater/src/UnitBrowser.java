package com.matrixeater.src;

import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorSettings;
import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorTree;
import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorTreeBrowser;
import com.hiveworkshop.wc3.jworldedit.objects.UnitTabTreeBrowserBuilder;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData;
import net.infonode.docking.View;

import javax.swing.*;
import java.awt.*;

public class UnitBrowser {
    static View createUnitBrowser(MainPanel mainPanel, final ImageIcon imageIcon) {
//        final View mpqBrowserView = MPQBrowser.createMPQBrowser(mainPanel, imageIcon);
        final UnitEditorTree unitEditorTree = createUnitEditorTree(mainPanel);
//        UnitEditorTree createUnitEditorTree() {
//            final UnitEditorTree unitEditorTree = new UnitEditorTreeBrowser(mainPanel.getUnitData(), new UnitTabTreeBrowserBuilder(),
//                    new UnitEditorSettings(), MutableObjectData.WorldEditorDataType.UNITS,
//                    (mdxFilePath, b, c, icon) -> FileUtils.loadStreamMdx(mainPanel, MpqCodebase.get().getResourceAsStream(mdxFilePath), b, c, icon), mainPanel.prefs);
//            return unitEditorTree;
//        }

        new JScrollPane(unitEditorTree);
//        final View view = new View("Data Browser", imageIcon, mpqBrowser);
//        view.getWindowProperties().setCloseEnabled(true);
        return new View("Unit Browser", imageIcon, new JScrollPane(unitEditorTree));
    }

    static View createUnitBrowser(MainPanel mainPanel) {
        return createUnitBrowser(
                mainPanel, new ImageIcon(MainFrame.MAIN_PROGRAM_ICON.getScaledInstance(16, 16, Image.SCALE_FAST)));
    }

    static UnitEditorTree createUnitEditorTree(MainPanel mainPanel) {
        final UnitEditorTree unitEditorTree = new UnitEditorTreeBrowser(mainPanel.getUnitData(), new UnitTabTreeBrowserBuilder(),
                new UnitEditorSettings(), MutableObjectData.WorldEditorDataType.UNITS,
                (mdxFilePath, b, c, icon) -> FileUtils.loadStreamMdx(mainPanel, MpqCodebase.get().getResourceAsStream(mdxFilePath), b, c, icon), mainPanel.prefs);
        return unitEditorTree;
    }
}
