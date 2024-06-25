package cn.ankele.plugin;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;

// 弃用
public class UpdateTask extends PluginTask<MagicItem> {
    public UpdateTask(MagicItem owner) {
        super(owner);
    }

    public void onRun(int i) {
        for (Player player : MagicItem.getInstance().getServer().getOnlinePlayers().values()) {
            MagicItem.updateItem(player);
        }
    }
}
