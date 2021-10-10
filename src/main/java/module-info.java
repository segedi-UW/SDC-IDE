module com.sdc.tthree.sdcide {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.sdc.tthree.sdcide to javafx.fxml;
    exports com.sdc.tthree.sdcide;
}