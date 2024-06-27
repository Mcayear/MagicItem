package cn.ankele.plugin.bean;

import RcRPG.AttrManager.ItemAttr;
import cn.ankele.plugin.MagicItem;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MagicItemAttr extends ItemAttr {
    public MagicItemAttr(Map<String, Object> newAttr, float multiple) {
        Map<String, float[]> attrMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : newAttr.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof List<?> values) {
                float[] floatValues = new float[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i) instanceof Double) {
                        floatValues[i] = ((Double) values.get(i)).floatValue() * multiple;
                    } else if (values.get(i) instanceof Integer) {
                        floatValues[i] = ((Integer) values.get(i)).floatValue() * multiple;
                    }
                }
                attrMap.put(key, floatValues);
            } else if (value instanceof float[] floatValue){
                if (floatValue.length == 1) {
                    float[] newValue = { floatValue[0] * multiple, floatValue[0] * multiple };
                    attrMap.put(key, newValue);
                } else {
                    attrMap.put(key, new float[]{ floatValue[0] * multiple, floatValue[1] * multiple });
                }
            } else {
                MagicItem.getInstance().getLogger().warning(key + " MagicItemAttr 中不知道是啥类型");
            }
        }
        mainAttr = attrMap;
    }

    public MagicItemAttr(Map<String, Object> newAttr) {
        this(newAttr, 1);
    }

    public CompoundTag getCompound() {
        CompoundTag compoundTag = new CompoundTag();
        for (String attrName : this.getMainAttr().keySet()) {
            ListTag<FloatTag> modifiedValues = new ListTag<>();
            for (float value : this.getMainAttr().get(attrName)) {
                modifiedValues.add(new FloatTag(value));
            }
            compoundTag.putList(attrName, modifiedValues);
        }
        return compoundTag;
    }
}
