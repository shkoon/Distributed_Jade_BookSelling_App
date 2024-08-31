package agents.seller;

import agents.buyer.BookBuyerAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookSellerContainer extends Application {
    protected BookSellerAgent bookSellerAgent;
    protected ObservableList<String> observableListData;
    protected AgentContainer agentContainer;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("Book Seller Container");
        HBox hBox=new HBox();
        Label label=new Label("Agent name:");
        TextField textField=new TextField();
        Button deployButton=new Button("Deploy");
        hBox.getChildren().addAll(label,textField,deployButton);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10));
        BorderPane borderPane=new BorderPane();
        borderPane.setTop(hBox);
        VBox vBox=new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);
        observableListData= FXCollections.observableArrayList();
        ListView<String> listViewMessages=new ListView<>(observableListData);

        vBox.getChildren().add(listViewMessages);
        borderPane.setCenter(vBox);
        Scene scene=new Scene(borderPane,600,400);
        stage.setScene(scene);
        stage.show();

        deployButton.setOnAction((evt)->{
            try {
                String name=textField.getText();
                AgentController agentController=agentContainer.createNewAgent(name ,BookSellerAgent.class.getName(),new  Object[]{this});
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        });
    }
    private void startContainer() throws Exception{
        Runtime runtime= Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
         agentContainer=runtime.createAgentContainer(profile);
        try {

            agentContainer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableListData.add(aclMessage.getSender().getName()+"=>"+aclMessage.getContent());
        });


    }
}
