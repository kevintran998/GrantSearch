***** DESCRIPTION ***** 

Grant Search utilizes grants.gov RSS feed data to provide a list of grants to the userbased on the user's search criteria.

***** PROGRAMING LANGUAGE AND TOOLS *****
- Java
- Android Studio

***** FEATURES ***** 
- Welcome screen with logo
- Search/Filter functionality using onClick buttons and user input
- Reads data from grants.gov RSS feed
- Converts RSS feed to a text document
- Date-Picker Fragment
- Async-Task
- RecyclerView Adapater
- Connects to internet to display original grant webpage


***** HOW IT WORKS ***** 

When the app is opened, the screen displays the app logo then transitions to a tap-to-start screen. The user is given a set of filtering options with an input search bar, buttons, and date-picker. Once the user inputs their desired filters, the app will connect to the internet and gather information from the grants.gov RSS feed. Then, the application will pull out information on each grant and place the info into objects. The grant objects are held within an array list which is then filtered based on the user's initial inputs. The app displays the filtered list onto the screen when finished. The user is given the option to scroll through the list and click on each grant for more information.
