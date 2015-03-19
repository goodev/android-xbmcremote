# How we use the threads #

What we have is a client interacting with a server by HTTP. There are lots of different API commands, which are kept in four groups: _Control_, _Info_, _Music_ and _Video_. Each API command takes different arguments and can return different object types. The goal is an asynchronous approach that doesn‘t block the GUI while data is being fetched.

Instead of spawning a thread on each command, we decided to keep one single thread, HttpApiThread. It loops indefinitely and keeps a handler which executes the Runnable objects that are passed to it.

Obviously we can‘t just implement the API comands as methods with their return value as result, then it wouldn‘t be async. From the GUI thread, we pass a [HttpApiHandler](http://code.google.com/p/android-xbmcremote/source/browse/trunk/XBMC+Remote/src/org/xbmc/android/backend/httpapi/HttpApiHandler.java) object to the [HttpApiThread](http://code.google.com/p/android-xbmcremote/source/browse/trunk/XBMC%20Remote/src/org/xbmc/android/backend/httpapi/HttpApiHandler.java), which is runnable and implements the code to be run at completion. `HttpApiHandler` must be of one type `T`, which is the return type of the API command. It holds the returned result in the `value` member of the class.

The four wrapper classes the `HttpApiThread` holds contain the wrapper methods to the real HTTP API clients.

![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Code/Diagrams/HttpAPI.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Code/Diagrams/HttpAPI.png)
Click [here](http://android-xbmcremote.googlecode.com/svn/trunk/Graphics/Diagrams/HttpAPI.pdf) for the PDF version.

That's what we decided based on our limited Android experience. If you're an experienced developer and know better, please let us know via the [forum](http://xbmc.org/forum/showthread.php?t=55346) or the [issue tracker](http://code.google.com/p/android-xbmcremote/issues/list). :)