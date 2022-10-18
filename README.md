# Charged Explosives
A small mod adding in configurable charged explosives. Craft an explosive, then right-click to view the configuration GUI to select the width, height and depth of the explosion.

Place the explosive down on any surface (wall, floor or ceiling), check the explosion preview (the red rectangle that pops up) and then right-click or activate with redstone to start the countdown timer. Alternatively, placing one down with a redstone torch in your offhand will automatically start the countdown timer.

Once the timer elapses, the explosive will detonate.

Via `config/charged_explosives-common.toml` you can configure:

- Maximum Explosion Settings
- Time between activation & detonation
- Number of beeps during time between activation & detonation (I usually keep these equal to make it 1 beep / second)
- Concussive Damage Scale - a percent scale to control how much concussive damage is dealt to entities nearby, if any
- Control over whether the detonation drops the blocks exploded or not