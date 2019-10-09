package com.github.pierre_ernst.github_contrib_stats;


public class Main {
    public static void main(String... args) {
        try {
            if (args.length != 2) {
                System.err.println("Usage: java " + Main.class.getName() + " <org> <repo>");
            } else {
                new ContributorCounter(args[0], args[1]).count(System.out);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
