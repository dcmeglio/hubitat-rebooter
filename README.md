# hubitat-rebooter
This app allows you to reboot your hub on a scheduled basis. Currently some Hubitat users are experiencing slowdowns after the hub has been running for a long time. This app will automatically reboot the hub (or restart the Hubitat process) on specified days to help with the issue.
 
## Apps
Install the __Rebooter__ app.

### Configuration
The app supports both hubs that use Hub Security and those that do not. If you are using hub security you will need to supply the username and password of an administrative user for your hub.

The app lets you configure when the reboot/restart should occur. You should pick a time when you're not running any automations and also when backups are not scheduled. Some users have experienced slowdowns after varying lengths of time. As a result, you can specify which days of the week the app should run. A recommendation would be to run the app as infrequently as possible.

Through some debugging with the Hubitat team, an option was added to restart the Hubitat process instead of reboot the hub. Restarting the process is faster and it will also help the Hubitat team narrow down the issue. Please attempt to use the restart option first and only use the reboot feature if restarting doesn't seem to help. If restarting does not help, please post on the forum so the Hubitat team is aware.

## Donations
If you find this app useful, please consider making a [donation](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7LBRPJRLJSDDN&source=url)! 

## Revision History
* v 2020.03.30 - Added an option to restart the Hubitat process instead of rebooting the hub
