package agents.seller;

import agents.buyer.BookBuyerContainer;
import jade.core.Service;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class BookSellerAgent extends GuiAgent {

    BookSellerContainer bookSellerContainer;
    @Override
    protected void setup() {

        if(this.getArguments().length==1) {
            bookSellerContainer=(BookSellerContainer) this.getArguments()[0];
            bookSellerContainer.bookSellerAgent=this;
        }

        ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage=receive();
                if(aclMessage!=null){
                    bookSellerContainer.logMessage(aclMessage);

                    switch (aclMessage.getPerformative()){
                        case ACLMessage.CFP -> {

                            ACLMessage reply=aclMessage.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(500+new Random().nextInt(1000)));
                            send(reply);
                        }
                        case ACLMessage.ACCEPT_PROPOSAL -> {

                            ACLMessage reply=aclMessage.createReply();
                            reply.setPerformative(ACLMessage.AGREE);
                            send(reply);
                        }

                    }
                }
                else {
                    block();
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription agentDescription=new DFAgentDescription();
                agentDescription.setName(getAID());
                ServiceDescription serviceDescription=new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-livres");
                agentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent,agentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
