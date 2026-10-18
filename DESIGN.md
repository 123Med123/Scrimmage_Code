# BioBuzz Bot Design: mecanum + 3-wheel odometry + Limelight 3A + dual-flywheel shooter

Goal: intake and shoot pollen (2.8 in) and nectar (3.6 in); tip the hive in AUTO (20 pts) by launching into the upward-facing cell.

## Rule limits that drive the design
- Start in an 18 in cube touching the wall with 4 pollen (R102, G304); may expand to 18 x 24 x 29 in (R105).
- Max 8 motors and 8 servos (R503). Limelight 3A only. COTS dead wheels are legal (R303). No pneumatics.
- Hive tips only if elements are launched into the upward-facing cell (G417). The opening is about 20 in wide x 14 in tall and is reported at 53-66 in off the floor (**unverified, measure on your field**), so the shooter needs a high arc.

## Subsystems
| Subsystem | Parts | Notes |
|---|---|---|
| Drive | 4 x goBILDA 5203 (312 rpm) or REV HD Hex, 96 mm mecanum | 4 motors |
| Odometry | 3 dead wheels (2 parallel, 1 perpendicular), goBILDA 35 mm pods | Encoders plug into drive-motor encoder ports (no extra motors). Pedro default names: `lf`, `rr`, `lr` |
| Intake | 1 motor, compliant (flex) wheels across the front, 4.5 in wide mouth | One roller must pull in both 2.8 in and 3.6 in balls; use compression 0.25-0.5 in |
| Indexer | Short ramp from intake into a 4-5 element vertical or curved magazine, then a continuous-rotation feeder servo | Holds the 4 preloads inside the 18 in cube |
| Shooter | 2 flywheel motors (goBILDA 6000 rpm), 4 in compliant wheels facing each other, adjustable hood servo | Two wheels give backspin control and consistent exit speed; hood sets angle 55-70 deg |
| Vision | Limelight 3A, front mounted, AprilTag pipeline | Use for heading and distance correction, not for tip detection |

Motor count: 4 drive + 1 intake + 2 shooter = 7 of 8. Servos: feeder, hood (+ spare).

## Shooter sizing (starting point)
- Distance to hive center from a shooting spot ~35 in, target height ~60 in, release height ~14 in -> at 65 deg the exit speed is ~ 10-11 m/s.
- Wheel 4 in (0.1 m) at 6000 rpm gives surface speed ~31 m/s unloaded; ball exit speed is ~50% of that under load, so ~15 m/s possible. Run ~65-70% power or use velocity control.
- Use velocity control (`setVelocity`) so every element leaves at the same speed. Feed only when at speed.
- Pollen and nectar need separate speed/hood presets (see `Shooter.java`).

## Build checklist
1. Mount odometry pods with springs/rubber bands so they stay in contact; measure their X/Y offsets from the center.
2. Tune Pedro (follower, mecanum, 3-wheel localizer) using the Pedro AutoTune page.
3. Measure the real hive cell height and calibrate `HOOD_*` and `TARGET_VELOCITY_*`.
4. Count how many elements it takes to tip the hive on your field and update `SHOTS` in the Auto.

## Known unknowns
- Tip threshold is not published in the sources I found. The 4 preloaded pollen may not be enough; you may need nectar or a partner's help in TeleOp.
- All pose and shooter numbers are placeholders. Nothing has been compiled or run on a robot.
