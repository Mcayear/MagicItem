package cn.ankele.plugin.utils;

import cn.ankele.plugin.bean.Award;
import cn.nukkit.item.enchantment.Enchantment;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tools {
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.isEmpty()) {
            return null;
        }
        String hexString2 = hexString.toUpperCase();
        int length = hexString2.length() / 2;
        char[] hexChars = hexString2.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) ((charToByte(hexChars[pos]) << 4) | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length == 0) {
            return null;
        }
        for (byte aSrc : src) {
            String hv = Integer.toHexString(aSrc & 255);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static boolean isExits(File dir, String name) {
        File[] listFiles = dir.listFiles();
        if (listFiles == null) {
            return false;
        }
        for (File listFile : listFiles) {
            if (listFile.getName().equals(name + ".yml")) {
                return true;
            }
        }
        return false;
    }

    public static List<Enchantment> getEnchant(List<String> ench) {
        List<Enchantment> list = new ArrayList<>();
        for (String enchantment : ench) {
            String[] s = enchantment.split(":");
            list.add(Enchantment.getEnchantment(Integer.parseInt(s[0])).setLevel(Integer.parseInt(s[1])));
        }
        return list;
    }

    public static Award lottery(List<Award> awards) {
        float totalPro = 0.0f;
        List<Float> proSection = new ArrayList<>();
        proSection.add(Float.valueOf(0.0f));
        for (Award award : awards) {
            totalPro += award.probability * 100.0f;
            proSection.add(Float.valueOf(totalPro));
        }
        float randomPro = (float) new Random().nextInt((int) totalPro);
        int size = proSection.size();
        for (int i = 0; i < size; i++) {
            if (randomPro >= proSection.get(i).floatValue() && randomPro < proSection.get(i + 1).floatValue()) {
                return awards.get(i);
            }
        }
        return null;
    }
}
