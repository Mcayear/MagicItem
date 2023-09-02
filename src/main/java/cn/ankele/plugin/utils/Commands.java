package cn.ankele.plugin.utils;

import cn.ankele.plugin.MagicItem;
import cn.ankele.plugin.bean.Award;
import cn.ankele.plugin.bean.ItemBean;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;

import me.onebone.economyapi.EconomyAPI;

public class Commands extends Command {
    private String pluginName = "§e[ §cMagicItem§e ]§r";
    private HashMap<String, Long> useTime = new HashMap<>();

    public Commands(String name) {
        super(name, "魔法物品", "/mi help");
    }

    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length >= 1) {
            String str = strings[0];
            char c = 65535;
            switch (str.hashCode()) {
                case 96417:
                    if (str.equals("add")) {
                        c = 1;
                        break;
                    }
                    break;
                case 101577:
                    if (str.equals("for")) {
                        c = 4;
                        break;
                    }
                    break;
                case 114376:
                    if (str.equals("syn")) {
                        c = 3;
                        break;
                    }
                    break;
                case 3173137:
                    if (str.equals("give")) {
                        c = 2;
                        break;
                    }
                    break;
                case 3198785:
                    if (str.equals("help")) {
                        c = 0;
                        break;
                    }
                    break;
                case 3522941:
                    if (str.equals("save")) {
                        c = 5;
                        break;
                    }
                    break;
                case 3526482:
                    if (str.equals("sell")) {
                        c = 7;
                        break;
                    }
                    break;
                case 3529469:
                    if (str.equals("show")) {
                        c = 6;
                        break;
                    }
                    break;
                case -934641255:
                    if (str.equals("reload")) {
                        c = 9;
                        break;
                    }
                    break;
                case 1757115473:
                    if (str.equals("nbtgive")) {
                        c = '\b';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    commandSender.sendMessage("-----" + this.pluginName + "-----");
                    commandSender.sendMessage("§e/mi add 名称 ID             §7新建魔法物品");
                    commandSender.sendMessage("§e/mi give 玩家 名称 数量     §7给玩家一定数量的魔法物品");
                    commandSender.sendMessage("§e/mi syn create 名称         §7创建合成文件");
                    commandSender.sendMessage("§e/mi syn use 名称            §7使用这个合成文件");
                    commandSender.sendMessage("§e/mi for create 名称         §7创建锻造文件");
                    commandSender.sendMessage("§e/mi for use 名称            §7使用这个锻造文件");
                    commandSender.sendMessage("§e/mi save 名称               §7保存手持物品信息");
                    commandSender.sendMessage("§e/mi show                    §7像全服展示手中物品");
                    commandSender.sendMessage("§e/mi reload                  §7重新读取配置");
                    commandSender.sendMessage("§e/mi sell                    §7回收背包全部可回收的魔法物品");
                    break;
                case 1:
                    if (!commandSender.isOp()) {
                        commandSender.sendMessage("§e>> §a权限不足！");
                        return false;
                    } else if (strings.length >= 3) {
                        ConfigSection data = new ConfigSection(new LinkedHashMap());
                        data.set("物品ID", strings[2]);
                        data.set("物品Damage", 0);
                        data.set("名字", "魔法物品");
                        data.set("获得药水", "1:1:10@2:1:2");
                        data.set("群体药水", "1:1:10@2:1:2");
                        data.set("药水范围", 5);
                        data.set("群体药水作用对象", 0);
                        data.set("使用雷击", false);
                        data.set("OP指令", "say {player}");
                        data.set("以玩家身份执行", "me test");
                        data.set("附魔", "0:1");
                        data.set("显示", "第一行{换行}第二行");
                        data.set("使用消耗", false);
                        data.set("回收价格", 0);
                        data.set("职业限制", "无");
                        data.set("冷却时间", 0);
                        data.set("获得提示", "恭喜获得魔法物品");
                        data.set("全服提示", "恭喜{player}获得魔法物品");
                        data.set("属性.攻击力", Arrays.asList(3, 5));
                        data.set("属性.防御力", Arrays.asList(1, 1));
                        data.set("魔素.风", 2);
                        data.set("魔素.同谐", 1);
                        if (!Tools.isExits(MagicItem.getInstance().getItemFile(), strings[1])) {
                            Config config = new Config(MagicItem.getInstance().getDataFolder().toString() + "/items/" + strings[1].toLowerCase() + ".yml", 2);
                            config.setAll(data);
                            config.save();
                            commandSender.sendMessage(this.pluginName + " §a添加成功！");
                            break;
                        } else {
                            commandSender.sendMessage(this.pluginName + " §a这个物品已存在！");
                            break;
                        }
                    } else {
                        commandSender.sendMessage("-----" + this.pluginName + "-----");
                        commandSender.sendMessage("§e用法：/mi add 名称 ID");
                        return true;
                    }
                case 2:
                    if (!commandSender.isOp()) {
                        commandSender.sendMessage("§e>> §a权限不足！");
                        return false;
                    } else if (strings.length >= 3) {
                        Player player = MagicItem.getInstance().getServer().getPlayer(strings[1]);
                        if (player != null) {
                            LinkedHashMap<String, Object> others = MagicItem.getOthers();
                            int count = 1;
                            if (strings.length > 3) {
                                count = Integer.parseInt(strings[3]);
                            }
                            if (Tools.isExits(MagicItem.getInstance().getItemFile(), strings[2])) {
                                Config config2 = new Config();
                                config2.load(MagicItem.getInstance().getItemFile() + "/" + strings[2].toLowerCase() + ".yml");
                                PlayerInventory inventory = player.getInventory();
                                ItemBean itemBean = new ItemBean(strings[2], config2);
                                Item item = createItem(itemBean);
                                if (!itemBean.getGetHints().isEmpty()) {
                                    player.sendPopup(itemBean.getGetHints());
                                    player.sendMessage(itemBean.getGetHints());
                                }
                                if (!itemBean.getBroadCast().isEmpty()) {
                                    String msg = itemBean.getBroadCast();
                                    MagicItem.getInstance().getServer().broadcastMessage(msg.replace("{player}", player.getName()));
                                    for (Map.Entry<UUID, Player> entry : MagicItem.getInstance().getServer().getOnlinePlayers().entrySet()) {
                                        entry.getValue().sendPopup("", msg.replace("{player}", player.getName()));
                                    }
                                }
                                item.setCount(count);
                                inventory.addItem(new Item[]{item});
                                break;
                            } else if (others.containsKey(strings[2])) {
                                PlayerInventory inventory = player.getInventory();
                                Item item = createSaveItems((String) others.get(strings[2]));
                                item.setCount(count);
                                inventory.addItem(new Item[]{item});
                            } else {
                                commandSender.sendMessage(this.pluginName + " §a这个物品不存在！");
                                break;
                            }
                        } else {
                            commandSender.sendMessage(this.pluginName + " §a玩家不在线！");
                            break;
                        }
                    } else {
                        commandSender.sendMessage("§e用法： /mi give 玩家 名称 数量");
                        return true;
                    }
                case 3:
                    if (strings.length >= 3) {
                        String str2 = strings[1];
                        char c2 = 65535;
                        switch (str2.hashCode()) {
                            case -1352294148:
                                if (str2.equals("create")) {
                                    c2 = 0;
                                    break;
                                }
                                break;
                            case 116103:
                                if (str2.equals("use")) {
                                    c2 = 1;
                                    break;
                                }
                                break;
                        }
                        switch (c2) {
                            case 0:
                                if (commandSender.isOp()) {
                                    ConfigSection data2 = new ConfigSection(new LinkedHashMap());
                                    data2.set("需求", "test:1@test2:2");
                                    data2.set("执行指令", "give {player} 264 66@op {player}");
                                    data2.set("需求金币", 0);
                                    if (!Tools.isExits(MagicItem.getInstance().getSynFile(), strings[2])) {
                                        Config config3 = new Config(MagicItem.getInstance().getDataFolder().toString() + "/syns/" + strings[2].toLowerCase() + ".yml", 2);
                                        config3.setAll(data2);
                                        config3.save();
                                        commandSender.sendMessage(this.pluginName + " §a添加成功！");
                                        break;
                                    } else {
                                        commandSender.sendMessage(this.pluginName + " §a这个合成已存在！");
                                        break;
                                    }
                                } else {
                                    commandSender.sendMessage("§e>> §a权限不足！");
                                    return false;
                                }
                            case 1:
                                if (!commandSender.isPlayer()) {
                                    commandSender.sendMessage("控制台别闹");
                                    return false;
                                } else if (Tools.isExits(MagicItem.getInstance().getSynFile(), strings[2])) {
                                    Player player2 = ((Player) commandSender).getPlayer();
                                    Config config22 = new Config();
                                    config22.load(MagicItem.getInstance().getSynFile() + "/" + strings[2].toLowerCase() + ".yml");
                                    double money = (double) config22.getInt("需求金币");
                                    LinkedHashMap<String, ItemBean> items = MagicItem.getItemsMap();
                                    List<Item> needItems = new ArrayList<>();

                                    String[] needs = config22.getString("需求").split("@");
                                    String[] cmds = config22.getString("执行指令").split("@");
                                    LinkedHashMap<String, Object> others = MagicItem.getOthers();
                                    for (String need : needs) {
                                        String[] needArgs = need.split(":");
                                        String name = needArgs[0];
                                        int count = Integer.parseInt(needArgs[1]);
                                        if (name.startsWith("[NWeapon]")) { // 判断是不是 NWeapon 物品
                                            name = name.substring(9);
                                            boolean isFullTag = false;
                                            if (name.contains(";")) {
                                                isFullTag = true;
                                            }
                                            PlayerInventory bag = player2.getInventory();
                                            boolean hasItem = false;
                                            for (int num = 0; num < bag.getSize(); num++) {
                                                Item bagItem = bag.getItem(num);
                                                if (bagItem == null) continue;
                                                CompoundTag itemtag = bagItem.getNamedTag();
                                                if (itemtag == null) continue;
                                                String NWeaponTag = itemtag.getString("NWeaponNameTag");
                                                if (NWeaponTag == null || Objects.equals(NWeaponTag, "")) continue;
                                                if (isFullTag) {
                                                    if (Objects.equals(NWeaponTag, name)) {
                                                        hasItem = true;
                                                        bagItem.setCount(count);
                                                        needItems.add(bagItem);
                                                        break;
                                                    }
                                                } else {
                                                    if (Objects.equals(NWeaponTag.split(";")[1], name)) {
                                                        hasItem = true;
                                                        bagItem.setCount(count);
                                                        needItems.add(bagItem);
                                                        break;
                                                    }
                                                }
                                            }
                                            if (!hasItem) {
                                                commandSender.sendMessage("§e>> §c你的背包中没有装备：§r" + name);
                                                return false;
                                            }
                                        } else {
                                            if (items.containsKey(name)) {
                                                Item item2 = Commands.createItem(items.get(name));
                                                item2.setCount(count);
                                                needItems.add(item2);
                                            } else if (others.containsKey(name)) {
                                                Item item3 = createSaveItems((String) others.get(name));
                                                item3.setCount(count);
                                                needItems.add(item3);
                                            } else {
                                                commandSender.sendMessage(this.pluginName + " §a检测到合成需求有不存在的物品！");
                                            }
                                        }
                                    }
                                    String[] tipMegs = {config22.getString("失败提示", ""), config22.getString("成功提示", "")};
                                    removeItem(player2, needItems, cmds, money, tipMegs);
                                    break;
                                } else {
                                    commandSender.sendMessage("§e>> §a这个合成不存在！");
                                    return false;
                                }
                        }
                    } else {
                        commandSender.sendMessage("§e用法： /mi syn create 名称");
                        return true;
                    }
                    break;
                case 4:
                    if (strings.length >= 3) {
                        String str3 = strings[1];
                        char c3 = 65535;
                        switch (str3.hashCode()) {
                            case -1352294148:
                                if (str3.equals("create")) {
                                    c3 = 0;
                                    break;
                                }
                                break;
                            case 116103:
                                if (str3.equals("use")) {
                                    c3 = 1;
                                    break;
                                }
                                break;
                        }
                        switch (c3) {
                            case 0:
                                if (commandSender.isOp()) {
                                    ConfigSection data3 = new ConfigSection(new LinkedHashMap());
                                    data3.set("需求", "test:1@test2:2");
                                    data3.set("执行指令", "give {player} 264 66:80@op {player}:20");
                                    data3.set("需求金币", 0);
                                    if (!Tools.isExits(MagicItem.getInstance().getForgingFile(), strings[2])) {
                                        Config config4 = new Config(MagicItem.getInstance().getDataFolder().toString() + "/forging/" + strings[2].toLowerCase() + ".yml", 2);
                                        config4.setAll(data3);
                                        config4.save();
                                        commandSender.sendMessage(this.pluginName + " §a添加成功！");
                                        break;
                                    } else {
                                        commandSender.sendMessage(this.pluginName + " §a这个锻造已存在！");
                                        break;
                                    }
                                } else {
                                    commandSender.sendMessage("§e>> §a权限不足！");
                                    return false;
                                }
                            case 1:
                                if (!commandSender.isPlayer()) {
                                    commandSender.sendMessage("控制台别闹");
                                    return false;
                                } else if (Tools.isExits(MagicItem.getInstance().getForgingFile(), strings[2])) {
                                    Player player3 = ((Player) commandSender).getPlayer();
                                    Config config23 = new Config();
                                    config23.load(MagicItem.getInstance().getForgingFile() + "/" + strings[2].toLowerCase() + ".yml");
                                    double money2 = (double) config23.getInt("需求金币");
                                    LinkedHashMap<String, ItemBean> items2 = MagicItem.getItemsMap();
                                    List<Item> needItems2 = new ArrayList<>();
                                    String[] needs2 = config23.getString("需求").split("@");
                                    //String[] msgs = config23.getString("执行命令").split("@");
                                    ConfigSection cmdValue = config23.getSection("执行命令");
                                    LinkedHashMap<String, Object> others2 = MagicItem.getOthers();
                                    for (String need2 : needs2) {
                                        String name2 = need2.split(":")[0];
                                        int count2 = Integer.parseInt(need2.split(":")[1]);
                                        if (items2.containsKey(name2)) {
                                            Item item4 = Commands.createItem(items2.get(name2));
                                            item4.setCount(count2);
                                            needItems2.add(item4);
                                        } else if (others2.containsKey(name2)) {
                                            Item item5 = createSaveItems((String) others2.get(name2));
                                            item5.setCount(count2);
                                            needItems2.add(item5);
                                        } else {
                                            commandSender.sendMessage(this.pluginName + " §a检测到锻造需求有不存在的物品！");
                                        }
                                    }
                                    //removeItemToForging(player3, needItems2, msgs, money2);
                                    removeItemToForging(player3, needItems2, cmdValue, money2);
                                    break;
                                } else {
                                    commandSender.sendMessage("§e>> §a这个锻造不存在！");
                                    return false;
                                }
                        }
                    } else {
                        commandSender.sendMessage("§e用法： /mi for create 名称");
                        return true;
                    }
                    break;
                case 5:
                    if (commandSender.isOp()) {
                        Player player4 = ((Player) commandSender).getPlayer();
                        if (strings.length >= 2) {
                            Item item6 = player4.getInventory().getItemInHand();
                            if (item6 != null) {
                                saveItem(item6, strings[1]);
                                commandSender.sendMessage(this.pluginName + " §a保存成功！");
                                break;
                            } else {
                                commandSender.sendMessage(this.pluginName + " §a手上没有物品哦！");
                                break;
                            }
                        } else {
                            commandSender.sendMessage("§e用法： /mi save 名称");
                            return true;
                        }
                    } else {
                        commandSender.sendMessage("§e>> §a权限不足！");
                        return false;
                    }
                case 6:
                    long time = System.currentTimeMillis();
                    Config config5 = MagicItem.getInstance().getMainConfig();
                    Player player22 = ((Player) commandSender).getPlayer();
                    Item showItem = player22.getInventory().getItemInHand();
                    if (showItem.hasCompoundTag()) {
                        if (!this.useTime.containsKey(player22.getName())) {
                            this.useTime.put(player22.getName(), Long.valueOf(time));
                        } else if ((time - this.useTime.get(player22.getName()).longValue()) / 1000 < ((long) config5.getInt("ItemDisplayCooldown"))) {
                            player22.sendMessage("§e[§a物品展示§e]§r§a冷却中...剩余" + (((long) config5.getInt("ItemDisplayCooldown")) - ((time - this.useTime.get(player22.getName()).longValue()) / 1000)) + "秒");
                            return true;
                        } else {
                            this.useTime.put(player22.getName(), Long.valueOf(time));
                        }
                        MagicItem.getInstance().getServer().broadcastMessage("§e[§a物品展示§e] §6玩家 §a" + player22.getName() + " §6手里拿的是 §r" + showItem.getNamedTag().getCompound("display").getString("Name"));
                        break;
                    }
                    break;
                case 7:
                    Player player32 = ((Player) commandSender).getPlayer();
                    PlayerInventory bag = player32.getInventory();
                    double total = 0.0d;
                    for (int num = 0; num < bag.getSize(); num++) {
                        Item item22 = bag.getItem(num);
                        if (item22 != null && MagicItem.getItemsMap().containsValue(item22)) {
                            double money3 = item22.getNamedTag().getDouble("sell");
                            if (money3 != 0.0d) {
                                bag.removeItem(new Item[]{item22});
                                EconomyAPI.getInstance().addMoney(player32, ((double) item22.count) * money3);
                                total += ((double) item22.count) * money3;
                            }
                        }
                    }
                    player32.sendMessage(this.pluginName + " §a回收完毕，获得 " + total + "元");
                    break;
                case 9:
                    MagicItem.instance.loadItems();
                    MagicItem.instance.initOther();
                    commandSender.sendMessage(this.pluginName + " §a配置文件已重新读取");
                    break;
                case '\b':
                    if (!commandSender.isOp()) {
                        commandSender.sendMessage("§e>> §a权限不足！");
                        return false;
                    } else if (strings.length >= 3) {
                        sendNbtItem(strings[1], strings[2], Integer.parseInt(strings[3]));
                        break;
                    } else {
                        commandSender.sendMessage("§e用法： /mi nbtgive 玩家 名称 数量");
                        return true;
                    }
            }
        } else {
            commandSender.sendMessage("-----" + this.pluginName + "-----");
            commandSender.sendMessage("§e/mi add 名称 ID             §7新建魔法物品");
            commandSender.sendMessage("§e/mi give 玩家 名称 数量      §7给玩家一定数量的魔法物品");
            commandSender.sendMessage("§e/mi syn create 名称         §7创建合成文件");
            commandSender.sendMessage("§e/mi syn use 名称            §7使用这个合成文件");
            commandSender.sendMessage("§e/mi for create 名称         §7创建锻造文件");
            commandSender.sendMessage("§e/mi for use 名称            §7使用这个锻造文件");
            commandSender.sendMessage("§e/mi save 名称               §7保存手持物品信息");
            commandSender.sendMessage("§e/mi show                    §7像全服展示手中物品");
            commandSender.sendMessage("§e/mi sell                    §7回收背包全部可回收的魔法物品");
        }
        return true;
    }

    public static Item createItem(ItemBean itemBean) {
        Item item = Item.fromString(itemBean.getItemId());
        try {
            if (item.getId() == 0) throw new Exception("无法获取物品！ID为"+itemBean.getItemId());
            item.setDamage(itemBean.getItemDamage());
        } catch (Throwable th) {
            MagicItem.getInstance().getLogger().info("出问题的是:" + itemBean.getYamlName());
        }
        CompoundTag tag = new CompoundTag();
        tag.putCompound("display", new CompoundTag("display").putString("Name", itemBean.getName()));
        tag.putString("yamlName", itemBean.getYamlName());
        tag.putString("effect", itemBean.getEffect());
        tag.putString("groupEffect", itemBean.getGroupEffect());
        tag.putInt("distance", itemBean.getDistance());
        tag.putInt("actionEntity", itemBean.getActionEntity());
        tag.putBoolean("thunder", itemBean.isThunder());
        tag.putString("opCmd", itemBean.getOpCmd());
        tag.putString("pCmd", itemBean.getpCmd());
        tag.putString("getHints", itemBean.getGetHints());
        tag.putBoolean("isCon", itemBean.isCon());
        tag.putString("job", itemBean.getJob());
        tag.putInt("coolTime", itemBean.getCoolTime());
        tag.putString("broadCast", itemBean.getBroadCast());
        tag.putDouble("sell", itemBean.getSell());

        item.setNamedTag(tag);

        List<String> loreList = new ArrayList<>();
        double quality = 0.0;
        if (!itemBean.attr.isEmpty()) {
            quality = selectQuality(loreList);
        }
        loreList.add("§4一一一一一一一一一一");
        loreList.add(itemBean.getLore().replaceAll("\\{换行}", String.valueOf('\n')));
        if (!itemBean.attr.isEmpty()) {
            loreList.add("§4一一一一一一一一一一");
            tag.putCompound("attr", handleAttr(quality, itemBean.attr, loreList));
        }

        item.setNamedTag(tag);

        item.setLore(String.join("\n", loreList));
        if (!itemBean.getEnch().isEmpty()) {
            for (Enchantment enchantment : Tools.getEnchant(itemBean.getEnch())) {
                item.addEnchantment(new Enchantment[]{enchantment});
            }
        }
        return item;
    }
    private static double selectQuality(List<String> loreList) {
        Config c = MagicItem.getInstance().getMainConfig();
        List<Double> qualityList = c.getDoubleList("quality.m");
        List<Double> probabilityList = c.getDoubleList("quality.p");

        // 随机生成一个0到1之间的随机数
        double randomValue = new Random().nextDouble();

        double cumulativeProbability = 0.0;
        for (int i = 0; i < qualityList.size(); i++) {
            cumulativeProbability += probabilityList.get(i);

            if (randomValue < cumulativeProbability) {
                loreList.add("§r§f[§b素材§f]§r            "+c.getStringList("quality.list").get(i));
                return qualityList.get(i);
            }
        }

        // 如果没有匹配的品质，则返回默认品质或者处理其他逻辑
        return 1;
    }
    private static CompoundTag handleAttr(double quality, Map<String, Object> attr, List<String> loreList) {
        CompoundTag compoundTag = new CompoundTag();
        DecimalFormat decimalFormat = new DecimalFormat("#.####");

        for (Map.Entry<String, Object> entry : attr.entrySet()) {
            String attributeName = entry.getKey();
            Object attributeValue = entry.getValue();

            if (attributeValue instanceof List<?>) {
                List<Double> attributeValues = (List<Double>) attributeValue;
                ListTag<DoubleTag> modifiedValues = new ListTag<>();

                for (Double value : attributeValues) {
                    // 将属性值乘以品质因子
                    double modifiedValue = value * quality;
                    String formattedValue = (value < 1) ? decimalFormat.format(modifiedValue) : String.valueOf((int) modifiedValue);
                    modifiedValues.add(new DoubleTag("", Double.parseDouble(formattedValue)));
                }
                if (modifiedValues.size() == 1) {
                    Double num = modifiedValues.get(0).getData();
                    if (num < 1 && num > 0) {
                        loreList.add("§7" + attributeName + ": " + num + "%%");// §7attrName: 123%%
                    } else {
                        loreList.add("§7" + attributeName + ": " + (int) Math.floor(num));// §7attrName: 123
                    }
                } else {
                    loreList.add("§7" + attributeName + ": " + (int) Math.floor(modifiedValues.get(0).getData()) + "-" + (int) Math.floor(modifiedValues.get(1).getData()));// §7attrName: 123-321
                }

                // 将修改后的属性值添加到CompoundTag
                compoundTag.putList(attributeName, modifiedValues);
            }
        }

        return compoundTag;
    }
    private Item createSaveItems(String msg) {
        String[] args = msg.split(":");
        Item item = Item.get(Integer.parseInt(args[0]), Integer.valueOf(Integer.parseInt(args[1])));
        item.setCount(Integer.parseInt(args[2]));
        item.setCompoundTag(Tools.hexStringToBytes(args[3]));
        return item;
    }

    private void saveItem(Item item, String name) {
        String tag = item.hasCompoundTag() ? Tools.bytesToHexString(item.getCompoundTag()) : "not";
        Config config = new Config(MagicItem.getInstance().getOtherFile() + "/items.yml", 2);
        config.set(name, item.getId() + ":" + item.getDamage() + ":" + item.getCount() + ":" + tag);
        config.save();
    }

    private void removeItem(Player player, List<Item> items, String[] cmds, double money, String[] tipMegs) {
        int i = 0;
        Inventory inventory = player.getInventory();
        List<String> needItemList = new ArrayList<String>();
        for (Item item : items) {
            if (inventory.contains(item)) {
                i++;
            } else {
                String itemname = item.getNamedTag().getCompound("display").getString("Name");
                if (item.getId() == 387) {
                    itemname = item.getNamedTag().getString("title");
                }
                needItemList.add(itemname + " §r*" + item.count);
            }
        }
        if (i == items.size() && EconomyAPI.getInstance().myMoney(player) >= money) {
            for (Item item : items) {
                inventory.removeItem(new Item[]{item});
            }
            player.sendMessage(tipMegs[1] == "" ? "§a=== 合成成功 ===" : tipMegs[1]);
            for (String cmd : cmds) {
                if (cmd.contains("{player}")) {
                    runCommand(player, cmd);
                } else {
                    MagicItem.getInstance().getServer().dispatchCommand(new ConsoleCommandSender(), cmd);
                }
            }
            EconomyAPI.getInstance().reduceMoney(player.getName(), money);
            return;
        }
        if (money > 0) {
            needItemList.add("§6金币 §r*" + money);
        }
        final String needItems = String.join("、", needItemList);
        player.sendMessage((tipMegs[0] == "" ? "§c缺少材料：" : tipMegs[0]) + needItems);
    }

    private void removeItemToForging(Player player, List<Item> items, ConfigSection msgs, double money) {
        int i = 0;
        List<Award> awards = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();
        for (Item item : items) {
            if (inventory.contains(item) && EconomyAPI.getInstance().myMoney(player.getName()) > money) {
                i++;
            }
        }
        if (i == items.size()) {
            Iterator<Item> it = items.iterator();
            while (it.hasNext()) {
                inventory.removeItem(new Item[]{it.next()});
            }
            player.sendMessage("§e>> §a锻造成功！");
            Map<String, Object> cmdMap = msgs.getAllMap();
            for (Map.Entry<String, Object> entry : cmdMap.entrySet()) {
                String key = entry.getKey();
                String[] stringArray = (String[]) entry.getValue();
                awards.add(new Award(stringArray, Float.parseFloat(key)));
            }
            Award award = Tools.lottery(awards);
            if (award != null) {
                for (String cmd : award.cmd) {
                    if (cmd.contains("{player}")) {
                        runCommand(player, cmd);
                    } else {
                        MagicItem.getInstance().getServer().dispatchCommand(new ConsoleCommandSender(), cmd);
                    }
                }
                EconomyAPI.getInstance().reduceMoney(player.getName(), money);
                return;
            }
            return;
        }
        player.sendMessage("§e>> §c锻造失败 §e<<");
        player.sendMessage("§e>> §a需要物品 §e<<");
        for (Item item2 : items) {
            player.sendMessage(item2.getNamedTag().getCompound("display").getString("Name") + "§r*" + item2.count);
        }
        player.sendMessage("§6金币§r*" + money);
    }

    private void runCommand(Player player, String cmd) {
        MagicItem.getInstance().getServer().dispatchCommand(new ConsoleCommandSender(), cmd.replace("{player}", player.getName()));
    }

    private void sendNbtItem(String str, String name, int count) {
        Player player = MagicItem.getInstance().getServer().getPlayer(str);
        if (player != null) {
            Item item = createSaveItems((String) MagicItem.getOthers().get(name));
            item.setCount(count);
            player.getInventory().addItem(new Item[]{item});
            player.sendMessage("§e>> §a恭喜获得 §5" + name);
        }
    }
}
