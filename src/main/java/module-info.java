module com.sdc.tthree.sdcide {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.fxmisc.richtext;

    opens com.sdc.three.ide to javafx.fxml;
    exports com.sdc.three.ide;
}