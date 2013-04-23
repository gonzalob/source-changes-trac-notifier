# source-changes-trac-notifier #
An SVN hook to check commited files for a tag, and update a Trac ticket when it appears.

# REQUIREMENTS #
 * An [http://subversion.tigris.org/](SVN) repository
 * A [http://trac.edgewall.org/](Trac) installation with XMLRPC, and a user with TICKET_MODIFY and XMLRPC permissions on it
 * [http://groovy.codehaus.org/](Groovy)

# INSTALLATION #
Call this script from the repository's post-commit hook.

# CONFIGURATION #
Settings for this script must be installed in the repository's conf directory. It must be a [http://groovy.codehaus.org/ConfigSlurper](valid Groovy config file) (a Java Properties file will do)

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
This software is distributed under the [http://www.gnu.org/licenses/gpl-2.0.html](GPLv2).

