#!/usr/bin/groovy
@Grab( group='org.codehaus.groovy', module='groovy-xmlrpc', version='0.8' )
import groovy.net.xmlrpc.*
if( args.length < 2 ) throw new IllegalArgumentException( "This script is supposed to be invoked as an svn post commit hook. see http://svnbook.red-bean.com/en/1.4/svn.ref.reposhooks.post-commit.html" )
def repo     = args[0]
def revision = args[1]
def settings = new File( repo + "/conf/notify-changes.conf" )
if(! settings.exists()) throw new IllegalStateException( "Could not find settings file ${settings.absolutePath}" )
def config   = new ConfigSlurper().parse( settings.toURL() )
def notificationTag = config.core.tag     ?: /.*NOTIFY_CHANGE +ticket *= *(\d*).*/
def svnlook         = config.core.svnlook ?: "svnlook"

def pendingNotifications = [:].withDefault { [] }
def changed = "${svnlook} changed ${repo} --revision=${revision}".execute()
changed.in.text.eachLine { action ->
 def file = action.substring(4)
 if(action[0] != 'D') { // cannot examine deleted files
  def contents = "${svnlook} cat ${repo} --revision=${revision} ${file}".execute()
  contents.in.text.eachLine { line ->
   def notification = line =~ notificationTag
   if( notification.matches() ) {
    def ticketNumber = notification.group( 1 )
    pendingNotifications.get( ticketNumber ) << file
   }
  }
 }
}

def tracClient = new XMLRPCServerProxy(config.trac.url + "/login/xmlrpc")
def tracUsername = config.trac.username ?: ""
def tracPassword = config.trac.password ?: ""
tracClient.setBasicAuth tracUsername, tracPassword
// constants for fields in the ticket type in trac's xmlrpc api. see http://trac-hacks.org/wiki/XmlRpcPlugin#APIUsage
def TICKET_ID    = 0
def TIME_CHANGED = 2
pendingNotifications.each { ticketNumber, sources ->
    def ticket     = tracClient.ticket.get ticketNumber
    // http://trac.edgewall.org/wiki/TracLinks
    def sourceLinks = sources.collect { "source:" + it }.join(', ')
    String message = "Watched file(s) ${sourceLinks} changed in revision r${revision}."
    def attributes = [ 'action': 'leave', '_ts': ticket[TIME_CHANGED] ]
    tracClient.ticket.update( ticket[TICKET_ID], message, attributes, false )
}
