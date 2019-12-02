package com.example.phonite;

import java.util.ArrayList;

public class Node {
    private int type = 0;  // 0=input,1=biasToHidden,2=hidden,3=biasToOutput,4=Output
    public ArrayList<NodeWeightPair> parents = null;

    private double input = 0.0;
    private double output = 0.0;

    public Node(int type) {
        if (type > 4 || type < 0) {
            System.exit(1);
        } else {
            this.type = type;
        }
        if (type == 2 || type == 4) {
            parents = new ArrayList<>();
        }
    }
}
