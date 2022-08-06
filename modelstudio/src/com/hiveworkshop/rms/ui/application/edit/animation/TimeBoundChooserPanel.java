package com.hiveworkshop.rms.ui.application.edit.animation;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.actions.animation.AddSequenceAction;
import com.hiveworkshop.rms.editor.actions.animation.DuplicateAnimationAction;
import com.hiveworkshop.rms.editor.actions.animation.RemoveSequenceAction;
import com.hiveworkshop.rms.editor.actions.util.CompoundAction;
import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.GlobalSeq;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.UndoManager;
import com.hiveworkshop.rms.ui.application.model.editors.IntEditorJSpinner;
import com.hiveworkshop.rms.ui.application.model.editors.TwiTextField;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.util.TwiList;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class TimeBoundChooserPanel extends JPanel {
	private final ModelStructureChangeListener changeListener = ModelStructureChangeListener.changeListener;
	private final ModelHandler modelHandler;
	private final UndoManager undoManager;
	private final EditableModel model;
	private final JTabbedPane tabs;
	private JSpinner timeStart, timeEnd;
	private Animation selectedAnim;
	private Collection<Animation> selectedAnims;
	private Collection<GlobalSeq> selectedGlobalSeqs;
	private GlobalSeq selectedGlobalSeq;

	public TimeBoundChooserPanel(ModelHandler modelHandler) {
		super(new BorderLayout());
		this.modelHandler = modelHandler;
		this.undoManager = modelHandler.getUndoManager();
		this.model = modelHandler.getModel();

		tabs = new JTabbedPane();
		tabs.addTab("Animation", getAnimationPanel());
		tabs.addTab("Custom Time", getCustomTimePanel());
		tabs.addTab("Global Sequence", getGlobSeqPanel());
		add(tabs);
	}

	private JPanel getAnimationPanel() {
		final JPanel animationPanel = new JPanel(new MigLayout("fill", "[]", "[grow][]"));

		JScrollPane animationScrollPane = new JScrollPane(getAnimationBox());
		animationScrollPane.setPreferredSize(new Dimension(500, 320));
		animationPanel.add(animationScrollPane, "spanx, growx, growy, wrap");

		JPanel buttonPanel = new JPanel(new MigLayout("ins 0"));
		final JButton createAnimation = new JButton("Create");
		createAnimation.addActionListener(e -> new CreateAnimationPopup(modelHandler, null).showPopup());
		buttonPanel.add(createAnimation);

		final JButton duplicateAnimation = new JButton("Duplicate");
		duplicateAnimation.addActionListener(e -> duplicateAnimation());
		buttonPanel.add(duplicateAnimation);


		final JButton editAnimation = new JButton("Edit");
		editAnimation.addActionListener(e -> new CreateAnimationPopup(modelHandler, selectedAnim).showPopup());
		buttonPanel.add(editAnimation);

		final JButton deleteAnimation = new JButton("Delete");
		deleteAnimation.addActionListener(e -> deleteSequences(selectedAnims, "Animation"));
		buttonPanel.add(deleteAnimation);

		animationPanel.add(buttonPanel);
		return animationPanel;
	}
	private TwiList<Animation> getAnimationBox() {
		TwiList<Animation> animationBox = new TwiList<>(model.getAnims());
		animationBox.addSelectionListener1(this::setSelectedAnim);
		animationBox.addMultiSelectionListener(o -> selectedAnims = o);
		return animationBox;
	}

	private JPanel getGlobSeqPanel() {
		final JPanel globSeqPanel = new JPanel(new MigLayout("fill"));

		TwiList<GlobalSeq> globalSeqBox = new TwiList<>(model.getGlobalSeqs());
		globalSeqBox.addSelectionListener1(this::setSelectedGlobalSeq);
		globalSeqBox.addMultiSelectionListener(o -> selectedGlobalSeqs = o);

		JScrollPane globalSeqScrollPane = new JScrollPane(globalSeqBox);
		globalSeqScrollPane.setPreferredSize(new Dimension(500, 320));
		globSeqPanel.add(globalSeqScrollPane, "spanx, growx, growy");

		final JButton createGlobalSeq = new JButton("Create");
//		createGlobalSeq.addActionListener(e -> createGlobalSeq());
		createGlobalSeq.addActionListener(e -> GlobalSeqHelper.showNewGlobSeqPopup(this, "Enter Length", modelHandler));

		final JButton deleteGlobalSeq = new JButton("Delete");
		deleteGlobalSeq.addActionListener(e -> deleteSequences(selectedGlobalSeqs, "GlobalSeq"));

		globSeqPanel.add(createGlobalSeq);
		globSeqPanel.add(deleteGlobalSeq);
		return globSeqPanel;
	}

	private JPanel getCustomTimePanel() {
		Sequence timeBound = getTimeBound(modelHandler.getRenderModel());
		int startTime = 0;
		int endTime = 1000;
		if (timeBound != null) {
			startTime = timeBound.getStart();
			endTime = timeBound.getEnd();
		}
		final JPanel customTimePanel = new JPanel(new MigLayout("", "[]"));
		customTimePanel.add(new JLabel("Start:"));
		timeStart = new JSpinner(new SpinnerNumberModel(startTime, 0, Integer.MAX_VALUE, 1));
		customTimePanel.add(timeStart, "growx, wrap");

		customTimePanel.add(new JLabel("End:"));
		timeEnd = new JSpinner(new SpinnerNumberModel(endTime, 0, Integer.MAX_VALUE, 1));
		customTimePanel.add(timeEnd, "growx, wrap");
		return customTimePanel;
	}

	private Sequence getTimeBound(RenderModel editorRenderModel) {
		TimeEnvironmentImpl renderEnvironment = editorRenderModel.getTimeEnvironment();
		if (renderEnvironment != null) {
			return renderEnvironment.getCurrentSequence();
		}
		return null;
	}

	private void setSelectedGlobalSeq(GlobalSeq selectedValue) {
		selectedGlobalSeq = selectedValue;
		if (selectedValue != null) {
			timeStart.setValue(selectedValue.getStart());
			timeEnd.setValue(selectedValue.getEnd());
		}
	}
	private void setSelectedAnim(Animation selectedValue) {
		selectedAnim = selectedValue;
		if (selectedValue != null) {
			timeStart.setValue(selectedValue.getStart());
			timeEnd.setValue(selectedValue.getEnd());
		}
	}

	public void applyTo(final TimeEnvironmentImpl timeEnvironmentImpl) {
		if (tabs.getSelectedIndex() == 0) {
			if (selectedAnim != null) {
				timeEnvironmentImpl.setSequence(selectedAnim);
			}
		} else if (tabs.getSelectedIndex() == 1) {
			timeEnvironmentImpl.setBounds(((Integer) timeStart.getValue()), ((Integer) timeEnd.getValue()));
		} else if (tabs.getSelectedIndex() == 2) {
			if (selectedGlobalSeq != null) {
				timeEnvironmentImpl.setSequence(selectedGlobalSeq);
			}
		}
	}

	private void deleteSequences(Collection<? extends Sequence> sequences, String type) {
		final int result = JOptionPane.showConfirmDialog(TimeBoundChooserPanel.this,
				"Also delete keyframes?", "Delete " + type + "(s)",
				JOptionPane.YES_NO_CANCEL_OPTION);
		if (result != JOptionPane.CANCEL_OPTION) {
			List<UndoAction> deleteActions = new ArrayList<>();
			for (Sequence sequence : sequences) {
				deleteActions.add(new RemoveSequenceAction(model, sequence, null));
			}
			UndoAction undoAction = new CompoundAction("Delete " + deleteActions.size() + " " + type + "(s)", deleteActions, changeListener::animationParamsChanged);
			undoManager.pushAction(undoAction.redo());
		}
	}

	private void createGlobalSeq() {
		IntEditorJSpinner spinner = new IntEditorJSpinner(1000, 1, null);
		int option = JOptionPane.showConfirmDialog(TimeBoundChooserPanel.this, spinner,
				"Enter Length", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (option == JOptionPane.OK_OPTION) {
			GlobalSeq sequence = new GlobalSeq(spinner.getIntValue());
			if (model.contains(sequence)) {
				JOptionPane.showMessageDialog(TimeBoundChooserPanel.this,
						"A Global Sequence with that length already exists." +
								"\nThis program does not support multiple Global Sequences of the same length." +
								"\nInstead, simply add animation data to the sequence of that length which already exists.",
						"Error", JOptionPane.ERROR_MESSAGE);
			} else {
				undoManager.pushAction(new AddSequenceAction(model, sequence, changeListener).redo());
			}
		}
	}


	private void duplicateAnimation() {
		Map<Sequence, String> newNames = getNewNames();
		if (newNames != null) {
			List<UndoAction> undoActions = new ArrayList<>();
			for (Sequence animation : newNames.keySet()){
				undoActions.add(new DuplicateAnimationAction(model, animation, newNames.get(animation), null));
			}
			String actionName = newNames.size() == 1 ? "Added Animation " + newNames.values().stream().findFirst().get() : "Added "+ newNames.size() +" Animations";
			CompoundAction action = new CompoundAction(actionName, undoActions, changeListener::animationParamsChanged);
			undoManager.pushAction(action.redo());
		}
	}

	private Map<Sequence, String> getNewNames(){
		if(0 < selectedAnims.size()){
			JPanel panel = new JPanel(new MigLayout());
			Map<Sequence, String> nameMap = new LinkedHashMap<>();
			for(Animation sequence : selectedAnims){
				String newName = sequence.getName() + " Copy";
				nameMap.put(sequence, newName);
				panel.add(new JLabel(sequence.getName()));
				panel.add(new TwiTextField(newName, 24, s -> nameMap.put(sequence, s)), "wrap");
			}

			String title = nameMap.size() == 1 ? "Choose new animation name" : "Choose new animations names";
			int userChoice = JOptionPane.showConfirmDialog(TimeBoundChooserPanel.this, panel,
					title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (userChoice == JOptionPane.OK_OPTION) {
				return nameMap;
			}
		}
		return null;
	}
}
