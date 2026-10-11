# RULES

## Code
1. Mojang names only. Minecraft 26.x is unobfuscated. No Yarn, no `remapJar`.
2. Verify class and method names against the real 26.2 sources or compiler output. Do not rely on memory or old tutorials.
3. Client-only code lives in `src/client`. Never reference client classes from `src/main`.
4. The game thread never blocks on network or model calls.
5. Treat all model output as untrusted. Validate before acting. Invalid output means idle.
6. The model never controls movement. It selects actions from the fixed set. Vanilla pathfinding moves the mob.
7. Keep prompts short. Add context only when it changes the behavior.
8. Every AI feature must respect the config toggle and the kill switch.
9. Keep `fabric.mod.json`, `gradle.properties` and the docs consistent when versions change.

## Workflow
1. One step at a time. Wait for the result before the next step.
2. Changes are delivered as merge bundles (zip laid out as the project root). List new and replaced files every time.
3. After each bundle the user runs `.\gradlew.bat build` and reports the result. A bundle is not "done" until it builds.
4. Commit after each working bundle, with a short message describing what it added.
5. Keep the sandbox and the user's machine in sync through git: the user pushes, a new session clones.

## Git style
- Commands are copy-pasteable, one per code block, no inline comments.
- Commit messages are short and in the imperative: "Add Verity renderer".

## Docs
- `HANDOFF.md` is updated at the end of every session and whenever it goes stale.
- Non-obvious choices get a numbered entry in `docs/DECISIONS.md`.
- `docs/SPEC.md` changes only when the plan itself changes.
