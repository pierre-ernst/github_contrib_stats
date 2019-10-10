package com.github.pierre_ernst.github_contrib_stats;


import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;
import org.kohsuke.github.*;
import org.kohsuke.github.extras.OkHttp3Connector;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


public class ContributorCounter implements AutoCloseable {

    private String orgName;
    private String repoName;
    private Date deadline;
    private GitHub gitHub;
    private ConfigPOJO config;

    /**
     * @param pat  GitHub Personal Access Token
     * @param org  GitHub organization name
     * @param repo GitHub repository name
     * @throws IOException
     */
    public ContributorCounter(String pat, String org, String repo) throws IOException {
        orgName = org;
        repoName = repo;

        config = ConfigPOJO.getInstance();

        deadline = Date.from(LocalDate.now().minusDays(config.getTimeWindow()).atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());

        File cacheDir = Files.createTempDirectory("ContributorCounterCache").toFile();
        Cache cache = new Cache(cacheDir, 5 * 1024 * 1024); // 5MB cache

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        // TODO wait for the next release of github-api to fix the depreciation warning
        gitHub = new GitHubBuilder().withOAuthToken(pat)
                .withConnector(new OkHttp3Connector(new OkUrlFactory(client)))
                .withRateLimitHandler(RateLimitHandler.WAIT).build();

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

        GHOrganization org = null;
        try {
            org = gitHub.getOrganization(orgName);
        } catch (IOException ex) {
            throw new IOException("Unknown org " + orgName + ", or missing access", ex);
        }

        GHRepository repo = null;
        try {
            repo = org.getRepository(repoName);
            if (repo == null) {
                throw new IOException("Unknown repo " + repoName + ", or missing access");
            }
        } catch (IOException ex) {
            throw new IOException("Unknown repo " + repoName + ", or missing access", ex);
        }


        /**
         * The boolean value indicates if the given contributor belongs to the org
         */
        Map<GHUser, Boolean> contributors = new HashMap<>();

        // For all the commits in the given org/repo within the time window
        for (GHCommit commit : repo.queryCommits().since(deadline).list()) {

            // Only keep the members of the org
            boolean isMember = false;
            if (commit.getAuthor() != null) {
                if (contributors.containsKey(commit.getAuthor())) {
                    // Membership state is already cached
                    isMember = contributors.get(commit.getAuthor());
                } else {
                    // Get the answer from an API call
                    isMember = commit.getAuthor().isMemberOf(org);
                    contributors.put(commit.getAuthor(), false);
                }
            }

            if (isMember) {

                // For all the files part of this commit
                for (GHCommit.File file : commit.getFiles()) {

                    // Only keep source-code file
                    if (config.getLanguages().contains(getFileType(file.getFileName()))) {
                        contributors.put(commit.getAuthor(), true);
                    }
                }
            }
        }

        int activeCount   = displayContribList(true, contributors, "Active Contributors", out);
        int inactiveCount = displayContribList(false, contributors, "Inactive Contributors", out);

        out.println();
        out.println("Total number of active   contributors for " + orgName + "/" + repoName + ": " + activeCount);
        out.println("Total number of inactive contributors for " + orgName + "/" + repoName + ": " + inactiveCount);
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

    private static int displayContribList(boolean isMember, Map<GHUser, Boolean> list, String title, PrintStream out) throws IOException {
        int count = 0;
        out.println("--- " + title + " ---");
        for (GHUser contributor : list.keySet()) {
            if (list.get(contributor) == isMember) {
                out.println(toString(contributor));
                count++;
            }
        }
        return count;
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

    @Override
    public void close() throws Exception {
        // TODO it appears to be no way to close/disconnect the API client
    }
}
