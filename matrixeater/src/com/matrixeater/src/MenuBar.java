package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.gui.modeledit.UVPanel;
import com.hiveworkshop.wc3.gui.modeledit.UndoHandler;
import com.hiveworkshop.wc3.mdl.*;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.resources.Resources;
import com.hiveworkshop.wc3.resources.WEString;
import com.hiveworkshop.wc3.units.*;
import com.hiveworkshop.wc3.units.fields.UnitFields;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData;
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
import java.util.Iterator;
import java.util.List;

public class MenuBar implements UndoHandler {

    final MainPanel mainPanel;
    final JMenuBar menuBar;
    UndoMenuItem undo;
    RedoMenuItem redo;

    public MenuBar(MainPanel mainPanel){
        this.mainPanel = mainPanel;
        this.menuBar = createMenuBar(mainPanel);
    }

    static void scaleAnimationsUgg(ModelPanel currentModelPanel, MainLayoutUgg mainLayoutUgg) {
        final AnimationFrame aFrame = new AnimationFrame(currentModelPanel, mainLayoutUgg.editTab.timeSliderPanel::revalidateKeyframeDisplay);
        aFrame.setVisible(true);
    }

    static void nullModelUgg(MainPanel mainPanel) {
        FileUtils.nullModelFile(mainPanel);
        mainPanel.modelPanelUgg.refreshController();
    }

    public JMenuBar createMenuBar(final MainPanel mainPanel) {
        // Create top menu bar
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = createMenuBarMenu("File", menuBar, KeyEvent.VK_F, "Allows the user to open, save, close, and manipulate files.");
        fillFileMenu(mainPanel, fileMenu);

        JMenu editMenu = createMenuBarMenu("Edit", menuBar, KeyEvent.VK_E, "Allows the user to use various tools to edit the currently selected model.");
        fillEditMenu(mainPanel, editMenu);

        mainPanel.toolsMenu = createMenuBarMenu("Tools", menuBar, KeyEvent.VK_T, "Allows the user to use various model editing tools. (You must open a model before you may use this menu.)");
        fillToolsMenu(mainPanel);

        JMenu viewMenu = createMenuBarMenu("View", menuBar, KeyEvent.VK_V, "Allows the user to control view settings.");
//		viewMenu = createMenuBarMenu(menuBar, "View", -1, "Allows the user to control view settings.");
        fillViewMenu(mainPanel, viewMenu);

        JMenu teamColorMenu = createMenuBarMenu("Team Color1", menuBar, -1, "Allows the user to control team color settings.");
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

        mainPanel.windowMenu = createMenuBarMenu("Window", menuBar, KeyEvent.VK_W, "Allows the user to open various windows containing the program features.");
        fillWindowMenu(mainPanel);

        JMenu addMenu = createMenuBarMenu("Add", menuBar, KeyEvent.VK_A, "Allows the user to add new components to the model.");
        fillAddMenu(mainPanel, addMenu);

        JMenu scriptsMenu = createMenuBarMenu("Scripts", menuBar, KeyEvent.VK_A, "Allows the user to execute model edit scripts.");
        fillScriptsMenu(mainPanel, scriptsMenu);

        JMenu aboutMenu = createMenuBarMenu("Help", menuBar, KeyEvent.VK_H);
        fillHelpMenu(mainPanel, aboutMenu);

//        reteraLand(mainPanel);

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            menuBar.getMenu(i).getPopupMenu().setLightWeightPopupEnabled(false);
        }
        return menuBar;
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
        mainPanel.textureModels = createMenuCheckboxItem("Texture Models", viewMenu, e -> mainPanel.prefs.setTextureModels(mainPanel.textureModels.isSelected()),true, KeyEvent.VK_T);

        final JMenuItem newDirectory =  createMenuItem("Change Game Directory", viewMenu, mainPanel, KeyEvent.VK_D, KeyStroke.getKeyStroke("control shift D"));
        newDirectory.setToolTipText("Changes the directory from which to load texture files for the 3D display.");

        viewMenu.add(new JSeparator());

        mainPanel.showVertexModifyControls = createMenuCheckboxItem("Show Viewport Buttons", viewMenu, e -> showVertexModifyControls(mainPanel.modelPanels, mainPanel.prefs, mainPanel.showVertexModifyControls), true, -1);

        viewMenu.add(new JSeparator());

