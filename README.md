# source-changes-trac-notifier #
An SVN hook to check commited files for a tag, and update a Trac ticket when it appears.

# REQUIREMENTS #
 * An [SVN](http://subversion.tigris.org/) repository
 * A [Trac](http://trac.edgewall.org/) installation with XMLRPC, and a user with TICKET_MODIFY and XMLRPC permissions on it
 * [Groovy](http://groovy.codehaus.org/)

# INSTALLATION #
Call this script from the repository's post-commit hook.

# CONFIGURATION #
Settings for this script must be installed in the repository's conf directory. It must be a [valid Groovy config file](http://groovy.codehaus.org/ConfigSlurper) (a Java Properties file will do)

## Required parameters ##
 * trac.url      : Root URL for the trac installation
 * trac.username : Username to authenticate against trac
 * trac.password : Password for the specified user

## Optional parameters ##
 * core.tag      : Tag to look for. See the section called TAG for details
 * core.svnlook  : Path to the svnlook utility. Defaults to none (i.e. will be searched for in $PATH)

# TAG #
The default tag is *NOTIFY_CHANGE ticket=(ticket id)*. If customized, the parameter core.tag must contain
a regular expression with a single capture group for the ticket's id.

# LICENSE #
This software is distributed under the [GPLv2](http://www.gnu.org/licenses/gpl-2.0.html).

