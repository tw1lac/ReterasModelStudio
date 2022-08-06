package com.hiveworkshop.rms.ui.application.edit.animation;

import com.hiveworkshop.rms.editor.actions.animation.SlideKeyframesAction;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.UndoManager;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TimeLineMouseListener extends MouseAdapter {


	private final TimeSlider timeSlider;

	private TimeEnvironmentImpl timeEnvironment;

	private final TimeLinePopup popupMenu;
	private KeyFrame draggingFrame = null;
	private int draggingFrameStartTime = 0;
	private boolean draggingSlider = false;
	private Robot robot;

	private final Cursor slideCursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
	//	private final Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private UndoManager undoManager;

	private boolean drawing;
	private final KeyframeHandler keyframeHandler;

	private final TimeSliderPanel timeSliderPanel;

	Point lastMousePoint;
	double mouseDownPointX = 0;
	int mouseDragXOffset = 0;

	public TimeLineMouseListener(TimeSliderPanel timeSliderPanel, KeyframeHandler keyframeHandler, TimeSlider timeSlider){
		this.timeSliderPanel = timeSliderPanel;
		this.keyframeHandler = keyframeHandler;
		this.timeSlider = timeSlider;

		popupMenu = new TimeLinePopup(keyframeHandler);
		try {
			robot = new Robot();
		} catch (final AWTException e1) {
			e1.printStackTrace();
		}
	}

	public void setModelHandler(ModelHandler modelHandler) {
		if(modelHandler != null) {
			this.undoManager = modelHandler.getUndoManager();
			timeEnvironment = modelHandler.getRenderModel().getTimeEnvironment();
			popupMenu.setTimeEnvironment(timeEnvironment);
		} else {
			this.undoManager = null;
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		super.mouseReleased(e);
		if (drawing) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point mousePoint = e.getPoint();
				if (draggingSlider) {
					timeSlider.setFromTimeFraction(timeEnvironment.getTimeRatio());
					draggingSlider = false;
				} else if (draggingFrame != null && mouseDownPointX != e.getPoint().getX()) {
					int dx = mousePoint.x - mouseDragXOffset;
					applyDraggedFrame(dx);
				}
				checkMouseOver(mousePoint);
			} else if (SwingUtilities.isRightMouseButton(e)) {
				showPopupIfFrameExists(e);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		mouseDownPointX = e.getPoint().getX();
		if (drawing) {
			lastMousePoint = e.getPoint();
			draggingSlider = timeSlider.onSlide(lastMousePoint);
			if (!draggingSlider) {
				mouseDragXOffset = mousePressed(e, lastMousePoint);
			}
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		super.mouseMoved(e);
		if (drawing) {
			checkMouseOver(e.getPoint());
		}
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		super.mouseDragged(e);
		if (drawing) {
			Point mousePoint = e.getPoint();
			if (draggingSlider) {
				dragTimeSlider((int) (mousePoint.getX() - lastMousePoint.getX()));
			} else if (draggingFrame != null) {
				int newX = mousePoint.x - mouseDragXOffset;
				updateDraggedKeyframe(newX, draggingFrame);
				timeSliderPanel.repaint();
			}
			lastMousePoint = e.getPoint();
		}
	}

	private void applyDraggedFrame(int newX) {
		updateDraggedKeyframe(newX, draggingFrame);
		if (undoManager != null && draggingFrameStartTime != draggingFrame.getTime()) {
			SlideKeyframesAction slideKeyframesAction = draggingFrame.finnishDrag();
			undoManager.pushAction(slideKeyframesAction.redo());
		}
		draggingFrame = null;
		timeSliderPanel.repaint();
	}

	private Integer initDragging(Point lastMousePoint) {
		draggingFrame = keyframeHandler.initDragging(lastMousePoint);
		if(draggingFrame != null) {
			draggingFrameStartTime = draggingFrame.getTime();
			return (int) (lastMousePoint.getX() - draggingFrame.getXPoint());
		}
		return null;
	}

	public void updateDraggedKeyframe(int newX, KeyFrame draggingFrame) {
		draggingFrame.getRenderRect().x = newX;

		int computedTime = (int) ((newX / (double) timeSlider.getMaxX()) * timeEnvironment.getLength());

		if (computedTime < 0) {
			computedTime = 0;
		} else if (computedTime > timeEnvironment.getLength()) {
			computedTime = timeEnvironment.getLength();
		}

		keyframeHandler.dragFrame(draggingFrame, computedTime);
	}


	private int mousePressed(MouseEvent mouseEvent, Point lastMousePoint) {
		if (timeSlider.onBackward(lastMousePoint)) {
			timeStep(-1, true);
		} else if (timeSlider.onForward(lastMousePoint)) {
			timeStep(1, true);
		} else {
			if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
				Integer lastMousePoint1 = initDragging(lastMousePoint);
				if (lastMousePoint1 != null) return lastMousePoint1;
			}
		}
		return 0;
	}

	private void timeStep(int step, boolean moveMouse) {
		timeEnvironment.stepAnimationTime(step);
		if (robot != null && moveMouse) {
			int xStart = timeSlider.getX();
			timeSlider.setFromTimeFraction(timeEnvironment.getTimeRatio());

			int pixelDelta = timeSlider.getX() - xStart;
			robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x + pixelDelta, MouseInfo.getPointerInfo().getLocation().y);
		} else {
			timeSlider.setFromTimeFraction(timeEnvironment.getTimeRatio());
		}
		timeSliderPanel.repaint();
	}


	private void dragTimeSlider(int dx) {
		timeSlider.moveSlider(dx);
		int computedTime = (int) (timeSlider.getLocationFraction() * timeEnvironment.getLength());
		if (computedTime != timeEnvironment.getEnvTrackTime()) {
			timeEnvironment.setAnimationTime(computedTime);
		}
		timeSliderPanel.repaint();
	}

	public void checkMouseOver(Point mousePt) {
		if (keyframeHandler.getTimeFromPoint(mousePt) != null) {
			timeSliderPanel.setCursor(slideCursor);
		} else {
			timeSliderPanel.setCursor(null);
		}
		timeSliderPanel.repaint();
	}

	private void showPopupIfFrameExists(MouseEvent mouseEvent) {
		boolean foundFrame = false;
		for (Integer time : keyframeHandler.getTimes()) {
			if (keyframeHandler.getKeyFrame(time).containsPoint(mouseEvent.getPoint())) {
				popupMenu.fillAndShow(time, timeSliderPanel, mouseEvent.getX(), mouseEvent.getY(), true);
				return;
			}
		}
		if (timeSlider.containsPoint(mouseEvent.getPoint())) {
			popupMenu.fillAndShow(timeEnvironment.getEnvTrackTime(), timeSliderPanel, mouseEvent.getX(), mouseEvent.getY(), false);
		}
	}

	public boolean isDraggingKeyframe() {
		return draggingFrame != null;
	}

	public KeyFrame getDraggingFrame() {
		return draggingFrame;
	}

	public TimeLineMouseListener setDrawing(boolean drawing) {
		this.drawing = drawing;
		return this;
	}
}
