package gregtech.common.gui;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.internal.network.NetworkUtils;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;

import gregtech.GT_Mod;
import gregtech.api.enums.InventoryType;
import gregtech.api.gui.GUIHost;
import gregtech.api.gui.GUIProvider;
import gregtech.api.gui.modularui.GUITextureSet;
import gregtech.api.logic.interfaces.ItemInventoryLogicHost;

public class InventoryGUIProvider<T extends GUIHost & ItemInventoryLogicHost> extends GUIProvider<T> {

    protected int cols = 9;

    protected int title_color = 0x404040;
    protected GUITextureSet guiTextureSet = GUITextureSet.DEFAULT;

    protected int tooltip_delay = 5;

    @Override
    protected void attachSynchHandlers(@NotNull ModularWindow.Builder builder, @NotNull UIBuildContext uiContext) {

    }

    public InventoryGUIProvider(@Nonnull T host) {
        super(host);
    }

    public InventoryGUIProvider(@Nonnull T host, int cols) {
        super(host);
        this.cols = cols;
    }

    public InventoryGUIProvider<T> setTitleColor(int title_color) {
        this.title_color = title_color;
        return this;
    }

    public InventoryGUIProvider<T> setGUITextureSet(GUITextureSet guiTextureSet) {
        if (guiTextureSet != null) {
            this.guiTextureSet = guiTextureSet;
        }
        return this;
    }

    public InventoryGUIProvider<T> setToolTipDelay(int tooltip_delay) {
        this.tooltip_delay = tooltip_delay;
        return this;
    }

    @Override
    protected void addWidgets(@NotNull ModularWindow.Builder builder, @NotNull UIBuildContext uiContext) {
        // builder.setBackground(GT_UITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setBackground(guiTextureSet.getMainBackground());
        MultiChildWidget mainTab = new MultiChildWidget();
        mainTab.setSize(host.getWidth(), host.getHeight());
        createMainTab(mainTab, builder, uiContext);
        addTitleToUI(builder);
        builder.widget(mainTab);

    }

    protected void createMainTab(@Nonnull MultiChildWidget tab, @Nonnull ModularWindow.Builder builder,
        @Nonnull UIBuildContext uiBuildContext) {
        var logic = host.getItemLogic(ForgeDirection.UNKNOWN, InventoryType.Both);
        if (logic == null) return;
        tab.addChild(
            logic.getGUIPart(9)
                .setPos(4, 4)
                .setSize(18 * cols + 4, (18 * Math.min(Math.max(1, host.getSizeInventory() / this.cols), 6)))

        );
    }

    protected void addTitleToUI(ModularWindow.Builder builder) {
        addTitleToUI(builder, host.getMachineName());
    }

    protected void addTitleToUI(ModularWindow.Builder builder, String title) {
        if (GT_Mod.gregtechproxy.mTitleTabStyle == 2) {
            addTitleItemIconStyle(builder, title);
        } else {
            addTitleTextStyle(builder, title);
        }
    }

    protected void addTitleTextStyle(ModularWindow.Builder builder, String title) {
        final int TAB_PADDING = 3;
        final int TITLE_PADDING = 2;
        int titleWidth = 0, titleHeight = 0;
        if (NetworkUtils.isClient()) {
            final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            final List<String> titleLines = fontRenderer
                .listFormattedStringToWidth(title, host.getWidth() - (TAB_PADDING + TITLE_PADDING) * 2);
            titleWidth = titleLines.size() > 1 ? host.getWidth() - (TAB_PADDING + TITLE_PADDING) * 2
                : fontRenderer.getStringWidth(title);
            // noinspection PointlessArithmeticExpression
            titleHeight = titleLines.size() * fontRenderer.FONT_HEIGHT + (titleLines.size() - 1) * 1;
        }

        final DrawableWidget tab = new DrawableWidget();
        final TextWidget text = new TextWidget(title).setDefaultColor(title_color)
            .setTextAlignment(Alignment.CenterLeft)
            .setMaxWidth(titleWidth);
        if (GT_Mod.gregtechproxy.mTitleTabStyle == 1) {
            tab.setDrawable(guiTextureSet.getTitleTabAngular())
                .setPos(0, -(titleHeight + TAB_PADDING) + 1)
                .setSize(host.getWidth(), titleHeight + TAB_PADDING * 2);
            text.setPos(TAB_PADDING + TITLE_PADDING, -titleHeight + TAB_PADDING);
        } else {
            tab.setDrawable(guiTextureSet.getTitleTabDark())
                .setPos(0, -(titleHeight + TAB_PADDING * 2) + 1)
                .setSize(titleWidth + (TAB_PADDING + TITLE_PADDING) * 2, titleHeight + TAB_PADDING * 2 - 1);
            text.setPos(TAB_PADDING + TITLE_PADDING, -titleHeight);
        }
        builder.widget(tab)
            .widget(text);
    }

    protected void addTitleItemIconStyle(ModularWindow.Builder builder, String title) {
        builder.widget(
            new MultiChildWidget().addChild(
                new DrawableWidget().setDrawable(guiTextureSet.getTitleTabNormal())
                    .setPos(0, 0)
                    .setSize(24, 24))
                .addChild(
                    new ItemDrawable(host.getAsItem()).asWidget()
                        .setPos(4, 4))
                .addTooltip(title)
                .setTooltipShowUpDelay(tooltip_delay)
                .setPos(0, -24 + 3));
    }

}
