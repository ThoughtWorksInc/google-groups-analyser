# Google Group Stats

Processes a named google group and provides statistics about the posts and authors.
It processes atom feeds too but Google limit that to the last 100.

## Usage

If you don't already have it, please install https://leiningen.org/ (also needs Clojure and Java 8+)

From the root dir of this project

    lein run < google-group-forum-url> <no-of-pages-to-crawl> <optional-path-to-cookies>

## Options

None yet...

## Examples

    lein run https://groups.google.com/forum/?_escaped_fragment_=forum/ubu-comp-sci-masters-project-group-4 2 

### Testing

    lein test

## License

Copyright © 2020 ThoughtWorks

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
