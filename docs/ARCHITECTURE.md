# ARCHITECTURE

Status markers: **[exists]** is in the repo now, **[planned]** is not written yet.

## Package layout (`verity`)

| Path | Status | Role |
|---|---|---|
| `verity.Verity` | exists | Mod entrypoint. Holds `MOD_ID`, logger, `id(path)` helper. Calls `ModEntities.init()` |
| `verity.ModEntities` | exists | Registers the `verity:verity` entity type and its default attributes |
| `verity.entity.VerityEntity` | exists | The mob. Extends `PathfinderMob`. Holds the current `VerityMode`, never despawns, saves its mode. Goal priority: float, stay, follow, stroll, look at player, look around |
| `verity.event.VerityDeathEvents` | exists | On death, picks exactly one random event (ignite, weak lightning, temporary lava, random bad effect) and applies it to the killer or the nearest player. Risky events are skipped at low health |
| `verity.event.TemporaryBlocks` | exists | Places a block briefly and restores the original, including on server stop |
| `verity.entity.VerityMode` | exists | WANDER, FOLLOW, STAY. Goals check it. Later, validated model actions set it |
| `verity.client.VerityClient` | exists | Client entrypoint. Registers the model layer and the entity renderer |
| `verity.client.VerityModel`, `VerityModelLayers`, `VerityRenderer` | exists | 8x8x8 cube model (32x16 texture, pivots at its center and follows head pitch and yaw), model layer registration, `MobRenderer` using vanilla `LivingEntityRenderState` |
| `verity.ModItems` | exists | Registers the spawn egg and adds it to the Spawn Eggs creative tab |
| `verity.config.VerityConfig` | exists | Loads `config/verity.json` (max health, movement speed, death events on/off, Ollama URL, model and timeout) with Gson at startup. Later: AI toggle |
| `verity.entity.goal` | exists | `ModeStrollGoal` (own wander logic), `StayGoal`, `FollowPlayerGoal`. Later: goals for `walk_to` and `look_at` model actions |
| `verity.ai.OllamaClient` | exists | Sends chat requests to Ollama on one background thread (`verity-brain`) and returns a `CompletableFuture`. Thinking off, non-streaming |
| `verity.ai.Observation`, `ObservationCollector` | exists | Immutable snapshot of nearby players (distance, whether they look at Verity, held item), light level, weather, Verity's own mode and health. Collected on the server thread, turned into prompt text with `toPromptText()`. Time of day, recent chat and builds are not in yet |
| `verity.ai` (rest) | planned | Prompt builder, JSON parser and action validator, action executor, the periodic loop |
| `verity.memory` | planned | Per-world memory file and summarizer |
| `verity.command.VerityCommands` | exists | `/verity mode <mode> <targets>`, `/verity status <targets>`, `/verity ping`, `/verity observe <targets>`. Later: kill switch and debug commands |

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
