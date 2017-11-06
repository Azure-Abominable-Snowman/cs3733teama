package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    private TextField loanAmt;
    @FXML
    private TextField interestMonths;
    @FXML
    private TextField years;
    @FXML
    private Label output;

    @FXML
    private void calculateButtonClicked() {
        double out = new Mortgage(
                Integer.parseInt(loanAmt.getText()),
                Double.parseDouble(interestMonths.getText()),
                Integer.parseInt(years.getText())).calculateMortgage();
        output.setText(Double.toString(out));
    }
}
