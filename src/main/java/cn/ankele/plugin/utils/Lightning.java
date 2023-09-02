package cn.ankele.plugin.utils;

import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class Lightning extends EntityLightning {
    public Lightning(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.fireProof = true;
        this.fireTicks = 0;
        this.maxFireTicks = 0;
    }
}
