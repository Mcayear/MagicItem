package cn.ankele.plugin;

import cn.ankele.plugin.utils.Lightning;
import cn.nukkit.Player;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;

import java.util.HashMap;

public class PlayerEvents implements Listener {
    private HashMap<String, HashMap<String, Long>> allUse = new HashMap<>();
    private HashMap<String, Long> useTime = new HashMap<>();

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Item item = player.getInventory().getItemInHand();
        if (!item.hasCompoundTag()) {
            return;
        }
        CompoundTag tag = item.getNamedTag();
        if (tag.getString("yamlName").isEmpty()) {
            return;
        }
        Config config = MagicItem.getInstance().getMainConfig();
        if (config.getList("RestrictedWorlds").contains(player.level.getName())) {
            player.sendMessage("§e[§cMagicItem§e]§r§a这个世界不允许使用魔法物品！");
            return;
        }
        long time = System.currentTimeMillis();
        if (config.getBoolean("GlobalItemCooldown")) {
            if (!this.useTime.containsKey(player.getName()) || (time - this.useTime.get(player.getName()).longValue()) / 1000 >= ((long) tag.getInt("coolTime"))) {
                this.useTime.put(player.getName(), Long.valueOf(time));
            } else {
                player.sendMessage("§e[§cMagicItem§e]§r§a冷却中...剩余" + (((long) tag.getInt("coolTime")) - ((time - this.useTime.get(player.getName()).longValue()) / 1000)) + "秒");
                return;
            }
        } else if (this.allUse.containsKey(player.getName())) {
            HashMap<String, Long> allUserItem = this.allUse.get(player.getName());
            if (!allUserItem.containsKey(tag.getString("yamlName"))) {
                HashMap<String, Long> temp2 = new HashMap<>();
                temp2.put(tag.getString("yamlName"), Long.valueOf(time));
                this.allUse.put(player.getName(), temp2);
            } else if ((time - allUserItem.get(tag.getString("yamlName")).longValue()) / 1000 < ((long) tag.getInt("coolTime"))) {
                player.sendMessage("§e[§cMagicItem§e]§r§a冷却中...剩余" + (((long) tag.getInt("coolTime")) - ((time - allUserItem.get(tag.getString("yamlName")).longValue()) / 1000)) + "秒");
                return;
            } else {
                HashMap<String, Long> nowUserItem = new HashMap<>();
                nowUserItem.put(tag.getString("yamlName"), Long.valueOf(time));
                this.allUse.put(player.getName(), nowUserItem);
            }
        } else {
            HashMap<String, Long> temp = new HashMap<>();
            temp.put(tag.getString("yamlName"), Long.valueOf(time));
            this.allUse.put(player.getName(), temp);
        }
        if (!tag.getString("pCmd").isEmpty()) {
            String[] cmds = tag.getString("pCmd").split("@");
            int length = cmds.length;
            for (int i = 0; i < length; i++) {
                MagicItem.getInstance().getServer().dispatchCommand(player, cmds[i]);
            }
        }
        if (tag.getBoolean("thunder")) {
            new Lightning(player.chunk, Lightning.getDefaultNBT(player)).spawnToAll();
            new EntityDamageByEntityEvent(player, player, EntityDamageEvent.DamageCause.LIGHTNING, 0.0f);
        }
        if (tag.getBoolean("isCon")) {
            item.setCount(1);
            player.getInventory().removeItem(new Item[]{item});
        }
        if (!tag.getString("opCmd").isEmpty()) {
            String[] cmds2 = tag.getString("opCmd").split("@");
            int length2 = cmds2.length;
            for (int i2 = 0; i2 < length2; i2++) {
                String cmd = cmds2[i2];
                if (cmd.contains("{player}")) {
                    runCommand(player, cmd);
                } else {
                    MagicItem.getInstance().getServer().dispatchCommand(new ConsoleCommandSender(), cmd);
                }
            }
        }
        if (!tag.getString("effect").isEmpty()) {
            String[] args = tag.getString("effect").split("@");
            int length3 = args.length;
            for (int i3 = 0; i3 < length3; i3++) {
                String[] effect = args[i3].split(":");
                player.addEffect(Effect.getEffect(Integer.parseInt(effect[0])).setAmplifier(Integer.parseInt(effect[1])).setDuration(Integer.parseInt(effect[2]) * 20));
            }
        }
        if (!tag.getString("groupEffect").isEmpty()) {
            String[] args2 = tag.getString("groupEffect").split("@");
            int distance = tag.getInt("distance");
            if (tag.getInt("actionEntity") == 0) {
                Entity[] entities = player.getLevel().getEntities();
                int length4 = entities.length;
                for (int i4 = 0; i4 < length4; i4++) {
                    Entity entity = entities[i4];
                    if (entity.distance(player) <= ((double) distance) && !entity.getName().equals(player.getName())) {
                        int length5 = args2.length;
                        for (int i5 = 0; i5 < length5; i5++) {
                            String[] effect2 = args2[i5].split(":");
                            entity.addEffect(Effect.getEffect(Integer.parseInt(effect2[0])).setAmplifier(Integer.parseInt(effect2[1])).setDuration(Integer.parseInt(effect2[2]) * 20));
                        }
                    }
                }
            } else if (tag.getInt("actionEntity") == 1) {
                Entity[] entities2 = player.getLevel().getEntities();
                int length6 = entities2.length;
                for (int i6 = 0; i6 < length6; i6++) {
                    Entity entity2 = entities2[i6];
                    if ((entity2 instanceof Player) && entity2.distance(player) <= ((double) distance) && !entity2.getName().equals(player.getName())) {
                        int length7 = args2.length;
                        for (int i7 = 0; i7 < length7; i7++) {
                            String[] effect3 = args2[i7].split(":");
                            entity2.addEffect(Effect.getEffect(Integer.parseInt(effect3[0])).setAmplifier(Integer.parseInt(effect3[1])).setDuration(Integer.parseInt(effect3[2]) * 20));
                        }
                    }
                }
            } else if (tag.getInt("actionEntity") == 2) {
                Entity[] entities3 = player.getLevel().getEntities();
                int length8 = entities3.length;
                for (int i8 = 0; i8 < length8; i8++) {
                    Entity entity3 = entities3[i8];
                    if (!(entity3 instanceof Player) && entity3.distance(player) <= ((double) distance) && !entity3.getName().equals(player.getName())) {
                        int length9 = args2.length;
                        for (int i9 = 0; i9 < length9; i9++) {
                            String[] effect4 = args2[i9].split(":");
                            entity3.addEffect(Effect.getEffect(Integer.parseInt(effect4[0])).setAmplifier(Integer.parseInt(effect4[1])).setDuration(Integer.parseInt(effect4[2]) * 20));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
    }

    private void runCommand(Player player, String cmd) {
        MagicItem.getInstance().getServer().dispatchCommand(new ConsoleCommandSender(), cmd.replace("{player}", "\"" + player.getName() + "\""));
    }
}
