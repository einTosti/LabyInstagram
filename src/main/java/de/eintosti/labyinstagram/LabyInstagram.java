package de.eintosti.labyinstagram;

import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.*;

import java.util.List;

/**
 * @author einTosti
 */
public class LabyInstagram extends LabyModAddon {

    @Override
    public void onEnable() {
        this.loadConfig();

        InstagramModule instagramModule = new InstagramModule(this);

        getApi().registerForgeListener(instagramModule);
        getApi().registerModule(instagramModule);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void loadConfig() {
    }

    @Override
    protected void fillSettings(List<SettingsElement> subSettings) {
    }
}
