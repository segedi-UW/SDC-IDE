# Overview

## Intro to Maven
We are using Maven to build our projects since it saves us the hassle of manually
downloading all the dependencies, and we can update dependencies very easily later
on by just changing one file - the pom.xml file. In Maven, this is the build options
file that manages our dependencies and takes care of any other tasks we give it. If
you have questions on how it works feel free to ask me, but there is not a huge point
in me creating an in depth tutorial on the ins and outs of Maven for our project currently.

## How JavaFX Works
JavaFX uses a scene graph that paints components onto the screen. The lowest form of a
visible object is the `Node` class. The `Node` class should not be extended in the vast
majority of cases. To be honest I have not done it to date, but I am fairly certain it
can be done.
JavaFX uses Properties and Bindings to separate data and code - UI components will be
automatically updated when the state of a variable changes provided we use this API. It
is necessary that we do a great job working with this if we want an easy to manage system
- I will create a tutorial on this shortly (as of 10/19/2021 no tutorial). There are plenty
of online tutorials as well.

JavaFX starts a JavaFX Application Thread after calling one of a few `launch()` methods from
or passing a subclass of `Application`. After initialization JavaFX calls that subclasses 
`@Override start(Stage)` method, providing the primary Stage for the application. The `Scene` 
can be created either in plain java (messy), or in FXML using the `FXMLLoader` class, which is
what we will be doing. JavaFX `Node`s can be styled using style sheets with special JavaFX CSS
grammar which we will also use.

## Our Application
So with how our project is currently configured, our application starts in the `App` class.
`App` extends `Application`, for now it includes `main` which launches JavaFX. Any 
JavaFX application must be launched before most of the JavaFX Objects can be used 
(visible graphics etc.). Our primitive `App` contains the following:
```java
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
In JavaFX when we launch the Application, JavaFX creates an object of our classes type.
_*This **requires** that we have a default constructor (one with no arguments) for our
`App` class._ After that JavaFX does its initialization and then calls our classes overridden
`start(Stage)` method. This passes to us an instance of the primary `Stage` for our application.
After that we load the contents of that Stage using `FXMLLoader`. The `FXMLLoader` is created
and in this case initialized with the resource that it reads from. We then use the `load()`
method to obtain the `Parent` object to provide our `Scene`.

**In summary:**
1. JavaFX creates an `App` object using `new App()`
2. JavaFX initializes its event dispatch Thread, and graphics etc.
3. JavaFX calls `@Override start(Stage stage)` in the `App` object
4. Using the passed stage we initialize the `Scene` using a previously created
fxml file and an `FXMLLoader` object (more on that below)

### FXML
Now I want to look at the FXML file. In our Application it is under our module resources named
**Controller.fxml**.
```fxml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Button?>

<VBox alignment="CENTER" spacing="20.0" xmlns:fx="http://javafx.com/fxml"   <- Root node
      fx:controller="com.sdc.three.ide.Controller">
    <padding>
        <Insets bottom="20.0" left="20.0" right="20.0" top="20.0"/> <-- This can alternatively be defined in our css file
    </padding>
    <children> <------------------------------------------------------- Default Property (discussed below)
      <Label fx:id="welcomeText" id="textExample"/>  <----------------- Label with fx:id for injection
      <Button text="Hello!" onAction="#onHelloButtonClick"/>
    </children>
