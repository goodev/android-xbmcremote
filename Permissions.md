We don't like apps demanding permissions that don't seem obvious, so here we'll explain each permission XBMC Remote asks prior to installation:

|_INTERNET_|We need to connect to XBMC. The _INTERNET_ permissions actually controls any socket, internet or not, so this is unavoidable.|
|:---------|:----------------------------------------------------------------------------------------------------------------------------|
|_ACCESS\_NETWORK\_STATE_, _ACCESS\_WIFI\_STATE_, _CHANGE\_WIFI\_STATE_|We've introduced an option that avoids connecting to XBMC when not connected to WiFi. In order to check this we need this permissions. |
|_VIBRATE_|Remote control screen lightly vibrates to give a more realistic user experience (configurable).|
|_READ\_PHONE\_STATE_|We have a feature that pauses anything playing on incoming calls. In order to receive this event, we need this permission. |
|_RECEIVE\_SMS_|The feature that displays SMS on the TV screen needs this permission in order to obtain the messages. |
|_READ\_CONTACTS_|In order to display contact info (and picture) on incoming calls or messages, we need permission to read the phone book.|
|_READ\_SMS_|When displaying SMS, we actually display the first part of the message, so we'll need read permissions of SMS.|
|_WAKE\_LOCK_, _DISABLE\_KEYGUARD_|A requested feature was overwriting the power manager to keep the processor from sleeping or the screen from dimming. This is configurable, but we'll need the permissions in any case (activated or not).|
|_WRITE\_EXTERNAL\_STORAGE_|In order to save cover and poster thumbnails locally for caching purpose, we need write access to your SD card. This permission was introduced with Android 1.6.|
|_RECEIVE\_BOOT\_COMPLETED_|Feature that makes the remote being launched at system bootup. The feature is optional and turned off by default.|