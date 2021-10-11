# Overview

## Intro to Maven
We are using Maven to build our projects since it saves us the hassle of manually downloading all the dependencies and we can update dependencies very easily later on by just changing one file - the pom.xml file. In Maven, this is the build options file that manages our dependencies and takes care of any other tasks we give it. If you have questions on how it works feel free to ask me, but there is not a huge point in me creating an in depth tutorial on the ins and outs of Maven for our project currently.

## How JavaFX Works
JavaFX uses a scene graph that paints components onto the screen. The lowest form of a visible object is the `Node` class. The `Node` class should not be extended in the vast majority of cases. To be honest I have not done it to date, but I am fairly certain it can be done.
JavaFX uses Properties and Bindings to separate data and code - UI components will be automatically updated when the state of a variable changes provided we use this API. It is necessary that we do a great job working with this if we want an easy to manage system - I will create a tutorial on this shortly (as of 10/11/2021 no tutorial). There are plenty of online tutorials as well.

JavaFX starts a JavaFX Application Thread after calling one of a few `launch()` methods from a subclass of `Application`. After initialization JavaFX calls that subclasses overridden `start(Stage)` method, providing the primary Stage for the application. The `Scene` can be created either in plain java or in FXML using the `FXMLLoader` class - which we will be taking advantage of. JavaFX `Node`s can be styled using style sheets with special JavaFX CSS grammer which we will also use.

## Our Application
So with how it is currently setup on our configuration, our application starts in the App class. This class extends Application, and later on we may launch externally from a different class but for now it includes a main method that launches JavaFX. Any JavaFX application must be lanched before most of the JavaFX Objects can be used (visible graphics etc.). In the basic App class we have the following:
```
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240); // Parent, width, height
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
```
In JavaFX when we launch the Application, JavaFX creates an object of our classes type. _*This **requires** that we have a default constructor (one with no arguments) for our `App` class._ After that JavaFX does its initialization and then calls our classes overridden `start(Stage)` method. This passes to us an instance of the primary `Stage` for our application. 
After that we load the contents of that Stage using `FXMLLoader`. The `FXMLLoader` is created and in this case initialized with the resource that it reads from. We then use the `load()` method to obtain the `Parent` object to provide our `Scene`.

### FXML
Now I want to look at the FXML file. In our Application it is under our module resources named **Controller.fxml**.
~~~ 
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Button?>

<VBox alignment="CENTER" spacing="20.0" xmlns:fx="http://javafx.com/fxml"   <- Root node
      fx:controller="com.sdc.tthree.sdcide.Controller">
    <padding>
        <Insets bottom="20.0" left="20.0" right="20.0" top="20.0"/> <-- This can alternatively be defined in our css file
    </padding>
    <children> <------------------------------------------------------- Default Property (discussed below)
      <Label fx:id="welcomeText" id="textExample"/>  <----------------- Label with fx:id for injection
      <Button text="Hello!" onAction="#onHelloButtonClick"/>
    </children>
</VBox>
~~~
As you can see FXML is XML based and follows a similar format using angle brackets for delimiters. Note that each follows the format (besides special `<?command data>`) of `<Object [properties]/>` or `<Object [properties]></Object>`. When using the `<></>` notation properties can be specifed using angled brackets as shown for the padding property above. Some properties like padding need to be specified in this way rather than the `property="data"` format in the header.

The first line provides basic encoding and document type. The next lines are import statments. In FXML when we specify what FXML objects to add we need to import them. Note that these imports include our own custom classes (more on that below). _Intellij is a blessing for imports_.

In this FXML file our root (outermost) Node is the `VBox`. The root Node has some special properties that we can / need to specify. The first is the **xmlns:fx**. This is necessary if we plan on utilizing _injection_ to populate our Controller class with `fx:id` named objects (discussed later).
**Note that `fx:id` is different than `id`! `id` is used with CSS to define a particular nodes style**.
Additionally the controller is specified with the `fx:controller` property (which as you see requires the `xmlns:fx`). This is optional for use with controllers, but when we define it in the FXML file, a controller class is instantiated automatically by JavaFX. If we leave this line out, we need to set the controller of the `FXMLLoader` before calling `load()`. Note that this is a nice way of getting dynamic controller classes or if we need a controller class without a default constructor. In that case we would simply instantiate the controller manually, call `FXMLLoader`'s `setController(Object)` passing our controller and then call `load()` as usual.

