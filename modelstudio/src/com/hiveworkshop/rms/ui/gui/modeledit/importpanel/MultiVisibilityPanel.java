package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class MultiVisibilityPanel extends VisibilityPanel implements ChangeListener, ItemListener {
	boolean oldVal = true;
	ImportPanel impPanel;

	public MultiVisibilityPanel(final DefaultComboBoxModel<Object> oldSources, final DefaultComboBoxModel<Object> newSources,
	                            final VisShellBoxCellRenderer renderer) {
		setLayout(new MigLayout("debug"));
		setMaximumSize(new Dimension(700, 500));

		title = new JLabel("Multiple Selected");
		title.setFont(new Font("Arial", Font.BOLD, 26));

		oldAnimsLabel = new JLabel("Existing animation visibility from: ");
		oldSourcesBox = createObjectJComboBox(oldSources, renderer);

		newAnimsLabel = new JLabel("Imported animation visibility from: ");
		newSourcesBox = createObjectJComboBox(newSources, renderer);

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

	private JComboBox<Object> createObjectJComboBox(DefaultComboBoxModel<Object> boxModel, VisShellBoxCellRenderer renderer) {
		JComboBox<Object> jComboBox = new JComboBox<>(boxModel);
		jComboBox.setEditable(false);
		jComboBox.setMaximumSize(new Dimension(500, 25));
		jComboBox.setRenderer(renderer);
		jComboBox.addItemListener(this);
		return jComboBox;
	}

	public static void setVisGroupSelected(JList<VisibilityPanel> visTabs, final boolean flag) {
		final Object[] selected = visTabs.getSelectedValuesList().toArray();
		for (Object o : selected) {
			final VisibilityPanel temp = (VisibilityPanel) o;
			temp.favorOld.setSelected(flag);
		}
	}

	public static void setVisGroupItemOld(JList<VisibilityPanel> visTabs, final Object o) {
		final Object[] selected = visTabs.getSelectedValuesList().toArray();
		for (Object value : selected) {
			final VisibilityPanel temp = (VisibilityPanel) value;
			temp.oldSourcesBox.setSelectedItem(o);
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

	public static void setVisGroupItemNew(JList<VisibilityPanel> visTabs, final Object o) {
		final Object[] selected = visTabs.getSelectedValuesList().toArray();
		for (Object value : selected) {
			final VisibilityPanel temp = (VisibilityPanel) value;
			temp.newSourcesBox.setSelectedItem(o);
		}
	}

	@Override
	public void stateChanged(final ChangeEvent e) {
		if (favorOld.isSelected() != oldVal) {
			setVisGroupSelected(getImportPanel().mht.visTabs, favorOld.isSelected());
			oldVal = favorOld.isSelected();
		}
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		if (e.getSource() == oldSourcesBox) {
			setVisGroupItemOld(getImportPanel().mht.visTabs, oldSourcesBox.getSelectedItem());
		}
		if (e.getSource() == newSourcesBox) {
			setVisGroupItemNew(getImportPanel().mht.visTabs, newSourcesBox.getSelectedItem());
		}
	}
}
