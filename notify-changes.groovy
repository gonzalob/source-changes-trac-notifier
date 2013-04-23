#!/usr/bin/groovy
@Grab( group='commons-httpclient', module='commons-httpclient', version='3.1'   )
@Grab( group='org.apache.xmlrpc' , module='xmlrpc-client'     , version='3.1.3' )
import org.apache.xmlrpc.client.*
if( args.length < 2 ) throw new IllegalArgumentException( "This script is supposed to be invoked as an svn post commit hook. see http://svnbook.red-bean.com/en/1.4/svn.ref.reposhooks.post-commit.html" )
def repo     = args[0]
def revision = args[1]
def settings = new File( repo + "/conf/notify-changes.conf" )
if(! settings.exists()) throw new IllegalStateException( "Could not find settings file ${settings.absolutePath}" )
def config   = new ConfigSlurper().parse( settings.toURL() )
def notificationTag = config.core.tag     ?: /.*NOTIFY_CHANGE +ticket *= *(\d*).*/
def svnlook         = config.core.svnlook ?: "svnlook"

// crappy xmlrpc configuration
def tracClient = new XmlRpcClient()
tracClient.setTransportFactory(new XmlRpcCommonsTransportFactory(tracClient))
def rpcClientConfig = new XmlRpcClientConfigImpl()
rpcClientConfig.with {
 setBasicUserName(config.trac.username ?: "")
 setBasicPassword(config.trac.password ?: "")
 setServerURL(new URL(config.trac.url + "/login/xmlrpc"))
}
tracClient.setConfig(rpcClientConfig)

def changed = "${svnlook} changed ${repo} --revision=${revision}".execute()
changed.in.text.eachLine { action ->
 def file = action.substring(4)
 if(action[0] != 'D') { // cannot examine deleted files
  def contents = "${svnlook} cat ${repo} --revision=${revision} ${file}".execute()
  contents.in.text.eachLine { line ->
   def notification = line =~ notificationTag
   if( notification.matches() ) {
    def ticket     = notification.group( 1 )
    // http://trac.edgewall.org/wiki/TracLinks
    String message = "Watched file source:${file} changed in revision r${revision}."
    tracClient.execute( "ticket.update", [ ticket.toInteger(), message, [:], false ] )
   }
  }
 }
}

