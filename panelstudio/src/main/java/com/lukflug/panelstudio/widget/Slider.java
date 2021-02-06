package com.lukflug.panelstudio.widget;

import java.awt.Rectangle;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.theme.ISliderRenderer;

/**
 * Base class for components that are sliders.
 * @author lukflug
 */
public abstract class Slider extends FocusableComponent {
	/**
	 * Whether slider was clicked and is sliding.
	 */
	protected boolean attached=false;
	/**
	 * The renderer to be used.
	 */
	protected ISliderRenderer renderer;
	
	/**
	 * Constructor.
	 * @param title caption of the slider
	 * @param description the description for this component
	 * @param visible whether this component is visible
	 * @param renderer renderer for the slider
	 */
	public Slider(String title, String description, IBoolean visible, ISliderRenderer renderer) {
		super(title,description,visible);
		this.renderer=renderer;
	}

	@Override
	public void render (Context context) {
		super.render(context);
		if (attached) {
			Rectangle rect=renderer.getSlideArea(context);
			double value=(context.getInterface().getMouse().x-rect.x)/(double)(rect.width-1);
			if (value<0) value=0;
			else if (value>1) value=1;
			setValue(value);
		}
		if (!context.getInterface().getButton(IInterface.LBUTTON)) {
			attached=false;
		}
		renderer.renderSlider(context,title,getDisplayState(),hasFocus(context),getValue());
	}
	
	@Override
	public void handleButton (Context context, int button) {
		super.handleButton(context,button);
		if (button==IInterface.LBUTTON && context.isClicked() && renderer.getSlideArea(context).contains(context.getInterface().getMouse())) {
			attached=true;
		}
	}
	
	@Override
	public void exit() {
		super.exit();
		attached=false;
	}

	@Override
	protected int getHeight() {
		return renderer.getDefaultHeight();
	}

	/**
	 * Abstract method to get the current slider value.
	 * @return the slider value between 0 (empty) and 1 (full)
	 */
	protected abstract double getValue();
	
	/**
	 * Abstract method to update the slider value.
	 * @param value the slider value between 0 (empty) and 1 (full)
	 */
	protected abstract void setValue (double value);
	
	/**
	 * Abstract method to get the displayed slider value.
	 * @return string to be displayed on slider
	 */
	protected abstract String getDisplayState();
}
