# HANDOFF

Read this first in a new session. Update it whenever it goes stale (see the checklist at the bottom).

**Last updated:** 2026-10-11 (bundle 002: renderer, model, spawn egg)

## Where we are

- Phase 0 (setup) is **done**.
- Phase 1 (the body) is **mostly done**, pending an in-game check of bundle 002:
  - Done: `VerityEntity` (extends `PathfinderMob`, 10 max health, 0.2 movement speed), `ModEntities` (entity type 0.52 x 0.52 plus attributes), `ModItems` (spawn egg in the Spawn Eggs tab), client model/renderer/model layer (`VerityModel`, `VerityRenderer`, `VerityModelLayers`), cube texture with a smiley, spawn egg texture and item model, `en_us.json` names.
  - Not done: config file for speed and max HP, a visual check that the face is on the correct side and the entity looks right.
- Nothing from Phases 2 to 5 exists yet.

## Environment

- Windows, IntelliJ IDEA, Temurin JDK 25, project at `D:\Tools\dev\verity`.
- Versions: see `gradle.properties` (Minecraft 26.2, Fabric Loader 0.19.x, Loom 1.18.x, Fabric API 0.161.x+26.2 at the time of writing).
- GitHub: https://github.com/YatharthJorwal/Verity

## Next step

1. User merges bundle 002, runs `.\gradlew.bat build`, then `.\gradlew.bat runClient`, creates a creative world, finds the Verity Spawn Egg in the Spawn Eggs tab (or runs `/summon verity:verity`), and reports what it looks like.
2. Fix any compile or visual problems found.
3. Then the config file (speed, max HP), and Phase 2 goals.

## Open questions

- Which Qwen model variant to run on Ollama (size and tag). Not chosen yet.
- Bundle 002 compiles only if the vanilla 26.2 class names match the 26.1.2 docs reference. Fabric API names were verified against the 26.2 branch. Confirm with the user's build output.

## Gotchas learned so far

- No Mods button in the title screen is normal. Vanilla Fabric does not add one. "Minecraft 26.2 (Modded)" in the corner confirms Fabric loaded.
- A mob with no registered renderer crashes the client when summoned. Bundle 002 adds the renderer.
- Useful sources for API names, reachable from the sandbox: `github.com/FabricMC/fabric` (branch `26.2`) and `github.com/FabricMC/fabric-docs` (`reference/26.1.2`).

## Update checklist

Update this file when: a phase item is finished, a decision changes the plan, a new gotcha is found, or the next step is no longer accurate. Keep it short. Move history to `docs/DECISIONS.md`.
