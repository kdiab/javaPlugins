package net.runelite.client.plugins.autonmz;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.Menu;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Random;

@Slf4j
@PluginDescriptor(
        name = "Auto NMZ"
)
public class AutoNmzPlugin extends Plugin {

    // every 5 minutes
    // get location of nmz
    // if not in nmz : logout
    // on/off button
    // top up absorp pts + drink ovl
    // heart on/off every 35-50 secs
    private final int NMZ_NORMAL_RUMBLE_REGION = 9033;

    private final int RAPID_HEAL_WIDGET_ID = 35454988;
    private final int LOGOUT_WIDGET_ID = 11927560;

    private final int NMZ_ABSORPTION_VARBIT = 3956;
    private final int NMZ_OVERLOAD_VARBIT = 3955;

    private final int[] OVERLOAD_IDS = new int [] { 11730, 11731, 11732, 11733 };
    private final int[] ABSORPTION_IDS = new int[] { 11734, 11735, 11736, 11737 };

    @Inject
    private Client client; // Importing Client to get access to invokeMenuAction
    private Random random;

    private int drinkAbsorptionAt = 0;
    private int rapidHealIn = 0;

    @Override
    // Startup function is called on plugin start, initializes absorptionInt
    public void startUp() {

        random = new Random();
        drinkAbsorptionAt = random.nextInt(50) + 450;
        rapidHealIn = random.nextInt(30) + 50;
    }

    @Subscribe
    public void onGameTick(GameTick event) {

        if (inNMZ()) {
            if (getAbsorptionPoints() < drinkAbsorptionAt)
            {

                int lastIndex = -1;
                for (int i = 0; i < random.nextInt(3) + 2; i++) {
                    lastIndex = inventoryAction("Absorption", lastIndex);
                }

                changeAbsorptionInt();
            }
            if (!isOverloaded()) {
                inventoryAction("Overload", -1);
            }
            if (rapidHealIn == 0) {
                toggleRapidHeal();
                toggleRapidHeal();
                changeRapidHeal();
            } else {
                rapidHealIn--;
            }

        } else {
            logout();
        }
    }

    public void changeAbsorptionInt() {

        drinkAbsorptionAt = random.nextInt(50) + 450;
    }

    public void changeRapidHeal() {

        rapidHealIn = random.nextInt(30) + 50;
    }

    public void toggleRapidHeal() {

        client.invokeMenuAction("", "", 1, MenuAction.CC_OP.getId(), -1, RAPID_HEAL_WIDGET_ID);
    }

    private void logout() {

        client.invokeMenuAction("", "", 1, MenuAction.CC_OP.getId(), -1, LOGOUT_WIDGET_ID);
    }

    public int inventoryAction(String name, int lastIndex) {
        // Inventory containers contain a list of id's for items on player
        // ItemContainer class allows us to get inventory
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null) return -1;

        for (int i = 0; i < (inventory.getItems()).length; i++) {
            if (lastIndex > i) {
                Item item = inventory.getItems()[i];

                if (item != null) {
                    ItemComposition def = client.getItemDefinition(item.getId());
                    if (def.getName().contains(name)) {
                        client.invokeMenuAction("", "", def.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), i, 9764864);
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    /*private int getRegionID() {
        // getLocal player gets player location
        // getWorldLocation gets actual X,Y based on the entire runescape map
        // getRegionID gets current region based on world location
        return client.get
    }*/

    private boolean inNMZ() {

        for (int i = 0; i < client.getMapRegions().length; i++) {

            if (client.getMapRegions()[i] == NMZ_NORMAL_RUMBLE_REGION) {
                return true;
            }
        }

        return false;
    }

    private boolean isOverloaded() {

        return client.getVarbitValue(NMZ_OVERLOAD_VARBIT) != 0;
    }

    private int getAbsorptionPoints() {
        // get varbitvalue returns current value of passed id
        return client.getVarbitValue(NMZ_ABSORPTION_VARBIT);
    }
}
