package de.eintosti.labyinstagram;

import de.eintosti.labyinstagram.util.FollowerUtil;
import de.eintosti.labyinstagram.util.FollowerUtilException;
import net.labymod.ingamegui.ModuleCategory;
import net.labymod.ingamegui.ModuleCategoryRegistry;
import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.ControlElement.IconData;
import net.labymod.settings.elements.NumberElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.StringElement;
import net.labymod.utils.Material;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * @author einTosti
 */
public class InstagramModule extends SimpleModule {
    private LabyInstagram addon;
    private FollowerUtil followerUtil;

    private String errorMessage;
    private String username;
    private int followerCount;
    private int updateInterval;
    private long lastRequest;

    public InstagramModule(LabyInstagram addon) {
        this.addon = addon;
        this.followerUtil = new FollowerUtil();
    }

    @Override
    public String getDisplayName() {
        return "Follower";
    }

    @Override
    public String getDisplayValue() {
        return (this.errorMessage == null) ? NumberFormat.getInstance(Locale.US).format(this.followerCount) : this.errorMessage;
    }

    @Override
    public String getDefaultValue() {
        return "0";
    }

    @Override
    public IconData getIconData() {
        return new IconData("labyinstagram/textures/icon.png");
    }

    @Override
    public void loadSettings() {
        this.username = this.getAttribute("username", "");
        this.updateInterval = Integer.parseInt(this.getAttribute("updateInterval", "10"));

        if (this.username != null)
            this.lastRequest = System.currentTimeMillis() - 1L;
    }

    @Override
    public void fillSubSettings(List<SettingsElement> subSettings) {
        super.fillSubSettings(subSettings);

        StringElement stringElement = new StringElement(this, new ControlElement.IconData(Material.PAPER), "Username", "username").maxLength(30).addCallback((String newValue) -> {
            addon.getConfig().addProperty("username", newValue);
            addon.saveConfig();
            this.username = newValue;
            this.lastRequest = System.currentTimeMillis() - 1L;
        });
        stringElement.setDescriptionText("Instagram username");
        subSettings.add(stringElement);

        NumberElement numberElement = new NumberElement(this, new ControlElement.IconData(Material.WATCH), "Update interval", "updateInterval").setMinValue(5).addCallback((Integer newValue) -> {
            addon.getConfig().addProperty("updateInterval", newValue);
            addon.saveConfig();
            this.updateInterval = newValue;
            this.lastRequest = System.currentTimeMillis() - 1L;
        });
        numberElement.setDescriptionText("Update interval in seconds");
        subSettings.add(numberElement);
    }

    @Override
    public String getSettingName() {
        return "Instagram Follower counter";
    }

    @Override
    public String getDescription() {
        return "Shows any user's Instagram follower count";
    }

    @Override
    public int getSortingId() {
        return 0;
    }

    @Override
    public ModuleCategory getCategory() {
        return ModuleCategoryRegistry.CATEGORY_EXTERNAL_SERVICES;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (LabyMod.getInstance().isInGame() && this.getEnabled().size() != 0 && this.isShown() && isDrawn()) {
            if (System.currentTimeMillis() > this.lastRequest) {
                if (this.username == null || this.username.isEmpty()) {
                    this.errorMessage = "No username given";
                    return;
                }
                this.lastRequest = System.currentTimeMillis() + this.updateInterval * 1000L;

                Executors.newCachedThreadPool().execute(() -> {
                    try {
                        if (this.errorMessage != null) this.errorMessage = null;
                        this.followerCount = followerUtil.getFollowers(this.username);
                    } catch (FollowerUtilException e) {
                        this.errorMessage = e.getMessage();
                    }
                });
            }
        }
    }
}
