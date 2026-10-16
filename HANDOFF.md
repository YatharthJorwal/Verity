# HANDOFF

Read this first in a new session. Update it whenever it goes stale (see the checklist at the bottom).

**Last updated:** 2026-10-17 (bundle 006: modes, follow, stay, persistence, /verity mode)

## Where we are

- Phase 0 (setup) is **done**.
- Phase 1 (the body) is **done** and verified in game, including the cube following head pitch and yaw.
- Phase 2 (basic behavior) is **mostly done**:
  - Verified in game: wander slowly, look at the nearest player, glance around.
  - Bundle 006 adds, awaiting the user's check: `VerityMode` (WANDER, FOLLOW, STAY), `StayGoal`, `FollowPlayerGoal` (starts beyond 5 blocks, stops within 3), a mode-gated stroll goal, no-despawn, mode saved with the entity, and `/verity mode <wander|follow|stay> <targets>`.
  - Not done: death and respawn handling.
- Nothing from Phases 3 to 5 exists yet.

## Environment

- Windows, IntelliJ IDEA, Temurin JDK 25, project at `D:\Tools\dev\verity`.
- Versions: see `gradle.properties` (Minecraft 26.2, Fabric Loader 0.19.x, Loom 1.18.x, Fabric API 0.161.x+26.2 at the time of writing).
- GitHub: https://github.com/YatharthJorwal/Verity
- Model for the brain (Phase 3): `hf.co/bartowski/Qwen_Qwen3.5-9B-GGUF:Q6_K_L`

## Next step

1. User merges bundle 006, builds, and tests: `/verity mode follow @e[type=verity:verity]` (walk away, it should trail at a distance), `stay` (it stops, still turns its head), `wander` (back to normal). Also quit and reload the world to check the mode is kept.
2. Then decide and build death and respawn handling, the last Phase 2 item.
3. Then Phase 3, starting with the Ollama client on a background thread, before any model output touches the game.

## Open questions

- Phase 3: confirm Ollama can run the chosen model and that thinking mode can be turned off for it.

## Gotchas learned so far

- No Mods button in the title screen is normal. Vanilla Fabric does not add one. "Minecraft 26.2 (Modded)" in the corner confirms Fabric loaded.
- A mob with no registered renderer crashes the client when summoned. Bundle 002 adds the renderer.
- Look-at behavior can be correct in game logic but invisible if the model never applies `state.xRot` and `state.yRot` (degrees, convert to radians for model parts).
- Useful sources for API names, reachable from the sandbox: `github.com/FabricMC/fabric` (branch `26.2`) and `github.com/FabricMC/fabric-docs` (`reference/26.1.2`).

## Update checklist

Update this file when: a phase item is finished, a decision changes the plan, a new gotcha is found, or the next step is no longer accurate. Keep it short. Move history to `docs/DECISIONS.md`.
