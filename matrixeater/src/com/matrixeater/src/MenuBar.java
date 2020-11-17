package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.gui.modeledit.util.TransferActionListener;
import com.hiveworkshop.wc3.gui.modelviewer.AnimationViewer;
import com.hiveworkshop.wc3.mdl.EventObject;
import com.hiveworkshop.wc3.mdl.*;
import com.hiveworkshop.wc3.mdl.render3d.RenderModel;
import com.hiveworkshop.wc3.mdl.v2.ModelViewManager;
import com.hiveworkshop.wc3.mdl.v2.timelines.InterpolationType;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.resources.Resources;
import com.hiveworkshop.wc3.resources.WEString;
import com.hiveworkshop.wc3.units.DataTable;
import com.hiveworkshop.wc3.units.ModelOptionPanel;
import com.hiveworkshop.wc3.units.UnitOptionPanel;
import com.hiveworkshop.wc3.user.SaveProfile;
import com.hiveworkshop.wc3.util.ModelUtils;
import com.matrixeaterhayate.TextureManager;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.*;

public class MenuBar {
    public static JMenuBar createMenuBar(final MainPanel mainPanel) {
        // Create top menu bar
        mainPanel.menuBar = new JMenuBar();

        mainPanel.fileMenu = createMenuBarMenu(mainPanel.menuBar, "File", KeyEvent.VK_F, "Allows the user to open, save, close, and manipulate files.");
        fillFileMenu(mainPanel);

        mainPanel.editMenu = createMenuBarMenu(mainPanel.menuBar, "Edit", KeyEvent.VK_E, "Allows the user to use various tools to edit the currently selected model.");
        fillEditMenu(mainPanel);

        mainPanel.toolsMenu = createMenuBarMenu(mainPanel.menuBar, "Tools", KeyEvent.VK_T, "Allows the user to use various model editing tools. (You must open a model before you may use this menu.)");
        fillToolsMenu(mainPanel);

        mainPanel.viewMenu = createMenuBarMenu(mainPanel.menuBar, "View", KeyEvent.VK_V, "Allows the user to control view settings.");
//		viewMenu = createMenuBarMenu(mainPanel.menuBar, "View", -1, "Allows the user to control view settings.");
        fillViewMenu(mainPanel);

        mainPanel.teamColorMenu = createMenuBarMenu(mainPanel.menuBar, "Team Color", -1, "Allows the user to control team color settings.");

        mainPanel.directoryChangeNotifier.subscribe(() -> {
            MpqCodebase.get().refresh(SaveProfile.get().getDataSources());
            // cache priority order...
            UnitOptionPanel.dropRaceCache();
            DataTable.dropCache();
            ModelOptionPanel.dropCache();
            WEString.dropCache();
            Resources.dropCache();
            BLPHandler.get().dropCache();
            mainPanel.teamColorMenu.removeAll();
            mainPanel.createTeamColorMenuItems();
            mainPanel.traverseAndReloadData(mainPanel.rootWindow);
        });
        mainPanel.createTeamColorMenuItems();

        mainPanel.windowMenu = createMenuBarMenu(mainPanel.menuBar, "Window", KeyEvent.VK_W, "Allows the user to open various windows containing the program features.");
        fillWindowMenu(mainPanel);

        mainPanel.addMenu = createMenuBarMenu(mainPanel.menuBar, "Add", KeyEvent.VK_A, "Allows the user to add new components to the model.");
        fillAddMenu(mainPanel);

        mainPanel.scriptsMenu = createMenuBarMenu(mainPanel.menuBar, "Scripts", KeyEvent.VK_A, "Allows the user to execute model edit scripts.");
        fillScriptsMenu(mainPanel);

        mainPanel.aboutMenu = createMenuBarMenu(mainPanel.menuBar, "Help", KeyEvent.VK_H);
        fillHelpMenu(mainPanel);

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

    private static void fillViewMenu(MainPanel mainPanel) {
        mainPanel.textureModels = new JCheckBoxMenuItem("Texture Models", true);
        mainPanel.textureModels.setMnemonic(KeyEvent.VK_T);
        mainPanel.textureModels.setSelected(true);
        mainPanel.textureModels.addActionListener(mainPanel);
        mainPanel.viewMenu.add(mainPanel.textureModels);

        mainPanel.newDirectory = createMenuItem(mainPanel, "Change Game Directory", KeyEvent.VK_D, mainPanel.viewMenu, KeyStroke.getKeyStroke("control shift D"));
        mainPanel.newDirectory.setToolTipText("Changes the directory from which to load texture files for the 3D display.");

        mainPanel.viewMenu.add(new JSeparator());

        mainPanel.showVertexModifyControls = new JCheckBoxMenuItem("Show Viewport Buttons", true);
        // showVertexModifyControls.setMnemonic(KeyEvent.VK_V);
        mainPanel.showVertexModifyControls.addActionListener(mainPanel);
        mainPanel.viewMenu.add(mainPanel.showVertexModifyControls);

        mainPanel.viewMenu.add(new JSeparator());

        mainPanel.showNormals = new JCheckBoxMenuItem("Show Normals", true);
        mainPanel.showNormals.setMnemonic(KeyEvent.VK_N);
        mainPanel.showNormals.setSelected(false);
        mainPanel.showNormals.addActionListener(mainPanel);
        mainPanel.viewMenu.add(mainPanel.showNormals);

        mainPanel.viewMode = createMenuMenu("3D View Mode", -1, mainPanel.viewMenu);

        mainPanel.viewModes = new ButtonGroup();

        mainPanel.wireframe = new JRadioButtonMenuItem("Wireframe");
        mainPanel.wireframe.addActionListener(repainter(mainPanel));
        mainPanel.viewMode.add(mainPanel.wireframe);
        mainPanel.viewModes.add(mainPanel.wireframe);

        mainPanel.solid = new JRadioButtonMenuItem("Solid");
        mainPanel.solid.addActionListener(repainter(mainPanel));
        mainPanel.viewMode.add(mainPanel.solid);
        mainPanel.viewModes.add(mainPanel.solid);

        mainPanel.viewModes.setSelected(mainPanel.solid.getModel(), true);
    }

    private static void fillHelpMenu(MainPanel mainPanel) {
        mainPanel.changelogButton = createMenuItem(mainPanel, "Changelog", KeyEvent.VK_A, mainPanel.aboutMenu);

        mainPanel.creditsButton = createMenuItem(mainPanel, "About", KeyEvent.VK_A, mainPanel.aboutMenu);
    }

    private static void fillToolsMenu(MainPanel mainPanel) {
        mainPanel.showMatrices = createMenuItem("View Selected \"Matrices\"", -1, mainPanel.toolsMenu, mainPanel.viewMatricesAction);
//		showMatrices = createMenuItem("View Selected \"Matrices\"", KeyEvent.VK_V, toolsMenu, viewMatricesAction);

        mainPanel.insideOut = createMenuItem("Flip all selected faces", KeyEvent.VK_I, mainPanel.toolsMenu, mainPanel.insideOutAction, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));

        mainPanel.insideOutNormals = createMenuItem("Flip all selected normals", -1, mainPanel.toolsMenu, mainPanel.insideOutNormalsAction);

        mainPanel.toolsMenu.add(new JSeparator());

        mainPanel.editUVs = createMenuItem(mainPanel, "Edit UV Mapping", KeyEvent.VK_U, mainPanel.toolsMenu);

        mainPanel.editTextures = createMenuItem("Edit Textures", KeyEvent.VK_T, mainPanel.toolsMenu, editTextures(mainPanel));

        mainPanel.rigButton = createMenuItem("Rig Selection", KeyEvent.VK_R, mainPanel.toolsMenu, mainPanel.rigAction, KeyStroke.getKeyStroke("control W"));

        mainPanel.tweaksSubmenu = createMenuMenu("Tweaks", KeyEvent.VK_T, "Allows the user to tweak conversion mistakes.", mainPanel.toolsMenu);

        mainPanel.flipAllUVsU = createMenuItem("Flip All UVs U", KeyEvent.VK_U, mainPanel.tweaksSubmenu, mainPanel.flipAllUVsUAction);

        mainPanel.flipAllUVsV = createMenuItem("Flip All UVs V", -1, mainPanel.tweaksSubmenu, mainPanel.flipAllUVsVAction);
//		flipAllUVsV = createMenuItem("Flip All UVs V", KeyEvent.VK_V, tweaksSubmenu, flipAllUVsVAction);

        mainPanel.inverseAllUVs = createMenuItem("Swap All UVs U for V", KeyEvent.VK_S, mainPanel.tweaksSubmenu, mainPanel.inverseAllUVsAction);

        mainPanel.mirrorSubmenu = createMenuMenu("Mirror", KeyEvent.VK_M, "Allows the user to mirror objects.", mainPanel.toolsMenu);

        mainPanel.mirrorX = createMenuItem("Mirror X", KeyEvent.VK_X, mainPanel.mirrorSubmenu, mainPanel.mirrorXAction);

        mainPanel.mirrorY = createMenuItem("Mirror Y", KeyEvent.VK_Y, mainPanel.mirrorSubmenu, mainPanel.mirrorYAction);

        mainPanel.mirrorZ = createMenuItem("Mirror Z", KeyEvent.VK_Z, mainPanel.mirrorSubmenu, mainPanel.mirrorZAction);

        mainPanel.mirrorSubmenu.add(new JSeparator());

        mainPanel.mirrorFlip = new JCheckBoxMenuItem("Automatically flip after mirror (preserves surface)", true);
        mainPanel.mirrorFlip.setMnemonic(KeyEvent.VK_A);
        mainPanel.mirrorSubmenu.add(mainPanel.mirrorFlip);
    }

