package agents.buyer;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookBuyerContainer extends Application {
    protected BookBuyerAgent bookBuyerAgent;
    protected ObservableList<String> observableListData;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("Book Buyer Container");
        BorderPane borderPane=new BorderPane();
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
    }

    private void startContainer() throws Exception{
        Runtime runtime= Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
        AgentContainer agentContainer=runtime.createAgentContainer(profile);
        try {
            AgentController agentController=agentContainer.createNewAgent("BookBuyerAgent",BookBuyerAgent.class.getName(),new  Object[]{this});
            agentController.start();
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
