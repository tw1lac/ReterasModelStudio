package com.hiveworkshop.rms.util;

import javax.swing.*;
import java.awt.event.ActionListener;

public class TwiButton extends JButton {

	public TwiButton() {
		this(null, null, null);
	}

	public TwiButton(String text) {
		this(text, null, null);
	}
	public TwiButton(Icon icon) {
		this(null, icon, null);
	}
	public TwiButton(String text, Icon icon) {
		this(text, icon, null);
	}

	public TwiButton(Action action) {
		this(null, null, action);
	}

	public TwiButton(String text, Action action) {
		this(text, null, action);
	}
	public TwiButton(String text, Icon icon, Action action) {
		super(text, icon);
		setAction(action);
	}

	public TwiButton setButtonText(String text){
		super.setText(text);
		return this;
	}
	public TwiButton setButtonAction(Action action){
		super.setAction(action);
		return this;
	}
	public TwiButton setButtonIcon(Icon icon){
		super.setIcon(icon);
		return this;
	}
	public TwiButton addButtonListener(ActionListener actionListener){
		super.addActionListener(actionListener);
		return this;
	}
	public TwiButton removeButtonListener(ActionListener actionListener){
		super.removeActionListener(actionListener);
		return this;
	}
}
