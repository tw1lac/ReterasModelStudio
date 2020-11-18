package com.hiveworkshop.wc3.gui.modeledit.actions;

import java.util.ArrayList;
import java.util.List;

import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.mdl.TVertex;

/**
 * MotionAction -- something for you to undo when you screw up with motion
 *
 * Eric Theller 6/8/2012
 */
public class UVMoveAction implements UndoAction {
	private List<TVertex> selection;
	private List<TVertex> moveVectors;
	private TVertex moveVector;
	private VertexActionType actType = VertexActionType.UNKNOWN;

	public UVMoveAction(final List<TVertex> selection, final List<TVertex> moveVectors, final VertexActionType actionType) {
		this.selection = new ArrayList<>(selection);
		this.moveVectors = moveVectors;
		actType = actionType;
	}

	public UVMoveAction(final List<TVertex> selection, final TVertex moveVector, final VertexActionType actionType) {
		this.selection = new ArrayList<>(selection);
		this.moveVector = moveVector;
		actType = actionType;
	}

	public UVMoveAction() {

	}

	public void storeSelection(final List<TVertex> selection) {
		this.selection = new ArrayList<>(selection);
	}

	public void createEmptyMoveVectors() {
		moveVectors = new ArrayList<>();
		for (int i = 0; i < selection.size(); i++) {
			moveVectors.add(new TVertex(0, 0));
		}
	}

	public void createEmptyMoveVector() {
		moveVector = new TVertex(0, 0);
	}

	@Override
	public void redo() {
		if (moveVector == null) {
			for (int i = 0; i < selection.size(); i++) {
				final TVertex ver = selection.get(i);
				final TVertex vect = moveVectors.get(i);
				ver.x += vect.x;
				ver.y += vect.y;
			}
		} else {
            for (final TVertex ver : selection) {
                final TVertex vect = moveVector;
                ver.x += vect.x;
                ver.y += vect.y;
            }
		}
	}

	@Override
	public void undo() {
		if (moveVector == null) {
			for (int i = 0; i < selection.size(); i++) {
				final TVertex ver = selection.get(i);
				final TVertex vect = moveVectors.get(i);
				ver.x -= vect.x;
				ver.y -= vect.y;
			}
		} else {
            for (final TVertex ver : selection) {
                final TVertex vect = moveVector;
                ver.x -= vect.x;
                ver.y -= vect.y;
            }
		}
	}

	@Override
	public String actionName() {
		String outName = "";
		switch (actType) {
		case MOVE:
			outName = "move";
			break;
		case ROTATE:
			outName = "rotate";
			break;
		case SCALE:
			outName = "scale";
			break;
		}
		if (outName.equals("")) {
			outName = "actionType_" + actType;
		}
		return outName + " TVertices";
	}

	public void setActType(final VertexActionType actType) {
		this.actType = actType;
	}

	public List<TVertex> getSelection() {
		return selection;
	}

	public List<TVertex> getMoveVectors() {
		return moveVectors;
	}

	public TVertex getMoveVector() {
		return moveVector;
	}

	public VertexActionType getActType() {
		return actType;
	}
}
