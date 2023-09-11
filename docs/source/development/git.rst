Git
===

This page documents how we should use Git in our workflow.

Before Committing
--------------------

Before you commit, you should run ``./gradlew spotlessApply`` (or double tap CTRL for "Run Anything" and run ``gradle spotlessApply`` inside IntelliJ).
This will autoformat your code.
This is required for your code to be merged into branch ``main``, as CI/CD will run against the changes you pushed.

You are allowed to commit and push changes that are not autoformatted,
but you will have to autoformat the changes before attempting to merge your code into ``main`` (via a pull request).

Branching
------------

No one should commit directly to the ``main`` branch.
You should name your branch so that it's clear what ticket or feature it relates to.
Bonus points if you decide to put your name in the branch name.

Committing
-------------

Committing is pretty standard.
Please make sure that you correctly have your name and email set.
(Run these commands to verify)

.. code-block:: shell

  git config user.name
  git config user.email

Your name should be your full name, not your username.
Your email can be any email you want.
If you would like, you can set it to ``GITHUB_USERNAME_HERE@users.noreply.github.com`` so that you don't expose your email publicly.
More info about `commit email address <https://docs.github.com/en/account-and-profile/setting-up-and-managing-your-personal-account-on-github/managing-email-preferences/setting-your-commit-email-address>`_ there.

Assuming that's setup correctly, committing your changes can be done via the command line or via IntelliJ's Git GUI.
Use whichever one you prefer.
Assuming you have everything setup correctly, you can push your changes.

Pull Requests
--------------

Pull requests (sometimes called merge requests) is a feature available on GitHub so that we can review the changes before merging them.
Typically you will make a pull request that requests to merge changes from your feature branch into the ``main`` branch.
