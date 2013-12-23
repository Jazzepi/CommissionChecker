CommissionChecker
=================

Check to see when artists are open for commission. Works with a few sites right now.


Requirements
------------

You need Java 1.6 or higher installed. You can check this by opening a command prompt and typing "java -version".


Quickstart
----------

1. Download the zip file, and unzip everything into a single directory. https://github.com/Jazzepi/CommissionChecker/raw/master/CommissionChecker.zip
2. Configure the checker.properties file with authentication information and what artists you're interested in watching. The checker uses the email information to send you an email notification when it finds a new commission open. You can edit this file with any simple text editor. Notepad will do. Do not use something like Microsoft Word. Set the any sites you want to be checked for commissions to "true".
3. Run the jar by double clicking on it.


Optional
--------

1. You can setup a script to run the commission checker every time you run in by modifying the included .bat file. Point it at your commission-checker.jar and then put the .bat file in your Startup folder. It will run every time you startup windows, with nothing but a system tray icon.


*Warning*
-------

* I haven't tested this on any STMP service besides Gmail. You're on your own with Yahoo/Hotmail/etc.
* You use this program at your own risk.
* Your password/username will be in clear text on your hard drive. Think about that and understand the security implications before you use this program. You should not run this program on a shared computer that other people might have access to.


Developers
----------

I accept pull requests. Especially for implementing watching on a new site. The checker.properties file is the "default" canonical one. If you pull down the project, create your own development based property file and set an environment property dev_checker_properties_file_name to the name of that file. CommissionChecker will look for the property file of that name in the same directory as the jar.

*Do*
* Write unit tests
* Stick with the conventions that are already there
* Use a website's API if possible

*Don't*
* Add another mocking framework
* Submit a pull request without proper unit test coverage
* Submit a pull request that adds XML bean configuration
* Write to Java 1.7 or higher

To add the capacity for watching a new site for commissions do the following:
1. Implement a new site class extending the CommissionWebsite class
2. Add the appropriate properties in the checker.properties
3. Add an entry to the activeCommissionWebsites in the AppConfig class
4. Add three new beans in the AppConfig, one for the username, the password, and the watched user list.
