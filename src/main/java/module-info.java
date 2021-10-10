module com.sdc.tthree.sdcide {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.sdc.t3.sdcide to javafx.fxml;
    exports com.sdc.t3.sdcide;
}