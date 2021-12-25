package net.runelite.client.plugins.autokarambwan;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.Menu;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.InventoryItemQuery;
import net.runelite.api.queries.NPCQuery;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Random;

@Slf4j
@PluginDescriptor(
        name = "Auto Kwambam"
)
public class AutoKarambwanPlugin extends Plugin {
    int KARAM_REGION = 11568;
    int ZANARIS_REGION = 9541;
    int KARAMJA_RING = 29560;
    int ZANARIS_RING = 29495;
    int KARAMBWAN_ID = 8;
    int X = 2386;
    int Y = 4456;
    int FAIRY_TELEPORT = 569;

    @Inject
    private Client client; // Importing Client to get access to invokeMenuAction

    @Override
    // Startup function is called on plugin start, initializes absorptionInt
    public void startUp() {

    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (inKaramja() && !fullInventory() && !isFishing()) {
            startFishing();
        }
        if (inKaramja() && fullInventory() && !isWalking() && !isTeleporting()) {
            teleportZanaris();
        }
        if (inZanaris() && !fullInventory() && !isWalking() && !isTeleporting()) {
            teleportKaramja();
        }
        if (inZanaris() && fullInventory() && !isWalking()) {
            if (banker() == -1) {
                walkToTile(X,Y);
            }
            else {
                openBank();
                bankAll();
            }
        }
    }
    //    Method to check if inventory is full -done
    private boolean fullInventory() {

        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory.getItems().length == 28) {
            for (int i = 0; i < inventory.getItems().length; i++) {
                if (inventory.getItems()[i].getId() == -1) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    //  Check if in Karamya
    private boolean inKaramja() {

        for (int i = 0; i < client.getMapRegions().length; i++) {

            if (client.getMapRegions()[i] == KARAM_REGION) {
                return true;
            }
        }

        return false;
    }
    //  Check if in zanaris
    private boolean inZanaris() {

        for (int i = 0; i < client.getMapRegions().length; i++) {

            if (client.getMapRegions()[i] == ZANARIS_REGION) {
                return true;
            }
        }

        return false;
    }

    //    Method to check if fishing - done
    private boolean isFishing() {
        if (client.getLocalPlayer().getInteracting() != null) {
            return client.getLocalPlayer().getInteracting().getName().equals("Fishing spot");
        }
        return false;
    }
    //    Method to start fishing -done
    private void startFishing() {
        client.invokeMenuAction("", "", fishingSpot(), MenuAction.NPC_FIRST_OPTION.getId(),0,0);
    }
    //    Get ring location
    private int zanarisRingLocationX(){
        return new GameObjectQuery().idEquals(KARAMJA_RING).result(client).first().getSceneMinLocation().getX();
    }
    private int zanarisRingLocationY(){
        return new GameObjectQuery().idEquals(KARAMJA_RING).result(client).first().getSceneMinLocation().getY();
    }
    private int karamjaRingLocationX(){
        return new GameObjectQuery().idEquals(ZANARIS_RING).result(client).first().getSceneMinLocation().getX();
    }
    private int karamjaRingLocationY(){
        return new GameObjectQuery().idEquals(ZANARIS_RING).result(client).first().getSceneMinLocation().getY();
    }

    //    Method to teleport to zanaris -done
    private void teleportZanaris() {
        client.invokeMenuAction("", "", ZANARIS_RING, MenuAction.GAME_OBJECT_FIRST_OPTION.getId(), karamjaRingLocationX(), karamjaRingLocationY());
    }
    //    Method to teleport to Karamya -done
    private void teleportKaramja() {
        client.invokeMenuAction("", "", KARAMJA_RING, MenuAction.GAME_OBJECT_THIRD_OPTION.getId(), zanarisRingLocationX(), zanarisRingLocationY());
    }
    //    Find Fish spot
    private int fishingSpot(){
       return new NPCQuery().nameEquals("Fishing spot").result(client).nearestTo(client.getLocalPlayer()).getIndex();
    }
    //    Minimap Flag
    private boolean isWalking(){
       if(client.getLocalDestinationLocation() != null) {
           return true;
       }
       return false;
    }
    //    Find banker
    private int banker() {
        if (new NPCQuery().nameEquals("Banker").result(client).nearestTo(client.getLocalPlayer()) != null){
            return new NPCQuery().nameEquals("Banker").result(client).nearestTo(client.getLocalPlayer()).getIndex();
        }
        else return -1;
    }
    //    Open bank
    private void openBank(){
        ItemContainer bank = client.getItemContainer(InventoryID.BANK);
        if (bank == null) {
            client.invokeMenuAction("","", banker(), MenuAction.NPC_THIRD_OPTION.getId(), 0,0);
        }
    }
    //    Bank all kwams
    private void bankAll(){
        ItemContainer bank = client.getItemContainer(InventoryID.BANK);
        if (bank != null) {
            client.invokeMenuAction("","", KARAMBWAN_ID, MenuAction.CC_OP_LOW_PRIORITY.getId(), 2,983043);
        }
    }
    //    Load zone
    private void walkToTile(int x, int y){
        client.setSelectedSceneTileX(x - client.getBaseX());
        client.setSelectedSceneTileY(y - client.getBaseY());
        client.setViewportWalking(true);
        client.setCheckClick(false);
    }
    // Getlocalplayer.getgraphic
    private boolean isTeleporting(){
        return  client.getLocalPlayer().getGraphic() == FAIRY_TELEPORT;
    }
}
