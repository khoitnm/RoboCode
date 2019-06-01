#Introduction
This project is my robot for the game RoboCode (https://robocode.sourceforge.io/)

#Terms
## Overview
- Source: our robot
- Target: other robot which is scanned by radar.
- Aimed position: the position when finishing aiming.
- Fired position: the position when bullet hit target.
- Velocity: negative & positive speed.
- Speed: = Abs(velocity) (always positive)
- Direction (Heading): negative & positive angle.
- MoveAngle: = Abs(heading) (always positive)

## Actions 
StandStill -> Start Aim -> Aimed (Finsh aiming) -> Start Fire -> Fired (Finished firing == bullet hit target)