</VBox>
```
As you can see FXML is XML based and follows a similar format using angle brackets for delimiters.
Note that each follows the format (besides special `<?command data>`) of `<Object [properties]/>`
or `<Object [properties]></Object>`. When using the `<></>` notation, properties can be specified
using angled brackets as shown for the padding property above. Some properties like padding need
to be specified in this way rather than the `property="data"` format in the header. _In this case
padding could instead be specified in the CSS sheet._

The first line provides basic encoding and document type. The next lines are import statements.
In FXML when we specify what FXML objects to add we need to import them. Note that these imports
include our own custom classes (more on that below). _Intellij is a blessing for imports_.

In this FXML file our root (outermost) `Node` is the `VBox`. The root `Node` has some special
properties that we can / need to specify. The first is the **xmlns:fx**. This is necessary
if we plan on utilizing _injection_ to populate our Controller class with `fx:id` named objects
(discussed later).
**Note that `fx:id` is different from `id`! `id` is used with CSS to define a particular nodes
style**. 

Additionally, the controller is specified with the `fx:controller` property (which as
you see requires the `xmlns:fx`). This is optional for use with controllers, but when we define
it in the FXML file, an unreferenced controller class is instantiated automatically by JavaFX. If we leave
this line out, we need to set the controller of the `FXMLLoader` before calling `load()`. 

#### Default Property
In FXML each class has a default property that when no property is specified is modified by the
`FXMLLoader`. This default property is very commonly used for container objects such as `VBox`
in the example above. In the example I included this default property for the sake of explanation,
but the more typical way to write the above FXML knowing that most container objects default property
is the children property is as follows:
```fxml
<VBox alignment="CENTER" spacing="20.0" xmlns:fx="http://javafx.com/fxml"   <----- Root node
      fx:controller="com.sdc.three.ide.Controller">
    <padding>
        <Insets bottom="20.0" left="20.0" right="20.0" top="20.0"/>
    </padding>
    <Label fx:id="welcomeText" id="textExample" styleClass="exampleClass"/>
    <Button text="Hello!" onAction="#onHelloButtonClick"/>
</VBox>
```
As you can see we got rid of the `<children>` and `</children>` headers since the default property
makes this redundant.

#### Subclassing / Custom Objects in code
The `FXMLLoader` is very robust and can create any JavaFX object providing it has a default constructor.
We can take advantage of this by subclassing JavaFX objects, so we can create our own custom objects.
In JavaFX subclassing is the primary way of implementing custom objects. Think perhaps a `Control` that
internally incorporates multiple other `Control` objects - such as subclassing a  `VBox` and 
then adding a `TextArea` and `Button` to it in its default constructor etc.

For example:
```java
public class Chat extends VBox {

    private TextArea chat;
    private Button sendBtn;
    private TextField input;
    
    public Chat() {
        super();
        area = new TextArea();
        input = new TextField();
        sendBtn = new Button("Send");
        HBox box = new HBox(input, sendBtn);
        getChildren().addAll(area, box);
    }
    
    /* other methods dealing with fields */
}
```
So now we can use `Chat` in an FXML file as an object (after importing it), and this is
what would be created. Our `Chat` object would share all properties of a
`VBox`, but we would not need to add the fields to it in the fxml file since that is
handled in the constructor.

Another way of achieving this that is nice for elaborate subclasses 
is given after the following introduction to Controllers.

### Controller
JavaFX's objects have all sorts of properties which I will create a tutorial for later.
These properties can be initialized using FXML as seen in the example code above, of which I want
to cover two things. To start, lets look at our last beginning class, the `Controller` class.
```java
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
1. Injection
2. Function calls
3. Instantiation Path

##### Injection
For injection, we provide the `fx:id` property with a **unique** name prefaced with the `@FXML` annotation.
Note that the object needs to be named exactly the same in the controller class, again as seen above.
You will notice that the nice thing about injection is that fields and methods can be hidden from classes if we want! 
This is very nice for abstraction, but it is not required. Public or protected methods are allowed too.

#### Function calls
For function calls, as seen in the `onAction` property being set in our FXML file, we reference
our controller's methods using the format `"#methodName"`. Our function calls can either take no
arguments, or take a JavaFX `ActionEvent` parameter that is filled by the `FXMLLoader`
automatically when the method is called. Examples of the `ActionEvent` parameter are provided later on.

##### Instantiation Path
When our controller class is called by `FXMLLoader`, the first thing that happens is the default constructor
is called to create the object. At this point all of our fields such as `Label welcomeText` will
be null! Luckily there is a no argument method that `FXMLLoader` checks for called `initialize()`
that it automatically calls if it exists after the injection process. As you can see it also is
annotated with `FXML`. Having this method is not a requirement of a controller class, but in most
cases will be necessary. At this point we can work with the injected fields which we likely will
want to do some setup with.

