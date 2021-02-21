package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.VisibilitySource;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlag;
import com.hiveworkshop.rms.editor.model.animflag.FloatAnimFlag;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class VisibilityPanel extends JPanel {
	static final String NOTVISIBLE = "Not visible";
	static final String VISIBLE = "Always visible";
	JLabel oldAnimsLabel;
	JComboBox<Object> oldSourcesBox;
	JLabel newAnimsLabel;
	JComboBox<Object> newSourcesBox;
	JCheckBox favorOld;
	VisibilityShell sourceShell;

	JLabel title;

	protected VisibilityPanel() {
		// for use in multi pane
	}

	public VisibilityPanel(final VisibilityShell sourceShell, final DefaultComboBoxModel<Object> oldSources,
	                       final DefaultComboBoxModel<Object> newSources, final VisShellBoxCellRenderer renderer) {
		this.sourceShell = sourceShell;
		setLayout(new MigLayout("gap 0"));
		setMaximumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(500, 500));
		setMaximumSize(new Dimension(500, 500));

		title = new JLabel(sourceShell.model.getName() + ": " + sourceShell.source.getName());
		title.setFont(new Font("Arial", Font.BOLD, 26));
		title.setMaximumSize(new Dimension(500, 500));
		add(title, "align center, wrap");

		oldAnimsLabel = new JLabel("Existing animation visibility from: ");
		add(oldAnimsLabel, "left, wrap");

		oldSourcesBox = new JComboBox<>(oldSources);
		oldSourcesBox.setEditable(false);
		oldSourcesBox.setMaximumSize(new Dimension(800, 25));
		oldSourcesBox.setRenderer(renderer);
		boolean didContain = false;
		for (int i = 0; (i < oldSources.getSize()) && !didContain; i++) {
			if (sourceShell == oldSources.getElementAt(i)) {
				didContain = true;
			}
		}
		if (didContain) {
			oldSourcesBox.setSelectedItem(sourceShell);
		} else {
			oldSourcesBox.setSelectedItem(VISIBLE);
		}
		add(oldSourcesBox, "grow, wrap");

		newAnimsLabel = new JLabel("Imported animation visibility from: ");
		add(newAnimsLabel, "left, wrap");

		newSourcesBox = new JComboBox<>(newSources);
		newSourcesBox.setEditable(false);
		newSourcesBox.setMaximumSize(new Dimension(800, 25));
		newSourcesBox.setRenderer(renderer);
		didContain = false;
		for (int i = 0; (i < newSources.getSize()) && !didContain; i++) {
			if (sourceShell == newSources.getElementAt(i)) {
				didContain = true;
			}
		}
		if (didContain) {
			newSourcesBox.setSelectedItem(sourceShell);
		} else {
			newSourcesBox.setSelectedItem(VISIBLE);
		}
		add(newSourcesBox, "grow, wrap");

		favorOld = new JCheckBox("Favor component's original visibility when combining");
		favorOld.setSelected(true);
		add(favorOld, "left, wrap");
	}

	public void selectSimilarOptions() {
		final ListModel oldSources = oldSourcesBox.getModel();
		for (int i = 0; i < oldSources.getSize(); i++) {
			if (!(oldSources.getElementAt(i) instanceof String)) {
				if (sourceShell.source.getName().equals(((VisibilityShell) oldSources.getElementAt(i)).source.getName())) {
					System.out.println(sourceShell.source.getName());
					oldSourcesBox.setSelectedItem(oldSources.getElementAt(i));
				}
			}
		}
		final ListModel newSources = newSourcesBox.getModel();
		for (int i = 0; i < newSources.getSize(); i++) {
			if (!(newSources.getElementAt(i) instanceof String)) {
				if (sourceShell.source.getName().equals(((VisibilityShell) newSources.getElementAt(i)).source.getName())) {
					System.out.println(sourceShell.source.getName());
					newSourcesBox.setSelectedItem(newSources.getElementAt(i));
				}
			}
		}
	}

	private static void deleteFlagAnimations(List<Animation> anims, FloatAnimFlag flag) {
		for (final Animation a : anims) {
			if (flag != null) {
				if (!flag.hasGlobalSeq()) {
					flag.deleteAnim(a);
				}
			}
		}
	}

	private static FloatAnimFlag getVisAnimFlag(List<Animation> anims, boolean tans, Object source) {
		FloatAnimFlag flag = null;
		if (source != null) {
			if (source.getClass() == String.class) {
				if (source == VisibilityPanel.NOTVISIBLE) {
					flag = new FloatAnimFlag("temp");
					for (final Animation a : anims) {
						if (tans) {
							flag.addEntry(a.getStart(), (float) 0, (float) 0, (float) 0);
						} else {
							flag.addEntry(a.getStart(), (float) 0);
						}
					}
				}
			} else {
				flag = (FloatAnimFlag) ((VisibilitySource) ((VisibilityShell) source).source).getVisibilityFlag();
			}
		}
		return flag;
	}

	public void addSelectedVisFlags(java.util.List<Animation> oldAnims, java.util.List<Animation> newAnims, boolean clearAnims, List<FloatAnimFlag> finalVisFlags, EditableModel currentModel, EditableModel importedModel) {
		final VisibilitySource temp = ((VisibilitySource) sourceShell.source);
		final AnimFlag<?> visFlag = temp.getVisibilityFlag();// might be null
		final FloatAnimFlag newVisFlag;
		boolean tans = false;
		if (visFlag != null) {
			newVisFlag = (FloatAnimFlag) AnimFlag.buildEmptyFrom(visFlag);
			tans = visFlag.tans();
		} else {
			newVisFlag = new FloatAnimFlag(temp.visFlagName());
		}
		// newVisFlag = new AnimFlag(temp.visFlagName());
		final Object oldSource = oldSourcesBox.getSelectedItem();
		FloatAnimFlag flagOld = getVisAnimFlag(oldAnims, tans, oldSource);
		final Object newSource = newSourcesBox.getSelectedItem();
		FloatAnimFlag flagNew = getVisAnimFlag(newAnims, tans, newSource);
		if ((favorOld.isSelected() && sourceShell.model == currentModel && !clearAnims) || (!favorOld.isSelected() && sourceShell.model == importedModel)) {
			// this is an element favoring existing animations over imported
			deleteFlagAnimations(oldAnims, flagNew);
			// All entries for visibility are deleted from imported sources during existing animation times
		} else {
			// this is an element not favoring existing over imported
			deleteFlagAnimations(newAnims, flagOld);
			// All entries for visibility are deleted from original-based sources during imported animation times
		}
		if (flagOld != null) {
			newVisFlag.copyFrom(flagOld);
		}
		if (flagNew != null) {
			newVisFlag.copyFrom(flagNew);
		}
		finalVisFlags.add(newVisFlag);
	}
}
