#Contributing to **Pants**

##I found a bug / I have a feature request
Make an issue detailing the bug along with steps to repeat it. Feature requests will take time to complete, and make sure there isn't already an issue open with your idea.

##I want contribute code
The code structure is a little messy at the moment, but it will get cleaned up eventually. *src/main/* contains all the implementations, where you can find different ones made in different languages.

###Client
The Pants Client runs parallel with the Bukkit Server that is is installed on. It can interact with the Bukkit Server using the Bukkit API. This is how we can send commands to the Client and have them run on the Bukkit Server. This is and will only be written in Java.

###Server
The Pants Server is on a computer that is separate from the computer hosting the Bukkit Server. It can interact with the Bukkit Server by sending messages to the Pants Client. It does not involve any Minecraft Client, so commands sent to the Client can't be traced back to a player.
