package com.hiveworkshop.rms.ui.application.model;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.ui.application.actions.model.globalsequence.SetGlobalSequenceLengthAction;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.UndoActionListener;
import com.hiveworkshop.rms.ui.application.model.editors.ComponentEditorJSpinner;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ComponentGlobalSequencePanel extends JPanel {
	private final JLabel indexLabel;
	private final ComponentEditorJSpinner lengthSpinner;
	private EditableModel model;
	private UndoActionListener undoActionListener;
	private ModelStructureChangeListener modelStructureChangeListener;
	private int globalSequenceId;
	private Integer value;

	public ComponentGlobalSequencePanel() {
		setLayout(new MigLayout());
		lengthSpinner = new ComponentEditorJSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		lengthSpinner.addActionListener(() -> {
            final SetGlobalSequenceLengthAction setGlobalSequenceLengthAction = new SetGlobalSequenceLengthAction(
                    model, globalSequenceId, value, ((Number) lengthSpinner.getValue()).intValue(),
                    modelStructureChangeListener);
            setGlobalSequenceLengthAction.redo();
            undoActionListener.pushAction(setGlobalSequenceLengthAction);
        });
		add(new JLabel("GlobalSequence "), "cell 0 0");

		indexLabel = new JLabel();
		add(indexLabel, "cell 1 0");

		add(new JLabel("Duration: "), "cell 0 1");
		add(lengthSpinner, "cell 1 1");
	}

	public void setGlobalSequence(final EditableModel model, final Integer value, final int globalSequenceId,
			final UndoActionListener undoActionListener,
			final ModelStructureChangeListener modelStructureChangeListener) {
		this.model = model;
		this.value = value;
		this.globalSequenceId = globalSequenceId;
		this.undoActionListener = undoActionListener;
		this.modelStructureChangeListener = modelStructureChangeListener;
		indexLabel.setText(Integer.toString(globalSequenceId));
		lengthSpinner.reloadNewValue(value);
	}
}
