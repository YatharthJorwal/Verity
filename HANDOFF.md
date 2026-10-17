# HANDOFF

Read this first in a new session. Update it whenever it goes stale (see the checklist at the bottom).

**Last updated:** 2026-10-17 (bundle 011: Ollama client, ping, lava 1 second)

## Where we are

- Phase 0 (setup), Phase 1 (the body) and Phase 2 (basic behavior) are **done and verified in game**: entity, renderer, spawn egg, config, wander/follow/stay modes, persistence, and the death events (one random event per death, see D-019). Lava now lasts 1 second.
- Phase 3 (the brain) has **started**: bundle 011 adds `OllamaClient` (background thread, non-streaming, thinking off), Ollama settings in the config, and `/verity ping` to test the connection. Nothing from the model touches the game yet. Awaiting the user's check that Ollama is installed, the model is pulled, and `/verity ping` gets an answer.
- Not started: observations, personality prompt, JSON action parsing and validation, executing actions, memory (Phase 4), polish (Phase 5).

## Environment

- Windows, IntelliJ IDEA, Temurin JDK 25, project at `D:\Tools\dev\verity`.
- Versions: see `gradle.properties` (Minecraft 26.2, Fabric Loader 0.19.x, Loom 1.18.x, Fabric API 0.161.x+26.2 at the time of writing).
- GitHub: https://github.com/YatharthJorwal/Verity
- Model for the brain (Phase 3): `hf.co/bartowski/Qwen_Qwen3.5-9B-GGUF:Q6_K_L`

## Next step

1. User installs Ollama, pulls the model, merges bundle 011, and runs `/verity ping` in game. Note how long the first reply takes and whether the text looks clean (no thinking text).
2. Next bundle: observation snapshot (nearby players, time of day, light level, held item) collected on the server thread and turned into a short text.
3. Then the action contract: the model replies with JSON from the fixed set (`walk_to`, `look_at`, `follow`, `stay`, `say`, `vanish`), parsed and validated, with idle as the fallback. `follow` and `stay` map onto `VerityMode`.

## Open questions

- Whether `think: false` really suppresses thinking for the chosen model, and typical reply time. Learned from the first `/verity ping`.

## Gotchas learned so far

- No Mods button in the title screen is normal. Vanilla Fabric does not add one. "Minecraft 26.2 (Modded)" in the corner confirms Fabric loaded.
- A mob with no registered renderer crashes the client when summoned. Bundle 002 adds the renderer.
- Look-at behavior can be correct in game logic but invisible if the model never applies `state.xRot` and `state.yRot` (degrees, convert to radians for model parts).
- In 26.2, vanilla entity type constants (`LIGHTNING_BOLT`, `PIG`, `ZOMBIE`, ...) live in `net.minecraft.world.entity.EntityTypes`, not `EntityType`. The 26.1.2 docs repo still shows the old form, so prefer the Fabric API 26.2 branch for names. Positioning uses `snapTo`.
- Useful sources for API names, reachable from the sandbox: `github.com/FabricMC/fabric` (branch `26.2`) and `github.com/FabricMC/fabric-docs` (`reference/26.1.2`).

## Update checklist

Update this file when: a phase item is finished, a decision changes the plan, a new gotcha is found, or the next step is no longer accurate. Keep it short. Move history to `docs/DECISIONS.md`.