#### Subclassing with FXML

Why subclass? One reason is we don't need a method that returns a Node that can be displayed - although
doing it in this manner is trivial, it adds a lot of next to useless code to achieve the same
effect: for example having an interface `Displayable` with `Node getDisplay()` as the method, then
implementing that interface for all visible objects. Everytime you want to add that object to the
scene graph you need to call `object.getDisplay()` instead of just passing the object as the
parameter.

A second reason is that we can call our subclasses within FXML files as previously mentioned,
importing them like other JavaFX objects. This allows us to separate a FXML file into sub
FXML files for specific sections if the file gets very large etc.

Here is an example of how this would be done using FXML:

chat.fxml
```fxml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.TextField?>
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.TextArea?>
<?import javafx.scene.layout.HBox?>

<VBox xmlns:fx="http://javafx.com/fxml" fx:controller="com.sdc.three.ide.Chat">
    <TextArea fx:id="chat" wrapText="true" editable="false"/>
    <HBox id="sendBar">
        <TextField fx:id="input" promptText="Type a message" HBox.hgrow="SOMETIMES"/>
        <Button onAction="#sendChat" text="Send" id="sendBtn" HBox.hgrow="ALWAYS" maxHeight="Infinity"/>
    </HBox>
    <Button text="Home" onAction="#switchToHome"/>
</VBox>
```

Chat.java
```java
/*
 * A StackPane is simply a JavaFX container that lays children in
 * the center and overlaid. In this case there is only the one
 * child which is a container itself - the VBox defined in chat.fxml
 */
public class Chat extends StackPane {  // <----- see above
    @FXML private TextArea chat;
    @FXML private TextField input;

    public Chat() {
        super();
        try {
            FXMLLoader loader = App.toLoader("chat.fxml");
            loader.setControllerFactory(callback -> this); // IMPORTANT LINE
            getChildren().add(loader.load());
        } catch (IOException e) {
            throw new IllegalStateException("Could not load chat.fxml: " + e.getMessage());
        }
    }
    
    @FXML
    private void sendChat() {
        /* send the message */
        chat.setText(String.format("%s\n> %s", chat.getText(), input.getText()));
        input.setText("");
    }

    @FXML
    public void switchToHome() throws IOException {
        FXMLLoader loader = App.toLoader("controller.fxml");
        Scene home = new Scene(loader.load(), 320, 240);
        App.addDefaultStylesheets(home);
        App.setScene(home);
    }
}
```
If we did not specify the controller factory in this object, the
`FXMLLoader` would create a _new instance of Chat_ rather than using
the created one. Our calls to our object would be useless. This is 
described in more depth later on as well.

A Scene including this object would be created like so:
```java
public class SomeClass {
    
    @FXML
    public void switchToChat(ActionEvent actionEvent) throws IOException {
        // ActionEvent is not used, simply included as an example
        actionEvent.consume(); // not necessary and sometimes not wanted - only for example
        // consume stops this actionEvent from being passed on to other objects, if for example we wanted to stop
        // a dialog from closing unless a certain field has valid input we could use a similar construct to this.
        // if you want to know how to do that simply ask me.
        Scene chat = new Scene(new Chat(), 320, 240); // this instance can be kept for use as well
        App.addDefaultStylesheets(chat);
        App.setScene(chat);
    }

}
```

Keep in mind that what the `FXMLLoader` returns with `load()` is the root `Node` in the
fxml file. So if we returned a `VBox` like we did here, we don't have to add it to a Scene. We could
also just add it as a child to one or any other use case for a `VBox`.

