Sample application for selling tickets

Run CLI application: `./gradlew cli:run --args='{path-to-csv} {query-date} {show-date}'` ,
where 
- `path-to-csv`: absolute path to csv file with shows. and the columns consist of the title, opening day (the day of the first performance of a show), and genre of the show.
- `query-date`: the reference date that determines the inventory state
- `show-date`: the date for which you want to know how many tickets are left

Run Web application: `./gradlew web:run --args='{path-to-csv}'`, where
- `path-to-csv`: absolute path to csv file with shows. and the columns consist of the title, opening day (the day of the first performance of a show), and genre of the show.

By default the app is running on [http://localhost:8080](http://localhost:8080)
