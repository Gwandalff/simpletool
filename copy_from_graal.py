#!/usr/bin/env python
# -*- coding: utf-8 -*-

# Since Simple Tool is not developed on this repository, we need to periodically sync this repository with the developement one.
# This scipt automates cloning the source repository and copying the ST code from there to here.
# Requires git to be installed.

import os
import sys
from subprocess import call

GRAAL_REPO = "https://github.com/oracle/graal"
GRAAL_DIR = "../" + GRAAL_REPO.split('/')[-1]

def fail(message):
    """ Print message to stderr and exit script

    """
    print >> sys.stderr, message
    sys.exit(1)

def clone(repo, path = ""):
    """ Clones the given repo url using git

    :repo: String containing the url

    """
    if call(["git", "clone", repo, path]) != 0:
        fail("Could not clone " + repo)
    pass

def checkout(path, commit, create = False):
    """ Checks out a new branch in SL

    :cwd: path to the git repo
    :commit: String, name for the new branch
    :create: create new or expect it to exist

    """
    command = ["git", "checkout"]
    if create:
        command.append("-b")
    command.append(commit)
    if call(command, cwd=path) != 0:
        fail("Could not checkout " + commit + " from " + path)

def replace(source, dest):
    """ Replace contents of dest dir with contents of source dir

    :source: String path to source
    :dest: String path do destination

    """
    call(["rm", "-rf", dest])
    call(["mkdir", "-p", dest])
    call(["cp", "-RTf", source, dest])

def copy_st():
    """ Copies ST from graal to simpletool

    """
    replace(GRAAL_DIR + "/truffle/src/com.oracle.truffle.st/src/com"         , "src/main/java/com")
    replace(GRAAL_DIR + "/truffle/src/com.oracle.truffle.st.test/src/com"    , "src/test/java/com")

def update_st(revision):
    """ Updates the SL repo from the graal repo given a revision

    :revision: the hash of the commit in the graal repo to be used

    """
    checkout(".", "st_update_"+revision, True)
    if os.path.isdir(GRAAL_DIR):
        call(['git', 'fetch'], cwd=GRAAL_DIR)
    else:
        clone(GRAAL_REPO, GRAAL_DIR)
    checkout(GRAAL_DIR, revision)
    copy_st()
    print ""
    print "NOTE: Update the version in st, README.md and all pom.xml files!"
    print "NOTE: Follow the instructions in README.md and make sure mvn package executes correctly!"
    print "NOTE: Make sure project open correctly on the supported IDEs!"
    print ""

if __name__ == "__main__":
    if (len(sys.argv) != 2):
        fail("usage: " + sys.argv[0] + " idOfGraalCommitToUseAsBase")
    update_st(sys.argv[1])
