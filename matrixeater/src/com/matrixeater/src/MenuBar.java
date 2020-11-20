package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.mdl.*;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.resources.Resources;
import com.hiveworkshop.wc3.resources.WEString;
import com.hiveworkshop.wc3.units.DataTable;
import com.hiveworkshop.wc3.units.ModelOptionPanel;
import com.hiveworkshop.wc3.units.UnitOptionPanel;
import com.hiveworkshop.wc3.user.SaveProfile;
import com.hiveworkshop.wc3.util.ModelUtils;
import net.infonode.docking.View;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MenuBar {
    public static JMenuBar createMenuBar(final MainPanel mainPanel) {
        // Create top menu bar
        mainPanel.menuBar = new JMenuBar();

        JMenu fileMenu = createMenuBarMenu("File", mainPanel.menuBar, KeyEvent.VK_F, "Allows the user to open, save, close, and manipulate files.");
        fillFileMenu(mainPanel, fileMenu);

        JMenu editMenu = createMenuBarMenu("Edit", mainPanel.menuBar, KeyEvent.VK_E, "Allows the user to use various tools to edit the currently selected model.");
        fillEditMenu(mainPanel, editMenu);

        mainPanel.toolsMenu = createMenuBarMenu("Tools", mainPanel.menuBar, KeyEvent.VK_T, "Allows the user to use various model editing tools. (You must open a model before you may use this menu.)");
        fillToolsMenu(mainPanel);

        JMenu viewMenu = createMenuBarMenu("View", mainPanel.menuBar, KeyEvent.VK_V, "Allows the user to control view settings.");
//		viewMenu = createMenuBarMenu(mainPanel.menuBar, "View", -1, "Allows the user to control view settings.");
        fillViewMenu(mainPanel, viewMenu);

        JMenu teamColorMenu = createMenuBarMenu("Team Color1", mainPanel.menuBar, -1, "Allows the user to control team color settings.");
        System.out.println("currentModelPanel:");
        System.out.println(mainPanel.currentModelPanel);
        createTeamColorMenuItems(mainPanel, teamColorMenu);

        mainPanel.directoryChangeNotifier.subscribe(() -> {
            MpqCodebase.get().refresh(SaveProfile.get().getDataSources());
            // cache priority order...
            UnitOptionPanel.dropRaceCache();
            DataTable.dropCache();
            ModelOptionPanel.dropCache();
            WEString.dropCache();
            Resources.dropCache();
            BLPHandler.get().dropCache();
            teamColorMenu.removeAll();
            createTeamColorMenuItems(mainPanel, teamColorMenu);
            DockingWindowUtils.traverseAndReloadData(mainPanel, mainPanel.rootWindow);
        });

        mainPanel.windowMenu = createMenuBarMenu("Window", mainPanel.menuBar, KeyEvent.VK_W, "Allows the user to open various windows containing the program features.");
        fillWindowMenu(mainPanel);

        JMenu addMenu = createMenuBarMenu("Add", mainPanel.menuBar, KeyEvent.VK_A, "Allows the user to add new components to the model.");
        fillAddMenu(mainPanel, addMenu);

        JMenu scriptsMenu = createMenuBarMenu("Scripts", mainPanel.menuBar, KeyEvent.VK_A, "Allows the user to execute model edit scripts.");
        fillScriptsMenu(mainPanel, scriptsMenu);

        JMenu aboutMenu = createMenuBarMenu("Help", mainPanel.menuBar, KeyEvent.VK_H);
        fillHelpMenu(mainPanel, aboutMenu);

//        reteraLand(mainPanel);

        for (int i = 0; i < mainPanel.menuBar.getMenuCount(); i++) {
            mainPanel.menuBar.getMenu(i).getPopupMenu().setLightWeightPopupEnabled(false);
        }
        return mainPanel.menuBar;
    }

    private static void reteraLand(MainPanel mainPanel) {
        final JMenuItem jokebutton = new JMenuItem("Load Retera Land");
        jokebutton.setMnemonic(KeyEvent.VK_A);
        jokebutton.addActionListener(e -> {
            final StringBuilder sb = new StringBuilder();
            for (final File file : new File(
                    "C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\CustomMapData\\LuaFpsMap\\Maps\\MultiplayerFun004")
                    .listFiles()) {
                if (!file.getName().toLowerCase().endsWith("_init.txt")) {
                    sb.setLength(0);
                    try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("BlzSetAbilityActivatedIcon")) {
                                final int startIndex = line.indexOf('"') + 1;
                                final int endIndex = line.lastIndexOf('"');
                                final String dataString = line.substring(startIndex, endIndex);
                                sb.append(dataString);
                            }
                        }
                    } catch (final IOException e1) {
                        e1.printStackTrace();
                    }
                    final String dataString = sb.toString();
                    for (int i = 0; (i + 23) < dataString.length(); i += 24) {
                        final Geoset geo = new Geoset();
                        mainPanel.currentMDL().addGeoset(geo);
                        geo.setParentModel(mainPanel.currentMDL());
                        geo.setMaterial(new Material(new Layer("Blend", new Bitmap("textures\\white.blp"))));
                        final String data = dataString.substring(i, i + 24);
                        final int x = Integer.parseInt(data.substring(0, 3));
                        final int y = Integer.parseInt(data.substring(3, 6));
                        final int z = Integer.parseInt(data.substring(6, 9));
                        final int sX = Integer.parseInt(data.substring(9, 10));
                        final int sY = Integer.parseInt(data.substring(10, 11));
                        final int sZ = Integer.parseInt(data.substring(11, 12));
                        final int red = Integer.parseInt(data.substring(12, 15));
                        final int green = Integer.parseInt(data.substring(15, 18));
                        final int blue = Integer.parseInt(data.substring(18, 21));
                        final int alpha = Integer.parseInt(data.substring(21, 24));
                        final GeosetAnim forceGetGeosetAnim = geo.forceGetGeosetAnim();
                        forceGetGeosetAnim.setStaticColor(new Vertex(blue / 255.0, green / 255.0, red / 255.0));
                        forceGetGeosetAnim.setStaticAlpha(alpha / 255.0);
                        System.out.println(x + "," + y + "," + z);

                        final ModelUtils.Mesh mesh = ModelUtils.createBox(new Vertex(x * 10, y * 10, z * 10),
                                new Vertex((x * 10) + (sX * 10), (y * 10) + (sY * 10), (z * 10) + (sZ * 10)), 1, 1,
                                1, geo);
                        geo.getVertices().addAll(mesh.getVertices());
                        geo.getTriangles().addAll(mesh.getTriangles());
                    }
                }

            }
            mainPanel.modelStructureChangeListener.geosetsAdded(new ArrayList<>(mainPanel.currentMDL().getGeosets()));
        });
