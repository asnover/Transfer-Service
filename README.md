# The Official Transfer Service plugin repository to transport players between servers.
## This is a two-part plugin utilizing the API's of both the Velocity Proxy and Paper Minecraft servers. This plugin uses the Velocity and Paper API's. No other servers are supported.

Plugin features:
- Gives players a way to easily transfer between servers by walking into "portals"
- Allows forced spawn points for lobbies/hubs and set forced spawn points
- Allows for a secret key to help prevent outside interference. This is not necessary if you are using Velocity's MODERN forwarding setting.
- The `TransferService.jar` file will go in the plugin folder for your Velocity Proxy server while the `TransferClient.jar` file will go in the plugin folder of the Paper Minecraft server. A full list of commands is available by typing in `/transfer`.

After setting up your servers and configuring your proxy in your `velocity.toml` file, you will then be able set up your "portals." The way portals work is that when a player walks onto a given location, it will automatically forward said player to the server that the portal is assigned to.

To register a set of coordinates to forward a player when they walk onto them, if you are logged into the server, you must first go into edit-mode using `/transfer edit-mode` to prevent you from teleporting while registering where new coordinate sets.
![Screenshot of transfer edit](https://i.imgur.com/UdDeaaa.png)

Then you can use the `/transfer register` command to register the coordinates that you are currently standing on. You MUST use the name of the server that you used in the `velocity.toml` file. You also have the option of typing in the coordinates yourself. You may add the coordinates manually through the console if you wish.
![Screenshot of transfer registration](https://i.imgur.com/OHEvqWm.png)

And lastly, if you want players to spawn at the same spawn point every time they join the server, you can set that point by using two commands. `/transfer setspawn` to set the spawn point of where you are currently standing, and `/transfer toggleforcedspawn` to force players to spawn in the same spot every time.
![Screenshot of forced spawning command](https://i.imgur.com/OgcuxBF.png)

This plugin is still in its early stages. If you find any bugs, do not be afraid to report them by sending me a message on here or by creating an issue on github.

Paper Config File:
```yaml
#WARNING! This plugin does not offer compatibility for other worlds besides the normal vanilla overworld!
#Changes made to the server's normal world functionality will break this plugin! Compatibility will be coming in the future
#Permission list for the transfer command:
# transferclient.transfer.register
# transferclient.transfer.listservers
# transferclient.transfer.remserver
# transferclient.transfer.showcoord
# transferclient.transfer.remcoord
# transferclient.transfer.test
# transferclient.transfer.setspawn
# transferclient.transfer.toggleforcedspawn
# transferclient.transfer.editmode

#Forces the player to spawn at the same specified spawn point on every login.
forcedspawn: false
#The spawn point for forced spawn. The format is x-y-z and is in whole numbers
spawn: 0 4 0
#This will be where you put your secret key. This is an added layer of security in order to prevent others from logging
#into your server via their own methods. Put it here and do not share it with others. You can make it whatever you want.
#You only need this if you're not using the MODERN velocity protocol
secret: 'SecretKeyHere'
#This list is here for reference to a list of server coordinate data sets.
#If this list is gone or if a server is removed, data for that server will not be loaded and the data may be removed.
servers:
```

Velocity Secret Key/Config
```toml
#This is not necessary if you are running velocity in the MODERN configuration.
#This will be where you put your secret key. This is an added layer of security in order to prevent others from logging
#into your server via their own methods. Put it here and do not share it with others. You can make it whatever you want., but it is recommended you use a random password generator.
secret = "SecretKeyHere"
```

Features to come in the future:
- [x] ~~Permission Support~~ (Added in 1.1.0)
- [ ] Allow more world teleportation options than just the overworld and the default world folder.
- [ ] Add Redis support
- [ ] Add SQL support

Known Issues:
- The command `/transfer remserver` will not fully delete data from `data.yml` and must be manually removed.
