module com.sdc.tthree.sdcide {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.fxmisc.richtext;

    opens com.sdc.three.ide to javafx.fxml;
    exports com.sdc.three.ide;
}