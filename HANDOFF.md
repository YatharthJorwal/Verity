# HANDOFF

Read this first in a new session. Update it whenever it goes stale (see the checklist at the bottom).

**Last updated:** 2026-10-17 (bundle 005: cube follows head pitch and yaw)

## Where we are

- Phase 0 (setup) is **done**.
- Phase 1 (the body) is **done**: entity, entity type, renderer and cube model, textures, spawn egg, config file. The config was verified in game (`maxHealth` change took effect after a restart).
- Phase 2 (basic behavior) has **started**: Verity wanders slowly, looks at the nearest player within 8 blocks, and glances around, using vanilla goals (bundle 004). User confirmed it wanders and glances around. It did not tilt up at the player because the model ignored head pitch; bundle 005 makes the cube follow head pitch and yaw. Awaiting the user's check of that.
- Not done in Phase 2: follow at a distance, stand still, persistence (no despawn), death and respawn handling.
- Nothing from Phases 3 to 5 exists yet.

## Environment

- Windows, IntelliJ IDEA, Temurin JDK 25, project at `D:\Tools\dev\verity`.
- Versions: see `gradle.properties` (Minecraft 26.2, Fabric Loader 0.19.x, Loom 1.18.x, Fabric API 0.161.x+26.2 at the time of writing).
- GitHub: https://github.com/YatharthJorwal/Verity
- Model for the brain (Phase 3): `hf.co/bartowski/Qwen_Qwen3.5-9B-GGUF:Q6_K_L`

## Next step

1. User merges bundle 005, builds, summons Verity, stands close, moves above and below it, and reports whether it tilts to face them.
2. Then the remaining Phase 2 items, one or two per bundle: stand still, follow at a distance, persistence, death and respawn.

## Open questions

- Phase 3: confirm Ollama can run the chosen model and that thinking mode can be turned off for it.

## Gotchas learned so far

- No Mods button in the title screen is normal. Vanilla Fabric does not add one. "Minecraft 26.2 (Modded)" in the corner confirms Fabric loaded.
- A mob with no registered renderer crashes the client when summoned. Bundle 002 adds the renderer.
- Look-at behavior can be correct in game logic but invisible if the model never applies `state.xRot` and `state.yRot` (degrees, convert to radians for model parts).
- Useful sources for API names, reachable from the sandbox: `github.com/FabricMC/fabric` (branch `26.2`) and `github.com/FabricMC/fabric-docs` (`reference/26.1.2`).

## Update checklist

Update this file when: a phase item is finished, a decision changes the plan, a new gotcha is found, or the next step is no longer accurate. Keep it short. Move history to `docs/DECISIONS.md`.
