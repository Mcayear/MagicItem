package cn.ankele.plugin;

import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;

public class UpDateTask extends Task {
    public void onRun(int i) {
        for (Player player : MagicItem.getInstance().getServer().getOnlinePlayers().values()) {
            MagicItem.updateItem(player);
        }
    }
}
