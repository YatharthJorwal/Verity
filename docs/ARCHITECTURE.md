# ARCHITECTURE

Status markers: **[exists]** is in the repo now, **[planned]** is not written yet.

## Package layout (`verity`)

| Path | Status | Role |
|---|---|---|
| `verity.Verity` | exists | Mod entrypoint. Holds `MOD_ID`, logger, `id(path)` helper. Calls `ModEntities.init()` |
| `verity.ModEntities` | exists | Registers the `verity:verity` entity type and its default attributes |
| `verity.entity.VerityEntity` | exists | The mob. Extends `PathfinderMob` |
| `verity.client.VerityClient` | exists (empty) | Client entrypoint. Will register the renderer |
| `verity.client.*` renderer and model | planned | Cube model, smiley texture |
| `verity.ModItems` (spawn egg) | planned | Item registration |
| `verity.config` | planned | Config file for speed and max HP, later AI toggle |
| `verity.entity.goal` | planned | Wander, look at player, follow, stand still |
| `verity.ai` | planned | Observation collector, Ollama HTTP client, prompt builder, JSON parser and action validator, action executor |
| `verity.memory` | planned | Per-world memory file and summarizer |
| `verity.command` | planned | Kill switch and debug commands |

## Planned data flow (Phase 3)

```
server tick thread                      background thread
------------------                      -----------------
collect observations  --snapshot-->     build prompt (personality + memory summary + observations)
                                        POST to Ollama (localhost:11434)
                                        parse JSON reply
                                        validate against fixed action set
apply validated action  <--queue--      (invalid -> idle)
(set goal / target, vanilla pathfinding)
```

Rules for this flow:
- The game thread never waits on the model. Results come back through a thread-safe queue and are applied on the next tick.
- The model only picks an action and its arguments. Movement is always vanilla pathfinding.
- Anything the model returns is untrusted input and is validated before use.

## Source layout

- `src/main`: common code (runs on server and client): entity, registration, AI, memory.
- `src/client`: client-only code: renderers, models, client entrypoint.
- Entity renderers must stay in `src/client`. Referencing client classes from `src/main` crashes dedicated servers.