### CSS
We have talked about the basics of everything except JavaFX CSS. Our code from earlier is only going to get a bit more
involved. I have made a few modifications to the `App` class as seen here:
```java
public class App extends Application {
    public static void setScene(Scene scene) {
        if (mainStage == null) throw new NullPointerException("Attempted to set scene of mainStage before initialization");
        mainStage.setScene(scene);
    }

    /**
     * Returns a FXMLLoader of a given system resource
     * @param resource the resource name
     * @return a FXMLLoader with a non-null FXML sheet
     * @throws NullPointerException if resource does not exist
     */
    public static FXMLLoader fxmlLoader(String resource) {
        return new FXMLLoader(toResourceURL(resource));
    }

    /**
     * Adds the stylesheet.css file and the BootstrapFX stylesheet to the Scene. This
     * should be called on all new Stages / Scenes in order to maintain a consistent look
     * for the Application.
     * @param scene the scene to add the stylesheets to.
     * @throws NullPointerException if stylesheet.css is not on the resource path
     */
    public static void addDefaultStylesheets(Scene scene) {
        String custom = toResourceURL("stylesheet.css").toExternalForm();
        scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet(), custom);
        // our custom sheet has the highest precedence when added last
    }

    /**
     * Returns a non-null URL of an Application resource.
     * @param resource the resource name
     * @return non-null URL
     * @throws NullPointerException if resource does not exist
     */
    public static URL toResourceURL(String resource) {
        URL url = App.class.getResource(resource);
        if (url == null) throw new NullPointerException(String.format("Resource \"%s\" was not found", resource));
        return url;
    }
}
```
All I have done is added some checks for null resources - these should be fatal errors here since if
we cannot load the UI there is not much for the user to do. We should never have a case where a
NPE is acceptable. _Catching NPE is considered bad code practice_.

I also added a static method for getting the FXMLLoader (convenient), and a method for adding the
stylesheet. Each `Node` that has a newly created `Scene` needs to have this stylesheet added,
which includes JavaFX objects like the `Alert` class and `Dialog` class, which is why this method
is useful. The general strategy for designing these types of classes will be to create a subclass
that extends one of these and then sets up all the methods inside (see Alert and Dialog section),
loading the content with the `FXMLLoader`. Of course if it is a very basic class then the
`FXMLLoader` is not necessary. I digress.
The only actual new thing here is the `addDefaultStylesheets(Scene)` method. Everything has just
been rewritten as functions.

Take a look at one line from our FXML file...

`<Label fx:id="welcomeText" id="textExample" styleClass="exampleClass"/>`

As you can see this Label has the `fx:id`, `id`, and `styleClass` properties set. In JavaFX `Node`
objects `id` and `styleClass` property corresponds to the CSS styles that a particular object has.
Note that each `Node` has a `setStyle(String)` method that allows inline styles directly in code.
Additionally, this can be done in FXML with the `style` property. However, it is best-practice to
separate CSS from our code base which is why we are using this external stylesheet approach.
JavaFX CSS cascades from the stylesheets added to a `Node`'s stylesheet `ObservableList<String>`
accessible via the `getStyleClass()` method. Every `Node` starts off with the true default
stylesheet ("caspian.css"). Stylesheets added to this list are overridden by sheets later in the
list, so if we want to override caspian.css styles for a given class we just need to add the
stylesheet (since this naturally appends the `String` to the end of the list).
The priorities for a `Node`'s CSS is StyleClass < ID < inline style.
In JavaFX CSS there are two selector types:
1. Class
2. ID

The id selector is a '#' followed by the id name. The class selector is a '.' followed by the
class name optionally followed by subclasses for specification. For example some JavaFX controls have text
elements inside them that are not directly accessible by styling the `Control` itself in the
stylesheet. These text elements can usually be styled by using the following syntax: `.controlName .labeled {...}`.
All selectors are followed by brackets with CSS like normal.

