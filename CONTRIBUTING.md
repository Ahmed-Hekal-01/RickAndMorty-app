# Team Git & GitHub Workflow Guide

Welcome to the official workflow guide for our DEPI graduation project! To ensure our Rick and Morty app codebase remains stable and to demonstrate professional software engineering practices to our instructor, we are using a **Protected Branch Workflow**.

This means **direct pushes to our main branches are strictly forbidden.** All code must be written on isolated branches, reviewed by a teammate, and merged via Pull Requests (PRs).

---

## 1. Our Branching Strategy

We use a structured branching model to keep our work organized and safe.

* **`main`**: The production-ready branch. This should always contain stable, compilable, and tested code.
* **`develop`**: The active integration branch. All completed features are merged here first.
* **`feature/*`**: Temporary branches where actual coding happens (e.g., `feature/character-list-ui`).
* **`bugfix/*`**: Branches dedicated to fixing specific bugs (e.g., `bugfix/api-crash-fix`).
* **`chore/*`**: Branches for configuration, updating dependencies, or modifying templates.

---

## 2. The Daily Development Cycle

Follow these exact steps every time you pick up a new task.

### Step 2.1: Always Sync First
Before writing any Kotlin code or XML layouts, ensure your local machine is up-to-date with the rest of the team.

```bash
# Switch to the integration branch
git checkout develop

# Pull the latest approved code
git pull origin develop
```

### Step 2.2: Create a Feature Branch
Create an isolated branch for your specific task. Name it descriptively.

```bash
# Create and switch to your new branch
git checkout -b feature/rick-and-morty-api-client
```

### Step 2.3: Write Code & Commit
Do your development work. When you are ready to save a logical chunk of work, use **Conventional Commits** to keep our history readable.

* `feat:` for a new feature.
* `fix:` for a bug fix.
* `refactor:` for rewriting code without changing its behavior.
* `chore:` for setup or configuration changes.

```bash
# Stage your modified files
git add app/src/main/java/com/depi/project/api/RickAndMortyApi.kt

# Commit with a clear, structured message
git commit -m "feat: setup Retrofit client for the Rick and Morty API"
```

### Step 2.4: Push Your Branch
Send your local branch up to the remote GitHub repository.

```bash
git push -u origin feature/rick-and-morty-api-client
```

---

## 3. Opening a Pull Request (PR)

Because our repository is protected, you cannot merge your branch directly. You must open a PR.

1. Navigate to our repository on GitHub.
2. Click the green **Compare & pull request** button next to your recently pushed branch.
3. **Crucial:** Set the base branch to **`develop`** (NOT `main`).
4. Fill out the PR description using the template checklist automatically provided in the text box.
5. On the right-hand sidebar, click **Reviewers** and tag at least one teammate.
6. Click **Create pull request**.

> **Note:** You will see a red message saying "Merging is blocked." This is normal! GitHub is waiting for a teammate to approve your code.

---

## 4. Reviewing and Approving Code

You cannot approve your own PR. When a teammate tags you for a code review, it is your responsibility to check their work.

1. Open the Pull Request on GitHub.
2. Click the **Files changed** tab at the top.
3. Review the code. Look for logic errors, formatting issues, or potential crashes.
4. Click the green **Review changes** button in the top right corner.
5. If everything looks good, select **Approve**. (If changes are needed, select *Request changes* and leave a comment explaining what needs fixing).
6. Click **Submit review**.

Once **1 approval** is registered, the red blocked message will disappear, and the PR author can click the green **Merge pull request** button to integrate their code into `develop`.

---

## 5. Best Practices & Troubleshooting

* **Pull Frequently:** To avoid massive merge conflicts, run `git pull origin develop` frequently, especially before starting a new branch.
* **Keep Branches Small:** Do not build the entire app on one branch. Break tasks down (e.g., one branch for the API call, another branch for the UI).
* **Do Not Commit Secrets:** Never commit files containing sensitive data or local IDE configurations (our `.gitignore` should handle this, but always double-check your `git status` before committing).
