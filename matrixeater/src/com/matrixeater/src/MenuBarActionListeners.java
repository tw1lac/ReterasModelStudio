package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.datachooser.DataSourceDescriptor;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.gui.modeledit.PerspDisplayPanel;
import com.hiveworkshop.wc3.gui.modeledit.ProgramPreferencesPanel;
import com.hiveworkshop.wc3.gui.modeledit.UVPanel;
import com.hiveworkshop.wc3.gui.modeledit.util.TransferActionListener;
import com.hiveworkshop.wc3.gui.modelviewer.AnimationViewer;
import com.hiveworkshop.wc3.jworldedit.objects.DoodadTabTreeBrowserBuilder;
import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorSettings;
import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorTree;
import com.hiveworkshop.wc3.mdl.EventObject;
import com.hiveworkshop.wc3.mdl.*;
import com.hiveworkshop.wc3.mdl.render3d.RenderModel;
import com.hiveworkshop.wc3.mdl.v2.ModelView;
import com.hiveworkshop.wc3.mdl.v2.ModelViewManager;
import com.hiveworkshop.wc3.mdl.v2.timelines.InterpolationType;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.units.GameObject;
import com.hiveworkshop.wc3.units.ModelOptionPane;
import com.hiveworkshop.wc3.units.fields.UnitFields;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData;
import com.hiveworkshop.wc3.units.objectdata.War3ID;
import com.hiveworkshop.wc3.user.SaveProfile;
import com.hiveworkshop.wc3.user.WarcraftDataSourceChangeListener;
import com.hiveworkshop.wc3.util.ModelUtils;
import com.matrixeater.imp.AnimationTransfer;
import com.matrixeaterhayate.TextureManager;
import net.infonode.docking.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.*;

public class MenuBarActionListeners {
    public static final ImageIcon AnimIcon = new ImageIcon(MainPanel.class.getResource("ImageBin/Anim.png"));
    static final ImageIcon MDLIcon = new ImageIcon(MainPanel.class.getResource("ImageBin/MDLIcon_16.png"));

    static void inverseAllUVsAction (MainPanel mainPanel) {
        for (final Geoset geo : mainPanel.currentMDL().getGeosets()) {
            for (final UVLayer layer : geo.getUVLayers()) {
                for (int i = 0; i < layer.numTVerteces(); i++) {
                    final TVertex tvert = layer.getTVertex(i);
                    final double temp = tvert.x;
                    tvert.x = tvert.y;
                    tvert.y = temp;
                }
            }
        }
        mainPanel.repaint();
    }

    static void repainter(MainPanel mainPanel) {
        if (mainPanel.wireframe.isSelected()) {
            mainPanel.prefs.setViewMode(0);
        } else if (mainPanel.solid.isSelected()) {
            mainPanel.prefs.setViewMode(1);
        } else {
            mainPanel.prefs.setViewMode(-1);
        }
        mainPanel.repaint();
    }

    static void copyActionListener(MainPanel mainPanel, ActionEvent e) {
        final TransferActionListener transferActionListener = new TransferActionListener();
        if (!mainPanel.animationModeState) {
            transferActionListener.actionPerformed(e);
        } else {
            if (e.getActionCommand().equals(TransferHandler.getCutAction().getValue(Action.NAME))) {
                mainPanel.mainLayoutUgg.editTab.timeSliderPanel.cut();
            } else if (e.getActionCommand().equals(TransferHandler.getCopyAction().getValue(Action.NAME))) {
                mainPanel.mainLayoutUgg.editTab.timeSliderPanel.copy();
            } else if (e.getActionCommand().equals(TransferHandler.getPasteAction().getValue(Action.NAME))) {
                mainPanel.mainLayoutUgg.editTab.timeSliderPanel.paste();
            }
        }
    }

    static void sortBones(MainPanel mainPanel) {
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
    }

