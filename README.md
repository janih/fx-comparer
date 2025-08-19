# FXComparer

(Automatically exported from code.google.com/p/fx-comparer)

FXComparer is an application which lets the user select two archives and compare them either by selecting the archive using a "file-chooser" or by "drag & drop" a file into the application. The result of the compare is presented in an easy to read table and instant feedback of the differences in the archives are presented for the users.

There are some blog-post connected to FXComparer which describes some steps of the development of the application.

1. [Defining the project, setup using Eclipse and Maven.](http://www.loop81.com/2013/04/project-fxcomparer-part-1-defining.html)
2. [Building the UI using FXML and JavaFX Sceen Builder.](http://www.loop81.com/2013/04/project-fxcomparer-part-2-building-ui.html)
3. [Handling input and showing results using a TableView.](http://www.loop81.com/2013/04/project-fxcomparer-part-3-handling.html)
4. [Packaging the application.](http://www.loop81.com/2013/04/project-fxcomparer-part-5-packaging.html)

## How to compile and run

    mvn compile javafx:run

Tested with openjdk 21.0.5 2024-10-15 LTS.