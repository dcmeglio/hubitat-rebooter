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
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	documentationLink: "https://github.com/dcmeglio/hubitat-rebooter/blob/master/README.md")

preferences {
    page(name: "prefMain")

}

def installed() {
    initialize()
}

def updated() {
	unschedule()
	unsubscribe()
    initialize()
}

def initialize() {
	if (rebootType == "Time" || rebootType == null) {
		def time = timeToday(rebootTime)
		def days = ""
		for (def i = 0; i < rebootDays.size(); i++) {
			days += rebootDays[i].substring(0,3)
			days += ","
		}
		days = days.substring(0,days.size()-1)
		schedule("00 ${time.minutes} ${time.hours} ? * ${days}", executeReboot)
	}
	else {
		subscribe(rebootButton, "pushed.1", buttonPushed)
	}
}

def buttonPushed(evt) {
	executeReboot()
}

def uninstalled() {
	logDebug "uninstalling app"
	unschedule()
	unsubscribe()
}

def prefMain() {
    return dynamicPage(name: "prefMain", title: "Reboot Configuration", install: true, uninstall: true) {
		section("") {
			input "rebootSecurity", "bool", title: "Is Hub Security enabled?", submitOnChange: true
			if (rebootSecurity)
			{
				input "rebootUsername", "string", title: "Hub Security username", required: true
				input "rebootPassword", "password", title: "Hub Security password", required: true
			}
			input "rebootType", "enum", title: "What would you like to use to trigger a reboot?", options: ["Time","Button"], defaultValue: "Time", submitOnChange: true
			if (rebootType == "Time" || rebootType == null) {
				input "rebootTime", "time", title: "Time of day to reboot", required: true
				input "rebootDays", "enum", title: "Which days should the hub be rebooted?", required: true, multiple: true, options:["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]
			}
			else if (rebootType == "Button") {
				input "rebootButton", "capability.pushableButton", title: "Button that, when pressed, will trigger a reboot/restart", required: true
			}
			input "restartInsteadOfReboot", "bool", title: "Restart the Hubitat process instead of rebooting the hub", defaultValue: true
		}
		displayFooter()
	}
}

def executeReboot() 
{
    def cookie = ""
	if (restartInsteadOfReboot)
		log.info "Restarting hub"
	else
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

def displayFooter(){
	section() {
		paragraph getFormat("line")
		paragraph "<div style='color:#1A77C9;text-align:center'>Hub Rebooter<br><a href='https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7LBRPJRLJSDDN&source=url' target='_blank'><img src='https://www.paypalobjects.com/webstatic/mktg/logo/pp_cc_mark_37x23.jpg' border='0' alt='PayPal Logo'></a><br><br>Please consider donating. This app took a lot of work to make.<br>If you find it valuable, I'd certainly appreciate it!</div>"
	}       
}

def getFormat(type, myText=""){			// Modified from @Stephack Code   
    if(type == "line") return "<hr style='background-color:#1A77C9; height: 1px; border: 0;'>"
    if(type == "title") return "<h2 style='color:#1A77C9;font-weight: bold'>${myText}</h2>"
}