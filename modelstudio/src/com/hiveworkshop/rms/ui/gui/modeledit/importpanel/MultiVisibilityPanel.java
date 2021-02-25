package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

class MultiVisibilityPanel extends VisibilityPanel implements ChangeListener, ItemListener {
	boolean oldVal = true;
	ImportPanel impPanel;
	private ModelHolderThing mht;

	public MultiVisibilityPanel(ModelHolderThing mht, final VisShellBoxCellRenderer renderer) {
		this.mht = mht;
		setLayout(new MigLayout("debug"));
		setMaximumSize(new Dimension(700, 500));

		title = new JLabel("Multiple Selected");
		title.setFont(new Font("Arial", Font.BOLD, 26));

		oldAnimsLabel = new JLabel("Existing animation visibility from: ");
		oldSourcesBox = createObjectJComboBox(mht.recModelVisSources, renderer, e -> setVisGroupItemOld());

		newAnimsLabel = new JLabel("Imported animation visibility from: ");
		newSourcesBox = createObjectJComboBox(mht.donModelVisSources, renderer, e -> setVisGroupItemNew());

		favorOld = new JCheckBox("Favor component's original visibility when combining");
		favorOld.setSelected(true);
		favorOld.addChangeListener(this);

		add(title, "cell 0 0, spanx, align center, wrap");
		add(oldAnimsLabel, "cell 0 1");
		add(oldSourcesBox, "cell 1 1");
		add(newAnimsLabel, "cell 0 2");
		add(newSourcesBox, "cell 1 2");
		add(favorOld, "cell 0 3");
	}

	public static void setVisGroupSelected(JList<VisibilityShell> visTabs, final boolean flag) {
		final Object[] selected = visTabs.getSelectedValuesList().toArray();
		for (Object o : selected) {
			final VisibilityPanel temp = (VisibilityPanel) o;
			temp.favorOld.setSelected(flag);
		}
	}

	private JComboBox<VisibilityShell> createObjectJComboBox(List<VisibilityShell> visList, VisShellBoxCellRenderer renderer, ActionListener actionListener) {
		DefaultComboBoxModel<VisibilityShell> boxModel = new DefaultComboBoxModel<>(visList.toArray(VisibilityShell[]::new));
		JComboBox<VisibilityShell> jComboBox = new JComboBox<>(boxModel);
		jComboBox.setEditable(false);
		jComboBox.setMaximumSize(new Dimension(500, 25));
		jComboBox.setRenderer(renderer);
		jComboBox.addActionListener(actionListener);
//		jComboBox.addItemListener(this);
		return jComboBox;
	}

	public void setVisGroupItemOld() {
		final List<VisibilityShell> selectedValuesList = mht.visTabs.getSelectedValuesList();
		for (VisibilityShell value : selectedValuesList) {
//			final VisibilityPanel temp = (VisibilityPanel) value;
//			temp.oldSourcesBox.setSelectedItem(o);
			value.setOldVisSource((VisibilityShell) oldSourcesBox.getSelectedItem());
		}
	}

	public void setMultipleOld() {
		oldSourcesBox.setEditable(true);
		oldSourcesBox.setSelectedItem("Multiple selected");
		oldSourcesBox.setEditable(false);
	}

	public void setMultipleNew() {
		newSourcesBox.setEditable(true);
		newSourcesBox.setSelectedItem("Multiple selected");
		newSourcesBox.setEditable(false);
	}

	public ImportPanel getImportPanel() {
		if (impPanel == null) {
			Container temp = getParent();
			while ((temp != null) && (temp.getClass() != ImportPanel.class)) {
				temp = temp.getParent();
			}
			impPanel = (ImportPanel) temp;
		}
		return impPanel;
	}

	public void setVisGroupItemNew() {
		final List<VisibilityShell> selected = mht.visTabs.getSelectedValuesList();
		for (VisibilityShell value : selected) {
			value.setNewVisSource((VisibilityShell) newSourcesBox.getSelectedItem());
//			value.setNewVisSource(o);
//			temp.newSourcesBox.setSelectedItem(o);
		}
	}

	@Override
	public void stateChanged(final ChangeEvent e) {
		System.out.println("multipanel stateChanged");
//		if (favorOld.isSelected() != oldVal) {
//			setVisGroupSelected(getImportPanel().mht.visTabs, favorOld.isSelected());
//			oldVal = favorOld.isSelected();
//		}
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		System.out.println("multipanel ItemStateListener");
//		if (e.getSource() == oldSourcesBox) {
//			setVisGroupItemOld(getImportPanel().mht.visTabs, (VisibilityShell) oldSourcesBox.getSelectedItem());
//		}
//		if (e.getSource() == newSourcesBox) {
//			setVisGroupItemNew(getImportPanel().mht.visTabs, (VisibilityShell) newSourcesBox.getSelectedItem());
//		}
	}
}
