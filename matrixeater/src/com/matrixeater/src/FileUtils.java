package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.GlobalIcons;
import com.hiveworkshop.wc3.gui.modeledit.ImportPanel;
import com.hiveworkshop.wc3.gui.modeledit.MaterialListRenderer;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.mdl.*;
import com.hiveworkshop.wc3.mdx.MdxModel;
import com.hiveworkshop.wc3.mdx.MdxUtils;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.units.GameObject;
import com.hiveworkshop.wc3.units.ModelOptionPane;
import com.hiveworkshop.wc3.units.fields.UnitFields;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData;
import com.hiveworkshop.wc3.user.SaveProfile;
import com.hiveworkshop.wc3.util.IconUtils;
import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.parser.Parse;
import de.wc3data.stream.BlizzardDataInputStream;
import de.wc3data.stream.BlizzardDataOutputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    public final static JFileChooser fc = createUniversalFileChooser();
    public final static JFileChooser fcm = createModelFileChooser();
    public final static JFileChooser exportTextureDialog = createTextureFileChooser();

    public FileUtils(){
        createFileChooser();
    }

    public static void loadFile(MainPanel mainPanel, final File f, final boolean temporary) {
        loadFile(mainPanel, f, temporary, true, MenuBarActionListeners.MDLIcon);
    }

    public static void loadFile(MainPanel mainPanel, final File f) {
        loadFile(mainPanel, f, false);
    }

    public static void openFile(MainPanel mainPanel, final File f) {
        mainPanel.currentFile = f;
        mainPanel.profile.setPath(mainPanel.currentFile.getParent());
        // frontArea.clearGeosets();
        // sideArea.clearGeosets();
        // botArea.clearGeosets();
        mainPanel.toolsMenu.getAccessibleContext().setAccessibleDescription(
                "Allows the user to control which parts of the model are displayed for editing.");
        mainPanel.toolsMenu.setEnabled(true);
        SaveProfile.get().addRecent(mainPanel.currentFile.getPath());
        mainPanel.updateRecent();
        loadFile(mainPanel, mainPanel.currentFile);
    }

    public static void importFile(MainPanel mainPanel, final File f) {
        final EditableModel currentModel = mainPanel.currentMDL();
        if (currentModel != null) {
            importFile(mainPanel, EditableModel.read(f));
        }
    }

    static void fetchIncludedParticles(final MainPanel mainPanel) {
        final File stockFolder = new File("matrixeater/stock/particles");
        final File[] stockFiles = stockFolder.listFiles((dir, name) -> name.endsWith(".mdx"));
        for (final File file : stockFiles) {
                final String basicName = file.getName().split("\\.")[0];
                final File pngImage = new File(file.getParent() + File.separatorChar + basicName + ".png");
                if (pngImage.exists()) {
                    try {
                        final Image image = ImageIO.read(pngImage);
                        final JMenuItem particleItem = new JMenuItem(basicName,
                                new ImageIcon(image.getScaledInstance(28, 28, Image.SCALE_DEFAULT)));
                        particleItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                final ParticleEmitter2 particle = EditableModel.read(file)
                                        .sortedIdObjects(ParticleEmitter2.class).get(0);

                                final JPanel particlePanel = new JPanel();
                                final java.util.List<IdObject> idObjects = new ArrayList<>(mainPanel.currentMDL().getIdObjects());
                                final Bone nullBone = new Bone("No parent");
                                idObjects.add(0, nullBone);
                                final JComboBox<IdObject> parent = new JComboBox<>(idObjects.toArray(new IdObject[0]));
                                parent.setRenderer(new BasicComboBoxRenderer() {
                                    @Override
                                    public Component getListCellRendererComponent(final JList list, final Object value,
                                                                                  final int index, final boolean isSelected, final boolean cellHasFocus) {
                                        final IdObject idObject = (IdObject) value;
                                        if (idObject == nullBone) {
                                            return super.getListCellRendererComponent(list, "No parent", index, isSelected,
                                                    cellHasFocus);
                                        }
                                        return super.getListCellRendererComponent(list,
                                                value.getClass().getSimpleName() + " \"" + idObject.getName() + "\"", index,
                                                isSelected, cellHasFocus);
                                    }
                                });
                                final JLabel parentLabel = new JLabel("Parent:");
                                final JLabel imageLabel = new JLabel(
                                        new ImageIcon(image.getScaledInstance(128, 128, Image.SCALE_SMOOTH)));
                                final JLabel titleLabel = new JLabel("Add " + basicName);
                                titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

                                final JLabel nameLabel = new JLabel("Particle Name:");
                                final JTextField nameField = new JTextField("MyBlizParticle");

                                final JLabel xLabel = new JLabel("Z:");
                                final JSpinner xSpinner = new JSpinner(
                                        new SpinnerNumberModel(0.0, -100000.00, 100000.0, 0.0001));

                                final JLabel yLabel = new JLabel("X:");
                                final JSpinner ySpinner = new JSpinner(
                                        new SpinnerNumberModel(0.0, -100000.00, 100000.0, 0.0001));

                                final JLabel zLabel = new JLabel("Y:");
                                final JSpinner zSpinner = new JSpinner(
                                        new SpinnerNumberModel(0.0, -100000.00, 100000.0, 0.0001));
                                parent.addActionListener(e12 -> {
                                    final IdObject choice = parent.getItemAt(parent.getSelectedIndex());
                                    xSpinner.setValue(choice.getPivotPoint().x);
                                    ySpinner.setValue(choice.getPivotPoint().y);
                                    zSpinner.setValue(choice.getPivotPoint().z);
                                });

                                final JPanel animPanel = new JPanel();
                                final List<Animation> anims = mainPanel.currentMDL().getAnims();
                                animPanel.setLayout(new GridLayout(anims.size() + 1, 1));
                                final JCheckBox[] checkBoxes = new JCheckBox[anims.size()];
                                int animIndex = 0;
                                for (final Animation anim : anims) {
                                    animPanel.add(checkBoxes[animIndex] = new JCheckBox(anim.getName()));
                                    checkBoxes[animIndex].setSelected(true);
                                    animIndex++;
                                }
                                final JButton chooseAnimations = new JButton("Choose when to show!");
                                chooseAnimations.addActionListener(e13 -> JOptionPane.showMessageDialog(particlePanel, animPanel));
                                final JButton[] colorButtons = new JButton[3];
                                final Color[] colors = new Color[colorButtons.length];
                                for (int i = 0; i < colorButtons.length; i++) {
                                    final Vertex colorValues = particle.getSegmentColor(i);
                                    final Color color = new Color((int) (colorValues.z * 255), (int) (colorValues.y * 255),
                                            (int) (colorValues.x * 255));

                                    final JButton button = new JButton("Color " + (i + 1),
                                            new ImageIcon(IconUtils.createBlank(color, 32, 32)));
                                    colors[i] = color;
                                    final int index = i;
                                    button.addActionListener(e14 -> {
                                        final Color colorChoice = JColorChooser.showDialog(mainPanel,
                                                "Chooser Color", colors[index]);
                                        if (colorChoice != null) {
                                            colors[index] = colorChoice;
                                            button.setIcon(new ImageIcon(IconUtils.createBlank(colors[index], 32, 32)));
                                        }
                                    });
                                    colorButtons[i] = button;
                                }

                                final GroupLayout layout = new GroupLayout(particlePanel);

                                layout.setHorizontalGroup(
                                        layout.createSequentialGroup().addComponent(imageLabel).addGap(8)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                        .addComponent(titleLabel)
                                                        .addGroup(layout.createSequentialGroup().addComponent(nameLabel)
                                                                .addGap(4).addComponent(nameField))
                                                        .addGroup(layout.createSequentialGroup().addComponent(parentLabel)
                                                                .addGap(4).addComponent(parent))
                                                        .addComponent(chooseAnimations)
                                                        .addGroup(layout.createSequentialGroup().addComponent(xLabel)
                                                                .addComponent(xSpinner).addGap(4).addComponent(yLabel)
                                                                .addComponent(ySpinner).addGap(4).addComponent(zLabel)
                                                                .addComponent(zSpinner))
                                                        .addGroup(
                                                                layout.createSequentialGroup().addComponent(colorButtons[0])
                                                                        .addGap(4).addComponent(colorButtons[1]).addGap(4)
                                                                        .addComponent(colorButtons[2]))));
                                layout.setVerticalGroup(
                                        layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(imageLabel)
                                                .addGroup(
                                                        layout.createSequentialGroup().addComponent(titleLabel)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                                        .addComponent(nameLabel).addComponent(nameField))
                                                                .addGap(4)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                                        .addComponent(parentLabel).addComponent(parent))
                                                                .addGap(4).addComponent(chooseAnimations).addGap(4)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                                        .addComponent(xLabel).addComponent(xSpinner)
                                                                        .addComponent(yLabel).addComponent(ySpinner)
                                                                        .addComponent(zLabel).addComponent(zSpinner))
                                                                .addGap(4)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                                        .addComponent(colorButtons[0])
                                                                        .addComponent(colorButtons[1])
                                                                        .addComponent(colorButtons[2]))));
                                particlePanel.setLayout(layout);
                                final int x = JOptionPane.showConfirmDialog(mainPanel, particlePanel,
                                        "Add " + basicName, JOptionPane.OK_CANCEL_OPTION);
                                if (x == JOptionPane.OK_OPTION) {
                                    // do stuff
                                    particle.setPivotPoint(new Vertex(((Number) xSpinner.getValue()).doubleValue(),
                                            ((Number) ySpinner.getValue()).doubleValue(),
                                            ((Number) zSpinner.getValue()).doubleValue()));
                                    for (int i = 0; i < colors.length; i++) {
                                        particle.setSegmentColor(i, new Vertex(colors[i].getBlue() / 255.00,
                                                colors[i].getGreen() / 255.00, colors[i].getRed() / 255.00));
                                    }
                                    final IdObject parentChoice = parent.getItemAt(parent.getSelectedIndex());
                                    if (parentChoice == nullBone) {
                                        particle.setParent(null);
                                    } else {
                                        particle.setParent(parentChoice);
                                    }
                                    AnimFlag oldFlag = particle.getVisibilityFlag();
                                    if (oldFlag == null) {
                                        oldFlag = new AnimFlag("Visibility");
                                    }
                                    final AnimFlag visFlag = AnimFlag.buildEmptyFrom(oldFlag);
                                    animIndex = 0;
                                    for (final Animation anim : anims) {
                                        if (!checkBoxes[animIndex].isSelected()) {
                                            visFlag.addEntry(anim.getStart(), 0);
                                        }
                                        animIndex++;
                                    }
                                    particle.setVisibilityFlag(visFlag);
                                    particle.setName(nameField.getText());
                                    mainPanel.currentMDL().add(particle);
                                    mainPanel.modelStructureChangeListener.nodesAdded(Collections.<IdObject>singletonList(particle));
                                }
                            }
                        });
                        mainPanel.addParticle.add(particleItem);
                    } catch (final IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
    }

    static String incName(final String name) {
        String output = name;

        int depth = 1;
        boolean continueLoop = true;
        while (continueLoop) {
            char c = '0';
            try {
                c = output.charAt(output.length() - depth);
            } catch (final IndexOutOfBoundsException e) {
                // c remains '0'
                continueLoop = false;
            }
            for (char n = '0'; (n < '9') && continueLoop; n++) {
                // JOptionPane.showMessageDialog(null,"checking "+c+" against
                // "+n);
                if (c == n) {
                    char x = c;
                    x++;
                    output = output.substring(0, output.length() - depth) + x
                            + output.substring((output.length() - depth) + 1);
                    continueLoop = false;
                }
            }
            if (c == '9') {
                output = output.substring(0, output.length() - depth) + 0
                        + output.substring((output.length() - depth) + 1);
            } else if (continueLoop) {
                output = output.substring(0, (output.length() - depth) + 1) + 1
                        + output.substring((output.length() - depth) + 1);
                continueLoop = false;
            }
            depth++;
        }
        if (output.equals(name)) {
            output = output + "_edit";
        }

        return output;
    }

    public static void nullModelFile(MainPanel mainPanel) {
        final EditableModel currentMDL = mainPanel.currentMDL();
        if (currentMDL != null) {
            final EditableModel newModel = new EditableModel();
            newModel.copyHeaders(currentMDL);
            if (newModel.getFileRef() == null) {
                newModel.setFileRef(
                        new File(System.getProperty("java.io.tmpdir") + "MatrixEaterExtract/matrixeater_anonymousMDL",
                                "" + (int) (Math.random() * Integer.MAX_VALUE) + ".mdl"));
            }
            while (newModel.getFile().exists()) {
                newModel.setFileRef(
                        new File(currentMDL.getFile().getParent() + "/" + incName(newModel.getName()) + ".mdl"));
            }
            mainPanel.importPanel = new ImportPanel(newModel, EditableModel.deepClone(currentMDL, "CurrentModel"));

            final Thread watcher = new Thread(() -> {
                while (mainPanel.importPanel.getParentFrame().isVisible()
                        && (!mainPanel.importPanel.importStarted() || mainPanel.importPanel.importEnded())) {
                    try {
                        Thread.sleep(1);
                    } catch (final Exception e) {
                        ExceptionPopup.display("MatrixEater detected error with Java's wait function", e);
                    }
                }
                if (mainPanel.importPanel.importStarted()) {
                    while (!mainPanel.importPanel.importEnded()) {
                        try {
                            Thread.sleep(1);
                        } catch (final Exception e) {
                            ExceptionPopup.display("MatrixEater detected error with Java's wait function", e);
                        }
                    }

                    if (mainPanel.importPanel.importSuccessful()) {
                        newModel.saveFile();
                        loadFile(mainPanel, newModel.getFile());
                    }
                }
            });
            watcher.start();
        }
    }

    public static void importFile(final MainPanel mainPanel, final EditableModel model) {
        final EditableModel currentModel = mainPanel.currentMDL();
        if (currentModel != null) {
            mainPanel.importPanel = new ImportPanel(currentModel, model);
            mainPanel.importPanel.setCallback(new ModelStructureChangeListenerImplementation(mainPanel, new MainPanel.ModelReference() {
                private final EditableModel model = mainPanel.currentMDL();

                @Override
                public EditableModel getModel() {
                    return model;
                }
            }));

        }
    }

    public static void loadFile(MainPanel mainPanel, final File f, final boolean temporary, final boolean selectNewTab, final ImageIcon icon) {
        if (f.getPath().toLowerCase().endsWith("blp")) {
            loadBLPPathAsModel(mainPanel, f.getName(), f.getParentFile());
            return;
        }
        if (f.getPath().toLowerCase().endsWith("png")) {
            loadBLPPathAsModel(mainPanel, f.getName(), f.getParentFile());
            return;
        }
        ModelPanel temp = null;
        try {
            if (f.getPath().toLowerCase().endsWith("mdx")) {
                BlizzardDataInputStream in = new BlizzardDataInputStream(new FileInputStream(f));
                loadStreamMdx(mainPanel, in, temporary, selectNewTab, icon);
                return;
            } else if (f.getPath().toLowerCase().endsWith("obj")) {
                final Build builder = new Build();
                final Parse obj = new Parse(builder, f.getPath());
                temp = NewModelPanel.createModelPanel(mainPanel, builder.createMDL(), icon, false);
            } else {
                temp = NewModelPanel.createModelPanel(mainPanel, EditableModel.read(f), icon, false);
                temp.setFile(f);
            }
        }catch (final IOException e) {
            ExceptionPopup.display(e);
            e.printStackTrace();
        }
        mainPanel.modelPanelUgg.loadModel(mainPanel, temporary, selectNewTab, temp);
    }

    public static void loadStreamMdx(MainPanel mainPanel, final InputStream f, final boolean temporary, final boolean selectNewTab,
                                     final ImageIcon icon) {
        ModelPanel temp;
        try (BlizzardDataInputStream in = new BlizzardDataInputStream(f)) {
            final EditableModel model = new EditableModel(MdxUtils.loadModel(in));
            model.setFileRef(null);
            temp = NewModelPanel.createModelPanel(mainPanel, model, icon, false);
        } catch (final IOException e) {
            e.printStackTrace();
            ExceptionPopup.display(e);
            throw new RuntimeException("Reading mdx failed");
        }
        mainPanel.modelPanelUgg.loadModel(mainPanel, temporary, selectNewTab, temp);
    }

    public static void loadBLPPathAsModel(MainPanel mainPanel, final String filepath) {
        loadBLPPathAsModel(mainPanel, filepath, null);
    }

    public static void loadBLPPathAsModel(MainPanel mainPanel, final String filepath, final File workingDirectory) {
        loadBLPPathAsModel(mainPanel, filepath, workingDirectory, 800);
    }

    public static void loadBLPPathAsModel(MainPanel mainPanel, final String filepath, final File workingDirectory, final int version) {
        final EditableModel blankTextureModel = new EditableModel(filepath.substring(filepath.lastIndexOf('\\') + 1));
        blankTextureModel.setFormatVersion(version);
        if (workingDirectory != null) {
            blankTextureModel.setFileRef(new File(workingDirectory.getPath() + "/" + filepath + ".mdl"));
        }
        final Geoset newGeoset = new Geoset();
        final Layer layer = new Layer("Blend", new Bitmap(filepath));
        layer.add("Unshaded");
        final Material material = new Material(layer);
        newGeoset.setMaterial(material);
        final BufferedImage bufferedImage = material.getBufferedImage(blankTextureModel.getWrappedDataSource());
        final int textureWidth = bufferedImage.getWidth();
        final int textureHeight = bufferedImage.getHeight();
        final float aspectRatio = textureWidth / (float) textureHeight;

        final int displayWidth = (int) (aspectRatio > 1 ? 128 : 128 * aspectRatio);
        final int displayHeight = (int) (aspectRatio < 1 ? 128 : 128 / aspectRatio);

        final int groundOffset = aspectRatio > 1 ? (128 - displayHeight) / 2 : 0;

        final GeosetVertex upperLeft = ModelUtils.addGeosetAndTVerticies(newGeoset, 0, displayWidth, displayHeight + groundOffset, 1, 0);

        final GeosetVertex upperRight = ModelUtils.addGeosetAndTVerticies(newGeoset, 0, -displayWidth, displayHeight + groundOffset, 0, 0);

        final GeosetVertex lowerLeft = ModelUtils.addGeosetAndTVerticies(newGeoset, 0, displayWidth, groundOffset, 1, 1);

        final GeosetVertex lowerRight = ModelUtils.addGeosetAndTVerticies(newGeoset, 0, -displayWidth, groundOffset, 0, 1);

        newGeoset.add(new Triangle(upperLeft, upperRight, lowerLeft));
        newGeoset.add(new Triangle(upperRight, lowerRight, lowerLeft));
        blankTextureModel.add(newGeoset);
        blankTextureModel.add(new Animation("Stand", 0, 1000));
        blankTextureModel.doSavePreps();

        mainPanel.modelPanelUgg.loadModel(mainPanel, workingDirectory == null, true,
                NewModelPanel.createModelPanel(mainPanel, blankTextureModel, GlobalIcons.ORANGE_ICON, true));
    }

    static void exportTextures(MainPanel mainPanel) {
        final DefaultListModel<Material> materials = new DefaultListModel<>();
        for (int i = 0; i < mainPanel.currentMDL().getMaterials().size(); i++) {
            final Material mat = mainPanel.currentMDL().getMaterials().get(i);
            materials.addElement(mat);
        }
        for (final ParticleEmitter2 emitter2 : mainPanel.currentMDL().sortedIdObjects(ParticleEmitter2.class)) {
            final Material dummyMaterial = new Material(
                    new Layer("Blend", mainPanel.currentMDL().getTexture(emitter2.getTextureID())));
        }

        final JList<Material> materialsList = new JList<>(materials);
        materialsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialsList.setCellRenderer(new MaterialListRenderer(mainPanel.currentMDL()));
        JOptionPane.showMessageDialog(mainPanel, new JScrollPane(materialsList));

        if (mainPanel.profile.getPath() != null) {
            exportTextureDialog.setCurrentDirectory(new File(mainPanel.profile.getPath()));
        }

        if (exportTextureDialog.getCurrentDirectory() == null) {
            final EditableModel current = mainPanel.currentMDL();
            if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
                exportTextureDialog.setCurrentDirectory(current.getFile().getParentFile());
            } else if (mainPanel.profile.getPath() != null) {
                exportTextureDialog.setCurrentDirectory(new File(mainPanel.profile.getPath()));
            }
        }

        exportTextureDialog.setSelectedFile(new File(materialsList.getSelectedValue().getName() + ".png"));
        final int x = exportTextureDialog.showSaveDialog(mainPanel);
        if (x == JFileChooser.APPROVE_OPTION) {
            final File file = exportTextureDialog.getSelectedFile();
            if (file != null) {
                try {
                    String fileName = file.getName().toLowerCase();
                    if (fileName.contains(".")) {
                        BufferedImage bufferedImage = materialsList.getSelectedValue().getBufferedImage(mainPanel.currentMDL().getWrappedDataSource());
                        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);

                        if (fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                            JOptionPane.showMessageDialog(mainPanel,
                                    "Warning: Alpha channel was converted to black. Some data will be lost\nif you convert this texture back to Warcraft BLP.");
                            bufferedImage = BLPHandler.removeAlphaChannel(bufferedImage);
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

    static void importFromFile(MainPanel mainPanel) {
        fc.setDialogTitle("Import");
        final EditableModel current = mainPanel.currentMDL();
        if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
            fc.setCurrentDirectory(current.getFile().getParentFile());
        } else if (mainPanel.profile.getPath() != null) {
            fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
        }
        final int returnValue = fc.showOpenDialog(mainPanel);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            mainPanel.currentFile = fc.getSelectedFile();
            mainPanel.profile.setPath(mainPanel.currentFile.getParent());
            mainPanel.toolsMenu.getAccessibleContext().setAccessibleDescription(
                    "Allows the user to control which parts of the model are displayed for editing.");
            mainPanel.toolsMenu.setEnabled(true);
            importFile(mainPanel, mainPanel.currentFile);
        }

        fc.setSelectedFile(null);
        mainPanel.modelPanelUgg.refreshController();
    }

    static void animFromFile(MainPanel mainPanel) {
        fc.setDialogTitle("Animation Source");
        final EditableModel current = mainPanel.currentMDL();
        if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
            fc.setCurrentDirectory(current.getFile().getParentFile());
        } else if (mainPanel.profile.getPath() != null) {
            fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
        }
        final int returnValue = fc.showOpenDialog(mainPanel);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            mainPanel.currentFile = fc.getSelectedFile();
            mainPanel.profile.setPath(mainPanel.currentFile.getParent());
            final EditableModel animationSourceModel = EditableModel.read(mainPanel.currentFile);
            mainPanel.addSingleAnimation(current, animationSourceModel);
        }

        fc.setSelectedFile(null);

        mainPanel.modelPanelUgg.refreshController();
    }

    static void animFromObject(MainPanel mainPanel) {
        fc.setDialogTitle("Animation Source");
        final MutableObjectData.MutableGameObject fetchResult = mainPanel.fetchObject();
        if (fetchResult != null) {
            fetchAndAddAnimationFromFile(mainPanel, fetchResult.getFieldAsString(UnitFields.MODEL_FILE, 0));
        }
    }

    static void animFromModel(MainPanel mainPanel) {
        fc.setDialogTitle("Animation Source");
        final ModelOptionPane.ModelElement fetchResult = mainPanel.fetchModel();
        if (fetchResult != null) {
            fetchAndAddAnimationFromFile(mainPanel, fetchResult.getFilepath());
        }
    }

    static void animFromUnit(MainPanel mainPanel) {
        fc.setDialogTitle("Animation Source");
        final GameObject fetchResult = mainPanel.fetchUnit();
        if (fetchResult != null) {
            fetchAndAddAnimationFromFile(mainPanel, fetchResult.getField("file"));
        }
    }

    static void onClickOpen(MainPanel mainPanel) {
        fc.setDialogTitle("Open");
        final EditableModel current = mainPanel.currentMDL();
        if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
            fc.setCurrentDirectory(current.getFile().getParentFile());
        } else if (mainPanel.profile.getPath() != null) {
            fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
        }

        final int returnValue = fc.showOpenDialog(mainPanel);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            openFile(mainPanel, fc.getSelectedFile());
        }

        fc.setSelectedFile(null);
    }

    static void onClickSaveAs(MainPanel mainPanel) {
        final EditableModel current = mainPanel.currentMDL();
        onClickSaveAs(mainPanel, current);
    }

    static void onClickSaveAs(MainPanel mainPanel, final EditableModel current) {
        try {
            fc.setDialogTitle("Save as");
            if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
                fc.setCurrentDirectory(current.getFile().getParentFile());
                fc.setSelectedFile(current.getFile());
            } else if (mainPanel.profile.getPath() != null) {
                fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
            }
            final int returnValue = fc.showSaveDialog(mainPanel);
            File temp = fc.getSelectedFile();
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                if (temp != null) {
                    final String name = temp.getName();
                    String ext = ".mdl";
                    if (name.contains(".")) {
                        ext = name.substring(name.lastIndexOf('.'));
                        System.out.println(ext);
                        if (ext.equals(".obj")) {
                            JOptionPane.showMessageDialog(MainFrame.frame, "OBJ saving has not been implemented yet.", "Unsuported File Type",
                                    JOptionPane.ERROR_MESSAGE);
                                return;
                        }
                        if (!name.endsWith(".mdl") || !name.endsWith(".mdx")) {
                            JOptionPane.showMessageDialog(MainFrame.frame, ext + " is not a supported format to save as.", "Unsuported File Type",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        temp = new File(temp.getAbsolutePath() + ext);
                    }
                    mainPanel.currentFile = temp;
                    if (temp.exists()) {
                        final Object[] options = {"Overwrite", "Cancel"};
                        final int n = JOptionPane.showOptionDialog(MainFrame.frame, "Selected file already exists.",
                                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
                                options[1]);
                        if (n == 1) {
                            fc.setSelectedFile(null);
                            return;
                        }
                    }
                    mainPanel.profile.setPath(mainPanel.currentFile.getParent());
                    if (ext.equals(".mdl")) {
                        mainPanel.currentMDL().printTo(mainPanel.currentFile);
                    } else {
                        final MdxModel model = new MdxModel(mainPanel.currentMDL());
                        try (BlizzardDataOutputStream writer = new BlizzardDataOutputStream(mainPanel.currentFile)) {
                            model.save(writer);
                        } catch (final IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    mainPanel.currentMDL().setFileRef(mainPanel.currentFile);
                    // currentMDLDisp().resetBeenSaved();
                    // TODO reset been saved
                    mainPanel.currentModelPanel.getMenuItem().setName(mainPanel.currentFile.getName().split("\\.")[0]);
                    mainPanel.currentModelPanel.getMenuItem().setToolTipText(mainPanel.currentFile.getPath());
                } else {
                    JOptionPane.showMessageDialog(mainPanel,
                            "You tried to save, but you somehow didn't select a file.\nThat is bad.");
                }
            }
            fc.setSelectedFile(null);
            return;
        } catch (final Exception exc) {
            ExceptionPopup.display(exc);
        }
        mainPanel.modelPanelUgg.refreshController();
    }

    static void onClickSave(MainPanel mainPanel) {
        try {
            if (mainPanel.currentMDL() != null && mainPanel.currentMDL().getFile() != null) {
                mainPanel.currentMDL().saveFile();
                mainPanel.profile.setPath(mainPanel.currentMDL().getFile().getParent());
                // currentMDLDisp().resetBeenSaved();
                // TODO reset been saved
            }
        } catch (final Exception exc) {
            ExceptionPopup.display(exc);
        }
        mainPanel.modelPanelUgg.refreshController();
    }

    static void mergeGeoset(MainPanel mainPanel) {
        fcm.setDialogTitle("Merge Single Geoset (Oinker-based)");

        final EditableModel current = mainPanel.currentMDL();

        if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
            fcm.setCurrentDirectory(current.getFile().getParentFile());
        } else if (mainPanel.profile.getPath() != null) {
            fcm.setCurrentDirectory(new File(mainPanel.profile.getPath()));
        }

        final int returnValue = fcm.showOpenDialog(mainPanel);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            mainPanel.currentFile = fcm.getSelectedFile();

            final EditableModel geoSource = EditableModel.read(mainPanel.currentFile);

            mainPanel.profile.setPath(mainPanel.currentFile.getParent());

            JSpinner geosetImport = new JSpinner(new SpinnerNumberModel(1, 1, geoSource.getGeosetsSize(), 1));
            JSpinner geosetReceive = new JSpinner(new SpinnerNumberModel(1, 1, current.getGeosetsSize(), 1));
            Object[] message = {
                    "Geoset to Import:", geosetImport,
                    "Geoset to Receive:", geosetReceive
            };
            Geoset host = null;
            Geoset newGeoset = null;
            int option = JOptionPane.showConfirmDialog(mainPanel, message, "Choose Geosets", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                final int receiveValue = (Integer) geosetReceive.getValue();
                final int importValue = (Integer) geosetImport.getValue();
                host = current.getGeoset(receiveValue - 1);
                newGeoset = geoSource.getGeoset(importValue - 1);
                System.out.println("choosen values: " + receiveValue + " & " + importValue);
            } else {
                System.out.println("Geoset import canceled");
            }

            newGeoset.updateToObjects(current);

            System.out.println("putting " + newGeoset.numUVLayers() + " into a nice " + host.numUVLayers());

            for (int i = 0; i < newGeoset.numVerteces(); i++) {
                final GeosetVertex ver = newGeoset.getVertex(i);
                host.add(ver);
                ver.setGeoset(host);
            }
            for (int i = 0; i < newGeoset.numTriangles(); i++) {
                final Triangle tri = newGeoset.getTriangle(i);
                host.add(tri);
                tri.setGeoRef(host);
            }
        }
        fcm.setSelectedFile(null);
    }

    static void fetchAndAddAnimationFromFile(MainPanel mainPanel, String filepath) {
        final EditableModel current = mainPanel.currentMDL();
        final String mdxFilepath = MenuBar.convertPathToMDX(filepath);
        if (mdxFilepath != null) {
            final EditableModel animationSource = EditableModel.read(MpqCodebase.get().getFile(mdxFilepath));
            mainPanel.addSingleAnimation(current, animationSource);
        }
    }

    public void createFileChooser() {
//        JFileChooser fc;
//        JFileChooser exportTextureDialog;
//        final FileFilter filter;
//        final File filterFile;
//        filterFile = new File("", ".mdl");
//        filter = new MDLFilter();
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("All Accepted FileTypes", "mdx", "blp", "png", "mdl", "obj"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Binary Model '-.mdx'", "mdx"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Model '-.mdl'", "mdl"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Texture '-.blp'", "blp"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("PNG Image '-.png'", "png"));
//        fc.addChoosableFileFilter(filter);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Wavefront OBJ '-.obj'", "obj"));
        exportTextureDialog.setDialogTitle("Export Texture");
        final String[] imageTypes = ImageIO.getWriterFileSuffixes();
        for (final String suffix : imageTypes) {
            exportTextureDialog.addChoosableFileFilter(new FileNameExtensionFilter(suffix.toUpperCase() + " Image File", suffix));
        }
    }

    public static JFileChooser createUniversalFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("All Accepted FileTypes", "mdx", "blp", "png", "mdl", "obj"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Binary Model '-.mdx'", "mdx"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Model '-.mdl'", "mdl"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Texture '-.blp'", "blp"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG Image '-.png'", "png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Wavefront OBJ '-.obj'", "obj"));
        return fileChooser;
    }

    public static JFileChooser createModelFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Models", "mdx", "mdl"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Binary Model '-.mdx'", "mdx"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Model '-.mdl'", "mdl"));
        return fileChooser;
    }

    public static JFileChooser createTextureFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        System.out.println("dir: " + fileChooser.getCurrentDirectory());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Export Texture");
        final String[] imageTypes = ImageIO.getWriterFileSuffixes();
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Supported Image Files", imageTypes));
        for (final String suffix : imageTypes) {
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(suffix.toUpperCase() + " Image File", suffix));
        }
        return fileChooser;
    }

}
