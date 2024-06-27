package cn.ankele.plugin.bean;

import cn.ankele.plugin.MagicItem;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class ItemBean {
    private int actionEntity;
    private String broadCast;
    private Config config;
    private int coolTime;
    private int distance;
    /**
     * 获得药水
     */
    private List<String> effect;
    /**
     * 附魔
     */
    private List<String> ench;
    private String getHints;
    /**
     * 群体药水
     */
    private List<String> groupEffect;
    private boolean isCon;
    private String itemId;
    private int itemMeta;
    private String job;
    private List<String> lore;
    private String name;
    private List<String> opCmd;
    private List<String> playerCmd;
    private MagicItem plugin;
    private double sell;
    public Map<String, Object> attr;
    public Map<String, Integer> mana;
    private boolean thunder;
    private String yamlName;

    public ItemBean(String yamlName2, Config config2) {
        this.yamlName = yamlName2;
        this.config = config2;
        init();
    }

    private void init() {
        this.name = this.config.getString("名字");
        this.itemId = this.config.getString("物品ID");
        this.itemMeta = this.config.getInt("物品Meta", 0);
        this.effect = this.config.getStringList("获得药水");
        this.groupEffect = this.config.getStringList("群体药水");
        this.thunder = this.config.getBoolean("使用雷击", false);
        this.opCmd = this.config.getStringList("OP指令");
        this.playerCmd = this.config.getStringList("以玩家身份执行");
        this.lore = this.config.getStringList("显示");
        this.isCon = this.config.getBoolean("使用消耗", false);
        this.coolTime = this.config.getInt("冷却时间", 0);
        this.getHints = this.config.getString("获得提示");
        this.broadCast = this.config.getString("全服提示");
        this.distance = this.config.getInt("药水范围");
        this.ench = this.config.getStringList("附魔");
        this.job = this.config.getString("职业限制");
        this.sell = this.config.getDouble("回收价格", 0);
        this.attr = this.config.getSection("属性").getAllMap();

        this.mana = MagicItemMana.readConfig(this.name, this.config.getSection("魔素").getAllMap());

    }

}