    private static void fillWindowMenu(MainPanel mainPanel) {
        final JMenuItem resetViewButton = createMenuItem("Reset Layout", -1, mainPanel.windowMenu, resetViewButton(mainPanel));

        final JMenu viewsMenu = createMenuMenu("Views", KeyEvent.VK_V, mainPanel.windowMenu);

        final JMenuItem testItem = new JMenuItem("test");
        testItem.addActionListener(new OpenViewAction(mainPanel.rootWindow, "Animation Preview", () -> {
            final JPanel testPanel = new JPanel();

            for (int i = 0; i < 3; i++) {
//					final ControlledAnimationViewer animationViewer = new ControlledAnimationViewer(
//							currentModelPanel().getModelViewManager(), prefs);
//					animationViewer.setMinimumSize(new Dimension(400, 400));
//					final AnimationController animationController = new AnimationController(
//							currentModelPanel().getModelViewManager(), true, animationViewer);

                final AnimationViewer animationViewer2 = new AnimationViewer(
                        mainPanel.currentModelPanel().getModelViewManager(), mainPanel.prefs, false);
                animationViewer2.setMinimumSize(new Dimension(400, 400));
                testPanel.add(animationViewer2);
//					testPanel.add(animationController);
            }
            testPanel.setLayout(new GridLayout(1, 4));
            return new View("Test", null, testPanel);
        }));

//		viewsMenu.add(testItem);

        mainPanel.animationViewer = createMenuItem("Animation Preview", KeyEvent.VK_A, viewsMenu, mainPanel.openAnimationViewerAction);

        mainPanel.animationController = createMenuItem("Animation Controller", KeyEvent.VK_C, viewsMenu, mainPanel.openAnimationControllerAction);

        mainPanel.modelingTab = createMenuItem("Modeling", KeyEvent.VK_M, viewsMenu, mainPanel.openModelingTabAction);

        final JMenuItem outlinerItem = createMenuItem("Outliner", KeyEvent.VK_O, viewsMenu, mainPanel.openOutlinerAction);

        final JMenuItem perspectiveItem = createMenuItem("Perspective", KeyEvent.VK_P, viewsMenu, mainPanel.openPerspectiveAction);

        final JMenuItem frontItem = createMenuItem("Front", KeyEvent.VK_F, viewsMenu, mainPanel.openFrontAction);

        final JMenuItem sideItem = createMenuItem("Side", KeyEvent.VK_S, viewsMenu, mainPanel.openSideAction);

        final JMenuItem bottomItem = createMenuItem("Bottom", KeyEvent.VK_B, viewsMenu, mainPanel.openBottomAction);

        final JMenuItem toolsItem = createMenuItem("Tools", KeyEvent.VK_T, viewsMenu, mainPanel.openToolsAction);

        final JMenuItem contentsItem = createMenuItem("Contents", KeyEvent.VK_C, viewsMenu, mainPanel.openModelDataContentsViewAction);

        final JMenuItem timeItem = createMenuItem("Footer", -1, viewsMenu, mainPanel.openTimeSliderAction);

        final JMenuItem hackerViewItem = createMenuItem("Matrix Eater Script", KeyEvent.VK_H, viewsMenu, mainPanel.hackerViewAction, KeyStroke.getKeyStroke("control P"));

        final JMenu browsersMenu = createMenuMenu("Browsers", KeyEvent.VK_B, mainPanel.windowMenu);

        mainPanel.mpqViewer = createMenuItem("Data Browser", KeyEvent.VK_A, browsersMenu, mainPanel.openMPQViewerAction);

        mainPanel.unitViewer = createMenuItem("Unit Browser", KeyEvent.VK_U, browsersMenu, mainPanel.openUnitViewerAction);

        final JMenuItem doodadViewer = createMenuItem("Doodad Browser", KeyEvent.VK_D, browsersMenu, mainPanel.openDoodadViewerAction);

        mainPanel.hiveViewer = new JMenuItem("Hive Browser");
        mainPanel.hiveViewer.setMnemonic(KeyEvent.VK_H);
        mainPanel.hiveViewer.addActionListener(mainPanel.openHiveViewerAction);
//		browsersMenu.add(hiveViewer);

        mainPanel.windowMenu.addSeparator();
    }

