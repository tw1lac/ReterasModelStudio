package com.hiveworkshop.wc3.gui.modeledit.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.datachooser.DataSource;
import com.hiveworkshop.wc3.gui.modeledit.actions.componenttree.bitmap.SetBitmapPathAction;
import com.hiveworkshop.wc3.gui.modeledit.actions.componenttree.bitmap.SetBitmapReplaceableIdAction;
import com.hiveworkshop.wc3.gui.modeledit.actions.componenttree.bitmap.SetBitmapWrapHeightAction;
import com.hiveworkshop.wc3.gui.modeledit.actions.componenttree.bitmap.SetBitmapWrapWidthAction;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.gui.modeledit.activity.UndoActionListener;
import com.hiveworkshop.wc3.gui.modeledit.components.editors.ComponentEditorJSpinner;
import com.hiveworkshop.wc3.gui.modeledit.components.editors.ComponentEditorTextField;
import com.hiveworkshop.wc3.gui.modeledit.util.TextureExporter;
import com.hiveworkshop.wc3.gui.modeledit.util.TextureExporter.TextureExporterClickListener;
import com.hiveworkshop.wc3.gui.mpqbrowser.BLPPanel;
import com.hiveworkshop.wc3.mdl.Bitmap;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mdl.v2.ModelView;
import com.hiveworkshop.wc3.mdl.v2.ModelViewManager;

import net.miginfocom.swing.MigLayout;

public class ComponentBitmapPanel extends JPanel implements ComponentPanel {

	private Bitmap bitmap;
	private final ComponentEditorTextField texturePathField;
	private final ComponentEditorJSpinner replaceableIdSpinner;
	private final JCheckBox wrapWidthBox;
	private final JCheckBox wrapHeightBox;
	private final JPanel previewPanel;
	private UndoActionListener undoListener;
	private ModelStructureChangeListener modelStructureChangeListener;
	private ModelViewManager modelViewManager;

	public ComponentBitmapPanel(final TextureExporter textureExporter) {
		texturePathField = new ComponentEditorTextField(24);
		texturePathField.addActionListener(e -> {
			final SetBitmapPathAction setBitmapPathAction = new SetBitmapPathAction(bitmap, bitmap.getPath(),
					texturePathField.getText(), modelStructureChangeListener);
			setBitmapPathAction.redo();
			undoListener.pushAction(setBitmapPathAction);
		});
		replaceableIdSpinner = new ComponentEditorJSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
		replaceableIdSpinner.addActionListener(() -> {
			final SetBitmapReplaceableIdAction setBitmapReplaceableIdAction = new SetBitmapReplaceableIdAction(
					bitmap, bitmap.getReplaceableId(), ((Number) replaceableIdSpinner.getValue()).intValue(),
					modelStructureChangeListener);
			setBitmapReplaceableIdAction.redo();
			undoListener.pushAction(setBitmapReplaceableIdAction);
		});
		wrapWidthBox = new JCheckBox("Wrap Width");
		wrapWidthBox.addActionListener(e -> {
			final SetBitmapWrapWidthAction setBitmapWrapWidthAction = new SetBitmapWrapWidthAction(bitmap,
					bitmap.isWrapWidth(), wrapWidthBox.isSelected(), modelStructureChangeListener);
			setBitmapWrapWidthAction.redo();
			undoListener.pushAction(setBitmapWrapWidthAction);
		});
		wrapHeightBox = new JCheckBox("Wrap Height");
		wrapHeightBox.addActionListener(e -> {
			final SetBitmapWrapHeightAction setBitmapWrapHeightAction = new SetBitmapWrapHeightAction(bitmap,
					bitmap.isWrapHeight(), wrapHeightBox.isSelected(), modelStructureChangeListener);
			setBitmapWrapHeightAction.redo();
			undoListener.pushAction(setBitmapWrapHeightAction);
		});
		previewPanel = new JPanel();
		previewPanel.setBorder(new TitledBorder(null, "Previewer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		previewPanel.setLayout(new BorderLayout());

		setLayout(new MigLayout("fillx", "[][grow][]", "[][][][][grow]"));
		add(new JLabel("Path: "), "cell 0 0");
		add(texturePathField, "cell 1 0 2, growx");
		add(new JLabel("ReplaceableId: "), "cell 0 1");
		add(replaceableIdSpinner, "cell 1 1 2");
		add(wrapWidthBox, "cell 0 2 3");
		add(wrapHeightBox, "cell 0 3");
		final JButton exportTextureImageFile = new JButton("Export Texture Image File");
		exportTextureImageFile.addActionListener(e -> {
			String suggestedName = texturePathField.getText();
			suggestedName = suggestedName.substring(suggestedName.lastIndexOf("\\") + 1);
			suggestedName = suggestedName.substring(suggestedName.lastIndexOf("/") + 1);
			textureExporter.exportTexture(suggestedName, (file, filter) -> BLPHandler.exportBitmapTextureFile(ComponentBitmapPanel.this, modelViewManager, bitmap, file), ComponentBitmapPanel.this);
		});
		add(exportTextureImageFile, "cell 2 3, pushx");
		add(previewPanel, "cell 0 4 3, growx, growy");
	}

	public void setBitmap(final Bitmap bitmap, final ModelViewManager modelViewManager,
			final UndoActionListener undoListener, final ModelStructureChangeListener modelStructureChangeListener) {
		this.bitmap = bitmap;
		this.modelViewManager = modelViewManager;
		this.undoListener = undoListener;
		this.modelStructureChangeListener = modelStructureChangeListener;

		texturePathField.reloadNewValue(bitmap.getPath());
		replaceableIdSpinner.reloadNewValue(bitmap.getReplaceableId());
		wrapWidthBox.setSelected(bitmap.isWrapWidth());
		wrapHeightBox.setSelected(bitmap.isWrapHeight());

		loadBitmapPreview(modelViewManager, bitmap);
	}

	@Override
	public void save(final EditableModel model, final UndoActionListener undoListener,
			final ModelStructureChangeListener changeListener) {

	}

	private void loadBitmapPreview(final ModelView modelView, final Bitmap defaultTexture) {
		if (defaultTexture != null) {
			final DataSource workingDirectory = modelViewManager.getModel().getWrappedDataSource();
			previewPanel.removeAll();
			try {
				final BufferedImage texture = BLPHandler.getImage(defaultTexture, workingDirectory);
				previewPanel.add(new BLPPanel(texture));
			} catch (final Exception exc) {
				final BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
				final Graphics2D g2 = image.createGraphics();
				g2.setColor(Color.RED);
				g2.drawString(exc.getClass().getSimpleName() + ": " + exc.getMessage(), 15, 15);
				previewPanel.add(new BLPPanel(image));
			}
			previewPanel.revalidate();
		}
	}
}
