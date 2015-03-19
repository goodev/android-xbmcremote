As reported in [issue 136](http://code.google.com/p/android-xbmcremote/issues/detail?id=136) Key repeats in remote control mode can be pretty fast.

### Fix/Workaround ###


  * create advancedsettings.xml in your userdata folder (where also guisettings.xml is located)
  * edit its content:

```
<advancedsettings>
    <services>
    	<esinitialdelay>750</esinitialdelay>
    	<escontinuousdelay>25</escontinuousdelay>
    </services>
</advancedsettings>
```
  * the values above are the default values. Change how you like.

If you're using an old build of XBMC, the settings are probably named differently:

```
<advancedsettings>
    <remoteevents>
    	<initialdelay>750</initialdelay>
    	<continuousdelay>25</continuousdelay>
    </remoteevents>
</advancedsettings>
```