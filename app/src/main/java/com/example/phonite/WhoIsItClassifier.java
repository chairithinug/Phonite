package com.example.phonite;

import java.util.ArrayList;
import java.util.Random;

public class WhoIsItClassifier {

//    public static void main() {
//        ArrayList<Instance> trainingSet = new ArrayList<>();
//        Random random = new Random(0);
//        Double[][] hiddenWeights = new Double[Integer.parseInt(args[0])][];
//        for (int i = 0; i < hiddenWeights.length; i++) {
//            hiddenWeights[i] = new Double[trainingSet.get(0).attributes.size() + 1];
//        }
//
//        Double[][] outputWeights = new Double[trainingSet.get(0).classValues.size()][];
//        for (int i = 0; i < outputWeights.length; i++) {
//            outputWeights[i] = new Double[hiddenWeights.length + 1];
//        }
//
//        generateWeights(hiddenWeights, outputWeights, random);
//
//        Double learningRate = Double.parseDouble(args[1]);
//
//        if (learningRate > 1 || learningRate <= 0) {
//            System.out.println("Incorrect value for learning rate\n");
//            System.exit(-1);
//        }
//
//        NNImpl nn = new NNImpl(trainingSet, Integer.parseInt(args[0]), Double.parseDouble(args[1]),
//                Integer.parseInt(args[2]), random, hiddenWeights, outputWeights);
//        nn.train();
//    }


    private static void generateWeights(Double[][] hiddenWeights, Double[][] outputWeights, Random r) {
        for (int i = 0; i < hiddenWeights.length; i++)
            for (int j = 0; j < hiddenWeights[i].length; j++)
                hiddenWeights[i][j] = r.nextGaussian() * 0.01;

        for (int i = 0; i < outputWeights.length; i++)
            for (int j = 0; j < outputWeights[i].length; j++)
                outputWeights[i][j] = r.nextGaussian() * 0.01;
    }
}
