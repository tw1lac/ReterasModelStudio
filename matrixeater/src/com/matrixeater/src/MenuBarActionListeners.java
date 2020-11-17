package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.gui.modeledit.util.TransferActionListener;
import com.hiveworkshop.wc3.mdl.EventObject;
import com.hiveworkshop.wc3.mdl.*;
import com.hiveworkshop.wc3.mdl.render3d.RenderModel;
import com.hiveworkshop.wc3.mdl.v2.ModelViewManager;
import com.hiveworkshop.wc3.mdl.v2.timelines.InterpolationType;
import com.matrixeaterhayate.TextureManager;
import net.infonode.docking.TabWindow;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.*;

public class MenuBarActionListeners {
    static AbstractAction inverseAllUVsAction(final MainPanel mainPanel) {
        return new AbstractAction("Swap UVs U for V") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // TODO this should be an action
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
        };
    }

    static ActionListener repainter(MainPanel mainPanel) {
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

    static ActionListener copyActionListener(MainPanel mainPanel) {
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

    static ActionListener sortBones(MainPanel mainPanel) {
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

    static ActionListener minimizeGeoset(MainPanel mainPanel) {
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

    static ActionListener exit(MainPanel mainPanel) {
        return e -> {
            if (mainPanel.closeAll()) {
                MainFrame.frame.dispose();
            }
        };
    }

    static ActionListener revert(MainPanel mainPanel) {
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

    static ActionListener editTextures(MainPanel mainPanel) {
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

    static ActionListener resetViewButton(MainPanel mainPanel) {
        return e -> {
            mainPanel.traverseAndReset(mainPanel.rootWindow);
            final TabWindow startupTabWindow = mainPanel.createMainLayout();
            mainPanel.rootWindow.setWindow(startupTabWindow);
            mainPanel.traverseAndFix(mainPanel.rootWindow);
        };
    }

    static ActionListener exportAnimatedToStaticMesh(MainPanel mainPanel) {
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

    static ActionListener exportAnimatedFramePNG(MainPanel mainPanel) {
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

    static ActionListener combineAnimations(MainPanel mainPanel) {
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
}
