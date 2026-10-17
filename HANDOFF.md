# HANDOFF

Read this first in a new session. Update it whenever it goes stale (see the checklist at the bottom).

**Last updated:** 2026-10-17 (bundle 010: reworked death events)

## Where we are

- Phase 0 (setup) is **done**.
- Phase 1 (the body) is **done** and verified in game, including the cube following head pitch and yaw.
- Phase 2 (basic behavior) is **mostly done**:
  - Verified in game: wander slowly, look at the nearest player, glance around.
  - Bundle 006 adds, awaiting the user's check: `VerityMode` (WANDER, FOLLOW, STAY), `StayGoal`, `FollowPlayerGoal` (starts beyond 5 blocks, stops within 3), a mode-gated stroll goal, no-despawn, mode saved with the entity, and `/verity mode <wander|follow|stay> <targets>`.
  - Verified in game: stay, follow, wander (after the bundle 007 rewrite of the wander goal), `/verity status`.
  - Death events (bundle 008) work in game. Bundle 010 reworks them (see D-019): one event per death, uniform among categories, weaker lightning, temporary lava, more effects. Awaiting the user's check.
  - Not done: nothing planned for Phase 2 beyond testing the death events.
- Nothing from Phases 3 to 5 exists yet.

## Environment

- Windows, IntelliJ IDEA, Temurin JDK 25, project at `D:\Tools\dev\verity`.
- Versions: see `gradle.properties` (Minecraft 26.2, Fabric Loader 0.19.x, Loom 1.18.x, Fabric API 0.161.x+26.2 at the time of writing).
- GitHub: https://github.com/YatharthJorwal/Verity
- Model for the brain (Phase 3): `hf.co/bartowski/Qwen_Qwen3.5-9B-GGUF:Q6_K_L`

## Next step

1. User merges bundle 010, builds, and tests the death events many times in a throwaway world (`/kill @e[type=verity:verity]` targets the nearest player; the log line `Verity death event: <name>` says which event fired). Check that lava disappears after about 3 seconds and that lightning does not kill.
2. Phase 3, starting with the Ollama client on a background thread, before any model output touches the game. First confirm Ollama can run the chosen model with thinking off.

## Open questions

- Phase 3: confirm Ollama can run the chosen model and that thinking mode can be turned off for it.

## Gotchas learned so far

- No Mods button in the title screen is normal. Vanilla Fabric does not add one. "Minecraft 26.2 (Modded)" in the corner confirms Fabric loaded.
- A mob with no registered renderer crashes the client when summoned. Bundle 002 adds the renderer.
- Look-at behavior can be correct in game logic but invisible if the model never applies `state.xRot` and `state.yRot` (degrees, convert to radians for model parts).
- In 26.2, vanilla entity type constants (`LIGHTNING_BOLT`, `PIG`, `ZOMBIE`, ...) live in `net.minecraft.world.entity.EntityTypes`, not `EntityType`. The 26.1.2 docs repo still shows the old form, so prefer the Fabric API 26.2 branch for names. Positioning uses `snapTo`.
- Useful sources for API names, reachable from the sandbox: `github.com/FabricMC/fabric` (branch `26.2`) and `github.com/FabricMC/fabric-docs` (`reference/26.1.2`).

## Update checklist

Update this file when: a phase item is finished, a decision changes the plan, a new gotcha is found, or the next step is no longer accurate. Keep it short. Move history to `docs/DECISIONS.md`.
