# Dogify
This app simply fetches information about dog breeds from [here](https://dog.ceo/dog-api/documentation/) and displays them in a grid.
## Data
A retrofit service `DogService` is already set up for fetching all the breed names and a random image for a given breed.
## UI
It has only one Activity `MainActivity` which loads a list of dog breeds from the network and displays them in a grid.
In the onCreate function we set up the recyclerView and kick off an asyncTask to load the data.
In the doInBackground function of the async task we create a retrofit service and call getBreeds to get the breed names.
Then in onPostExecute we add them all to the adapter and which binds them to the view.