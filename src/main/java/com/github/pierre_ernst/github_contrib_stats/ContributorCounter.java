package com.github.pierre_ernst.github_contrib_stats;

import org.kohsuke.github.*;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class ContributorCounter {

    /**
     * The definition of <i>active</i> contributors:
     * <u>e.g.</u> active contributors are GitHub users who committed files within the last <b>90</b> days
     */
    private static final int TIME_WINDOW_DAYS = 90;

    private String orgName;
    private String repoName;
    private Date deadline;

    public ContributorCounter(String org, String repo) {
        this.orgName = org;
        this.repoName = repo;
        deadline = Date.from(LocalDate.now().minusDays(TIME_WINDOW_DAYS).atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    /**
     * The main algorithm
     * <p>
     * All the commits within the last <code>TIME_WINDOW_DAYS</code> are examined:
     * <ul>
     *     <li>Are the committed files really source code as per <code>SupportedLanguages</code></li>
     *     <li>Are the committers members of the given GitHub org? (in other words: <i>are they on the payroll?</i>)</li>
     * </ul>
     *
     * @param out used to format results
     * @throws IOException
     */
    public void count(PrintStream out) throws IOException {
        GitHub github = GitHub.connect();

        GHOrganization org = github.getOrganization(orgName);
        GHRepository repo = org.getRepository(repoName);

        SupportedLanguages supportedLanguages = SupportedLanguages.getInstance();

        Set<GHUser> activeContributors = new HashSet<>();
        Set<GHUser> rejectedContributors = new HashSet<>();
        long acceptedFileCounter = 0;
        long rejectedFileCounter = 0;

        for (GHCommit commit : repo.queryCommits().since(deadline).list()) {
            for (GHCommit.File file : commit.getFiles()) {
                if (supportedLanguages.contains(getFileType(file.getFileName()))) {
                    acceptedFileCounter++;

                    if (commit.getAuthor().isMemberOf(org)) {
                        activeContributors.add(commit.getAuthor());
                    } else {
                        rejectedContributors.add(commit.getAuthor());
                    }

                } else {
                    rejectedFileCounter++;
                }
            }
        }

        out.println("Total number of active   contributors for " + orgName + "/" + repoName + ": " + activeContributors.size());
        out.println("Total number of rejected contributors for " + orgName + "/" + repoName + ": " + rejectedContributors.size());
        out.println((int) ((rejectedFileCounter * 100) / (rejectedFileCounter + acceptedFileCounter)) + "% of committed file rejected");

        displayContribList(activeContributors, "Active Contributors", out);
        displayContribList(rejectedContributors, "Rejected Contributors", out);
    }

    private static String toString(GHUser user) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(user.getLogin()).append(' ');
        if ((user.getName() != null) && (!user.getName().isEmpty())) {
            sb.append('(').append(user.getName()).append(") ");
        }
        if ((user.getLocation() != null) && (!user.getLocation().isEmpty())) {
            sb.append(", ").append(user.getLocation()).append(' ');
        }

        return sb.toString();
    }

    private static void displayContribList(Collection<GHUser> list, String title, PrintStream out) throws IOException {
        out.println("--- " + title + " ---");
        for (GHUser contributor : list) {
            out.println(toString(contributor));
        }
    }

    /**
     * Returns the file type as per the file extension (approximation)
     *
     * @param path
     * @return
     **/
    private static String getFileType(String path) {
        return path.substring(path.indexOf('.', path.lastIndexOf('/')) + 1).toLowerCase();
    }
}
