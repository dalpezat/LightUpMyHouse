/**
 *  LightUpMyHouse
 *
 *  Copyright 2017 Tony DalPezzo
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "LightUpMyHouse",
    namespace: "dalpezat",
    author: "Tony DalPezzo",
    description: "This app is for when we come home after the outside lights have turned off.  I want then to turn on and then turn off after a set time.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	    section("Turn on switches"){
            input "switches", "capability.switch", multiple: true, required: true
    }
    section("For Who"){
        input "people", "capability.presenceSensor", multiple: true, required: true
    }
    section("Turn off after"){
        input "minutes", "number", range: "1..*", title: "Minutes", required: true
    }
    section("Active between what times?") {
            input "fromTime", "time", title: "From", required: true
            input "toTime", "time", title: "To", required: true
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
subscribe(people, "presence", presence)
}

def turnOff() {
	switches.off()
}

def presence(evt) {
    log.debug "evt.name: $evt.value"
    if (evt.value == "present") {
        def between = timeOfDayIsBetween(fromTime, toTime, new Date(), location.timeZone)
        if (between) {
        switches.on()
            runIn(minutes*60, turnOff)
        } else {
        	//do nothing
        }
     }
    else {
        log.debug "Nothing to do"
    }
}
