package blockchain.core.chain.presentation.javafx.components;

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
