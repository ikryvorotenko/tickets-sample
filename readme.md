Sample application for selling tickets

### CLI

The cli application is represented as a single-run-application, which print the result once for provided parameters.

The app contains following components:
- `Params`: used for parsing and validating incoming pareters
- `ShowsParser`: used for parsing provided csv file with shows
- `ShowsService`: used for querying shows with provided query/show dates. `ShowsService` also contains the encoders for correct shows json serialization.
- `Domain`: used for domain entities definition and related logic description

Run CLI application: `./gradlew cli:run --args='{path-to-csv} {query-date} {show-date}'` ,
where 
- `path-to-csv`: absolute path to csv file with shows. and the columns consist of the title, opening day (the day of the first performance of a show), and genre of the show.
- `query-date`: the reference date that determines the inventory state
- `show-date`: the date for which you want to know how many tickets are left

### Web

The web application is pretty simple and can be considered only as a pilot for testing business idea. In case of further development, the UI and integration should be redesigned.

The app accepts the shows csv file as an input parameter and uses it as in-memory storage.

Run Web application: `./gradlew web:run --args='{path-to-csv}'`, where
- `path-to-csv`: absolute path to csv file with shows. and the columns consist of the title, opening day (the day of the first performance of a show), and genre of the show.

By default the app is running on [http://localhost:8080](http://localhost:8080)
