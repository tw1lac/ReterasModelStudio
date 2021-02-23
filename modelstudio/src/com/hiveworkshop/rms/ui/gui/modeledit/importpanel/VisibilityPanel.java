package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.VisibilitySource;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlag;
import com.hiveworkshop.rms.editor.model.animflag.FloatAnimFlag;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

class VisibilityPanel extends JPanel {
	static final String NOTVISIBLE = "Not visible";
	static final String VISIBLE = "Always visible";
	JLabel oldAnimsLabel;
	JComboBox<VisibilityShell> oldSourcesBox;
	JLabel newAnimsLabel;
	JComboBox<VisibilityShell> newSourcesBox;
	JCheckBox favorOld;
	VisibilityShell sourceShell;
	VisibilityShell currSourceShell;

	JLabel title;
	private ModelHolderThing mht;

	protected VisibilityPanel() {
		// for use in multi pane
	}

	public VisibilityPanel(ModelHolderThing mht, final VisShellBoxCellRenderer renderer) {
		this.mht = mht;
		setLayout(new MigLayout("gap 0"));
		setPreferredSize(new Dimension(500, 500));

		title = new JLabel("Title title");
		title.setFont(new Font("Arial", Font.BOLD, 26));
		title.setMaximumSize(new Dimension(500, 500));
		add(title, "align center, wrap");

		oldAnimsLabel = new JLabel("Existing animation visibility from: ");
		add(oldAnimsLabel, "left, wrap");

//		oldSourcesBox = createObjectJComboBox(mht.visSourcesOld, renderer, e -> setVisGroupItemOld(e));
		oldSourcesBox = createObjectJComboBox(mht.visSourcesOld, renderer, e -> setVisGroupItemOld());
		add(oldSourcesBox, "grow, wrap");


		newAnimsLabel = new JLabel("Imported animation visibility from: ");
		add(newAnimsLabel, "left, wrap");

//		newSourcesBox = createObjectJComboBox(mht.visSourcesNew, renderer, e -> setVisGroupItemNew(e));
		newSourcesBox = createObjectJComboBox(mht.visSourcesNew, renderer, e -> setVisGroupItemNew());
		add(newSourcesBox, "grow, wrap");

		favorOld = new JCheckBox("Favor component's original visibility when combining");
		favorOld.setSelected(true);
		favorOld.addActionListener(e -> changeFavorOld());
		add(favorOld, "left, wrap");
	}

//	public static void selectSimilarOptions(VisibilityShell sourceShell) {
////		for (mht)
//		final ListModel oldSources = oldSourcesBox.getModel();
//		for (int i = 0; i < oldSources.getSize(); i++) {
//			if (!(oldSources.getElementAt(i) instanceof String)) {
//				if (sourceShell.source.getName().equals(((VisibilityShell) oldSources.getElementAt(i)).source.getName())) {
//					System.out.println(sourceShell.source.getName());
//					oldSourcesBox.setSelectedItem(oldSources.getElementAt(i));
//				}
//			}
//		}
//		final ListModel newSources = newSourcesBox.getModel();
//		for (int i = 0; i < newSources.getSize(); i++) {
//			if (!(newSources.getElementAt(i) instanceof String)) {
//				if (sourceShell.source.getName().equals(((VisibilityShell) newSources.getElementAt(i)).source.getName())) {
//					System.out.println(sourceShell.source.getName());
//					newSourcesBox.setSelectedItem(newSources.getElementAt(i));
//				}
//			}
//		}
//	}

	public void setCurrSourceShell(VisibilityShell visibilityShell) {
		System.out.println("setting visShell to: " + visibilityShell);
		currSourceShell = visibilityShell;
		setTitle();
		setSelectedSources();
		favorOld.setSelected(currSourceShell.getFavorOld());
	}

	public void setTitle() {
		title.setText(currSourceShell.model.getName() + ": " + currSourceShell.source.getName());
	}

	public void setSelectedSources() {
//		setSelectedOld();
//		setSelectedNew();
		setSelected(oldSourcesBox, currSourceShell.getOldVisSource());
		setSelected(newSourcesBox, currSourceShell.getNewVisSource());
	}

	public void changeFavorOld() {
		currSourceShell.setFavorOld(favorOld.isSelected());
	}

	private void setSelected(JComboBox<VisibilityShell> jComboBox, VisibilityShell sourceShell) {
		jComboBox.setSelectedIndex(0);
		if (sourceShell != null) {
			jComboBox.setSelectedItem(sourceShell);
		} else {
			System.out.println("source shell was null");
		}
	}

	private void setSelectedNew() {
		newSourcesBox.setSelectedIndex(0);
		if (currSourceShell.getNewVisSource() != null) {
			newSourcesBox.setSelectedItem(currSourceShell.getNewVisSource());
		} else {
			System.out.println("NEW source shell was null");
			currSourceShell.setNewVisSource((VisibilityShell) newSourcesBox.getSelectedItem());
		}
	}

	private void setSelectedOld() {
		oldSourcesBox.setSelectedIndex(0);
		if (currSourceShell.getOldVisSource() != null) {
			oldSourcesBox.setSelectedItem(currSourceShell.getOldVisSource());
		} else {
			currSourceShell.setOldVisSource((VisibilityShell) oldSourcesBox.getSelectedItem());
		}
	}

	public void setVisGroupItemOld(ActionEvent e) {
		currSourceShell.setOldVisSource((VisibilityShell) oldSourcesBox.getSelectedItem());
	}

	public void setVisGroupItemNew(ActionEvent e) {
		currSourceShell.setNewVisSource((VisibilityShell) newSourcesBox.getSelectedItem());
	}

	public void setVisGroupItemOld() {
		currSourceShell.setOldVisSource((VisibilityShell) oldSourcesBox.getSelectedItem());
	}

	public void setVisGroupItemNew() {
		currSourceShell.setNewVisSource((VisibilityShell) newSourcesBox.getSelectedItem());
	}

	private JComboBox<VisibilityShell> createObjectJComboBox(List<VisibilityShell> visList, VisShellBoxCellRenderer renderer, ActionListener actionListener) {
		DefaultComboBoxModel<VisibilityShell> boxModel = new DefaultComboBoxModel<>(visList.toArray(VisibilityShell[]::new));
		JComboBox<VisibilityShell> jComboBox = new JComboBox<>(boxModel);
		jComboBox.setEditable(false);
		jComboBox.setMaximumSize(new Dimension(500, 25));
		jComboBox.setRenderer(renderer);
		jComboBox.addActionListener(actionListener);
		return jComboBox;
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
