package com.algo;

import com.algo.algorithms.OptionsEnum;

import java.util.Scanner;

public abstract class Runner {
    public abstract boolean run(Scanner sc) throws Exception;

    public <T extends OptionsEnum> T getInput(Scanner sc, OptionsEnum[] type) {
        System.out.println("Please select one of the following options:");
        printOptions(type);
        return parseInput(sc, type);
    }


    public <T extends OptionsEnum> T parseInput(Scanner sc, OptionsEnum[] type) {
        OptionsEnum input;
        try {
            input = type[getIntegerFromConsole(sc)];
        } catch (Exception exception) {
            System.out.println("Invalid Input.\n Please try again.");
            return null;
        }
        return (T) input;
    }

    protected void printOptions(OptionsEnum[] type) {
        for (OptionsEnum option : type) {
            System.out.println(option.getOrdinal() + " " + option.getText());
        }
    }

    protected Integer getIntegerFromConsole(Scanner sc) {
        return Integer.valueOf(sc.nextLine().trim());
    }
}
