package com.matrixeater.src;

import com.hiveworkshop.wc3.mdl.*;
import com.hiveworkshop.wc3.util.IconUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewParticleEffectStuff {
    static JMenu addParticle;
    public NewParticleEffectStuff(JMenu menu, MainPanel mainPanel){
        addParticle = menu;
        fetchIncludedParticles(mainPanel);
    }


    static void fetchIncludedParticles(final MainPanel mainPanel) {
        final File stockFolder = new File("matrixeater/stock/particles");
        final File[] stockFiles = stockFolder.listFiles((dir, name) -> name.endsWith(".mdx"));
        for (final File file : stockFiles) {
            parseStockFiles(mainPanel, file);
        }
    }

    private static void parseStockFiles(MainPanel mainPanel, File file) {
        final String basicName = file.getName().split("\\.")[0];
        final File pngImage = new File(file.getParent() + File.separatorChar + basicName + ".png");
        if (pngImage.exists()) {
            try {
                makeParticleButtons(mainPanel, file, basicName, pngImage);
            } catch (final IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private static void makeParticleButtons(MainPanel mainPanel, File file, String basicName, File pngImage) throws IOException {
        final Image image = ImageIO.read(pngImage);
        final JMenuItem particleItem = new JMenuItem(basicName,
                new ImageIcon(image.getScaledInstance(28, 28, Image.SCALE_DEFAULT)));
        particleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                addParticleEmitterButtonResponse(mainPanel, file, basicName, image);
            }
        });
        addParticle.add(particleItem);
    }

    private static void addParticleEmitterButtonResponse(MainPanel mainPanel, File file, String basicName, Image image) {
        final ParticleEmitter2 particle = EditableModel.read(file)
                .sortedIdObjects(ParticleEmitter2.class).get(0);

        final JPanel particlePanel = new JPanel();
        final java.util.List<IdObject> idObjects = new ArrayList<>(mainPanel.currentMDL().getIdObjects());
        final Bone nullBone = new Bone("No parent");
        idObjects.add(0, nullBone);
        final JComboBox<IdObject> parent = new JComboBox<>(idObjects.toArray(new IdObject[0]));
        parent.setRenderer(createBasicComboboxRender(nullBone));
        final JLabel parentLabel = new JLabel("Parent:");

        final JLabel imageLabel = new JLabel(new ImageIcon(image.getScaledInstance(128, 128, Image.SCALE_SMOOTH)));
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

        String axisLabel = "Y:";
        final JLabel zLabel = new JLabel(axisLabel);
        final JSpinner zSpinner = new JSpinner(
                new SpinnerNumberModel(0.0, -100000.00, 100000.0, 0.0001));
        zLabel.add(zSpinner);

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
            makeColorButtons(mainPanel, particle, colorButtons, colors, i);
        }

        final GroupLayout layout = new GroupLayout(particlePanel);

        setHorizontalLayoutGroup(parent, parentLabel, imageLabel, titleLabel, nameLabel, nameField, xLabel, xSpinner, yLabel, ySpinner, zLabel, zSpinner, chooseAnimations, colorButtons, layout);
        setVerticalLayoutGroup(parent, parentLabel, imageLabel, titleLabel, nameLabel, nameField, xLabel, xSpinner, yLabel, ySpinner, zLabel, zSpinner, chooseAnimations, colorButtons, layout);

        particlePanel.setLayout(layout);
        final int x = JOptionPane.showConfirmDialog(mainPanel, particlePanel,
                "Add " + basicName, JOptionPane.OK_CANCEL_OPTION);
        if (x == JOptionPane.OK_OPTION) {
            addChoosenParticles(particle, nullBone, parent, nameField, xSpinner, ySpinner, zSpinner, anims, checkBoxes, colors, mainPanel);
        }
    }

    private static void makeColorButtons(MainPanel mainPanel, ParticleEmitter2 particle, JButton[] colorButtons, Color[] colors, int i) {
        final Vertex colorValues = particle.getSegmentColor(i);
        final Color color = new Color((int) (colorValues.z * 255), (int) (colorValues.y * 255), (int) (colorValues.x * 255));

        final JButton button = new JButton("Color " + (i + 1), new ImageIcon(IconUtils.createBlank(color, 32, 32)));

        colors[i] = color;

        final int index = i;
        button.addActionListener(e -> {showColorChooserDialog(mainPanel, colors, button, index);});
        colorButtons[i] = button;
    }

    private static void showColorChooserDialog(MainPanel mainPanel, Color[] colors, JButton button, int index) {
        final Color colorChoice = JColorChooser.showDialog(mainPanel,
                "Chooser Color", colors[index]);
        if (colorChoice != null) {
            colors[index] = colorChoice;
            button.setIcon(new ImageIcon(IconUtils.createBlank(colors[index], 32, 32)));
        }
    }

    private static BasicComboBoxRenderer createBasicComboboxRender(Bone nullBone) {
        return new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
                final IdObject idObject = (IdObject) value;
                if (idObject == nullBone) {
                    return super.getListCellRendererComponent(list, "No parent", index, isSelected, cellHasFocus);
                }
                return super.getListCellRendererComponent(list, value.getClass().getSimpleName() + " \"" + idObject.getName() + "\"", index, isSelected, cellHasFocus);
            }
        };
    }

    private static void addChoosenParticles(ParticleEmitter2 particle, Bone nullBone, JComboBox<IdObject> parent, JTextField nameField, JSpinner xSpinner, JSpinner ySpinner, JSpinner zSpinner, List<Animation> anims, JCheckBox[] checkBoxes, Color[] colors, MainPanel mainPanel) {
        int animIndex;// do stuff
        particle.setPivotPoint(new Vertex(
                ((Number) xSpinner.getValue()).doubleValue(),
                ((Number) ySpinner.getValue()).doubleValue(),
                ((Number) zSpinner.getValue()).doubleValue()));
        for (int i = 0; i < colors.length; i++) {
            particle.setSegmentColor(i, new Vertex(colors[i].getBlue() / 255.00, colors[i].getGreen() / 255.00, colors[i].getRed() / 255.00));
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

    private static void setVerticalLayoutGroup(JComboBox<IdObject> parent, JLabel parentLabel, JLabel imageLabel, JLabel titleLabel, JLabel nameLabel, JTextField nameField, JLabel xLabel, JSpinner xSpinner, JLabel yLabel, JSpinner ySpinner, JLabel zLabel, JSpinner zSpinner, JButton chooseAnimations, JButton[] colorButtons, GroupLayout layout) {
        GroupLayout.ParallelGroup nameGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(nameLabel).addComponent(nameField);
        GroupLayout.ParallelGroup parentGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(parentLabel).addComponent(parent);
        GroupLayout.ParallelGroup axisSpinners = layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(xLabel)
                .addComponent(xSpinner)
                .addComponent(yLabel)
                .addComponent(ySpinner)
                .addComponent(zLabel)
                .addComponent(zSpinner);
        GroupLayout.ParallelGroup colorChoosers = layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(colorButtons[0])
                .addComponent(colorButtons[1])
                .addComponent(colorButtons[2]);

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(imageLabel)
                        .addGroup(layout.createSequentialGroup().addComponent(titleLabel)
                                .addGroup(nameGroup)
                                .addGap(4)
                                .addGroup(parentGroup)
                                .addGap(4)
                                .addComponent(chooseAnimations)
                                .addGap(4)
                                .addGroup(axisSpinners)
                                .addGap(4)
                                .addGroup(colorChoosers)));
    }

    private static void setHorizontalLayoutGroup(JComboBox<IdObject> parent, JLabel parentLabel, JLabel imageLabel, JLabel titleLabel, JLabel nameLabel, JTextField nameField, JLabel xLabel, JSpinner xSpinner, JLabel yLabel, JSpinner ySpinner, JLabel zLabel, JSpinner zSpinner, JButton chooseAnimations, JButton[] colorButtons, GroupLayout layout) {
        GroupLayout.SequentialGroup nameGroup = layout.createSequentialGroup().addComponent(nameLabel).addGap(4).addComponent(nameField);
        GroupLayout.SequentialGroup parentGroup = layout.createSequentialGroup().addComponent(parentLabel).addGap(4).addComponent(parent);
        GroupLayout.SequentialGroup axisSpinners = layout.createSequentialGroup()
                .addComponent(xLabel)
                .addComponent(xSpinner).addGap(4)
                .addComponent(yLabel)
                .addComponent(ySpinner).addGap(4)
                .addComponent(zLabel)
                .addComponent(zSpinner);
        GroupLayout.SequentialGroup colorChoosers = layout.createSequentialGroup()
                .addComponent(colorButtons[0]).addGap(4)
                .addComponent(colorButtons[1]).addGap(4)
                .addComponent(colorButtons[2]);

        layout.setHorizontalGroup(
                layout.createSequentialGroup().addComponent(imageLabel).addGap(8)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(titleLabel)
                                .addGroup(nameGroup)
                                .addGroup(parentGroup)
                                .addComponent(chooseAnimations)
                                .addGroup(axisSpinners)
                                .addGroup(colorChoosers)));
    }
}
