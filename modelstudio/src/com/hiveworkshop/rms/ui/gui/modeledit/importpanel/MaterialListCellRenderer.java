package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Material;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

class MaterialListCellRenderer extends DefaultListCellRenderer {
	EditableModel myModel;
	Object myMaterial;
	Font theFont = new Font("Arial", Font.BOLD, 32);
	HashMap<Material, ImageIcon> map = new HashMap<>();

	public MaterialListCellRenderer(final EditableModel model) {
		myModel = model;
	}

	public void setMaterial(final Object o) {
		myMaterial = o;
	}

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
	                                              final boolean iss, final boolean chf) {
		String name = ((Material) value).getName();
		if (value == myMaterial) {
			name = name + " (Original)";
		}
		if (myModel.contains((Material) value)) {
			super.getListCellRendererComponent(list, name, index, iss, chf);

			setIcon(value, ImportPanel.greenIcon);
		} else {
			super.getListCellRendererComponent(list, "Import: " + name, index, iss, chf);
			setIcon(value, ImportPanel.orangeIcon);
		}
		setFont(theFont);
		return this;
	}

	public void setIcon(Object value, ImageIcon orangeIcon) {
		ImageIcon myIcon = map.get(value);
		if (myIcon == null) {
			myIcon = new ImageIcon(Material.mergeImageScaled(
					orangeIcon.getImage(),
					((Material) value).getBufferedImage(myModel.getWrappedDataSource()),
					64, 64, 48, 48));
			map.put((Material) value, myIcon);
		}

		setIcon(myIcon);
	}
}