//		scriptsMenu.add(jokebutton);

        final JMenuItem fixReteraLand = new JMenuItem("Fix Retera Land");
        fixReteraLand.setMnemonic(KeyEvent.VK_A);
        fixReteraLand.addActionListener(e -> {
            final EditableModel currentMDL = mainPanel.currentMDL();
            for (final Geoset geo : currentMDL.getGeosets()) {
                final Animation anim = new Animation(new ExtLog(currentMDL.getExtents()));
                geo.add(anim);
            }
        });
//		scriptsMenu.add(fixReteraLand);
    }

    private static void fillViewMenu(MainPanel mainPanel, JMenu viewMenu) {
        mainPanel.textureModels = new JCheckBoxMenuItem("Texture Models", true);
        mainPanel.textureModels.setMnemonic(KeyEvent.VK_T);
        mainPanel.textureModels.setSelected(true);
        mainPanel.textureModels.addActionListener(e -> mainPanel.prefs.setTextureModels(mainPanel.textureModels.isSelected()));
        viewMenu.add(mainPanel.textureModels);

        final JMenuItem newDirectory =  createMenuItem("Change Game Directory", viewMenu, mainPanel, KeyEvent.VK_D, KeyStroke.getKeyStroke("control shift D"));
        newDirectory.setToolTipText("Changes the directory from which to load texture files for the 3D display.");

        viewMenu.add(new JSeparator());

        mainPanel.showVertexModifyControls = new JCheckBoxMenuItem("Show Viewport Buttons", true);
        // showVertexModifyControls.setMnemonic(KeyEvent.VK_V);
        mainPanel.showVertexModifyControls.addActionListener(e -> mainPanel.showVertexModifyControls());
        viewMenu.add(mainPanel.showVertexModifyControls);

        viewMenu.add(new JSeparator());

        mainPanel.showNormals = new JCheckBoxMenuItem("Show Normals", true);
        mainPanel.showNormals.setMnemonic(KeyEvent.VK_N);
        mainPanel.showNormals.setSelected(false);
        mainPanel.showNormals.addActionListener(e -> mainPanel.prefs.setShowNormals(mainPanel.showNormals.isSelected()));
        viewMenu.add(mainPanel.showNormals);

        mainPanel.viewMode = createMenuMenu("3D View Mode", -1, viewMenu);

        mainPanel.viewModes = new ButtonGroup();

        mainPanel.wireframe = new JRadioButtonMenuItem("Wireframe");
        mainPanel.wireframe.addActionListener(MenuBarActionListeners.repainter(mainPanel));
        mainPanel.viewMode.add(mainPanel.wireframe);
        mainPanel.viewModes.add(mainPanel.wireframe);

        mainPanel.solid = new JRadioButtonMenuItem("Solid");
        mainPanel.solid.addActionListener(MenuBarActionListeners.repainter(mainPanel));
        mainPanel.viewMode.add(mainPanel.solid);
        mainPanel.viewModes.add(mainPanel.solid);

        mainPanel.viewModes.setSelected(mainPanel.solid.getModel(), true);
    }

    private static void fillHelpMenu(MainPanel mainPanel, JMenu aboutMenu) {
        createMenuItem("Changelog", aboutMenu, e -> CreditsPanel.showCreditsButtonResponse("changelist.rtf", "Changelog"), KeyEvent.VK_A);

        createMenuItem("About", aboutMenu, e -> CreditsPanel.showCreditsButtonResponse("credits.rtf", "About"), KeyEvent.VK_A);
    }

    private static void fillToolsMenu(MainPanel mainPanel) {
        createMenuItem("View Selected \"Matrices\"", mainPanel.toolsMenu, MainPanelActions.viewMatricesAction(mainPanel), -1);
//		showMatrices = createMenuItem("View Selected \"Matrices\"", KeyEvent.VK_V, toolsMenu, viewMatricesAction);

        createMenuItem("Flip all selected faces", mainPanel.toolsMenu, MainPanelActions.insideOutAction(mainPanel), KeyEvent.VK_I, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));

        createMenuItem("Flip all selected normals", mainPanel.toolsMenu, MainPanelActions.insideOutNormalsAction(mainPanel), -1);

        mainPanel.toolsMenu.add(new JSeparator());

        createMenuItem("Edit UV Mapping", mainPanel.toolsMenu, e -> ModelPanelUgg.editUVs(mainPanel), KeyEvent.VK_U);

        createMenuItem("Edit Textures", mainPanel.toolsMenu, MenuBarActionListeners.editTextures(mainPanel), KeyEvent.VK_T);

        createMenuItem("Rig Selection", mainPanel.toolsMenu, MainPanelActions.rigAction(mainPanel), KeyEvent.VK_R, KeyStroke.getKeyStroke("control W"));

        JMenu tweaksSubmenu = createMenuMenu("Tweaks", KeyEvent.VK_T, "Allows the user to tweak conversion mistakes.", mainPanel.toolsMenu);

        createMenuItem("Flip All UVs U", tweaksSubmenu, MainPanelActions.flipAllUVsAxisAction(mainPanel, "Flip All UVs U"), KeyEvent.VK_U);

        createMenuItem("Flip All UVs V", tweaksSubmenu, MainPanelActions.flipAllUVsAxisAction(mainPanel, "Flip All UVs V"), -1);
