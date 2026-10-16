# Verity

A Fabric mod for Minecraft Java Edition 26.2 that adds **Verity**, a small passive companion mob with a smiley face. Verity is driven by a local language model (via Ollama), so it can notice what you do and react. It speaks rarely, reacts to specific details, and moves when you look away.

> Status: early development. See [HANDOFF.md](HANDOFF.md) for the current state.

## How it works

- Verity is a normal Minecraft mob. Vanilla pathfinding does all movement.
- Every few seconds a background thread collects observations (nearby players, time, light, recent chat, what you hold) and sends them to a local Ollama server.
- The model replies with one JSON action from a fixed set. The mod validates it. Anything invalid falls back to idle.
- The model never controls movement directly.

## Requirements

- Minecraft Java Edition 26.2
- Fabric Loader 0.19.5 or newer, and Fabric API for 26.2
- Java 25
- Ollama running locally on `localhost:11434` (only for the AI features, which can be toggled off), with the model `hf.co/bartowski/Qwen_Qwen3.5-9B-GGUF:Q6_K_L` pulled

## Configuration

On first launch the mod writes `config/verity.json` in the game folder. Current options: `maxHealth` and `movementSpeed`. Restart the game after editing.

## Commands

| Command | Effect |
|---|---|
| `/verity mode <wander\|follow\|stay> <targets>` | Sets what Verity is doing. Example: `/verity mode stay @e[type=verity:verity]` |
| `/verity status <targets>` | Shows each Verity's position, mode, and whether it is moving |

## Building from source

Windows:

```
.\gradlew.bat build
```

Run a dev client:

```
.\gradlew.bat runClient
```

## Project docs

| File | Purpose |
|---|---|
| [HANDOFF.md](HANDOFF.md) | Read first. Current state and next steps |
| [CLAUDE.md](CLAUDE.md) | Instructions for AI assistants working on this repo |
| [docs/SPEC.md](docs/SPEC.md) | What the mod does, by phase |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Code layout and data flow |
| [docs/RULES.md](docs/RULES.md) | Coding and workflow rules |
| [docs/DECISIONS.md](docs/DECISIONS.md) | Decision log |

## License

MIT. See [LICENSE](LICENSE).
