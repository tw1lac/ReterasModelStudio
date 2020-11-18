package com.hiveworkshop.wc3.gui.modeledit.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.hiveworkshop.wc3.gui.modeledit.actions.componenttree.material.SetMaterialPriorityPlaneAction;
import com.hiveworkshop.wc3.gui.modeledit.actions.componenttree.material.SetMaterialShaderStringAction;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.gui.modeledit.activity.UndoActionListener;
import com.hiveworkshop.wc3.gui.modeledit.components.editors.ComponentEditorJSpinner;
import com.hiveworkshop.wc3.gui.modeledit.components.editors.ComponentEditorTextField;
import com.hiveworkshop.wc3.gui.modeledit.components.material.ComponentMaterialLayersPanel;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mdl.Material;
import com.hiveworkshop.wc3.mdl.v2.ModelViewManager;

import net.miginfocom.swing.MigLayout;

public class ComponentMaterialPanel extends JPanel implements ComponentPanel {
	private static final String SD = "SD";
	private static final String HD = "HD";
	private Material material;
	private UndoActionListener undoActionListener;
	private ModelStructureChangeListener modelStructureChangeListener;

	private final JComboBox<String> shaderOptionComboBox;
	private ComponentEditorJSpinner priorityPlaneSpinner;
	private ComponentEditorTextField comboBoxEditor;
	private boolean listenForChanges = true;
	private ComponentMaterialLayersPanel multipleLayersPanel;

	public ComponentMaterialPanel() {
		final String[] shaderOptions = { "", "Shader_SD_FixedFunction", "Shader_HD_DefaultUnit" };
		shaderOptionComboBox = new JComboBox<>(shaderOptions);
		shaderOptionComboBox.setRenderer(new BasicComboBoxRenderer() {
			@Override
			protected void paintComponent(final Graphics g) {
				super.paintComponent(g);
				if ((getText() == null) || getText().isEmpty()) {
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("<empty>", 0, (getHeight() + g.getFontMetrics().getMaxAscent()) / 2);
				}
			}
		});
		shaderOptionComboBox.setEditor(new BasicComboBoxEditor() {
			@Override
			protected JTextField createEditorComponent() {
				final ComponentEditorTextField editor = new ComponentEditorTextField("", 9) {
					@Override
					protected void paintComponent(final Graphics g) {
						super.paintComponent(g);
						if ((getText() == null) || getText().isEmpty()) {
							g.setColor(Color.LIGHT_GRAY);
							g.drawString("<empty>", 0, (getHeight() + g.getFontMetrics().getMaxAscent()) / 2);
						}
					}

					@Override
					public void setText(final String s) {
						if (getText().equals(s)) {
							return;
						}
						super.setText(s);
					}

					@Override
					public void setBorder(final Border b) {
						if (!(b instanceof UIResource)) {
							super.setBorder(b);
						}
					}
				};
				comboBoxEditor = editor;
				editor.setBorder(null);
				return editor;
			}
		});
		shaderOptionComboBox.setEditable(true);

		shaderOptionComboBox.addActionListener(e -> {
			if (listenForChanges) {
				final SetMaterialShaderStringAction setMaterialShaderStringAction = new SetMaterialShaderStringAction(
						material, material.getShaderString(), (String) shaderOptionComboBox.getSelectedItem(),
						modelStructureChangeListener);
				setMaterialShaderStringAction.redo();
				undoActionListener.pushAction(setMaterialShaderStringAction);
			}
		});

		priorityPlaneSpinner = new ComponentEditorJSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
		priorityPlaneSpinner.addActionListener(() -> {
			final SetMaterialPriorityPlaneAction setMaterialPriorityPlaneAction = new SetMaterialPriorityPlaneAction(
					material, material.getPriorityPlane(), ((Number) priorityPlaneSpinner.getValue()).intValue(),
					modelStructureChangeListener);
			setMaterialPriorityPlaneAction.redo();
			undoActionListener.pushAction(setMaterialPriorityPlaneAction);
		});

		multipleLayersPanel = new ComponentMaterialLayersPanel();

		setLayout(new MigLayout("fill", "[][grow][grow]", "[][][grow]"));
		add(new JLabel("Shader:"));
		add(shaderOptionComboBox, "wrap, growx, span 2");
		add(new JLabel("Priority Plane:"));
		add(priorityPlaneSpinner, "wrap, growx, span 2");
		add(multipleLayersPanel, "growx, growy, span 3");
	}

	public void setMaterial(final Material material, final ModelViewManager modelViewManager,
			final UndoActionListener undoActionListener,
			final ModelStructureChangeListener modelStructureChangeListener) {
		this.material = material;
		this.undoActionListener = undoActionListener;
		this.modelStructureChangeListener = modelStructureChangeListener;

		String shaderString;
		if (material.getShaderString() != null) {
			shaderString = material.getShaderString();
		} else {
			shaderString = "";
		}
		listenForChanges = false;
		try {
			shaderOptionComboBox.setSelectedItem(shaderString);
			comboBoxEditor.setColorToSaved();
			priorityPlaneSpinner.reloadNewValue(material.getPriorityPlane());
		} finally {
			listenForChanges = true;
		}
		final boolean useHDPanel = Material.SHADER_HD_DEFAULT_UNIT.equals(shaderString);
		multipleLayersPanel.setMaterial(material, modelViewManager, undoActionListener, modelStructureChangeListener);
	}

	@Override
	public void save(final EditableModel model, final UndoActionListener undoListener,
			final ModelStructureChangeListener changeListener) {
	}

}
