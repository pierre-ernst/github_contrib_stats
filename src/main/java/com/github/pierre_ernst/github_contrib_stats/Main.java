package com.github.pierre_ernst.github_contrib_stats;


import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String... args) {
        if (args.length != 2) {
            System.err.println("Usage: java " + Main.class.getName() + " <org> <repo>");
        } else {
            System.out.print("Enter your PAT (GitHub Personal Access Token): ");
            String pat = new Scanner(System.in).nextLine();
            System.out.println();
            try (ContributorCounter cc = new ContributorCounter(pat, args[0], args[1])) {
                cc.count(System.out);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}
