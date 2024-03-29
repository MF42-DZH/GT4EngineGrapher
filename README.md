# GT4 Engine Grapher

This is a program trying to replicate the power/torque graphs you see in GT4 in the tuning menu.

All data is sourced from the SpecDB databases of the game (whose SQLite files were sourced from [Nenkai/GT4SaveEditor](https://github.com/Nenkai/GT4SaveEditor)).

Currently, torque units are stuck on `kgf.m` and power units are stuck on `PS`. Power and torque are also not accurate to how they are in GT4.

### High Priority Issues

[Power and Torque are inaccurate, and the inaccuracy varies wildly between car and upgrade combinations.](https://github.com/MF42-DZH/GT4EngineGrapher/issues/8)

### Usage

The grapher can be run as a standalone program with no arguments, but launching from the terminal will yield some customizability. Pass in `--help` or `-h` to the program for more information.

- If building and running (or running a prebuilt) JAR, use Java 21.
- If running from an installed instance, run the program executable installed onto your system. It has a JRE packaged along with all the necessary dependencies.

## Used Libraries

Reading the SpecDB SQLite databases is done using [Slick](https://scala-slick.org/) and an SQLite JDBC driver.

The graphs are powered by [JFreeChart](https://www.jfree.org/jfreechart/), and as such, you can right-click on the chart and save an image of the chart.

## Screenshots

### Parts Picker

This UI style will be system-dependent. For Linux, it assumes you have GTK+ 2.2 (or newer) installed.

![Parts Picker](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/PartsPicker.png)

### Graph

![Torque / power graph for the HPA Stage II R32](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/EngineGraph.png)

## Special Thanks

- TeaKanji: For inspiring me to go down this rabbit-hole.
- Ablationer: From the GT Modding Discord, for helping me with torque modifiers for upgrades (and inadvertently helping me rubber-duck debug this program).
