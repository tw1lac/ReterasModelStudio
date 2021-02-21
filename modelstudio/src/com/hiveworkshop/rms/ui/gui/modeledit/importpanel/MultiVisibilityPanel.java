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
		oldSourcesBox = new JComboBox<>(oldSources);
		oldSourcesBox.setEditable(false);
		oldSourcesBox.setMaximumSize(new Dimension(500, 25));
		oldSourcesBox.setRenderer(renderer);
		oldSourcesBox.addItemListener(this);

		newAnimsLabel = new JLabel("Imported animation visibility from: ");
		newSourcesBox = new JComboBox<>(newSources);
		newSourcesBox.setEditable(false);
		newSourcesBox.setMaximumSize(new Dimension(500, 25));
		newSourcesBox.setRenderer(renderer);
		newSourcesBox.addItemListener(this);

		favorOld = new JCheckBox("Favor component's original visibility when combining");
		favorOld.setSelected(true);
		favorOld.addChangeListener(this);

		add(title, "cell 0 0, spanx, align center, wrap");
		add(oldAnimsLabel, "cell 0 1");
		add(oldSourcesBox, "cell 1 1");
		add(newAnimsLabel, "cell 0 2");
		add(newSourcesBox, "cell 1 2");
		add(favorOld, "cell 0 3");

//		final GroupLayout layout = new GroupLayout(this);
//		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//				.addComponent(title)
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//						.addComponent(oldAnimsLabel)
//						.addComponent(oldSourcesBox)
//						.addComponent(newAnimsLabel)
//						.addComponent(newSourcesBox)
//						.addComponent(favorOld)));
//		layout.setVerticalGroup(layout.createSequentialGroup()
//				.addComponent(title).addGap(16)
//				.addComponent(oldAnimsLabel)
//				.addComponent(oldSourcesBox)
//				.addComponent(newAnimsLabel)
//				.addComponent(newSourcesBox)
//				.addComponent(favorOld));
//		setLayout(layout);
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
			setVisGroupSelected(getImportPanel().visTabs, favorOld.isSelected());
			oldVal = favorOld.isSelected();
		}
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		if (e.getSource() == oldSourcesBox) {
			setVisGroupItemOld(getImportPanel().visTabs, oldSourcesBox.getSelectedItem());
		}
		if (e.getSource() == newSourcesBox) {
			setVisGroupItemNew(getImportPanel().visTabs, newSourcesBox.getSelectedItem());
		}
	}
}
