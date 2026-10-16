# SPEC

What Verity does, by phase. Items are checked off in `HANDOFF.md`, not here.

## Phase 0: Setup
- Matching Minecraft 26.2, Fabric Loader, Fabric API and Loom versions.
- JDK 25 and IntelliJ. Project from the Fabric template. Empty mod runs in a dev client.

## Phase 1: The body
- Custom entity `Verity`: small passive mob, walk-only (no hopping), no attack goal.
- Slime-small hitbox. Simple cube or flat model.
- Smiley face texture.
- Speed and max HP in a config file.
- Spawn egg and `/summon` for testing.

## Phase 2: Basic behavior (no AI)
- Goals: wander slowly, look at nearest player, follow at a distance, stand still.
- Persistent (never despawns). Sensible death and respawn handling.

## Phase 3: The brain
- Background thread so the game never freezes while the model thinks.
- Observations every few seconds: nearby players, time of day, light level, recent chat, what the player holds or builds.
- Send observations plus a personality prompt to Ollama at `localhost:11434`. Model: `hf.co/bartowski/Qwen_Qwen3.5-9B-GGUF:Q6_K_L`.
- Model replies with JSON using a fixed action set: `walk_to`, `look_at`, `follow`, `stay`, `say`, `vanish`.
- Every reply is validated. Bad output falls back to idle.
- Vanilla pathfinding executes actions. The model never controls movement directly.

## Phase 4: Memory and personality
- Per-world memory file: player name, notable events, deaths, builds, past chat.
- A short summary of it goes into each model call.
- Personality prompt, iterated through test sessions. Restraint is the main tool: speaks rarely, reacts to specific details, moves when the player looks away.

## Phase 5: Polish
- Sounds, a subtle ambient effect.
- Config toggle for the AI and a kill switch command.
- Test alongside the user's other Fabric mods.

## Risks
- The newest Minecraft version may break tooling or change APIs. Verify names against real sources.
- Model thinking mode slows replies, so it is disabled.
- Too much context makes the model slow and unfocused, so prompts stay short.
