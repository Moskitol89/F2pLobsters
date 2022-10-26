import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.depositbox.DepositBox;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Player;

@ScriptManifest(category = Category.FISHING, name = "F2pLobsters", description = "free to play lobster farm(fishing 40+)",
        author = "Moskitol89", version = 0.1, image = "")

public class FreeLobsters extends AbstractScript {
    private final Player PLAYER = Players.getLocal();
    private Area sheepToLobstersArea = new Area(3026, 3224, 3029, 3215);
    private Area lobstersArea = new Area(2924, 3175, 2925, 3180);
    private Area sheepToTownArea = new Area(2950, 3150, 2960, 3146);
    private Area depositArea = new Area(3037, 3237, 3047, 3234);
    private boolean islandOutSheep = false;
    private boolean townOutSheep = false;
    private boolean isOnIsland = false;
    private boolean dialogEnd = false;

    private enum STATES {
        COMINGTOISLAND, FISHING, COMINGTOTOWN, DEPOSIT
    }

    private STATES state = STATES.COMINGTOISLAND;

    @Override
    public int onLoop() {
        switch (state) {
            case COMINGTOISLAND -> {
                log("Coming to island");
                if (isOnIsland) {
                    state = STATES.FISHING;
                    break;
                }
                if (Inventory.isFull()) {
                    state = STATES.COMINGTOTOWN;
                    break;
                }
                if (!sheepToLobstersArea.contains(PLAYER.getTile())) {
                    Walking.walk(sheepToLobstersArea.getRandomTile());
                } else {
                    if (!Dialogues.inDialogue()) {
                        NPCs.closest("Captain Tobias").interact("Talk-to");
                        sleep(1000L, 2600L);
                    } else {
                        sleep(900L, 1600L);
                        Dialogues.continueDialogue();
                        sleep(900L, 1600L);
                        Dialogues.continueDialogue();
                        sleep(900L, 1600L);
                        Dialogues.chooseOption(1);
                        sleep(1000L, 2600L);
                        Dialogues.continueDialogue();
                        sleep(900L, 1600L);
                        islandOutSheep = false;
                        state = STATES.FISHING;
                        break;
                    }
                }
            }
            case FISHING -> {
                log("fishing");
                isOnIsland = true;
                if (!islandOutSheep) {
                    GameObjects.closest("Gangplank").interact("Cross");
                    sleep(1000L, 1500L);
                    islandOutSheep = true;
                }
                if (Inventory.isFull()) {
                    state = STATES.COMINGTOTOWN;
                    break;
                }
                if (!lobstersArea.contains(PLAYER.getTile())) {
                    Walking.walk(lobstersArea.getRandomTile());
                } else if (!PLAYER.isAnimating() && !PLAYER.isMoving()) {
                    NPCs.closest(1522).interact("Cage");
                }
            }
            case COMINGTOTOWN -> {
                log("Comming to town");
                isOnIsland = true;
                if (isOnIsland) {
                    if (!sheepToTownArea.contains(PLAYER.getTile())) {
                        Walking.walk(sheepToTownArea.getRandomTile());
                    } else {
                        if(!Dialogues.inDialogue()) {
                            NPCs.closest("Customs officer").interact("Talk-to");
                            sleep(1000L, 1500L);
                        } else {
                            sleep(900L, 1600L);
                            Dialogues.continueDialogue();
                            sleep(900L, 1600L);
                            Dialogues.chooseOption(1);
                            sleep(900L, 1600L);
                            Dialogues.continueDialogue();
                            sleep(900L, 1600L);
                            Dialogues.continueDialogue();
                            sleep(900L, 1600L);
                            Dialogues.chooseOption(2);
                            sleep(900L, 1600L);
                            Dialogues.continueDialogue();
                            sleep(900L, 1600L);
                            Dialogues.continueDialogue();
                            sleep(900L, 1600L);
                            Dialogues.chooseOption(1);
                            sleep(900L, 1600L);
                            Dialogues.continueDialogue();
                            sleep(900L, 1600L);
                            //TD
                            isOnIsland = false;
                            townOutSheep = false;
                            state = STATES.DEPOSIT;
                            break;
                        }
                    }
                } else {
                    state = STATES.DEPOSIT;
                }
            }
            case DEPOSIT -> {
                log("In town");
                if (!townOutSheep) {
                    GameObjects.closest("Gangplank").interact("Cross");
                    sleep(1000L, 1500L);
                    townOutSheep = true;
                }
                if(!depositArea.contains(PLAYER.getTile())) {
                    Walking.walk(depositArea.getRandomTile());
                } else {
                    DepositBox.openClosest();
                    if(DepositBox.isOpen()) {
                        DepositBox.depositAll("Raw lobster");
                        sleep(1200L, 2100L);
                        state = STATES.COMINGTOISLAND;
                        break;
                    }
                }

            }
        }
        return Calculations.random(700, 1500);
    }
}
