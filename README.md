# Craft Config Lib

A client-sided configuration library with preset support, made for my mods. Feel free to use it in your mod if it fits your needs.

### Features
- **Config Presets**
  - Allow switching between multiple different configs quickly
  - Auto apply presets on specific world/servers
- **Built-in keybind support**
  - Automatically create options (boolean only) that can be binded to a key
- **Auto config registering**
  - Registers the config screens automatically, without the need of manually registering the configs.



## Showcase
### Main config screen
![main](https://cdn.modrinth.com/data/cached_images/9ef58bc4476109cd8cf100f9d33acd15f7c4dd3d_0.webp)


### Presets editing

![preset](https://cdn.modrinth.com/data/cached_images/c6ca54bc070eceab3693a28286d42b466013665c.png)

### Color Picker

![color](https://cdn.modrinth.com/data/cached_images/d47af1224e84cada04708e9b24e54f8e750017bc.png)

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
modApi files("C:\\Users\\UserName\\Documents\\craft_config-1.0.0+1.21.11-fabric.jar")
```



