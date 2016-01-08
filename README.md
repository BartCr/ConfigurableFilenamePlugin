# ConfigurableFilenamePlugin
An Intellij IDEA plugin for configurable filenames.

This allows developers in a team to define a template for filenames when a common pattern is needed for certain types of files.


Available template variables:

| Variable | Description                                                                                                                                                        |
| -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| ${NAME}  | name of the new file specified by you in the 'New' dialog                                                                                                          |
| ${USER}  | current user system login name                                                                                                                                     |
| ${NOW}   | current system date. The formatting for the date can be specified by a format string after a semicolon (${NOW;FORMAT}). The default format is yyyy-MM-dd_HH-mm-ss. |
