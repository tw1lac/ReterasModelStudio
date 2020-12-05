package com.hiveworkshop.rms.ui.application.model;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelViewManager;
import com.hiveworkshop.rms.ui.application.actions.model.header.SetBlendTimeAction;
import com.hiveworkshop.rms.ui.application.actions.model.header.SetFormatVersionAction;
import com.hiveworkshop.rms.ui.application.actions.model.header.SetHeaderExtentsAction;
import com.hiveworkshop.rms.ui.application.actions.model.header.SetNameAction;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.UndoActionListener;
import com.hiveworkshop.rms.ui.application.model.editors.ComponentEditorJSpinner;
import com.hiveworkshop.rms.ui.application.model.editors.ComponentEditorTextField;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public class ComponentHeaderPanel extends JPanel implements ComponentPanel {
	private static final Dimension MAXIMUM_SIZE = new Dimension(99999, 25);
	private final ComponentEditorTextField modelNameField;
	private final ComponentEditorJSpinner formatVersionSpinner;
	private final ComponentEditorJSpinner blendTimeSpinner;
	private final ExtLogEditor extLogEditor;
	private ModelViewManager modelViewManager;
	private UndoActionListener undoActionListener;
	private ModelStructureChangeListener changeListener;

	public ComponentHeaderPanel() {
		final JLabel modelNameLabel = new JLabel("Model Name:");
		modelNameField = new ComponentEditorTextField();
		modelNameField.setMaximumSize(MAXIMUM_SIZE);
		modelNameField.addActionListener(e -> {
            if (modelViewManager != null) {
                final SetNameAction action = new SetNameAction(modelViewManager.getModel().getHeaderName(),
                        modelNameField.getText(), modelViewManager, changeListener);
                action.redo();
                undoActionListener.pushAction(action);
            }
        });

        final JLabel versionLabel = new JLabel("Format Version:");
		formatVersionSpinner = new ComponentEditorJSpinner(
				new SpinnerNumberModel(800, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		formatVersionSpinner.setMaximumSize(MAXIMUM_SIZE);
		formatVersionSpinner.addActionListener(() -> {
            if (modelViewManager != null) {
                final SetFormatVersionAction setFormatVersionAction = new SetFormatVersionAction(
                        modelViewManager.getModel().getFormatVersion(),
                        ((Number) formatVersionSpinner.getValue()).intValue(), modelViewManager, changeListener);
                setFormatVersionAction.redo();
                undoActionListener.pushAction(setFormatVersionAction);
            }
        });

        final JLabel blendTimeLabel = new JLabel("Blend Time:");
		blendTimeSpinner = new ComponentEditorJSpinner(
				new SpinnerNumberModel(150, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		blendTimeSpinner.setMaximumSize(MAXIMUM_SIZE);
		blendTimeSpinner.addActionListener(() -> {
            if (modelViewManager != null) {
                final SetBlendTimeAction setFormatVersionAction = new SetBlendTimeAction(
                        modelViewManager.getModel().getBlendTime(),
                        ((Number) blendTimeSpinner.getValue()).intValue(), modelViewManager, changeListener);
                setFormatVersionAction.redo();
                undoActionListener.pushAction(setFormatVersionAction);
            }
        });

		extLogEditor = new ExtLogEditor();
		extLogEditor.setBorder(BorderFactory.createTitledBorder("Extents"));
		extLogEditor.addActionListener(() -> {
            final SetHeaderExtentsAction setHeaderExtentsAction = new SetHeaderExtentsAction(
                    modelViewManager.getModel().getExtents(), extLogEditor.getExtLog(), modelViewManager,
                    changeListener);
            setHeaderExtentsAction.redo();
            undoActionListener.pushAction(setHeaderExtentsAction);
        });

		final GroupLayout layout = new GroupLayout(this);

		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(modelNameLabel).addComponent(modelNameField)
				.addComponent(versionLabel).addComponent(formatVersionSpinner).addComponent(blendTimeLabel)
				.addComponent(blendTimeSpinner).addComponent(extLogEditor));
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(modelNameLabel).addComponent(modelNameField)
				.addComponent(versionLabel).addComponent(formatVersionSpinner).addComponent(blendTimeLabel)
				.addComponent(blendTimeSpinner).addComponent(extLogEditor));
		setLayout(layout);
	}

	private void setModelHeader(final EditableModel model) {
		modelNameField.reloadNewValue(model.getHeaderName());
		formatVersionSpinner.reloadNewValue(model.getFormatVersion());
		blendTimeSpinner.reloadNewValue(model.getBlendTime());
		extLogEditor.setExtLog(model.getExtents());
	}

	public void setActiveModel(final ModelViewManager modelViewManager, final UndoActionListener undoActionListener,
			final ModelStructureChangeListener changeListener) {
		commitEdits();
		this.modelViewManager = modelViewManager;
		this.undoActionListener = undoActionListener;
		this.changeListener = changeListener;
		setModelHeader(modelViewManager.getModel());
	}

	private void commitEdits() {
		try {
			formatVersionSpinner.commitEdit();
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		try {
			blendTimeSpinner.commitEdit();
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		extLogEditor.commitEdits();

	}

	@Override
	public void save(final EditableModel modelOutput, final UndoActionListener undoListener,
			final ModelStructureChangeListener changeListener) {
		modelOutput.setFormatVersion(((Number) formatVersionSpinner.getValue()).intValue());
		modelOutput.setBlendTime(((Number) blendTimeSpinner.getValue()).intValue());
		modelOutput.setExtents(extLogEditor.getExtLog());
	}

}
