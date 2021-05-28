package com.hiveworkshop.rms.ui.application.edit.mesh;

import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.animation.WrongModeException;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.editor.SimpleRotateAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.editor.StaticMeshMoveAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.editor.StaticMeshRotateAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.editor.StaticMeshScaleAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.CompoundAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.GenericMoveAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.GenericRotateAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.GenericScaleAction;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionItemTypes;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionManager;
import com.hiveworkshop.rms.util.Vec3;

import java.util.Arrays;

public class AbstractModelEditor extends ModelEditor {
    protected final ModelStructureChangeListener structureChangeListener;
    protected ModelHandler modelHandler;
    protected SelectionItemTypes selectionMode;

	public AbstractModelEditor(SelectionManager selectionManager,
                               ModelStructureChangeListener structureChangeListener,
                               ModelHandler modelHandler, SelectionItemTypes selectionMode) {
		super(selectionManager, modelHandler.getModelView());
		this.modelHandler = modelHandler;
		this.structureChangeListener = structureChangeListener;
		this.selectionMode = selectionMode;
	}

    @Override
    public UndoAction translate(Vec3 v) {
        Vec3 delta = new Vec3(v);
	    return new StaticMeshMoveAction(modelView, delta).redo();
    }

    @Override
    public UndoAction scale(Vec3 center, Vec3 scale) {
	    return new StaticMeshScaleAction(modelView, center).updateScale(scale).redo();
    }

    @Override
    public UndoAction setPosition(Vec3 center, Vec3 v) {
        Vec3 delta = Vec3.getDiff(v, center);
	    return new StaticMeshMoveAction(modelView, delta).redo();
    }

    @Override
    public UndoAction rotate(Vec3 center, Vec3 rotate) {
	    return new CompoundAction("rotate", Arrays.asList(
			    new SimpleRotateAction(modelView, center, rotate.x, (byte) 2, (byte) 1),
			    new SimpleRotateAction(modelView, center, rotate.y, (byte) 0, (byte) 2),
			    new SimpleRotateAction(modelView, center, rotate.z, (byte) 1, (byte) 0)))
			    .redo();
    }

    @Override
    public boolean editorWantsAnimation() {
        return false;
    }

    @Override
    public GenericMoveAction beginTranslation() {
        return new StaticMeshMoveAction(modelView, Vec3.ORIGIN);
    }

    @Override
    public GenericRotateAction beginRotation(Vec3 center, byte dim1, byte dim2) {
        return new StaticMeshRotateAction(modelView, new Vec3(center), dim1, dim2);
    }

    @Override
    public GenericRotateAction beginSquatTool(Vec3 center, byte firstXYZ, byte secondXYZ) {
        throw new WrongModeException("Unable to use squat tool outside animation editor mode");
    }

    @Override
    public GenericScaleAction beginScaling(Vec3 center) {
        return new StaticMeshScaleAction(modelView, center);
    }
}