#### Default Property
In FXML each class has a default property that when no property is specified is modified by the `FXMLLoader`. This default property is very commonly used for container objects such as `VBox` in the example above. In the example I included this default property for the sake of explaination, but the more typical way to write the above FXML knowing that most container objects default property is the children property is as follows:
```
<VBox alignment="CENTER" spacing="20.0" xmlns:fx="http://javafx.com/fxml"   <----- Root node
      fx:controller="com.sdc.tthree.sdcide.Controller">
    <padding>
        <Insets bottom="20.0" left="20.0" right="20.0" top="20.0"/>
    </padding>
    <Label fx:id="welcomeText" id="textExample" styleClass="exampleClass"/>
    <Button text="Hello!" onAction="#onHelloButtonClick"/>
</VBox>
```
As you can see we got rid of the `<children>` and `</children>` headers since the default property makes this redundant.

#### Subclassing / Custom Objects
FXMLLoader is very robust and can create any JavaFX object providing it has a default constructor. We can take advantage of this by subclassing JavaFX objects so we can create our own custom objects. In JavaFX subclassing is the primary way of implementing custom objects. Think perhaps a control that incorporates multiple other controls - such as subclassing a  `VBox` and then adding a `TextArea` and `Button` to it in its default constructor etc.

### Controller
JavaFX objects have all sorts of properties which I will create a tutorial for later.
These properties can be initialized using FXML as seen in the example code above, of which I want to cover two things. To start, lets look at our last beginning class, the `Controller` class.
```
public class Controller {
    @FXML
    private Label welcomeText;

    @FXML
    private void initialize() {
        // TODO
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
```
There are three things to cover here:
1. Function calls
2. Injection
3. Instantiation Path

#### Function calls
For function calls, as seen in the 'onAction' property being set in our FXML file, we reference our controller's methods using the format `"#methodName"`. Note that in our controller class this method should be annotated with the `@FXML` as seen above. Our function calls can either take no arguments, or take a JavaFX `ActionEvent` parameter that is filled by the `FXMLLoader` automatically when the method is called.

##### Injection
For injection, we provide the `fx:id` property with a **unique** name prefaced with `@FXML`. Note that the object needs to be named exactly the same in the controller class, again as seen above. 
You will notice that the nice thing about injection is that we can have our fields be private and our methods private as well! This is very nice for seperation / abstraction.

##### Instantiation Path
When our controller class is called by `FXMLLoader`, the first thing that happens is the default constructor is called to create the object. At this point all of our fields such as `Label welcomeText` will be null! Luckily there is a no argument method that `FXMLLoader` checks for called `initialize()` that it automatically calls if it exists after the injection process. As you can see it also is annotated with `FXML`. Having this method is not a requirement of a controller class, but in most cases will be necessary. At this point we can work with the injected fields which we likely will want to do some setup with.

See the below FXML resources for more information.

### CSS
We have talked about the basics of everything except JavaFX CSS. Our code from earlier is only going to get a bit more involved.
I have made a few modifications to the `App` class as seen here:
```
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = fxmlLoader("Controller.fxml");
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        addDefaultStylesheet(scene);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static FXMLLoader fxmlLoader(String resource) {
        return new FXMLLoader(resourceURL(resource));
    }

    public static void addDefaultStylesheet(Scene scene) {
        scene.getStylesheets().add(resourceURL("stylesheet.css").toExternalForm());
    }
    
    public static URL resourceURL(String resource) {
        URL url = App.class.getResource(resource);
        if (url == null) throw new NullPointerException(String.format("Resource \"%s\" was not found", resource));
        return url;
    }
}
```
All I have done is added some checks for null resources - these should be fatal errors here since if we cannot load the UI there is not much for the user to do. We should never have a case where a NPE is acceptable. _Catching NPE is considered bad code practice_. 

I also added a static method for getting the FXMLLoader (conveinent), and a method for adding the stylesheet. Each `Node` that has a newly created `Scene` needs to have this stylesheet added, which includes JavaFX objects like the `Alert` class and `Dialog` class, which is why this method is useful. The general strategy for designing these types of classes will be to create a subclass that extends one of these and then sets up all of the methods inside (see Alert and Dialog section), loading the content with the `FXMLLoader`. Of course if it is a very basic class then the `FXMLLoader` is not necessary. I digress. 
The only actual new thing here is the `addDefaultStylesheet(Scene)` method.

Lets take a look at one line from our FXML file...

`<Label fx:id="welcomeText" id="textExample" styleClass="exampleClass"/>`

