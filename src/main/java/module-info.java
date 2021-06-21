module MainModule {
    requires javafx.controls;
    requires com.jfoenix;
    requires org.controlsfx.controls;
    requires java.desktop;

    exports shared;
    exports bg.checkers.server.local;
    exports bg.checkers.server.room;
    exports bg.checkers.lang;
    exports bg.checkers.room;
    exports bg.checkers.game;
    exports bg.checkers;
}