    private static void fillAddMenu(MainPanel mainPanel) {
        mainPanel.addParticle = createMenuMenu("Particle", KeyEvent.VK_P, mainPanel.addMenu);

        FileUtils.fetchIncludedParticles(mainPanel);

        mainPanel.animationMenu = createMenuMenu("Animation", KeyEvent.VK_A, mainPanel.addMenu);

        mainPanel.riseFallBirth = createMenuItem(mainPanel, "Rising/Falling Birth/Death", KeyEvent.VK_R, mainPanel.animationMenu);

        mainPanel.singleAnimationMenu = createMenuMenu("Single", KeyEvent.VK_S, mainPanel.animationMenu);

        mainPanel.animFromFile = createMenuItem(mainPanel, "From File", KeyEvent.VK_F, mainPanel.singleAnimationMenu);

        mainPanel.animFromUnit = createMenuItem(mainPanel, "From Unit", KeyEvent.VK_U, mainPanel.singleAnimationMenu);

        mainPanel.animFromModel = createMenuItem(mainPanel, "From Model", KeyEvent.VK_M, mainPanel.singleAnimationMenu);

        mainPanel.animFromObject = createMenuItem(mainPanel, "From Object", KeyEvent.VK_O, mainPanel.singleAnimationMenu);
    }

    private static void fillScriptsMenu(MainPanel mainPanel) {
        mainPanel.importButtonScript = createMenuItem(mainPanel, "Oinkerwinkle-Style AnimTransfer", KeyEvent.VK_P, mainPanel.scriptsMenu, KeyStroke.getKeyStroke("control shift S"));

        mainPanel.mergeGeoset = createMenuItem(mainPanel, "Oinkerwinkle-Style Merge Geoset", KeyEvent.VK_M, mainPanel.scriptsMenu, KeyStroke.getKeyStroke("control M"));

        mainPanel.nullmodelButton = createMenuItem(mainPanel, "Edit/delete model components", KeyEvent.VK_E, mainPanel.scriptsMenu, KeyStroke.getKeyStroke("control E"));

        JMenuItem exportAnimatedToStaticMesh = createMenuItem("Export Animated to Static Mesh", KeyEvent.VK_E, mainPanel.scriptsMenu, exportAnimatedToStaticMesh(mainPanel));

        JMenuItem exportAnimatedFramePNG = createMenuItem("Export Animated Frame PNG", KeyEvent.VK_F, mainPanel.scriptsMenu, exportAnimatedFramePNG(mainPanel));

        JMenuItem combineAnims = createMenuItem("Create Back2Back Animation", KeyEvent.VK_P, mainPanel.scriptsMenu, combineAnimations(mainPanel));

        mainPanel.scaleAnimations = createMenuItem(mainPanel, "Change Animation Lengths by Scaling", KeyEvent.VK_A, mainPanel.scriptsMenu);

        final JMenuItem version800Toggle = createMenuItem("Assign FormatVersion 800", KeyEvent.VK_A, mainPanel.scriptsMenu, e -> mainPanel.currentMDL().setFormatVersion(800));
        ;

        final JMenuItem version1000Toggle = createMenuItem("Assign FormatVersion 1000", KeyEvent.VK_A, mainPanel.scriptsMenu, e -> mainPanel.currentMDL().setFormatVersion(1000));

        final JMenuItem makeItHDItem = createMenuItem("SD -> HD (highly experimental, requires 900 or 1000)", KeyEvent.VK_A, mainPanel.scriptsMenu, e -> EditableModel.makeItHD(mainPanel.currentMDL()));

        final JMenuItem version800EditingToggle = createMenuItem("HD -> SD (highly experimental, becomes 800)", KeyEvent.VK_A, mainPanel.scriptsMenu, e -> EditableModel.convertToV800(1, mainPanel.currentMDL()));

        final JMenuItem recalculateTangents = createMenuItem("Recalculate Tangents (requires 900 or 1000)", KeyEvent.VK_A, mainPanel.scriptsMenu, e -> EditableModel.recalculateTangents(mainPanel.currentMDL(), mainPanel));
    }

