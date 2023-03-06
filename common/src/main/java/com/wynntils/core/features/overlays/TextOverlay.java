/*
 * Copyright © Wynntils 2023.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.core.features.overlays;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;
import com.wynntils.core.config.Config;
import com.wynntils.core.config.ConfigHolder;
import com.wynntils.core.features.overlays.sizes.OverlaySize;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.render.FontRenderer;
import com.wynntils.utils.render.buffered.BufferedFontRenderer;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.TextShadow;
import com.wynntils.utils.render.type.VerticalAlignment;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * An overlay, which main purpose is to display function templates.
 */
public abstract class TextOverlay extends Overlay {
    @Config(key = "overlay.wynntils.textOverlay.textShadow")
    public TextShadow textShadow = TextShadow.OUTLINE;

    @Config(key = "overlay.wynntils.textOverlay.secondsPerRecalculation")
    public float secondsPerRecalculation = 0.5f;

    protected String[] cachedLines;
    protected long lastUpdate = 0;

    protected TextOverlay(OverlayPosition position, float width, float height) {
        super(position, width, height);
    }

    protected TextOverlay(OverlayPosition position, OverlaySize size) {
        super(position, size);
    }

    protected TextOverlay(
            OverlayPosition position,
            OverlaySize size,
            HorizontalAlignment horizontalAlignmentOverride,
            VerticalAlignment verticalAlignmentOverride) {
        super(position, size, horizontalAlignmentOverride, verticalAlignmentOverride);
    }

    @Override
    public void render(
            PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float partialTicks, Window window) {
        if (!Models.WorldState.onWorld()) return;

        renderTemplate(poseStack, bufferSource, getTemplate());
    }

    @Override
    public void renderPreview(
            PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float partialTicks, Window window) {
        if (!Models.WorldState.onWorld()) return;

        renderTemplate(poseStack, bufferSource, getPreviewTemplate());
    }

    protected void renderTemplate(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, String template) {
        if (System.currentTimeMillis() - lastUpdate > secondsPerRecalculation) {
            lastUpdate = System.currentTimeMillis();
            cachedLines = Managers.Function.doFormatLines(template);
        }

        float renderX = this.getRenderX();
        float renderY = this.getRenderY();
        for (String line : cachedLines) {
            BufferedFontRenderer.getInstance()
                    .renderAlignedTextInBox(
                            poseStack,
                            bufferSource,
                            line,
                            renderX,
                            renderX + this.getWidth(),
                            renderY,
                            renderY + this.getHeight(),
                            0,
                            CommonColors.WHITE,
                            this.getRenderHorizontalAlignment(),
                            this.getRenderVerticalAlignment(),
                            this.textShadow);

            renderY += FontRenderer.getInstance().getFont().lineHeight;
        }
    }

    public abstract String getTemplate();

    public abstract String getPreviewTemplate();

    @Override
    protected void onConfigUpdate(ConfigHolder configHolder) {}
}