        mainPanel.showNormals = createMenuCheckboxItem("Show Normals", viewMenu, e -> mainPanel.prefs.setShowNormals(mainPanel.showNormals.isSelected()), false, -1);


        JMenu viewMode = createMenuMenu("3D View Mode", -1, viewMenu);
        ButtonGroup viewModes = new ButtonGroup();

        mainPanel.wireframe = new JRadioButtonMenuItem("Wireframe");
        mainPanel.wireframe.addActionListener(e -> MenuBarActionListeners.repainter(mainPanel));
        viewMode.add(mainPanel.wireframe);
        viewModes.add(mainPanel.wireframe);

        mainPanel.solid = new JRadioButtonMenuItem("Solid");
        mainPanel.solid.addActionListener(e -> MenuBarActionListeners.repainter(mainPanel));
        viewMode.add(mainPanel.solid);
        viewModes.add(mainPanel.solid);

        viewModes.setSelected(mainPanel.solid.getModel(), true);
    }

    private static void fillHelpMenu(MainPanel mainPanel, JMenu aboutMenu) {
        createMenuItem("Changelog", aboutMenu, e -> CreditsPanel.showCreditsButtonResponse("changelist.rtf", "Changelog"), KeyEvent.VK_A);

        createMenuItem("About", aboutMenu, e -> CreditsPanel.showCreditsButtonResponse("credits.rtf", "About"), KeyEvent.VK_A);
    }

    private static void fillToolsMenu(MainPanel mainPanel) {
        createMenuItem("View Selected \"Matrices\"", mainPanel.toolsMenu, e -> MainPanelActions.viewMatricesAction(mainPanel), -1);
//		showMatrices = createMenuItem("View Selected \"Matrices\"", KeyEvent.VK_V, toolsMenu, viewMatricesAction);

        createMenuItem("Flip all selected faces", mainPanel.toolsMenu, e -> MainPanelActions.insideOutAction(mainPanel), KeyEvent.VK_I, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));

        createMenuItem("Flip all selected normals", mainPanel.toolsMenu, e -> MainPanelActions.insideOutNormalsAction(mainPanel), -1);

        mainPanel.toolsMenu.add(new JSeparator());

        createMenuItem("Edit UV Mapping", mainPanel.toolsMenu, e -> MenuBarActionListeners.editUVs(mainPanel), KeyEvent.VK_U);

        createMenuItem("Edit Textures", mainPanel.toolsMenu, e -> MenuBarActionListeners.editTextures(mainPanel), KeyEvent.VK_T);

        createMenuItem("Rig Selection", mainPanel.toolsMenu, e-> MainPanelActions.rigAction(mainPanel), KeyEvent.VK_R, KeyStroke.getKeyStroke("control W"));

        JMenu tweaksSubmenu = createMenuMenu("Tweaks", KeyEvent.VK_T, "Allows the user to tweak conversion mistakes.", mainPanel.toolsMenu);

        createMenuItem("Flip All UVs U", tweaksSubmenu, e-> MainPanelActions.flipAllUVsAxisAction(mainPanel, "Flip All UVs U"), KeyEvent.VK_U);

        createMenuItem("Flip All UVs V", tweaksSubmenu, e-> MainPanelActions.flipAllUVsAxisAction(mainPanel, "Flip All UVs V"), -1);
