package com.teama.controllers_refactor2;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * Created by aliss on 12/11/2017.
 */
public class TabGenerator {
    private String tabTitle;

    public TabGenerator(String tabName) {
        this.tabTitle = tabName;
    }

    /*
    <Tab fx:id="nodeTab" text="Untitled Tab">
                           <content>
                              <AnchorPane maxWidth="-Infinity" minHeight="0.0" minWidth="0.0" prefHeight="245.0" prefWidth="481.0">
                                 <children>
                                    <JFXButton fx:id="editNode" layoutX="273.0" onAction="#onEditNode" text="Edit" translateY="5.0">
                                       <font>
                                          <Font size="15.0" />
                                       </font>
                                    </JFXButton>
                                    <GridPane fx:id="nodeDetails" maxHeight="-Infinity" maxWidth="-Infinity" prefHeight="205.0" prefWidth="280.0" translateX="30.0" translateY="25.0">
                                       <columnConstraints>
                                          <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
                                       </columnConstraints>
                                       <rowConstraints>
                                          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                                          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                                          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                                          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                                          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                                       </rowConstraints>
                                       <children>
                                          <JFXTextField fx:id="nodeID" maxHeight="-Infinity" maxWidth="-Infinity" prefHeight="24.0" prefWidth="287.0" promptText="ID" GridPane.halignment="CENTER" GridPane.valignment="CENTER">
                                             <GridPane.margin>
                                                <Insets left="10.0" right="10.0" />
                                             </GridPane.margin>
                                          </JFXTextField>
                                          <JFXTextField fx:id="nodeCoord" maxHeight="-Infinity" maxWidth="-Infinity" prefHeight="24.0" prefWidth="122.0" promptText="Coordinate" GridPane.rowIndex="1">
                                             <GridPane.margin>
                                                <Insets />
                                             </GridPane.margin>
                                             <padding>
                                                <Insets left="10.0" />
                                             </padding>
                                          </JFXTextField>
                                          <JFXTextField fx:id="longName" promptText="Long Name" GridPane.rowIndex="2">
                                             <padding>
                                                <Insets left="10.0" right="10.0" />
                                             </padding>
                                          </JFXTextField>
                                          <JFXTextField fx:id="shortName" promptText="Short Name" GridPane.halignment="CENTER" GridPane.rowIndex="3">
                                             <GridPane.margin>
                                                <Insets />
                                             </GridPane.margin>
                                             <padding>
                                                <Insets left="10.0" right="10.0" />
                                             </padding>
                                          </JFXTextField>
                                          <JFXComboBox fx:id="floorSelect" prefHeight="22.0" prefWidth="113.0" promptText="Floor" GridPane.halignment="RIGHT" GridPane.rowIndex="1">
                                             <padding>
                                                <Insets right="10.0" />
                                             </padding>
                                          </JFXComboBox>
                                          <JFXComboBox fx:id="nodeType" prefHeight="21.0" prefWidth="166.0" promptText="Node Type" GridPane.halignment="CENTER" GridPane.rowIndex="4">
                                             <GridPane.margin>
                                                <Insets />
                                             </GridPane.margin>
                                          </JFXComboBox>
                                       </children>
                                    </GridPane>
                                    <JFXButton fx:id="clearBtn" layoutX="14.0" layoutY="5.0" onAction="#onEditNode" text="x">
                                       <font>
                                          <Font size="15.0" />
                                       </font>
                                    </JFXButton>
                                 </children>
                              </AnchorPane>
                           </content>
                        </Tab>
                     </tabs>
     */
    public Tab makeTab() {
        //TODO: MAKE CSS FOR ALL BUTTONS AND SUCH
        Tab newTab = new Tab();
        newTab.setText(this.tabTitle);
        JFXButton edit = new JFXButton();
        edit.setText("Edit");
        JFXButton clear = new JFXButton();
        GridPane nodeInfo = new GridPane();
        nodeInfo.setPrefWidth(280);
        nodeInfo.setPrefHeight(205);
        nodeInfo.add(edit, 0,0);
        AnchorPane newPane = new AnchorPane();
        newPane.getChildren().addAll(nodeInfo);
        newTab.setContent(newPane);
        return newTab;

    }
}
