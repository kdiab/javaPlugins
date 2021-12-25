package net.runelite.client.plugins.autoseaweedspore;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.Menu;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
        name = "Seaweed spore picker"
)
public class AutoSeaweedSporePlugin extends Plugin {

    @Inject
    private Client client;

    private Tile tile;
    private TileItem tileItem;

    private final int SPORE_ID = 21490;
    private final int SPORE_REGION_ID = 15008;

    @Subscribe
    public void onGameTick(GameTick event) {
    }


    @Override
    public void startUp() {

    }

    @Subscribe
    public void onItemSpawned(ItemSpawned itemSpawned) {
        int sceneX = itemSpawned.getTile().getSceneLocation().getX();
        int sceneY = itemSpawned.getTile().getSceneLocation().getY();
        int sporeSpawn = itemSpawned.getItem().getId();
        if (sporeSpawn == SPORE_ID) {
            clickSpore(sceneX, sceneY, sporeSpawn);
        }
    }

    public void clickSpore(int x, int y, int spore) {
        client.invokeMenuAction("", "", spore, MenuAction.GROUND_ITEM_THIRD_OPTION.getId(), x, y);
    }



}