As you can see this Label has the `fx:id`, `id`, and `styleClass` properties set. In JavaFX `Node` objects `id` and `styleClass` property corresponds to the CSS styles that a particular object has. Note that each `Node` has a `setStyle(String)` method that allows inline styles directly in code. Additionally this can be done in FXML with the `style` property. However it is best practice to seperate CSS from our code base which is why we are using this external stylesheet approach.
JavaFX CSS cascades from the stylesheets added to a `Node`'s stylesheet `ObservableList<String>` accessible via the `getStyleClass()` method. Every `Node` starts off with the true default stylesheet ("caspian.css"). Stylesheets added to this list are overridden by sheets later in the list, so if we want to override caspian.css styles for a given class we just need to add the stylesheet (since this naturally appends the `String` to the end of the list).
The priorities for a `Node`'s CSS is StyleClass < ID < inline style.
In JavaFX CSS there are two selector types:
1. Class
2. ID

The class selector is a '.' follwed by the class name followed by optional subclasses for specification followed by brackets like normal CSS.
The id selector is a '#' followed by the id name followed by brackets. Here is the example CSS sheet I have filled a bit in for demonstration:
```
// From reference
.root {
    -fx-font-size: 16pt;
    -fx-font-family: "Courier New";
    -fx-base: rgb(132, 145, 47);
    -fx-background: rgb(225, 228, 203);
}

#textExample {
    -fx-font-size: 8pt;
    -fx-text-fill: red;
}

.exampleClass {
    -fx-underline: true;
}
```
The **root** class is an example of a class override, since the stylesheet that JavaFX uses as default has a root styleclass that we change here. It is the base skin for our JavaFX Application that we can style to have a uniform style among all of our components. The `#textExample` is an example of an id selector, and the `.exampleClass` is an example of a class that we defined on our own. 
Just like in normal CSS, a `Node` can have many style classes, but only one id. Unlike normal CSS, id's are not required to be unique.
Not every Node type supports all JavaFX CSS properties - see the official JavaFX CSS Reference for specifics.
- If you are interested in state CSS (hovering, selected, etc) check out psuedo classes in JavaFX CSS in the reference.
- If you want to know more about subclassing and specifics such as `.overiddenClass .subclass {//content}` you should see the reference as well.

### Alert and Dialog
The JavaFX `Alert` and `Dialog` class have there own scenes and stage, and as the name suggests act as modal pop ups that require the user to complete before returning. The best practice for clean code is to create a subclass to design. For example, consider the case you want to create an dialog that prompts the user to save their workspace before exiting the application...
```
public class ExitAlert extends Alert {

  public ExitAlert() {
    super(AlertType.CONFIRMATION);
    // design code
    App.addDefaultStylesheet(getDialogPane().getScene()); // check method path
    setHeaderText("Exit And Save?");
    setContentText("Click \"OK\" to exit and save your workspace.");
    setTitle("Exit And Save");
  }

}
```
For dialogs it would be something similar but it is a bit more involved since you are likely to return something. In that case it may look like the following:
```
  public class FormDialog extends Dialog<Form> {
  
  @FXML private TextField name;
  @FXML private CheckBox isMale;
    
    public FormDialog() {
      super();
      // see Alert for similar design API calls
      setResultConverter(this::formResult); // lookup method lambdas if this confuses you. Alternatively use (button -> {/*no method just do conversion here*/}); 
                                        // or (button -> formResult(button)); LOVE Lambdas, they are the best thing since sliced bread.
      // could use FXMLLoader here as so...
      FXMLLoader loader = App.fxmlLoader("formDialog.fxml"); // assuming existence etc
      loader.setController(this); // set this object as controller so that resultConverter can utilize controller injected fields etc.
      getDialogPane().setContent(loader.load());
    }
    
    private Form formResult(ButtonType button) {
      // process code
      if (button == ButtonType.OK)
        return new Form(name.getText(), isMale.getSelected());
      return null;
    }
  }
```
Of course I am being very brief here - I just want to expose you to this a bit to give you a direction.
The API for Dialog is that if you subclass it you **must** set the `ResultConverter`. Failure to do so will result in a not only nearly useless dialog but also non functional one. See the JavaFX documentation on `Dialog` for more information and specifics. Note that in this above code we set the controller. The reason we must set the controller and must not specify it in the fxml file is because if we do that then the FXML file will create an _instance of the FormDialog that is separate from this one_. Then the result converter will not match up with the correct object, and in fact we would observe a NPE since the injected fields would never be set in the FormDialog class that we call.

