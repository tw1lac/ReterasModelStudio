package com.hiveworkshop.rms.ui.application;

import com.hiveworkshop.rms.editor.model.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomParticlePanel {

	DefaultListModel<Bitmap> bitmapListModel;
	private JComboBox<Object> textureChooser;
	private EditableModel model;

	private void customParticle(MainPanel mainPanel) {
		model = mainPanel.currentMDL();
		if (model != null) {

			final JPanel particlePanel = new JPanel(new MigLayout());
			final JLabel nameLabel = new JLabel("Particle Name:");
			particlePanel.add(nameLabel);
			final JTextField nameField = new JTextField("My new Particle");
			particlePanel.add(nameField);

			particlePanel.add(new JLabel("Texture"));
			textureChooser = new JComboBox<>(getTextures(model));
			textureChooser.setModel(new DefaultComboBoxModel<>(getTextures(model)));
			particlePanel.add(textureChooser);

			particlePanel.add(new JLabel("Speed"));
			JSpinner staticSpeed = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
			particlePanel.add(staticSpeed, "wrap");
			particlePanel.add(new JLabel("Variation"));
			JSpinner staticVariation = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
			particlePanel.add(staticVariation, "wrap");
			particlePanel.add(new JLabel("Latitude"));
			JSpinner staticLatitude = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
			particlePanel.add(staticLatitude, "wrap");
			particlePanel.add(new JLabel("Gravity"));
			JSpinner staticGravity = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
			particlePanel.add(staticGravity, "wrap");
			particlePanel.add(new JLabel("LifeSpan"));
			JSpinner LifeSpan = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
			particlePanel.add(LifeSpan, "wrap");
			particlePanel.add(new JLabel("Width"));
			JSpinner staticWidth = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
			particlePanel.add(staticWidth, "wrap");
			particlePanel.add(new JLabel("Length"));
			JSpinner staticLength = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
			particlePanel.add(staticLength, "wrap");
			particlePanel.add(new JLabel("TailLength"));
			JSpinner staticTailLength = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
			particlePanel.add(staticTailLength, "wrap");
			particlePanel.add(new JLabel("Time"));
			JSpinner staticTime = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
			particlePanel.add(staticTime, "wrap");

//			particlePanel.add(, "wrap");
//			particlePanel.add(new JLabel(""));


			final ParticleEmitter2 particle = new ParticleEmitter2();


			final java.util.List<IdObject> idObjects = new ArrayList<>(mainPanel.currentMDL().getIdObjects());
			final Bone nullBone = new Bone("No parent");
			idObjects.add(0, nullBone);

			final JComboBox<IdObject> chooseParticleParentBone = new JComboBox<>(idObjects.toArray(new IdObject[0]));

//			chooseParticleParentBone.setRenderer(createParticleParentComboBox(nullBone));
			final JLabel particleParentChooserLabel = new JLabel("Parent:");


//			Map<String, Pair<JLabel, JSpinner>> coordinateSpinners = createCoordinateSpinners();

//			chooseParticleParentBone.addActionListener(e14 -> setSpinnersToParentBoneCoordinates(chooseParticleParentBone, coordinateSpinners));

			final JPanel animPanel = new JPanel();
			final List<Animation> anims = mainPanel.currentMDL().getAnims();
			final JCheckBox[] checkBoxes = new JCheckBox[anims.size()];

//			final JButton chooseAnimations = createAnimationChooserButton(particlePanel, animPanel, anims, checkBoxes);


			final JButton[] colorButtons = new JButton[3];
			final Color[] colors = new Color[colorButtons.length];
//			makeColorButtons(particlePanel, particle, colorButtons, colors);

//			makeParticlePanelLayout(particlePanel, chooseParticleParentBone, particleParentChooserLabel , imageLabel, titleLabel, nameLabel, nameField, coordinateSpinners, chooseAnimations, colorButtons);

			final int x = JOptionPane.showConfirmDialog(mainPanel, particlePanel, "Add New Particle", JOptionPane.OK_CANCEL_OPTION);
			if (x == JOptionPane.OK_OPTION) {
//				addParticleEmitter(mainPanel, particle, nullBone, chooseParticleParentBone, nameField, coordinateSpinners, anims, checkBoxes, colors);
			}
		}
	}

	private String[] getTextures(EditableModel model) {

		bitmapListModel = new DefaultListModel<>();
		List<String> bitmapNames = new ArrayList<>();

		for (final Bitmap bitmap : model.getTextures()) {
			bitmapNames.add(bitmap.getName());
			bitmapListModel.addElement(bitmap);
		}

		return bitmapNames.toArray(new String[0]);
	}
}
