#Pants
The worst Bukkit plugin you could ever install. (*Malicious AF*)

##What is Pants?
Pants is a client & server program that allows you to take control of someone's Bukkit Server. You can run console commands, modify local files, or just wipe the whole thing.

##How does it work?
Pants is split up into two parts, the *Client* and the *Server*. The Client is secretly embedded into a Poison Plugin that you can give to the owner of the server you want access to. When the plugin starts up, it will silently create a thread that will try to connect to your Server. Once you boot the Server up, they can talk. You can get information like installed plugins, a list of operators, and other stats needed for malicious activities. You can also send the Client different commands to do fun stuff, like disable certain plugins or even wipe all the worlds!

##When can I use it?
Pants has only recently begun development, so don't expect anything too amazing right this instant. It can do some cool stuff, like OP anyone you'd like while filtering out any messages that may appear in the console, or just remotely run console commands. Right now I'm getting the connections ready to go, then I'll move on to malicious features.

##What if someone decompiles my plugin?
The Pants Client was made to be completely confusing to anyone who may decompile it. The server owner would need to know pretty advanced Java to figure out what the heck the thing did. It's also only one class, which could be nothing in a plugin with tons of classes, like a minigame. There are no stray *setOp(true);*s anywhere, or anything of that sort. Everything has been highly obfuscated to look like very complex code to the normal observer. If they've got a PhD in Computer Science, then you might want to worry a little bit.

##Why u do dis?
**trololololz**
