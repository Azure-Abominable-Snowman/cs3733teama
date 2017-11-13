package boundaries;

public interface Controller {
    default String getFXMLFileName() { return ""; }
}
