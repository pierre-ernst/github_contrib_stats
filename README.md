# github_contrib_stats

## Description
A small CLI tool relying on GitHub API to gather statistics on a given GitHub repo: 
* Recent committers
* employee Vs. non-employee
* source code Vs. other file types

A *recent* committer is a GitHub user who committed at least one file within the last 90 days.
The number of days can be changed in the [configuration file](src/main/resources/config.json).

An *employee* is a GitHub user who belongs ([publicly or privately](https://developer.github.com/v3/orgs/members/#check-membership)) to the given organization.

A file is considered *source code* when its extension belongs to a [configurable list](src/main/resources/config.json) of programming language file extensions

## Installation
`$ mvn install`

## Usage
`$ java -jar target/github_contrib_stats.jar <org> <repo>`

e.g.:

```
$ java -jar target/github_contrib_stats.jar elastic elasticsearch
$ Enter your PAT (GitHub Personal Access Token): baba1234567890baba1234567890baba12345678
```

## Output Example
```
Total number of active   contributors for elastic/elasticsearch: 20
Total number of rejected contributors for elastic/elasticsearch: 3
20% of committed file rejected
--- Active Contributors ---
pierre-ernst (Pierre Ernst) 
jjjddd (Jane Doe) , Sydney, Australia 
  ...
--- Rejected Contributors ---
weird-name (weird-name) , Paris 
anonymousdude (anonymousdude) , Sydney, NS 
  ...

```

