package cn.ankele.plugin.utils;

import cn.ankele.plugin.MagicItem;
import cn.ankele.plugin.bean.Award;
import cn.ankele.plugin.bean.ItemBean;
import cn.ankele.plugin.bean.MagicItemAttr;
import cn.ankele.plugin.bean.MagicItemMana;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.LangCode;
import cn.nukkit.lang.PluginI18n;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.util.*;

import cn.nukkit.utils.TextFormat;
import me.onebone.economyapi.EconomyAPI;

import static cn.ankele.plugin.MagicItem.hasRcRPG;

public class BaseCommand extends Command {
    protected MagicItem api;
    protected PluginI18n i18n;
    private String pluginName = "§e[ §cMagicItem§e ]§r";
    private HashMap<String, Long> useTime = new HashMap<>();

    public BaseCommand(String name) {
        super(name, "魔法物品");

        this.getCommandParameters().clear();

        this.getCommandParameters().put("help", new CommandParameter[]{
                CommandParameter.newEnum("help", false, new String[]{"help"})
        });

        this.getCommandParameters().put("add", new CommandParameter[]{
                CommandParameter.newEnum("add", false, new String[]{"add"}),
                CommandParameter.newType("itemName", false, CommandParamType.STRING),
                CommandParameter.newType("itemId", false, CommandParamType.STRING)
        });

        this.getCommandParameters().put("give", new CommandParameter[]{
                CommandParameter.newEnum("give", false, new String[]{"give"}),
                CommandParameter.newType("player", true, CommandParamType.TARGET),
                CommandParameter.newType("itemName", false, CommandParamType.STRING),
                CommandParameter.newType("count", true, CommandParamType.INT),
                CommandParameter.newType("quality", true, CommandParamType.INT)
        });

        this.getCommandParameters().put("syn", new CommandParameter[]{
                CommandParameter.newEnum("syn", false, new String[]{"syn"}),
                CommandParameter.newEnum("operation", false, new String[]{"use", "create"}),
                CommandParameter.newType("synName", false, CommandParamType.STRING)
        });

        this.getCommandParameters().put("for", new CommandParameter[]{
                CommandParameter.newEnum("for", false, new String[]{"for"}),
                CommandParameter.newEnum("operation", false, new String[]{"use", "create"}),
                CommandParameter.newType("forName", false, CommandParamType.STRING)
        });

        this.getCommandParameters().put("save", new CommandParameter[]{
                CommandParameter.newEnum("save", false, new String[]{"save"}),
                CommandParameter.newType("itemName", false, CommandParamType.STRING)
        });

        this.getCommandParameters().put("show", new CommandParameter[]{
                CommandParameter.newEnum("show", false, new String[]{"show"})
        });

        this.getCommandParameters().put("reload", new CommandParameter[]{
                CommandParameter.newEnum("reload", false, new String[]{"reload"})
        });

        this.getCommandParameters().put("sell", new CommandParameter[]{
                CommandParameter.newEnum("sell", false, new String[]{"sell"})
        });

        api = MagicItem.getInstance();
        i18n = MagicItem.getI18n();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        LangCode langCode = sender.isPlayer() ? ((Player) sender).getLanguageCode() : LangCode.zh_CN;

        if (args.length < 1) {
            sender.sendMessage("缺少参数");
            return false;
        }
        switch (args[0]) {
            case "help" -> {
                this.sendCommandHelp(sender);
                return true;
            }
            case "add" -> {
                if (!sender.hasPermission("magicitem.command.op")) {
                    sender.sendMessage(i18n.tr(langCode, "magicitem.usage.permission"));
                    return false;
                }
                String itemName = args[1];
                String itemId = args[2];

                if (Tools.isExits(MagicItem.getInstance().getItemFile(), itemName)) {
                    sender.sendMessage(TextFormat.RED + "§c这个物品已存在！");
                    return false;
                }

                ConfigSection data = new ConfigSection(new LinkedHashMap<>());
                data.set("物品ID", itemId);
                data.set("物品Meta", 0);
                data.set("名字", "魔法物品");
                data.set("获得药水", List.of("1:1:10", "2:1:2"));
                data.set("群体药水", List.of("1:1:10", "2:1:2"));
                data.set("药水范围", 5);
                data.set("群体药水作用对象", 0);
                data.set("使用雷击", false);
                data.set("OP指令", List.of("say {player}"));
                data.set("以玩家身份执行", List.of("me test"));
                data.set("附魔", List.of("0:1"));
                data.set("显示", List.of("第一行", "第二行"));
                data.set("使用消耗", false);
                data.set("回收价格", 0);
                data.set("职业限制", "无");
                data.set("冷却时间", 0);
                data.set("获得提示", "恭喜获得魔法物品");
                data.set("全服提示", "恭喜 {player} 获得魔法物品");
//                data.set("属性.攻击力", Arrays.asList(3.0f, 5.0f));
//                data.set("属性.防御力", Arrays.asList(1.0f, 1.0f));
//                data.set("魔素.风", 2);
//                data.set("魔素.同谐", 1);
                Config config = new Config(MagicItem.getInstance().getDataFolder().toString() + "/items/" + itemName.toLowerCase() + ".yml", 2);
                config.setAll(data);
                config.save();
                sender.sendMessage("§a添加成功！");
                return true;
            }
            case "give" -> {
                Player player = api.getServer().getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage("没有匹配的目标");
                    return false;
                }

                if (!sender.hasPermission("magicitem.command.op")) {
                    sender.sendMessage(i18n.tr(langCode, "magicitem.usage.permission"));
                    return false;
                }

                String itemName = args[2];
                int count = args.length > 3 ? Integer.parseInt(args[3]) : 1;
                ItemBean itemBean = null;
                Item item;

                LinkedHashMap<String, Object> others = MagicItem.getOthers();
                if (Tools.isExits(MagicItem.getInstance().getItemFile(), itemName)) {
                    Config config2 = new Config();
                    config2.load(MagicItem.getInstance().getItemFile() + "/" + itemName.toLowerCase() + ".yml");
                    itemBean = new ItemBean(itemName, config2);
                    if (args.length > 4) {
                        item = createItem(itemBean, Integer.parseInt(args[4]));
                    } else {
                        item = createItem(itemBean);
                    }
                } else if (others.containsKey(itemName)) {
                    item = createSaveItems((String) others.get(itemName));
                } else {
                    sender.sendMessage("§c无法给予，物品§r " + itemName + "§r§c 不存在！");
                    return false;
                }

                item.setCount(count);

                if (itemBean != null && !itemBean.getGetHints().isEmpty()) {
                    player.sendPopup(itemBean.getGetHints());
                    player.sendMessage(itemBean.getGetHints());
                }
                if (itemBean != null && !itemBean.getBroadCast().isEmpty()) {
                    String msg = itemBean.getBroadCast();
                    MagicItem.getInstance().getServer().broadcastMessage(msg.replace("{player}", player.getName()));
                    for (Map.Entry<UUID, Player> entry : MagicItem.getInstance().getServer().getOnlinePlayers().entrySet()) {
                        entry.getValue().sendPopup("", msg.replace("{player}", player.getName()));
                    }
                }
                item.setCount(count);
                PlayerInventory inventory = player.getInventory();
                inventory.addItem(new Item[]{item});
                return true;
            }
            case "syn" -> {
                String operation = args[1];
                String synName = args[2];

                if (Objects.equals(operation, "create")) {
                    if (!sender.hasPermission("magicitem.command.op")) {
                        sender.sendMessage("magicitem.usage.permission");
                        return false;
                    }

                    ConfigSection data2 = new ConfigSection(new LinkedHashMap<>());
                    data2.set("需求", List.of("test:1", "test2:2"));
                    data2.set("执行指令", List.of("give {player} 264 66", "op {player}"));
                    data2.set("需求金币", 0);
                    if (!Tools.isExits(MagicItem.getInstance().getSynFile(), synName)) {
                        Config config3 = new Config(MagicItem.getInstance().getDataFolder().toString() + "/syns/" + synName.toLowerCase() + ".yml", 2);
                        config3.setAll(data2);
                        config3.save();
                        sender.sendMessage("§a添加成功！");
                        return true;
                    } else {
                        sender.sendMessage("§6这个合成已存在！");
                        return false;
                    }
                } else if (Objects.equals(operation, "use")) {
                    if (!sender.isPlayer()) {
                        sender.sendMessage(TextFormat.RED + "仅限玩家执行");
                        return false;
                    }
                    if (!Tools.isExits(MagicItem.getInstance().getSynFile(), synName)) {
                        sender.sendMessage("§c这个合成不存在！");
                        return false;
                    }
                    Player player2 = ((Player) sender).getPlayer();
                    Config config22 = new Config();
                    config22.load(MagicItem.getInstance().getSynFile() + "/" + synName.toLowerCase() + ".yml");
                    double money = config22.getInt("需求金币");
                    LinkedHashMap<String, ItemBean> items = MagicItem.getItemsMap();
                    List<Item> needItems = new ArrayList<>();

                    List<String> needs = config22.getStringList("需求");
                    List<String> cmds = config22.getStringList("执行指令");
                    LinkedHashMap<String, Object> others = MagicItem.getOthers();
                    for (String need : needs) {
                        String[] needArgs = need.split(":");
                        String name = needArgs[0];
                        int count = Integer.parseInt(needArgs[1]);
                        if (name.startsWith("[RcRPG]")) { // 判断是不是 RcRPG 物品
                            String[] nameTag = name.substring(7).split(";");
                            if (nameTag.length == 1 || nameTag.length > 2) {
                                sender.sendMessage("§e本合成方案有误，请联系管理员§r" + name);
                            }

                            PlayerInventory bag = player2.getInventory();
                            boolean hasItem = false;
                            for (int num = 0; num < bag.getSize(); num++) {
                                Item bagItem = bag.getItem(num).clone();
                                if (bagItem.isNull()) continue;// 物品是 AIR
                                CompoundTag itemtag = bagItem.getNamedTag();
                                if (itemtag == null) continue;// 物品沒有 NBT
                                String type = itemtag.getString("type");
                                if (type.isEmpty()) continue;// type 為 null
                                String yamlName = itemtag.getString("name");// yamlName 值沒有 .yml 后綴
                                if (yamlName.isEmpty()) continue;// yamlName 為 null
                                if (!type.equals(nameTag[0])) continue;
                                if (!yamlName.equals(nameTag[1])) continue;

                                hasItem = true;
                                bagItem.setCount(count);
                                needItems.add(bagItem);
                                break;
                            }
                            if (!hasItem) {
                                sender.sendMessage("§c你的背包中没有装备：§r" + name);
                                return false;
                            }
                        } else {
                            if (items.containsKey(name)) {
                                Item item = createItem(items.get(name));
                                item.setCount(count);
                                needItems.add(item);
                            } else if (others.containsKey(name)) {
                                Item item = createSaveItems((String) others.get(name));
                                item.setCount(count);
                                needItems.add(item);
                            } else {
                                sender.sendMessage("§c检测到合成需求有不存在的物品：§r" + name);
                                return false;
                            }
                        }
                    }
                    String[] tipMegs = {config22.getString("失败提示", ""), config22.getString("成功提示", "")};
                    removeItem(player2, needItems, cmds, money, tipMegs);
                    return true;
                }
                sender.sendMessage("§c/mi syn 中不存在这个操作");
                return false;
            }
            case "for" -> {
                String operation = args[1];
                String forName = args[2];

                if (Objects.equals(operation, "create")) {
                    if (!sender.hasPermission("magicitem.command.op")) {
                        sender.sendMessage(i18n.tr(langCode, "magicitem.usage.permission"));
                        return false;
                    }

                    if (Tools.isExits(MagicItem.getInstance().getForgingFile(), forName)) {
                        sender.sendMessage("§6这个锻造已存在！");
                        return false;
                    }

                    ConfigSection data = new ConfigSection(new LinkedHashMap<>());
                    data.set("需求", List.of("test:1", "test2:2"));
                    data.set("执行指令", new ConfigSection(new LinkedHashMap<>() {
                        {
                            put("80", List.of("give {player} 264 66"));
                            put("20", List.of("op {player}"));
                        }
                    }));
                    data.set("需求金币", 0);
                    Config config = new Config(MagicItem.getInstance().getDataFolder().toString() + "/forging/" + forName.toLowerCase() + ".yml", 2);
                    config.setAll(data);
                    config.save();
                    sender.sendMessage("§a添加成功！");
                    return true;
                } else if (Objects.equals(operation, "use")) {
                    if (!sender.isPlayer()) {
                        sender.sendMessage("仅限玩家执行");
                        return false;
                    }

                    if (!Tools.isExits(MagicItem.getInstance().getForgingFile(), forName)) {
                        sender.sendMessage("§c这个锻造不存在！");
                        return false;
                    }
                    Player player3 = ((Player) sender).getPlayer();
                    Config config = new Config();
                    config.load(MagicItem.getInstance().getForgingFile() + "/" + forName.toLowerCase() + ".yml");
                    double money2 = config.getInt("需求金币");

                    LinkedHashMap<String, ItemBean> itemsMap = MagicItem.getItemsMap();
                    List<Item> needItems = new ArrayList<>();

                    List<String> needArray = config.getStringList("需求");
                    //String[] msgs = config.getString("执行指令").split("@");
                    ConfigSection cmdValue = config.getSection("执行指令");
                    LinkedHashMap<String, Object> others2 = MagicItem.getOthers();
                    for (String needArray2 : needArray) {
                        String itemName = needArray2.split(":")[0];
                        int count = Integer.parseInt(needArray2.split(":")[1]);
                        if (itemsMap.containsKey(itemName)) {
                            Item item4 = createItem(itemsMap.get(itemName));
                            item4.setCount(count);
                            needItems.add(item4);
                        } else if (others2.containsKey(itemName)) {
                            Item item5 = createSaveItems((String) others2.get(itemName));
                            item5.setCount(count);
                            needItems.add(item5);
                        } else {
                            sender.sendMessage("§c检测到锻造合成需求有不存在的物品：§r" + itemName);
                            return false;
                        }
                    }
                    String[] tipMegs = {config.getString("失败提示", ""), config.getString("成功提示", "")};
                    removeItemToForging(player3, needItems, cmdValue, money2, tipMegs);

                    return true;
                }

                sender.sendMessage("§c/mi for 中不存在这个操作");
                return false;
            }
            case "save" -> {
                if (!sender.hasPermission("magicitem.command.op")) {
                    sender.sendMessage(i18n.tr(langCode, "magicitem.usage.permission"));
                    return false;
                }

                if (!sender.isPlayer()) {
                    sender.sendMessage(TextFormat.RED + "仅限玩家执行");
                    return false;
                }
                Player player = sender.asPlayer();
                Item item = player.getInventory().getItemInHand();
                CompoundTag tag = item.getNamedTag();
                if (tag != null) {
                    MagicItem.instance.getLogger().info(item.getNamespaceId() + " *" + item.getCount());
                    MagicItem.instance.getLogger().info(tag.toString());
                }

//                String itemName = args[1];
//                Item item6 = ((Player) sender).getInventory().getItemInHand();
//                if (item6 != null) {
//                    saveItem(item6, itemName);
//                    log.addSuccess("§a保存成功！").output();
//                    return true;
//                } else {
//                    log.addError("§6手上没有物品哦！").output();
//                    return false;
//                }
                return true;
            }
            case "show" -> {
                if (!sender.isPlayer()) {
                    sender.sendMessage(TextFormat.RED + "仅限玩家执行");
                    return false;
                }
                Player player = (Player) sender;

                long time = System.currentTimeMillis();
                Config mainConfig = MagicItem.getInstance().getMainConfig();
                Item showItem = player.getInventory().getItemInHand();
                if (!showItem.hasCompoundTag()) {
                    Server.getInstance().getOnlinePlayers().values().forEach(p -> {
                        p.sendMessage(MagicItem.getI18n().tr(p.getLanguageCode(), "magicitem.usage.showItem", player.getName(), showItem.getName()));
                    });
                    return true;
                }
                if (!this.useTime.containsKey(player.getName())) {
                    this.useTime.put(player.getName(), time);
                } else if ((time - this.useTime.get(player.getName())) / 1000 < ((long) mainConfig.getInt("ItemDisplayCooldown"))) {
                    player.sendMessage(MagicItem.getI18n().tr(
                            player.getLanguageCode(),
                            "magicitem.usage.showItem.cooldown",
                            ((long) mainConfig.getInt("ItemDisplayCooldown")) - ((time - this.useTime.get(player.getName())) / 1000)
                    ));
                    return false;
                } else {
                    this.useTime.put(player.getName(), time);
                }
                Server.getInstance().getOnlinePlayers().values().forEach(p -> {
                    String itemName = showItem.getCustomName().isEmpty() ? showItem.getName() : showItem.getCustomName();
                    if (showItem.getId() == Item.WRITTEN_BOOK) {
                        itemName = showItem.getNamedTag().getString("title");
                    }
                    if (showItem.getCount() > 1) {
                        itemName += "§r§f *" + showItem.getCount();
                    }
                    p.sendMessage(MagicItem.getI18n().tr(p.getLanguageCode(), "magicitem.usage.showItem", player.getName(), itemName));
                });
                return true;
            }
            case "sell" -> {
                if (!sender.isPlayer()) {
                    sender.sendMessage(TextFormat.RED + "仅限玩家执行");
                    return false;
                }
                Player player = (Player) sender;
                PlayerInventory bag = player.getInventory();
                double total = 0.0d;
                for (int num = 0; num < bag.getSize(); num++) {
                    Item bagItem = bag.getItem(num);
                    if (!bagItem.hasCompoundTag()) continue;
                    String yamlName = bagItem.getNamedTag().getString("yamlName");
                    if (yamlName.isEmpty()) continue;
                    if (!MagicItem.getItemsMap().containsKey(yamlName)) continue;
                    double sell = bagItem.getNamedTag().getDouble("sell");
                    if (sell != 0.0d) {
                        bag.removeItem(bagItem);
                        EconomyAPI.getInstance().addMoney(player, ((double) bagItem.count) * sell);
                        total += ((double) bagItem.count) * sell;
                    }
                }
                player.sendMessage("§a回收完毕，获得 " + total + "元");
                return true;
            }
            case "reload" -> {
                MagicItem.instance.loadItems();
                MagicItem.instance.initOther();
                sender.sendMessage(TextFormat.GREEN + "配置文件已重新读取");
                Server.getInstance().getOnlinePlayers().values().forEach(MagicItem::updateItem);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * 创建物品
     *
     * @param itemBean
     * @param qualityIndex 这里是品质的索引从 0 开始
     * @return
     */
    public static Item createItem(ItemBean itemBean, int qualityIndex) {

        Item item = Item.fromString(itemBean.getItemId());

        if (item.getId() == Item.AIR_ITEM.getId()) {
            MagicItem.getInstance().getLogger().error("出问题的是:" + itemBean.getYamlName() + "\n无法获取物品！ID为：" + itemBean.getItemId());
            return Item.AIR_ITEM;
        }

        item.setDamage(itemBean.getItemMeta());

        CompoundTag tag = new CompoundTag();
        tag.putCompound("display", new CompoundTag("display").putString("Name", itemBean.getName()));
        tag.putString("yamlName", itemBean.getYamlName());

        ListTag<StringTag> effectListTag = new ListTag<>();
        for (String value : itemBean.getEffect()) {
            effectListTag.add(new StringTag("", value));
        }
        tag.putList("effect", effectListTag);

        ListTag<StringTag> groupEffectListTag = new ListTag<>();
        for (String value : itemBean.getGroupEffect()) {
            groupEffectListTag.add(new StringTag("", value));
        }

        tag.putList("groupEffect", groupEffectListTag);
        tag.putInt("distance", itemBean.getDistance());
        tag.putInt("actionEntity", itemBean.getActionEntity());
        tag.putBoolean("thunder", itemBean.isThunder());

        // 将 List<String> 转换为 ListTag<StringTag>
        ListTag<StringTag> opCmdListTag = new ListTag<>();
        for (String cmd : itemBean.getOpCmd()) {
            opCmdListTag.add(new StringTag("", cmd));
        }

        ListTag<StringTag> pCmdListTag = new ListTag<>();
        for (String cmd : itemBean.getPlayerCmd()) {
            pCmdListTag.add(new StringTag("", cmd));
        }

        // 将 ListTag 放入 tag
        tag.putList("opCmd", opCmdListTag);
        tag.putList("pCmd", pCmdListTag);

        tag.putString("getHints", itemBean.getGetHints());
        tag.putBoolean("isCon", itemBean.isCon());
        tag.putString("job", itemBean.getJob());
        tag.putInt("coolTime", itemBean.getCoolTime());
        tag.putString("broadCast", itemBean.getBroadCast());
        tag.putDouble("sell", itemBean.getSell());

        item.setNamedTag(tag);

        List<String> loreList = new ArrayList<>(itemBean.getLore());

        if (!itemBean.attr.isEmpty()) {
            tag.putInt("quality", qualityIndex);
            for (int i = 0; i < loreList.size(); i++) {
                String v = loreList.get(i);
                if (v.contains("@quality")) {
                    loreList.set(i, v.replaceFirst("@quality", MagicItem.getInstance().getMainConfig().getStringList("quality.list").get(qualityIndex)));
                    break;
                }
            }

            if (!hasRcRPG) {
                MagicItem.getInstance().getLogger().error("出问题的是:" + itemBean.getYamlName() + "\n无法进行属性解析，前置插件 RcRPG 未安装，请先安装插件。");
                return Item.AIR_ITEM;
            }

            float multiple = MagicItem.getInstance().getMainConfig().getFloatList("quality.m").get(qualityIndex);
            MagicItemAttr magicItemAttr = new MagicItemAttr(itemBean.attr, multiple);
            for (int i = 0; i < loreList.size(); i++) {
                String v = loreList.get(i);
                if (v.contains("{{")) {
                    loreList.set(i, magicItemAttr.replaceAttrTemplate(v));
                }
            }
            tag.putCompound("attr", magicItemAttr.getCompound());
        }

        if (!itemBean.mana.isEmpty()) {
            MagicItemMana magicItemMana = new MagicItemMana(itemBean.mana);
            for (int i = 0; i < loreList.size(); i++) {
                String v = loreList.get(i);
                if (v.contains("{(")) {
                    loreList.set(i, magicItemMana.replaceAttrTemplate(v));
                }
            }

            tag.putCompound("mana", magicItemMana.getCompound());
        }

        item.setNamedTag(tag);

        item.setLore(String.join("\n", loreList));
        if (!itemBean.getEnch().isEmpty()) {
            for (Enchantment enchantment : Tools.getEnchant(itemBean.getEnch())) {
                item.addEnchantment(enchantment);
            }
        }
        return item;
    }

    public void sendCommandHelp(CommandSender sender) {
        LangCode langCode = sender.isPlayer() ? ((Player) sender).getLanguageCode() : LangCode.zh_CN;
        sender.sendMessage(i18n.tr(langCode, "magicitem.commands.help"));
        if (sender.isOp()) {
            sender.sendMessage(i18n.tr(langCode, "magicitem.commands.add.help"));
            sender.sendMessage(i18n.tr(langCode, "magicitem.commands.give.help"));
            sender.sendMessage(i18n.tr(langCode, "magicitem.commands.reload.help"));
        }
        sender.sendMessage(i18n.tr(langCode, "magicitem.commands.show.help"));
        sender.sendMessage(i18n.tr(langCode, "magicitem.commands.sell.help"));
    }

    public static Item createItem(ItemBean itemBean) {
        if (!itemBean.attr.isEmpty()) {
            return createItem(itemBean, selectQuality());
        }
        return createItem(itemBean, 0);
    }

    /**
     * 选择品质
     *
     * @return 正整数 0:劣质 1:普通 2:优质 3:完美 4:传说
     */
    private static int selectQuality() {
        Config c = MagicItem.getInstance().getMainConfig();
        List<Double> probabilityList = c.getDoubleList("quality.p");

        // 随机生成一个0到1之间的随机数
        double randomValue = new Random().nextDouble();

        double cumulativeProbability = 0.0;
        for (int i = 0; i < probabilityList.size(); i++) {
            cumulativeProbability += probabilityList.get(i);

            if (randomValue < cumulativeProbability) {
                return i;
            }
        }

        // 如果没有匹配的品质，则返回默认品质或者处理其他逻辑
        return 0;
    }

    private Item createSaveItems(String msg) {
        String[] args = msg.split(":");
        Item item = Item.get(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
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

    private void removeItem(Player player, List<Item> items, List<String> cmds, double money, String[] tipMegs) {
        int i = 0;
        Inventory inventory = player.getInventory();
        List<String> needItemList = new ArrayList<>();
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
                inventory.removeItem(item);
            }
            player.sendMessage(tipMegs[1].isEmpty() ? "§a=== 合成成功 ===" : tipMegs[1]);
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
        player.sendMessage((tipMegs[0].isEmpty() ? "§c缺少材料：" : tipMegs[0]) + needItems);
    }

    private void removeItemToForging(Player player, List<Item> items, ConfigSection msgs, double money, String[] tipMegs) {
        int i = 0;
        List<Award> awards = new ArrayList<>();
        List<String> needItemList = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();
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
        if (i == items.size()) {
            for (Item item : items) {
                inventory.removeItem(item);
            }
            player.sendMessage(tipMegs[1].isEmpty() ? "§a=== 锻造成功 ===" : tipMegs[1]);
            Map<String, Object> cmdMap = msgs.getAllMap();
            for (Map.Entry<String, Object> entry : cmdMap.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> stringArray = (ArrayList<String>) entry.getValue();
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

        if (money > 0) {
            needItemList.add("§6金币 §r*" + money);
        }
        final String needItems = String.join("、", needItemList);
        player.sendMessage(tipMegs[0].isEmpty() ? "§c缺少材料：" + needItems : tipMegs[0].replace("%need%", needItems));
    }

    private void runCommand(Player player, String cmd) {
        MagicItem.getInstance().getServer().dispatchCommand(new ConsoleCommandSender(), cmd.replace("{player}", player.getName()));
    }

    private void sendNbtItem(String str, String name, int count) {
        Player player = MagicItem.getInstance().getServer().getPlayer(str);
        if (player != null) {
            Item item = createSaveItems((String) MagicItem.getOthers().get(name));
            item.setCount(count);
            player.getInventory().addItem(item);
            player.sendMessage("§e>> §a恭喜获得 §5" + name);
        }
    }
}