//		flipAllUVsV = createMenuItem("Flip All UVs V", KeyEvent.VK_V, tweaksSubmenu, flipAllUVsVAction);

        createMenuItem("Swap All UVs U for V", tweaksSubmenu, MenuBarActionListeners.inverseAllUVsAction(mainPanel), KeyEvent.VK_S);

        JMenu mirrorSubmenu = createMenuMenu("Mirror", KeyEvent.VK_M, "Allows the user to mirror objects.", mainPanel.toolsMenu);

        createMenuItem("Mirror X", mirrorSubmenu, MainPanelActions.mirrorAxisAction(mainPanel, "Mirror X"), KeyEvent.VK_X);

        createMenuItem("Mirror Y", mirrorSubmenu, MainPanelActions.mirrorAxisAction(mainPanel, "Mirror Y"), KeyEvent.VK_Y);

        createMenuItem("Mirror Z", mirrorSubmenu, MainPanelActions.mirrorAxisAction(mainPanel, "Mirror Z"), KeyEvent.VK_Z);

        mirrorSubmenu.add(new JSeparator());

        mainPanel.mirrorFlip = new JCheckBoxMenuItem("Automatically flip after mirror (preserves surface)", true);
        mainPanel.mirrorFlip.setMnemonic(KeyEvent.VK_A);
        mirrorSubmenu.add(mainPanel.mirrorFlip);
    }

    private static void fillWindowMenu(MainPanel mainPanel) {
        final JMenuItem resetViewButton = createMenuItem("Reset Layout", mainPanel.windowMenu, MenuBarActionListeners.resetViewButton(mainPanel), -1);

        final JMenu viewsMenu = createMenuMenu("Views", KeyEvent.VK_V, mainPanel.windowMenu);

        final JMenuItem testItem = new JMenuItem("test");
        testItem.addActionListener(MenuBarActionListeners.testItemAnimationPreviewListener(mainPanel));

//		viewsMenu.add(testItem);

        createMenuItem("Animation Preview", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Animation Preview", () -> mainPanel.previewView), KeyEvent.VK_A);

        createMenuItem("Animation Controller", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Animation Controller", () -> mainPanel.animationControllerView), KeyEvent.VK_C);

        createMenuItem("Modeling", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Modeling", () -> mainPanel.creatorView), KeyEvent.VK_M);

        final JMenuItem outlinerItem = createMenuItem("Outliner", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Outliner", () -> mainPanel.viewportControllerWindowView), KeyEvent.VK_O);

        final JMenuItem perspectiveItem = createMenuItem("Perspective", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Perspective", () -> mainPanel.perspectiveView), KeyEvent.VK_P);

        final JMenuItem frontItem = createMenuItem("Front", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Front", () -> mainPanel.frontView), KeyEvent.VK_F);

        final JMenuItem sideItem = createMenuItem("Side", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Side", () -> mainPanel.leftView), KeyEvent.VK_S);

        final JMenuItem bottomItem = createMenuItem("Bottom", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Bottom", () -> mainPanel.bottomView), KeyEvent.VK_B);

        final JMenuItem toolsItem = createMenuItem("Tools", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Tools", () -> mainPanel.toolView), KeyEvent.VK_T);

        final JMenuItem contentsItem = createMenuItem("Contents", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Model", () -> mainPanel.modelDataView), KeyEvent.VK_C);

        final JMenuItem timeItem = createMenuItem("Footer", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Footer", () -> mainPanel.timeSliderView), -1);

        final JMenuItem hackerViewItem = createMenuItem("Matrix Eater Script", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Matrix Eater Script", () -> mainPanel.hackerView), KeyEvent.VK_H, KeyStroke.getKeyStroke("control P"));

        final JMenu browsersMenu = createMenuMenu("Browsers", KeyEvent.VK_B, mainPanel.windowMenu);

        createMenuItem("Data Browser", browsersMenu, MenuBarActionListeners.openMPQViewer(mainPanel), KeyEvent.VK_A);

        createMenuItem("Unit Browser", browsersMenu, MenuBarActionListeners.openUnitViewer(mainPanel), KeyEvent.VK_U);

        final JMenuItem doodadViewer = createMenuItem("Doodad Browser", browsersMenu, MenuBarActionListeners.openDoodadViewer(mainPanel), KeyEvent.VK_D);

        JMenuItem hiveViewer = new JMenuItem("Hive Browser");
        hiveViewer.setMnemonic(KeyEvent.VK_H);
        hiveViewer.addActionListener(MenuBarActionListeners.openHiveViewer(mainPanel.rootWindow));
//		browsersMenu.add(hiveViewer);

        mainPanel.windowMenu.addSeparator();
    }

    private static void fillAddMenu(MainPanel mainPanel, JMenu addMenu) {
        mainPanel.addParticle = createMenuMenu("Particle", KeyEvent.VK_P, addMenu);

        FileUtils.fetchIncludedParticles(mainPanel);

        JMenu animationMenu = createMenuMenu("Animation", KeyEvent.VK_A, addMenu);

        createMenuItem("Rising/Falling Birth/Death", animationMenu, e -> mainPanel.riseFallBirth(), KeyEvent.VK_R);

        JMenu singleAnimationMenu = createMenuMenu("Single", KeyEvent.VK_S, animationMenu);

        createMenuItem("From File", singleAnimationMenu, e -> FileUtils.animFromFile(mainPanel), KeyEvent.VK_F);

        createMenuItem("From Unit", singleAnimationMenu, e -> FileUtils.animFromUnit(mainPanel), KeyEvent.VK_U);

        createMenuItem("From Model", singleAnimationMenu, e -> FileUtils.animFromModel(mainPanel), KeyEvent.VK_M);

        createMenuItem("From Object", singleAnimationMenu, e -> FileUtils.animFromObject(mainPanel), KeyEvent.VK_O);
    }

    private static void fillScriptsMenu(MainPanel mainPanel, JMenu scriptsMenu) {
        createMenuItem("Oinkerwinkle-Style AnimTransfer", scriptsMenu, e -> mainPanel.importScript(), KeyEvent.VK_P, KeyStroke.getKeyStroke("control shift S"));

        createMenuItem("Oinkerwinkle-Style Merge Geoset", scriptsMenu, e -> FileUtils.mergeGeoset(mainPanel), KeyEvent.VK_M, KeyStroke.getKeyStroke("control M"));

        createMenuItem("Edit/delete model components", scriptsMenu, e -> mainPanel.nullModelUgg(), KeyEvent.VK_E, KeyStroke.getKeyStroke("control E"));

        JMenuItem exportAnimatedToStaticMesh = createMenuItem("Export Animated to Static Mesh", scriptsMenu, MenuBarActionListeners.exportAnimatedToStaticMesh(mainPanel), KeyEvent.VK_E);

        JMenuItem exportAnimatedFramePNG = createMenuItem("Export Animated Frame PNG", scriptsMenu, MenuBarActionListeners.exportAnimatedFramePNG(mainPanel), KeyEvent.VK_F);

        JMenuItem combineAnims = createMenuItem("Create Back2Back Animation", scriptsMenu, MenuBarActionListeners.combineAnimations(mainPanel), KeyEvent.VK_P);

        createMenuItem("Change Animation Lengths by Scaling", scriptsMenu, e -> mainPanel.scaleAnimationsUgg(), KeyEvent.VK_A);

        final JMenuItem version800Toggle = createMenuItem("Assign FormatVersion 800", scriptsMenu, e -> mainPanel.currentMDL().setFormatVersion(800), KeyEvent.VK_A);

        final JMenuItem version1000Toggle = createMenuItem("Assign FormatVersion 1000", scriptsMenu, e -> mainPanel.currentMDL().setFormatVersion(1000), KeyEvent.VK_A);

        final JMenuItem makeItHDItem = createMenuItem("SD -> HD (highly experimental, requires 900 or 1000)", scriptsMenu, e -> EditableModel.makeItHD(mainPanel.currentMDL()), KeyEvent.VK_A);

        final JMenuItem version800EditingToggle = createMenuItem("HD -> SD (highly experimental, becomes 800)", scriptsMenu, e -> EditableModel.convertToV800(1, mainPanel.currentMDL()), KeyEvent.VK_A);

        final JMenuItem recalculateTangents = createMenuItem("Recalculate Tangents (requires 900 or 1000)", scriptsMenu, e -> EditableModel.recalculateTangents(mainPanel.currentMDL(), mainPanel), KeyEvent.VK_A);
    }

    private static void fillFileMenu(MainPanel mainPanel, JMenu fileMenu) {
        createMenuItem("New", fileMenu, e -> NewModelPanel.newModel(mainPanel), KeyEvent.VK_N, KeyStroke.getKeyStroke("control N"));

        createMenuItem("Open", fileMenu, e -> FileUtils.onClickOpen(mainPanel), KeyEvent.VK_O, KeyStroke.getKeyStroke("control O"));

        mainPanel.recentMenu = createMenuMenu("Open Recent", KeyEvent.VK_R, "Allows you to access recently opened files.", fileMenu);
        mainPanel.recentMenu.add(new JSeparator());
//        mainPanel.clearRecent = createMenuItem(mainPanel, "Clear", KeyEvent.VK_C, mainPanel.recentMenu);
        createMenuItem("Clear", mainPanel.recentMenu, e -> MenuBarActionListeners.clearResent(mainPanel), KeyEvent.VK_C);
        mainPanel.updateRecent();

        JMenu fetch = createMenuMenu("Open Internal", KeyEvent.VK_F, fileMenu);

        createMenuItem("Unit", fetch, e -> mainPanel.fetchUnitButtonResponse(), KeyEvent.VK_U, KeyStroke.getKeyStroke("control U"));

        createMenuItem("Model", fetch, e -> mainPanel.fetchModelButtonResponse(), KeyEvent.VK_M, KeyStroke.getKeyStroke("control M"));

        createMenuItem("Object Editor", fetch, e -> mainPanel.fetchObjectButtonResponse(), KeyEvent.VK_O, KeyStroke.getKeyStroke("control O"));

        fetch.add(new JSeparator());

        mainPanel.fetchPortraitsToo = new JCheckBoxMenuItem("Fetch portraits, too!", true);
        mainPanel.fetchPortraitsToo.setMnemonic(KeyEvent.VK_P);
        mainPanel.fetchPortraitsToo.setSelected(true);
        mainPanel.fetchPortraitsToo.addActionListener(e -> mainPanel.prefs.setLoadPortraits(mainPanel.fetchPortraitsToo.isSelected()));
        fetch.add(mainPanel.fetchPortraitsToo);

        fileMenu.add(new JSeparator());

        JMenu importMenu = createMenuMenu("Import", KeyEvent.VK_I, fileMenu);

        createMenuItem("From File", importMenu, e -> FileUtils.importFromFile(mainPanel), KeyEvent.VK_I, KeyStroke.getKeyStroke("control shift I"));

        createMenuItem("From Unit", importMenu, e -> mainPanel.importUnit(), KeyEvent.VK_U, KeyStroke.getKeyStroke("control shift U"));

        createMenuItem("From WC3 Model", importMenu, e -> mainPanel.importGameModel(), KeyEvent.VK_M);

        createMenuItem("From Object Editor", importMenu, e -> mainPanel.importGameObject(), KeyEvent.VK_O);

        createMenuItem("From Workspace", importMenu, e -> mainPanel.importFromWorkspace(), KeyEvent.VK_O);

        createMenuItem("Save", fileMenu, e -> FileUtils.onClickSave(mainPanel), KeyEvent.VK_S, KeyStroke.getKeyStroke("control S"));

        createMenuItem("Save as", fileMenu, e -> FileUtils.onClickSaveAs(mainPanel), KeyEvent.VK_A, KeyStroke.getKeyStroke("control Q"));

        fileMenu.add(new JSeparator());

        createMenuItem("Export Texture", fileMenu, e -> FileUtils.exportTextures(mainPanel), KeyEvent.VK_E);

        fileMenu.add(new JSeparator());

        createMenuItem("Revert", fileMenu, MenuBarActionListeners.revert(mainPanel), -1);

        createMenuItem("Close", fileMenu, e -> onClickClose(mainPanel), KeyEvent.VK_E, KeyStroke.getKeyStroke("control E"));

        fileMenu.add(new JSeparator());

        createMenuItem("Exit", fileMenu, MenuBarActionListeners.exit(mainPanel), KeyEvent.VK_E);
    }

    private static void fillEditMenu(MainPanel mainPanel, JMenu editMenu) {
        mainPanel.undo = new UndoMenuItem(mainPanel, "Undo");
        mainPanel.undo.addActionListener(mainPanel.undoAction);
        mainPanel.undo.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        // undo.addMouseListener(this);
        editMenu.add(mainPanel.undo);
        mainPanel.undo.setEnabled(mainPanel.undo.funcEnabled());

        mainPanel.redo = new RedoMenuItem(mainPanel, "Redo");
        mainPanel.redo.addActionListener(mainPanel.redoAction);
        mainPanel.redo.setAccelerator(KeyStroke.getKeyStroke("control Y"));
        // redo.addMouseListener(this);
        editMenu.add(mainPanel.redo);
        mainPanel.redo.setEnabled(mainPanel.redo.funcEnabled());

        editMenu.add(new JSeparator());

        final JMenu optimizeMenu = createMenuMenu("Optimize", KeyEvent.VK_O, editMenu);

        createMenuItem("Linearize Animations", optimizeMenu, e -> mainPanel.linearizeAnimations(), KeyEvent.VK_L);

        createMenuItem("Simplify Keyframes (Experimental)", optimizeMenu, e -> mainPanel.simplifyKeyframesButtonResponse(), KeyEvent.VK_K);

        final JMenuItem minimizeGeoset = createMenuItem("Minimize Geosets", optimizeMenu, MenuBarActionListeners.minimizeGeoset(mainPanel), KeyEvent.VK_K);

        createMenuItem("Sort Nodes", optimizeMenu, MenuBarActionListeners.sortBones(mainPanel), KeyEvent.VK_S);

        final JMenuItem flushUnusedTexture = createMenuItem("Flush Unused Texture", optimizeMenu, mainPanel, KeyEvent.VK_F);
        flushUnusedTexture.setEnabled(false);

        final JMenuItem recalcNormals = createMenuItem("Recalculate Normals", editMenu, MainPanelActions.recalculateNormalsAction(mainPanel), -1, KeyStroke.getKeyStroke("control N"));

        final JMenuItem recalcExtents = createMenuItem("Recalculate Extents", editMenu, MainPanelActions.recalculateExtentsAction(mainPanel), -1, KeyStroke.getKeyStroke("control shift E"));

        editMenu.add(new JSeparator());

        mainPanel.cut = createMenuItem( "Cut", editMenu, MenuBarActionListeners.copyActionListener(mainPanel), -1, KeyStroke.getKeyStroke("control X"));
        mainPanel.cut.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));

        mainPanel.copy = createMenuItem( "Copy", editMenu, MenuBarActionListeners.copyActionListener(mainPanel), -1, KeyStroke.getKeyStroke("control C"));
        mainPanel.copy.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));

        mainPanel.paste = createMenuItem("Paste", editMenu, MenuBarActionListeners.copyActionListener(mainPanel), -1, KeyStroke.getKeyStroke("control V"));
        mainPanel.paste.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));

        createMenuItem("Duplicate", editMenu, MainPanelActions.cloneAction(mainPanel), -1, KeyStroke.getKeyStroke("control D"));
//        ModelPanelUgg.duplicateSelection(namePicker, currentModelPanel);
        editMenu.add(new JSeparator());

        createMenuItem("Snap Vertices", editMenu, MainPanelActions.snapVerticesAction(mainPanel), -1, KeyStroke.getKeyStroke("control shift W"));

        createMenuItem("Snap Normals", editMenu, MainPanelActions.snapNormalsAction(mainPanel), -1, KeyStroke.getKeyStroke("control L"));

        editMenu.add(new JSeparator());

        createMenuItem("Select All", editMenu, MainPanelActions.selectAllAction(mainPanel), -1, KeyStroke.getKeyStroke("control A"));

        createMenuItem("Invert Selection", editMenu, MainPanelActions.invertSelectAction(mainPanel), -1, KeyStroke.getKeyStroke("control I"));

        createMenuItem("Expand Selection", editMenu, MainPanelActions.expandSelectionAction(mainPanel), -1, KeyStroke.getKeyStroke("control E"));

        editMenu.addSeparator();

        final JMenuItem deleteButton = createMenuItem("Delete", editMenu, MainPanelActions.deleteAction(mainPanel), KeyEvent.VK_D);

        editMenu.addSeparator();

        createMenuItem("Preferences Window", editMenu, MenuBarActionListeners.openPreferences(mainPanel), KeyEvent.VK_P);
    }


    private static JMenu createMenuBarMenu(String menuText, JMenuBar menuBar, int keyEvent) {
        JMenu menu = new JMenu(menuText);
        menu.setMnemonic(keyEvent);
        menuBar.add(menu);
        return menu;
    }

    private static JMenu createMenuBarMenu(String menuText, JMenuBar menuBar, int keyEvent, String description) {
        JMenu menu = new JMenu(menuText);
        menu.setMnemonic(keyEvent);
        menu.getAccessibleContext().setAccessibleDescription(description);
        menuBar.add(menu);
        return menu;
    }

    private static JMenu createMenuMenu(String menuText, int keyEvent, JMenu menuMenu) {
        JMenu menu = new JMenu(menuText);
        menu.setMnemonic(keyEvent);
        menuMenu.add(menu);
        return menu;
    }

    private static JMenu createMenuMenu(String menuText, int keyEvent, String description, JMenu menuMenu) {
        JMenu menu = new JMenu(menuText);
        menu.setMnemonic(keyEvent);
        menu.getAccessibleContext().setAccessibleDescription(description);
        menuMenu.add(menu);
        return menu;
    }

    private static JMenuItem createMenuItem(String itemText, JMenu menu, MainPanel mainPanel, int keyEvent) {
        return createMenuItem(itemText, menu, mainPanel, keyEvent, null);
    }

//    private static JMenuItem createMenuItem(String itemText, JMenu menu, MainPanel mainPanel, int keyEvent, KeyStroke keyStroke) {
//        return createMenuItem(itemText, menu, mainPanel, keyEvent, keyStroke);
//    }

    private static JMenuItem createMenuItem(String itemText, JMenu menu, ActionListener actionListener, int keyEvent) {
        return createMenuItem(itemText, menu, actionListener, keyEvent, null);
    }

    private static JMenuItem createMenuItem(String itemText, JMenu menu, ActionListener actionListener, int keyEvent, KeyStroke keyStroke) {
        JMenuItem menuItem;
        menuItem = new JMenuItem(itemText);
        menuItem.setMnemonic(keyEvent);
        menuItem.setAccelerator(keyStroke);
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        return menuItem;
    }

    static void createTeamColorMenuItems(MainPanel mainPanel, JMenu teamColorMenu) {
        for (int i = 0; i < 25; i++) {
            final String colorNumber = String.format("%2s", i).replace(' ', '0');
            try {
                final String colorName = WEString.getString("WESTRING_UNITCOLOR_" + colorNumber);
                final JMenuItem menuItem = new JMenuItem(colorName, new ImageIcon(BLPHandler.get()
                        .getGameTex("ReplaceableTextures\\TeamColor\\TeamColor" + colorNumber + ".blp")));
                teamColorMenu.add(menuItem);
                final int teamColorValueNumber = i;
                menuItem.addActionListener(setTeamColor(mainPanel, teamColorValueNumber));
            } catch (final Exception ex) {
//                ex.printStackTrace();
                // load failed
                break;
            }
        }
    }

    private static ActionListener setTeamColor(MainPanel mainPanel, int teamColorValueNumber) {
        return e -> {
            Material.teamColor = teamColorValueNumber;

            if (mainPanel.currentModelPanel != null) {
                mainPanel.currentModelPanel.getAnimationViewer().reloadAllTextures();
                mainPanel.currentModelPanel.getPerspArea().reloadAllTextures();

                ModelPanelUgg.reloadComponentBrowser(mainPanel.geoControlModelData, mainPanel.currentModelPanel);
            }
            mainPanel.profile.getPreferences().setTeamColor(teamColorValueNumber);
        };
    }

    static void onClickClose(MainPanel mainPanel) {
//        System.out.println("onClickClose");
        final ModelPanel modelPanel = mainPanel.currentModelPanel;
        final int oldIndex = mainPanel.modelPanels.indexOf(modelPanel);
        if (modelPanel != null) {
            if (modelPanel.close(mainPanel)) {
                mainPanel.modelPanels.remove(modelPanel);
                mainPanel.windowMenu.remove(modelPanel.getMenuItem());
                if (mainPanel.modelPanels.size() > 0) {
                    final int newIndex = Math.min(mainPanel.modelPanels.size() - 1, oldIndex);
                    ModelPanelUgg.setCurrentModel(mainPanel, mainPanel.modelPanels.get(newIndex));
                } else {
                    // TODO remove from notifiers to fix leaks
                    ModelPanelUgg.setCurrentModel(mainPanel, null);
                }
            }
        }
    }
}
