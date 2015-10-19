#Contributing to **Pants**

##I found a bug / I have a feature request
Make an issue detailing the bug along with steps to repeat it.

##I want contribute code
The basic directory format:
* *dev/*
  * Pseudocode/Research
* *client/*
  * Code for the Pants Client on the Bukkit Server
* *server/*
  * Code for the Pants Server

###Client
The Pants Client runs parallel with the Bukkit Server that is is installed on. It can interact with the Bukkit Server using the Bukkit API. This is how we can send commands to the Client and have them run on the Bukkit Server.

###Server
The Pants Server is on a computer that is separate from the computer hosting the Bukkit Server. It can interact with the Bukkit Server by sending messages to the Pants Client. It does not involve any Minecraft Client, so commands sent to the Client can't be traced back to a player.