    private static void fillFileMenu(MainPanel mainPanel) {
        mainPanel.newModel = createMenuItem(mainPanel, "New", KeyEvent.VK_N, mainPanel.fileMenu, KeyStroke.getKeyStroke("control N"));

        mainPanel.open = createMenuItem(mainPanel, "Open", KeyEvent.VK_O, mainPanel.fileMenu, KeyStroke.getKeyStroke("control O"));

        mainPanel.recentMenu = createMenuMenu("Open Recent", KeyEvent.VK_R, "Allows you to access recently opened files.", mainPanel.fileMenu);
        mainPanel.recentMenu.add(new JSeparator());
        mainPanel.clearRecent = createMenuItem(mainPanel, "Clear", KeyEvent.VK_C, mainPanel.recentMenu);
        mainPanel.updateRecent();

        mainPanel.fetch = createMenuMenu("Open Internal", KeyEvent.VK_F, mainPanel.fileMenu);

        mainPanel.fetchUnit = createMenuItem(mainPanel, "Unit", KeyEvent.VK_U, mainPanel.fetch, KeyStroke.getKeyStroke("control U"));

        mainPanel.fetchModel = createMenuItem(mainPanel, "Model", KeyEvent.VK_M, mainPanel.fetch, KeyStroke.getKeyStroke("control M"));

        mainPanel.fetchObject = createMenuItem(mainPanel, "Object Editor", KeyEvent.VK_O, mainPanel.fetch, KeyStroke.getKeyStroke("control O"));

        mainPanel.fetch.add(new JSeparator());

        mainPanel.fetchPortraitsToo = new JCheckBoxMenuItem("Fetch portraits, too!", true);
        mainPanel.fetchPortraitsToo.setMnemonic(KeyEvent.VK_P);
        mainPanel.fetchPortraitsToo.setSelected(true);
        mainPanel.fetchPortraitsToo.addActionListener(e -> mainPanel.prefs.setLoadPortraits(mainPanel.fetchPortraitsToo.isSelected()));
        mainPanel.fetch.add(mainPanel.fetchPortraitsToo);

        mainPanel.fileMenu.add(new JSeparator());

        mainPanel.importMenu = createMenuMenu("Import", KeyEvent.VK_I, mainPanel.fileMenu);

        mainPanel.importButton = createMenuItem(mainPanel, "From File", KeyEvent.VK_I, mainPanel.importMenu, KeyStroke.getKeyStroke("control shift I"));

        mainPanel.importUnit = createMenuItem(mainPanel, "From Unit", KeyEvent.VK_U, mainPanel.importMenu, KeyStroke.getKeyStroke("control shift U"));

        mainPanel.importGameModel = createMenuItem(mainPanel, "From WC3 Model", KeyEvent.VK_M, mainPanel.importMenu);

        mainPanel.importGameObject = createMenuItem(mainPanel, "From Object Editor", KeyEvent.VK_O, mainPanel.importMenu);

        mainPanel.importFromWorkspace = createMenuItem(mainPanel, "From Workspace", KeyEvent.VK_O, mainPanel.importMenu);

        mainPanel.save = createMenuItem(mainPanel, "Save", KeyEvent.VK_S, mainPanel.fileMenu, KeyStroke.getKeyStroke("control S"));

        mainPanel.saveAs = createMenuItem(mainPanel, "Save as", KeyEvent.VK_A, mainPanel.fileMenu, KeyStroke.getKeyStroke("control Q"));

        mainPanel.fileMenu.add(new JSeparator());

        mainPanel.exportTextures = createMenuItem(mainPanel, "Export Texture", KeyEvent.VK_E, mainPanel.fileMenu);

        mainPanel.fileMenu.add(new JSeparator());

        mainPanel.revert = createMenuItem("Revert", -1, mainPanel.fileMenu, revert(mainPanel));

        mainPanel.close = createMenuItem(mainPanel, "Close", KeyEvent.VK_E, mainPanel.fileMenu, KeyStroke.getKeyStroke("control E"));

        mainPanel.fileMenu.add(new JSeparator());

        mainPanel.exit = createMenuItem("Exit", KeyEvent.VK_E, mainPanel.fileMenu, exit(mainPanel));
    }

