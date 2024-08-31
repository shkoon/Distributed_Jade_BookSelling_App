package agents.buyer;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class BookBuyerAgent extends GuiAgent {

    protected BookBuyerContainer bookBuyerContainer;
    protected AID[] vendeurs;
    @Override
    protected void setup() {

        if(this.getArguments().length==1) {
            bookBuyerContainer=(BookBuyerContainer) this.getArguments()[0];
            bookBuyerContainer.bookBuyerAgent=this;
        }

        ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,5000) {
            @Override
            protected void onTick() {

                DFAgentDescription template=new DFAgentDescription();
                ServiceDescription serviceDescription=new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-livres");
                template.addServices(serviceDescription);

                try {
                    DFAgentDescription[]  results=DFService.search(myAgent,template);
                    vendeurs=new AID[results.length];

                    for (int i = 0; i < vendeurs.length; i++) {
                        vendeurs[i]=results[i].getName();
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }






            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private int counter=0;
            private List<ACLMessage> replies=new ArrayList<>();
            @Override
            public void action() {
                MessageTemplate messageTemplate=MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
                                        )));
                ACLMessage aclMessage=receive(messageTemplate);
                if(aclMessage!=null){

                    switch (aclMessage.getPerformative()){

                        case ACLMessage.REQUEST->{
                            ACLMessage aclMessage1=new ACLMessage(ACLMessage.CFP);
                            for (AID aid:vendeurs){
                                aclMessage1.addReceiver(aid);
                                aclMessage1.setContent(aclMessage.getContent());
                            }
                            send(aclMessage1);
                        }
                        case ACLMessage.PROPOSE->{

                            ++counter;
                            replies.add(aclMessage);
                            if(counter==vendeurs.length){
                                ACLMessage meilleurOffre=replies.get(0);
                                int min=Integer.parseInt(replies.get(0).getContent());

                                for (ACLMessage offre:replies){
                                    int price=Integer.parseInt(offre.getContent());
                                    if(price<min){
                                        min=price;
                                        meilleurOffre=offre;

                                    }

                                }
                                ACLMessage accept=meilleurOffre.createReply();
                                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                send(accept);
                            }

                        }
                        case ACLMessage.AGREE->{

                            ACLMessage reply=new ACLMessage(ACLMessage.CONFIRM);
                            reply.addReceiver(new AID("consumer",AID.ISLOCALNAME));
                            reply.setContent(aclMessage.getContent());
                            send(reply);

                        }
                        case ACLMessage.REFUSE->{

                        }
                    }

                    String livre=aclMessage.getContent();
                    bookBuyerContainer.logMessage(aclMessage);
                    ACLMessage reply=aclMessage.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Trying to buy=>"+aclMessage.getContent());
                    send(reply);
                    ACLMessage aclMessage1=new ACLMessage(ACLMessage.CFP);
                    aclMessage1.setContent(livre);
                    aclMessage1.addReceiver(new AID("BookSellerAgent",AID.ISLOCALNAME));
                    send(aclMessage1);
                }
                else {
                    block();
                }
            }
        });


    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