See the JavaFX CSS reference for more details, and I recommend checking out
[Skinning JavaFX Applications with CSS](https://docs.oracle.com/javafx/2/css_tutorial/jfxpub-css_tutorial.htm)
for a great introduction. This tutorial is linked in the CSS section at the end as well.

Here is an example CSS sheet I have filled a bit in for demonstration:
```css
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
The **root** class is an example of a class override, since the stylesheet that JavaFX uses as
default has a root style class that we change here. It is the base skin for our JavaFX Application
that we can style to have a uniform style among all of our components. The `#textExample` is an example of an id selector, and the `.exampleClass` is an example of a class that we defined on
our own.

Note that the
`-fx-base` property handles the base color of **all** `Control` objects when specified in
the root like this.

Just like in normal CSS, a `Node` can have many style classes, but only one id. Unlike normal
CSS, ids are not required to be unique.

Also, not every Node type supports all JavaFX CSS properties - see the official JavaFX CSS Reference
for specifics.
- If you are interested in state CSS (hovering, selected, etc.) check out pseudo classes in
  JavaFX CSS in the reference.
- If you want to know more about subclassing and specifics such as `.overiddenClass .subclass
  {...}` you should see the reference and previously linked tutorial as well.

### Brief Intro to Method Lambdas
In a moment you will see some code that utilizes Java method lambdas! They are pretty cool, but
I don't expect you to know what they are, so I am prefacing them here.

A standard lambda is a shortened version of an abstract class.
In java, there are two forms of lambda - the one line and the multiline form.

```java
public class SomeClass {

private interface DoMath {
    double calculate(double n1, double n2);
}

    public void someMethod() {
        // multiline
        DoMath add = (add1, add2) -> {
            return add1 + add2;
        };

        // one line - return is expected and is not explicit here
        add = (add1, add2) -> add1 + add2;
    }
}
```

The one line version of the lambda can be expressed as this:
```java
public class SomeClass {
    
    public void someMethod() {
        // old one line
        add = (add1, add2) -> add1 + add2;

        // new one line using a method instead
        add = (add1, add2) -> add(add1, add2);
    }

    public double add(double add1, double add2) {
        return add1 + add2;
    }

}
```

This allows a usual multiline lambda to be defined in an external function! This is very cool
and allows for cleaner code, but is more verbose than it needs to be.
Since lambdas work to reduce this, there is a better lambda solution:

```java
public class SomeClass {

    public void someMethod() {
        add = this::add;
    }

    public double add(double add1, double add2) {
        return add1 + add2;
    }

}
```

The first part is the class or object that the method is a part of and the second part is the method name. These two parts are
connected with two colons in the form: `<class>::<method name>`. As you can see this can only be done with methods that 
take parameters that are the exact same as the lambda (calling method) provides. When used they reduce redundant code in the one line
referencing a method. Referencing a method in this way instead of writing the method's contents in a multiline lambda at
the location is useful for separating code for handling events from code that sets up the handlers for the
events, which we will encounter and use a lot while working on our project. 

### Alert and Dialog
The JavaFX `Alert` and `Dialog` class have their own scenes and stage, and as the name suggests
act as modal pop-ups that require the user to complete before returning. The best practice for
clean code is to create a subclass to design. For example, consider the case you want to create
a dialog that prompts the user to save their workspace before exiting the application...
```java
public class ExitAlert extends Alert {

    public ExitAlert() {
        super(AlertType.CONFIRMATION);
        // design code
        App.addDefaultStylesheets(getDialogPane().getScene()); // check method path
        setHeaderText("Exit And Save?");
        setContentText("Click \"OK\" to exit and save your workspace.");
        setTitle("Exit And Save");
    }

}
```
For dialogs, it would be something similar, but it is a bit more involved since you are
likely to return something. In that case it may look like the following:
```java
  public class FormDialog extends Dialog<Form> {

    @FXML private TextField name;
    @FXML private CheckBox isMale;

    public FormDialog() {
        super();
        // see above Alert for similar design API calls:  
        // set various text and add default stylesheets!
        setResultConverter(this::formResult);

        FXMLLoader loader = App.toLoader("formDialog.fxml");
        // set this object as controller so that resultConverter can utilize controller injected fields etc.
        loader.setControllerFactory(callback -> this);
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
Of course, I am being very brief here - I just want to expose you to this a bit to give you a
direction.

Note that in above code we manually set the controller factory. The reason we must set
the controller factory is otherwise the `FXMLLoader` will create an _instance of the
`FormDialog` that is separate from the one we use_. The result converter
will not match up with the correct object, and in fact in that case we would observe a
NPE since the injected fields would never be set in the FormDialog class that we call.

#### Other Notes on Dialog's

If you use `Dialog` or `Alert` classes make sure you add the default stylesheets!
Otherwise, our Application will have discontinuous style.

The Dialog API **requires the `ResultConverter` to be set when subclassing it and not
using ButtonType as the generic**.

Not doing this will result in a non-functional dialog.
It should nearly always be the case that when using Dialog's you do this,
otherwise using the `Alert` class is likely easier.

See the JavaFX documentation on `Dialog` for more information and specifics.

#### Implementation

Now that we have implemented these subclasses, we can use them in our program as follows:
```java
public class SomeClass {
    
public someAlertMethod() {
        ExitAlert alert = new ExitAlert();
        alert.showAndWait(this::save);
        }

private save(ButtonType button) {
        switch (button) {
        case ButtonType.OK:
        // save the data
        saveData();
        // fall through
        case ButtonType.NO:
        // exit
        Platform.exit(); // JavaFX exits the Application
        break;
default: // includes ButtonType.CANCEL
        // don't save, don't exit
        break;
        }
        }

public someMethod() {
        FormDialog dialog = new FormDialog();
        dialog.showAndWait().ifPresent(this::addForm);
        }

private addForm(Form form) {
        // form will never be null
        forms.add(form); // or whatever etc.
        }

}
```
The code `dialog.showAndWait()` shows the dialog and does not progress the Application until
we have returned from the dialog. When the user triggers the dialog to close our result
converter creates the form and then returns it. If the form is null, then ifPresent does not
run - if it is non-null, then it executes the code in addForm via the method lambda. Another
popular form is replacing `ifPresent()` with `ifPresentOrElse()`. This will allow you
to write code for the case it is null. If you are not a fan there
are alternatives to Java's Optional API, although I highly recommend you use it.

## JavaFX Gotchas
- JavaFX is not thread safe - see concurrent reference for how to use concurrency.
- All graphical methods (`show()`, `showAndWait()`, etc.) must be done on the _JavaFX
  Application thread_. If a different thread is being used, then `Platform.runLater(Runnable)`
  can be used.
- The FXML property `fx:id` is different from the `id` property - `fx:id` is used to inject
  into controllers, and `id` is used for CSS styling of unique `Node` objects.

## JavaFX Platform Util
- `Platform.exit()` can be used to exit the JavaFX Application at any time. When this method
  is called **all** `Stage`s are closed and the JavaFX Application Thread terminates. Execution
  returns to where the Application was launched from. JavaFX `Application` class has a `close()`
  method that can be overridden for normal Application termination procedures.
- `Platform.runLater(Runnable)` can be used to run graphical methods on the JavaFX Application
  thread.

# Resources
## JavaFX Resources
- [Master Video](https://www.youtube.com/watch?v=t4ehYIynI34) - Everything you would need to
  know in 5 hours, not necessary (at all) but provided
- [Building JavaFX with Maven](https://edencoding.com/javafx-maven/)
- [JavaFX Objects w/ Tutorials](http://tutorials.jenkov.com/javafx/index.html)
- [JavaFX Documentation](https://openjfx.io/javadoc/11/)

### CSS
- [CSS Reference](https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html#typefont)
- [Skinning JavaFX Applications](https://docs.oracle.com/javafx/2/css_tutorial/jfxpub-css_tutorial.htm)

### FXML
- [Getting Started With FXML](https://docs.oracle.com/javafx/2/get_started/fxml_tutorial.htm)
- [Jenkov FXML Tutorial](http://tutorials.jenkov.com/javafx/fxml.html)
- [FXML Reference](https://docs.oracle.com/javafx/2/api/javafx/fxml/doc-files/introduction_to_fxml.html#scripting)
- [Mastering FXML](https://docs.oracle.com/javafx/2/fxml_get_started/jfxpub-fxml_get_started.htm)

### Bindings API
- [JavaFX Properties & Binding](https://edencoding.com/javafx-properties-and-binding-a-complete-guide/)
  Very Important! Will add tutorial on request

### Concurrency
- [JavaFX Concurrency](https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/concurrency.htm)

### Layouts
- [Default JavaFX Containers](https://www.tutorialspoint.com/javafx/javafx_layout_panes.htm) -
  small list of basic containers

### Resources
- [Location not set (null resource)](https://edencoding.com/location-not-set/) -
  resource explanation. Should not be a problem with how I configured our Application.


## Libraries
### Included Libraries
JavaFX Libraries I have included in the project already:
- [BootstrapFX](https://github.com/kordamp/bootstrapfx) -
  a default stylesheet that is better than the root
- [ControlsFX](https://github.com/controlsfx/controlsfx) -
  library with more controls and notifications etc.
  [ControlsFX Feature Website](http://fxexperience.com/controlsfx/features/#notifications)
- [RichTextFX](https://github.com/FXMisc/RichTextFX) -
  library that looks like it is built for creating IDE level text editors
  [(RichTextFX wiki)](https://github.com/FXMisc/RichTextFX/wiki)
- [TestFX](https://github.com/TestFX/TestFX) -
  documentation is not great so here are some tutorials:
  [google](https://www.google.com/search?q=using+TestFX+JavaFX&newwindow=1&client=firefox-b-1-d&channel=nus5&sxsrf=AOaemvLiPkFvphfGwoafDPcFph65yKmzEg%3A1634245450996&ei=SptoYZuRPNGFtQbtuLagCQ&ved=0ahUKEwib3tPc5srzAhXRQs0KHW2cDZQQ4dUDCA0&uact=5&oq=using+TestFX+JavaFX&gs_lcp=Cgdnd3Mtd2l6EAM6BwgAEEcQsANKBAhBGABQ2owsWPaPLGDOkyxoAXACeACAAXWIAb4BkgEDMS4xmAEAoAEByAEIwAEB&sclient=gws-wiz), [TDD with TestFX](https://medium.com/information-and-technology/test-driven-development-in-javafx-with-testfx-66a84cd561e0), [YouTube Video Tutorial](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwjziZK26crzAhXKQs0KHbqeAPsQFnoECAYQAw&url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DNG03nNpSmgU&usg=AOvVaw0RdZS4D0AFJN-fwDEO3thx)
### Potential Libraries
JavaFX Potential Libraries (Discuss)
- [ValidatorFX](https://github.com/effad/ValidatorFX) -
  library for requiring input on text fields etc.
- [FormsFX](https://github.com/dlsc-software-consulting-gmbh/FormsFX) -
  a library for creating forms (thinking we may want this for preferences/ options
- [General Libraries](https://www.jrebel.com/blog/best-javafx-libraries) -
  list of JavaFX libraries we may want to check out (I pulled some for this list)

## Git Resources
- [Git Mastery Tutorial](https://www.freecodecamp.org/news/git-for-professionals/)
- [Git Branches Tips](https://uoftcoders.github.io/studyGroup/lessons/git/branches/lesson/)

## Misc Resources
May be useful for later on, may not
- [Running Commands](https://www.journaldev.com/937/compile-run-java-program-another-java-program) -
  processes etc.
- [System Properties](https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html) -
  System constants such as directory location etc.
- [Bean Class JavaFX](https://www.omnijava.com/2017/08/29/the-bean-class-for-javafx-programming/) -
  Style of Property classes
- [Pattern Documentation](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#sum) - Java Regex
- [regex101](https://regex101.com/) - use the java8 tab, great for testing regex with
  documentation and help
- [Preferences](https://docs.oracle.com/javase/8/docs/api/java/util/prefs/Preferences.html) -
  Automatically store and retrieve user preferences (Builtin Database)
