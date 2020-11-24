package com.matrixeater.src;

import com.hiveworkshop.wc3.mdl.*;
import com.hiveworkshop.wc3.util.IconUtils;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;

public class AddParticlePanel {
    Color[] colors;
    JPanel particlePanel;
    List<Animation> anims;
    Set<Animation> notVisibleAnimationSet;

    public AddParticlePanel(){
        particlePanel = new JPanel();
        notVisibleAnimationSet = new HashSet<>();
    }

    void addParticleEmitterButtonResponse(MainPanel mainPanel, File file, String basicName, Image image) {
        final ParticleEmitter2 particle = EditableModel.read(file).sortedIdObjects(ParticleEmitter2.class).get(0);

        final java.util.List<IdObject> idObjects = new ArrayList<>(mainPanel.currentMDL().getIdObjects());
        final Bone nullBone = new Bone("No parent");
        idObjects.add(0, nullBone);

        final JComboBox<IdObject> particleParentBoneChooser = new JComboBox<>(idObjects.toArray(new IdObject[0]));
        particleParentBoneChooser.setRenderer(createBasicComboboxRender(nullBone));
        final JLabel particleParentChooserLabel = new JLabel("Parent:");

        final JLabel imageLabel = new JLabel(new ImageIcon(image.getScaledInstance(128, 128, Image.SCALE_SMOOTH)));
        final JLabel titleLabel = new JLabel("Add " + basicName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

        final JLabel nameLabel = new JLabel("Particle Name:");
        final JTextField nameField = new JTextField("MyBlizParticle");

        Map<String, Pair<JLabel, JSpinner>> coordinateSpinners = createCoordinateSpinners();

        particleParentBoneChooser.addActionListener(e -> {
            final IdObject choice = particleParentBoneChooser.getItemAt(particleParentBoneChooser.getSelectedIndex());
            coordinateSpinners.get("X").getValue().setValue(choice.getPivotPoint().x);
            coordinateSpinners.get("Y").getValue().setValue(choice.getPivotPoint().y);
            coordinateSpinners.get("Z").getValue().setValue(choice.getPivotPoint().z);
        });

        final JButton chooseAnimations = createAnimationChooserButton(mainPanel);

        final JButton[] colorButtons = makeColorButtons(particle);

        final GroupLayout layout = new GroupLayout(particlePanel);

        setHorizontalLayoutGroup(particleParentBoneChooser, particleParentChooserLabel, imageLabel, titleLabel, nameLabel, nameField, coordinateSpinners, chooseAnimations, colorButtons, layout);
        setVerticalLayoutGroup(particleParentBoneChooser, particleParentChooserLabel, imageLabel, titleLabel, nameLabel, nameField, coordinateSpinners, chooseAnimations, colorButtons, layout);

        particlePanel.setLayout(layout);

        final int x = JOptionPane.showConfirmDialog(mainPanel, particlePanel, "Add " + basicName, JOptionPane.OK_CANCEL_OPTION);

        if (x == JOptionPane.OK_OPTION) {
            NewParticleEffectStuff.addChosenParticles(particle, nullBone, particleParentBoneChooser, nameField, getCoordinates(coordinateSpinners), getVisFlag(particle), mainPanel, colors);
        }
    }

    private Map<String, Pair<JLabel, JSpinner>> createCoordinateSpinners() {
        Map<String, Pair<JLabel, JSpinner>> coordinateSpinners = new HashMap<>();
        coordinateSpinners.put("Z", new Pair<>(new JLabel("Z:"), new JSpinner(
                new SpinnerNumberModel(0.0, -100000.00, 100000.0, 0.0001))));
        coordinateSpinners.put("X", new Pair<>(new JLabel("X:"), new JSpinner(
                new SpinnerNumberModel(0.0, -100000.00, 100000.0, 0.0001))));
        coordinateSpinners.put("Y", new Pair<>(new JLabel("Y:"), new JSpinner(
                new SpinnerNumberModel(0.0, -100000.00, 100000.0, 0.0001))));
        return coordinateSpinners;
    }

    private double[] getCoordinates(Map<String, Pair<JLabel, JSpinner>> coordinateSpinners){
        double[] values = new double[3];
        values[0] = ((Number) coordinateSpinners.get("X").getValue().getValue()).doubleValue();
        values[1] = ((Number) coordinateSpinners.get("Y").getValue().getValue()).doubleValue();
        values[2] = ((Number) coordinateSpinners.get("Z").getValue().getValue()).doubleValue();
        return values;
    }

    private JButton createAnimationChooserButton(MainPanel mainPanel) {
        final JPanel animPanel = createAnimationChooserPanel(mainPanel);
        final JButton chooseAnimations = new JButton("Choose when to show!");
        chooseAnimations.addActionListener(e -> JOptionPane.showMessageDialog(particlePanel, animPanel));
        return chooseAnimations;
    }

    private JPanel createAnimationChooserPanel(MainPanel mainPanel) {
        JCheckBox[] checkBoxes;
        anims = mainPanel.currentMDL().getAnims();
        final JPanel animPanel = new JPanel();
        animPanel.setLayout(new GridLayout(anims.size() + 1, 1));
        checkBoxes = new JCheckBox[anims.size()];
        int animIndex = 0;
        for (final Animation anim : anims) {
            JCheckBox checkBox = new JCheckBox(anim.getName());
            animPanel.add(checkBoxes[animIndex] = checkBox);
            checkBoxes[animIndex].setSelected(true);
            checkBox.addActionListener(e -> toggleVisible(checkBox, anim));
            animIndex++;
        }
        return animPanel;
    }

    private AnimFlag getVisFlag(ParticleEmitter2 particle){
        AnimFlag oldFlag = particle.getVisibilityFlag();

        if (oldFlag == null) {
            oldFlag = new AnimFlag("Visibility");
        }

        AnimFlag visFlag = AnimFlag.buildEmptyFrom(oldFlag);

        for (final Animation anim : notVisibleAnimationSet) {
            visFlag.addEntry(anim.getStart(), 0);
        }
        return visFlag;
    }

    private void toggleVisible(JCheckBox checkBox, Animation animation){
        System.out.println(checkBox.isSelected());
        System.out.println(animation);
        if (checkBox.isSelected()) {
            notVisibleAnimationSet.remove(animation);
        }
        else {
            notVisibleAnimationSet.add(animation);
        }
    }

    private JButton[] makeColorButtons(ParticleEmitter2 particle) {
        final JButton[] colorButtons = new JButton[3];
        colors = new Color[colorButtons.length];

        for (int i = 0; i < colorButtons.length; i++) {
            final Vertex colorValues = particle.getSegmentColor(i);
            final Color color = new Color((int) (colorValues.z * 255), (int) (colorValues.y * 255), (int) (colorValues.x * 255));

            final JButton button = new JButton("Color " + (i + 1), new ImageIcon(IconUtils.createBlank(color, 32, 32)));

            colors[i] = color;

            final int index = i;
            button.addActionListener(e -> {showColorChooserDialog(button, index);});
            colorButtons[i] = button;
        }

        return colorButtons;
    }

    private void showColorChooserDialog(JButton button, int index) {
        final Color colorChoice = JColorChooser.showDialog(particlePanel,"Chooser Color", colors[index]);
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

    private static void setVerticalLayoutGroup(JComboBox<IdObject> parent, JLabel parentLabel, JLabel imageLabel, JLabel titleLabel, JLabel nameLabel, JTextField nameField, Map<String, Pair<JLabel, JSpinner>> coordinateSpinners, JButton chooseAnimations, JButton[] colorButtons, GroupLayout layout) {
        GroupLayout.ParallelGroup nameGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(nameLabel).addComponent(nameField);
        GroupLayout.ParallelGroup parentGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(parentLabel).addComponent(parent);
        GroupLayout.ParallelGroup axisSpinners = layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(coordinateSpinners.get("X").getKey())
                .addComponent(coordinateSpinners.get("X").getValue())
                .addComponent(coordinateSpinners.get("Y").getKey())
                .addComponent(coordinateSpinners.get("Y").getValue())
                .addComponent(coordinateSpinners.get("Z").getKey())
                .addComponent(coordinateSpinners.get("Z").getValue());
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

    private static void setHorizontalLayoutGroup(JComboBox<IdObject> parent, JLabel parentLabel, JLabel imageLabel, JLabel titleLabel, JLabel nameLabel, JTextField nameField, Map<String, Pair<JLabel, JSpinner>> coordinateSpinners, JButton chooseAnimations, JButton[] colorButtons, GroupLayout layout) {
        GroupLayout.SequentialGroup nameGroup = layout.createSequentialGroup().addComponent(nameLabel).addGap(4).addComponent(nameField);
        GroupLayout.SequentialGroup parentGroup = layout.createSequentialGroup().addComponent(parentLabel).addGap(4).addComponent(parent);
        GroupLayout.SequentialGroup axisSpinners = layout.createSequentialGroup()
                .addComponent(coordinateSpinners.get("X").getKey())
                .addComponent(coordinateSpinners.get("X").getValue()).addGap(4)
                .addComponent(coordinateSpinners.get("Y").getKey())
                .addComponent(coordinateSpinners.get("Y").getValue()).addGap(4)
                .addComponent(coordinateSpinners.get("Z").getKey())
                .addComponent(coordinateSpinners.get("Z").getValue());
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
