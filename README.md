# Craft Config
TODO: CHANGE README FOR CRAFT CONFIG
Description goes here
### List of my mods using this API: (change this)
| **Modrinth**     |**Curseforge**   |
| ------------ |------------ |
|[Holo Damage Indicator](https://modrinth.com/mod/holo-damage-indicator)|[Holo Damage Indicator](https://www.curseforge.com/minecraft/mc-mods/holo-damage-indicator)| 
|[Holo Health Indicator](https://modrinth.com/mod/holo-health-indicator)|[Holo Health Indicator](https://www.curseforge.com/minecraft/mc-mods/holo-health-indicator)|
|[Holo Items](https://modrinth.com/mod/holo-items)|[Holo Items](https://www.curseforge.com/minecraft/mc-mods/holo-items)|

![demo](https://cdn.modrinth.com/data/cached_images/7ff004424d8c36154cbe82d7f84dbb19128bf2e7.png)

## For developers

### Setup
Download a version of the mod that is suitable for your version.
Add this to dependencies

```
modApi files("<your file path>")
```

<your file path> should be replaced to where your downloaded mod is.

Note: use double ```\\```
 instead of ```\``` in your path to avoid formatting.
Example:

```
modApi files("C:\\Users\\UserName\\Documents\\hologram_api-fabric-1.0.0 1.21.1.jar")
```


### Example Usage

```java
Component component = Component.literal("Hello World!").withStyle(ChatFormatting.GREEN); //create a minecraft component 
Hologram hologram = CraftConfig.create(comp, 0,80,0).lifetime(40).shadow(true).scale(2).renderDistance(20); //creating the hologram
hologram.onUpdate(h -> { //setting the animation (updates 20 times a second)
  h.y += 0.05f; //example animation to rise one block up every tick.
});
```

This creates a hologram with text "Hello World!" at coordinates 0, 80 ,0. It lasts for a total of 40 seconds and has a scale of 2. Additionally, it will only render when the player is within 20 blocks of radius.

