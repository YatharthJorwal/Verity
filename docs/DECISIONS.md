# DECISIONS

Newest at the bottom. Status is **accepted** or **provisional** (revisit if it causes trouble).

## D-001: Fabric with Java
Fabric Loader and Fabric API, written in Java. Accepted.

## D-002: Target Minecraft 26.2, Java 25, unobfuscated names
26.x is unobfuscated, so the project uses Mojang names with the non-remapping Loom setup from the official template. Accepted.

## D-003: The model never controls movement
The model chooses from a fixed action set (`walk_to`, `look_at`, `follow`, `stay`, `say`, `vanish`). Vanilla pathfinding executes. This keeps behavior safe and predictable. Accepted.

## D-004: Validate every model reply
Parse JSON, check the action name and arguments against the fixed set. Anything invalid falls back to idle. Accepted.

## D-005: Background thread for model calls
Ollama calls run off the game thread so the game never freezes. Accepted.

## D-006: Local model through Ollama, thinking mode off
Ollama at `localhost:11434`, thinking mode disabled for speed. Model: `hf.co/bartowski/Qwen_Qwen3.5-9B-GGUF:Q6_K_L` (Qwen3.5 9B, Q6_K_L quant from bartowski). Accepted.

## D-007: Entity base class and size
`VerityEntity` extends `PathfinderMob` with a 0.52 x 0.52 hitbox (slime-small), 10 max health, 0.2 movement speed. Provisional. Values move to a config file in Phase 1.

## D-008: Restraint over chattiness
Verity speaks rarely, reacts to specific details, and moves when the player looks away. This is the main personality tool. Accepted.

## D-009: MIT license
The repo is MIT licensed. The template's CC0 license was replaced, and `fabric.mod.json` updated to match. Accepted.

## D-010: Doc layout
`CLAUDE.md`, `HANDOFF.md` and `README.md` sit at the repo root so tools and new sessions find them. Detailed docs live in `docs/`. Accepted.

## D-011: Changes ship as merge bundles
Code changes are delivered as zips laid out like the project root, extracted over the project with overwrite, rather than hand-edited. Accepted.

## D-012: Renderer built on vanilla render state, API names verified from sources
The renderer uses `MobRenderer<VerityEntity, LivingEntityRenderState, VerityModel>` with no custom render state class until one is needed. Registration uses `EntityRenderers.register` (Fabric's `EntityRendererRegistry` is deprecated in 26.2) and `ModelLayerRegistry`. Names were checked against the FabricMC/fabric `26.2` branch and the FabricMC/fabric-docs `26.1.2` reference code. Vanilla class names themselves are not verified until the user's build passes. Accepted.

## D-013: Config is a JSON file read once at startup
`config/verity.json` in the game directory, read with Gson in `VerityConfig.load()` before anything is registered. Missing file means defaults are written out. Values are clamped to safe ranges. A file that fails to parse is never overwritten, and defaults are used instead. Attributes are registered once, so edits need a game restart. Accepted.

## D-014: Start Phase 2 with vanilla goals
Wander and look-at-player use vanilla goals (`RandomStrollGoal`, `LookAtPlayerGoal`, `RandomLookAroundGoal`, plus `FloatGoal`) registered in `VerityEntity.registerGoals()`. Wander uses a 0.6 speed multiplier so Verity moves slowly. Custom goals are written only for behavior vanilla lacks (follow at a distance, stand still, and later the model-driven actions). Priority order: float, wander, look at player, look around. Provisional.

## D-015: Behavior is driven by a mode, not by goals deciding alone
`VerityEntity` holds a `VerityMode` (WANDER, FOLLOW, STAY). Each custom goal runs only in its own mode. This gives Phase 3 a single narrow thing to set: a validated model action like `follow` or `stay` just changes the mode, and vanilla goals and pathfinding do the rest. `/verity mode <mode> <targets>` sets it by hand for testing. The mode is saved with the entity. Provisional.

## D-016: Persistence by overriding removeWhenFarAway
Verity never despawns when players are far away (`removeWhenFarAway` returns false). Death and respawn handling is still open and is its own step. Provisional.

## D-017: Own wander goal instead of subclassing vanilla's
After bundle 006, wander stopped working while stay and follow worked. The root cause was not confirmed. `ModeStrollGoal` is now self-contained (random timing, `DefaultRandomPos`, navigation) so it no longer relies on vanilla stroll internals. `/verity status` reports mode and whether Verity is moving, to diagnose this kind of problem. Provisional.

## D-018: Death events (superseded by D-019)
When Verity dies, `VerityDeathEvents` picks `deathEventCount` (default 2, range 1 to 3) distinct events from: ignite the player and place fire at their feet, lightning at their position, one random bad effect for 15 seconds (poison, wither, blindness, nausea, slowness, weakness, hunger). Target is the player who landed the killing blow, otherwise the nearest player within 32 blocks. Nothing happens if no player is found. `deathEventsEnabled` in the config turns it off. No respawn: the player brings Verity back with the spawn egg. Triggered from `VerityEntity.die`, once per death. Provisional.

## D-019: One death event, startle rather than kill
Replaces D-018. On death exactly one event fires, never two. Selection is uniform among the eligible categories: ignite (5 seconds plus fire at the feet), weak lightning, temporary lava at the feet, random bad effect. The effect category picks uniformly from poison, wither, blindness, nausea, slowness, weakness, hunger, mining fatigue and darkness, with a random duration and amplifier within a per-effect range. Lightning is visual only (flash and sound, no fire) plus up to 4 damage, never taking the player below 2 health. Lava lasts 1 second (changed from 3 in bundle 011) and is then replaced with the original block, and any pending lava is restored when the server stops. Ignite needs the target at 10 or more health and lava at 14 or more; otherwise those categories are skipped for that death. Target is the killer, otherwise the nearest player within 32 blocks. The chosen event is logged. `deathEventCount` was removed from the config. Provisional.

## D-020: Ollama client design
`OllamaClient` uses the JDK's built-in `HttpClient` and Gson, with the native `/api/chat` endpoint, `stream: false` and `think: false`. All requests run on a single daemon thread named `verity-brain`, so requests are processed one at a time and the game thread never waits. Callers receive a `CompletableFuture` and return to the server thread with `server.execute`. Any `<think>` block that still shows up in a reply is stripped. Settings (`ollamaUrl`, `ollamaModel`, `ollamaTimeoutSeconds`) live in the config. `/verity ping` is a manual connectivity test and changes nothing in the world. Provisional.

## D-021: Observations are immutable snapshots built on the server thread
`ObservationCollector.collect` reads live world state and must run on the server thread. It returns an immutable `Observation` record, which is the only thing handed to the background thread. Players are included if within 24 blocks and not spectators, nearest first, at most 4. "Looking at Verity" means the player's view direction is within about 18 degrees of Verity's eyes. Light level is a 0 to 15 number with a word (dark, dim, bright). Time of day is deferred: Minecraft 26.x replaced the old day-time counter with world clocks, so it needs the `ClockManager` API from the real sources. Provisional.

