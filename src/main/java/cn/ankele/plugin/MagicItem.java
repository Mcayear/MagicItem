package cn.ankele.plugin;

import cn.ankele.plugin.bean.ItemBean;
import cn.ankele.plugin.utils.Commands;
import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static cn.nukkit.inventory.PlayerInventory.*;
import static cn.nukkit.item.Item.*;
import static cn.nukkit.nbt.tag.CompoundTag.*;
import static cn.nukkit.plugin.PluginBase.*;
import static cn.nukkit.utils.Config.*;

public class MagicItem extends PluginBase {
    public static MagicItem instance;
    private static LinkedHashMap<String, ItemBean> items = new LinkedHashMap<>();
    private static LinkedHashMap<String, Object> others = new LinkedHashMap<>();

    public void onEnable() {
        instance = this;
        getLogger().info("魔法物品已加载.....");
        getLogger().info("作者：Ankele");
        File itemsFiles = getItemFile();
        File synFiles = getSynFile();
        File otherFiles = getOtherFile();
        File forgingFiles = getForgingFile();
        if (!itemsFiles.exists()) {
            itemsFiles.mkdirs();
        }
        if (!synFiles.exists()) {
            synFiles.mkdirs();
        }
        if (!otherFiles.exists()) {
            otherFiles.mkdirs();
        }
        if (!forgingFiles.exists()) {
            forgingFiles.mkdirs();
        }
        loadItems();
        initOther();
        saveDefaultConfig();
        getServer().getCommandMap().register("", new Commands("mi"));
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        new Thread(() -> MagicItem.this.getServer().getScheduler().scheduleRepeatingTask(new UpDateTask(), 10, true)).start();
    }
    public Config getMainConfig() {
        Config config = new Config(MagicItem.getInstance().getDataFolder() + "/config.yml", 2);
        return config;
    }
    public void onDisable() {
        super.onDisable();
    }

    public void loadItems() {
        items.clear();
        String[] items_ = getItems();
        for (String itemName : items_) {
            items.put(itemName, new ItemBean(itemName, new Config(getItemFile() + "/" + itemName.toLowerCase() + ".yml")));
        }
    }

    public void initOther() {
        others.clear();
        Config config = new Config();
        config.load(getOtherFile() + "/items.yml", 2);
        others = config.getRootSection();
    }

    private String[] getItems() {
        File[] files;
        LinkedList<String> names = new LinkedList<>();
        File file = new File(String.valueOf(getItemFile()));
        if (file.isDirectory() && (files = file.listFiles()) != null) {
            for (File itemFile : files) {
                names.add(itemFile.getName().substring(0, itemFile.getName().lastIndexOf(".")));
            }
        }
        return names.toArray(new String[0]);
    }

    public File getItemFile() {
        return new File(getDataFolder() + "/items");
    }

    public File getSynFile() {
        return new File(getDataFolder() + "/syns");
    }

    public File getOtherFile() {
        return new File(getDataFolder() + "/other");
    }

    public File getForgingFile() {
        return new File(getDataFolder() + "/forging");
    }

    public static MagicItem getInstance() {
        return instance;
    }

    public static LinkedHashMap<String, ItemBean> getItemsMap() {
        return items;
    }

    public static LinkedHashMap<String, Object> getOthers() {
        return others;
    }

    public static void updateItem(Player player) {
        if (player == null) {
            return;
        }

        PlayerInventory bag = player.getInventory();
        LinkedHashMap<String, ItemBean> items2 = getItemsMap();

        for (int num = 0; num < bag.getSize(); num++) {
            Item item = bag.getItem(num);

            if (item.isNull() || !item.hasCompoundTag()) {
                continue;
            }

            CompoundTag tag = item.getNamedTag();
            if (tag.getString("yamlName").isEmpty()) {
                continue;
            }

            String yamlName = tag.getString("yamlName");
            if (!items2.containsKey(yamlName)) {
                bag.remove(item);
                player.sendMessage("§e魔法物品配置文件不存在...已自动删除");
                continue;
            }
            if (!items2.get(yamlName).attr.isEmpty()) {
                continue;
            }
            Item newItem = Commands.createItem(items2.get(yamlName));
            if (!item.equals(newItem)) {
                newItem.setCount(item.count);
                bag.remove(item);
                bag.setItem(num, newItem);
            }
        }
    }

}
