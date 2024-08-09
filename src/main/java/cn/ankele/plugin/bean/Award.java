package cn.ankele.plugin.bean;

import java.util.ArrayList;

public class Award {
    public ArrayList<String> cmd;
    public float probability;

    public Award(ArrayList<String> cmd2, float probability2) {
        this.cmd = cmd2;
        this.probability = probability2;
    }
}
