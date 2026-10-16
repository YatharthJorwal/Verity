# HANDOFF

Read this first in a new session. Update it whenever it goes stale (see the checklist at the bottom).

**Last updated:** 2026-10-17 (bundle 003: config file)

## Where we are

- Phase 0 (setup) is **done**.
- Phase 1 (the body) is **code complete**: entity, entity type, renderer and cube model, textures, spawn egg, and a config file (`config/verity.json` with `maxHealth` and `movementSpeed`, read at startup).
- Not yet confirmed by the user: how Verity looks in game (face on the correct side, size).
- Nothing from Phases 2 to 5 exists yet.

## Environment

- Windows, IntelliJ IDEA, Temurin JDK 25, project at `D:\Tools\dev\verity`.
- Versions: see `gradle.properties` (Minecraft 26.2, Fabric Loader 0.19.x, Loom 1.18.x, Fabric API 0.161.x+26.2 at the time of writing).
- GitHub: https://github.com/YatharthJorwal/Verity
- Model for the brain (Phase 3): `hf.co/bartowski/Qwen_Qwen3.5-9B-GGUF:Q6_K_L`

## Next step

Phase 2, basic behavior with no AI: goals for wander slowly, look at the nearest player, follow at a distance, and stand still; make the entity persistent (no despawn); sensible death and respawn handling. Do it in small bundles, one or two goals at a time.

## Open questions

- Phase 3: confirm Ollama can run the chosen model and that thinking mode can be turned off for it.

## Gotchas learned so far

- No Mods button in the title screen is normal. Vanilla Fabric does not add one. "Minecraft 26.2 (Modded)" in the corner confirms Fabric loaded.
- A mob with no registered renderer crashes the client when summoned. Bundle 002 adds the renderer.
- Useful sources for API names, reachable from the sandbox: `github.com/FabricMC/fabric` (branch `26.2`) and `github.com/FabricMC/fabric-docs` (`reference/26.1.2`).

## Update checklist

Update this file when: a phase item is finished, a decision changes the plan, a new gotcha is found, or the next step is no longer accurate. Keep it short. Move history to `docs/DECISIONS.md`.
