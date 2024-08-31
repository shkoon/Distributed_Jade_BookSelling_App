package agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
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



public class ConsumerContainer extends Application {
    protected ObservableList<String> observableListData;
    protected ConsumerAgent consumerAgent;
    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("Consumer Container");
        BorderPane borderPane=new BorderPane();
        HBox hBox=new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        javafx.scene.control.Label label=new javafx.scene.control.Label("Book Name");
        javafx.scene.control.TextField textFieldBookName=new javafx.scene.control.TextField();

        javafx.scene.control.Button buttonOk=new javafx.scene.control.Button("Ok");
        hBox.getChildren().addAll(label,textFieldBookName,buttonOk);
        borderPane.setTop(hBox);
         observableListData= FXCollections.observableArrayList();
        ListView<String> listViewMessages=new ListView<>(observableListData);
        VBox vBox=new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);
        vBox.getChildren().addAll(listViewMessages);
        borderPane.setCenter(vBox );
        buttonOk.setOnAction(evt->{
            String bookName=textFieldBookName.getText();
            GuiEvent event=new GuiEvent(this,1);
            event.addParameter(bookName);
            consumerAgent.onGuiEvent(event );
        });
        Scene scene=new Scene(borderPane,600,400);
        stage.setScene(scene);
        stage.show();
    }

    private void startContainer() throws Exception{
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
        AgentContainer container=runtime.createAgentContainer(profile);

        try {
            AgentController agentController=container.createNewAgent("consumer","agents.ConsumerAgent",new  Object[]{this});
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
