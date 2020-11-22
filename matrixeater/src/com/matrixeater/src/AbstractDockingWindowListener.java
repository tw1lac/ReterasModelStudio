package com.matrixeater.src;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;

public abstract class AbstractDockingWindowListener implements DockingWindowListener {
    @Override
    public void windowAdded(DockingWindow dockingWindow, DockingWindow dockingWindow1) { }

    @Override
    public void windowRemoved(DockingWindow dockingWindow, DockingWindow dockingWindow1) { }

    @Override
    public void windowShown(DockingWindow dockingWindow) {}

    @Override
    public void windowHidden(DockingWindow dockingWindow) {}

    @Override
    public void viewFocusChanged(View view, View view1) {}

    @Override
    public void windowClosing(DockingWindow dockingWindow) throws OperationAbortedException {}

    @Override
    public void windowClosed(DockingWindow dockingWindow) {}

    @Override
    public void windowUndocking(DockingWindow dockingWindow) throws OperationAbortedException {}

    @Override
    public void windowUndocked(DockingWindow dockingWindow) {}

    @Override
    public void windowDocking(DockingWindow dockingWindow) throws OperationAbortedException {}

    @Override
    public void windowDocked(DockingWindow dockingWindow) {}

    @Override
    public void windowMinimizing(DockingWindow dockingWindow) throws OperationAbortedException {}

    @Override
    public void windowMinimized(DockingWindow dockingWindow) {}

    @Override
    public void windowMaximizing(DockingWindow dockingWindow) throws OperationAbortedException {}

    @Override
    public void windowMaximized(DockingWindow dockingWindow) {}

    @Override
    public void windowRestoring(DockingWindow dockingWindow) throws OperationAbortedException {}

    @Override
    public void windowRestored(DockingWindow dockingWindow) {}
}
