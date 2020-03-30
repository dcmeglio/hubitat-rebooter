/**
 *
 *  Rebooter
 *
 *  Copyright 2019-2020 Dominick Meglio
 *
 *	If you find this useful, donations are always appreciated 
 *	https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7LBRPJRLJSDDN&source=url
 *
 * Revision History
 * v 2020.03.30 - Added an option to restart the Hubitat process instead of rebooting the hub
 */
 
definition(
    name: "Rebooter",
    namespace: "dcm.rebooter",
    author: "Dominick Meglio",
    description: "Restart your hub on a schedule",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    page(name: "prefMain")

}

def installed() {
    initialize()
}

def updated() {
	unschedule()
    initialize()
}

def initialize() {
	def time = timeToday(rebootTime)
	def days = ""
	for (def i = 0; i < rebootDays.size(); i++) {
		days += rebootDays[i].substring(0,3)
		days += ","
	}
	days = days.substring(0,days.size()-1)
    schedule("00 ${time.minutes} ${time.hours} ? * ${days}", scheduledReboot)
}

def uninstalled() {
	logDebug "uninstalling app"
	unschedule()
}

def prefMain() {
    return dynamicPage(name: "prefMain", title: "Reboot Configuration", install: true, uninstall: true) {
		section("") {
			input("rebootSecurity", "bool", title: "Is Hub Security enabled?", submitOnChange: true)
			if (rebootSecurity)
			{
				input("rebootUsername", "string", title: "Hub Security username", required: true)
				input("rebootPassword", "password", title: "Hub Security password", required: true)
			}
			input("rebootTime", "time", title: "Time of day to reboot", required: true)
			input("rebootDays", "enum", title: "Which days should the hub be rebooted?", required: true, multiple: true, options:["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"])
			input("restartInsteadOfReboot", "bool", title: "Restart the Hubitat process instead of rebooting the hub", defaultValue: true)
		}
	}
}

def scheduledReboot() 
{
    def cookie = ""
	log.info "Rebooting hub"
    if (rebootSecurity)
    {
		httpPost(
			[
				uri: "http://127.0.0.1:8080",
				path: "/login",
				query: 
				[
					loginRedirect: "/"
				],
				body:
				[
					username: rebootUsername,
					password: rebootPassword,
					submit: "Login"
				]
			]
		)
		{ resp ->
            cookie = resp?.headers?.'Set-Cookie'?.split(';')?.getAt(0)
        }
	}
	
	def rebootPath = "/hub/reboot"
	if (restartInsteadOfReboot)
		rebootPath = "/hub/restart"
	httpPost(
		[
			uri: "http://127.0.0.1:8080",
			path: rebootPath,
			headers:
			[
				"Cookie": cookie
			]
		]
	) 
	{
		resp ->
	}      
}