    private static void fillEditMenu(MainPanel mainPanel) {
        mainPanel.undo = new UndoMenuItem(mainPanel, "Undo");
        mainPanel.undo.addActionListener(mainPanel.undoAction);
        mainPanel.undo.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        // undo.addMouseListener(this);
        mainPanel.editMenu.add(mainPanel.undo);
        mainPanel.undo.setEnabled(mainPanel.undo.funcEnabled());

        mainPanel.redo = new RedoMenuItem(mainPanel, "Redo");
        mainPanel.redo.addActionListener(mainPanel.redoAction);
        mainPanel.redo.setAccelerator(KeyStroke.getKeyStroke("control Y"));
        // redo.addMouseListener(this);
        mainPanel.editMenu.add(mainPanel.redo);
        mainPanel.redo.setEnabled(mainPanel.redo.funcEnabled());

        mainPanel.editMenu.add(new JSeparator());

        final JMenu optimizeMenu = createMenuMenu("Optimize", KeyEvent.VK_O, mainPanel.editMenu);

        mainPanel.linearizeAnimations = createMenuItem(mainPanel, "Linearize Animations", KeyEvent.VK_L, optimizeMenu);

        mainPanel.simplifyKeyframes = createMenuItem(mainPanel, "Simplify Keyframes (Experimental)", KeyEvent.VK_K, optimizeMenu);

        final JMenuItem minimizeGeoset = createMenuItem("Minimize Geosets", KeyEvent.VK_K, optimizeMenu, minimizeGeoset(mainPanel));

        mainPanel.sortBones = createMenuItem("Sort Nodes", KeyEvent.VK_S, optimizeMenu, sortBones(mainPanel));

        final JMenuItem flushUnusedTexture = createMenuItem(mainPanel, "Flush Unused Texture", KeyEvent.VK_F, optimizeMenu);
        flushUnusedTexture.setEnabled(false);

        final JMenuItem recalcNormals = createMenuItem("Recalculate Normals", -1, mainPanel.editMenu, mainPanel.recalcNormalsAction, KeyStroke.getKeyStroke("control N"));

        final JMenuItem recalcExtents = createMenuItem("Recalculate Extents", -1, mainPanel.editMenu, mainPanel.recalcExtentsAction, KeyStroke.getKeyStroke("control shift E"));

        mainPanel.editMenu.add(new JSeparator());

        mainPanel.cut = createMenuItem( "Cut", -1, mainPanel.editMenu, copyActionListener(mainPanel), KeyStroke.getKeyStroke("control X"));
        mainPanel.cut.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));

        mainPanel.copy = createMenuItem( "Copy", -1, mainPanel.editMenu, copyActionListener(mainPanel), KeyStroke.getKeyStroke("control C"));
        mainPanel.copy.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));

        mainPanel.paste = createMenuItem("Paste", -1, mainPanel.editMenu, copyActionListener(mainPanel), KeyStroke.getKeyStroke("control V"));
        mainPanel.paste.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));

        mainPanel.duplicateSelection = createMenuItem("Duplicate", -1, mainPanel.editMenu, mainPanel.cloneAction, KeyStroke.getKeyStroke("control D"));

        mainPanel.editMenu.add(new JSeparator());

        mainPanel.snapVertices = createMenuItem("Snap Vertices", -1, mainPanel.editMenu, mainPanel.snapVerticesAction, KeyStroke.getKeyStroke("control shift W"));

        mainPanel.snapNormals = createMenuItem("Snap Normals", -1, mainPanel.editMenu, mainPanel.snapNormalsAction, KeyStroke.getKeyStroke("control L"));

        mainPanel.editMenu.add(new JSeparator());

        mainPanel.selectAll = createMenuItem("Select All", -1, mainPanel.editMenu, mainPanel.selectAllAction, KeyStroke.getKeyStroke("control A"));

        mainPanel.invertSelect = createMenuItem("Invert Selection", -1, mainPanel.editMenu, mainPanel.invertSelectAction, KeyStroke.getKeyStroke("control I"));

        mainPanel.expandSelection = createMenuItem("Expand Selection", -1, mainPanel.editMenu, mainPanel.expandSelectionAction, KeyStroke.getKeyStroke("control E"));

        mainPanel.editMenu.addSeparator();

        final JMenuItem deleteButton = createMenuItem("Delete", KeyEvent.VK_D, mainPanel.editMenu, mainPanel.deleteAction);

        mainPanel.editMenu.addSeparator();

        mainPanel.preferencesWindow = createMenuItem("Preferences Window", KeyEvent.VK_P, mainPanel.editMenu, mainPanel.openPreferencesAction);
    }

    private static ActionListener repainter(MainPanel mainPanel) {
        return e -> {
            if (mainPanel.wireframe.isSelected()) {
                mainPanel.prefs.setViewMode(0);
            } else if (mainPanel.solid.isSelected()) {
                mainPanel.prefs.setViewMode(1);
            } else {
                mainPanel.prefs.setViewMode(-1);
            }
            mainPanel.repaint();
        };
    }

    private static ActionListener copyActionListener(MainPanel mainPanel) {
        return e -> {
            final TransferActionListener transferActionListener = new TransferActionListener();
            if (!mainPanel.animationModeState) {
                transferActionListener.actionPerformed(e);
            } else {
                if (e.getActionCommand().equals(TransferHandler.getCutAction().getValue(Action.NAME))) {
                    mainPanel.timeSliderPanel.cut();
                } else if (e.getActionCommand().equals(TransferHandler.getCopyAction().getValue(Action.NAME))) {
                    mainPanel.timeSliderPanel.copy();
                } else if (e.getActionCommand().equals(TransferHandler.getPasteAction().getValue(Action.NAME))) {
                    mainPanel.timeSliderPanel.paste();
                }
            }
        };
    }

    private static ActionListener sortBones(MainPanel mainPanel) {
        return e -> {
            final EditableModel model = mainPanel.currentMDL();
            final List<IdObject> roots = new ArrayList<>();
            final ArrayList<IdObject> modelList = model.getIdObjects();
            for (final IdObject object : modelList) {
                if (object.getParent() == null) {
                    roots.add(object);
                }
            }
            final Queue<IdObject> bfsQueue = new LinkedList<>(roots);
            final List<IdObject> result = new ArrayList<>();
            while (!bfsQueue.isEmpty()) {
                final IdObject nextItem = bfsQueue.poll();
                bfsQueue.addAll(nextItem.getChildrenNodes());
                result.add(nextItem);
            }
            for (final IdObject node : result) {
                model.remove(node);
            }
            mainPanel.modelStructureChangeListener.nodesRemoved(result);
            for (final IdObject node : result) {
                model.add(node);
            }
            mainPanel.modelStructureChangeListener.nodesAdded(result);
        };
    }

    private static ActionListener minimizeGeoset(MainPanel mainPanel) {
        return new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int confirm = JOptionPane.showConfirmDialog(mainPanel,
                        "This is experimental and I did not code the Undo option for it yet. Continue?\nMy advice is to click cancel and save once first.",
                        "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.OK_OPTION) {
                    return;
                }

                mainPanel.currentMDL().doSavePreps();

                final Map<Geoset, Geoset> sourceToDestination = new HashMap<>();
                final List<Geoset> retainedGeosets = new ArrayList<>();
                for (final Geoset geoset : mainPanel.currentMDL().getGeosets()) {
                    boolean alreadyRetained = false;
                    for (final Geoset retainedGeoset : retainedGeosets) {
                        if (retainedGeoset.getMaterial().equals(geoset.getMaterial())
                                && (retainedGeoset.getSelectionGroup() == geoset.getSelectionGroup())
                                && (retainedGeoset.getFlags().contains("Unselectable") == geoset.getFlags()
                                .contains("Unselectable"))
                                && mergableGeosetAnims(retainedGeoset.getGeosetAnim(), geoset.getGeosetAnim())) {
                            alreadyRetained = true;
                            for (final GeosetVertex gv : geoset.getVertices()) {
                                retainedGeoset.add(gv);
                            }
                            for (final Triangle t : geoset.getTriangles()) {
                                retainedGeoset.add(t);
                            }
                            break;
                        }
                    }
                    if (!alreadyRetained) {
                        retainedGeosets.add(geoset);
                    }
                }
                final EditableModel currentMDL = mainPanel.currentMDL();
                final ArrayList<Geoset> geosets = currentMDL.getGeosets();
                final List<Geoset> geosetsRemoved = new ArrayList<>();
                final Iterator<Geoset> iterator = geosets.iterator();
                while (iterator.hasNext()) {
                    final Geoset geoset = iterator.next();
                    if (!retainedGeosets.contains(geoset)) {
                        iterator.remove();
                        final GeosetAnim geosetAnim = geoset.getGeosetAnim();
                        if (geosetAnim != null) {
                            currentMDL.remove(geosetAnim);
                        }
                        geosetsRemoved.add(geoset);
                    }
                }
                mainPanel.modelStructureChangeListener.geosetsRemoved(geosetsRemoved);
            }

            private boolean mergableGeosetAnims(final GeosetAnim first, final GeosetAnim second) {
                if ((first == null) && (second == null)) {
                    return true;
                }
                if ((first == null) || (second == null)) {
                    return false;
                }
                final AnimFlag firstVisibilityFlag = first.getVisibilityFlag();
                final AnimFlag secondVisibilityFlag = second.getVisibilityFlag();
                if ((firstVisibilityFlag == null) != (secondVisibilityFlag == null)) {
                    return false;
                }
                if ((firstVisibilityFlag != null) && !firstVisibilityFlag.equals(secondVisibilityFlag)) {
                    return false;
                }
                if (first.isDropShadow() != second.isDropShadow()) {
                    return false;
                }
                if (Math.abs(first.getStaticAlpha() - second.getStaticAlpha()) > 0.001) {
                    return false;
                }
                if ((first.getStaticColor() == null) != (second.getStaticColor() == null)) {
                    return false;
                }
                if ((first.getStaticColor() != null) && !first.getStaticColor().equalLocs(second.getStaticColor())) {
                    return false;
                }
                final AnimFlag firstAnimatedColor = AnimFlag.find(first.getAnimFlags(), "Color");
                final AnimFlag secondAnimatedColor = AnimFlag.find(second.getAnimFlags(), "Color");
                if ((firstAnimatedColor == null) != (secondAnimatedColor == null)) {
                    return false;
                }
                if ((firstAnimatedColor != null) && !firstAnimatedColor.equals(secondAnimatedColor)) {
                    return false;
                }
                return true;
            }
        };
    }

    private static ActionListener exit(MainPanel mainPanel) {
        return e -> {
            if (mainPanel.closeAll()) {
                MainFrame.frame.dispose();
            }
        };
    }

    private static ActionListener revert(MainPanel mainPanel) {
        return e -> {
            final ModelPanel modelPanel = mainPanel.currentModelPanel();
            final int oldIndex = mainPanel.modelPanels.indexOf(modelPanel);
            if (modelPanel != null) {
                if (modelPanel.close(mainPanel)) {
                    mainPanel.modelPanels.remove(modelPanel);
                    mainPanel.windowMenu.remove(modelPanel.getMenuItem());
                    if (mainPanel.modelPanels.size() > 0) {
                        final int newIndex = Math.min(mainPanel.modelPanels.size() - 1, oldIndex);
                        mainPanel.setCurrentModel(mainPanel.modelPanels.get(newIndex));
                    } else {
                        // TODO remove from notifiers to fix leaks
                        mainPanel.setCurrentModel(null);
                    }
                    final File fileToRevert = modelPanel.getModel().getFile();
                    FileUtils.loadFile(mainPanel, fileToRevert);
                }
            }
        };
    }

    private static ActionListener editTextures(MainPanel mainPanel) {
        return e -> {
            final TextureManager textureManager = new TextureManager(mainPanel.currentModelPanel().getModelViewManager(),
                    mainPanel.modelStructureChangeListener, mainPanel.textureExporter);
            final JFrame frame = new JFrame("Edit Textures");
            textureManager.setSize(new Dimension(800, 650));
            frame.setContentPane(textureManager);
            frame.setSize(textureManager.getSize());
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        };
    }

    private static ActionListener resetViewButton(MainPanel mainPanel) {
        return e -> {
            mainPanel.traverseAndReset(mainPanel.rootWindow);
            final TabWindow startupTabWindow = mainPanel.createMainLayout();
            mainPanel.rootWindow.setWindow(startupTabWindow);
            mainPanel.traverseAndFix(mainPanel.rootWindow);
        };
    }

    private static ActionListener exportAnimatedToStaticMesh(MainPanel mainPanel) {
        return e -> {
            if (!mainPanel.animationModeState) {
                JOptionPane.showMessageDialog(mainPanel, "You must be in the Animation Editor to use that!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final Vector4f vertexHeap = new Vector4f();
            final Vector4f appliedVertexHeap = new Vector4f();
            final Vector4f vertexSumHeap = new Vector4f();
            final Vector4f normalHeap = new Vector4f();
            final Vector4f appliedNormalHeap = new Vector4f();
            final Vector4f normalSumHeap = new Vector4f();
            final ModelPanel modelContext = mainPanel.currentModelPanel();
            final RenderModel editorRenderModel = modelContext.getEditorRenderModel();
            final EditableModel model = modelContext.getModel();
            final ModelViewManager modelViewManager = modelContext.getModelViewManager();
            final EditableModel snapshotModel = EditableModel.deepClone(model, model.getHeaderName() + "At"
                    + editorRenderModel.getAnimatedRenderEnvironment().getAnimationTime());
            for (int geosetIndex = 0; geosetIndex < snapshotModel.getGeosets().size(); geosetIndex++) {
                final Geoset geoset = model.getGeoset(geosetIndex);
                final Geoset snapshotGeoset = snapshotModel.getGeoset(geosetIndex);
                for (int vertexIndex = 0; vertexIndex < geoset.getVertices().size(); vertexIndex++) {
                    final GeosetVertex vertex = geoset.getVertex(vertexIndex);
                    final GeosetVertex snapshotVertex = snapshotGeoset.getVertex(vertexIndex);
                    final List<Bone> bones = vertex.getBones();
                    vertexHeap.x = (float) vertex.x;
                    vertexHeap.y = (float) vertex.y;
                    vertexHeap.z = (float) vertex.z;
                    vertexHeap.w = 1;
                    if (bones.size() > 0) {
                        vertexSumHeap.set(0, 0, 0, 0);
                        for (final Bone bone : bones) {
                            Matrix4f.transform(editorRenderModel.getRenderNode(bone).getWorldMatrix(), vertexHeap,
                                    appliedVertexHeap);
                            Vector4f.add(vertexSumHeap, appliedVertexHeap, vertexSumHeap);
                        }
                        final int boneCount = bones.size();
                        vertexSumHeap.x /= boneCount;
                        vertexSumHeap.y /= boneCount;
                        vertexSumHeap.z /= boneCount;
                        vertexSumHeap.w /= boneCount;
                    } else {
                        vertexSumHeap.set(vertexHeap);
                    }
                    snapshotVertex.x = vertexSumHeap.x;
                    snapshotVertex.y = vertexSumHeap.y;
                    snapshotVertex.z = vertexSumHeap.z;

                    normalHeap.x = (float) vertex.getNormal().x;
                    normalHeap.y = (float) vertex.getNormal().y;
                    normalHeap.z = (float) vertex.getNormal().z;
                    normalHeap.w = 0;
                    if (bones.size() > 0) {
                        normalSumHeap.set(0, 0, 0, 0);
                        for (final Bone bone : bones) {
                            Matrix4f.transform(editorRenderModel.getRenderNode(bone).getWorldMatrix(), normalHeap,
                                    appliedNormalHeap);
                            Vector4f.add(normalSumHeap, appliedNormalHeap, normalSumHeap);
                        }

                        if (normalSumHeap.length() > 0) {
                            normalSumHeap.normalise();
                        } else {
                            normalSumHeap.set(0, 1, 0, 0);
                        }
                    } else {
                        normalSumHeap.set(normalHeap);
                    }
                    snapshotVertex.getNormal().x = normalSumHeap.x;
                    snapshotVertex.getNormal().y = normalSumHeap.y;
                    snapshotVertex.getNormal().z = normalSumHeap.z;
                }
            }
            snapshotModel.getIdObjects().clear();
            final Bone boneRoot = new Bone("Bone_Root");
            boneRoot.setPivotPoint(new Vertex(0, 0, 0));
            snapshotModel.add(boneRoot);
            for (final Geoset geoset : snapshotModel.getGeosets()) {
                for (final GeosetVertex vertex : geoset.getVertices()) {
                    vertex.getBones().clear();
                    vertex.getBones().add(boneRoot);
                }
            }
            final Iterator<Geoset> geosetIterator = snapshotModel.getGeosets().iterator();
            while (geosetIterator.hasNext()) {
                final Geoset geoset = geosetIterator.next();
                final GeosetAnim geosetAnim = geoset.getGeosetAnim();
                if (geosetAnim != null) {
                    final Object visibilityValue = geosetAnim.getVisibilityFlag()
                            .interpolateAt(editorRenderModel.getAnimatedRenderEnvironment());
                    if (visibilityValue instanceof Double) {
                        final Double visibility = (Double) visibilityValue;
                        final double visvalue = visibility;
                        if (visvalue < 0.01) {
                            geosetIterator.remove();
                            snapshotModel.remove(geosetAnim);
                        }
                    }

                }
            }
            snapshotModel.getAnims().clear();
            snapshotModel.add(new Animation("Stand", 333, 1333));
            final List<AnimFlag> allAnimFlags = snapshotModel.getAllAnimFlags();
            for (final AnimFlag flag : allAnimFlags) {
                if (!flag.hasGlobalSeq()) {
                    if (flag.size() > 0) {
                        final Object value = flag.interpolateAt(mainPanel.animatedRenderEnvironment);
                        flag.setInterpType(InterpolationType.DONT_INTERP);
                        flag.getValues().clear();
                        flag.getTimes().clear();
                        flag.getInTans().clear();
                        flag.getOutTans().clear();
                        flag.addEntry(333, value);
                    }
                }
            }
            mainPanel.fc.setDialogTitle("Export Static Snapshot");
            final int result = mainPanel.fc.showSaveDialog(mainPanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = mainPanel.fc.getSelectedFile();
                if (selectedFile != null) {
                    if (!selectedFile.getPath().toLowerCase().endsWith(".mdx")) {
                        selectedFile = new File(selectedFile.getPath() + ".mdx");
                    }
                    snapshotModel.printTo(selectedFile);
                }
            }

        };
    }

    private static ActionListener exportAnimatedFramePNG(MainPanel mainPanel) {
        return e -> {
            final BufferedImage fBufferedImage = mainPanel.currentModelPanel().getAnimationViewer().getBufferedImage();

            if (mainPanel.exportTextureDialog.getCurrentDirectory() == null) {
                final EditableModel current = mainPanel.currentMDL();
                if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
                    mainPanel.fc.setCurrentDirectory(current.getFile().getParentFile());
                } else if (mainPanel.profile.getPath() != null) {
                    mainPanel.fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
                }
            }
            if (mainPanel.exportTextureDialog.getCurrentDirectory() == null) {
                mainPanel.exportTextureDialog
                        .setSelectedFile(new File(mainPanel.exportTextureDialog.getCurrentDirectory() + File.separator));
            }

            final int x = mainPanel.exportTextureDialog.showSaveDialog(mainPanel);
            if (x == JFileChooser.APPROVE_OPTION) {
                final File file = mainPanel.exportTextureDialog.getSelectedFile();
                if (file != null) {
                    try {
                        if (file.getName().lastIndexOf('.') >= 0) {
                            BufferedImage bufferedImage = fBufferedImage;
                            String fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1)
                                    .toUpperCase();
                            if (fileExtension.equals("BMP") || fileExtension.equals("JPG")
                                    || fileExtension.equals("JPEG")) {
                                JOptionPane.showMessageDialog(mainPanel,
                                        "Warning: Alpha channel was converted to black. Some data will be lost\nif you convert this texture back to Warcraft BLP.");
                                bufferedImage = BLPHandler.removeAlphaChannel(bufferedImage);
                            }
                            if (fileExtension.equals("BLP")) {
                                fileExtension = "blp";
                            }
                            final boolean write = ImageIO.write(bufferedImage, fileExtension, file);
                            if (!write) {
                                JOptionPane.showMessageDialog(mainPanel, "File type unknown or unavailable");
                            }
                        } else {
                            JOptionPane.showMessageDialog(mainPanel, "No file type was specified");
                        }
                    } catch (final Exception e1) {
                        ExceptionPopup.display(e1);
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "No output file was specified");
                }
            }
        };
    }

    private static ActionListener combineAnimations(MainPanel mainPanel) {
        return e -> {
            final ArrayList<Animation> anims = mainPanel.currentMDL().getAnims();
            final Animation[] array = anims.toArray(new Animation[0]);
            final Object choice = JOptionPane.showInputDialog(mainPanel, "Pick the first animation",
                    "Choose 1st Anim", JOptionPane.PLAIN_MESSAGE, null, array, array[0]);
            final Animation animation = (Animation) choice;

            final Object choice2 = JOptionPane.showInputDialog(mainPanel, "Pick the second animation",
                    "Choose 2nd Anim", JOptionPane.PLAIN_MESSAGE, null, array, array[0]);
            final Animation animation2 = (Animation) choice2;

            final String nameChoice = JOptionPane.showInputDialog(mainPanel,
                    "What should the combined animation be called?");
            if (nameChoice != null) {
                final int anim1Length = animation.getEnd() - animation.getStart();
                final int anim2Length = animation2.getEnd() - animation2.getStart();
                final int totalLength = anim1Length + anim2Length;

                final EditableModel model = mainPanel.currentMDL();
                final int animTrackEnd = model.animTrackEnd();
                final int start = animTrackEnd + 1000;
                animation.copyToInterval(start, start + anim1Length, model.getAllAnimFlags(),
                        model.sortedIdObjects(EventObject.class));
                animation2.copyToInterval(start + anim1Length, start + totalLength, model.getAllAnimFlags(),
                        model.sortedIdObjects(EventObject.class));

                final Animation newAnimation = new Animation(nameChoice, start, start + totalLength);
                model.add(newAnimation);
                newAnimation.getTags().add("NonLooping");
                newAnimation.setExtents(new ExtLog(animation.getExtents()));
                JOptionPane.showMessageDialog(mainPanel,
                        "DONE! Made a combined animation called " + newAnimation.getName(), "Success",
                        JOptionPane.PLAIN_MESSAGE);
            }
        };
    }

    private static JMenu createMenuBarMenu(JMenuBar menuBar, String menuText, int keyEvent) {
        JMenu menu = new JMenu(menuText);
        menu.setMnemonic(keyEvent);
        menuBar.add(menu);
        return menu;
    }

    private static JMenu createMenuBarMenu(JMenuBar menuBar, String menuText, int keyEvent, String description) {
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

    private static JMenuItem createMenuItem(MainPanel mainPanel, String itemText, int keyEvent, JMenu menu) {
        return createMenuItem(itemText, keyEvent, menu, mainPanel, null);
    }

    private static JMenuItem createMenuItem(MainPanel mainPanel, String itemText, int keyEvent, JMenu menu, KeyStroke keyStroke) {
        return createMenuItem(itemText, keyEvent, menu, mainPanel, keyStroke);
    }

    private static JMenuItem createMenuItem(String itemText, int keyEvent, JMenu menu, ActionListener actionListener) {
        return createMenuItem(itemText, keyEvent, menu, actionListener, null);
    }

    private static JMenuItem createMenuItem(String itemText, int keyEvent, JMenu menu, ActionListener actionListener, KeyStroke keyStroke) {
        JMenuItem menuItem;
        menuItem = new JMenuItem(itemText);
        menuItem.setMnemonic(keyEvent);
        menuItem.setAccelerator(keyStroke);
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        return menuItem;
    }
}
