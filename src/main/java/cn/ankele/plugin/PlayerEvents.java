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
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;

import java.util.HashMap;
import java.util.List;

public class PlayerEvents implements Listener {
    private final HashMap<String, HashMap<String, Long>> allUse = new HashMap<>();
    private final HashMap<String, Long> useTime = new HashMap<>();
    private final HashMap<String, Long> lastInteractTime = new HashMap<>(); // 用于记录每个玩家的上一次交互时间

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        // 检查防抖机制：确保每个玩家每秒只触发一次
        long currentTime = System.currentTimeMillis();
        long lastTime = lastInteractTime.getOrDefault(player.getName(), 0L);
        if (currentTime - lastTime < 1000) { // 如果距离上次触发不足1秒
            return; // 跳过处理
        }
        lastInteractTime.put(player.getName(), currentTime); // 更新最后交互时间

        // 物理触碰不应触发
        if (event.getAction().equals(PlayerInteractEvent.Action.PHYSICAL)) {
            return;
        }

        Item item = player.getInventory().getItemInHand();
        if (!item.hasCompoundTag()) {
            return;
        }
        CompoundTag tag = item.getNamedTag();
        if (!tag.containsString("yamlName")) {
            return;
        }
        Config config = MagicItem.getInstance().getMainConfig();
        if (config.getList("RestrictedWorlds").contains(player.level.getName())) {
            player.sendMessage(MagicItem.getI18n().tr(player.getLanguageCode(), "magicitem.usage.restrictedWorld"));
            return;
        }
        long time = System.currentTimeMillis();
        if (config.getBoolean("GlobalItemCooldown")) {
            if (!this.useTime.containsKey(player.getName()) || (time - this.useTime.get(player.getName())) / 1000 >= ((long) tag.getInt("coolTime"))) {
                this.useTime.put(player.getName(), time);
            } else {
                player.sendMessage(MagicItem.getI18n().tr(player.getLanguageCode(), "magicitem.usage.cooldown", ((long) tag.getInt("coolTime")) - ((time - this.useTime.get(player.getName())) / 1000)));
                return;
            }
        } else if (this.allUse.containsKey(player.getName())) {
            HashMap<String, Long> allUserItem = this.allUse.get(player.getName());
            if (!allUserItem.containsKey(tag.getString("yamlName"))) {
                HashMap<String, Long> temp2 = new HashMap<>();
                temp2.put(tag.getString("yamlName"), time);
                this.allUse.put(player.getName(), temp2);
            } else if ((time - allUserItem.get(tag.getString("yamlName"))) / 1000 < ((long) tag.getInt("coolTime"))) {
                player.sendMessage(MagicItem.getI18n().tr(player.getLanguageCode(), "magicitem.usage.cooldown", ((long) tag.getInt("coolTime")) - ((time - allUserItem.get(tag.getString("yamlName"))) / 1000)));
                return;
            } else {
                HashMap<String, Long> nowUserItem = new HashMap<>();
                nowUserItem.put(tag.getString("yamlName"), time);
                this.allUse.put(player.getName(), nowUserItem);
            }
        } else {
            HashMap<String, Long> temp = new HashMap<>();
            temp.put(tag.getString("yamlName"), time);
            this.allUse.put(player.getName(), temp);
        }
        if (tag.containsList("pCmd")) {
            List<StringTag> cmds = tag.getList("pCmd", StringTag.class).getAll();
            for (StringTag cmdTag : cmds) {
                String cmd = cmdTag.data;
                MagicItem.getInstance().getServer().dispatchCommand(player, cmd);
            }
        }
        if (tag.getBoolean("thunder")) {
            new Lightning(player.chunk, Lightning.getDefaultNBT(player)).spawnToAll();
            new EntityDamageByEntityEvent(player, player, EntityDamageEvent.DamageCause.LIGHTNING, 0.0f);
        }
        if (tag.getBoolean("isCon")) {
            item.setCount(1);
            player.getInventory().removeItem(item);
        }
        if (tag.containsList("opCmd")) {
            List<StringTag> cmds = tag.getList("opCmd", StringTag.class).getAll();
            for (StringTag cmdTag : cmds) {
                String cmd = cmdTag.data;
                if (cmd.contains("{player}")) {
                    runCommand(player, cmd);
                } else {
                    MagicItem.getInstance().getServer().dispatchCommand(new ConsoleCommandSender(), cmd);
                }
            }
        }
        if (tag.containsList("effect")) {
            List<StringTag> args = (List<StringTag>) tag.getList("effect").getAll();
            for (StringTag arg : args) {
                String[] effect = arg.parseValue().split(":");
                player.addEffect(Effect.getEffect(Integer.parseInt(effect[0])).setAmplifier(Integer.parseInt(effect[1])).setDuration(Integer.parseInt(effect[2]) * 20));
            }
        }
        if (tag.containsList("groupEffect")) {
            List<StringTag> args = (List<StringTag>) tag.getList("groupEffect").getAll();
            int distance = tag.getInt("distance");
            if (tag.getInt("actionEntity") == 0) {
                Entity[] entities = player.getLevel().getEntities();
                for (Entity entity : entities) {
                    if (entity.distance(player) <= ((double) distance) && !entity.getName().equals(player.getName())) {
                        for (StringTag arg : args) {
                            String[] effect2 = arg.parseValue().split(":");
                            entity.addEffect(Effect.getEffect(Integer.parseInt(effect2[0])).setAmplifier(Integer.parseInt(effect2[1])).setDuration(Integer.parseInt(effect2[2]) * 20));
                        }
                    }
                }
            } else if (tag.getInt("actionEntity") == 1) {
                Entity[] entities2 = player.getLevel().getEntities();
                for (Entity entity2 : entities2) {
                    if ((entity2 instanceof Player) && entity2.distance(player) <= ((double) distance) && !entity2.getName().equals(player.getName())) {
                        for (StringTag arg : args) {
                            String[] effect3 = arg.parseValue().split(":");
                            entity2.addEffect(Effect.getEffect(Integer.parseInt(effect3[0])).setAmplifier(Integer.parseInt(effect3[1])).setDuration(Integer.parseInt(effect3[2]) * 20));
                        }
                    }
                }
            } else if (tag.getInt("actionEntity") == 2) {
                Entity[] entities3 = player.getLevel().getEntities();
                for (Entity entity3 : entities3) {
                    if (!(entity3 instanceof Player) && entity3.distance(player) <= ((double) distance) && !entity3.getName().equals(player.getName())) {
                        for (StringTag arg : args) {
                            String[] effect4 = arg.parseValue().split(":");
                            entity3.addEffect(Effect.getEffect(Integer.parseInt(effect4[0])).setAmplifier(Integer.parseInt(effect4[1])).setDuration(Integer.parseInt(effect4[2]) * 20));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerLocallyInitializedEvent event) {
        MagicItem.updateItem(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        lastInteractTime.remove(event.getPlayer().getName());
    }

    private void runCommand(Player player, String cmd) {
        MagicItem.getInstance().getServer().dispatchCommand(new ConsoleCommandSender(), cmd.replace("{player}", "\"" + player.getName() + "\""));
    }
}
