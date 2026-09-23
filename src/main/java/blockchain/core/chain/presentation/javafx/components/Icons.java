package blockchain.core.chain.presentation.javafx.components;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public final class Icons {

    private Icons() {
    }

    public static Icon of(String name) {
        return new Icon(name);
    }

    public static Icon large(String name) {
        Icon icon = new Icon(name);
        icon.getStyleClass().add("icon-lg");
        return icon;
    }
}
