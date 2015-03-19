# Introduction #

We tried to segment the code into different layers, as the [MVC model](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) suggests.


# Architecture #

The layers are called _presentation_, _business_ and _data_. The idea is that the layers only communicate with each other via an interface and that they are stacked on each other, meaning the _data_ layer has only access to the _business_ layer and not to the _presentation_ layer and vice versa.


## Packages ##

| | Code Package | API Package | Class Naming |
|:|:-------------|:------------|:-------------|
|_presentation_| org.xbmc.android.remote.presentation | org.xbmc.api.presentation | Controller, Activity |
|_business_| org.xbmc.android.remote.business | org.xbmc.api.business | Manager |
|_data_| org.xbmc.android.remote.data | org.xbmc.api.data | Client |
|_common_| org.xbmc.android.util, org.xbmc.android.widget | org.xbmc.api.info, org.xbmc.api.object, org.xbmc.api.type|  |

The _presentation_ layer accesses the _business_ layer only by the interfaces defined in org.xbmc.api.business. Idem the _business_ layer accesses the _data_ layer only by the interfaces in org.xbmc.api.data. The necessary instances are uniquely created by the ClientFactory and ManagerFactory classes (_presentation_ layer classes are instantiated by Android).

An example of a complete workflow can be found [here](http://android-xbmcremote.googlecode.com/svn/trunk/Graphics/Diagrams/MVC.png).


## Presentation layer ##

In Android, the first entry point of an application is an Activity, which corresponds to a view in the MVC. In order to separate the GUI logic from the actual presentation code, we associate one or several Controllers to each Activity, which take are of the GUIs business logic. For instance, we have a AlbumListController, which is used by the music library activity, but also by the list-albums-by-artist- and list-albums-by-genre activity.

## Business layer ##

The business layer mainly takes care of efficiently accessing the data layer. It spawns a few threads that are used for fetching data asynchronously and provides a fast memory/sdcard cache for covers.

## Data layer ##

The data layer is the only layer not in the org.xbmc.android namespace. It can therefore be reused for any other Java application. It is also the only layer for which we'll have several implementations, since Boxee uses a different database model than XBMC. The ClientFactory class will thus instantiate the correct class based on which engine XBMC is running without any other layer noticing.



