package cn.ankele.plugin.bean;

import cn.ankele.plugin.MagicItem;
import cn.nukkit.utils.Config;

import java.util.Map;
import java.util.List;
import java.util.Random;

public class ItemBean {
    private int actionEntity;
    private String broadCast;
    private Config config;
    private int coolTime;
    private int distance;
    private String effect;
    private String ench;
    private String getHints;
    private String groupEffect;
    private boolean isCon;
    private String itemId;
    private int itemDamage;
    private String job;
    private String lore;
    private String name;
    private String opCmd;
    private String pCmd;
    private MagicItem plugin;
    private double sell;
    public Map<String, Object> attr;
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
        this.itemDamage = this.config.getInt("物品Damage", 0);
        this.effect = this.config.getString("获得药水");
        this.groupEffect = this.config.getString("群体药水");
        this.thunder = this.config.getBoolean("使用雷击", false);
        this.opCmd = this.config.getString("OP指令");
        this.pCmd = this.config.getString("以玩家身份执行");
        this.lore = this.config.getString("显示");
        this.isCon = this.config.getBoolean("使用消耗", false);
        this.coolTime = this.config.getInt("冷却时间", 0);
        this.getHints = this.config.getString("获得提示");
        this.broadCast = this.config.getString("全服提示");
        this.distance = this.config.getInt("药水范围");
        this.ench = this.config.getString("附魔");
        this.job = this.config.getString("职业限制");
        this.sell = this.config.getDouble("回收价格", 0);
        this.attr = this.config.getSection("属性").getAllMap();
    }
    public String getYamlName() {
        return this.yamlName;
    }

    public void setYamlName(String yamlName2) {
        this.yamlName = yamlName2;
    }

    public MagicItem getPlugin() {
        return this.plugin;
    }

    public void setPlugin(MagicItem plugin2) {
        this.plugin = plugin2;
    }

    public Config getConfig() {
        return this.config;
    }

    public void setConfig(Config config2) {
        this.config = config2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getItemId() {
        return this.itemId;
    }
    public int getItemDamage() {
        return this.itemDamage;
    }

    public void setItemId(String itemId2) {
        this.itemId = itemId2;
    }

    public String getEffect() {
        return this.effect;
    }

    public void setEffect(String effect2) {
        this.effect = effect2;
    }

    public String getGroupEffect() {
        return this.groupEffect;
    }

    public void setGroupEffect(String groupEffect2) {
        this.groupEffect = groupEffect2;
    }

    public boolean isThunder() {
        return this.thunder;
    }

    public void setThunder(boolean thunder2) {
        this.thunder = thunder2;
    }

    public String getOpCmd() {
        return this.opCmd;
    }

    public void setOpCmd(String opCmd2) {
        this.opCmd = opCmd2;
    }

    public String getpCmd() {
        return this.pCmd;
    }

    public void setpCmd(String pCmd2) {
        this.pCmd = pCmd2;
    }

    public String getLore() {
        return this.lore;
    }

    public void setLore(String lore2) {
        this.lore = lore2;
    }

    public boolean isCon() {
        return this.isCon;
    }

    public void setCon(boolean con) {
        this.isCon = con;
    }

    public int getCoolTime() {
        return this.coolTime;
    }

    public void setCoolTime(int coolTime2) {
        this.coolTime = coolTime2;
    }

    public String getGetHints() {
        return this.getHints;
    }

    public void setGetHints(String getHints2) {
        this.getHints = getHints2;
    }

    public String getBroadCast() {
        return this.broadCast;
    }

    public void setBroadCast(String broadCast2) {
        this.broadCast = broadCast2;
    }

    public int getDistance() {
        return this.distance;
    }

    public void setDistance(int distance2) {
        this.distance = distance2;
    }

    public String getEnch() {
        return this.ench;
    }

    public void setEnch(String ench2) {
        this.ench = ench2;
    }

    public int getActionEntity() {
        return this.actionEntity;
    }

    public void setActionEntity(int actionEntity2) {
        this.actionEntity = actionEntity2;
    }

    public String getJob() {
        return this.job;
    }

    public void setJob(String job2) {
        this.job = job2;
    }

    public double getSell() {
        return this.sell;
    }

    public void setSell(double sell2) {
        this.sell = sell2;
    }
}
