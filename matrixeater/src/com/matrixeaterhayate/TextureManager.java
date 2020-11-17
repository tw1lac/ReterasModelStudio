package com.matrixeaterhayate;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.datachooser.DataSource;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.gui.modeledit.util.TextureExporter;
import com.hiveworkshop.wc3.gui.mpqbrowser.BLPPanel;
import com.hiveworkshop.wc3.mdl.Bitmap;
import com.hiveworkshop.wc3.mdl.v2.ModelView;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class TextureManager extends JPanel {
	private final JTextField pathField;
	private final JPanel panel_1;

	/**
	 * Create the panel.
	 */
	public TextureManager(final ModelView modelView, final ModelStructureChangeListener listener,
			final TextureExporter textureExporter) {
		setLayout(null);

		final JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Textures", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(16, 17, 297, 507);
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		final JCheckBox chckbxDisplayPath = new JCheckBox("Display Path");

		final JList<Bitmap> list = new JList<>();
		chckbxDisplayPath.addActionListener(e -> list.repaint());
		panel.add(new JScrollPane(list));
		Bitmap defaultTexture = null;

		final DefaultListModel<Bitmap> bitmapListModel = new DefaultListModel<>();
		for (final Bitmap bitmap : modelView.getModel().getTextures()) {
			if ((bitmap.getPath() != null) && (bitmap.getPath().length() > 0)) {
				bitmapListModel.addElement(bitmap);
				if (defaultTexture == null) {
					defaultTexture = bitmap;
				}
			}
		}

		list.setModel(bitmapListModel);
		list.setCellRenderer(newCellRenderer(chckbxDisplayPath));
		list.addListSelectionListener(NewListSelectionListener(modelView, list));
		panel.add(chckbxDisplayPath, BorderLayout.SOUTH);

		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Image Viewer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(323, 17, 439, 507);
		panel_1.setLayout(new BorderLayout());
		add(panel_1);

		loadBitmap(modelView, defaultTexture);

		final JButton importButton = new JButton("Import");
		importButton.setBounds(26, 535, 89, 23);
		importButton.addActionListener(importButtonAction(modelView, listener, textureExporter, bitmapListModel));
		add(importButton);

		final JButton exportButton = new JButton("Export");
		exportButton.setBounds(125, 535, 89, 23);
		exportButton.addActionListener(exportButtonAction(modelView, textureExporter, list));
		add(exportButton);

		final JButton btnReplaceTexture = new JButton("Replace Texture");
		btnReplaceTexture.setBounds(25, 569, 185, 23);
		btnReplaceTexture.addActionListener(btnReplaceTextureAction(modelView, listener, textureExporter, list));
		add(btnReplaceTexture);

		final JButton btnRemove = new JButton("Remove");
		btnRemove.setBounds(224, 535, 89, 23);
		btnRemove.addActionListener(btnRemoveAction(modelView, listener, list, bitmapListModel));
		add(btnRemove);

		final JButton btnEditTexture = new JButton("Edit Path");
		btnEditTexture.setBounds(415, 535, 88, 23);
		btnEditTexture.addActionListener(btnEditTextureActionListener(modelView, listener, list));
		add(btnEditTexture);

		pathField = new JTextField();
		pathField.setBounds(513, 535, 249, 20);
		pathField.setColumns(10);
		pathField.addActionListener(pathFieldAction(modelView, listener, list));
		add(pathField);

		final JButton btnAdd = new JButton("Add Path");
		btnAdd.setBounds(415, 569, 89, 23);
		btnAdd.addActionListener(btnAddAction(modelView, listener, bitmapListModel));
		add(btnAdd);


	}

	private ListSelectionListener NewListSelectionListener(ModelView modelView, JList<Bitmap> list) {
		return e -> {
			final Bitmap selectedValue = list.getSelectedValue();
			if (selectedValue != null) {
				pathField.setText(selectedValue.getPath());
				loadBitmap(modelView, list.getSelectedValue());
			}
		};
	}

	private DefaultListCellRenderer newCellRenderer(JCheckBox chckbxDisplayPath) {
		return new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
														  final boolean isSelected, final boolean cellHasFocus) {
				if (value instanceof Bitmap) {
					final String path = ((Bitmap) value).getPath();
					if (!chckbxDisplayPath.isSelected()) {
						final String displayName = path.substring(path.lastIndexOf("\\") + 1);
						return super.getListCellRendererComponent(list, displayName, index, isSelected, cellHasFocus);
					} else {
						return super.getListCellRendererComponent(list, path, index, isSelected, cellHasFocus);
					}
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		};
	}

	private ActionListener pathFieldAction(ModelView modelView, ModelStructureChangeListener listener, JList<Bitmap> list) {
		return e -> {
			final Bitmap selectedValue = list.getSelectedValue();
			if (selectedValue != null) {
				selectedValue.setPath(pathField.getText());
				list.repaint();
				loadBitmap(modelView, selectedValue);
				listener.texturesChanged();
			}
		};
	}

	private ActionListener btnAddAction(ModelView modelView, ModelStructureChangeListener listener, DefaultListModel<Bitmap> bitmapListModel) {
		return e -> {
			final String path = JOptionPane.showInputDialog(TextureManager.this, "Enter texture path:",
					"Add Texture", JOptionPane.PLAIN_MESSAGE);
			if (path != null) {
				final Bitmap newBitmap = new Bitmap(path);
				modelView.getModel().add(newBitmap);
				bitmapListModel.addElement(newBitmap);
				listener.texturesChanged();
			}
		};
	}

	private ActionListener btnEditTextureActionListener(ModelView modelView, ModelStructureChangeListener listener, JList<Bitmap> list) {
		return e -> {
			final Bitmap selectedValue = list.getSelectedValue();
			if (selectedValue != null) {
				selectedValue.setPath(pathField.getText());
				list.repaint();
				loadBitmap(modelView, selectedValue);
				listener.texturesChanged();
			}
		};
	}

	private ActionListener btnRemoveAction(ModelView modelView, ModelStructureChangeListener listener, JList<Bitmap> list, DefaultListModel<Bitmap> bitmapListModel) {
		return e -> {
			final Bitmap selectedValue = list.getSelectedValue();
			if (selectedValue != null) {
				modelView.getModel().remove(selectedValue);
				bitmapListModel.removeElement(selectedValue);
				listener.texturesChanged();
			}
		};
	}

	private ActionListener btnReplaceTextureAction(ModelView modelView, ModelStructureChangeListener listener, TextureExporter textureExporter, JList<Bitmap> list) {
		return e -> {
			final Bitmap selectedValue = list.getSelectedValue();
			if (selectedValue != null) {
				textureExporter.showOpenDialog("", (file, filter) -> {
					selectedValue.setPath(file.getName());
					list.repaint();
					loadBitmap(modelView, selectedValue);
					listener.texturesChanged();
				}, TextureManager.this);
			}
		};
	}

	private ActionListener exportButtonAction(ModelView modelView, TextureExporter textureExporter, JList<Bitmap> list) {
		return e -> {
			final Bitmap selectedValue = list.getSelectedValue();
			if (selectedValue != null) {
				String selectedPath = selectedValue.getPath();
				selectedPath = selectedPath.substring(selectedPath.lastIndexOf("\\") + 1);
				textureExporter.exportTexture(selectedPath, (file, filter) -> BLPHandler.exportBitmapTextureFile(TextureManager.this, modelView, selectedValue, file), TextureManager.this);
			}
		};
	}

	private ActionListener importButtonAction(ModelView modelView, ModelStructureChangeListener listener, TextureExporter textureExporter, DefaultListModel<Bitmap> bitmapListModel) {
		return e -> textureExporter.showOpenDialog("", (file, filter) -> {
			final Bitmap newBitmap = new Bitmap(file.getName());
			modelView.getModel().add(newBitmap);
			bitmapListModel.addElement(newBitmap);
			listener.texturesChanged();
		}, TextureManager.this);
	}

	private void loadBitmap(final ModelView modelView, final Bitmap defaultTexture) {
		if (defaultTexture != null) {
			final DataSource workingDirectory = modelView.getModel().getWrappedDataSource();
			panel_1.removeAll();
			try {
				panel_1.add(new BLPPanel(BLPHandler.get().getTexture(workingDirectory, defaultTexture.getPath())));
			} catch (final Exception exc) {
				final BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
				final Graphics2D g2 = image.createGraphics();
				g2.setColor(Color.RED);
				g2.drawString(exc.getClass().getSimpleName() + ": " + exc.getMessage(), 15, 15);
				panel_1.add(new BLPPanel(image));
			}
			panel_1.revalidate();
		}
	}
}