Now that we have implemented these subclasses, we can use them in our program as follows:
```
public someAlertMethod() {
  ExitAlert alert = new ExitAlert();
  alert.showAndWait(this::save);
}

private save(ButtonType button) {
  switch (button) {
    case ButtonType.OK:
      saveData();
      // fall through
    case ButtonType.NO:
      Platform.exit(); // JavaFX exits the Application
      break;
    default: // includes ButtonType.CANCEL
      break;
  }
}

public someMethod() {
  FormDialog dialog = new FormDialog();
  dialog.showAndWait().ifPresent(this::addForm);
}

private addForm(Form form) {
  // form will never be null
  forms.add(form); // or whatever etc
}
```
The code `dialog.showAndWait()` shows the dialog and does not progress the Application until we have returned from the dialog. When the user triggers the dialog to close our result converter creates the form and then returns it. If the form is null, then ifPresent does not run - if it is non null, then it executes the code in addForm via the method lambda. Another popular form is replacing `ifPresent()` with `ifPresentOrElse()`. If you are not a fan there are alternatives to Java's Optional API although I highly recommend it.

## JavaFX Gotchas
- JavaFX is not thread safe - see concurrent reference for how to use concurrency.
- All graphical methods (`show()`, `showAndWait()`, etc.) must be done on the _JavaFX Application thread_. If a different thread is being used, then `Platform.runLater(Runnable)` can be used.

## JavaFX Platform Util
- `Platform.exit()` can be used to exit the JavaFX Application at any time. When this method is called **all** `Stage`s are closed and the JavaFX Application Thread terminates. Execution returns to where the Application was launched from. JavaFX `Application` class has a `close()` method that can be overridden for normal Application termination procedures. 
- `Platform.runLater(Runnable)` can be used to run graphical methods on the JavaFX Application thread.

# Resources
## JavaFX Resources
- [Master Video](https://www.youtube.com/watch?v=t4ehYIynI34) - Everything you would need to know in 5 hours, not necessary but provided
- [Building JavaFX with Maven](https://edencoding.com/javafx-maven/)

### CSS
- [CSS Reference](https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html#typefont)
- [Skinning JavaFX Applications](https://docs.oracle.com/javafx/2/css_tutorial/jfxpub-css_tutorial.htm)

### FXML
- [Getting Started With FXML](https://docs.oracle.com/javafx/2/get_started/fxml_tutorial.htm)
- [Jenkov FXML Tutorial](http://tutorials.jenkov.com/javafx/fxml.html)
- [FXML Reference](https://docs.oracle.com/javafx/2/api/javafx/fxml/doc-files/introduction_to_fxml.html#scripting)
- [Mastering FXML](https://docs.oracle.com/javafx/2/fxml_get_started/jfxpub-fxml_get_started.htm)

### Bindings API
- [JavaFX Properties & Binding](https://edencoding.com/javafx-properties-and-binding-a-complete-guide/) - Very Important! Will add tutorial on request

### Concurrency
- [JavaFX Concurrency](https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/concurrency.htm)

### Layouts
- [Default JavaFX Containers](https://www.tutorialspoint.com/javafx/javafx_layout_panes.htm) - small list of containers

### Resources
- [Location not set (null resource)](https://edencoding.com/location-not-set/) - resource explanation


## Libraries
### Included Libraries
JavaFX Libraries I have included in the project already:
- [BootstrapFX](https://github.com/kordamp/bootstrapfx) - a default stylesheet that is better than the root
- [ControlsFX](https://github.com/controlsfx/controlsfx) - library with more controls and notifications etc. [ControlsFX Feature Website](http://fxexperience.com/controlsfx/features/#notifications)

### Potential Libraries
JavaFX Potential Libraries (Discuss)
- [RichTextFX](https://github.com/FXMisc/RichTextFX) - library that looks like it is built for creating IDE level text editors
- [ValidatorFX](https://github.com/effad/ValidatorFX) - library for requirihg input on text fields etc.
- [FormsFX](https://github.com/dlsc-software-consulting-gmbh/FormsFX) - a library for creating forms (thinking we may want this for preferences/ options
- [General Libraries](https://www.jrebel.com/blog/best-javafx-libraries) - list of JavaFX libraries we may want to check out (I pulled some for this list)


## Misc Resources
May be useful for later on, may not
- [Running Commands](https://www.journaldev.com/937/compile-run-java-program-another-java-program) - processes etc.
- [System Properties](https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html) - System constants such as directory location etc.
- [Bean Class JavaFX](https://www.omnijava.com/2017/08/29/the-bean-class-for-javafx-programming/) - Style of Property classes
- [Pattern Documentation](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#sum) - Java Regex
- [regex101](https://regex101.com/) - use the java8 tab, great for testing regex with documentation and help
- [Preferences](https://docs.oracle.com/javase/8/docs/api/java/util/prefs/Preferences.html) - Automatically store and retrieve user preferences (Database-esque)
