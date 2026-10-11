# CLAUDE.md

Instructions for any AI assistant working on this repo. Read this file, then **HANDOFF.md**, then the docs you need.

## Start of every session

1. Read `HANDOFF.md` first. It holds the current state and the next step.
2. Skim `docs/RULES.md`. Open `docs/SPEC.md`, `docs/ARCHITECTURE.md` and `docs/DECISIONS.md` as needed.
3. If this is a fresh sandbox, clone the repo: `git clone https://github.com/YatharthJorwal/Verity`
4. If `HANDOFF.md` looks stale compared to the code, say so and fix it before building on it.

## Project in one paragraph

Verity is a Fabric mod (Java, Minecraft 26.2) adding a small passive mob driven by a local Ollama model. The model picks actions from a fixed JSON set. Vanilla pathfinding carries them out. See `docs/SPEC.md`.

## How to work with the user

- **One step at a time.** Give a single step, then wait for the result before the next.
- **Deliver changes as merge bundles.** A zip laid out like the project root, which the user extracts over the project folder with overwrite. List which files are new and which are replaced. Do not ask the user to hand-edit files.
- **Git commands:** copy-pasteable, one command per code block, no inline comments. The user is still learning git.
- **Environment:** Windows, IntelliJ IDEA, Temurin JDK 25. Use PowerShell syntax (`.\gradlew.bat`).
- Ask for screenshots or pasted error text instead of guessing.

## Technical ground rules

- Minecraft 26.x is **not obfuscated**. Use Mojang names. No Yarn mappings, no `remapJar`.
- Class and method names changed a lot after 1.21. **Do not trust memory or old tutorials.** Check the current names against the project's decompiled sources or the user's compiler output.
- The sandbox probably cannot build the project, because Maven hosts are likely blocked. Treat compile errors from the user's machine as the source of truth, and ask the user to run `.\gradlew.bat build` after each bundle and paste the result.
- Versions live in `gradle.properties`. That file is the source of truth, not these docs.

## End of every session

Update `HANDOFF.md` (state, what changed, next step, open questions). Add an entry to `docs/DECISIONS.md` for any non-obvious choice.
