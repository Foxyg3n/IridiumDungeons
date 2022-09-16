package me.foxyg3n.iridiumdungeons.commands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.iridium.iridiumskyblock.commands.Command;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.foxyg3n.iridiumdungeons.Dungeon;
import me.foxyg3n.iridiumdungeons.DungeonGUI;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;
import me.foxyg3n.iridiumdungeons.listeners.RightClickListener;
import me.foxyg3n.iridiumdungeons.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;

public class DungeonCommand extends Command {

    public DungeonCommand() {
        super(Collections.singletonList("dungeons"), "Manage Dungeons", "%prefix% /is dungeons <option>", "", true, Duration.ZERO);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        switch(args.length) {
            case 1 -> {
                player.openInventory(new DungeonGUI(player).getInventory());
            }
            case 2 -> {
                if(args[1].equals("reload") && player.hasPermission("*")) {
                    IridiumDungeons plugin = IridiumDungeons.getInstance();
                    plugin.loadConfigs();
                    plugin.loadCustomConfigs();
                    plugin.getDungeonManager().getDungeonList().forEach(Dungeon::stopDungeon);
                    plugin.getDungeonManager().loadDungeons();
                    plugin.getDungeonManager().getDungeonList().forEach(Dungeon::resetDungeon);
                    MessageUtils.sendInfo(player, "IridiumDungeons reloaded!");
                } else if(args[1].equals("opusc")) {
                    Dungeon dungeon = IridiumDungeons.getInstance().getDungeonManager().getPlayingPlayerDungeon(player);
                    if(dungeon == null) {
                        MessageUtils.sendWarning(player, "Nie znajdujesz się w żadnym dungeonie!");
                        return true;
                    }
                    World world = IridiumDungeons.getInstance().getDefaultWorld();
                    player.teleport(world.getSpawnLocation());
                    if(dungeon.isRunning()) {
                        dungeon.removePlayer(player);
                    } else {
                        dungeon.removeAwaitingPlayer(player);
                    }
                    MessageUtils.sendMessage(player, "Opuściłeś dungeon");
                }
            }
            case 3 -> {
                if(args[1].equals("teleport") && player.hasPermission("*")) {
                    Dungeon dungeon = switch(args[2]) {
                        case "easyDungeon" -> IridiumDungeons.getInstance().getDungeonManager().easyDungeon;
                        case "mediumDungeon" -> IridiumDungeons.getInstance().getDungeonManager().mediumDungeon;
                        case "hardDungeon" -> IridiumDungeons.getInstance().getDungeonManager().hardDungeon;
                        default -> null;
                    };
                    if(dungeon == null) {
                        MessageUtils.sendWarning(player, "Incorrect dungeon name");
                        return true;
                    }
                    Location dungeonSpawn = dungeon.getSpawnLocation();
                    if(dungeonSpawn == null) {
                        MessageUtils.sendWarning(player, "Dungeon's spawn has not been set");
                    } else {
                        MessageUtils.sendMessage(player, "Teleporting to dungeon's spawn");
                        player.teleport(dungeonSpawn);
                    }
                } else if(args[1].equals("showLoot") && player.hasPermission("*")) {
                    Dungeon dungeon = switch(args[2]) {
                        case "easyDungeon" -> IridiumDungeons.getInstance().getDungeonManager().easyDungeon;
                        case "mediumDungeon" -> IridiumDungeons.getInstance().getDungeonManager().mediumDungeon;
                        case "hardDungeon" -> IridiumDungeons.getInstance().getDungeonManager().hardDungeon;
                        default -> null;
                    };
                    dungeon.toggleShowLoot();
                } else if(args[1].equals("bypass") && player.hasPermission("*")) {
                    Dungeon dungeon = getDungeonFromString(args[2]);
                    ItemStack bypassItem = new ItemStack(Material.PAPER);
                    ItemMeta bypassMeta = bypassItem.getItemMeta();
                    PersistentDataContainer data = bypassMeta.getPersistentDataContainer();
                    data.set(new NamespacedKey(IridiumDungeons.getInstance(), "dungeonBypass"), PersistentDataType.STRING, dungeon.getType().name());
                    bypassMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Przepustka "
                        + ChatColor.RESET + "" + ChatColor.GRAY + "- "
                        + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + dungeon.getType().getName());
                    bypassItem.setItemMeta(bypassMeta);
                    player.getInventory().addItem(bypassItem);
                }
            }
            case 4 -> {
                if(args[1].equals("settings") && player.hasPermission("*")) {
                    Dungeon dungeon = switch(args[2]) {
                        case "easyDungeon" -> IridiumDungeons.getInstance().getDungeonManager().easyDungeon;
                        case "mediumDungeon" -> IridiumDungeons.getInstance().getDungeonManager().mediumDungeon;
                        case "hardDungeon" -> IridiumDungeons.getInstance().getDungeonManager().hardDungeon;
                        default -> null;
                    };
                    switch(args[3]) {
                        case "setSpawn" -> {
                            dungeon.setSpawnLocation(player.getLocation());
                            MessageUtils.sendMessage(player, "You've set " + dungeon.getType().getName() + "'s spawn location");
                        }
                        case "addChest" -> {
                            if(player.getLocation().getWorld() != IridiumDungeons.getInstance().getDungeonManager().getDungeonWorld()) {
                                MessageUtils.sendWarning(player, "You can only add chests to the dungeon world");
                                return true;
                            }
                            RightClickListener.addSettingPlayer(player, dungeon.getId());
                            MessageUtils.sendMessage(player, "Set loot chest's position to add to the dungeon");
                        }
                        case "removeChest" -> {
                            dungeon.removeLootChest(player.getLocation());
                            MessageUtils.sendMessage(player, "Removed closest loot chest from the dungeon");
                        }
                        case "removeSpawner" -> {
                            dungeon.removeSpawner(player.getLocation());
                            MessageUtils.sendMessage(player, "Removed closest spawner from the dungeon");
                        }
                        case "setBossSpawn" -> {
                            dungeon.setBossSpawn(player.getLocation());
                            MessageUtils.sendMessage(player, "You've set " + dungeon.getType().getName() + "'s boss spawn location");
                        }
                        case "setBossCheckPos1" -> {
                            dungeon.setBossCheckPos1(player.getLocation());
                            MessageUtils.sendMessage(player, "You've set " + dungeon.getType().getName() + "'s boss position 1");
                        }
                        case "setBossCheckPos2" -> {
                            dungeon.setBossCheckPos2(player.getLocation());
                            MessageUtils.sendMessage(player, "You've set " + dungeon.getType().getName() + "'s boss position 2");
                        }
                        case "setAreaPos1" -> {
                            dungeon.setAreaPos1(player.getLocation());
                            MessageUtils.sendMessage(player, "You've set " + dungeon.getType().getName() + "'s area position 1");
                        }
                        case "setAreaPos2" -> {
                            dungeon.setAreaPos2(player.getLocation());
                            MessageUtils.sendMessage(player, "You've set " + dungeon.getType().getName() + "'s area position 2");
                        }
                        case "stop" -> {
                            dungeon.stopDungeon();
                            MessageUtils.sendMessage(player, "Stopping the dungeon");
                        }
                        case "start" -> {
                            dungeon.runDungeon();
                            MessageUtils.sendMessage(player, "Starting the dungeon");
                        }
                        case "reset" -> {
                            dungeon.resetDungeon();
                            MessageUtils.sendMessage(player, "Reseting the dungeon");
                        }
                        default -> MessageUtils.sendWarning(player, "Uncomplete command");
                    }
                } else if(args[1].equals("utilities") && player.hasPermission("*")) {
                    switch(args[2]) {
                        case "runEasyDungeon" -> {
                            Dungeon dungeon = IridiumDungeons.getInstance().getDungeonManager().easyDungeon;
                            dungeon.resetDungeon();
                            dungeon.addAwaitingPlayer(player);
                            dungeon.runDungeon();
                        }
                        case "runMediumDungeon" -> {
                            Dungeon dungeon = IridiumDungeons.getInstance().getDungeonManager().mediumDungeon;
                            dungeon.resetDungeon();
                            dungeon.addAwaitingPlayer(player);
                            dungeon.runDungeon();
                        }
                        case "runHardDungeon" -> {
                            Dungeon dungeon = IridiumDungeons.getInstance().getDungeonManager().hardDungeon;
                            dungeon.resetDungeon();
                            dungeon.addAwaitingPlayer(player);
                            dungeon.runDungeon();
                        }
                    }
                }
            }
            case 5 -> {
                if(args[1].equals("settings") && player.hasPermission("*")) {
                    Dungeon dungeon = switch(args[2]) {
                        case "easyDungeon" -> IridiumDungeons.getInstance().getDungeonManager().easyDungeon;
                        case "mediumDungeon" -> IridiumDungeons.getInstance().getDungeonManager().mediumDungeon;
                        case "hardDungeon" -> IridiumDungeons.getInstance().getDungeonManager().hardDungeon;
                        default -> null;
                    };
                    switch(args[3]) {
                        case "addSpawner" -> {
                            if(player.getLocation().getWorld() != IridiumDungeons.getInstance().getDungeonManager().getDungeonWorld()) {
                                MessageUtils.sendWarning(player, "You can only add spawners to the dungeon world");
                                return true;
                            }
                            RightClickListener.addSettingPlayer(player, dungeon.getId());
                            if(MythicBukkit.inst().getMobManager().getMobNames().contains(args[4])) {
                                dungeon.addSpawner(player.getLocation(), args[4]);
                                MessageUtils.sendMessage(player, "You've set a spawner for " + args[4] + " dungeon mob");
                            } else {
                                MessageUtils.sendWarning(player, "Spawner for " + args[4] + " not found");
                            }
                        }
                        default -> MessageUtils.sendWarning(player, "Incorrect dungeon mob type");
                    }
                }
            }
            default -> {
                MessageUtils.sendWarning(player, "Incorrect dungeon name");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        List<String> completer = new ArrayList<String>();
        
        if(args.length == 2) {
            completer.add("opusc");
        }

        if(!sender.hasPermission("*")) return completer;

        switch(args.length) {
            case 2 -> {
                completer.add("settings");
                completer.add("teleport");
                completer.add("showLoot");
                completer.add("reload");
                completer.add("bypass");
                completer.add("utilities");
            }
            case 3 -> {
                if(args[1].equals("teleport") || args[1].equals("settings") || args[1].equals("showLoot") || args[1].equals("bypass")) {
                    completer.add("easyDungeon");
                    completer.add("mediumDungeon");
                    completer.add("hardDungeon");
                } else if(args[1].equals("utilities")) {
                    completer.add("runEasyDungeon");
                    completer.add("runMediumDungeon");
                    completer.add("runHardDungeon");
                }
            }
            case 4 -> {
                if(args[1].equals("settings")) {
                    completer.add("addChest");
                    completer.add("addSpawner");
                    completer.add("removeChest");
                    completer.add("removeSpawner");
                    completer.add("setSpawn");
                    completer.add("setBossSpawn");
                    completer.add("setBossCheckPos1");
                    completer.add("setBossCheckPos2");
                    completer.add("setAreaPos1");
                    completer.add("setAreaPos2");
                    completer.add("stop");
                    completer.add("start");
                    completer.add("reset");
                }
            }
            case 5 -> {
                if(args[1].equals("settings") && args[3].equals("addSpawner")) {
                    MythicBukkit.inst().getMobManager().getMobNames().forEach(completer::add);
                }
            }
        }

        return completer;
    }

    private Dungeon getDungeonFromString(String dungeonName) {
        return switch(dungeonName) {
            case "easyDungeon" -> IridiumDungeons.getInstance().getDungeonManager().easyDungeon;
            case "mediumDungeon" -> IridiumDungeons.getInstance().getDungeonManager().mediumDungeon;
            case "hardDungeon" -> IridiumDungeons.getInstance().getDungeonManager().hardDungeon;
            default -> null;
        };
    }
    
}