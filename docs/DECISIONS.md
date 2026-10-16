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
Ollama at `localhost:11434` with a Qwen model, thinking mode disabled for speed. The exact model variant is undecided. Accepted (variant open).

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

