package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.modeledit.activity.UndoActionListener;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.tools.EditAnimationLengthsAction;
import com.hiveworkshop.wc3.mdl.Animation;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mdl.v2.ModelView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationPanel extends JPanel implements ActionListener {
	final List<SliderBarHandler> bars = new ArrayList<>();
	final JButton okay;
    final JButton cancel;
	final ModelView mdlDisp;
	final AnimationFrame parentFrame;
	private final UndoActionListener undoActionListener;
	private final Runnable onFinish;

	public AnimationPanel(final ModelView mdlDisp, final AnimationFrame frame,
			final UndoActionListener undoActionListener, final Runnable onFinish) {
		this.mdlDisp = mdlDisp;
		parentFrame = frame;
		this.undoActionListener = undoActionListener;
		this.onFinish = onFinish;
		final GridLayout layout = new GridLayout(
				(mdlDisp.getModel().getAnimsSize() + mdlDisp.getModel().getGlobalSeqs().size()) * 2 + 2, 2);
		setLayout(layout);

		for (final Animation anim : mdlDisp.getModel().getAnims()) {
			String labelString = anim.getName() + " (" + anim.length() / 1000.00 + " s)";
			addLabeledNumberAdjuster(anim.length(), labelString);
		}
		int i = 0;
		for (final Integer globalSeq : mdlDisp.getModel().getGlobalSeqs()) {
			String labelString = "Global Sequence " + ++i + " (" + globalSeq / 1000.00 + " s)";
			addLabeledNumberAdjuster(globalSeq, labelString);
		}

		okay = new JButton("OK");
		okay.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		add(cancel);
		add(okay);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == cancel) {
			parentFrame.setVisible(false);
		}
		if (e.getSource() == okay) {
			final EditableModel mdl = mdlDisp.getModel();
			int myAnimationsIndex = 0;
			final Map<Animation, Integer> animationToNewLength = new HashMap<>();
			final Map<Animation, Integer> animationToOldLength = new HashMap<>();
			final int[] oldGlobalSeqLengths = new int[mdl.getGlobalSeqs().size()];
			final int[] newGlobalSeqLengths = new int[mdl.getGlobalSeqs().size()];
			for (final Animation myAnimation : mdl.getAnims()) {
				animationToNewLength.put(myAnimation, bars.get(myAnimationsIndex).bar.getValue());
				animationToOldLength.put(myAnimation, myAnimation.length());
				myAnimationsIndex++;
			}
			for (final Integer myAnimation : mdl.getGlobalSeqs()) {
				final int newLength = bars.get(myAnimationsIndex).bar.getValue();
				newGlobalSeqLengths[myAnimationsIndex - mdl.getAnimsSize()] = newLength;
				oldGlobalSeqLengths[myAnimationsIndex - mdl.getAnimsSize()] = myAnimation;
				myAnimationsIndex++;
			}
			final EditAnimationLengthsAction editAnimationLengths = new EditAnimationLengthsAction(mdl,
					animationToNewLength, animationToOldLength, newGlobalSeqLengths, oldGlobalSeqLengths);
			editAnimationLengths.redo();
			undoActionListener.pushAction(editAnimationLengths);
			parentFrame.setVisible(false);
			onFinish.run();
		}
	}

	private void addLabeledNumberAdjuster(int length, String labelString) {
		final JLabel label = new JLabel(labelString);

		final int maxLength = Math.max(100000, length * 4);
		final JSlider bar = new JSlider(0, maxLength);
		bar.setValue(length);
		final JSpinner spinner = new JSpinner(
				new SpinnerNumberModel(length / 1000.00, 0.0, maxLength / 1000.00, 0.001));

		final SliderBarHandler handler = new SliderBarHandler(bar, spinner);
		bar.addChangeListener(handler);
		spinner.addChangeListener(handler);
		bars.add(handler);

		add(label);
		add(new JSeparator());
		add(bar);
		add(spinner);
	}
}
