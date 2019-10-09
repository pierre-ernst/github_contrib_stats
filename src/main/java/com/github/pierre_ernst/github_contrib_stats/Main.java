package com.github.pierre_ernst.github_contrib_stats;


import java.io.IOException;

public class Main {
    public static void main(String... args) {
        if (args.length != 3) {
            System.err.println("Usage: java " + Main.class.getName() + " <pat> <org> <repo>");
        } else {
            try (ContributorCounter cc = new ContributorCounter(args[0], args[1], args[2])) {
                cc.count(System.out);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}