    static void minimizeGeoset(MainPanel mainPanel) {
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

    private static boolean mergableGeosetAnims(final GeosetAnim first, final GeosetAnim second) {
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
        return (firstAnimatedColor == null) || firstAnimatedColor.equals(secondAnimatedColor);
    }

    static void exit(MainPanel mainPanel) {
        if (MenuBar.closeAll(mainPanel, mainPanel.menuBar.windowMenu)) {
            MainFrame.frame.dispose();
        }
    }

    static void revert(MainPanel mainPanel) {
        final ModelPanel modelPanel = mainPanel.currentModelPanel;
        final int oldIndex = mainPanel.modelPanels.indexOf(modelPanel);
        if (modelPanel != null) {
            if (modelPanel.close(mainPanel)) {
                mainPanel.modelPanels.remove(modelPanel);
                mainPanel.menuBar.windowMenu.remove(modelPanel.getMenuItem());
                if (mainPanel.modelPanels.size() > 0) {
                    final int newIndex = Math.min(mainPanel.modelPanels.size() - 1, oldIndex);
                    mainPanel.modelPanelUgg.setCurrentModel(mainPanel, mainPanel.modelPanels.get(newIndex));
                } else {
                    // TODO remove from notifiers to fix leaks
                    mainPanel.modelPanelUgg.setCurrentModel(mainPanel, null);
                }
                final File fileToRevert = modelPanel.getModel().getFile();
                FileUtils.loadFile(mainPanel, fileToRevert);
            }
        }
    }

    static void editTextures(MainPanel mainPanel) {
        final TextureManager textureManager = new TextureManager(mainPanel.currentModelPanel.getModelViewManager(),
                mainPanel.modelStructureChangeListener, mainPanel.textureExporter);
        final JFrame frame = new JFrame("Edit Textures");
        textureManager.setSize(new Dimension(800, 650));
        frame.setContentPane(textureManager);
        frame.setSize(textureManager.getSize());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    static void resetViewButton(MainPanel mainPanel) {
        DockingWindowUtils.traverseAndReset(mainPanel.rootWindow);
        final TabWindow startupTabWindow = mainPanel.mainLayoutUgg.createMainLayout(mainPanel);
        mainPanel.rootWindow.setWindow(startupTabWindow);
        DockingWindowUtils.traverseAndFix(mainPanel.rootWindow);
    }

    static void exportAnimatedToStaticMesh(MainPanel mainPanel) {
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
        final ModelPanel modelContext = mainPanel.currentModelPanel;
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
        FileUtils.fc.setDialogTitle("Export Static Snapshot");
        final int result = FileUtils.fc.showSaveDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = FileUtils.fc.getSelectedFile();
            if (selectedFile != null) {
                if (!selectedFile.getPath().toLowerCase().endsWith(".mdx")) {
                    selectedFile = new File(selectedFile.getPath() + ".mdx");
                }
                snapshotModel.printTo(selectedFile);
            }
        }
    }

    static void exportAnimatedFramePNG(MainPanel mainPanel) {
        final BufferedImage fBufferedImage = mainPanel.currentModelPanel.getAnimationViewer().getBufferedImage();

        if (FileUtils.exportTextureDialog.getCurrentDirectory() == null) {
            final EditableModel current = mainPanel.currentMDL();
            if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
                FileUtils.fc.setCurrentDirectory(current.getFile().getParentFile());
            } else if (mainPanel.profile.getPath() != null) {
                FileUtils.fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
            }
        }
        if (FileUtils.exportTextureDialog.getCurrentDirectory() == null) {
            FileUtils.exportTextureDialog
                    .setSelectedFile(new File(FileUtils.exportTextureDialog.getCurrentDirectory() + File.separator));
        }

        final int x = FileUtils.exportTextureDialog.showSaveDialog(mainPanel);
        if (x == JFileChooser.APPROVE_OPTION) {
            final File file = FileUtils.exportTextureDialog.getSelectedFile();
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
    }

    static void combineAnimations(MainPanel mainPanel) {
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
    }

    static void openHiveViewer(final RootWindow rootWindow) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(BorderLayout.BEFORE_FIRST_LINE, new JLabel(MainPanel.POWERED_BY_HIVE));
        // final JPanel resourceFilters = new JPanel();
        // resourceFilters.setBorder(BorderFactory.createTitledBorder("Resource
        // Filters"));
        // panel.add(BorderLayout.BEFORE_LINE_BEGINS, resourceFilters);
        // resourceFilters.add(new JLabel("Resource Type"));
        // resourceFilters.add(new JComboBox<>(new String[] { "Any" }));
        final JList<String> view = new JList<>(
                new String[]{"Bongo Bongo (Phantom Shadow Beast)", "Other Model", "Other Model"});
        view.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(final JList<?> list, final Object value,
                                                          final int index, final boolean isSelected, final boolean cellHasFocus) {
                final Component listCellRendererComponent = super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
                final ImageIcon icon = new ImageIcon(MainPanel.class.getResource("ImageBin/deleteme.png"));
                setIcon(new ImageIcon(icon.getImage().getScaledInstance(48, 32, Image.SCALE_DEFAULT)));
                return listCellRendererComponent;
            }
        });
        panel.add(BorderLayout.BEFORE_LINE_BEGINS, new JScrollPane(view));

        final JPanel tags = new JPanel();
        tags.setBorder(BorderFactory.createTitledBorder("Tags"));
        tags.setLayout(new GridLayout(30, 1));
        tags.add(new JCheckBox("Results must include all selected tags"));
        tags.add(new JSeparator());
        tags.add(new JLabel("Types (Models)"));
        tags.add(new JSeparator());
        tags.add(new JCheckBox("Building"));
        tags.add(new JCheckBox("Doodad"));
        tags.add(new JCheckBox("Item"));
        tags.add(new JCheckBox("User Interface"));
        panel.add(BorderLayout.CENTER, tags);
        // final FloatingWindow floatingWindow =
        // rootWindow.createFloatingWindow(rootWindow.getLocation(),
        // mpqBrowser.getPreferredSize(),
        // new View("MPQ Browser",
        // new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16,
        // Image.SCALE_FAST)),
        // mpqBrowser));
        // floatingWindow.getTopLevelAncestor().setVisible(true);
        rootWindow.setWindow(new SplitWindow(true, 0.75f, rootWindow.getWindow(), new View("Hive Browser",
                new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)), panel)));
    }

    static void openDoodadViewer(MainPanel mainPanel) {
        final UnitEditorTree unitEditorTree = new UnitEditorTree(mainPanel.getDoodadData(), new DoodadTabTreeBrowserBuilder(),
                new UnitEditorSettings(), MutableObjectData.WorldEditorDataType.DOODADS);
        unitEditorTree.selectFirstUnit();
        // final FloatingWindow floatingWindow =
        // rootWindow.createFloatingWindow(rootWindow.getLocation(),
        // mpqBrowser.getPreferredSize(),
        // new View("MPQ Browser",
        // new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16,
        // Image.SCALE_FAST)),
        // mpqBrowser));
        // floatingWindow.getTopLevelAncestor().setVisible(true);
        unitEditorTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                try {
                    if (e.getClickCount() >= 2) {
                        final TreePath currentUnitTreePath = unitEditorTree.getSelectionPath();
                        if (currentUnitTreePath != null) {
                            final DefaultMutableTreeNode o = (DefaultMutableTreeNode) currentUnitTreePath
                                    .getLastPathComponent();
                            if (o.getUserObject() instanceof MutableObjectData.MutableGameObject) {
                                final MutableObjectData.MutableGameObject obj = (MutableObjectData.MutableGameObject) o.getUserObject();
                                final int numberOfVariations = obj.getFieldAsInteger(War3ID.fromString("dvar"), 0);
                                if (numberOfVariations > 1) {
                                    for (int i = 0; i < numberOfVariations; i++) {
                                        final String path = MenuBar.convertPathToMDX(
                                                obj.getFieldAsString(War3ID.fromString("dfil"), 0) + i + ".mdl");
                                        final String portrait = ModelUtils.getPortrait(path);
                                        final ImageIcon icon = new ImageIcon(com.hiveworkshop.wc3.util.IconUtils
                                                .getIcon(obj, MutableObjectData.WorldEditorDataType.DOODADS)
                                                .getScaledInstance(16, 16, Image.SCALE_DEFAULT));

                                        System.out.println(path);
                                        FileUtils.loadStreamMdx(mainPanel, MpqCodebase.get().getResourceAsStream(path), true, i == 0,
                                                icon);
                                        if (mainPanel.prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
                                            FileUtils.loadStreamMdx(mainPanel, MpqCodebase.get().getResourceAsStream(portrait), true,
                                                    false, icon);
                                        }
                                    }
                                } else {
                                    final String path = MenuBar.convertPathToMDX(
                                            obj.getFieldAsString(War3ID.fromString("dfil"), 0));
                                    final String portrait = ModelUtils.getPortrait(path);
                                    final ImageIcon icon = new ImageIcon(com.hiveworkshop.wc3.util.IconUtils
                                            .getIcon(obj, MutableObjectData.WorldEditorDataType.DOODADS)
                                            .getScaledInstance(16, 16, Image.SCALE_DEFAULT));
                                    System.out.println(path);
                                    FileUtils.loadStreamMdx(mainPanel, MpqCodebase.get().getResourceAsStream(path), true, true, icon);
                                    if (mainPanel.prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
                                        FileUtils.loadStreamMdx(mainPanel, MpqCodebase.get().getResourceAsStream(portrait), true, false,
                                                icon);
                                    }
                                }
                                mainPanel.menuBar.toolsMenu.getAccessibleContext().setAccessibleDescription(
                                        "Allows the user to control which parts of the model are displayed for editing.");
                                mainPanel.menuBar.toolsMenu.setEnabled(true);
                            }
                        }
                    }
                } catch (final Exception exc) {
                    exc.printStackTrace();
                    ExceptionPopup.display(exc);
                }
            }
        });
        mainPanel.rootWindow.setWindow(new SplitWindow(true, 0.75f, mainPanel.rootWindow.getWindow(),
                new View("Doodad Browser",
                        new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)),
                        new JScrollPane(unitEditorTree))));
    }

    static void openPreferences(MainPanel mainPanel) {
        final ProgramPreferences programPreferences = new ProgramPreferences();
        programPreferences.loadFrom(mainPanel.prefs);
        final List<DataSourceDescriptor> priorDataSources = SaveProfile.get().getDataSources();
        final ProgramPreferencesPanel programPreferencesPanel = new ProgramPreferencesPanel(programPreferences,
                priorDataSources);
        final int ret = JOptionPane.showConfirmDialog(mainPanel, programPreferencesPanel, "Preferences",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ret == JOptionPane.OK_OPTION) {
            mainPanel.prefs.loadFrom(programPreferences);
            final List<DataSourceDescriptor> dataSources = programPreferencesPanel.getDataSources();
            final boolean changedDataSources = (dataSources != null) && !dataSources.equals(priorDataSources);
            if (changedDataSources) {
                SaveProfile.get().setDataSources(dataSources);
            }
            SaveProfile.save();
            if (changedDataSources) {
                dataSourcesChanged(mainPanel.directoryChangeNotifier, mainPanel.modelPanels);
            }
            mainPanel.updateUIFromProgramPreferences();
        }
    }

    static void openViewer(MainPanel mainPanel, View browser) {
        //                final UnitEditorTree unitEditorTree = UnitBrowser.createUnitEditorTree(mainPanel);
        mainPanel.rootWindow.setWindow(new SplitWindow(true, 0.75f, mainPanel.rootWindow.getWindow(), browser));
//                        new View("Unit Browser",
//                                new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)),
//                                new JScrollPane(unitEditorTree))));
    }

    static void clearResent(MainPanel mainPanel) {
        final int dialogResult = JOptionPane.showConfirmDialog(mainPanel,
                "Are you sure you want to clear the Recent history?", "Confirm Clear",
                JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            SaveProfile.get().clearRecent();
            mainPanel.updateRecent();
        }
    }

    static OpenViewAction testItemAnimationPreviewListener(MainPanel mainPanel) {
        return new OpenViewAction(mainPanel.rootWindow, "Animation Preview", () -> {
            final JPanel testPanel = new JPanel();

            for (int i = 0; i < 3; i++) {
//					final ControlledAnimationViewer animationViewer = new ControlledAnimationViewer(
//							currentModelPanel().getModelViewManager(), prefs);
//					animationViewer.setMinimumSize(new Dimension(400, 400));
//					final AnimationController animationController = new AnimationController(
//							currentModelPanel().getModelViewManager(), true, animationViewer);

                final AnimationViewer animationViewer2 = new AnimationViewer(
                        mainPanel.currentModelPanel.getModelViewManager(), mainPanel.prefs, false);
                animationViewer2.setMinimumSize(new Dimension(400, 400));
                testPanel.add(animationViewer2);
//					testPanel.add(animationController);
            }
            testPanel.setLayout(new GridLayout(1, 4));
            return new View("Test", null, testPanel);
        });
    }

    static ActionListener createBtnReplayPlayActionListener(final MainPanel mainPanel, RSyntaxTextArea matrixEaterScriptTextArea) {
        return new ActionListener() {
            final ScriptEngineManager factory = new ScriptEngineManager();

            @Override
            public void actionPerformed(final ActionEvent e) {
                final String text = matrixEaterScriptTextArea.getText();
                final ScriptEngine engine = factory.getEngineByName("JavaScript");
                final ModelPanel modelPanel = mainPanel.currentModelPanel;
                if (modelPanel != null) {
                    engine.put("modelPanel", modelPanel);
                    engine.put("model", modelPanel.getModel());
                    engine.put("world", mainPanel);
                    try {
                        engine.eval(text);
                    } catch (final ScriptException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(mainPanel, e1.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Must open a file!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    static void editUVs(MainPanel mainPanel) {
        final ModelPanel disp = mainPanel.currentModelPanel;
        if (disp.getEditUVPanel() == null) {
            final UVPanel panel = new UVPanel(disp, mainPanel.prefs, mainPanel.modelStructureChangeListener);
            disp.setEditUVPanel(panel);

            panel.initViewport();
            final FloatingWindow floatingWindow = mainPanel.rootWindow.createFloatingWindow(
                    new Point(mainPanel.getX() + (mainPanel.getWidth() / 2), mainPanel.getY() + (mainPanel.getHeight() / 2)), panel.getSize(),
                    panel.getView());
            panel.init();
            floatingWindow.getTopLevelAncestor().setVisible(true);
            panel.packFrame();
        } else if (!disp.getEditUVPanel().frameVisible()) {
            final FloatingWindow floatingWindow = mainPanel.rootWindow.createFloatingWindow(
                    new Point(mainPanel.getX() + (mainPanel.getWidth() / 2), mainPanel.getY() + (mainPanel.getHeight() / 2)),
                    disp.getEditUVPanel().getSize(), disp.getEditUVPanel().getView());
            floatingWindow.getTopLevelAncestor().setVisible(true);
        }
    }

    static void simplifyKeyframesButtonResponse(MainPanel mainPanel) {
        final int x = JOptionPane.showConfirmDialog(mainPanel,
                "This is an irreversible process that will lose some of your model data,\nin exchange for making it a smaller storage size.\n\nContinue and simplify keyframes?",
                "Warning: Simplify Keyframes", JOptionPane.OK_CANCEL_OPTION);
        if (x == JOptionPane.OK_OPTION) {
            final EditableModel currentMDL = mainPanel.currentMDL();
            currentMDL.simplifyKeyframes();
        }
    }

    static void linearizeAnimations(MainPanel mainPanel) {
        final int x = JOptionPane.showConfirmDialog(mainPanel,
                "This is an irreversible process that will lose some of your model data,\nin exchange for making it a smaller storage size.\n\nContinue and simplify animations?",
                "Warning: Linearize Animations", JOptionPane.OK_CANCEL_OPTION);
        if (x == JOptionPane.OK_OPTION) {
            final List<AnimFlag> allAnimFlags = mainPanel.currentMDL().getAllAnimFlags();
            for (final AnimFlag flag : allAnimFlags) {
                flag.linearize();
            }
        }
    }

    static void importScript() {
        final JFrame frame = new JFrame("Animation Transferer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new AnimationTransfer(frame));
        frame.setIconImage(AnimIcon.getImage());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static void fetchUnitButtonResponse(MainPanel mainPanel) {
        final GameObject unitFetched = mainPanel.fetchUnit();
        if (unitFetched != null) {
            ImageIcon icon = unitFetched.getScaledIcon(0.25f);

            loadAsMDXAndEnableToolsMenu(mainPanel, icon, unitFetched.getField("file"));
        }
    }

    static void fetchModelButtonResponse(MainPanel mainPanel) {
        final ModelOptionPane.ModelElement model = mainPanel.fetchModel();
        if (model != null) {
            final ImageIcon icon = model.hasCachedIconPath() ?
                    new ImageIcon(BLPHandler.get().getGameTex(model.getCachedIconPath())
                            .getScaledInstance(16, 16, Image.SCALE_FAST)) : MDLIcon;

            loadAsMDXAndEnableToolsMenu(mainPanel, icon, model.getFilepath());
        }
    }

    static void fetchObjectButtonResponse(MainPanel mainPanel) {
        final MutableObjectData.MutableGameObject objectFetched = mainPanel.fetchObject();
        if (objectFetched != null) {
            ImageIcon icon = new ImageIcon(BLPHandler.get()
                    .getGameTex(objectFetched.getFieldAsString(UnitFields.INTERFACE_ICON, 0))
                    .getScaledInstance(16, 16, Image.SCALE_FAST));

            loadAsMDXAndEnableToolsMenu(mainPanel, icon, objectFetched.getFieldAsString(UnitFields.MODEL_FILE, 0));
        }
    }

    static void loadAsMDXAndEnableToolsMenu(MainPanel mainPanel, ImageIcon icon, String path) {
        final String filepath = MenuBar.convertPathToMDX(path);

        if (filepath != null) {
            FileUtils.loadStreamMdx(mainPanel, MpqCodebase.get().getResourceAsStream(filepath), true, true, icon);

            final String portrait = filepath.substring(0, filepath.lastIndexOf('.')) + "_portrait" + filepath.substring(filepath.lastIndexOf('.'));

            if (mainPanel.prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
                FileUtils.loadStreamMdx(mainPanel, MpqCodebase.get()
                        .getResourceAsStream(portrait), true, false, icon);
            }
            mainPanel.menuBar.toolsMenu.getAccessibleContext().setAccessibleDescription(
                    "Allows the user to control which parts of the model are displayed for editing.");
            mainPanel.menuBar.toolsMenu.setEnabled(true);
        }
    }

    static void riseFallBirth(MainPanel mainPanel) {
        final int confirmed = JOptionPane.showConfirmDialog(mainPanel,
                "This will permanently alter model. Are you sure?", "Confirmation",
                JOptionPane.OK_CANCEL_OPTION);
        if (confirmed != JOptionPane.OK_OPTION) {
            return;
        }

        final ModelView disp = mainPanel.currentModelPanel.getModelViewManager();
        final EditableModel model = disp.getModel();

        replaceOrUseOldAnimation(mainPanel, model, "Birth");
        replaceOrUseOldAnimation(mainPanel, model, "Death");

        JOptionPane.showMessageDialog(mainPanel, "Done!");
    }

    static void setAnimationVisibilityFlag(Animation animation, Animation stand, VisibilitySource source) {
        final AnimFlag dummy = new AnimFlag("dummy");
        final AnimFlag af = source.getVisibilityFlag();
        dummy.copyFrom(af);
        af.deleteAnim(animation);
        af.copyFrom(dummy, stand.getStart(), stand.getEnd(), animation.getStart(), animation.getEnd());
        af.setEntry(animation.getEnd(), 0);
    }

    static void addAnimationFlags(Animation animation, IdObject obj) {
        if (obj instanceof Bone) {
            final Bone b = (Bone) obj;
            AnimFlag trans = null;
            boolean globalSeq = false;
            for (final AnimFlag af : b.getAnimFlags().values()) {
                if (af.getTypeId() == AnimFlag.TRANSLATION) {
                    if (af.hasGlobalSeq()) {
                        globalSeq = true;
                    } else {
                        trans = af;
                    }
                }
            }
            if (globalSeq) {
                return;
            }
            if (trans == null) {
                final ArrayList<Integer> times = new ArrayList<>();
                final ArrayList<Integer> values = new ArrayList<>();
                trans = new AnimFlag("Translation", times, values);
                trans.addTag("Linear");
                b.getAnimFlags().put("Translation", trans);
            }
            trans.addEntry(animation.getStart(), new Vertex(0, 0, 0));
            trans.addEntry(animation.getEnd(), new Vertex(0, 0, -300));
        }
    }

    private static void replaceOrUseOldAnimation(MainPanel mainPanel, EditableModel model, String animationName) {
        final Animation lastAnim = model.getAnim(model.getAnimsSize() - 1);
        Animation animation = new Animation(animationName, lastAnim.getEnd() + 300, lastAnim.getEnd() + 2300);

        boolean removeOldAnimation = false;
        final Animation oldAnimation = model.findAnimByName(animationName);

        if (oldAnimation != null) {
            final String KEEP_NEW = "Keep new";
            final String KEEP_OLD = "Keep old";
            final String[] choices = {"Cancel operation", KEEP_OLD, KEEP_NEW};
            final Object x = JOptionPane.showInputDialog(mainPanel,
                    "Existing " + animationName + " detected. What should be done with it?", "Question",
                    JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
            if (x == KEEP_NEW) {
                removeOldAnimation = true;
            } else if (x == KEEP_OLD) {
                animation = oldAnimation;
            }
            else {
                return;
            }
        }
        if(removeOldAnimation){
            model.remove(oldAnimation);
        }

        final Animation stand = model.findAnimByName("stand");

        final List<IdObject> roots = new ArrayList<>();
        for (final IdObject obj : model.getIdObjects()) {
            if (obj.getParent() == null) {
                roots.add(obj);
            }
        }
        for (final AnimFlag af : model.getAllAnimFlags()) {
            af.deleteAnim(animation);
        }
        for (final IdObject obj : roots) {
            addAnimationFlags(animation, obj);
        }

        // visibility
        for (final VisibilitySource source : model.getAllVisibilitySources()) {
            setAnimationVisibilityFlag(animation, stand, source);
        }

        if (!animation.getTags().contains("NonLooping")) {
            animation.addTag("NonLooping");
        }

        if (!model.contains(animation)) {
            model.add(animation);
        }

    }

    static void dataSourcesChanged(WarcraftDataSourceChangeListener.WarcraftDataSourceChangeNotifier directoryChangeNotifier, List<ModelPanel> modelPanels) {
        for (final ModelPanel modelPanel : modelPanels) {
            final PerspDisplayPanel pdp = modelPanel.getPerspArea();
            pdp.reloadAllTextures();
            modelPanel.getAnimationViewer().reloadAllTextures();
        }
        directoryChangeNotifier.dataSourcesChanged();
    }
}
