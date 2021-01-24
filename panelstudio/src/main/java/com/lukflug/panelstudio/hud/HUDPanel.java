package com.lukflug.panelstudio.hud;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.theme.IRenderer;
import com.lukflug.panelstudio.theme.RendererProxy;
import com.lukflug.panelstudio.widget.DraggableContainer;

/**
 * Panel holding an HUD component.
 * @author lukflug
 */
public class HUDPanel extends DraggableContainer {
	/**
	 * Whether GUI is open.
	 */
	protected IToggleable guiOpen;
	/**
	 * The HUD component.
	 */
	protected IFixedComponent component;
	
	/**
	 * Constructor.
	 * @param component the component
	 * @param renderer the renderer for this container
	 * @param open toggleable indicating whether the container is open or closed
	 * @param animation the animation for opening and closing this container
	 * @param guiOpen whether to accept input and render container itself or not
	 * @param minBorder the minimum border for the container
	 */
	public HUDPanel(IFixedComponent component, IRenderer renderer, IToggleable open, Animation animation, IToggleable guiOpen, int minBorder) {
		super(component.getTitle(),null,new HUDRenderer(renderer,guiOpen,minBorder),open,animation,null,new Point(0,0),0);
		addComponent(component);
		this.guiOpen=guiOpen;
		this.component=component;
		bodyDrag=true;
	}
	
	/**
	 * Mask out input, if GUI is turned off.
	 */
	@Override
	public void handleButton (Context context, int button) {
		if (guiOpen.isOn()) super.handleButton(context,button);
	}

	/**
	 * Mask out input, if GUI is turned off.
	 */
	@Override
	public void handleScroll (Context context, int diff) {
		if (guiOpen.isOn()) super.handleScroll(context,diff);
	}
	
	/**
	 * Gets position from child component.
	 */
	@Override
	public Point getPosition (IInterface inter) {
		position=component.getPosition(inter);
		position.translate(0,-renderer.getHeight(open.getValue()!=0)-renderer.getOffset());
		return super.getPosition(inter);
	}

	/**
	 * Sets position of child component.
	 */
	@Override
	public void setPosition (IInterface inter, Point position) {
		component.setPosition(inter,new Point(position.x,position.y+renderer.getHeight(open.getValue()!=0)+renderer.getOffset()));
	}
	
	/**
	 * Get the child component width.
	 */
	@Override
	public int getWidth (IInterface inter) {
		return component.getWidth(inter)+renderer.getBorder()*2+renderer.getLeftBorder(scroll)+renderer.getRightBorder(scroll);
	}
	
	/**
	 * Disable clipping, if container fully open.
	 */
	@Override
	protected Rectangle getClipRect (Context context, int height) {
		if (open.getValue()!=1) return super.getClipRect(context,height);
		else return null;
	}

	@Override
	public void saveConfig(IInterface inter, IPanelConfig config) {
		component.saveConfig(inter,config);
		config.saveState(open.isOn());
	}

	@Override
	public void loadConfig(IInterface inter, IPanelConfig config) {
		component.loadConfig(inter,config);
		if (open.isOn()!=config.loadState()) open.toggle();
	}
	
	
	/**
	 * Proxy for a {@link IRenderer}, doesn't display container, when GUI is off.
	 * @author lukflug
	 */
	protected static class HUDRenderer extends RendererProxy {
		/**
		 * Base renderer.
		 */
		protected IRenderer renderer;
		/**
		 * Whether GUI is open.
		 */
		protected IToggleable guiOpen;
		/**
		 * Minimum border.
		 */
		protected int minBorder;
		
		/**
		 * Constructor.
		 * @param renderer the base renderer
		 * @param guiOpen whether to accept input and render container itself or not
	 * @param minBorder the minimum border for the container
		 */
		public HUDRenderer (IRenderer renderer, IToggleable guiOpen, int minBorder) {
			this.renderer=renderer;
			this.guiOpen=guiOpen;
			this.minBorder=minBorder;
		}

		/**
		 * Returns the offset defined by the base renderer, if it is larger than {@link #minBorder}.
		 * Otherwise it will return {@link #minBorder}.
		 */
		@Override
		public int getOffset() {
			return Math.max(renderer.getOffset(),minBorder);
		}

		/**
		 * Returns the border defined by the base renderer, if it is larger than {@link #minBorder}.
		 * Otherwise it will return {@link #minBorder}.
		 */
		@Override
		public int getBorder() {
			return Math.max(renderer.getBorder(),minBorder);
		}

		@Override
		public void renderTitle(Context context, String text, boolean focus) {
			if (guiOpen.isOn()) renderer.renderTitle(context,text,focus);
		}

		@Override
		public void renderTitle(Context context, String text, boolean focus, boolean active) {
			if (guiOpen.isOn()) renderer.renderTitle(context,text,focus,active);
		}

		@Override
		public void renderTitle(Context context, String text, boolean focus, boolean active, boolean open) {
			if (guiOpen.isOn()) renderer.renderTitle(context,text,focus,open);
		}

		@Override
		public void renderRect(Context context, String text, boolean focus, boolean active, Rectangle rectangle, boolean overlay) {
			if (guiOpen.isOn()) renderer.renderRect(context,text,focus,active,rectangle,overlay);
		}

		@Override
		public void renderBackground(Context context, boolean focus) {
			if (guiOpen.isOn()) renderer.renderBackground(context,focus);
		}

		@Override
		public void renderBorder(Context context, boolean focus, boolean active, boolean open) {
			if (guiOpen.isOn()) renderer.renderBorder(context,focus,active,open);
		}
		
		@Override
		public int renderScrollBar (Context context, boolean focus, boolean active, boolean scroll, int childHeight, int scrollPosition) {
			if (guiOpen.isOn()) return renderer.renderScrollBar(context,focus,active,scroll,childHeight,scrollPosition);
			else return scrollPosition;
		}

		/**
		 * Returns invisible color, if GUI is off.
		 */
		@Override
		public Color getMainColor(boolean focus, boolean active) {
			if (guiOpen.isOn()) return renderer.getMainColor(focus,active);
			else return new Color(0,0,0,0);
		}

		/**
		 * Returns invisible color, if GUI is off.
		 */
		@Override
		public Color getBackgroundColor(boolean focus) {
			if (guiOpen.isOn()) return renderer.getBackgroundColor(focus);
			else return new Color(0,0,0,0);
		}

		/**
		 * Returns invisible color, if GUI is off.
		 */
		@Override
		public Color getFontColor(boolean focus) {
			if (guiOpen.isOn()) return renderer.getFontColor(focus);
			else return new Color(0,0,0,0);
		}

		@Override
		protected IRenderer getRenderer() {
			return renderer;
		}
	}
}
