# HANDOFF

Read this first in a new session. Update it whenever it goes stale (see the checklist at the bottom).

**Last updated:** 2026-10-17 (bundle 012: observation snapshot)

## Where we are

- Phase 0 (setup), Phase 1 (the body) and Phase 2 (basic behavior) are **done and verified in game**: entity, renderer, spawn egg, config, wander/follow/stay modes, persistence, and the death events (one random event per death, see D-019). Lava now lasts 1 second.
- Phase 3 (the brain) is **in progress**:
  - Done and verified: `OllamaClient` and `/verity ping`. First reply took about 53 seconds (model load), later replies about 0.7 seconds, and the text was clean with no thinking leak.
  - Bundle 012 adds `Observation` and `ObservationCollector` (nearby players with distance, whether they look at Verity and their held item, light level, weather, Verity's mode and health) and `/verity observe <targets>` to print it. Awaiting the user's check.
  - Not yet in the snapshot: time of day, recent chat, what the player is building.
- Not started: personality prompt, JSON action parsing and validation, executing actions, memory (Phase 4), polish (Phase 5).

## Environment

- Windows, IntelliJ IDEA, Temurin JDK 25, project at `D:\Tools\dev\verity`.
- Versions: see `gradle.properties` (Minecraft 26.2, Fabric Loader 0.19.x, Loom 1.18.x, Fabric API 0.161.x+26.2 at the time of writing).
- GitHub: https://github.com/YatharthJorwal/Verity
- Model for the brain (Phase 3): `hf.co/bartowski/Qwen_Qwen3.5-9B-GGUF:Q6_K_L`

## Next step

1. User merges bundle 012 and checks `/verity observe @e[type=verity:verity]` against reality (distances, looking at, held item, light, weather).
2. Time of day: the user opens `ClockManager` in IntelliJ (Ctrl+N, type ClockManager, then Alt+7 for the structure) and shares the method list, so the code uses the real 26.2 API.
3. Recent chat (Fabric `ServerMessageEvents`) and a rough "building" signal (recent block placements and breaks), as small separate bundles.
4. Then the action contract: JSON from the fixed set (`walk_to`, `look_at`, `follow`, `stay`, `say`, `vanish`), parsed and validated, idle fallback. `follow` and `stay` map onto `VerityMode`.

## Open questions

- Time of day API in 26.2 (world clocks). See next step 2.
- `think: false` was fine for the chosen model in the first test. Recheck once real prompts and JSON output are in play.

## Gotchas learned so far

- No Mods button in the title screen is normal. Vanilla Fabric does not add one. "Minecraft 26.2 (Modded)" in the corner confirms Fabric loaded.
- Minecraft 26.x has no `getDayTime`. Time is handled by world clocks (`level.dimensionType().defaultClock()` and `server.clockManager()`), so verify the API before using it.
- A mob with no registered renderer crashes the client when summoned. Bundle 002 adds the renderer.
- Look-at behavior can be correct in game logic but invisible if the model never applies `state.xRot` and `state.yRot` (degrees, convert to radians for model parts).
- In 26.2, vanilla entity type constants (`LIGHTNING_BOLT`, `PIG`, `ZOMBIE`, ...) live in `net.minecraft.world.entity.EntityTypes`, not `EntityType`. The 26.1.2 docs repo still shows the old form, so prefer the Fabric API 26.2 branch for names. Positioning uses `snapTo`.
- Useful sources for API names, reachable from the sandbox: `github.com/FabricMC/fabric` (branch `26.2`) and `github.com/FabricMC/fabric-docs` (`reference/26.1.2`).

## Update checklist

Update this file when: a phase item is finished, a decision changes the plan, a new gotcha is found, or the next step is no longer accurate. Keep it short. Move history to `docs/DECISIONS.md`.