//		flipAllUVsV = createMenuItem("Flip All UVs V", KeyEvent.VK_V, tweaksSubmenu, flipAllUVsVAction);

        createMenuItem("Swap All UVs U for V", tweaksSubmenu, e -> MenuBarActionListeners.inverseAllUVsAction(mainPanel), KeyEvent.VK_S);

        JMenu mirrorSubmenu = createMenuMenu("Mirror", KeyEvent.VK_M, "Allows the user to mirror objects.", mainPanel.toolsMenu);

        createMenuItem("Mirror X", mirrorSubmenu, e -> MainPanelActions.mirrorAxisAction(mainPanel, "Mirror X"), KeyEvent.VK_X);

        createMenuItem("Mirror Y", mirrorSubmenu, e -> MainPanelActions.mirrorAxisAction(mainPanel, "Mirror Y"), KeyEvent.VK_Y);

        createMenuItem("Mirror Z", mirrorSubmenu, e -> MainPanelActions.mirrorAxisAction(mainPanel, "Mirror Z"), KeyEvent.VK_Z);

        mirrorSubmenu.add(new JSeparator());

        mainPanel.mirrorFlip = createMenuCheckboxItem("Automatically flip after mirror (preserves surface)", mirrorSubmenu, mainPanel, false, KeyEvent.VK_A);
    }

    private static void fillWindowMenu(MainPanel mainPanel) {
        final JMenuItem resetViewButton = createMenuItem("Reset Layout", mainPanel.windowMenu, e -> MenuBarActionListeners.resetViewButton(mainPanel), -1);

        final JMenu viewsMenu = createMenuMenu("Views", KeyEvent.VK_V, mainPanel.windowMenu);

        final JMenuItem testItem = new JMenuItem("test");
        testItem.addActionListener(MenuBarActionListeners.testItemAnimationPreviewListener(mainPanel));

//		viewsMenu.add(testItem);

        createMenuItem("Animation Preview", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Animation Preview", () -> mainPanel.mainLayoutUgg.previewView), KeyEvent.VK_A);

        createMenuItem("Animation Controller", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Animation Controller", () -> mainPanel.mainLayoutUgg.animationControllerView), KeyEvent.VK_C);

        createMenuItem("Modeling", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Modeling", () -> mainPanel.mainLayoutUgg.editTab.creatorView), KeyEvent.VK_M);

        final JMenuItem outlinerItem = createMenuItem("Outliner", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Outliner", () -> mainPanel.mainLayoutUgg.editTab.viewportControllerWindowView), KeyEvent.VK_O);

        final JMenuItem perspectiveItem = createMenuItem("Perspective", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Perspective", () -> mainPanel.mainLayoutUgg.editTab.perspectiveView), KeyEvent.VK_P);

        final JMenuItem frontItem = createMenuItem("Front", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Front", () -> mainPanel.mainLayoutUgg.editTab.frontView), KeyEvent.VK_F);

        final JMenuItem sideItem = createMenuItem("Side", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Side", () -> mainPanel.mainLayoutUgg.editTab.leftView), KeyEvent.VK_S);

        final JMenuItem bottomItem = createMenuItem("Bottom", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Bottom", () -> mainPanel.mainLayoutUgg.editTab.bottomView), KeyEvent.VK_B);

        final JMenuItem toolsItem = createMenuItem("Tools", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Tools", () -> mainPanel.mainLayoutUgg.editTab.toolView), KeyEvent.VK_T);

        final JMenuItem contentsItem = createMenuItem("Contents", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Model", () -> mainPanel.mainLayoutUgg.modelDataView), KeyEvent.VK_C);

        final JMenuItem timeItem = createMenuItem("Footer", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Footer", () -> mainPanel.mainLayoutUgg.editTab.timeSliderView), -1);

        final JMenuItem scriptViewItem = createMenuItem("Matrix Eater Script", viewsMenu, new OpenViewAction(mainPanel.rootWindow, "Matrix Eater Script", () -> createMatrixEaterScriptPanel(mainPanel)), KeyEvent.VK_H, KeyStroke.getKeyStroke("control P"));

        final JMenu browsersMenu = createMenuMenu("Browsers", KeyEvent.VK_B, mainPanel.windowMenu);

        createMenuItem("Data Browser", browsersMenu, e -> MenuBarActionListeners.openViewer(mainPanel, MPQBrowser.createMPQBrowser(mainPanel)), KeyEvent.VK_A);

//        createMenuItem("Unit Browser", browsersMenu, MenuBarActionListeners.openUnitViewerUgg(mainPanel), KeyEvent.VK_U);
        createMenuItem("Unit Browser", browsersMenu, e -> MenuBarActionListeners.openViewer(mainPanel, UnitBrowser.createUnitBrowser(mainPanel)), KeyEvent.VK_U);

        final JMenuItem doodadViewer = createMenuItem("Doodad Browser", browsersMenu, e -> MenuBarActionListeners.openDoodadViewer(mainPanel), KeyEvent.VK_D);

        JMenuItem hiveViewer = new JMenuItem("Hive Browser");
        hiveViewer.setMnemonic(KeyEvent.VK_H);
        hiveViewer.addActionListener(e -> MenuBarActionListeners.openHiveViewer(mainPanel.rootWindow));
//		browsersMenu.add(hiveViewer);

        mainPanel.windowMenu.addSeparator();
    }

    private static void fillAddMenu(MainPanel mainPanel, JMenu addMenu) {
        mainPanel.addParticle = createMenuMenu("Particle", KeyEvent.VK_P, addMenu);

        FileUtils.fetchIncludedParticles(mainPanel);

        JMenu animationMenu = createMenuMenu("Animation", KeyEvent.VK_A, addMenu);

        createMenuItem("Rising/Falling Birth/Death", animationMenu, e -> MenuBarActionListeners.riseFallBirth(mainPanel), KeyEvent.VK_R);

        JMenu singleAnimationMenu = createMenuMenu("Single", KeyEvent.VK_S, animationMenu);

        createMenuItem("From File", singleAnimationMenu, e -> FileUtils.animFromFile(mainPanel), KeyEvent.VK_F);

        createMenuItem("From Unit", singleAnimationMenu, e -> FileUtils.animFromUnit(mainPanel), KeyEvent.VK_U);

        createMenuItem("From Model", singleAnimationMenu, e -> FileUtils.animFromModel(mainPanel), KeyEvent.VK_M);

        createMenuItem("From Object", singleAnimationMenu, e -> FileUtils.animFromObject(mainPanel), KeyEvent.VK_O);
    }

    private static void fillScriptsMenu(MainPanel mainPanel, JMenu scriptsMenu) {
        createMenuItem("Oinkerwinkle-Style AnimTransfer", scriptsMenu, e -> MenuBarActionListeners.importScript(), KeyEvent.VK_P, KeyStroke.getKeyStroke("control shift S"));

        createMenuItem("Oinkerwinkle-Style Merge Geoset", scriptsMenu, e -> FileUtils.mergeGeoset(mainPanel), KeyEvent.VK_M, KeyStroke.getKeyStroke("control M"));

        createMenuItem("Edit/delete model components", scriptsMenu, e -> nullModelUgg(mainPanel), KeyEvent.VK_E, KeyStroke.getKeyStroke("control E"));

        JMenuItem exportAnimatedToStaticMesh = createMenuItem("Export Animated to Static Mesh", scriptsMenu, e -> MenuBarActionListeners.exportAnimatedToStaticMesh(mainPanel), KeyEvent.VK_E);

        JMenuItem exportAnimatedFramePNG = createMenuItem("Export Animated Frame PNG", scriptsMenu, e -> MenuBarActionListeners.exportAnimatedFramePNG(mainPanel), KeyEvent.VK_F);

        JMenuItem combineAnims = createMenuItem("Create Back2Back Animation", scriptsMenu, e -> MenuBarActionListeners.combineAnimations(mainPanel), KeyEvent.VK_P);

        createMenuItem("Change Animation Lengths by Scaling", scriptsMenu, e -> scaleAnimationsUgg(mainPanel.currentModelPanel, mainPanel.mainLayoutUgg), KeyEvent.VK_A);

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

        createMenuItem("Unit", fetch, e -> MenuBarActionListeners.fetchUnitButtonResponse(mainPanel), KeyEvent.VK_U, KeyStroke.getKeyStroke("control U"));

        createMenuItem("Model", fetch, e -> MenuBarActionListeners.fetchModelButtonResponse(mainPanel), KeyEvent.VK_M, KeyStroke.getKeyStroke("control M"));

        createMenuItem("Object Editor", fetch, e -> MenuBarActionListeners.fetchObjectButtonResponse(mainPanel), KeyEvent.VK_O, KeyStroke.getKeyStroke("control O"));

        fetch.add(new JSeparator());

        mainPanel.fetchPortraitsToo = createMenuCheckboxItem("Fetch portraits, too!", fetch, e -> mainPanel.prefs.setLoadPortraits(mainPanel.fetchPortraitsToo.isSelected()), true, KeyEvent.VK_P);

        fileMenu.add(new JSeparator());

        JMenu importMenu = createMenuMenu("Import", KeyEvent.VK_I, fileMenu);

        createMenuItem("From File", importMenu, e -> FileUtils.importFromFile(mainPanel), KeyEvent.VK_I, KeyStroke.getKeyStroke("control shift I"));

        createMenuItem("From Unit", importMenu, e -> importUnit(mainPanel), KeyEvent.VK_U, KeyStroke.getKeyStroke("control shift U"));

        createMenuItem("From WC3 Model", importMenu, e -> importGameModel(mainPanel), KeyEvent.VK_M);

        createMenuItem("From Object Editor", importMenu, e -> importGameObject(mainPanel), KeyEvent.VK_O);

        createMenuItem("From Workspace", importMenu, e -> importFromWorkspace(mainPanel), KeyEvent.VK_O);

        createMenuItem("Save", fileMenu, e -> FileUtils.onClickSave(mainPanel), KeyEvent.VK_S, KeyStroke.getKeyStroke("control S"));

        createMenuItem("Save as", fileMenu, e -> FileUtils.onClickSaveAs(mainPanel), KeyEvent.VK_A, KeyStroke.getKeyStroke("control Q"));

        fileMenu.add(new JSeparator());

        createMenuItem("Export Texture", fileMenu, e -> FileUtils.exportTextures(mainPanel), KeyEvent.VK_E);

        fileMenu.add(new JSeparator());

        createMenuItem("Revert", fileMenu, e -> MenuBarActionListeners.revert(mainPanel), -1);

        createMenuItem("Close", fileMenu, e -> onClickClose(mainPanel), KeyEvent.VK_E, KeyStroke.getKeyStroke("control E"));

        fileMenu.add(new JSeparator());

        createMenuItem("Exit", fileMenu, e -> MenuBarActionListeners.exit(mainPanel), KeyEvent.VK_E);
    }

    private void fillEditMenu(MainPanel mainPanel, JMenu editMenu) {
        undo = new UndoMenuItem(mainPanel, "Undo");
        undo.addActionListener(mainPanel.undoAction);
        undo.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        // undo.addMouseListener(this);
        editMenu.add(undo);
        undo.setEnabled(undo.funcEnabled());

        redo = new RedoMenuItem(mainPanel, "Redo");
        redo.addActionListener(mainPanel.redoAction);
        redo.setAccelerator(KeyStroke.getKeyStroke("control Y"));
        // redo.addMouseListener(this);
        editMenu.add(redo);
        redo.setEnabled(redo.funcEnabled());

        editMenu.add(new JSeparator());

        final JMenu optimizeMenu = createMenuMenu("Optimize", KeyEvent.VK_O, editMenu);

        createMenuItem("Linearize Animations", optimizeMenu, e -> MenuBarActionListeners.linearizeAnimations(mainPanel), KeyEvent.VK_L);

        createMenuItem("Simplify Keyframes (Experimental)", optimizeMenu, e -> MenuBarActionListeners.simplifyKeyframesButtonResponse(mainPanel), KeyEvent.VK_K);

        final JMenuItem minimizeGeoset = createMenuItem("Minimize Geosets", optimizeMenu, e -> MenuBarActionListeners.minimizeGeoset(mainPanel), KeyEvent.VK_K);

        createMenuItem("Sort Nodes", optimizeMenu, e -> MenuBarActionListeners.sortBones(mainPanel), KeyEvent.VK_S);

        final JMenuItem flushUnusedTexture = createMenuItem("Flush Unused Texture", optimizeMenu, mainPanel, KeyEvent.VK_F);
        flushUnusedTexture.setEnabled(false);

        final JMenuItem recalcNormals = createMenuItem("Recalculate Normals", editMenu, e -> MainPanelActions.recalculateNormalsAction(mainPanel), -1, KeyStroke.getKeyStroke("control N"));

        final JMenuItem recalcExtents = createMenuItem("Recalculate Extents", editMenu, e -> MainPanelActions.recalculateExtentsAction(mainPanel), -1, KeyStroke.getKeyStroke("control shift E"));

        editMenu.add(new JSeparator());

        mainPanel.cut = createMenuItem( "Cut", editMenu, e -> MenuBarActionListeners.copyActionListener(mainPanel, e), -1, KeyStroke.getKeyStroke("control X"));
        mainPanel.cut.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));

        mainPanel.copy = createMenuItem( "Copy", editMenu, e -> MenuBarActionListeners.copyActionListener(mainPanel, e), -1, KeyStroke.getKeyStroke("control C"));
        mainPanel.copy.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));

        mainPanel.paste = createMenuItem("Paste", editMenu, e -> MenuBarActionListeners.copyActionListener(mainPanel, e), -1, KeyStroke.getKeyStroke("control V"));
        mainPanel.paste.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));

        createMenuItem("Duplicate", editMenu, e -> MainPanelActions.cloneAction(mainPanel), -1, KeyStroke.getKeyStroke("control D"));
//        ModelPanelUgg.duplicateSelection(namePicker, currentModelPanel);
        editMenu.add(new JSeparator());

        createMenuItem("Snap Vertices", editMenu, e -> MainPanelActions.snapVerticesAction(mainPanel), -1, KeyStroke.getKeyStroke("control shift W"));

        createMenuItem("Snap Normals", editMenu, e -> MainPanelActions.snapNormalsAction(mainPanel), -1, KeyStroke.getKeyStroke("control L"));

        editMenu.add(new JSeparator());

        createMenuItem("Select All", editMenu, e -> MainPanelActions.selectAllAction(mainPanel), -1, KeyStroke.getKeyStroke("control A"));

        createMenuItem("Invert Selection", editMenu, e -> MainPanelActions.invertSelectAction(mainPanel), -1, KeyStroke.getKeyStroke("control I"));

        createMenuItem("Expand Selection", editMenu, e -> MainPanelActions.expandSelectionAction(mainPanel), -1, KeyStroke.getKeyStroke("control E"));

        editMenu.addSeparator();

        final JMenuItem deleteButton = createMenuItem("Delete", editMenu, e -> MainPanelActions.deleteAction(mainPanel), KeyEvent.VK_D);

        editMenu.addSeparator();

        createMenuItem("Preferences Window", editMenu, e -> MenuBarActionListeners.openPreferences(mainPanel), KeyEvent.VK_P);
    }


    private static JCheckBoxMenuItem createMenuCheckboxItem(String text, JMenu menu, ActionListener actionListener, boolean setSelected, int keyEvent) {
        JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(text, true);
        checkBoxMenuItem.setMnemonic(KeyEvent.VK_N);
        checkBoxMenuItem.setSelected(setSelected);
        checkBoxMenuItem.setMnemonic(keyEvent);
        checkBoxMenuItem.addActionListener(actionListener);
        menu.add(checkBoxMenuItem);
        return checkBoxMenuItem;
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

                ModelPanelUgg.reloadComponentBrowser(mainPanel.modelPanelUgg.geoControlModelData, mainPanel.currentModelPanel);
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
                    mainPanel.modelPanelUgg.setCurrentModel(mainPanel, mainPanel.modelPanels.get(newIndex));
                } else {
                    // TODO remove from notifiers to fix leaks
//                    System.out.println("(close) no more modelPanel :O");
                    mainPanel.modelPanelUgg.setCurrentModel(mainPanel, null);
                }
            }
        }
    }

    static View createMatrixEaterScriptPanel(MainPanel mainPanel) {
        final JPanel hackerPanel = new JPanel(new BorderLayout());
        final RSyntaxTextArea matrixEaterScriptTextArea = new RSyntaxTextArea(20, 60);
        matrixEaterScriptTextArea.setCodeFoldingEnabled(true);
        matrixEaterScriptTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        hackerPanel.add(new RTextScrollPane(matrixEaterScriptTextArea), BorderLayout.CENTER);
        final JButton run = new JButton("Run",
                new ImageIcon(BLPHandler.get().getGameTex("ReplaceableTextures\\CommandButtons\\BTNReplay-Play.blp")
                        .getScaledInstance(24, 24, Image.SCALE_FAST)));
        run.addActionListener(MenuBarActionListeners.createBtnReplayPlayActionListener(mainPanel, matrixEaterScriptTextArea));
        hackerPanel.add(run, BorderLayout.NORTH);
        return new View("Matrix Eater Script", null, hackerPanel);
    }

    static boolean closeAll(MainPanel mainPanel) {
        boolean success = true;
        final Iterator<ModelPanel> iterator = mainPanel.modelPanels.iterator();
        boolean closedCurrentPanel = false;
        ModelPanel lastUnclosedModelPanel = null;
        while (iterator.hasNext()) {
            final ModelPanel panel = iterator.next();
            if (success = panel.close(mainPanel)) {
                mainPanel.windowMenu.remove(panel.getMenuItem());
                iterator.remove();
                if (panel == mainPanel.currentModelPanel) {
                    closedCurrentPanel = true;
                }
            } else {
                lastUnclosedModelPanel = panel;
                break;
            }
        }
        if (closedCurrentPanel) {
            mainPanel.modelPanelUgg.setCurrentModel(mainPanel, lastUnclosedModelPanel);
        }
        return success;
    }

    static void createCloseContextPopupMenu(MainPanel mainPanel) {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem contextClose = new JMenuItem("Close uggabugga");
        contextClose.addActionListener(mainPanel);
        contextMenu.add(contextClose);

        JMenuItem contextCloseOthers = new JMenuItem("Close Others");
        contextCloseOthers.addActionListener(e -> MainPanel.closeOthers(mainPanel, mainPanel.currentModelPanel));
        contextMenu.add(contextCloseOthers);

        JMenuItem contextCloseAll = new JMenuItem("Close All");
        contextCloseAll.addActionListener(e -> closeAll(mainPanel));
        contextMenu.add(contextCloseAll);
    }

    static void importUnit(MainPanel mainPanel) {
        final GameObject fetchUnitResult = mainPanel.fetchUnit();
        if (fetchUnitResult == null) {
            return;
        }
        final String filepath = convertPathToMDX(fetchUnitResult.getField("file"));
        final EditableModel current = mainPanel.currentMDL();
        if (filepath != null) {
            final File animationSource = MpqCodebase.get().getFile(filepath);
            FileUtils.importFile(mainPanel, animationSource);
        }
        mainPanel.modelPanelUgg.refreshController();
    }

    static void importGameModel(MainPanel mainPanel) {
        final ModelOptionPane.ModelElement fetchModelResult = mainPanel.fetchModel();
        if (fetchModelResult == null) {
            return;
        }
        final String filepath = convertPathToMDX(fetchModelResult.getFilepath());
        final EditableModel current = mainPanel.currentMDL();
        if (filepath != null) {
            final File animationSource = MpqCodebase.get().getFile(filepath);
            FileUtils.importFile(mainPanel, animationSource);
        }
        mainPanel.modelPanelUgg.refreshController();
    }

    static void importGameObject(MainPanel mainPanel) {
        final MutableObjectData.MutableGameObject fetchObjectResult = mainPanel.fetchObject();
        if (fetchObjectResult == null) {
            return;
        }
        final String filepath = convertPathToMDX(fetchObjectResult.getFieldAsString(UnitFields.MODEL_FILE, 0));
        final EditableModel current = mainPanel.currentMDL();
        if (filepath != null) {
            final File animationSource = MpqCodebase.get().getFile(filepath);
            FileUtils.importFile(mainPanel, animationSource);
        }
        mainPanel.modelPanelUgg.refreshController();
    }

    static void importFromWorkspace(MainPanel mainPanel) {
        final List<EditableModel> optionNames = new ArrayList<>();
        for (final ModelPanel modelPanel : mainPanel.modelPanels) {
            final EditableModel model = modelPanel.getModel();
            optionNames.add(model);
        }
        final EditableModel choice = (EditableModel) JOptionPane.showInputDialog(mainPanel,
                "Choose a workspace item to import data from:", "Import from Workspace",
                JOptionPane.OK_CANCEL_OPTION, null, optionNames.toArray(), optionNames.get(0));
        if (choice != null) {
            FileUtils.importFile(mainPanel, EditableModel.deepClone(choice, choice.getHeaderName()));
        }
        mainPanel.modelPanelUgg.refreshController();
    }

    static void showVertexModifyControls(List<ModelPanel> modelPanels, ProgramPreferences prefs, JCheckBoxMenuItem showVertexModifyControls) {
        final boolean selected = showVertexModifyControls.isSelected();
        prefs.setShowVertexModifierControls(selected);
        // SaveProfile.get().setShowViewportButtons(selected);
        for (final ModelPanel panel : modelPanels) {
            panel.getFrontArea().setControlsVisible(selected);
            panel.getBotArea().setControlsVisible(selected);
            panel.getSideArea().setControlsVisible(selected);
            final UVPanel uvPanel = panel.getEditUVPanel();
            if (uvPanel != null) {
                uvPanel.setControlsVisible(selected);
            }
        }
    }

    static String convertPathToMDX(String filepath) {
        if (filepath.endsWith(".mdl")) {
            filepath = filepath.replace(".mdl", ".mdx");
        } else if (!filepath.endsWith(".mdx")) {
            filepath = filepath.concat(".mdx");
        }
        return filepath;
    }

    @Override
    public void refreshUndo() {
        undo.setEnabled(undo.funcEnabled());
        redo.setEnabled(redo.funcEnabled());
    }
}
