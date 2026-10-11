# HANDOFF

Read this first in a new session. Update it whenever it goes stale (see the checklist at the bottom).

**Last updated:** 2026-10-11 (end of Phase 0 / start of Phase 1)

## Where we are

- Phase 0 (setup) is **done**. Project generated from the Fabric template, empty mod runs in a dev client on Minecraft 26.2, `gradlew build` succeeds.
- Phase 1 (the body) has **started**:
  - Done: `VerityEntity` (extends `PathfinderMob`, 10 max health, 0.2 movement speed) and `ModEntities` (registers the entity type at 0.52 x 0.52 and its default attributes). `Verity.onInitialize()` calls `ModEntities.init()`.
  - Not done: renderer and model (client side), smiley texture, spawn egg, config file, `/summon` test.
- Nothing from Phases 2 to 5 exists yet.

## Environment

- Windows, IntelliJ IDEA, Temurin JDK 25, project at `D:\Tools\dev\verity`.
- Versions: see `gradle.properties` (Minecraft 26.2, Fabric Loader 0.19.x, Loom 1.18.x, Fabric API 0.161.x+26.2 at the time of writing).
- GitHub: https://github.com/YatharthJorwal/Verity

## Next step

Phase 1, client side: register an entity renderer in `VerityClient` with a simple cube model and a placeholder smiley texture, then add a spawn egg. After that, the user can `/summon verity:verity` and see it.

## Open questions

- Which Qwen model variant to run on Ollama (size and tag). Not chosen yet.
- Exact 26.2 client rendering API names for entity renderers. Verify against the real sources before writing code.

## Gotchas learned so far

- No Mods button in the title screen is normal. Vanilla Fabric does not add one. "Minecraft 26.2 (Modded)" in the corner confirms Fabric loaded.
- Summoning the entity before a renderer exists will crash the client.

## Update checklist

Update this file when: a phase item is finished, a decision changes the plan, a new gotcha is found, or the next step is no longer accurate. Keep it short. Move history to `docs/DECISIONS.md`.
