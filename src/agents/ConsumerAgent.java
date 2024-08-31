package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

public class ConsumerAgent extends GuiAgent {

    protected ConsumerContainer consumerContainer;
    @Override
    protected void setup() {

        if(this.getArguments().length==1){
            consumerContainer=(ConsumerContainer) this.getArguments()[0];
            consumerContainer.consumerAgent=this;
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage=receive();

                if(aclMessage!=null){
                    switch (aclMessage.getPerformative()) {

                        case ACLMessage.CONFIRM -> {
                          consumerContainer.logMessage(aclMessage);
                        }
                    }
                }else {
                    block();
                }
            }
        });

    }


    @Override
    protected void onGuiEvent(GuiEvent evt) {
        if(evt.getType()==1){
            String bookName=(String) evt.getParameter(0);
            ACLMessage aclMessage=new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(bookName);
            aclMessage.addReceiver(new AID("BookBuyerAgent",AID.ISLOCALNAME));

            send(aclMessage);


        }
    }
}
