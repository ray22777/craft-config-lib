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
Add to repositories
```
repositories {
    maven {
        url = "https://api.modrinth.com/maven"
    }
}
```

Then in your dependencies include this
```
dependencies {
    implementation("maven.modrinth:craft-config:<version_name>")
}
```

Replace <version_name> with the one that is suitable.

You can find the version names at [https://modrinth.com/mod/craft-config/versions](https://modrinth.com/mod/craft-config/versions)


