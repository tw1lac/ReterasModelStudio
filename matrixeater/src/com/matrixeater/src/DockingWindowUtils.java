package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.mpqbrowser.MPQBrowser;
import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorTree;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

import javax.swing.*;
import java.awt.*;

public class DockingWindowUtils {
    static void traverseAndFix(final DockingWindow window) {
        final boolean tabWindow = window instanceof TabWindow;
        final int childWindowCount = window.getChildWindowCount();
        for (int i = 0; i < childWindowCount; i++) {
            final DockingWindow childWindow = window.getChildWindow(i);
            traverseAndFix(childWindow);
            if (tabWindow && (childWindowCount != 1) && (childWindow instanceof View)) {
                final View view = (View) childWindow;
                view.getViewProperties().getViewTitleBarProperties().setVisible(false);
            }
        }
    }

    static void traverseAndReset(final DockingWindow window) {
        final int childWindowCount = window.getChildWindowCount();
        for (int i = 0; i < childWindowCount; i++) {
            final DockingWindow childWindow = window.getChildWindow(i);
            traverseAndReset(childWindow);
            if (childWindow instanceof View) {
                final View view = (View) childWindow;
                view.getViewProperties().getViewTitleBarProperties().setVisible(true);
            }
        }
    }

    static void traverseAndReloadData(MainPanel mainPanel, final DockingWindow window) {
        final int childWindowCount = window.getChildWindowCount();
        for (int i = 0; i < childWindowCount; i++) {
            final DockingWindow childWindow = window.getChildWindow(i);
            traverseAndReloadData(mainPanel, childWindow);
            if (childWindow instanceof View) {
                final View view = (View) childWindow;
                final Component component = view.getComponent();
                if (component instanceof JScrollPane) {
                    final JScrollPane pane = (JScrollPane) component;
                    final Component viewportView = pane.getViewport().getView();
                    if (viewportView instanceof UnitEditorTree) {
                        final UnitEditorTree unitEditorTree = (UnitEditorTree) viewportView;
                        final MutableObjectData.WorldEditorDataType dataType = unitEditorTree.getDataType();
                        if (dataType == MutableObjectData.WorldEditorDataType.UNITS) {
                            System.out.println("saw unit tree");
                            unitEditorTree.setUnitDataAndReloadVerySlowly(mainPanel.getUnitData());
                        } else if (dataType == MutableObjectData.WorldEditorDataType.DOODADS) {
                            System.out.println("saw doodad tree");
                            unitEditorTree.setUnitDataAndReloadVerySlowly(mainPanel.getDoodadData());
                        }
                    }
                } else if (component instanceof com.hiveworkshop.wc3.gui.mpqbrowser.MPQBrowser) {
                    System.out.println("saw mpq tree");
                    final com.hiveworkshop.wc3.gui.mpqbrowser.MPQBrowser comp = (MPQBrowser) component;
                    comp.refreshTree();
                }
            }
        }
    }
}
