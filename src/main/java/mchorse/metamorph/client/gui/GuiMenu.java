package mchorse.metamorph.client.gui;

import org.lwjgl.opengl.GL11;

import mchorse.metamorph.api.Model;
import mchorse.metamorph.api.morph.MorphManager;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.client.model.ModelCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Morphing survival GUI menu
 * 
 * This menu is responsible for rendering and storing the index of selected 
 * morph of currently player acquired morphs. 
 * 
 * This menu looks pretty similar to (alt / cmd) + tab menu like in most 
 * GUI based Operating Systems.
 */
public class GuiMenu extends Gui
{
    public Minecraft mc = Minecraft.getMinecraft();

    /**
     * Index of selected morph 
     */
    public int index = 0;

    /**
     * "Fade out" timer 
     */
    public int timer = 0;

    /**
     * Render the GUI 
     */
    public void render(int width, int height)
    {
        if (timer == 0)
        {
            return;
        }

        this.timer--;

        /* GUI size */
        int w = (int) (width * 0.8F);
        int h = (int) (height * 0.2F);

        w = w - w % 20;
        h = h - h % 2;

        /* F*$! those ints */
        float rx = (float) Math.ceil((double) this.mc.displayWidth / (double) width);
        float ry = (float) Math.ceil((double) this.mc.displayHeight / (double) height);

        /* Background */
        int x1 = width / 2 - w / 2;
        int y1 = height / 2 - h / 2;
        int x2 = width / 2 + w / 2;
        int y2 = height / 2 + h / 2;

        Gui.drawRect(x1, y1, x2, y2, 0x99000000);

        /* Clipping area around scroll area */
        int x = (int) (x1 * rx);
        int y = (int) (this.mc.displayHeight - y2 * ry);
        int ww = (int) (w * rx);
        int hh = (int) (h * ry);

        GL11.glScissor(x, y, ww, hh);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        this.renderMenu(width, height, w, h);

        GlStateManager.enableDepth();
    }

    /**
     * Render the menu
     * 
     * Pretty big method, huh? Big mix of math and rendering combined in this 
     * method.
     * 
     * @todo add player
     */
    public void renderMenu(int width, int height, int w, int h)
    {
        EntityPlayer player = this.mc.thePlayer;
        String label = "";

        int scale = (int) (height * 0.17F / 2);
        int margin = width / 10;

        scale -= scale % 2;
        margin -= margin % 2;

        int offset = this.index * margin;
        int maxScroll = this.getMorphCount() * margin - w / 2 - margin / 2 + 2;

        int i = 0;

        offset = (int) MathHelper.clamp_float(offset, 0, maxScroll);

        for (String name : this.getMorph().getAcquiredMorphs())
        {
            int x = width / 2 - w / 2 + i * margin + margin / 2 + 1;
            int y = height / 2 + h / 2;

            /* Scroll the position */
            if (offset > w / 2 - margin / 2)
            {
                x -= offset - (w / 2 - margin / 2);
            }

            /* Render border around the selected morph */
            if (this.index == i)
            {
                this.renderSelected(x - margin / 2, height / 2 - h / 2 + 1, margin, h - 2);

                label = name;
            }

            /* Render morph itself */
            this.renderMorph(player, name, x, y - 2, scale);

            i++;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        /* Draw the title */
        this.drawCenteredString(this.mc.fontRendererObj, label, width / 2, height / 2 + h / 2 + 4, 0xffffffff);
    }

    /**
     * Render a grey outline around the given area.
     * 
     * Basically, this method renders selection.
     */
    public void renderSelected(int x, int y, int width, int height)
    {
        int color = 0xffcccccc;

        this.drawHorizontalLine(x, x + width - 1, y, color);
        this.drawHorizontalLine(x, x + width - 1, y + height - 1, color);

        this.drawVerticalLine(x, y, y + height - 1, color);
        this.drawVerticalLine(x + width - 1, y, y + height - 1, color);
    }

    /**
     * Render morph 
     * 
     * This method is accumulation of some rendering code in vanilla minecraft 
     * which can (theoretically) render any type of ModelBase on the screen 
     * without require the render.  
     * 
     * This method takes code from 
     * {@link RenderLivingBase#doRender(net.minecraft.entity.EntityLivingBase, double, double, double, float, float)} 
     * and {@link GuiInventory#drawEntityOnScreen(int, int, int, float, float, net.minecraft.entity.EntityLivingBase)}.
     */
    public void renderMorph(EntityPlayer player, String name, int x, int y, float scale)
    {
        float factor = 0.0625F;

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        Model data = MorphManager.INSTANCE.morphs.get(name).model;
        ModelCustom model = ModelCustom.MODELS.get(name);

        model.pose = model.model.poses.get("standing");
        model.swingProgress = 0;

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        GlStateManager.enableAlpha();

        model.setLivingAnimations(player, 0, 0, 0);
        model.setRotationAngles(0, 0, 0, 0, 0, factor, player);

        Minecraft.getMinecraft().renderEngine.bindTexture(data.defaultTexture);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        model.render(player, 0, 0, 0, 0, 0, factor);

        GlStateManager.disableDepth();

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Proceed to the next morph 
     */
    public void next()
    {
        int length = this.getMorphCount();

        if (length == 0)
        {
            return;
        }

        if (this.index < length - 1)
        {
            this.index++;
            this.timer = this.getDelay();
        }
    }

    /**
     * Proceed to the previous morph 
     */
    public void prev()
    {
        int length = this.getMorphCount();

        if (length == 0)
        {
            return;
        }

        if (this.index > -1)
        {
            this.index--;
            this.timer = this.getDelay();
        }
    }

    /**
     * Get delay for the timer
     * 
     * Delay is about 2 seconds, however you may never know which is it since 
     * the game may lag. 
     */
    private int getDelay()
    {
        int frameRate = this.mc.gameSettings.limitFramerate;

        if (frameRate > 120)
        {
            frameRate = 120;
        }

        return frameRate * 2;
    }

    /**
     * Get morphing 
     */
    private IMorphing getMorph()
    {
        return Minecraft.getMinecraft().thePlayer.getCapability(MorphingProvider.MORPHING_CAP, null);
    }

    /**
     * Get how much player has acquired morphs 
     */
    private int getMorphCount()
    {
        return this.getMorph().getAcquiredMorphs().size();
    }
}