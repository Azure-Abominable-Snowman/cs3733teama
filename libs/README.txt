---- Team G Food Request ----

Welcom to the Team G food request API! This is an easy to use API that has many advanced features.

NOTE: There are known scaling problems, the UI was designed and confirmed to work on a 1080p screeen with a scaling percentage between 100% and 125%.
----------------------------------------------------------------------------------------------
Steps to include this API into your project:

1) Add the JAR file as a library in your project
2) Add a function that is linked to a button in your UI
3) In that function, enter the following code:

FoodRequest foodRequest = new FoodRequest();
try{
    foodRequest.run(0,0,1900,1000,null,null,null);
}catch (Exception e){
    System.out.println("Failed to run API");
    e.printStackTrace();
}

And that's it, the API should just load the next time the function is called by the button!
----------------------------------------------------------------------------------------------

About the run function:
1) the first 2 parameters are the x and y coordinates of the top left corner of the window
2) the next 2 parameters are the height and the width of the window created
	HINT: The suggested minimum height is 1000 and the suggested minimum width is 1900
3) the next parameter is the path to your CSV file, if this is NULL, we have provided a default
4) the last 2 parameters are not used