# XBMC Configuration #

  1. Start XBMC, navigate to **SYSTEM** and select **Settings**:<br />![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/xbmc_settings.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/xbmc_settings.png)<br />
  1. Select **Network**:<br />![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/xbmc_settings_network.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/xbmc_settings_network.png)<br />
  1. Navigate to **Services** and make sure the following options are enabled:
    * Allow control of XBMC via HTTP
    * Allow programs on this system to control XBMC
    * Allow programs on other systems to control XBMC
> > ![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/xbmc_settings_network_services.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/xbmc_settings_network_services.png)<br />

Also make sure to remember the port setting (here 8080). _If any other program like uTorrent already sits on the same port, choose a different one!_

# Android Configuration #

  1. Make sure your Android device is connected to your home network via Wi-Fi.
  1. Open up the app.<br />![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_01_no_hosts.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_01_no_hosts.png)<br />You'll be notified that the app doesn't know where to connect to. Click on **Settings** in order to get to the hosts setup page. You can also navigate to it later by pressing **Menu** -> **Settings** -> **Manage XBMC Hosts**.<br />
  1. You'll see a blank page with a message how to add a new host.<br />![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_02_no_hosts_2.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_02_no_hosts_2.png)<br />
  1. Press **Menu** on your Android device and select **Add Host**:<br />![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_03_add_host.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_03_add_host.png)<br />
  1. You'll see a form where you can define how to connect to your XBMC box:<br />![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_04_add_new_host.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_04_add_new_host.png)<br />
  1. Enter a name (since you can have multiple XBMC hosts stored for fast switching). Also enter the IP address or host name of your XBMC box. **Make sure there are no spaces at the end, otherwise the remote will fail to connect.** The other settings are for advanced usage and not important for now.<br />![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_05_add_new_host.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_05_add_new_host.png)<br />Also make sure the port setting is the same as the one you've entered previously in your XBMC setup.
  1. Select **OK** and go back to the main page. You should see all the library items popping up:<br />![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_06_done.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_06_done.png)

You can switch XBMC instances by tapping on the version box and selecting a different instance:<br />
![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_07_switch.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_07_switch.png) ![http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_08_switch.png](http://android-xbmcremote.googlecode.com/svn/trunk/Documentation/Images/Installation/android_08_switch.png)

# Problems #

Please, first of all, read the [FAQ](http://forum.xbmc.org/showthread.php?t=70243), there are already answers for most of your questions. If you're having general connection problems, open up a browser on your desktop PC and type:

> `http://192.168.0.100:8080/xbmcCmds/xbmcHttp?command=GetSystemInfo(120;121)`
With your IP/port settings of XBMC of course. If that works, use the browser on your Android device for the same URL to make sure it's not a network problem. If you see a version and a date that means XBMC is running and your network is set up correctly. In that case, post your problem to the [forum](http://forum.xbmc.org/forumdisplay.php?f=129) or the [Issue Tracker](http://code.google.com/p/android-xbmcremote/issues/list). If you're getting timeouts or other connection errors, please fix them first, since they are not related to the Android app.