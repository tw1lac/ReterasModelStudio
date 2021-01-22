package com.hiveworkshop.rms.ui.application.tools;

import com.hiveworkshop.rms.editor.model.Bitmap;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.filesystem.sources.DataSource;
import com.hiveworkshop.rms.parsers.blp.BLPHandler;
import com.hiveworkshop.rms.ui.application.FileDialog;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.util.ZoomableImagePreviewPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EditTexturesPopupPanel extends JPanel {
	private final JTextField pathField;
	private final JPanel imageViewerPanel;
	private final FileDialog fileDialog;

	/**
	 * Create the panel.
	 */
	public EditTexturesPopupPanel(final ModelView modelView, final ModelStructureChangeListener listener) {
		fileDialog = new FileDialog(this);
		setLayout(null);

		final JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Textures", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(16, 17, 297, 507);
		panel.setLayout(new BorderLayout(0, 0));
		add(panel);

		final JCheckBox chckbxDisplayPath = new JCheckBox("Display Path");

		final DefaultListModel<Bitmap> bitmapListModel = new DefaultListModel<>();
		final JList<Bitmap> list = new JList<>();
		chckbxDisplayPath.addActionListener(e -> list.repaint());

		Bitmap defaultTexture = getBitmaps(modelView, bitmapListModel);

		list.setModel(bitmapListModel);
		list.setCellRenderer(getCellRenderer(chckbxDisplayPath));
		list.addListSelectionListener(e -> ListListener(modelView, list));
		panel.add(new JScrollPane(list));

		panel.add(chckbxDisplayPath, BorderLayout.SOUTH);

		imageViewerPanel = new JPanel();
		imageViewerPanel.setBorder(new TitledBorder(null, "Image Viewer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		imageViewerPanel.setBounds(323, 17, 439, 507);
		imageViewerPanel.setLayout(new BorderLayout());
		add(imageViewerPanel);

		loadBitmap(modelView, defaultTexture);

		final JButton importButton = new JButton("Import");
		importButton.setBounds(26, 535, 89, 23);
		importButton.addActionListener(e -> importTexturePopup(modelView, listener, bitmapListModel, list));
		add(importButton);

		final JButton exportButton = new JButton("Export");
		exportButton.setBounds(125, 535, 89, 23);
		exportButton.addActionListener(e -> exportTexture(list));
		add(exportButton);

		final JButton btnReplaceTexture = new JButton("Replace Texture");
		btnReplaceTexture.setBounds(25, 569, 185, 23);
		btnReplaceTexture.addActionListener(e -> btnReplaceTexture(modelView, listener, list));
		add(btnReplaceTexture);

		final JButton btnRemove = new JButton("Remove");
		btnRemove.setBounds(224, 535, 89, 23);
		btnRemove.addActionListener(e -> btnRemoveTexture(modelView, listener, list, bitmapListModel));
		add(btnRemove);

		final JButton btnEditTexture = new JButton("Edit Path");
		btnEditTexture.setBounds(415, 535, 88, 23);
		btnEditTexture.addActionListener(e -> btnEditTexture(list, modelView, listener));
		add(btnEditTexture);

		pathField = new JTextField();
		pathField.setBounds(513, 535, 249, 20);
		pathField.setColumns(10);
		pathField.addActionListener(e -> btnEditTexture(list, modelView, listener));
		add(pathField);

		final JButton btnAdd = new JButton("Add Path");
		btnAdd.setBounds(415, 569, 89, 23);
		btnAdd.addActionListener(e -> btnAddTexture(modelView, listener, bitmapListModel));
		add(btnAdd);

	}

	private void ListListener(ModelView modelView, JList<Bitmap> list) {
		final Bitmap selectedValue = list.getSelectedValue();
		if (selectedValue != null) {
			pathField.setText(selectedValue.getPath());
			loadBitmap(modelView, list.getSelectedValue());
		}
	}

	private DefaultListCellRenderer getCellRenderer(JCheckBox chckbxDisplayPath) {
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

	private Bitmap getBitmaps(ModelView modelView, DefaultListModel<Bitmap> bitmapListModel) {
		Bitmap defaultTexture = null;
		for (final Bitmap bitmap : modelView.getModel().getTextures()) {
			if ((bitmap.getPath() != null) && (bitmap.getPath().length() > 0)) {
				bitmapListModel.addElement(bitmap);
				if (defaultTexture == null) {
					defaultTexture = bitmap;
				}
			}
		}
		return defaultTexture;
	}

	private void importTexturePopup(ModelView modelView, ModelStructureChangeListener listener, DefaultListModel<Bitmap> bitmapListModel, JList<Bitmap> list) {
		final Bitmap newBitmap = fileDialog.importImage();
		if (newBitmap != null) {
			modelView.getModel().add(newBitmap);
			bitmapListModel.addElement(newBitmap);
			list.setSelectedIndex(bitmapListModel.size() - 1);
			listener.texturesChanged();
		}
	}

	private void exportTexture(JList<Bitmap> list) {
		final Bitmap selectedValue = list.getSelectedValue();
		if (selectedValue != null) {
			fileDialog.exportImage(selectedValue);
		}
	}

	private void btnAddTexture(ModelView modelView, ModelStructureChangeListener listener, DefaultListModel<Bitmap> bitmapListModel) {
		final String path = JOptionPane.showInputDialog(EditTexturesPopupPanel.this, "Enter texture path:",
				"Add Texture", JOptionPane.PLAIN_MESSAGE);
		if (path != null) {
			final Bitmap newBitmap = new Bitmap(path);
			modelView.getModel().add(newBitmap);
			bitmapListModel.addElement(newBitmap);
			listener.texturesChanged();
		}
	}

	private void btnEditTexture(JList<Bitmap> list, ModelView modelView, ModelStructureChangeListener listener) {
		final Bitmap selectedValue = list.getSelectedValue();
		if (selectedValue != null) {
			selectedValue.setPath(pathField.getText());
			list.repaint();
			loadBitmap(modelView, selectedValue);
			listener.texturesChanged();
		}
	}

	private void btnRemoveTexture(ModelView modelView, ModelStructureChangeListener listener, JList<Bitmap> list, DefaultListModel<Bitmap> bitmapListModel) {
		final Bitmap selectedValue = list.getSelectedValue();
		if (selectedValue != null) {
			modelView.getModel().remove(selectedValue);
			bitmapListModel.removeElement(selectedValue);
			listener.texturesChanged();
		}
	}

	private void btnReplaceTexture(ModelView modelView, ModelStructureChangeListener listener, JList<Bitmap> list) {
		final Bitmap selectedValue = list.getSelectedValue();
		if (selectedValue != null) {
			Bitmap newBitmap = fileDialog.importImage();
			if (newBitmap != null) {
				selectedValue.setPath(newBitmap.getPath());
				pathField.setText(selectedValue.getPath());
				list.repaint();
				loadBitmap(modelView, selectedValue);
				listener.texturesChanged();
			}
		}
	}

	private void loadBitmap(final ModelView modelView, final Bitmap defaultTexture) {
		if (defaultTexture != null) {
			final DataSource workingDirectory = modelView.getModel().getWrappedDataSource();
			imageViewerPanel.removeAll();
			try {
				imageViewerPanel.add(new ZoomableImagePreviewPanel(BLPHandler.get().getTexture(workingDirectory, defaultTexture.getPath())));
			} catch (final Exception exc) {
				final BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
				final Graphics2D g2 = image.createGraphics();
				g2.setColor(Color.RED);
				g2.drawString(exc.getClass().getSimpleName() + ": " + exc.getMessage(), 15, 15);
				imageViewerPanel.add(new ZoomableImagePreviewPanel(image));
			}
			imageViewerPanel.revalidate();
		}
	}
}
